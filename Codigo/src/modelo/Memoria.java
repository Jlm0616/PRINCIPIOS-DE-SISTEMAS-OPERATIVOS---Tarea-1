package modelo;

public class Memoria {
    private int tamanoMemoria;
    private int limiteKernelUsuario;
    private String[] arregloMemoria;
    
    public Memoria(int tamanoMemoria, int limiteKernelUsuario) {
        if (tamanoMemoria < 128) {
            throw new IllegalArgumentException("Tamaño de memoria menor a 128");
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
    
    public int getEspacioUsuarioDisponible() {
        return tamanoMemoria - limiteKernelUsuario;
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
}