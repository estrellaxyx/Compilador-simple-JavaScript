import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.JFileChooser;
import es.upm.aedlib.Pair;


public class AnalizadorSintactico {
	private FileReader fr;
	private BufferedReader br;
	private String ubicTokens = "FicheroTokens.txt";
	private String ubicTablas = "FicheroTablas.txt";
	private String ubicParse = "FicheroParse.txt";
	private String ubicErrores = "FicheroErrores.txt";
	private String ubic;
	private MT_AFD matriz;
	private ControlTS CTS;
	private TablaSimbolos TSG;
	private TablaSimbolos TSF;
	private int desp;
	private int despG;
	private int nParam = 0;
	private boolean usaFun;
	private boolean enFun;
	private boolean enWhile;
	private AnalizadorLexico aLex;
	private Token sig_tok;
	private String parse = "Descendente";
	private GestorErrores GE;

	
	public AnalizadorSintactico(File archivo) {
/*	public AnalizadorSintactico(String ubicacion) {
		File archivo = new File(ubicacion);*/
		try {
			fr = new FileReader(archivo);
		} catch (FileNotFoundException e) {
		}
		br = new BufferedReader(fr);
		
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Seleccione una carpeta para guardar los archivos que se creen");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);

		int flag = chooser.showOpenDialog(null);
		if (flag == JFileChooser.APPROVE_OPTION) {
			ubic = chooser.getSelectedFile().getAbsolutePath();
		} else {
		}
		ubicTokens = ubic+"/"+ubicTokens;
		ubicTablas = ubic+"/"+ubicTablas;
		ubicParse = ubic+"/"+ubicParse;
		ubicErrores = ubic+"/"+ubicErrores;
		GE = new GestorErrores(ubicErrores);
	}
	
	public void aSemantico() throws Exception {
		matriz = new MT_AFD();
		CTS = new ControlTS();
		aLex = new AnalizadorLexico(br, matriz, CTS, GE);
		
		// Creo los ficheros de tokens y tablas
		crearFichero(ubicTablas);
		crearFichero(ubicTokens);
		crearFichero(ubicParse);
		crearFichero(ubicErrores);
		
		// Pido el primer token
		sig_tok = getToken();

		// Hago lo que seria P'
		TSG = CTS.setTSG(new TablaSimbolos(true));
		CTS.setTSActual(TSG);
		desp = 0;
		
		// Llamo a la funcion del axioma
		p();

		CTS.eliminarTablas();
		escribirFichero(ubicTablas, CTS.toString());
		
		escribirFichero(ubicParse, parse);

		
		if (sig_tok != null)
			throw new Exception("Hay un error sint�ctico");

	}

	public Token getToken() throws Exception {
		Token token = null;
		try {
			token = aLex.Alex();
		} catch (Exception e) {
			acabarConError();
			throw new Exception(e.getMessage()); 
		}
		if (token != null && !token.getTipo().equals("id")){
			escribirFichero(ubicTokens, token.toString());
		}
		
		return token;
	}


	private void p() throws Exception {
		if(sig_tok != null) {
			// Si es function (el FIRST (F)) aplico la regla 2
			if(sig_tok.getTipo().equals("palabraReservada") && (int) sig_tok.getValor() == 2) {
				parse += "  " + 2;
				
				f();
				p();
			}
			// Puede ser if, let, while o FIRST (S) = {id, alert, input, return, break} para FIRST (B)
			else if (sig_tok.getTipo().equals("id") || sig_tok.getTipo().equals("palabraReservada") && 
					((int) sig_tok.getValor() == 1 || (int) sig_tok.getValor() == 3 || ( (int) sig_tok.getValor() >= 8 && (int) sig_tok.getValor() <=12) )) {
				parse += "  " + 1;
				
				b();
				p();
			}
			else {
				acabarConError();
				GE.lanzarExcepSint(aLex.getNLinea(), matriz.toStringToken(sig_tok));
			}
		}
		// El FOLLOW (P) = {$}, luego cuando el token es null no se hace nada, regla 3
		else {
			parse += "  " + 3;
		}
	}

	private Pair<String, String> b() throws Exception {
		String tipo = null, tipoRet = null;
		
		if(sig_tok != null) {
			// Si el token es if aplicamos la regla 4
			if(sig_tok.getTipo().equals("palabraReservada") && (int) sig_tok.getValor() == 11) {
				parse += "  " + 4;
				
				// Equiparo el token (ya se que es un if) y pido otro token
				sig_tok = getToken();
				
				// Equiparo el token (con un '(')
				equiparar("(");
				
				String tE =e();
				
				if(!tE.equals("logico")) {
					acabarConError();
					GE.lanzarExcepSem(3, tE, aLex.getNLinea());
				}
				
				// Equiparo el token (con un ')')
				equiparar(")");
				
				Pair<String, String> tS = s();
				
				tipo = tE.equals("logico")? tS.getLeft(): "tipo_error";
				tipoRet = tS.getRight();
			}
			// Si el token es let aplicamos la regla 5
			else if(sig_tok.getTipo().equals("palabraReservada") && (int) sig_tok.getValor() == 1) {
				parse += "  " + 5;
				
				// Equiparo el token (ya se que es un let) y pido otro token
				sig_tok = getToken();
				
				Pair<String, Integer> tT = t();
				
				
				if(enFun) {
					CTS.comprobarIdEnFun(sig_tok);
				}
				int pos = sig_tok.getPos();
				// Equiparo el token (con un 'id')
				equiparar("id");
				
				String tZ = z();
				
				
				CTS.addEntrada(pos, tT.getLeft(), desp);
				desp += tT.getRight();
				tipo = (tZ.equals("vacio") || tZ.equals(tT.getLeft()))? "tipo_ok": "tipo_error";
				tipoRet = "vacio";
				
				if(tipo.equals("tipo_error")) {
					acabarConError();
					GE.lanzarExcepSem(5, tT.getLeft(), tZ, aLex.getNLinea());
				}

				// Equiparo el token (con un ';')
				equiparar(";");
			}
			// Si el token es FIRST (S) aplicamos la regla 6
			else if(sig_tok.getTipo().equals("id") || sig_tok.getTipo().equals("palabraReservada") && 
					(((int) sig_tok.getValor() >= 8 && (int) sig_tok.getValor() <=10) || (int) sig_tok.getValor() ==12)) {
				parse += "  " + 6;
				
				Pair<String, String> tS = s();
				
				tipo = tS.getLeft();
				tipoRet = tS.getRight();
			}
			// Si el token es while aplicamos la regla 7
			else if(sig_tok.getTipo().equals("palabraReservada") && (int) sig_tok.getValor() == 3) {
				parse += "  " + 7;
				
				// Equiparo el token (ya se que es un while) y pido otro token
				sig_tok = getToken();
				
				// Equiparo el token (con un '(')
				equiparar("(");
				
				String tE = e();
				
				enWhile = true;
				if(!tE.equals("logico")) {
					acabarConError();
					GE.lanzarExcepSem(8, tE, aLex.getNLinea());
				}
				
				// Equiparo el token (con un ')')
				equiparar(")");
				
				// Equiparo el token (con un '{')
				equiparar("{");
				
				Pair<String, String> tC = c();
				
				tipo = tE.equals("logico")? tC.getLeft() : "tipo_error";
				tipoRet = tC.getRight();
				enWhile = false;
				
				// Equiparo el token (con un '}')
				equiparar("}");
			}	
			else {
				acabarConError();
				GE.lanzarExcepSint(aLex.getNLinea(), matriz.toStringToken(sig_tok));
			}
		}
		// Si el token es null hay un error
		else {
			acabarConError();
			GE.lanzarExcepSint(aLex.getNLinea());
		}
		
		return new Pair<String, String>(tipo, tipoRet);
	}
	
	private Pair<String, Integer> t() throws Exception {		
		String tipo = null;
		int ancho = 0;
		
		if(sig_tok != null) {			
			// Si el token es 'number' aplico la regla 8
			if(sig_tok.getTipo().equals("palabraReservada") && (int) sig_tok.getValor() == 4) {
				parse += "  " + 8;
				
				// Equiparo el token (ya se que es un number) y pido otro token
				sig_tok = getToken();
				
				tipo = "entero";
				ancho = 1; 
			}
			// Si el token es 'boolean' aplico la regla 9
			else if(sig_tok.getTipo().equals("palabraReservada") && (int) sig_tok.getValor() == 6) {
				parse += "  " + 9;
				
				// Equiparo el token (ya se que es un boolean) y pido otro token
				sig_tok = getToken();
				
				tipo = "logico";
				ancho = 1; 
			}
			// Si el token es 'string' aplico la regla 10
			else if(sig_tok.getTipo().equals("palabraReservada") && (int) sig_tok.getValor() == 5) {
				parse += "  " + 10;
				
				// Equiparo el token (ya se que es un string) y pido otro token
				sig_tok = getToken();
				
				tipo = "cadena";
				ancho = 64; 
			}
			else {
				acabarConError();
				GE.lanzarExcepSint(aLex.getNLinea(), matriz.toStringToken(sig_tok));
			}
		}
		// Si el token es null hay un error
		else {
			acabarConError();
			GE.lanzarExcepSint(aLex.getNLinea());
		}
		
		return new Pair<String, Integer>(tipo, ancho);
	}
	
	private String z() throws Exception{
		String tipo = null;
		if(sig_tok != null) {
			// Si el token es '=' aplico la regla 11
			if(sig_tok.getTipo().equals("igual")) {
				parse += "  " + 11;
				
				// Equiparo el token (ya se que es un =) y pido otro token
				sig_tok = getToken();
				
				tipo = e();
			}
			// Si el token es '+=' aplico la regla 12
			else if(sig_tok.getTipo().equals("asignacionConSuma")) {
				parse += "  " + 12;
				
				// Equiparo el token (ya se que es un +=) y pido otro token
				sig_tok = getToken();
				
				String t = e();
				tipo = t.equals("logico")? "tipo_error" : t;
				if(t.equals("logico")) {
					acabarConError();
					GE.lanzarExcepSem(7, t, aLex.getNLinea());
				}
			}
			// Si el token es '&=' aplico la regla 13
			else if(sig_tok.getTipo().equals("asignacionConYLogico")) {
				parse += "  " + 13;
				
				// Equiparo el token (ya se que es un &=) y pido otro token
				sig_tok = getToken();
				
				String t = e();
				tipo = t.equals("logico")? t : "tipo_error";
				if(!t.equals("logico")) {
					acabarConError();
					GE.lanzarExcepSem(6, t, aLex.getNLinea());
				}
			}
			// Si el token es ; estoy aplicando lambda, regla 14
			else if(sig_tok.getTipo().equals("separador") && (int) sig_tok.getValor() == 1) {
				parse += "  " + 14;
				
				tipo = "vacio";
			}
			else {
				acabarConError();
				GE.lanzarExcepSint(aLex.getNLinea(), matriz.toStringToken(sig_tok));
			}
		}
		// Si el token es null hay un error
		else {
			acabarConError();
			GE.lanzarExcepSint(aLex.getNLinea());
		}
		return tipo;
	}

	private void f() throws Exception {
		if(sig_tok != null) {
			// Si el token es 'function' aplico la regla 15
			if(sig_tok.getTipo().equals("palabraReservada") && (int) sig_tok.getValor() == 2) {
				parse += "  " + 15;
				
				// Equiparo el token (ya se que es un function) y pido otro token
				sig_tok = getToken();
				
				String tH = h();
				
				Token tok = sig_tok;
				// Equiparo el token (con un 'id')
				equiparar("id");
				
				//ACCIONES SEM.
				nParam = 0;
				enFun = true;
				CTS.addEntrada(tok.getPos(), tH, -1);
				TSF = CTS.setTSF(new TablaSimbolos(false));
				CTS.setTSActual(TSF);
				despG = desp;
				desp = 0;
				
				// Equiparo el token (con un '(')
				equiparar("(");
				
				String tA = a();
				
				// Equiparo el token (con un ')')
				equiparar(")");
				
				//ACCION SEM.
				CTS.addEntrada(tok, tH, tA, nParam);
				
				// Equiparo el token (con un '{')
				equiparar("{");
				
				Pair<String, String> tC = c();
				
				//ACCIONES SEM.
				if (tC.getLeft().equals("tipo_error")) {
					acabarConError();
					GE.lanzarExcepSem();
				}else if ( !tC.getRight().equals(tH) && !tC.getRight().equals("vacio")) {
					acabarConError();
					GE.lanzarExcepSem(2,(String) tok.getValor(), tH, tC.getRight(), aLex.getNLinea());
				}else if ( !tC.getRight().equals(tH) && tC.getRight().equals("vacio")) {
					acabarConError();
					GE.lanzarExcepSem(3,(String) tok.getValor(), tH, null, aLex.getNLinea());
				}
				
				CTS.eliminarTabla(TSF);
				CTS.setTSActual(TSG);
				TSF = null;
				desp = despG;
				enFun = false;
				
				// Equiparo el token (con un '}')
				equiparar("}");
			}
			else {
				acabarConError();
				GE.lanzarExcepSint(aLex.getNLinea(), matriz.toStringToken(sig_tok));
			}
		}
		// Si el token es null hay un error
		else {
			acabarConError();
			GE.lanzarExcepSint(aLex.getNLinea());
		}
	}
	
	private String h() throws Exception {
		String tipo = null;
		
		if(sig_tok != null) {
			// Si es el FIRST (T) aplico la regla 16
			if(sig_tok.getTipo().equals("palabraReservada") && ((int) sig_tok.getValor() >= 4  && (int) sig_tok.getValor() <= 6)) {
				parse += "  " + 16;
				
				tipo = t().getLeft();
			}
			// Si el token es id estoy aplicando lambda, regla 17
			else if(sig_tok.getTipo().equals("id")){
				parse += "  " + 17;
				
				tipo = "vacio";
			}
			else {
				acabarConError();
				GE.lanzarExcepSint(aLex.getNLinea(), matriz.toStringToken(sig_tok));
			}
		}
		// Si el token es null hay un error
		else {
			acabarConError();
			GE.lanzarExcepSint(aLex.getNLinea());
		}
		
		return tipo;
	}

	private String a() throws Exception {
		String tipo = null;
		
		if(sig_tok != null) {
			// Si es el FIRST (T) aplico la regla 18
			if(sig_tok.getTipo().equals("palabraReservada") && ((int) sig_tok.getValor() >= 4  && (int) sig_tok.getValor() <= 6)) {
				parse += "  " + 18;
				
				Pair<String, Integer> tT = t();
				
				CTS.comprobarIdEnFun(sig_tok);
				Token tok = sig_tok;
				// Equiparo el token (con un 'id')
				equiparar("id");
				
				String tK = k();
				
				nParam += 1;
				
				CTS.addEntrada(tok.getPos(), tT.getLeft(), desp);
				desp += tT.getRight();
				tipo = tK.equals("vacio")? tT.getLeft(): tT.getLeft()+", "+tK;
			}
			// Si el token es ) estoy aplicando lambda, regla 19
			else if(sig_tok.getTipo().equals("separador") && (int) sig_tok.getValor() == 4){
				parse += "  " + 19;
				tipo = "vacio";
			}
			else {
				acabarConError();
				GE.lanzarExcepSint(aLex.getNLinea(), matriz.toStringToken(sig_tok));
			}
		}
		// Si el token es null hay un error
		else {
			acabarConError();
			GE.lanzarExcepSint(aLex.getNLinea());
		}
		
		return tipo;
	}
	
	private String k() throws Exception {
		String tipo = null;
		
		if(sig_tok != null) {
			// Si es el ',' aplico la regla 20
			if(sig_tok.getTipo().equals("separador") && (int) sig_tok.getValor() == 2 ) {
				parse += "  " + 20;
				
				// Equiparo el token (ya se que es un ',') y pido otro token
				sig_tok = getToken();
				
				Pair<String, Integer> tT = t();
				
				CTS.comprobarIdEnFun(sig_tok);
				Token tok = sig_tok;
				// Equiparo el token (con un 'id')
				equiparar("id");
				
				String tK = k();
				
				nParam += 1;
				
				CTS.addEntrada(tok.getPos(), tT.getLeft(), desp);
				desp += tT.getRight();
				tipo = tK.equals("vacio")? tT.getLeft(): tT.getLeft()+", "+tK;
			}
			// Si el token es ) estoy aplicando lambda, regla 21
			else if(sig_tok.getTipo().equals("separador") && (int) sig_tok.getValor() == 4){
				parse += "  " + 21;
				
				tipo = "vacio";
			}
			else {
				acabarConError();
				GE.lanzarExcepSint(aLex.getNLinea(), matriz.toStringToken(sig_tok));
			}
		}
		// Si el token es null hay un error
		else {
			acabarConError();
			GE.lanzarExcepSint(aLex.getNLinea());
		}
		
		return tipo;
	}

	private Pair<String, String> c() throws Exception {
		String tipo = null, tipoRet = null;
		
		if(sig_tok != null) {
			// Si es el FIRST (B) aplico la regla 22
			if(sig_tok.getTipo().equals("id") || sig_tok.getTipo().equals("palabraReservada") && 
					((int) sig_tok.getValor() == 1 || (int) sig_tok.getValor() == 3 || ( (int) sig_tok.getValor() >= 8 && (int) sig_tok.getValor() <=12) )) {
				parse += "  " + 22;
				
				Pair<String, String> tB = b();
				Pair<String, String> tC = c();
				
				tipo = (tB.getLeft().equals(tC.getLeft()) && tB.getLeft().equals("tipo_ok"))? "tipo_ok": "tipo_error";
				tipoRet = (tB.getRight().equals(tC.getRight()) || tC.getRight().equals("vacio"))? tB.getRight(): tB.getRight().equals("vacio")? tC.getRight(): "tipo_error" ;
			}
			// Si es } estoy aplicando lambda, regla 23
			else if(sig_tok.getTipo().equals("separador") && (int) sig_tok.getValor() == 6 ) {
				parse += "  " + 23;
				
				tipo = "tipo_ok";
				tipoRet = "vacio";
			}
			else {
				acabarConError();
				GE.lanzarExcepSint(aLex.getNLinea(), matriz.toStringToken(sig_tok));
			}
		}
		// Si el token es null hay un error
		else {
			acabarConError();
			GE.lanzarExcepSint(aLex.getNLinea());
		}
		
		return new Pair<String, String>(tipo, tipoRet);
	}

	private Pair<String, String> s() throws Exception {
		String tipo = null, tipoRet = null;
		
		if(sig_tok != null) {
			// Si el token es 'id' aplico la regla 24
			if(sig_tok.getTipo().equals("id")) {
				parse += "  " + 24;
				
				usaFun = false;
				String id = (String) sig_tok.getValor();
				if(!CTS.tieneTipo(sig_tok)) {
					CTS.addEntradaGlobal(sig_tok, "entero", enFun? despG: desp);
					if(enFun) {
						despG += 1;
					}else {
						desp += 1;
					}
				}
				int pos = sig_tok.getPos();
				boolean esFun = CTS.esFun(sig_tok);
				String tTokRet = esFun? CTS.getTipoRet(pos): "vacio";
				String tTokParam = esFun? CTS.buscaTipoParamTS(pos) : "vacio";
				String tTok = CTS.buscaTipoTS(sig_tok);
				
				// Imprimo el token tras las modificaciones
				escribirFichero(ubicTokens, sig_tok.toString());
				
				// Equiparo el token (ya se que es un id) y pido otro token
				sig_tok = getToken();
				
				String tSP = sPrima();
				tipo = usaFun? (tTokParam.equals(tSP)? "tipo_ok": "tipo_error" ): tTok.equals(tSP)? "tipo_ok": "tipo_error";
				tipoRet = tTokRet;
				
				if(esFun && !usaFun) {
					acabarConError();
					GE.lanzarExcepSem(11, id, aLex.getNLinea()-1);
				}else if(!esFun && usaFun) {
					acabarConError();
					GE.lanzarExcepSem(1, id, aLex.getNLinea()-1);
				}else if(!usaFun && !tTok.equals(tSP)) {
					acabarConError();
					GE.lanzarExcepSem(4, tTok, tSP, aLex.getNLinea()-1);
				}else if(usaFun && !tTokParam.equals(tSP)) {
					acabarConError();
					GE.lanzarExcepSem(1, id, tTokParam, tSP, aLex.getNLinea()-1);
				}
			}
			// Si el token es 'alert' aplico la regla 25
			else if (sig_tok.getTipo().equals("palabraReservada") && (int) sig_tok.getValor() == 8) {
				parse += "  " + 25;
				
				// Equiparo el token (ya se que es un alert) y pido otro token
				sig_tok = getToken();
				
				// Equiparo el token (con un '(')
				equiparar("(");
				
				String t = e();
				
				// Equiparo el token (con un ')')
				equiparar(")");
				
				tipo = (t.equals("tipo_error") || t.equals("logico"))? "tipo_error" : "tipo_ok";
				tipoRet = "vacio";
				
				if(tipo.equals("tipo_error")) {
					acabarConError();
					GE.lanzarExcepSem(9, t, aLex.getNLinea());
				}
				
				// Equiparo el token (con un ';')
				equiparar(";");				
			}
			// Si el token es 'input' aplico la regla 26
			else if (sig_tok.getTipo().equals("palabraReservada") && (int) sig_tok.getValor() == 9) {
				parse += "  " + 26;
				
				// Equiparo el token (ya se que es un input) y pido otro token
				sig_tok = getToken();
				
				// Equiparo el token (con un '(')
				equiparar("(");
				
				if(!CTS.tieneTipo(sig_tok)) {
					CTS.addEntradaGlobal(sig_tok, "entero", desp);
					desp += 1;
				}
				
				tipo = sig_tok.getTipo().equals("logico")? "tipo_error": "tipo_ok";
				tipoRet = "vacio";
				
				if(tipo.equals("tipo_error")) {
					acabarConError();
					GE.lanzarExcepSem(10, sig_tok.getTipo(), aLex.getNLinea());
				}
				
				// Equiparo el token (con un 'id')
				equiparar("id");
				
				// Equiparo el token (con un ')')
				equiparar(")");
				
				// Equiparo el token (con un ';')
				equiparar(";");
				
			}
			// Si el token es 'return' aplico la regla 27
			else if (sig_tok.getTipo().equals("palabraReservada") && (int) sig_tok.getValor() == 10) {
				parse += "  " + 27;
				
				if(!enFun) {
					acabarConError();
					GE.lanzarExcepSem(2, aLex.getNLinea());
				}
				
				// Equiparo el token (ya se que es un return) y pido otro token
				sig_tok = getToken();
				
				String t = x();
				
				// Equiparo el token (con un ';')
				equiparar(";");
				
				tipo = t.equals("tipo_error")? "tipo_error" : "tipo_ok";
				tipoRet = t;
			}
			// Si el token es 'break' aplico la regla 28
			else if (sig_tok.getTipo().equals("palabraReservada") && (int) sig_tok.getValor() == 12) {
				parse += "  " + 28;
				
				if(!enWhile) {
					acabarConError();
					GE.lanzarExcepSem(3, aLex.getNLinea());
				}
				
				// Equiparo el token (ya se que es un break) y pido otro token
				sig_tok = getToken();
				
				// Equiparo el token (con un ';')
				equiparar(";");
				
				tipo = "tipo_ok";
				tipoRet = "vacio";
			}
			else {
				acabarConError();
				GE.lanzarExcepSint(aLex.getNLinea(), matriz.toStringToken(sig_tok));
			}
		}
		// Si el token es null hay un error
		else {
			acabarConError();
			GE.lanzarExcepSint(aLex.getNLinea());
		}
		
		return new Pair<String, String>(tipo, tipoRet);
	}
	
	private String sPrima() throws Exception {
		String tipo = null;
		
		if(sig_tok != null) {
			// Si el token es '=' aplico la regla 29
			if(sig_tok.getTipo().equals("igual")) {
				parse += "  " + 29;
				
				// Equiparo el token (ya se que es un =) y pido otro token
				sig_tok = getToken();
				
				tipo = e();
				
				// Equiparo el token (con un ';')
				equiparar(";");
			}
			// Si el token es '+=' aplico la regla 30
			else if(sig_tok.getTipo().equals("asignacionConSuma")) {
				parse += "  " + 30;
				
				// Equiparo el token (ya se que es un +=) y pido otro token
				sig_tok = getToken();
				
				String t = e();
				
				tipo = t.equals("logico")? "tipo_error" : t;
				if(t.equals("logico")) {
					acabarConError();
					GE.lanzarExcepSem(7, t, aLex.getNLinea());
				}
				
				// Equiparo el token (con un ';')
				equiparar(";");
			}
			// Si el token es '&=' aplico la regla 31
			else if(sig_tok.getTipo().equals("asignacionConYLogico")) {
				parse += "  " + 31;
				
				// Equiparo el token (ya se que es un &=) y pido otro token
				sig_tok = getToken();
				
				String t = e();
				
				tipo = t.equals("logico")? "logico" : "tipo_error";
				if(!t.equals("logico")) {
					acabarConError();
					GE.lanzarExcepSem(6, t, aLex.getNLinea());
				}
				
				// Equiparo el token (con un ';')
				equiparar(";");
			}
			// Si el token es '(' aplico la regla 32
			else if(sig_tok.getTipo().equals("separador") && (int) sig_tok.getValor() == 3 ) {
				parse += "  " + 32;
			
				// Equiparo el token (ya se que es un '(') y pido otro token
				sig_tok = getToken();
				
				tipo = l();
				usaFun = true;
				
				// Equiparo el token (con un ')')
				equiparar(")");
				
				// Equiparo el token (con un ';')
				equiparar(";");
			}
			else {
				acabarConError();
				GE.lanzarExcepSint(aLex.getNLinea(), matriz.toStringToken(sig_tok));
			}
		}
		// Si el token es null hay un error
		else {
			acabarConError();
			GE.lanzarExcepSint(aLex.getNLinea());
		}
		
		return tipo;
	}

	private String l() throws Exception {
		String tipo = null;
		if (sig_tok != null) {
			// Si es FIRST (E) = {id,(,ent,cad,bool,!} aplico la regla 33
			if(sig_tok.getTipo().equals("id") || sig_tok.getTipo().equals("cadena") || sig_tok.getTipo().equals("constanteEntera") || sig_tok.getTipo().equals("constanteLogica") ||
					sig_tok.getTipo().equals("operadorLogico") || (sig_tok.getTipo().equals("separador") && (int) sig_tok.getValor() == 3)) {
				parse += "  " + 33;
				
				String tE = e();
				String tQ = q();
				
				tipo = tQ.equals("vacio")? tE : tE+", "+tQ;
			}
			// Si el token es ) estoy aplicando lambda, regla 34
			else if(sig_tok.getTipo().equals("separador") && (int) sig_tok.getValor() == 4 ) {
				parse += "  " + 34;
				
				tipo = "vacio";
			}
			else {
				acabarConError();
				GE.lanzarExcepSint(aLex.getNLinea(), matriz.toStringToken(sig_tok));
			}
		}
		// Si el token es null hay un error
		else {
			acabarConError();
			GE.lanzarExcepSint(aLex.getNLinea());
		}
		
		return tipo;
	}
	
	private String q() throws Exception {
		String tipo = null;
		if (sig_tok != null) {
			// Si es ',' aplico la regla 35
			if(sig_tok.getTipo().equals("separador") && (int) sig_tok.getValor() == 2) {
				parse += "  " + 35;
				
				// Equiparo el token (ya se que es un ',') y pido otro token
				sig_tok = getToken();
				
				String tE = e();
				String tQ = q();
				
				tipo = tQ.equals("vacio")? tE : tE+", "+tQ;
			}
			// Si el token es ) estoy aplicando lambda, regla 36
			else if(sig_tok.getTipo().equals("separador") && (int) sig_tok.getValor() == 4 ) {
				parse += "  " + 36;
				
				tipo = "vacio";
			}
			else {
				acabarConError();
				GE.lanzarExcepSint(aLex.getNLinea(), matriz.toStringToken(sig_tok));
			}
		}
		// Si el token es null hay un error
		else {
			acabarConError();
			GE.lanzarExcepSint(aLex.getNLinea());
		}
		
		return tipo;
	}
	
	private String x() throws Exception {
		String tipo = null;
		
		if (sig_tok != null) {
			// Si es FIRST (E) = {id,(,ent,cad,bool,!} aplico la regla 37
			if(sig_tok.getTipo().equals("id") || sig_tok.getTipo().equals("cadena") || sig_tok.getTipo().equals("constanteEntera") || sig_tok.getTipo().equals("constanteLogica") ||
					sig_tok.getTipo().equals("operadorLogico") || (sig_tok.getTipo().equals("separador") && (int) sig_tok.getValor() == 3)) {
				parse += "  " + 37;
				
				tipo = e();
			}
			// Si el token es ; estoy aplicando lambda, regla 38
			else if(sig_tok.getTipo().equals("separador") && (int) sig_tok.getValor() == 1 ) {
				parse += "  " + 38;
				
				tipo = "vacio";
			}
			else {
				acabarConError();
				GE.lanzarExcepSint(aLex.getNLinea(), matriz.toStringToken(sig_tok));
			}
		}
		// Si el token es null hay un error
		else {
			acabarConError();
			GE.lanzarExcepSint(aLex.getNLinea());
		}
		
		return tipo;
	}
	
	private String e() throws Exception {
		String tipo = null;
		if(sig_tok != null) {
			// Si es FIRST (E) = {id,(,ent,cad,bool,!} aplico la regla 39
			if(sig_tok.getTipo().equals("id") || sig_tok.getTipo().equals("cadena") || sig_tok.getTipo().equals("constanteEntera") || sig_tok.getTipo().equals("constanteLogica") ||
					sig_tok.getTipo().equals("operadorLogico") || (sig_tok.getTipo().equals("separador") && (int) sig_tok.getValor() == 3)) {
				parse += "  " + 39;
				
				String tR = r();
				String tEP = ePrima();
				
				tipo = tEP.equals("vacio")? tR: (tR.equals(tEP) && !tR.equals("tipo_error"))? "logico": "tipo_error";
				if(!tEP.equals("vacio") && !tR.equals(tEP)) {
					acabarConError();
					GE.lanzarExcepSem(3, tR, tEP, aLex.getNLinea());
				}else if(tipo.equals("tipo_error")) {
					acabarConError();
					GE.lanzarExcepSem(1, aLex.getNLinea());
				}
			}
			else {
				acabarConError();
				GE.lanzarExcepSint(aLex.getNLinea(), matriz.toStringToken(sig_tok));
			}
		}
		// Si el token es null hay un error
		else {
			acabarConError();
			GE.lanzarExcepSint(aLex.getNLinea());
		}
		
		return tipo;
	}
	
	
	private String ePrima() throws Exception {
		String tipo = null;
		
		if(sig_tok != null) {
			// Si es == aplico la regla 40
			if (sig_tok.getTipo().equals("operadorRelacional") && (int) sig_tok.getValor() == 1) {
				parse += "  " + 40;
				
				// Equiparo el token (ya se que es un ==) y pido otro token
				sig_tok = getToken();
				
				String tR = r();	
				String tEP = ePrima();
				
				tipo = tEP.equals("vacio")? tR: (tR.equals(tEP) && !tR.equals("tipo_error"))? "logico": "tipo_error";
				if(!tEP.equals("vacio") && !tR.equals(tEP)) {
					acabarConError();
					GE.lanzarExcepSem(2, tR, tEP, aLex.getNLinea());
				}
			}
			// Si es != aplico la regla 41
			else if (sig_tok.getTipo().equals("operadorRelacional") && (int) sig_tok.getValor() == 2) {
				parse += "  " + 41;
				
				// Equiparo el token (ya se que es un !=) y pido otro token
				sig_tok = getToken();
				
				String tR = r();
				String tEP = ePrima();
				
				tipo = tEP.equals("vacio")? tR: (tR.equals(tEP) && !tR.equals("tipo_error"))? "logico": "tipo_error";
				if(!tEP.equals("vacio") && !tR.equals(tEP)) {
					acabarConError();
					GE.lanzarExcepSem(2, tR, tEP, aLex.getNLinea());
				}
			}
			// Si el token es FOLLOW(E')={),,,;} estoy aplicando lambda, regla 42
			else if(sig_tok.getTipo().equals("separador") && ((int) sig_tok.getValor() == 1 || (int) sig_tok.getValor() == 2 || (int) sig_tok.getValor() == 4)) {
				parse += "  " + 42;
				
				tipo = "vacio";
			}
			else {
				acabarConError();
				GE.lanzarExcepSint(aLex.getNLinea(), matriz.toStringToken(sig_tok));
			}
		}
		// Si el token es null hay un error
		else {
			acabarConError();
			GE.lanzarExcepSint(aLex.getNLinea());
		}
		
		return tipo;
	}

	
	private String r() throws Exception {
		String tipo = null;
		
		if (sig_tok != null) {
			// Si es FIRST (U) = {id,(,ent,cad,bool,!} aplico la regla 43
			if(sig_tok.getTipo().equals("id") || sig_tok.getTipo().equals("cadena") || sig_tok.getTipo().equals("constanteEntera") || sig_tok.getTipo().equals("constanteLogica") ||
					sig_tok.getTipo().equals("operadorLogico") || (sig_tok.getTipo().equals("separador") && (int) sig_tok.getValor() == 3)) {
				parse += "  " + 43;
				
				String tU = u();
				String tRP = rPrima();
				
				tipo =  tRP.equals("vacio")? tU: ( tU.equals("logico") || tRP.equals("tipo_error"))? "tipo_error": tRP.equals(tU)? tRP: "cadena";
				if(!tRP.equals("vacio") && tU.equals("logico")) {
					acabarConError();
					GE.lanzarExcepSem(5, tU, aLex.getNLinea());
				}
			}
			else {
				acabarConError();
				GE.lanzarExcepSint(aLex.getNLinea(), matriz.toStringToken(sig_tok));
			}
		}
		// Si el token es null hay un error
		else {
			acabarConError();
			GE.lanzarExcepSint(aLex.getNLinea());
		}
		
		return tipo;
	}
	

	private String rPrima() throws Exception {
		String tipo = null;
		
		if (sig_tok != null) {
			// Si el token es + aplico la regla 44
			if(sig_tok.getTipo().equals("operadorAritmetico")) {
				parse += "  " + 44;
				
				// Equiparo el token (ya se que es un +) y pido otro token
				sig_tok = getToken();
				
				String tU = u();
				String tRP = rPrima();
				
				tipo = ( tU.equals("logico") || tRP.equals("tipo_error"))? "tipo_error": tRP.equals("vacio")? tU: tRP.equals(tU)? tRP: "cadena";
				if(tipo.equals("tipo_error") && tU.equals("logico")) {
					acabarConError();
					GE.lanzarExcepSem(4, tU, aLex.getNLinea());
				}
			}
			// Si el token es FOLLOW(R') estoy aplicando lambda, regla 45
			else if(sig_tok.getTipo().equals("operadorRelacional") || (sig_tok.getTipo().equals("separador") && 
					((int) sig_tok.getValor() == 1 || (int) sig_tok.getValor() == 2 || (int) sig_tok.getValor() == 4))) {
				parse += "  " + 45;
				
				tipo = "vacio";
			}
			else {
				acabarConError();
				GE.lanzarExcepSint(aLex.getNLinea(), matriz.toStringToken(sig_tok));
			}
		}
		// Si el token es null hay un error
		else {
			acabarConError();
			GE.lanzarExcepSint(aLex.getNLinea());
		}
		
		return tipo;
	}
	
	private String u() throws Exception {
		String tipo = null;
		
		if(sig_tok != null) {
			// Si el token es ! aplico la regla 46
			if(sig_tok.getTipo().equals("operadorLogico")) {
				parse += "  " + 46;
				
				// Equiparo el token (ya se que es un '!') y pido otro token
				sig_tok = getToken();
				
				String tU = u();
				
				tipo = tU.equals("logico")? "logico" : "tipo_error";
				if(tipo.equals("tipo_error")) {
					acabarConError();
					GE.lanzarExcepSem(2, tU, aLex.getNLinea());
				}
			}
			// Si el token es FIRST (V)  aplico la regla 47
			else if(sig_tok.getTipo().equals("id") || sig_tok.getTipo().equals("cadena") || sig_tok.getTipo().equals("constanteEntera") ||
					sig_tok.getTipo().equals("constanteLogica") || (sig_tok.getTipo().equals("separador") && (int) sig_tok.getValor() == 3)) {
				parse += "  " + 47;
				
				tipo = v();
			}
			else {
				acabarConError();
				GE.lanzarExcepSint(aLex.getNLinea(), matriz.toStringToken(sig_tok));
			}
		}
		// Si el token es null hay un error
		else {
			acabarConError();
			GE.lanzarExcepSint(aLex.getNLinea());
		}
		
		return tipo;
	}
	
	private String v() throws Exception {
		String tipo = null;
		
		if (sig_tok != null) {
			// Si el token es id aplico la regla 48
			if(sig_tok.getTipo().equals("id")) {
				parse += "  " + 48;
				
				Token tok_temp = sig_tok;
				if(!CTS.tieneTipo(tok_temp)) {
					CTS.addEntrada(tok_temp.getPos(), "entero", desp);
					desp += 1;
				}
				boolean esFun = CTS.esFun(tok_temp);
				String tTok = CTS.buscaTipoTS(tok_temp);
				String tTokParam = esFun? CTS.buscaTipoParamTS(tok_temp.getPos()): "vacio";
				
				//Escribo el token tras las modificaciones
				escribirFichero(ubicTokens, sig_tok.toString());
				
				// Equiparo el token (ya se que es un id) y pido otro token
				sig_tok = getToken();
				
				String tVP = vPrima();
				
				tipo = tVP.equals("vacio")? tTok: tVP.equals(tTokParam)? tTok: "tipo_error";
				
				if (tipo.equals("tipo_error") && !esFun) {
					acabarConError();
					GE.lanzarExcepSem(1, (String)tok_temp.getValor(), aLex.getNLinea());
				}else if(tipo.equals("tipo_error")  && esFun) {
					acabarConError();
					GE.lanzarExcepSem(1, (String)tok_temp.getValor(), tTokParam, tVP, aLex.getNLinea());
				}
			}
			// Si el token es '(' aplico la regla 49
			else if(sig_tok.getTipo().equals("separador") && (int) sig_tok.getValor() == 3) {
				parse += "  " + 49;
				
				// Equiparo el token (ya se que es un '(') y pido otro token
				sig_tok = getToken();
				
				tipo = e();
				
				// Equiparo el token (con un ')')
				equiparar(")");
			}
			// Si el token es entero aplico la regla 50
			else if(sig_tok.getTipo().equals("constanteEntera")) {
				parse += "  " + 50;
				
				// Equiparo el token (ya se que es un entero) y pido otro token
				sig_tok = getToken();
				
				tipo = "entero";
			}
			// Si el token es cadena aplico la regla 51
			else if(sig_tok.getTipo().equals("cadena")) {
				parse += "  " + 51;
				
				// Equiparo el token (ya se que es una cadena) y pido otro token
				sig_tok = getToken();
				
				tipo = "cadena";
			}
			// Si el token es booleano aplico la regla 52
			else if(sig_tok.getTipo().equals("constanteLogica")) {
				parse += "  " + 52;
				
				// Equiparo el token (ya se que es un booleano) y pido otro token
				sig_tok = getToken();
				
				tipo = "logico";
			}
			else {
				acabarConError();
				GE.lanzarExcepSint(aLex.getNLinea(), matriz.toStringToken(sig_tok));
			}
		}
		// Si el token es null hay un error
		else {
			acabarConError();
			GE.lanzarExcepSint(aLex.getNLinea());
		}
		
		return tipo;
	}
	
	
	private String vPrima() throws Exception {
		String tipo = null;
		
		if (sig_tok != null) {
			// Si el token es '(' aplico la regla 53
			if(sig_tok.getTipo().equals("separador") && (int) sig_tok.getValor() == 3) {
				parse += "  " + 53;
				
				// Equiparo el token (ya se que es un '(') y pido otro token
				sig_tok = getToken();
				
				tipo = l();
				
				// Equiparo el token (con un ')')
				equiparar(")");
			}
			// Si el token es FOLLOW(V') estoy aplicando lambda, regla 53
			else if(sig_tok.getTipo().equals("operadorAritmetico") || sig_tok.getTipo().equals("operadorRelacional") || (sig_tok.getTipo().equals("separador") && 
					((int) sig_tok.getValor() == 1 || (int) sig_tok.getValor() == 2 || (int) sig_tok.getValor() == 4))) {
				parse += "  " + 54;
				
				tipo = "vacio";
			}
			else {
				acabarConError();
				GE.lanzarExcepSint(aLex.getNLinea(), matriz.toStringToken(sig_tok));
			}
		}
		// Si el token es null hay un error
		else {
			acabarConError();
			GE.lanzarExcepSint(aLex.getNLinea());
		}

		return tipo;
	}

	private void equiparar(String s) throws Exception {
		boolean hayError = false;
		String esperado = "";
		switch (s) {
		case "id":
			if(sig_tok.getTipo().equals("id")) {
				escribirFichero(ubicTokens, sig_tok.toString());
				sig_tok = getToken();
			}else {
				hayError = true;
				esperado = "id";
			}
			break;
		case "(":
			if(sig_tok.getTipo().equals("separador") && (int) sig_tok.getValor() == 3 ) {
				sig_tok = getToken();
			}else {
				hayError = true;
				esperado = "(";
			}
			break;
		case ")":
			if(sig_tok.getTipo().equals("separador") && (int) sig_tok.getValor() == 4 ) {
				sig_tok = getToken();
			}else {
				hayError = true;
				esperado = ")";
			}
			break;
		case "{":
			if(sig_tok.getTipo().equals("separador") && (int) sig_tok.getValor() == 5 ) {
				sig_tok = getToken();
			}else {
				hayError = true;
				esperado = "{";
			}
			break;
		case "}":
			if(sig_tok.getTipo().equals("separador") && (int) sig_tok.getValor() == 6 ) {
				sig_tok = getToken();
			}else {
				hayError = true;
				esperado = "}";
			}
			break;
		case ";":
			if(sig_tok.getTipo().equals("separador") && (int) sig_tok.getValor() == 1 ) {
				sig_tok = getToken();
			}else {
				hayError = true;
				esperado = ";";
			}
			break;
		}
		if(hayError) {
			acabarConError();
			GE.lanzarExcepSint(aLex.getNLinea(), esperado, matriz.toStringToken(sig_tok));
		}
	}
	
	public void acabarConError() {
		escribirFichero(ubicParse, parse);
		CTS.eliminarTablas();
		escribirFichero(ubicTablas, CTS.toString());
		
	}
	
	// Funcion que crea un fichero en @ubicacion
	private void crearFichero(String ubicacion) {
		File fichero = new File(ubicacion); // Tipo "C:\\demo\\music.txt"
		if (fichero.exists()){
		     fichero.delete();
		 }  
		try {
			fichero.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Funcion que escribe @lineaTexto al final del fichero existente de @ubicacion
	private void escribirFichero(String ubicacion, String lineaTexto) {
		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			fw = new FileWriter(ubicacion, true);
			pw = new PrintWriter(fw);
			pw.println(lineaTexto);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fw.close();
				pw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
