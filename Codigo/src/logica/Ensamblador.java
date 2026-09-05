package logica;

import modelo.Instruccion;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;

public class Ensamblador {

    public List<Instruccion> leerArchivo(File archivoEnsamblador) {
        List<Instruccion> instrucciones = new ArrayList<>();
        
        try {
            Scanner lector = new Scanner(archivoEnsamblador);
            while(lector.hasNextLine()) {
                String linea = lector.nextLine();
                String[] lineasCodigo = linea.split(" ");
                String opcode = lineasCodigo[0];
                String registro = lineasCodigo[1].replace(",", "");
                int valor = 0;
                
                if (lineasCodigo.length > 2) {
                    valor = Integer.parseInt(lineasCodigo[2]);
                }
                
                Instruccion instruccionEnsamblador = new Instruccion(opcode, registro, valor);
                instrucciones.add(instruccionEnsamblador);
            }
        } catch (Exception e){
            System.out.println("Error leyendo el archivo: " + e.getMessage());
        }
        
        return instrucciones;
    }
}
