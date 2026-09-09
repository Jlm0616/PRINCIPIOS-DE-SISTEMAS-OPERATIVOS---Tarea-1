package logica;

import modelo.CPU;
import modelo.Memoria;
import modelo.BCP;

public class EjecutorCPU {
    private CPU cpu;
    private Memoria memoria;
    private BCP bcp;
    private Traductor traductor;
    
    public EjecutorCPU(CPU cpu, Memoria memoria) {
        this.cpu = cpu;
        this.memoria = memoria;
        this.bcp = new BCP(1);
        this.traductor = new Traductor();
    }
    
    public void ejecutarInstruccion() {
        int pc = cpu.getPC();
        
        String primerByte = memoria.leer(pc);
        String segundoByte = memoria.leer(pc + 1);

        String instruccionCompleta = primerByte + segundoByte;
        cpu.setIR(Integer.parseInt(instruccionCompleta, 2));
        
        String codigoOpcode = primerByte.substring(0, 4);
        String codigoRegistro = primerByte.substring(4, 8);
        int valor = traductor.decodificarValor(segundoByte);
        
        String opcode = traductor.decodificarOpcode(codigoOpcode);
        String registro = traductor.decodificarRegistro(codigoRegistro);
        
        ejecutarOperacion(opcode, registro, valor);
        
        cpu.setPC(pc + 2);
        
        /*Funcionamiento del BCP*/
        bcp.setEstado("EJECUTANDO");
        bcp.setPc(cpu.getPC());
        bcp.setAc(cpu.getAC());
        bcp.setAx(cpu.getAX());
        bcp.setBx(cpu.getBX());
        bcp.setCx(cpu.getCX());
        bcp.setDx(cpu.getDX());
    }
    
    public void ejecutarPrograma(int cantidadInstrucciones) {
        for (int i = 0; i < cantidadInstrucciones; i++) {
            ejecutarInstruccion();
        }
        
        bcp.setEstado("TERMINADO");
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
    
    public BCP getBcp() {
        return bcp;
    }
}