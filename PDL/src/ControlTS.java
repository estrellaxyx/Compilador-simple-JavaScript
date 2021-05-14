import es.upm.aedlib.Pair;
import es.upm.aedlib.indexedlist.ArrayIndexedList;
import es.upm.aedlib.indexedlist.IndexedList;


public class ControlTS {
	private TablaSimbolos TSG;
	private TablaSimbolos TSF;
	private int contador;
	private TablaSimbolos TSActual;
	private IndexedList<TablaSimbolos> tablOrd = new ArrayIndexedList<TablaSimbolos>(); 

	public ControlTS() {
		contador = 0;
	}
	
	/**
	 * Actualiza la TSActual a la dada
	 * @param tabla
	 */
	public void setTSActual(TablaSimbolos tabla) {
		TSActual = tabla;
	}
	
	/**
	 * Asigna la tabla TSG y la devuelve
	 * @param tabla
	 */
	public TablaSimbolos setTSG(TablaSimbolos tabla) {
		TSG = tabla;
		contador++;
		TSG.setNumero(contador);
		tablOrd.add(0, TSG);
		return TSG;
	}
	
	/**
	 * Asigna la tabla TSF y la devuelve
	 * @param tabla
	 */
	public TablaSimbolos setTSF(TablaSimbolos tabla) {
		TSF = tabla;
		contador++;
		TSF.setNumero(contador);
		return TSF;
	}

	public Pair<Boolean, TablaSimbolos> estaId(String id) {
		boolean esta = false;
		TablaSimbolos usada = null;
		if (TSF != null) {
			usada = TSF;
			esta = TSF.estaId(id);
		}
		if(TSG != null && !esta)	{
			usada = TSG;
			esta = TSG.estaId(id);
		}
		return new Pair<Boolean, TablaSimbolos>(esta, usada);
	}
	
	public int posEnTabla(String id) {
		Pair<Boolean, TablaSimbolos> entrada = estaId(id);
		int res = -1;
		if(entrada.getLeft()) {
			res = entrada.getRight().estaPos(id);
		}
		return res + 1;
	}

	/**
	 * Elimino las tablas y las añado en el array para luego imprimirlas en orden
	 */
	public void eliminarTablas() {
		if (TSActual != null && !TSActual.equals(TSG)) {
			tablOrd.add(TSF.getNumero()-1, TSF.clone());
			TSF = null;
		}
	}
	
	
	/**
	 * Elimina la tabla dada y devuelve su toString
	 * @param tabla
	 * @return
	 */
	public void eliminarTabla(TablaSimbolos tabla) {
		if(tabla.equals(TSF)) {
			tablOrd.add(TSF.getNumero()-1, TSF.clone());
			TSF = null;
		}
	}

	
	/**
	 * Añade un lexema (sin valores) a la TSActual
	 * @param lexema
	 * @return
	 */
	public int addLexema(String lexema) {
		return TSActual.addLexema(lexema);
	}
	
	
	/**
	 * Añade una entrada en la tabla de simbolos actual.
	 * Se modifican los valores del lexema en la posicion de la TSActual dada.
	 * @param pos
	 * @param tipo
	 * @param desp
	 */
	public void addEntrada(int pos, String tipo, int desp) {
		String[] valores = {tipo, (desp < 0)? null: ""+desp};
		TSActual.addValores(pos, valores);
	}
	
	/**
	 * Añade una entrada en la tabla de simbolos global y si la TSActual es TSF lo elimina y modifica su pos.
	 * Se modifican los valores del lexema en la posicion de la TSActual dada.
	 * @param pos
	 * @param tipo
	 * @param desp
	 */
	public void addEntradaGlobal(Token tok, String tipo, int desp) {
		if(TSActual.equals(TSF)) {
			TSF.eliminarEntrada(tok.getPos());
			tok.setPos(TSG.addLexema((String) tok.getValor()));
		}
		String[] valores = {tipo, (desp < 0)? null: ""+desp};
		TSG.addValores(tok.getPos(), valores);
	}
	
	/**
	 * Añade una entrada de tipo funcion en la tabla de simbolos actual
	 * @param tok
	 * @param tipo
	 * @param tipoParam
	 * @param nParam
	 * @param desp
	 */
	public void addEntrada(Token tok, String tipo, String tipoParam, int nParam) {
		String[] valores = {"funcion", null, ""+nParam, tipoParam, null, tipo, "Et_"+tok.getValor()};
		TSG.addValores(tok.getPos(), valores);
	}
	
	/**
	 * Como es un parametro si ya hay otro id en la TSG con el mismo lexema 
	 * se debe crear otra entrada en la TSF.
	 * @param tok
	 */
	public void comprobarIdEnFun(Token tok) {
		if(TSG.getSizeTabla() >= tok.getPos() && TSG.estaId((String) tok.getValor())) {
			if(!TSG.estaTokTipo(tok).getRight()) {
				TSG.eliminarEntrada(tok.getPos());
			}
			tok.setPos(TSF.addLexema((String)tok.getValor()));
		}
	}
	
	/**
	 * Mira en la todas las tablas (empezando por la TSActual) 
	 * si tiene tipo el id en la pos dada. 
	 * @param pos
	 * @return
	 */
	public boolean tieneTipo(Token tok) {
		Pair<Boolean, Boolean> res;
		if(TSActual.equals(TSG)) {
			res = TSActual.estaTokTipo(tok);
		}
		else {
			res = TSActual.estaTokTipo(tok);
			if(!res.getLeft()) {
				res = TSG.estaTokTipo(tok);
			}
		}
		return res.getRight();
	}
	
	/**
	 * Mira en las tablas (empezando por la TSActual) 
	 * cual es el tipo del id en la pos dada.
	 * @param tok
	 * @return
	 */
	public String buscaTipoTS(Token tok) {
		String res = null;
		if(TSActual.equals(TSG)) {
			res = TSActual.getTipo(tok);
		}
		else {
			res = TSActual.getTipo(tok);
			if(res == null) {
				res = TSG.getTipo(tok);
			}
		}
		return res;
	}
	
	/**
	 * Mira en la TSG si el id pertenece a una funcion.
	 * @param tok
	 * @return
	 */
	public boolean esFun(Token tok) {
		return TSG.esFun(tok);
	}
	
	/**
	 *  Mira en la TSG y devuelve el tipo de retorno. <br>
	 *  Previamente se debe comprobar que es una funcion.
	 * @param pos
	 * @return
	 */
	public String getTipoRet(int pos) {
		return TSG.getTipoRet(pos);
	}
	
	/**
	 * Devuelve el tipo de los parametros de una funcion (lo busca en la TSG). <br>
	 * Como la funcion a la que llama no verifica nada (no evita errores),
	 * solo se la debe llamar cuando se sabe que el id es de una funcion.
	 * @param pos
	 * @return
	 */
	public String buscaTipoParamTS(int pos) {
		return TSG.getTipoParam(pos);
	}
	
	
	@Override
	public String toString() {
		String res = "";
		for (TablaSimbolos tS : tablOrd) {
			res += tS.toString()+"\n";
		}
		return res;
	}
}