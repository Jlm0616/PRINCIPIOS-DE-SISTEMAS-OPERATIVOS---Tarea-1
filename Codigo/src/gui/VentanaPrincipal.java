package gui;

import modelo.*;
import logica.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.List;

public class VentanaPrincipal extends JFrame {

    private Memoria memoria;
    private CPU cpu;
    private EjecutorCPU ejecutor;
    private List<Instruccion> instrucciones;
    private int instruccionesEjecutadas = 0;

    private JTable tablaInstrucciones;
    private DefaultTableModel modeloInstrucciones;
    private JTable tablaMemoria;
    private DefaultTableModel modeloMemoria;

    private JLabel lblPC, lblIR, lblAC, lblAX, lblBX, lblCX, lblDX, lblEstado;
    private JLabel lblInfo;
    private JButton btnCargar, btnEjecutar, btnPaso, btnLimpiar, btnEstadisticas;
    private JProgressBar progressBar;
    
    private int filaActual = -1;

    // Paleta de colores
    private static final Color COLOR_PRIMARY = new Color(41, 128, 185);
    private static final Color COLOR_SECONDARY = new Color(52, 73, 94);
    private static final Color COLOR_SUCCESS = new Color(46, 204, 113);
    private static final Color COLOR_WARNING = new Color(241, 196, 15);
    private static final Color COLOR_DANGER = new Color(231, 76, 60);
    private static final Color COLOR_PURPLE = new Color(155, 89, 182);
    private static final Color COLOR_BACKGROUND = new Color(236, 240, 241);
    private static final Color COLOR_PANEL = new Color(255, 255, 255);
    private static final Color COLOR_TEXT = new Color(44, 62, 80);
    private static final Color COLOR_ROW_ALT = new Color(245, 247, 250);

    public VentanaPrincipal() {
        super("Tarea 1 - Mini PC | Simulador de CPU");
        aplicarLookAndFeel();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 720);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(950, 620));

        inicializarComponentes();
        inicializarSistema();
    }

    private void aplicarLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Si Nimbus no esta disponible, se usa el look and feel por defecto
        }
    }

    private void inicializarSistema() {
        memoria = new Memoria(256, 64);
        cpu = new CPU(64);
        ejecutor = new EjecutorCPU(cpu, memoria);
        actualizarRegistros();
    }

    private void inicializarComponentes() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(COLOR_BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(mainPanel);

        JPanel encabezado = crearPanelEncabezado();
        mainPanel.add(encabezado, BorderLayout.NORTH);
        mainPanel.add(crearPanelCentral(), BorderLayout.CENTER);
        mainPanel.add(crearPanelEstadisticas(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelEncabezado() {
        JPanel titulo = new JPanel(new BorderLayout());
        titulo.setBackground(COLOR_SECONDARY);
        titulo.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblTitulo = new JLabel("Mini PC - Simulador de Ejecucion de Instrucciones");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        titulo.add(lblTitulo, BorderLayout.WEST);

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.add(titulo, BorderLayout.NORTH);
        encabezado.add(crearPanelBotones(), BorderLayout.SOUTH);

        return encabezado;
    }

    private JPanel crearPanelTitulo() {
        JPanel contenedor = new JPanel(new BorderLayout(0, 10));
        contenedor.setOpaque(false);

        JPanel titulo = new JPanel(new BorderLayout());
        titulo.setBackground(COLOR_SECONDARY);
        titulo.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblTitulo = new JLabel("Mini PC — Simulador de Ejecucion de Instrucciones");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        titulo.add(lblTitulo, BorderLayout.WEST);
        contenedor.add(titulo, BorderLayout.NORTH);

        contenedor.add(crearPanelBotones(), BorderLayout.CENTER);

        JPanel envoltorio = new JPanel(new BorderLayout(15, 15));
        envoltorio.setBackground(COLOR_BACKGROUND);
        envoltorio.add(contenedor, BorderLayout.NORTH);
        envoltorio.add(crearPanelCentral(), BorderLayout.CENTER);
        envoltorio.add(crearPanelEstadisticas(), BorderLayout.SOUTH);

        return envoltorio;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 0, COLOR_PRIMARY),
                new EmptyBorder(8, 15, 8, 15)
        ));

        Dimension buttonSize = new Dimension(150, 38);

        btnCargar = crearBoton("Cargar archivo", COLOR_PRIMARY, buttonSize);
        btnEjecutar = crearBoton("Ejecutar", COLOR_SUCCESS, buttonSize);
        btnPaso = crearBoton("Paso a paso", COLOR_WARNING, buttonSize);
        btnLimpiar = crearBoton("Limpiar", COLOR_DANGER, buttonSize);
        btnEstadisticas = crearBoton("Estadisticas", COLOR_SECONDARY, buttonSize);

        panel.add(btnCargar);
        panel.add(btnEjecutar);
        panel.add(btnPaso);
        panel.add(btnLimpiar);
        panel.add(btnEstadisticas);

        btnCargar.addActionListener(e -> cargarArchivo());
        btnEjecutar.addActionListener(e -> ejecutarTodo());
        btnPaso.addActionListener(e -> ejecutarPaso());
        btnLimpiar.addActionListener(e -> limpiar());
        btnEstadisticas.addActionListener(e -> mostrarEstadisticas());

        return panel;
    }

    private JButton crearBoton(String texto, Color color, Dimension size) {
        JButton boton = new JButton(texto);
        boton.setPreferredSize(size);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setOpaque(true);
        boton.setBorderPainted(false);

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(color);
            }
        });

        return boton;
    }

    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(8, 0, 0, 0);

        modeloInstrucciones = new DefaultTableModel(new Object[]{"Instruccion", "Binario"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaInstrucciones = crearTablaEstilizada(modeloInstrucciones, COLOR_PRIMARY);
        JScrollPane scrollInstrucciones = envolverConTitulo(tablaInstrucciones, "Programa cargado", COLOR_PRIMARY);
        scrollInstrucciones.setPreferredSize(new Dimension(400, 0));

        modeloMemoria = new DefaultTableModel(new Object[]{"Pos", "Valor"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaMemoria = crearTablaEstilizada(modeloMemoria, COLOR_DANGER);
        JScrollPane scrollMemoria = envolverConTitulo(tablaMemoria, "Memoria (zona usuario)", COLOR_DANGER);
        scrollMemoria.setPreferredSize(new Dimension(230, 0));

        JPanel panelRegistros = crearPanelRegistros();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.42;
        panel.add(scrollInstrucciones, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.23;
        panel.add(scrollMemoria, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.35;
        panel.add(panelRegistros, gbc);

        return panel;
    }

    private JTable crearTablaEstilizada(DefaultTableModel modelo, Color colorHeader) {
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(26);
        tabla.setFont(new Font("Consolas", Font.PLAIN, 13));
        tabla.setGridColor(new Color(230, 230, 230));
        tabla.setShowGrid(true);
        tabla.setIntercellSpacing(new Dimension(1, 1));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(colorHeader);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 32));
        tabla.setSelectionBackground(colorHeader.brighter());
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setFillsViewportHeight(true);

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                             boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (table == tablaInstrucciones && row == filaActual) {
                    c.setBackground(COLOR_WARNING);
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : COLOR_ROW_ALT);
                    setFont(getFont().deriveFont(Font.PLAIN));
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        return tabla;
    }

    private JScrollPane envolverConTitulo(JComponent componente, String titulo, Color color) {
        JScrollPane scroll = new JScrollPane(componente);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(color, 2),
                titulo,
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 13),
                color
        ));
        return scroll;
    }

    private JPanel crearPanelRegistros() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_PURPLE, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1;

        JLabel titulo = new JLabel("CPU / BCP", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(COLOR_PURPLE);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(titulo, gbc);

        gbc.gridy = 1;
        JSeparator separador = new JSeparator();
        panel.add(separador, gbc);

        gbc.gridwidth = 1;
        Font labelFont = new Font("Consolas", Font.BOLD, 14);

        lblPC = crearLabelRegistro("PC:", "-", labelFont, COLOR_PRIMARY);
        lblIR = crearLabelRegistro("IR:", "-", labelFont, COLOR_DANGER);
        lblAC = crearLabelRegistro("AC:", "-", labelFont, COLOR_SUCCESS);
        lblAX = crearLabelRegistro("AX:", "-", labelFont, COLOR_WARNING.darker());
        lblBX = crearLabelRegistro("BX:", "-", labelFont, new Color(52, 152, 219));
        lblCX = crearLabelRegistro("CX:", "-", labelFont, COLOR_PURPLE);
        lblDX = crearLabelRegistro("DX:", "-", labelFont, new Color(230, 126, 34));

        int y = 2;
        gbc.gridy = y++;
        gbc.gridx = 0; panel.add(lblPC, gbc);
        gbc.gridx = 1; panel.add(lblIR, gbc);

        gbc.gridy = y++;
        gbc.gridx = 0; panel.add(lblAC, gbc);
        gbc.gridx = 1; panel.add(lblAX, gbc);

        gbc.gridy = y++;
        gbc.gridx = 0; panel.add(lblBX, gbc);
        gbc.gridx = 1; panel.add(lblCX, gbc);

        gbc.gridy = y++;
        gbc.gridx = 0; panel.add(lblDX, gbc);

        gbc.gridy = y++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        lblEstado = new JLabel("Estado: -", SwingConstants.CENTER);
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblEstado.setForeground(COLOR_TEXT);
        lblEstado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_PURPLE, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        lblEstado.setOpaque(true);
        lblEstado.setBackground(new Color(245, 245, 245));
        panel.add(lblEstado, gbc);

        return panel;
    }

    private JLabel crearLabelRegistro(String nombre, String valor, Font font, Color color) {
        JLabel label = new JLabel(nombre + " " + valor, SwingConstants.CENTER);
        label.setFont(font);
        label.setForeground(color);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        label.setOpaque(true);
        label.setBackground(new Color(248, 249, 250));
        return label;
    }

    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, COLOR_SECONDARY),
                new EmptyBorder(10, 15, 10, 15)
        ));

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(COLOR_SUCCESS);
        progressBar.setBackground(new Color(236, 240, 241));
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        progressBar.setPreferredSize(new Dimension(0, 26));

        lblInfo = new JLabel("Listo para cargar un programa...");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfo.setForeground(COLOR_TEXT);

        panel.add(lblInfo, BorderLayout.WEST);
        panel.add(progressBar, BorderLayout.CENTER);

        return panel;
    }

    private void cargarArchivo() {
        FileDialog fileDialog = new FileDialog(this, "Seleccionar archivo ASM", FileDialog.LOAD);
        fileDialog.setSize(900, 650);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        fileDialog.setLocation((screenSize.width - 900) / 2, (screenSize.height - 650) / 2);
        fileDialog.setFile("*.asm;*.ASM");
        fileDialog.setDirectory(System.getProperty("user.home"));
        fileDialog.setVisible(true);

        String directorio = fileDialog.getDirectory();
        String archivoSeleccionado = fileDialog.getFile();

        if (archivoSeleccionado != null && directorio != null) {
            File archivo = new File(directorio, archivoSeleccionado);

            String nombre = archivo.getName().toLowerCase();
            if (!nombre.endsWith(".asm")) {
                JOptionPane.showMessageDialog(this,
                        "Por favor selecciona un archivo .asm\n\nArchivo seleccionado: " + archivo.getName(),
                        "Formato incorrecto", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Ensamblador ensamblador = new Ensamblador();
                instrucciones = ensamblador.leerArchivo(archivo);

                if (instrucciones.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "El archivo esta vacio o no se pudo leer.\nAsegurate de que tenga instrucciones validas.",
                            "Archivo vacio", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (!memoria.cabeProgramaDeUsuario(instrucciones.size() * 2)) {
                    JOptionPane.showMessageDialog(this,
                            "El programa no cabe en la memoria disponible.\n"
                            + "Tamano del programa: " + (instrucciones.size() * 2) + " bytes\n"
                            + "Memoria disponible: " + memoria.getEspacioUsuarioDisponible() + " bytes",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                CargarMemoria cargador = new CargarMemoria();
                cargador.cargar(instrucciones, memoria);

                instruccionesEjecutadas = 0;
                llenarTablaInstrucciones();
                llenarTablaMemoria();
                actualizarRegistros();
                actualizarBarraProgreso();
                actualizarMensajeEstado("Programa cargado: " + instrucciones.size() + " instrucciones desde " + archivo.getName());

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error al leer el archivo:\n" + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void llenarTablaInstrucciones() {
        modeloInstrucciones.setRowCount(0);
        Traductor traductor = new Traductor();
        for (Instruccion instr : instrucciones) {
            String texto;
            if ("MOV".equals(instr.getOpcode())) {
                texto = instr.getOpcode() + " " + instr.getRegistro() + ", " + instr.getValor();
            } else {
                texto = instr.getOpcode() + " " + instr.getRegistro();
            }

            String primerByte = traductor.primerByte(instr);
            String segundoByte = traductor.valorEnsamblador(instr.getValor());
            String binario = primerByte.substring(0, 4) + " " + primerByte.substring(4, 8) + " " + segundoByte;

            modeloInstrucciones.addRow(new Object[]{texto, binario});
        }
    }

    private void llenarTablaMemoria() {
        modeloMemoria.setRowCount(0);
        int inicio = memoria.getLimiteKernelUsuario();
        int fin = inicio + instrucciones.size() * 2;
        for (int i = inicio; i < fin; i++) {
            modeloMemoria.addRow(new Object[]{i, memoria.leer(i)});
        }
    }

    private void ejecutarPaso() {
        if (instrucciones == null || instrucciones.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero carga un archivo .asm");
            return;
        }
        if (instruccionesEjecutadas >= instrucciones.size()) {
            JOptionPane.showMessageDialog(this, "El programa ya termino de ejecutarse.");
            return;
        }

        filaActual = instruccionesEjecutadas;
        tablaInstrucciones.scrollRectToVisible(tablaInstrucciones.getCellRect(filaActual, 0, true));

        ejecutor.ejecutarInstruccion();
        instruccionesEjecutadas++;

        if (instruccionesEjecutadas >= instrucciones.size()) {
            ejecutor.getBcp().setEstado("TERMINADO");
            filaActual = -1;
        }

        tablaInstrucciones.repaint();
        actualizarRegistros();
        actualizarBarraProgreso();

        if (instruccionesEjecutadas >= instrucciones.size()) {
            actualizarMensajeEstado("Programa completado!");
            progressBar.setForeground(COLOR_SUCCESS);
        } else {
            actualizarMensajeEstado("Ejecutando instruccion " + instruccionesEjecutadas + "/" + instrucciones.size());
        }
    }

    private void ejecutarTodo() {
        if (instrucciones == null || instrucciones.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero carga un archivo .asm");
            return;
        }
        actualizarMensajeEstado("Ejecutando programa...");
        while (instruccionesEjecutadas < instrucciones.size()) {
            ejecutor.ejecutarInstruccion();
            instruccionesEjecutadas++;
            actualizarBarraProgreso();
        }
        ejecutor.getBcp().setEstado("TERMINADO");
        actualizarRegistros();
        actualizarMensajeEstado("Programa completado!");
        progressBar.setForeground(COLOR_SUCCESS);
    }

    private void actualizarRegistros() {
        lblPC.setText("PC: " + cpu.getPC());
        lblIR.setText("IR: " + cpu.getIR());
        lblAC.setText("AC: " + cpu.getAC());
        lblAX.setText("AX: " + cpu.getAX());
        lblBX.setText("BX: " + cpu.getBX());
        lblCX.setText("CX: " + cpu.getCX());
        lblDX.setText("DX: " + cpu.getDX());
        lblEstado.setText("Estado: " + ejecutor.getBcp().getEstado());
        actualizarColorEstado();
    }

    private void actualizarColorEstado() {
        String estado = ejecutor.getBcp().getEstado();
        if ("TERMINADO".equals(estado)) {
            lblEstado.setBackground(new Color(46, 204, 113, 60));
        } else if ("EJECUTANDO".equals(estado)) {
            lblEstado.setBackground(new Color(241, 196, 15, 60));
        } else {
            lblEstado.setBackground(new Color(245, 245, 245));
        }
    }

    private void actualizarBarraProgreso() {
        if (instrucciones != null && !instrucciones.isEmpty()) {
            int progreso = (instruccionesEjecutadas * 100) / instrucciones.size();
            progressBar.setValue(progreso);
            progressBar.setString(progreso + "%");
        }
    }

    private void actualizarMensajeEstado(String mensaje) {
        lblInfo.setText(mensaje);
    }

    private void limpiar() {
        memoria = new Memoria(256, 64);
        cpu = new CPU(64);
        ejecutor = new EjecutorCPU(cpu, memoria);
        instrucciones = null;
        instruccionesEjecutadas = 0;
        filaActual = -1;
        modeloInstrucciones.setRowCount(0);
        modeloMemoria.setRowCount(0);
        actualizarRegistros();
        progressBar.setValue(0);
        progressBar.setString("0%");
        progressBar.setForeground(COLOR_SUCCESS);
        actualizarMensajeEstado("Sistema limpiado");
    }

    private void mostrarEstadisticas() {
        if (instrucciones == null) {
            JOptionPane.showMessageDialog(this, "No hay programa cargado.");
            return;
        }
        String mensaje = "Estadisticas del programa:\n\n"
                + "Total de instrucciones: " + instrucciones.size() + "\n"
                + "Ejecutadas: " + instruccionesEjecutadas + "\n"
                + "Restantes: " + (instrucciones.size() - instruccionesEjecutadas) + "\n"
                + "Estado: " + ejecutor.getBcp().getEstado();

        JOptionPane.showMessageDialog(this, mensaje, "Estadisticas", JOptionPane.INFORMATION_MESSAGE);
    }
}