import es.upm.aedlib.Pair;
import es.upm.aedlib.indexedlist.ArrayIndexedList;
import es.upm.aedlib.indexedlist.IndexedList;


public class TablaSimbolos{
	private int desplazamiento = 0;
	private IndexedList<Pair<String, String[]>> tabla;
	private int numero = 0;
	private int nId = 0;

	public TablaSimbolos(boolean esGlobal) {
		tabla = new ArrayIndexedList<Pair<String, String[]>>();
	}

	/**
	 * Añade el lexema a la TS poniendo como valores null.
	 * @param lexema
	 * @return
	 */
	public int addLexema(String lexema) {
		tabla.add(tabla.size(), new Pair<String, String[]>(lexema, null));
		nId++;
		
		return nId;
	}
	
	/**
	 * Añade los valores de un lexema sabiendo su posicion.
	 * @param pos
	 * @param valores
	 */
	public void addValores(int pos, String[] valores) {
		Pair<String, String[]> entry = tabla.get(pos-1);
		entry.setRight(valores);
		tabla.set(pos-1, entry);
	}
	
	/**
	 * Elimina la entrada en la posicion dada de la tabla.
	 * @param pos
	 */
	public void eliminarEntrada(int pos) {
		tabla.removeElementAt(pos-1);
		nId--;
	}
	
	/**
	 * Devuelve el tipo del id en la pos dada 
	 * (o null si no existe el id en la tabla).
	 * @param tok
	 * @return
	 */
	public String getTipo(Token tok) {
		String tipo = null;
		int pos = tok.getPos();
		if(tabla.size() >= pos) {
			Pair<String, String[]> par = tabla.get(pos-1);
			
			//Miro si coincide el valor del id con el String que hay en la tabla
			if(tok.getValor().equals(par.getLeft())) {	
				tipo = par.getRight().length > 2? par.getRight()[5]: par.getRight()[0];
			}
		}
		return tipo;
	}
	
	/**
	 * Devuelve el tipo de los parametros de una funcion.
	 * Como no verifica nada (no evita errores) solo se la debe llamar cuando 
	 * se sabe que el id es de una funcion.
	 * @param pos
	 * @return
	 */
	public String getTipoParam(int pos) {
		return tabla.get(pos-1).getRight()[3]; 
	}
	
	/**
	 * Miro si es funcion el id si la longitud de los valores es mayor que 2.
	 * @param tok
	 * @return
	 */
	public boolean esFun(Token tok) {
		int pos = tok.getPos();
		boolean esFun = false;
		
		//Miro si coincide el valor del id con el String que hay en la tabla
		if(tabla.size()>=pos && tok.getValor().equals(tabla.get(pos-1).getLeft())) {
			String[] valores = tabla.size() >= pos? tabla.get(pos-1).getRight(): null;
			
			if(valores != null) {
				esFun = valores.length > 2;
			}
		}
		return esFun;
	}
	
	/**
	 * Mira en la posicion del token si coicide su valor con el que hay en la tabla.
	 * A continuacion mira si tiene tipo.
	 * @param tok
	 * @return
	 */
	public Pair<Boolean, Boolean> estaTokTipo(Token tok){
		boolean esta = false, tiene = false;
		int pos = tok.getPos();
		Pair<String, String[]> par;
		
		if(tabla.size() >= tok.getPos()) {
			par = tabla.get(pos-1);
			esta = tok.getValor().equals(par.getLeft());	
			tiene = esta? par.getRight() == null? false: true: false;
		}
		return new Pair<Boolean, Boolean>(esta, tiene);
	}
	
	public String getTipoRet(int pos) {
		String[] valores = tabla.get(pos-1).getRight();
		String tipoRet = null;
		if(valores != null) {
			tipoRet = valores[0];
		}
		return tipoRet;
	}

	public boolean estaId(String id) {
		boolean esta = false;
		for (Pair<String, String[]> pair : tabla) {
			if (!esta) {
				esta = pair.getLeft().equals(id);
			}
		}
		return esta;
	}

	public int estaPos(String id) {
		boolean esta = false;
		boolean encontrado = false;
		int pos = -1;
		for (Pair<String, String[]> pair : tabla) {
			esta = esta ? true : pair.getLeft().equals(id);
			if (!encontrado)
				pos = tabla.indexOf(pair);
			encontrado = esta;
		}
		return pos;
	}

	
	public void setNumero(int numero) {
		this.numero = new Integer(numero);
	}
	
	public void setTabla(IndexedList<Pair<String, String[]>> tabla) {
		this.tabla = tabla;
	}
	
	public void setDesplazamiento(int desplazamiento) {
		this.desplazamiento = new Integer(desplazamiento);
	}
	
	public void setNId(int nId) {
		this.nId = new Integer(nId);
	}
	
	public int getSizeTabla() {
		return tabla.size();
	}
	
	public int getNumero() {
		return numero;
	}
		
	@Override
	public String toString() {
		String res = "CONTENIDO DE LA TABLA # " + numero + " :\n";
		String[] atributos;
		String cadena, cadenaAux;
		int posI = 0, posF, contador = 1;

		for (Pair<String, String[]> pair : tabla) {

			atributos = pair.getRight();
			res += "*\tLEXEMA : '" + pair.getLeft() + "'\n";
			
			if(atributos != null) {
				res += "\tATRIBUTOS :\n\t+ tipo : '" + atributos[0] + "'\n";
	
				// Caso de linea de una función
				if (atributos.length > 2) {
					res += "\t+ numParam : " + atributos[2] + "\n";
	
					// parametros (asumo que no tienen modo)
					cadena = atributos[3];
					posF = cadena.indexOf(',', 0);
					char[] num = atributos[2].toCharArray();
					if(num.length == 1 && ((int)num[0]-48) == 0) {
						// No hago nada
						// Caso en el que no tiene atributos la funcion
					}else {
						while (posF != -1) {
							cadenaAux = cadena.substring(posI, posF);
							res += "\t+ tipoParam" + contador + " : '" + cadenaAux + "'\n";
							contador++;
							// Si tuviese modo, debería añadir aqui una linea
							posI = posF +1;
							posF = cadena.indexOf(',', posI);	
						}
						if (posI == 0) {
							res += "\t+ tipoParam" + contador + " : '" + cadena + "'\n";
							posI = 0;
						} else {
							res += "\t+ tipoParam" + contador + " : '" + cadena.substring(posI, cadena.length()) + "'\n";
							posI = 0;
						}
					}
					// Si tuviese modo, debería añadir aqui una linea, es el atributos[4]
	
					// Tipo de retorno y etiqueta
					res += "\t+ TipoRetorno : '" + atributos[5] + "'\n\t+ EtiqFuncion : '" + atributos[6] + "'\n";
				} else {
					// Caso de parametro simple falta añadir desplazamiento
					res += "\t+ despl : " + atributos[1] + "\n";
				}
				// Separador entre parametros
			}
			res += "--------------------------------------\n";

		}

		return res;
	}

	public TablaSimbolos clone() {
		TablaSimbolos nueva = new TablaSimbolos(false);
		nueva.setDesplazamiento(desplazamiento);
		nueva.setNId(nId);
		nueva.setNumero(numero);
		nueva.setTabla(tabla);
		
		return nueva;
	}
}
