package modelo;

public class CPU {
    private int PC;
    private int IR;
    private int AC;
    private int AX;
    private int BX;
    private int CX;
    private int DX;
    
    public CPU(int limiteKernelUsuario) {
        this.PC = limiteKernelUsuario;
        this.IR = 0;
        this.AC = 0;
        this.AX = 0;
        this.BX = 0;
        this.CX = 0;
        this.DX = 0;
    }
    
    /*GETTERS*/
    public int getPC() {
        return PC;
    }

    public int getIR() {
        return IR;
    }

    public int getAC() {
        return AC;
    }

    public int getAX() {
        return AX;
    }

    public int getBX() {
        return BX;
    }

    public int getCX() {
        return CX;
    }

    public int getDX() {
        return DX;
    }

    /*SETTERS*/
    public void setPC(int PC) {
        this.PC = PC;
    }

    public void setIR(int IR) {
        this.IR = IR;
    }

    public void setAC(int AC) {
        this.AC = AC;
    }

    public void setAX(int AX) {
        this.AX = AX;
    }

    public void setBX(int BX) {
        this.BX = BX;
    }

    public void setCX(int CX) {
        this.CX = CX;
    }

    public void setDX(int DX) {
        this.DX = DX;
    }
}
