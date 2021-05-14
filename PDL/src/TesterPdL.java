import java.io.File;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class TesterPdL {
	public static void main(String[] args) {
		
		//Para elegir el fichero de las pruebas
		File archivo = null;
		JFileChooser selector=new JFileChooser();
        selector.setDialogTitle("Seleccione una prueba a realizar");
        
        //Filtro los tipos de archivos para que solo sean .txt
        FileNameExtensionFilter filtroTexto = new FileNameExtensionFilter("TEXTO","txt");
        selector.setFileFilter(filtroTexto);
        
        //Se abre el cuadro de diálogo
        int flag = selector.showOpenDialog(null);
        
        //Comprobacion de que pulse en aceptar
        if(flag == JFileChooser.APPROVE_OPTION){
            try {
                //Devuelve el fichero seleccionado
                archivo = selector.getSelectedFile();
            } catch (Exception e) {
            }
                  
        }
		
		AnalizadorSintactico AS = new AnalizadorSintactico(archivo);
		/*
		String ubicacion = "SemTablas6.txt";
		AnalizadorSintactico AS = new AnalizadorSintactico(ubicacion);*/
		try {
			AS.aSemantico();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
