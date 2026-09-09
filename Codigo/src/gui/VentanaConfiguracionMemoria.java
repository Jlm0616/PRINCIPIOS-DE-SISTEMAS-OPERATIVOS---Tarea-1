package gui;

import modelo.Memoria;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;

/**
 * Ventana modal cuya unica responsabilidad es pedir al usuario
 * el tamano de memoria y el limite Kernel/Usuario, validarlos,
 * y dejarlos disponibles mediante getters si el usuario confirma.
 *
 * No crea objetos Memoria ni CPU: esa decision le corresponde
 * a quien abre esta ventana (VentanaPrincipal).
 */
public class VentanaConfiguracionMemoria extends JDialog {

    private JTextField txtTamanoMemoria;
    private JTextField txtLimiteKernel;

    private int tamanoMemoria;
    private int limiteKernel;
    private boolean confirmado;

    public VentanaConfiguracionMemoria(Frame propietario, int tamanoActual, int limiteActual) {
        super(propietario, "Configurar memoria", true);
        this.confirmado = false;
        this.tamanoMemoria = tamanoActual;
        this.limiteKernel = limiteActual;

        construirInterfaz(tamanoActual, limiteActual);
        pack();
        setResizable(false);
        setLocationRelativeTo(propietario);
    }

    private void construirInterfaz(int tamanoActual, int limiteActual) {
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(new EmptyBorder(20, 20, 10, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        Font fontLabel = new Font("Segoe UI", Font.PLAIN, 13);

        JLabel lblInfo = new JLabel("Minimo permitido: " + Memoria.TAMANO_MINIMO + " posiciones");
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panelFormulario.add(lblInfo, gbc);
        gbc.gridwidth = 1;

        JLabel lblTamano = new JLabel("Tamano total de memoria:");
        lblTamano.setFont(fontLabel);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelFormulario.add(lblTamano, gbc);

        txtTamanoMemoria = new JTextField(String.valueOf(tamanoActual), 8);
        gbc.gridx = 1;
        panelFormulario.add(txtTamanoMemoria, gbc);

        JLabel lblLimite = new JLabel("Limite Kernel/Usuario:");
        lblLimite.setFont(fontLabel);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panelFormulario.add(lblLimite, gbc);

        txtLimiteKernel = new JTextField(String.valueOf(limiteActual), 8);
        gbc.gridx = 1;
        panelFormulario.add(txtLimiteKernel, gbc);

        JLabel lblAyuda = new JLabel("<html><body style='width: 260px'>"
                + "Posiciones 0 a (limite-1) = zona Kernel.<br>"
                + "Posiciones limite a (tamano-1) = zona Usuario."
                + "</body></html>");
        lblAyuda.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panelFormulario.add(lblAyuda, gbc);

        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        btnAceptar.addActionListener(e -> validarYConfirmar());
        btnCancelar.addActionListener(e -> {
            confirmado = false;
            dispose();
        });

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelBotones.setBorder(new EmptyBorder(0, 20, 15, 20));
        panelBotones.add(btnCancelar);
        panelBotones.add(btnAceptar);

        setLayout(new BorderLayout());
        add(panelFormulario, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void validarYConfirmar() {
        int tamanoIngresado;
        int limiteIngresado;

        try {
            tamanoIngresado = Integer.parseInt(txtTamanoMemoria.getText().trim());
            limiteIngresado = Integer.parseInt(txtLimiteKernel.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "El tamano de memoria y el limite del kernel deben ser numeros enteros.",
                    "Datos invalidos", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (tamanoIngresado < Memoria.TAMANO_MINIMO) {
            JOptionPane.showMessageDialog(this,
                    "El tamano de memoria debe ser al menos " + Memoria.TAMANO_MINIMO + ".",
                    "Datos invalidos", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int minimoKernel = (int) Math.ceil(tamanoIngresado * 0.20);
        if (limiteIngresado >= tamanoIngresado) {
            JOptionPane.showMessageDialog(this,
                    "El limite del Kernel no puede ser igual o mayor al tamano total,\n"
                    + "porque no quedaria espacio para la zona de Usuario.",
                    "Datos invalidos", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (limiteIngresado < minimoKernel) {
            JOptionPane.showMessageDialog(this,
                    "El limite del Kernel debe ser al menos el 20% de la memoria total.\n"
                    + "Para " + tamanoIngresado + " posiciones, el minimo es " + minimoKernel + ".",
                    "Datos invalidos", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        final int TAMANO_MAXIMO = 65536;  /*2 ** 16*/
        if (tamanoIngresado > TAMANO_MAXIMO) {
            JOptionPane.showMessageDialog(this,
                    "El tamano de memoria no puede superar " + TAMANO_MAXIMO + " posiciones\n"
                    + "(limite practico para este simulador educativo).",
                    "Datos invalidos", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (tamanoIngresado - limiteIngresado < 2) {
            JOptionPane.showMessageDialog(this,
                    "Debe quedar espacio para al menos 1 instruccion (2 bytes) en la zona de Usuario.",
                    "Datos invalidos", JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.tamanoMemoria = tamanoIngresado;
        this.limiteKernel = limiteIngresado;
        this.confirmado = true;
        dispose();
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public int getTamanoMemoria() {
        return tamanoMemoria;
    }

    public int getLimiteKernel() {
        return limiteKernel;
    }
}