package logica;

import modelo.Instruccion;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

public class Ensamblador {

    private static final Set<String> OPCODES_VALIDOS = new HashSet<>(
            Arrays.asList("LOAD", "STORE", "MOV", "SUB", "ADD"));

    private static final Set<String> REGISTROS_VALIDOS = new HashSet<>(
            Arrays.asList("AX", "BX", "CX", "DX"));

    private String primerErrorEncontrado;

    /**
     * Verifica que el archivo exista, tenga extension .asm, y que cada
     * linea de contenido tenga formato valido (opcode y registro conocidos,
     * y valor numerico cuando corresponde). Si una sola linea falla,
     * el archivo completo se considera invalido.
     */
    public boolean esArchivoValido(File archivo) {
        primerErrorEncontrado = null;

        if (archivo == null || !archivo.exists() || !archivo.canRead()) {
            primerErrorEncontrado = "El archivo no existe o no se puede leer.";
            return false;
        }

        if (!archivo.getName().toLowerCase().endsWith(".asm")) {
            primerErrorEncontrado = "El archivo debe tener extension .asm";
            return false;
        }

        int numeroLinea = 0;
        boolean tieneAlMenosUnaInstruccion = false;

        try (Scanner lector = new Scanner(archivo)) {
            while (lector.hasNextLine()) {
                numeroLinea++;
                String linea = lector.nextLine().trim();

                if (linea.isEmpty()) {
                    continue;
                }

                if (!esLineaValida(linea)) {
                    primerErrorEncontrado = "Linea " + numeroLinea + " invalida: \"" + linea + "\"";
                    return false;
                }

                tieneAlMenosUnaInstruccion = true;
            }
        } catch (Exception e) {
            primerErrorEncontrado = "No se pudo leer el archivo: " + e.getMessage();
            return false;
        }

        if (!tieneAlMenosUnaInstruccion) {
            primerErrorEncontrado = "El archivo no contiene ninguna instruccion.";
            return false;
        }

        return true;
    }

    private boolean esLineaValida(String linea) {
        String[] partes = linea.split(" ");

        if (partes.length != 2 && partes.length != 3) {
            return false;
        }

        String opcode = partes[0].toUpperCase();
        if (!OPCODES_VALIDOS.contains(opcode)) {
            return false;
        }

        String registro = partes[1].replace(",", "").toUpperCase();
        if (!REGISTROS_VALIDOS.contains(registro)) {
            return false;
        }

        if (partes.length == 3) {
            try {
                Integer.parseInt(partes[2]);
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return true;
    }

    public String getPrimerErrorEncontrado() {
        return primerErrorEncontrado;
    }

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