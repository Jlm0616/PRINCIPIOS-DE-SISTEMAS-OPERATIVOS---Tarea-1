package logica;

import modelo.CPU;
import modelo.Memoria;

public class EjecutorCPU {
    private CPU cpu;
    private Memoria memoria;
    private Traductor traductor;
    
    public EjecutorCPU(CPU cpu, Memoria memoria) {
        this.cpu = cpu;
        this.memoria = memoria;
        this.traductor = new Traductor();
    }
    
    public void ejecutarInstruccion() {
        int pc = cpu.getPC();
        
        String primerByte = memoria.leer(pc);
        String segundoByte = memoria.leer(pc + 1);
        
        String codigoOpcode = primerByte.substring(0, 4);
        String codigoRegistro = primerByte.substring(4, 8);
        int valor = traductor.decodificarValor(segundoByte);
        
        String opcode = traductor.decodificarOpcode(codigoOpcode);
        String registro = traductor.decodificarRegistro(codigoRegistro);
        
        ejecutarOperacion(opcode, registro, valor);
        
        cpu.setPC(pc + 2);
    }
    
    public void ejecutarPrograma(int cantidadInstrucciones) {
        for (int i = 0; i < cantidadInstrucciones; i++) {
            ejecutarInstruccion();
        }
    }
    
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
    
    private int leerRegistro(String registro) {
        switch (registro) {
            case "AX": 
                return cpu.getAX();
            case "BX": 
                return cpu.getBX();
            case "CX": 
                return cpu.getCX();
            case "DX": 
                return cpu.getDX();
        }
        return 0;
    }
    
    private void escribirRegistro(String registro, int valor) {
        switch (registro) {
            case "AX": 
                cpu.setAX(valor); 
                break;
            case "BX": 
                cpu.setBX(valor); 
                break;
            case "CX": 
                cpu.setCX(valor); 
                break;
            case "DX": 
                cpu.setDX(valor); 
                break;
        }
    }
}