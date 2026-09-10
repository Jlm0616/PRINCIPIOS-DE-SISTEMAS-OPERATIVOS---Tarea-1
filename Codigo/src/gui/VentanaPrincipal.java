package gui;

import modelo.Memoria;
import modelo.CPU;
import modelo.Instruccion;
import logica.Ensamblador;
import logica.EjecutorCPU;
import logica.CargarMemoria;
import logica.Traductor;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JOptionPane;
import javax.swing.JComponent;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.Box;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.FileDialog;

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
    private JButton btnCargar, btnModo, btnAccion, btnLimpiar, btnEstadisticas, btnAjustarMemoria;
    private JProgressBar progressBar;

    // NUEVO: referencia al panel de estadisticas para forzar repintado
    private JPanel panelEstadisticas;

    private int filaActual = -1;
    private boolean modoPasoAPaso = false;
    private int tamanoMemoriaActual = 256;
    private int limiteKernelActual = 64;


    private static final Color COLOR_BACKGROUND = new Color(236, 240, 241);
    private static final Color COLOR_PANEL = new Color(255, 255, 255);
    private static final Color COLOR_TEXT = new Color(44, 62, 80);
    private static final Color COLOR_ROW_ALT = new Color(245, 247, 250);
    private static final Color COLOR_SUCCESS = new Color(46, 204, 113);
    private static final Color COLOR_WARNING = new Color(230, 170, 20);
    private static final Color COLOR_PRIMARY = new Color(41, 128, 185);
    private static final Color COLOR_SECONDARY = new Color(52, 73, 94);
    private static final Color COLOR_DANGER = new Color(231, 76, 60);
    private static final Color COLOR_PURPLE = new Color(142, 68, 173);

    private static final Color GEMA_ESPACIO = new Color(52, 118, 168);
    private static final Color GEMA_MENTE = new Color(217, 172, 51);
    private static final Color GEMA_REALIDAD = new Color(178, 58, 58);
    private static final Color GEMA_PODER = new Color(122, 78, 145);
    private static final Color GEMA_TIEMPO = new Color(69, 145, 94);
    private static final Color GEMA_ALMA = new Color(204, 122, 61);
    private static final Color COLOR_DORADO = new Color(150, 113, 23);
    
    public VentanaPrincipal() {
        super("Tarea 1 - Mini PC - Simulador de CPU");
        aplicarLookAndFeel();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 720);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1400, 620));

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
        memoria = new Memoria(tamanoMemoriaActual, limiteKernelActual);
        cpu = new CPU(limiteKernelActual);
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

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 0, COLOR_PRIMARY),
                new EmptyBorder(8, 15, 8, 15)
        ));

        Dimension buttonSize = new Dimension(150, 38);

        btnCargar = crearBoton("Cargar archivo", GEMA_ESPACIO, buttonSize);
        btnModo = crearBoton("Modo: Automatico", GEMA_PODER, buttonSize);
        btnAccion = crearBoton("Ejecutar", GEMA_TIEMPO, buttonSize);
        btnLimpiar = crearBoton("Limpiar", GEMA_REALIDAD, buttonSize);
        btnEstadisticas = crearBoton("Estadisticas", GEMA_MENTE, buttonSize);
        btnAjustarMemoria = crearBoton("Ajustar memoria", GEMA_ALMA, buttonSize);

        panel.add(btnCargar);
        panel.add(btnModo);
        panel.add(btnAccion);
        panel.add(btnLimpiar);
        panel.add(btnEstadisticas);
        panel.add(btnAjustarMemoria);

        btnCargar.addActionListener(e -> cargarArchivo());
        btnModo.addActionListener(e -> alternarModo());
        btnAccion.addActionListener(e -> ejecutarAccion());
        btnLimpiar.addActionListener(e -> limpiar());
        btnEstadisticas.addActionListener(e -> mostrarEstadisticas());
        btnAjustarMemoria.addActionListener(e -> abrirVentanaConfiguracion());

        return panel;
    }

    private void alternarModo() {
        modoPasoAPaso = !modoPasoAPaso;
        if (modoPasoAPaso) {
            btnModo.setText("Modo: Paso a paso");
            btnAccion.setText("Siguiente");
        } else {
            btnModo.setText("Modo: Automatico");
            btnAccion.setText("Ejecutar");
        }
    }

    private void ejecutarAccion() {
        if (modoPasoAPaso) {
            ejecutarUnPaso();
        } else {
            ejecutarProgramaCompleto();
        }
    }

    private void abrirVentanaConfiguracion() {
        if (instrucciones != null) {
            JOptionPane.showMessageDialog(this,
                    "No se puede cambiar la configuracion de memoria mientras hay un programa cargado.\n"
                    + "Presiona 'Limpiar' primero.",
                    "Memoria en uso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        VentanaConfiguracionMemoria dialogo = new VentanaConfiguracionMemoria(
                this, memoria.getTamanoMemoria(), memoria.getLimiteKernelUsuario());
        dialogo.setVisible(true);

        if (dialogo.isConfirmado()) {
            // NUEVO: guardar los valores configurados
            tamanoMemoriaActual = dialogo.getTamanoMemoria();
            limiteKernelActual = dialogo.getLimiteKernel();

            memoria = new Memoria(tamanoMemoriaActual, limiteKernelActual);
            cpu = new CPU(limiteKernelActual);
            ejecutor = new EjecutorCPU(cpu, memoria);
            instrucciones = null;
            instruccionesEjecutadas = 0;
            filaActual = -1;
            modeloInstrucciones.setRowCount(0);
            modeloMemoria.setRowCount(0);
            actualizarRegistros();
            progressBar.setValue(0);
            progressBar.setString("0%");
            actualizarMensajeEstado("Memoria configurada: " + tamanoMemoriaActual
                    + " posiciones, Kernel 0-" + (limiteKernelActual - 1)
                    + ", Usuario " + limiteKernelActual + "-" + (tamanoMemoriaActual - 1));
        }
    }

    private JButton crearBoton(String texto, Color color, Dimension size) {
        JButton boton = new JButton(texto);
        boton.setPreferredSize(size);

        double luminosidad = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue());
        Color colorTexto = luminosidad > 150 ? Color.BLACK : Color.WHITE;

        boton.setForeground(colorTexto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        boton.putClientProperty("Nimbus.Overrides", createNimbusOverride(color));
        boton.putClientProperty("Nimbus.Overrides.InheritDefaults", false);
        boton.setBackground(color);

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

    private UIManager.LookAndFeelInfo createNimbusOverride(Color color) {
        return null;
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
        tablaInstrucciones = crearTablaEstilizada(modeloInstrucciones, GEMA_ESPACIO);
        JScrollPane scrollInstrucciones = envolverConTitulo(tablaInstrucciones, "Programa cargado", GEMA_ESPACIO);
        scrollInstrucciones.setPreferredSize(new Dimension(400, 0));

        modeloMemoria = new DefaultTableModel(new Object[]{"Pos", "Valor"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaMemoria = crearTablaEstilizada(modeloMemoria, GEMA_REALIDAD);
        JScrollPane scrollMemoria = envolverConTitulo(tablaMemoria, "Memoria (zona usuario)", GEMA_REALIDAD);
        scrollMemoria.setPreferredSize(new Dimension(230, 0));

        JPanel panelRegistros = crearPanelRegistros();
        JScrollPane scrollRegistros = envolverConTitulo(panelRegistros, "CPU / BCP", GEMA_PODER);
        scrollRegistros.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // NUEVO: limitar el tamano del scroll de registros para que no invada el panel sur
        scrollRegistros.setPreferredSize(new Dimension(0, 400));
        scrollRegistros.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.42;
        panel.add(scrollInstrucciones, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.23;
        panel.add(scrollMemoria, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.35;
        panel.add(scrollRegistros, gbc);

        return panel;
    }

    private JTable crearTablaEstilizada(DefaultTableModel modelo, Color colorHeader) {
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(26);
        tabla.setFont(new Font("Consolas", Font.PLAIN, 13));
        tabla.setGridColor(new Color(230, 230, 230));
        tabla.setShowGrid(true);
        tabla.setIntercellSpacing(new Dimension(1, 1));
        tabla.setSelectionBackground(colorHeader.brighter());
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setFillsViewportHeight(true);

        tabla.getTableHeader().setPreferredSize(new Dimension(0, 32));
        tabla.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                             boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setOpaque(true);
                label.setBackground(colorHeader);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                return label;
            }
        });

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
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 13),
                color
        ));
        return scroll;
    }

    private JPanel crearPanelRegistros() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.weightx = 1;

        Font labelFont = new Font("Consolas", Font.BOLD, 14);
        Font irFont = new Font("Consolas", Font.BOLD, 12);

        lblPC = crearLabelRegistro("PC:", "-", labelFont, GEMA_ESPACIO);
        lblIR = crearLabelRegistro("IR:", "-", irFont, GEMA_REALIDAD);
        lblAC = crearLabelRegistro("AC:", "-", labelFont, COLOR_SUCCESS);
        lblAX = crearLabelRegistro("AX:", "-", labelFont, COLOR_WARNING.darker());
        lblBX = crearLabelRegistro("BX:", "-", labelFont, new Color(52, 152, 219));
        lblCX = crearLabelRegistro("CX:", "-", labelFont, GEMA_PODER);
        lblDX = crearLabelRegistro("DX:", "-", labelFont, new Color(230, 126, 34));

        int y = 0;

        gbc.gridy = y++;
        gbc.gridx = 0; gbc.gridwidth = 1; panel.add(lblPC, gbc);
        gbc.gridx = 1; panel.add(lblAC, gbc);

        gbc.gridy = y++;
        gbc.gridx = 0; gbc.gridwidth = 2; panel.add(lblIR, gbc);

        gbc.gridy = y++;
        gbc.gridx = 0; gbc.gridwidth = 1; panel.add(lblAX, gbc);
        gbc.gridx = 1; panel.add(lblBX, gbc);

        gbc.gridy = y++;
        gbc.gridx = 0; panel.add(lblCX, gbc);
        gbc.gridx = 1; panel.add(lblDX, gbc);

        gbc.gridy = y++;
        gbc.gridx = 0; gbc.gridwidth = 2;
        lblEstado = new JLabel("Estado: -", SwingConstants.CENTER);
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblEstado.setForeground(COLOR_TEXT);
        lblEstado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GEMA_PODER, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        lblEstado.setOpaque(true);
        lblEstado.setBackground(new Color(245, 245, 245));
        panel.add(lblEstado, gbc);

        gbc.gridy = y;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(Box.createGlue(), gbc);

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
        panelEstadisticas = new JPanel(new BorderLayout(10, 0));
        panelEstadisticas.setBackground(COLOR_PANEL);
        panelEstadisticas.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, COLOR_SECONDARY),
                new EmptyBorder(10, 15, 10, 15)
        ));

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(COLOR_SUCCESS);
        progressBar.setBackground(new Color(236, 240, 241));
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        progressBar.setPreferredSize(new Dimension(0, 26));
        progressBar.setOpaque(true);

        lblInfo = new JLabel("Listo para cargar un programa...");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfo.setForeground(COLOR_TEXT);
        lblInfo.setOpaque(true);
        lblInfo.setBackground(COLOR_PANEL);

        panelEstadisticas.add(lblInfo, BorderLayout.WEST);
        panelEstadisticas.add(progressBar, BorderLayout.CENTER);

        return panelEstadisticas;
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

                if (!ensamblador.esArchivoValido(archivo)) {
                    JOptionPane.showMessageDialog(this,
                            "El archivo no es valido:\n" + ensamblador.getPrimerErrorEncontrado(),
                            "Archivo invalido", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Leer en variable LOCAL, no asignar a 'instrucciones' todavia
                List<Instruccion> nuevasInstrucciones = ensamblador.leerArchivo(archivo);

                if (nuevasInstrucciones.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "El archivo esta vacio o no se pudo leer.\nAsegurate de que tenga instrucciones validas.",
                            "Archivo vacio", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (!memoria.cabeProgramaDeUsuario(nuevasInstrucciones.size() * 2)) {
                    JOptionPane.showMessageDialog(this,
                            "El programa no cabe en la memoria disponible.\n"
                            + "Tamano del programa: " + (nuevasInstrucciones.size() * 2) + " bytes\n"
                            + "Memoria disponible: " + memoria.getEspacioUsuarioDisponible() + " bytes\n\n"
                            + "Usa 'Ajustar memoria' para ampliarla, o presiona 'Limpiar' si ya hay un programa cargado.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;   // 'instrucciones' sigue siendo null, todo consistente
                }

                // TODO VALIDADO: ahora si asignamos
                instrucciones = nuevasInstrucciones;

                CargarMemoria cargador = new CargarMemoria();
                cargador.cargar(instrucciones, memoria);

                instruccionesEjecutadas = 0;
                llenarTablaInstrucciones();
                llenarTablaMemoria();
                actualizarRegistros();
                actualizarBarraProgreso();
                actualizarMensajeEstado("Programa cargado: " + instrucciones.size()
                        + " instrucciones desde " + archivo.getName());
                btnAjustarMemoria.setEnabled(false);

            } catch (Exception e) {
                // Si algo falla, dejamos todo como estaba
                instrucciones = null;
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

    private void ejecutarUnPaso() {
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

        // Forzar repintado general
        revalidate();
        repaint();
    }

    private void ejecutarProgramaCompleto() {
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

        // Forzar repintado general al terminar
        revalidate();
        repaint();
    }

    private void actualizarRegistros() {
        lblPC.setText("PC: " + cpu.getPC());
        String irBinario = String.format("%16s", Integer.toBinaryString(cpu.getIR())).replace(' ', '0');
        lblIR.setText("IR: " + irBinario);
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
            lblEstado.setBackground(new Color(230, 170, 20, 60));
        } else {
            lblEstado.setBackground(new Color(245, 245, 245));
        }
    }

    private void actualizarBarraProgreso() {
        if (instrucciones != null && !instrucciones.isEmpty()) {
            int progreso = (instruccionesEjecutadas * 100) / instrucciones.size();
            progressBar.setValue(progreso);
            progressBar.setString(progreso + "%");
            if (panelEstadisticas != null) {
                panelEstadisticas.repaint();
            }
        }
    }

    private void actualizarMensajeEstado(String mensaje) {
        lblInfo.setText(mensaje);
        if (panelEstadisticas != null) {
            panelEstadisticas.repaint();
        }
    }

    private void limpiar() {
        memoria = new Memoria(tamanoMemoriaActual, limiteKernelActual);
        cpu = new CPU(limiteKernelActual);
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
        actualizarMensajeEstado("Sistema limpiado (memoria: " + tamanoMemoriaActual
                + " posiciones, kernel 0-" + (limiteKernelActual - 1) + ")");
        btnAjustarMemoria.setEnabled(true);
        modoPasoAPaso = false;
        btnModo.setText("Modo: Automatico");
        btnAccion.setText("Ejecutar");

        revalidate();
        repaint();
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