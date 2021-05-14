import java.io.BufferedReader;
import java.io.IOException;

import es.upm.aedlib.Pair;

public class AnalizadorLexico {
	private int estado = 0;
	private int accion;
	private ControlTS CTS;
	private GestorErrores GE;
	private BufferedReader br;
	private MT_AFD matriz;
	private String lexema;
	private int valor;
	private int contador;
	private int codigo;
	private Integer guardado = null;

	private int nLinea = 1;

	public AnalizadorLexico(BufferedReader br, MT_AFD matriz, ControlTS CTS, GestorErrores GE) {
		this.br = br;
		this.matriz = matriz;
		this.CTS = CTS;
		this.GE = GE;
		lexema = "";
		valor = 0;
		contador = 0;
	}

	public Token Alex() throws Exception {
		// bucle hasta un estado final, cuando envia el token
		int leido = -1;
		estado = 0;
		lexema = "";
		valor = 0;
		contador = 0;
		if (guardado == null) {
			try {
				leido = br.read();
			} catch (IOException e) {
			}
		} else {
			leido = guardado;
		}
		while (estado < 20) {
			// leemos siguiente caracter
			if (leido == -1) { // indica el fin del stream
				return null;
			}

			// Aumento el numero de linea si es un <eol>
			if (leido == 10 && (guardado == null || guardado != 10)) {
				nLinea++;
				guardado = null;
			}

			int estadoAnt = estado;
			Pair<Integer, Integer> par = matriz.estadoAccion(estado, leido);
			accion = par.getRight();
			estado = par.getLeft();
			char cAccion = (char) accion;

			if (estado == -2) {
				GE.lanzarExcepLex(4, leido, nLinea);
			}

			if (estado == -1 && estadoAnt != 11 && estadoAnt != 2) {
				GE.lanzarExcepLex(1, leido, nLinea);
			} else if (estado == -1 && estadoAnt == 11) {
				estado = 11;
				cAccion = 'X';
			} else if (estado == -1 && estadoAnt == 2) {
				estado = 2;
				cAccion = 'F';
			}
			
			switch (cAccion) {
			case 'A':
				leido = br.read();
				break;
			case 'B':
				lexema += (char) leido;
				leido = br.read();
				break;
			case 'C':
				lexema += (char) leido;
				leido = br.read();
				break;
			case 'D':
				guardado = leido;
				if (lexema.equals("true"))
					return new Token("constanteLogica", 1);
				if (lexema.equals("false"))
					return new Token("constanteLogica", 2);
				codigo = matriz.getPalabra(lexema);
				if (codigo != -1)
					return new Token("palabraReservada", codigo);

				// Busco el id en la TS
				int pos = CTS.posEnTabla(lexema);

				// Si no esta lo añado
				if (pos <= 0)
					pos = CTS.addLexema(lexema);
				Token id = new Token("id", lexema, pos);
				id.setPos(pos);
				return id;
			case 'E':
				leido = br.read();
				break;
			case 'F':
				lexema += (char) leido;
				contador++;
				leido = br.read();
				break;
			case 'G':
				guardado = null;
				if (contador > 64)
					GE.lanzarExcepLex(2, lexema, nLinea);
				return new Token("cadena", lexema);
			case 'H':
				valor = leido - 48; // El 0 en ascii es el 48
				leido = br.read();
				break;
			case 'I':
				valor = valor * 10 + (leido - 48);
				leido = br.read();
				break;
			case 'J':
				guardado = leido;
				if (valor > 32767)
					GE.lanzarExcepLex(3, valor, nLinea);
				return new Token("constanteEntera", valor);
			case 'K':
				leido = br.read();
				break;
			case 'L':
				guardado = leido;
				return new Token("igual");
			case 'M':
				leido = br.read();
				break;
			case 'N':
				guardado = leido;
				return new Token("operadorRelacional", 1); // Es el ==
			case 'O':
				leido = br.read();
				break;
			case 'P':
				guardado = leido;
				return new Token("operadorAritmetico", 1); // Es el +
			case 'Q':
				leido = br.read();
				break;
			case 'R':
				guardado = leido;
				return new Token("asignacionConSuma");
			case 'S':
				leido = br.read();
				break;
			case 'T':
				guardado = leido;
				return new Token("operadorLogico", 1); // Es el !
			case 'U':
				leido = br.read();
				break;
			case 'V':
				guardado = leido;
				return new Token("operadorRelacional", 2); // Es el !=
			case 'W':
				guardado = null;
				codigo = matriz.getSeparador((char) leido);
				return new Token("separador", codigo);
			case 'X':
				leido = br.read();
				break;
			case 'Y':
				return new Token("asignacionConYLogico"); // Es el &=
			default:
				break;
			}

		}
		return null;
	}

	public int getNLinea() {
		return nLinea;
	}
}
