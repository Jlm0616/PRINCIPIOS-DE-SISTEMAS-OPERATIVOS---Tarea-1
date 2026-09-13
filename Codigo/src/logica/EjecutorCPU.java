package logica;

import modelo.CPU;
import modelo.Memoria;
import modelo.BCP;

/**
 * Ejecutor del ciclo de instrucción de la CPU (fetch-decode-execute).
 *
 * Se encarga de:
 *   1. Leer dos posiciones de memoria a partir del PC (primer y segundo byte).
 *   2. Decodificar el primer byte en opcode + registro (4 bits cada uno).
 *   3. Decodificar el segundo byte como valor numérico.
 *   4. Ejecutar la operación sobre los registros de la CPU.
 *   5. Avanzar el PC en 2 posiciones.
 *   6. Sincronizar el estado de la CPU con el BCP.
 *
 * Formato del primer byte (8 bits):
 *   [ bits 0-3 ] código de opcode
 *   [ bits 4-7 ] código de registro
 *
 * El segundo byte contiene el operando (valor o dirección) en binario.
 */
public class EjecutorCPU {

    private CPU cpu;              // CPU sobre la que se ejecutan las instrucciones
    private Memoria memoria;      // memoria de donde se leen las instrucciones
    private BCP bcp;              // bloque de control del proceso en ejecución
    private Traductor traductor;  // decodifica bytes a opcode/registro/valor

    /**
     * Crea un ejecutor asociado a una CPU y una memoria.
     *
     * Inicializa un BCP propio con id 1 y un Traductor por defecto.
     *
     * @param cpu     CPU sobre la que se ejecutará
     * @param memoria memoria desde la que se leerán las instrucciones
     */
    public EjecutorCPU(CPU cpu, Memoria memoria) {
        this.cpu = cpu;
        this.memoria = memoria;
        this.bcp = new BCP(1);
        this.traductor = new Traductor();
    }

    /**
     * Ejecuta una instrucción completa (fetch-decode-execute).
     *
     * Pasos:
     *   1. Lee los bytes en PC y PC+1 y los concatena como IR (binario).
     *   2. Del primer byte extrae opcode (bits 0-3) y registro (bits 4-7).
     *   3. Del segundo byte extrae el valor numérico.
     *   4. Ejecuta la operación correspondiente.
     *   5. Avanza el PC en 2 posiciones.
     *   6. Copia el estado de la CPU al BCP y lo marca como "EJECUTANDO".
     */
    public void ejecutarInstruccion() {
        int pc = cpu.getPC();

        String primerByte  = memoria.leer(pc);
        String segundoByte = memoria.leer(pc + 1);

        // Se guarda la instrucción completa (16 bits) en IR
        String instruccionCompleta = primerByte + segundoByte;
        cpu.setIR(Integer.parseInt(instruccionCompleta, 2));

        // Decodificación: primer byte = opcode (4 bits) + registro (4 bits)
        String codigoOpcode   = primerByte.substring(0, 4);
        String codigoRegistro = primerByte.substring(4, 8);
        int valor = traductor.decodificarValor(segundoByte);

        String opcode   = traductor.decodificarOpcode(codigoOpcode);
        String registro = traductor.decodificarRegistro(codigoRegistro);

        ejecutarOperacion(opcode, registro, valor);

        cpu.setPC(pc + 2);   // cada instrucción ocupa 2 posiciones

        /* -------- Sincronización con el BCP -------- */
        bcp.setEstado("EJECUTANDO");
        bcp.setPc(cpu.getPC());
        bcp.setAc(cpu.getAC());
        bcp.setAx(cpu.getAX());
        bcp.setBx(cpu.getBX());
        bcp.setCx(cpu.getCX());
        bcp.setDx(cpu.getDX());
    }

    /**
     * Ejecuta una secuencia de instrucciones consecutivas.
     *
     * Al terminar, marca el BCP como "TERMINADO".
     *
     * @param cantidadInstrucciones número de instrucciones a ejecutar
     */
    public void ejecutarPrograma(int cantidadInstrucciones) {
        for (int i = 0; i < cantidadInstrucciones; i++) {
            ejecutarInstruccion();
        }

        bcp.setEstado("TERMINADO");
    }

    /**
     * Ejecuta la operación indicada por el opcode sobre el registro y valor dados.
     *
     * Operaciones soportadas:
     *   MOV   -> registro = valor
     *   LOAD  -> AC = registro
     *   STORE -> registro = AC
     *   ADD   -> AC = AC + registro
     *   SUB   -> AC = AC - registro
     *
     * Si el opcode no coincide con ninguna operación, no hace nada.
     *
     * @param opcode   nombre de la operación
     * @param registro registro sobre el que actúa
     * @param valor    operando numérico (usado solo por MOV)
     */
    private void ejecutarOperacion(String opcode, String registro, int valor) {
        switch (opcode) {
            case "MOV":
                escribirRegistro(registro, valor);
                break;
            case "LOAD":
                cpu.setAC(leerRegistro(registro));
                break;
            case "STORE":
                escribirRegistro(registro, cpu.getAC());
                break;
            case "ADD":
                cpu.setAC(cpu.getAC() + leerRegistro(registro));
                break;
            case "SUB":
                cpu.setAC(cpu.getAC() - leerRegistro(registro));
                break;
        }
    }

    /**
     * Lee el valor de un registro de propósito general de la CPU.
     *
     * @param registro nombre del registro ("AX", "BX", "CX", "DX")
     * @return el valor del registro, o 0 si el nombre no coincide
     */
    private int leerRegistro(String registro) {
        switch (registro) {
            case "AX": return cpu.getAX();
            case "BX": return cpu.getBX();
            case "CX": return cpu.getCX();
            case "DX": return cpu.getDX();
        }
        return 0;
    }

    /**
     * Escribe un valor en un registro de propósito general de la CPU.
     *
     * Si el nombre del registro no coincide con ninguno válido, no hace nada.
     *
     * @param registro nombre del registro ("AX", "BX", "CX", "DX")
     * @param valor    valor a escribir
     */
    private void escribirRegistro(String registro, int valor) {
        switch (registro) {
            case "AX": cpu.setAX(valor); break;
            case "BX": cpu.setBX(valor); break;
            case "CX": cpu.setCX(valor); break;
            case "DX": cpu.setDX(valor); break;
        }
    }

    /** @return el BCP asociado a este ejecutor. */
    public BCP getBcp() {
        return bcp;
    }
}