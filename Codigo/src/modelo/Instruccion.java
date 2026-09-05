package modelo;

public class Instruccion {
    private String opcode;
    private String registro;
    private int valor;
    
    public Instruccion(String opcode, String registro, int valor) {
        this.opcode = opcode;
        this.registro = registro;
        this.valor = valor;
    }
    /*GETTERS*/
    public String getOpcode() {
        return opcode;
    }
    public String getRegistro() {
        return registro;
    }
    public int getValor() {
        return valor;
    }
}