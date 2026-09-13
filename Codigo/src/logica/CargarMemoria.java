package logica;

import modelo.Memoria;
import modelo.Instruccion;
import java.util.List;

/**
 * Se encarga de cargar un programa (lista de instrucciones) en la memoria
 * de la máquina virtual.
 *
 * Cada instrucción se traduce a dos posiciones de memoria:
 *   - Primer byte:  código de operación (qué hace la instrucción)
 *   - Segundo byte: operando (valor o dirección sobre la que actúa)
 *
 * La carga comienza en el límite kernel/usuario, es decir, siempre se escribe
 * en la zona de usuario y nunca sobre la zona kernel.
 */
public class CargarMemoria {

    private Traductor traductor;   // traduce instrucciones a su representación en bytes

    public CargarMemoria() {
        traductor = new Traductor();
    }

    /**
     * Escribe las instrucciones en la memoria a partir del límite kernel/usuario.
     *
     * Cada instrucción ocupa DOS posiciones consecutivas:
     *   posicionActual     -> primer byte (código de operación)
     *   posicionActual + 1 -> segundo byte (operando)
     *
     * El contador avanza de a 2 por cada instrucción procesada.
     *
     * @param listaInstrucciones instrucciones a cargar, en orden de ejecución
     * @param memoria            memoria destino (debe tener espacio suficiente)
     */
    public void cargar(List<Instruccion> listaInstrucciones, Memoria memoria) {
        int posicionActual = memoria.getLimiteKernelUsuario();

        for (Instruccion instruccionActual : listaInstrucciones) {
            String primerByte   = traductor.primerByte(instruccionActual);
            String segundoByte  = traductor.valorEnsamblador(instruccionActual.getValor());

            memoria.escribir(posicionActual, primerByte);
            posicionActual++;
            memoria.escribir(posicionActual, segundoByte);
            posicionActual++;
        }
    }
}