
public class Token {
	private String tipo;
	private String valorCadena;
	private int valorEntero;
	private boolean conString;
	private int pos = -1;
	
	public Token(String tipo) {
		this.tipo = tipo;
		valorCadena = null;
		conString = true;
	}
	
	public Token(String tipo, String valor) {
		this.tipo = tipo;
		valorCadena = valor;
		conString = true;
	}
	
	public Token(String tipo, int valor) {
		this.tipo = tipo;
		valorEntero = valor;
		conString = false;
	}
	
	public Token(String tipo, String valor, int pos) {
		this.tipo = tipo;
		valorCadena = valor;
		conString = true;
		this.pos = pos;
	}
	
	public String getTipo () {
		return tipo;
	}
	
	public Object getValor(){		
		return conString? valorCadena: valorEntero;
	}
	
	public void setPos(int pos) {
		this.pos = pos;
	}
	
	public int getPos() {
		return pos;
	}
	
	@Override
	public String toString() {
		String resultado = "<" + tipo;
		if (pos != -1)
			resultado += ", " + pos + ">";
		else if (conString && valorCadena != null)
			resultado += ", " + '"' + valorCadena + '"' + ">";
		else if (conString && valorCadena == null)
			resultado += ", >";
		else 
			resultado += ", " + valorEntero + ">";
		return resultado;
	}
}
