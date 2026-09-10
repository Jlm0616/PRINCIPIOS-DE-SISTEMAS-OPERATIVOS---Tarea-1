package modelo;

public class BCP {
    private int id;
    private String estado;
    private int pc;
    private int ac;
    private int ax;
    private int bx;
    private int cx;
    private int dx;
    
    public BCP(int id) { 
        this.id = id;
        this.estado = "NUEVO";
        this.pc = 0;
        this.ac = 0;
        this.ax = 0;
        this.bx = 0;
        this.cx = 0;
        this.dx = 0;
        
    }

    /*GETTERS*/
    public int getId() {
        return id;
    }

    public String getEstado() {
        return estado;
    }

    public int getPc() {
        return pc;
    }

    public int getAc() {
        return ac;
    }

    public int getAx() {
        return ax;
    }

    public int getBx() {
        return bx;
    }

    public int getCx() {
        return cx;
    }

    public int getDx() {
        return dx;
    }

    /*SETTERS*/
    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public void setAc(int ac) {
        this.ac = ac;
    }

    public void setAx(int ax) {
        this.ax = ax;
    }

    public void setBx(int bx) {
        this.bx = bx;
    }

    public void setCx(int cx) {
        this.cx = cx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }
}