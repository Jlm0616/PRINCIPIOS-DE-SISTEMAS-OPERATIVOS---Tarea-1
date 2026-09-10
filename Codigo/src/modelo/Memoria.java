package modelo;

public class Memoria {
    private int tamanoMemoria;
    private int limiteKernelUsuario;
    private String[] arregloMemoria;
    public static final int TAMANO_MINIMO = 128;
    
    public Memoria(int tamanoMemoria, int limiteKernelUsuario) {
        if (tamanoMemoria < TAMANO_MINIMO) {
            throw new IllegalArgumentException("Tamaño de memoria menor a " + TAMANO_MINIMO);
        }
        this.tamanoMemoria = tamanoMemoria;
        this.limiteKernelUsuario = limiteKernelUsuario;
        this.arregloMemoria = new String[tamanoMemoria];
    }

    public void escribir(int posicionMemoria, String valorMemoria) {
        arregloMemoria[posicionMemoria] = valorMemoria;
    }

    public String leer(int posicionMemoria) {
        return arregloMemoria[posicionMemoria];
    }

    public boolean esZonaKernel(int posicion) {
        return posicion < limiteKernelUsuario;
    }

    public boolean cabeProgramaDeUsuario(int cantidadPosiciones) {
        return cantidadPosiciones <= getEspacioUsuarioDisponible();
    }
    
    /*GETTERS*/
    public int getTamanoMemoria() {
        return tamanoMemoria;
    }
    
    public int getLimiteKernelUsuario() {
        return limiteKernelUsuario;
    }
    
    public String[] getArregloMemoria() {
        return arregloMemoria;
    }
    
    public int getEspacioUsuarioDisponible() {
        return tamanoMemoria - limiteKernelUsuario;
    }

}