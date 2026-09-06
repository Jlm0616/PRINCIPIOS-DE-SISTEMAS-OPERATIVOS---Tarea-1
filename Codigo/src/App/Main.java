package App;

import modelo.CPU;
import modelo.Memoria;
import logica.Ensamblador;
import logica.CargarMemoria;
import logica.EjecutorCPU;
import modelo.Instruccion;

import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Memoria memoria = new Memoria(256, 64);
        CPU cpu = new CPU(64);
        Ensamblador ensamblador = new Ensamblador();
        
        File archivo = new File("C:/Users/julia/Downloads/ejemplo.asm");
        List<Instruccion> codigo = ensamblador.leerArchivo(archivo);
        
        CargarMemoria cargador = new CargarMemoria();
        cargador.cargar(codigo, memoria);
        
        EjecutorCPU ejecutor = new EjecutorCPU(cpu, memoria);
        ejecutor.ejecutarPrograma(codigo.size());
        
        System.out.println("AC final: " + cpu.getAC());
        System.out.println("AX final: " + cpu.getAX());
        System.out.println("BX final: " + cpu.getBX());
        System.out.println("CX final: " + cpu.getCX());
        System.out.println("DX final: " + cpu.getDX());
        
        System.out.println("Estado BCP: " + ejecutor.getBcp().getEstado());
        System.out.println("PC guardado en BCP: " + ejecutor.getBcp().getPc());
        System.out.println("AC guardado en BCP: " + ejecutor.getBcp().getAc());
    } 
}
