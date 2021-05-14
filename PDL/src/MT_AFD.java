import es.upm.aedlib.Entry;
import es.upm.aedlib.Pair;
import es.upm.aedlib.map.HashTableMap;

public class MT_AFD {
	// El primer par es el estado actual y el caracter leido, y el segundo es el estado al que transitar y la acción a ejecutar
	private HashTableMap<Pair<Integer, Integer>, Pair<Integer, Integer>> MT;
	private HashTableMap<Character, Integer> tablaSeparadores;
	private HashTableMap<String, Integer> tablaPalabras;
	
	public MT_AFD() {
		MT = new HashTableMap<Pair<Integer,Integer>, Pair<Integer,Integer>>();
		Pair<Integer, Integer> error = new Pair<Integer, Integer>(-2, 30);
		Pair<Integer, Integer> c22_J = new Pair<Integer, Integer>(22, (int)'J');
		Pair<Integer, Integer> c20_D = new Pair<Integer, Integer>(20, (int)'D');
		Pair<Integer, Integer> c2_F = new Pair<Integer, Integer>(2, (int)'F');
		Pair<Integer, Integer> c1_C = new Pair<Integer, Integer>(1, (int)'C');
		Pair<Integer, Integer> c27_T = new Pair<Integer, Integer>(27, (int)'T');
		Pair<Integer, Integer> c11_X = new Pair<Integer, Integer>(11, (int)'X');
		Pair<Integer, Integer> c23_L = new Pair<Integer, Integer>(23, (int)'L');
		Pair<Integer, Integer> c24_N = new Pair<Integer, Integer>(24, (int)'N');
		Pair<Integer, Integer> c25_P = new Pair<Integer, Integer>(25, (int)'P');
		Pair<Integer, Integer> c26_R = new Pair<Integer, Integer>(26, (int)'R');
		Pair<Integer, Integer> c28_V = new Pair<Integer, Integer>(28, (int)'V');
		
		// Fila 0
		MT.put(new Pair<Integer, Integer>(0, (int)'('), new Pair<Integer, Integer>(29, (int)'W')); //0,c3
		MT.put(new Pair<Integer, Integer>(0, (int)'/'), new Pair<Integer, Integer>(10, (int)'X')); //0,/
		MT.put(new Pair<Integer, Integer>(0, 10), new Pair<Integer, Integer>(0, (int)'A')); //0,<eol>
		MT.put(new Pair<Integer, Integer>(0, 13), new Pair<Integer, Integer>(0, (int)'A')); //0,<eol>
		MT.put(new Pair<Integer, Integer>(0, (int)'!'), new Pair<Integer, Integer>(8, (int)'S')); //0,!
		MT.put(new Pair<Integer, Integer>(0, (int)'='), new Pair<Integer, Integer>(4, (int)'K')); //0,=
		MT.put(new Pair<Integer, Integer>(0, 9), new Pair<Integer, Integer>(0, (int)'A')); //0,<tab><b>
		MT.put(new Pair<Integer, Integer>(0, (int)'+'), new Pair<Integer, Integer>(6, (int)'O')); //0,+
		MT.put(new Pair<Integer, Integer>(0, (int)'0'), new Pair<Integer, Integer>(3, (int)'H')); //0,d
		MT.put(new Pair<Integer, Integer>(0, (int)'"'), new Pair<Integer, Integer>(2, (int)'E')); //0,"
		MT.put(new Pair<Integer, Integer>(0, (int)'A'), new Pair<Integer, Integer>(1, (int)'B')); //0,l
		MT.put(new Pair<Integer, Integer>(0, (int)'_'), error); //0,_
		MT.put(new Pair<Integer, Integer>(0, (int)'&'), new Pair<Integer, Integer>(12, (int)'X')); //0,&
		
		// Fila 1
		MT.put(new Pair<Integer, Integer>(1, (int)'('),c20_D); 	//1,c3
		MT.put(new Pair<Integer, Integer>(1, (int)'/'), c20_D); //1,/
		MT.put(new Pair<Integer, Integer>(1, 10), c20_D); 		//1,<eol>
		MT.put(new Pair<Integer, Integer>(1, 13), c20_D); 		//1,<eol>
		MT.put(new Pair<Integer, Integer>(1, (int)'!'), c20_D); //1,!
		MT.put(new Pair<Integer, Integer>(1, (int)'='), c20_D); //1,=
		MT.put(new Pair<Integer, Integer>(1, 9), c20_D); 		//1,<tab><b>
		MT.put(new Pair<Integer, Integer>(1, (int)'+'), c20_D); //1,+
		MT.put(new Pair<Integer, Integer>(1, (int)'0'), c1_C); 	//1,d
		MT.put(new Pair<Integer, Integer>(1, (int)'"'), c20_D); //1,"
		MT.put(new Pair<Integer, Integer>(1, (int)'A'), c1_C); 	//1,l
		MT.put(new Pair<Integer, Integer>(1, (int)'_'), c1_C); 	//1,_
		MT.put(new Pair<Integer, Integer>(1, (int)'&'), c20_D); //1,&
		
		// Fila 2
		MT.put(new Pair<Integer, Integer>(2, (int)'('),c2_F); 	//2,c3
		MT.put(new Pair<Integer, Integer>(2, (int)'/'), c2_F); //2,/
		MT.put(new Pair<Integer, Integer>(2, 10), c2_F); 		//2,<eol>
		MT.put(new Pair<Integer, Integer>(2, 13), c2_F); 		//2,<eol>
		MT.put(new Pair<Integer, Integer>(2, (int)'!'), c2_F); //2,!
		MT.put(new Pair<Integer, Integer>(2, (int)'='), c2_F); //2,=
		MT.put(new Pair<Integer, Integer>(2, 9), c2_F); 		//2,<tab><b>
		MT.put(new Pair<Integer, Integer>(2, (int)'+'), c2_F); //2,+
		MT.put(new Pair<Integer, Integer>(2, (int)'0'), c2_F); 	//2,d
		MT.put(new Pair<Integer, Integer>(2, (int)'"'), new Pair<Integer, Integer>(21, (int)'G')); //2,"
		MT.put(new Pair<Integer, Integer>(2, (int)'A'), c2_F); 	//2,l
		MT.put(new Pair<Integer, Integer>(2, (int)'_'), c2_F); 	//2,_
		MT.put(new Pair<Integer, Integer>(2, (int)'&'), c2_F); 	//2,&
		
		// Fila 3
		MT.put(new Pair<Integer, Integer>(3, (int)'('),c22_J); 	//3,c3
		MT.put(new Pair<Integer, Integer>(3, (int)'/'), c22_J); //3,/
		MT.put(new Pair<Integer, Integer>(3, 10), c22_J); 		//3,<eol>
		MT.put(new Pair<Integer, Integer>(3, 13), c22_J); 		//3,<eol>
		MT.put(new Pair<Integer, Integer>(3, (int)'!'), c22_J); //3,!
		MT.put(new Pair<Integer, Integer>(3, (int)'='), c22_J); //3,=
		MT.put(new Pair<Integer, Integer>(3, 9), c22_J); 		//3,<tab><b>
		MT.put(new Pair<Integer, Integer>(3, (int)'+'), c22_J); //3,+
		MT.put(new Pair<Integer, Integer>(3, (int)'0'), new Pair<Integer, Integer>(3, (int)'I')); 	//3,d
		MT.put(new Pair<Integer, Integer>(3, (int)'"'), c22_J); 	//3,"
		MT.put(new Pair<Integer, Integer>(3, (int)'A'), c22_J); 	//3,l
		MT.put(new Pair<Integer, Integer>(3, (int)'_'), c22_J); 	//3,_
		MT.put(new Pair<Integer, Integer>(3, (int)'&'), c22_J); 	//3,&
		
		// Fila 4
		MT.put(new Pair<Integer, Integer>(4, (int)'('),c23_L); 	//4,c3
		MT.put(new Pair<Integer, Integer>(4, (int)'/'), c23_L); //4,/
		MT.put(new Pair<Integer, Integer>(4, 10), c23_L); 		//4,<eol>
		MT.put(new Pair<Integer, Integer>(4, 13), c23_L); 		//4,<eol>
		MT.put(new Pair<Integer, Integer>(4, (int)'!'), c23_L); //4,!
		MT.put(new Pair<Integer, Integer>(4, (int)'='), new Pair<Integer, Integer>(5, (int)'M')); //4,=
		MT.put(new Pair<Integer, Integer>(4, 9), c23_L); 		//4,<tab><b>
		MT.put(new Pair<Integer, Integer>(4, (int)'+'), c23_L); //4,+
		MT.put(new Pair<Integer, Integer>(4, (int)'0'), c23_L); 	//4,d
		MT.put(new Pair<Integer, Integer>(4, (int)'"'), c23_L); 	//4,"
		MT.put(new Pair<Integer, Integer>(4, (int)'A'), c23_L); 	//4,l
		MT.put(new Pair<Integer, Integer>(4, (int)'_'), c23_L); 	//4,_
		MT.put(new Pair<Integer, Integer>(4, (int)'&'), c23_L); 	//4,&
		
		// Fila 5
		MT.put(new Pair<Integer, Integer>(5, (int)'('),c24_N); 	//5,c3
		MT.put(new Pair<Integer, Integer>(5, (int)'/'), c24_N); //5,/
		MT.put(new Pair<Integer, Integer>(5, 10), c24_N); 		//5,<eol>
		MT.put(new Pair<Integer, Integer>(5, 13), c24_N); 		//5,<eol>
		MT.put(new Pair<Integer, Integer>(5, (int)'!'), c24_N); //5,!
		MT.put(new Pair<Integer, Integer>(5, (int)'='), c24_N); //5,=
		MT.put(new Pair<Integer, Integer>(5, 9), c24_N); 		//5,<tab><b>
		MT.put(new Pair<Integer, Integer>(5, (int)'+'), c24_N); //5,+
		MT.put(new Pair<Integer, Integer>(5, (int)'0'), c24_N); 	//5,d
		MT.put(new Pair<Integer, Integer>(5, (int)'"'), c24_N); 	//5,"
		MT.put(new Pair<Integer, Integer>(5, (int)'A'), c24_N); 	//5,l
		MT.put(new Pair<Integer, Integer>(5, (int)'_'), c24_N); 	//5,_
		MT.put(new Pair<Integer, Integer>(5, (int)'&'), c24_N); 	//5,&
		
		// Fila 6
		MT.put(new Pair<Integer, Integer>(6, (int)'('),c25_P); 	//6,c3
		MT.put(new Pair<Integer, Integer>(6, (int)'/'), c25_P); //6,/
		MT.put(new Pair<Integer, Integer>(6, 10), c25_P); 		//6,<eol>
		MT.put(new Pair<Integer, Integer>(6, 13), c25_P); 		//6,<eol>
		MT.put(new Pair<Integer, Integer>(6, (int)'!'), c25_P); //6,!
		MT.put(new Pair<Integer, Integer>(6, (int)'='), new Pair<Integer, Integer>(7, (int)'Q')); //6,=
		MT.put(new Pair<Integer, Integer>(6, 9), c25_P); 		//6,<tab><b>
		MT.put(new Pair<Integer, Integer>(6, (int)'+'), c25_P); //6,+
		MT.put(new Pair<Integer, Integer>(6, (int)'0'), c25_P); 	//6,d
		MT.put(new Pair<Integer, Integer>(6, (int)'"'), c25_P); 	//6,"
		MT.put(new Pair<Integer, Integer>(6, (int)'A'), c25_P); 	//6,l
		MT.put(new Pair<Integer, Integer>(6, (int)'_'), c25_P); 	//6,_
		MT.put(new Pair<Integer, Integer>(6, (int)'&'), c25_P); 	//6,&
		
		// Fila 7
		MT.put(new Pair<Integer, Integer>(7, (int)'('),c26_R); 	//7,c3
		MT.put(new Pair<Integer, Integer>(7, (int)'/'), c26_R); //7,/
		MT.put(new Pair<Integer, Integer>(7, 10), c26_R); 		//7,<eol>
		MT.put(new Pair<Integer, Integer>(7, 13), c26_R); 		//7,<eol>
		MT.put(new Pair<Integer, Integer>(7, (int)'!'), c26_R); //7,!
		MT.put(new Pair<Integer, Integer>(7, (int)'='), c26_R); //7,=
		MT.put(new Pair<Integer, Integer>(7, 9), c26_R); 		//7,<tab><b>
		MT.put(new Pair<Integer, Integer>(7, (int)'+'), c26_R); //7,+
		MT.put(new Pair<Integer, Integer>(7, (int)'0'), c26_R); 	//7,d
		MT.put(new Pair<Integer, Integer>(7, (int)'"'), c26_R); 	//7,"
		MT.put(new Pair<Integer, Integer>(7, (int)'A'), c26_R); 	//7,l
		MT.put(new Pair<Integer, Integer>(7, (int)'_'), c26_R); 	//7,_
		MT.put(new Pair<Integer, Integer>(7, (int)'&'), c26_R); 	//7,&
		
		// Fila 8
		MT.put(new Pair<Integer, Integer>(8, (int)'('),c27_T); 	//8,c3
		MT.put(new Pair<Integer, Integer>(8, (int)'/'), c27_T); //8,/
		MT.put(new Pair<Integer, Integer>(8, 10), c27_T); 		//8,<eol>
		MT.put(new Pair<Integer, Integer>(8, 13), c27_T); 		//8,<eol>
		MT.put(new Pair<Integer, Integer>(8, (int)'!'), c27_T); //8,!
		MT.put(new Pair<Integer, Integer>(8, (int)'='), new Pair<Integer, Integer>(9, (int)'U')); //8,=
		MT.put(new Pair<Integer, Integer>(8, 9), c27_T); 		//8,<tab><b>
		MT.put(new Pair<Integer, Integer>(8, (int)'+'), c27_T); //8,+
		MT.put(new Pair<Integer, Integer>(8, (int)'0'), c27_T); 	//8,d
		MT.put(new Pair<Integer, Integer>(8, (int)'"'), c27_T); 	//8,"
		MT.put(new Pair<Integer, Integer>(8, (int)'A'), c27_T); 	//8,l
		MT.put(new Pair<Integer, Integer>(8, (int)'_'), c27_T); 	//8,_
		MT.put(new Pair<Integer, Integer>(8, (int)'&'), c27_T); 	//8,&
		
		// Fila 9
		MT.put(new Pair<Integer, Integer>(9, (int)'('),c28_V); 	//9,c3
		MT.put(new Pair<Integer, Integer>(9, (int)'/'), c28_V); //9,/
		MT.put(new Pair<Integer, Integer>(9, 10), c28_V); 		//9,<eol>
		MT.put(new Pair<Integer, Integer>(9, 13), c28_V); 		//9,<eol>
		MT.put(new Pair<Integer, Integer>(9, (int)'!'), c28_V); //9,!
		MT.put(new Pair<Integer, Integer>(9, (int)'='), c28_V); //9,=
		MT.put(new Pair<Integer, Integer>(9, 9), c28_V); 		//9,<tab><b>
		MT.put(new Pair<Integer, Integer>(9, (int)'+'), c28_V); //9,+
		MT.put(new Pair<Integer, Integer>(9, (int)'0'), c28_V); 	//9,d
		MT.put(new Pair<Integer, Integer>(9, (int)'"'), c28_V); 	//9,"
		MT.put(new Pair<Integer, Integer>(9, (int)'A'), c28_V); 	//9,l
		MT.put(new Pair<Integer, Integer>(9, (int)'_'), c28_V); 	//9,_
		MT.put(new Pair<Integer, Integer>(9, (int)'&'), c28_V); 	//9,&

		// Fila 10
		MT.put(new Pair<Integer, Integer>(10, (int)'('),error); 	//10,c3
		MT.put(new Pair<Integer, Integer>(10, (int)'/'), c11_X); //10,/
		MT.put(new Pair<Integer, Integer>(10, 10), error); 		//10,<eol>
		MT.put(new Pair<Integer, Integer>(10, 13), error); 		//10,<eol>
		MT.put(new Pair<Integer, Integer>(10, (int)'!'), error); //10,!
		MT.put(new Pair<Integer, Integer>(10, (int)'='), error); //10,=
		MT.put(new Pair<Integer, Integer>(10, 9), error); 		//10,<tab><b>
		MT.put(new Pair<Integer, Integer>(10, (int)'+'), error); //10,+
		MT.put(new Pair<Integer, Integer>(10, (int)'0'), error); 	//10,d
		MT.put(new Pair<Integer, Integer>(10, (int)'"'), error); 	//10,"
		MT.put(new Pair<Integer, Integer>(10, (int)'A'), error); 	//10,l
		MT.put(new Pair<Integer, Integer>(10, (int)'_'), error); 	//10,_
		MT.put(new Pair<Integer, Integer>(10, (int)'&'), error); 	//10,&

		// Fila 11
		MT.put(new Pair<Integer, Integer>(11, (int)'('),c11_X); 	//11,c3
		MT.put(new Pair<Integer, Integer>(11, (int)'/'), c11_X); //11,/
		MT.put(new Pair<Integer, Integer>(11, 10), new Pair<Integer, Integer>(0, (int)'X')); 		//11,<eol>
		MT.put(new Pair<Integer, Integer>(11, 13), new Pair<Integer, Integer>(0, (int)'X')); 		//11,<eol>
		MT.put(new Pair<Integer, Integer>(11, (int)'!'), c11_X); //11,!
		MT.put(new Pair<Integer, Integer>(11, (int)'='), c11_X); //11,=
		MT.put(new Pair<Integer, Integer>(11, 9), c11_X); 		//11,<tab><b>
		MT.put(new Pair<Integer, Integer>(11, (int)'+'), c11_X); //11,+
		MT.put(new Pair<Integer, Integer>(11, (int)'0'), c11_X); 	//11,d
		MT.put(new Pair<Integer, Integer>(11, (int)'"'), c11_X); 	//11,"
		MT.put(new Pair<Integer, Integer>(11, (int)'A'), c11_X); 	//11,l
		MT.put(new Pair<Integer, Integer>(11, (int)'_'), c11_X); 	//11,_
		MT.put(new Pair<Integer, Integer>(11, (int)'&'), c11_X); 	//11,&
		
		// Fila 12
		MT.put(new Pair<Integer, Integer>(12, (int)'('),error); 	//12,c3
		MT.put(new Pair<Integer, Integer>(12, (int)'/'), error); 	//12,/
		MT.put(new Pair<Integer, Integer>(12, 10), error); 			//12,<eol>
		MT.put(new Pair<Integer, Integer>(12, 13), error); 			//12,<eol>
		MT.put(new Pair<Integer, Integer>(12, (int)'!'), error); 	//12,!
		MT.put(new Pair<Integer, Integer>(12, (int)'='), new Pair<Integer, Integer>(30, (int)'Y')); //12,=
		MT.put(new Pair<Integer, Integer>(12, 9), error); 			//12,<tab><b>
		MT.put(new Pair<Integer, Integer>(12, (int)'+'), error); 	//12,+
		MT.put(new Pair<Integer, Integer>(12, (int)'0'), error); 	//12,d
		MT.put(new Pair<Integer, Integer>(12, (int)'"'), error); 	//12,"
		MT.put(new Pair<Integer, Integer>(12, (int)'A'), error); 	//12,l
		MT.put(new Pair<Integer, Integer>(12, (int)'_'), error); 	//12,_
		MT.put(new Pair<Integer, Integer>(12, (int)'&'), error); 	//12,&
		
		
		tablaSeparadores = new HashTableMap<Character, Integer>();
		tablaSeparadores.put(';', 1);
		tablaSeparadores.put(',', 2);
		tablaSeparadores.put('(', 3);
		tablaSeparadores.put(')', 4);
		tablaSeparadores.put('{', 5);
		tablaSeparadores.put('}', 6);
		tablaSeparadores.put('.', 7);
		tablaSeparadores.put(':', 8);
		tablaSeparadores.put('[', 9);
		tablaSeparadores.put(']', 10);
		tablaSeparadores.put((char)92, 11);

		
		tablaPalabras = new HashTableMap<String, Integer>();
		tablaPalabras.put("let", 1);
		tablaPalabras.put("function", 2);
		tablaPalabras.put("while", 3);
		tablaPalabras.put("number", 4);
		tablaPalabras.put("string", 5);
		tablaPalabras.put("boolean", 6);
		tablaPalabras.put("void", 7);
		tablaPalabras.put("alert", 8);
		tablaPalabras.put("input", 9);
		tablaPalabras.put("return", 10);
		tablaPalabras.put("if", 11);
		tablaPalabras.put("break", 12);
	}
	
	/**
	 * Devuelve el estado siguiente (-1 si no existe el par <estadoActual, c>
	 * o -2 si es un error), y la accion a realizar. 
	 * @param estadoActual
	 * @param c
	 * @return
	 */
	public Pair<Integer, Integer> estadoAccion (int estadoActual, int c) {
		int car = representante(c);
		Pair<Integer, Integer> par = new Pair<Integer, Integer>(estadoActual, car);
		int estadoSiguiente = -1;
		int accion = 0;
		if (MT.containsKey(par)) {
			estadoSiguiente = MT.get(par).getLeft();
			accion = MT.get(par).getRight();
		}
		return new Pair<Integer, Integer>(estadoSiguiente, accion);
	}
	
	/**
	 * Dado el caracter, busca su representante (un caracter que representa su tipo de caracter)
	 * @param c
	 * @return
	 */
	public int representante(int c) {
		int representante = c;
		if(c>=63 && c<=90 || c>=97 && c<=122)	//letras y ? @
			representante = 65;
		else if (c>=48 && c<=57)				//digitos
			representante = 48;
		else if(c== 9 || c==32)					//del
			representante = 9;
		else if(c==40 || c==41 || c==44 || c==46 || c == 58 || c==59 || c==91 || c==92 || c==93 || c==123 || c==125 )	//separadores, están (),.:;[\]{}
			representante = 40;
		return representante;
	}
	
	public int getSeparador(char separador) {
		return tablaSeparadores.get(separador);
	}
	
	public int getPalabra(String palabra) {
		int codigo = -1;
		if(tablaPalabras.containsKey(palabra))
			codigo = tablaPalabras.get(palabra);
		return codigo;
	}
	
	public String toStringToken(Token token) {
		String res = "";
		switch(token.getTipo()) {
		case "id":
			res += "id";
			break;
		case "constanteLogica":
			res += (int) token.getValor() == 1;
			break;
		case "palabraReservada":
			for (Entry<String, Integer> entry : tablaPalabras) {
				if(entry.getValue() == (int) token.getValor()) {
					res += entry.getKey();
				}
			}
			break;
		case "cadena":
			res += "la cadena: "+token.getValor();
			break;
		case "constanteEntera":
			res += "el número: "+token.getValor();
			break;
		case "igual":
			res += "=";
			break;
		case "operadorRelacional":
			res += (int) token.getValor() == 1? "==": "!=";
			break;
		case "asignacionConSuma":
			res += "+=";
			break;
		case "asignacionConYLogico":
			res += "&=";
			break;
		case "operadorAritmetico":
			res += "+";
			break;
		case "operadorLogico":
			res += "!";
			break;
		case "separador":
			for (Entry<Character, Integer> entry : tablaSeparadores) {
				if(entry.getValue() == (int) token.getValor()) {
					res += entry.getKey();
				}
			}
			break;
		default:
			res += "fin de fichero";			
		}
		return res;
	}
}
