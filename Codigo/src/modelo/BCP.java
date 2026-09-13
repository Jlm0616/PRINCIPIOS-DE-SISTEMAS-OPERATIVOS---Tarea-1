package modelo;

/**
 * Bloque de Control de Proceso (BCP).
 *
 * Almacena el estado completo de un proceso: su identificador, estado actual
 * y una copia de los registros de la CPU (PC, AC, AX, BX, CX, DX) en el momento
 * en que fue interrumpido o creado.
 *
 * El BCP es lo que permite al sistema operativo hacer cambio de contexto:
 * guardar el estado de un proceso y restaurarlo después.
 */
public class BCP {

    private int id;         // identificador único del proceso
    private String estado;  // estado actual: NUEVO, EJECUTANDO, TERMINADO
    private int pc;         // copia del contador de programa
    private int ac;         // copia del acumulador
    private int ax;         // copia del registro AX
    private int bx;         // copia del registro BX
    private int cx;         // copia del registro CX
    private int dx;         // copia del registro DX

    /**
     * Crea un BCP para un proceso nuevo.
     *
     * Inicializa el estado en "NUEVO" y todos los registros en 0.
     *
     * @param id identificador único del proceso
     */
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

    /* ==================== GETTERS ==================== */

    public int getId() { return id; }
    public String getEstado() { return estado; }
    public int getPc() { return pc; }
    public int getAc() { return ac; }
    public int getAx() { return ax; }
    public int getBx() { return bx; }
    public int getCx() { return cx; }
    public int getDx() { return dx; }

    /* ==================== SETTERS ==================== */

    /**
     * Cambia el estado del proceso.
     *
     * @param estado nuevo estado (usar valores del conjunto definido en la clase)
     */
    public void setEstado(String estado) { this.estado = estado; }

    public void setPc(int pc) { this.pc = pc; }
    public void setAc(int ac) { this.ac = ac; }
    public void setAx(int ax) { this.ax = ax; }
    public void setBx(int bx) { this.bx = bx; }
    public void setCx(int cx) { this.cx = cx; }
    public void setDx(int dx) { this.dx = dx; }
}