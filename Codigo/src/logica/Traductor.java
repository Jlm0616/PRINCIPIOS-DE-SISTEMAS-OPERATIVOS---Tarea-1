package logica;

import modelo.Instruccion;
import java.util.Map;
import java.util.HashMap;

public class Traductor {
    private Map<String, String> mapOpcode;
    private Map<String, String> mapRegistro;
    
    public Traductor() {
        mapOpcode = new HashMap<>();
        mapOpcode.put("LOAD", "0001");
        mapOpcode.put("STORE", "0010");
        mapOpcode.put("MOV", "0011");
        mapOpcode.put("SUB", "0100");
        mapOpcode.put("ADD", "0101");

        mapRegistro = new HashMap<>();
        mapRegistro.put("AX", "0001");
        mapRegistro.put("BX", "0010");
        mapRegistro.put("CX", "0011");
        mapRegistro.put("DX", "0100");
    }
    
    public String valorEnsamblador(int valor) {
        String binario;
        String signo;
        if (valor < 0) {
            int valorAbsoluto = Math.abs(valor);
            signo = "1";
            binario = Integer.toBinaryString(valorAbsoluto);
        } 
        else {
            signo = "0";
            binario = Integer.toBinaryString(valor);
        }
        
        String relleno = String.format("%7s", binario).replace(' ', '0');
        relleno = signo + relleno;
        return relleno;
    }
    
    public String primerByte(Instruccion instruccion) {
        String codigoOpcode = mapOpcode.get(instruccion.getOpcode());
        String codigoRegistro = mapRegistro.get(instruccion.getRegistro());
        String codigo = codigoOpcode + codigoRegistro;
        
        return codigo;
    }
    
    public String decodificarOpcode(String codigo) {
        for (Map.Entry<String, String> entrada : mapOpcode.entrySet()) {
            if (entrada.getValue().equals(codigo)) {
                return entrada.getKey();
            }
        }
        return null;
    }

    public String decodificarRegistro(String codigo) {
        for (Map.Entry<String, String> entrada : mapRegistro.entrySet()) {
            if (entrada.getValue().equals(codigo)) {
                return entrada.getKey();
            }
        }
        return null;
    }
    
    public int decodificarValor(String byteValor) {
        char signo = byteValor.charAt(0);
        String magnitudBinaria = byteValor.substring(1, 8);
        int magnitud = Integer.parseInt(magnitudBinaria, 2);
        return signo == '1' ? -magnitud : magnitud;
    }
}
