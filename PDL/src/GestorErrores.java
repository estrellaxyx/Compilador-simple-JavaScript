import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class GestorErrores {
	private String ubicErrores;

	public GestorErrores(String ubicacion) {
		ubicErrores = ubicacion;
	}
	
	// ERRORES LEXICOS
	/**
	 * 1: Caracter no reconocido <br>
	 * 2: Longitud de cadena fuera de rango <br>
	 * 3: Valor fuera de rango <br>
	 * 4: Caracter no permitido <br>
	 * @param codigo
	 */
	public void lanzarExcepLex(int codigo, Object valor, int nLinea) throws Exception {
		String error = "";
		switch (codigo) {
		case 1: 
			error = "Error léxico en la línea "+nLinea+", se ha leido el caracter no reconocido: "+(char) ((int) valor)+".";
			break;
		case 2:
			error = "Error léxico en la línea "+nLinea+", se ha leido la cadena: \""+(String) valor+"\" que tiene una longitud fuera de rango.";
			break;
		case 3:
			error = "Error léxico en la línea "+nLinea+", se ha leido el entero: "+(int) valor+" que tiene un valor fuera de rango.";
			break;
		case 4:
			error = "Error léxico en la línea "+nLinea+", se ha leido el caracter no permitido: "+(char) ((int) valor)+".";
		}
		escribirFichero(ubicErrores, error);
		throw new Exception(error);
	}
	
	
	//ERRORES SINTACTICOS
	/**
	 * Error de cadena no esperada
	 * @param linea
	 * @param esperado
	 * @param encontrado
	 * @throws Exception
	 */
	public void lanzarExcepSint(int linea, String esperado, String encontrado) throws Exception {
		String error = "Error sintáctico en la línea "+linea+", se esperaba un "+esperado+" y se ha encontrado "+encontrado+".";
		escribirFichero(ubicErrores, error);
		throw new Exception(error);
	}
	/**
	 * Error, se esperaban mas valores
	 * @param linea
	 * @throws Exception
	 */
	public void lanzarExcepSint(int linea) throws Exception{
		String error = "Error sintáctico en la línea "+linea+", se esperaban más valores y se ha encontrado el final de fichero.";
		escribirFichero(ubicErrores, error);
		throw new Exception(error);
	}
	
	/**
	 * Error, cadena encontrada no valida
	 * @param linea
	 * @param encontrado
	 * @throws Exception
	 */
	public void lanzarExcepSint(int linea, String encontrado) throws Exception{
		String error = "Error sintáctico en la línea "+linea+", se ha encontrado "+encontrado+", pero no es válido.";
		escribirFichero(ubicErrores, error);
		throw new Exception(error);
	}
	
	
	//ERRORES SEMANTICOS
	/**
	 * 2. Error, tipos tras un comparador distintos <br>
	 * 3. Error, se estan comparando tipos distintos <br>
	 * 4. Error, el tipo del id no coincide con el de la sentencia <br>
	 * 5. Error, en la inicializacion se le asigna algo con tipo distinto de el del id <br>
	 * @param codigo
	 * @param tipo1
	 * @param tipo2
	 * @param nLinea
	 * @throws Exception
	 */
	public void lanzarExcepSem(int codigo, String tipo1, String tipo2, int nLinea) throws Exception {
		String error = "";
		switch (codigo) {
		case 2:
			error = "Error semántico en la línea "+nLinea+", los tipos de los terminos tras la comparacion son de tipos distintos, son \""+tipo1+"\" y \""+tipo2+"\".";
			break;
		case 3:
			error = "Error semántico en la línea "+nLinea+", se estan comparando terminos (o expresiones) de tipos distintos, uno es de tipo \""+tipo1+"\" y el otro de tipo \""+tipo2+"\".";
			break;
		case 4:
			error = "Error semántico en la línea "+nLinea+", la sentencia es incorrecta por no coincidir los tipos, ya que el tipo del id es \""+tipo1+"\" y el del resto de la sentencia es \""+tipo2+"\".";
			break;
		case 5:
			error = "Error semántico en la línea "+nLinea+", al inicializar el id que tiene tipo \""+tipo1+"\" se le asigna un valor con tipo distinto (\""+tipo2+"\").";
			break;
		}
		escribirFichero(ubicErrores, error);
		throw new Exception(error);
	}
	
	/**
	 * Error, cuerpo de funcion incorrecto
	 * @throws Exception
	 */
	public void lanzarExcepSem() throws Exception{
		String error = "Cuerpo de la función incorrecto.";
		escribirFichero(ubicErrores, error);
		throw new Exception(error);
	}
	
	/**
	 * 1. Error, se usa un id como si fuese una funcion. id(...) <br>
	 * 2. Error, se usa una negacion en un termino no logico. <br>
	 * 3. Error, en un if se usa una expresion de tipo no logico. <br>
	 * 4. Error, tipo tras la suma incorrecto <br>
	 * 5. Error, tipo al inicio de la suma incorrecto <br>
	 * 6. Error, se usa &= con un tipo incorrecto <br>
	 * 7. Error, se usa += con un tipo incorrecto <br>
	 * 8. Error, en un while se usa una expresion de tipo no logico <br>
	 * 9. Error, en un alert se usa una expresion de tipo no valido <br>
	 * 10. Error, en un input se usa un id de tipo no valido <br>
	 * 11. Error, no se puede usar la asignacion para una funcion <br>
	 * @param codigo
	 * @param cadena
	 * @param nLinea
	 * @throws Exception
	 */
	public void lanzarExcepSem(int codigo, String cadena, int nLinea) throws Exception {
		String error = "";
		switch (codigo) {
		case 1:
			error = "Error semántico en la línea "+nLinea+", \""+cadena+"\" no se puede usar como si fuese una funcion.";
			break;
		case 2:
			error = "Error semántico en la línea "+nLinea+", la negacion no se puede usar con terminos de tipo no logicos. El tipo encontrado es \""+cadena+"\".";
			break;
		case 3:
			error = "Error semántico en la línea "+nLinea+", en un if, la expresion debe ser de tipo logica y se ha encontrado de tipo \""+cadena+"\".";
			break;
		case 4:
			error = "Error semántico en la línea "+nLinea+", el tipo tras la suma no es valido, es \""+cadena+"\" y deberia ser entero o una cadena.";
			break;
		case 5:
			error = "Error semántico en la línea "+nLinea+", el tipo al inicio de la suma no es valido, es \""+cadena+"\" y deberia ser entero o una cadena.";
			break;
		case 6:
			error = "Error semántico en la línea "+nLinea+", no se puede usar el tipo \""+cadena+"\" con el operador &=, deberia ser logico.";
			break;
		case 7:
			error = "Error semántico en la línea "+nLinea+", no se puede usar el tipo \""+cadena+"\" con el operador +=, deberia ser entero o una cadena.";
			break;
		case 8:
			error = "Error semántico en la línea "+nLinea+", en un while, la expresion debe ser de tipo logica y se ha encontrado de tipo \""+cadena+"\".";
			break;
		case 9:
			error = "Error semántico en la línea "+nLinea+", en un alert, la expresion debe ser de tipo entera o cadena y se ha encontrado de tipo \""+cadena+"\".";
			break;
		case 10:
			error = "Error semántico en la línea "+nLinea+", en un input, el id debe ser de tipo entero o cadena y se ha encontrado de tipo \""+cadena+"\".";
			break;
		case 11:
			error = "Error semántico en la línea "+nLinea+", se esta usando la asignacion para la funcion \""+cadena+"\", lo cual no es correcto.";
			break;
		}
		escribirFichero(ubicErrores, error);
		throw new Exception(error);
	}
	
	/**
	 * 1. Error, se usa una funcion sin los tipos de parametros correctos.<br>
	 * 2. Error, el tipo de retorno encontrado de la funcion es distinto al esperado. <br>
	 * 3. Error, falta una sentencia return del tipo de la funcion. <br>
	 * @param id
	 * @param tipoEsp
	 * @param tipoEnc
	 * @param nLinea
	 * @throws Exception
	 */
	public void lanzarExcepSem(int codigo, String id, String tipoEsp, String tipoEnc, int nLinea) throws Exception {
		String error = "";
		switch (codigo) {
		case 1:
			error = "Error semántico en la línea "+nLinea+", el tipo de parametros de la funcion \""+id+"\" es \""+tipoEsp+"\" y se ha encontrado \""+tipoEnc+"\".";
			break;
		case 2:
			error = "Error semántico en la línea "+nLinea+", el tipo de retorno de la funcion \""+id+"\" es \""+tipoEsp+"\" y se ha encontrado \""+tipoEnc+"\".";
			break;
		case 3:
			error = "Error semántico en la línea "+nLinea+", en la funcion \""+id+"\" falta la sentencia return que devuelva algo del tipo \""+tipoEsp+"\".";
			break;
		}
		escribirFichero(ubicErrores, error);
		throw new Exception(error);
	}
	
	/**
	 * 1. Error, la expresion tiene un error. <br>
	 * 2. Error, se usa un return fuera de una funcion. <br>
	 * 3. Error, se usa un break fuera de un bucle. <br>
	 * @param codigo
	 * @param nLinea
	 * @throws Exception
	 */
	public void lanzarExcepSem(int codigo, int nLinea) throws Exception {
		String error = "";
		switch(codigo) {
		case 1:
			error = "Error semántico en la línea "+nLinea+", la expresion tiene un error.";
			break;
		case 2:
			error = "Error semántico en la línea "+nLinea+", el return debe estar dentro de una funcion.";
			break;
		case 3:
			error = "Error semántico en la línea "+nLinea+", el break debe estar dentro de un bucle.";
			break;
		}
		escribirFichero(ubicErrores, error);
		throw new Exception(error);
	}
	
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
