package logica;

import modelo.Memoria;
import modelo.Instruccion;
import java.util.List;


public class CargarMemoria {
    private Traductor traductor;
    
    public CargarMemoria() {
        traductor = new Traductor();
    }
    
    public void cargar(List<Instruccion> listaInstrucciones, Memoria memoria) {
        int posicionActual = memoria.getLimiteKernelUsuario();
        
        for (Instruccion instruccionActual: listaInstrucciones) { 
            String primerByte = traductor.primerByte(instruccionActual);
            String segundoByte = traductor.valorEnsamblador(instruccionActual.getValor());
            
            memoria.escribir(posicionActual, primerByte);
            posicionActual++;
            memoria.escribir(posicionActual, segundoByte);
            posicionActual++;
        }
    }
    
}
