// importamos las librerias graficas de swing y awt ya que nescesitaremos ventanas, tablas y eventos
// tambien importamos io para el manejo de archivos y util para las listas y regex

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Compilador extends JFrame {

    // --- Componentes de la Interfaz ---
    // declaramos los componentes de texto y las tablas donde mostraremos los resultados
    private TablaSimbolos ts; //  objeto de la clse tabla de simbolos
    private TablaErrores te; //  obejto de la clase tabla de errores
    private JTextPane txtEntrada; // panel en el que se escribe
    private JTextArea txtNumerosLineas;
    private StyledDocument doc;  
    private JTextArea txtSintactico; // area en la cual se escribe
    private JTable tablaSimbolos; // esta tabla es la encargada de mostrar todos los simbolos contenidos en la tabla de simbolos
    private JTable tablaErrores;  // tabla la cual se darä la informacion de la tabla errores para imprimirse muestra los errores

    // variable para guardar el archivo que estamos editando actualmente
    private File archivoActual = null;

    // guardamos la raiz del arbol sintactico para poder recorrerlo despues
    private NodoAST raizAST = null;

    private DefaultTableModel simbolos; // m
    private DefaultTableModel errores;
    private JLabel lblResumen;

    // estilos para el coloreado de sintaxis (highlighting)
    private Style normal;
    private Style reservada;
    private Style numero;
    private Style operador;
    private Style errorStyle;
    private Style verdeComentario;
    private Style estructuraDato;
    private Style cadenaStyle;

    // atributos para el control del timer de coloreado para que no se trabe la interfaz
    private Timer timerColoreo;
    private boolean coloreando = false;

    /*
    Definimos sets estaticos con las palabras reservadas y estructuras
    Usamos Set.of para busquedas rapidas al momento de colorear el texto
     */
    private static final Set<String> PALABRAS_RESERVADAS = Set.of(
            "CREAR", "IF", "ELSE", "MOSTRAR",
            "INSERTAR", "INSERTAR_FINAL", "INSERTAR_INICIO", "INSERTAR_EN_POSICION",
            "INSERTARIZQUIERDA", "INSERTARDERECHA", "AGREGARNODO",
            "APILAR", "ENCOLAR", "PUSH", "ENQUEUE",
            "ELIMINAR", "ELIMINAR_INICIO", "ELIMINAR_FINAL",
            "ELIMINAR_FRENTE", "ELIMINAR_POSICION", "ELIMINARNODO",
            "DESAPILAR", "POP", "DESENCOLAR", "DEQUEUE",
            "BUSCAR",
            "RECORRER", "RECORRERADELANTE", "RECORRERATRAS",
            "PREORDEN", "INORDEN", "POSTORDEN", "RECORRIDOPORNIVELES",
            "BFS", "DFS", "AGREGARARISTA", "ELIMINARARISTA", "CAMINOCORTO",
            "EN", "PESO", "ACTUALIZAR", "REHASH", "VACIA",
            "TOPE", "FRENTE", "FRONT", "PEEK", "VERFILA", "CLAVE",
            "TAMANO", "ALTURA", "HOJAS", "NODOS", "VECINOS", "LLENA",
            "NUMERO", "TEXTO", "FOR", "WHILE", "DO", "VER_FILA","INSERTAR_FRENTE"
    );

    private static final Set<String> ESTRUCTURAS_DATOS = Set.of(
            "PILA", "COLA", "BICOLA", "LISTA_ENLAZADA", "LISTA_CIRCULAR",
            "ARBOL_BINARIO", "TABLA_HASH", "GRAFO", "PILA_CIRCULAR"
    );

    /*
    En este constructor inicializamos toda la ventana
    Configuramos el tamaño, los paneles, el menu superior y los listeners de los botones
    Es donde se "arma" visualmente la aplicacion
     */
    public Compilador() {

        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JMenuBar barraMenu = new JMenuBar();

        // --- MENU ARCHIVO ---
        JMenu menuArchivo = new JMenu("Archivo");

        // creamos los items del menu y les asignamos atajos de teclado (shortcuts)
        JMenuItem itemAbrir = new JMenuItem("Abrir archivo (.txt)");
        itemAbrir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        itemAbrir.addActionListener(e -> abrirArchivo());

        // boton guardar
        JMenuItem itemGuardar = new JMenuItem("Guardar");
        itemGuardar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        itemGuardar.addActionListener(e -> guardarArchivo());

        
        // boton guardar como
        JMenuItem itemGuardarComo = new JMenuItem("Guardar Como...");
        itemGuardarComo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        itemGuardarComo.addActionListener(e -> guardarArchivoComo());

        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> System.exit(0));

        menuArchivo.add(itemAbrir);
        menuArchivo.add(itemGuardar);
        menuArchivo.add(itemGuardarComo);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);

     //   componente que te da una idea de como crear sentencias en DSL
        JMenu menuReferencias = new JMenu("Referencias");

        JMenuItem itemTabla = new JMenuItem("Tabla de Símbolos (Léxico)");
        itemTabla.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK));
        itemTabla.addActionListener(e -> VentanaReferencia.mostrarTablaSimbolos());

        JMenuItem itemGramatica = new JMenuItem("Gramática BNF (Sintáctico)");
        itemGramatica.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));
        itemGramatica.addActionListener(e -> VentanaReferencia.mostrarGramatica());

        menuReferencias.add(itemTabla);
        menuReferencias.addSeparator();
        menuReferencias.add(itemGramatica);

        this.ts = new TablaSimbolos();
        this.te = new TablaErrores();
        // --- MENU RUN ---
        JMenu menuRun = new JMenu("Run");

        JMenuItem itemRunLexico = new JMenuItem("Analizador Léxico (Solo Tokens)");
        itemRunLexico.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        itemRunLexico.addActionListener(e -> ejecutarAnalisisSoloLexico());

        JMenuItem itemRunSintactico = new JMenuItem("Analizador Sintáctico (Completo)");
        itemRunSintactico.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0));
        itemRunSintactico.addActionListener(e -> ejecutarAnalisisSintactico());

        menuRun.add(itemRunLexico);
        menuRun.add(itemRunSintactico);

        // agregamos los menus a la barra
        barraMenu.add(menuArchivo);
        barraMenu.add(menuReferencias);
        barraMenu.add(menuRun);

        setJMenuBar(barraMenu);

        // --- EDITOR DE CÓDIGO ---
        JPanel panelCodigo = new JPanel(new BorderLayout(5, 5));
        panelCodigo.setBorder(BorderFactory.createTitledBorder(" Editor de Código DSL "));

        txtEntrada = new JTextPane();
        txtEntrada.setFont(new Font("Consolas", Font.PLAIN, 14));
        // texto por defecto para probar rapido
        txtEntrada.setText("// Ejemplo de codigo \nCREAR PILA miPila;\nAPILAR 10 EN miPila;\n\nIF (TOPE EN miPila > 15) {\n    MOSTRAR \"Es mayor\";\n    ELIMINAR EN miPila;\n} ELSE {\n    MOSTRAR \"Es menor\";\n}");

        doc = txtEntrada.getStyledDocument();

        // configuramos el area de los numeros de linea
        txtNumerosLineas = new JTextArea("1");
        txtNumerosLineas.setFont(new Font("Consolas", Font.PLAIN, 14));
        txtNumerosLineas.setBackground(new Color(230, 230, 230));
        txtNumerosLineas.setForeground(Color.GRAY);
        txtNumerosLineas.setEditable(false);
        txtNumerosLineas.setMargin(new Insets(0, 5, 0, 5));

        JScrollPane scrollCodigo = new JScrollPane(txtEntrada);
        scrollCodigo.setRowHeaderView(txtNumerosLineas);
        scrollCodigo.setPreferredSize(new Dimension(1000, 200));

        JButton btnAnalizar = new JButton("Compilar / Ejecutar");
        btnAnalizar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAnalizar.setForeground(Color.WHITE);
        btnAnalizar.setBackground(new Color(0, 120, 215));
        btnAnalizar.setFocusPainted(false);
        btnAnalizar.setBorderPainted(false);
        btnAnalizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAnalizar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // efecto hover simple para el boton
        btnAnalizar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnAnalizar.setBackground(new Color(0, 90, 170));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnAnalizar.setBackground(new Color(0, 120, 215));
            }
        });

        panelCodigo.add(scrollCodigo, BorderLayout.CENTER);
        panelCodigo.add(btnAnalizar, BorderLayout.EAST);

        // --- PESTAÑAS DE RESULTADOS ---
        JTabbedPane pestañas = new JTabbedPane();

        // configuramos la tabla del diccionario de errores
        String[] colsDiccionario = {"Código", "Categoría", "Significado / Solución"};
        DefaultTableModel modeloDic = new DefaultTableModel(getDatosDiccionario(), colsDiccionario) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        
        JTable tablaDiccionario = new JTable(modeloDic);
        tablaDiccionario.setRowHeight(25);
        tablaDiccionario.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaDiccionario.getColumnModel().getColumn(1).setPreferredWidth(100);
        tablaDiccionario.getColumnModel().getColumn(2).setPreferredWidth(400);
        tablaDiccionario.setBackground(new Color(245, 245, 250));

        // configuramos la tabla de tokens (simbolos)
        String[] colsSimbolos = {"Lexema", "Línea", "Col", "Tipo Token"};
       simbolos = new DefaultTableModel(colsSimbolos, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tablaSimbolos = new JTable(simbolos);
        tablaSimbolos.setFillsViewportHeight(true);
        JScrollPane scrollSimbolos = new JScrollPane(tablaSimbolos);
        pestañas.addTab("Análisis Léxico (Tokens)", scrollSimbolos);

        // area para mostrar el arbol sintactico
        txtSintactico = new JTextArea();
        txtSintactico.setEditable(false);
        txtSintactico.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtSintactico.setForeground(new Color(40, 40, 40));
        JScrollPane scrollSintactico = new JScrollPane(txtSintactico);

        JPanel panelArbol = new JPanel(new BorderLayout());

        JButton btnImprimirArbol = new JButton("Imprimir Árbol en .txt");
        btnImprimirArbol.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnImprimirArbol.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
        btnImprimirArbol.addActionListener(e -> imprimirArbolSintactico());

        JPanel panelBotonArbol = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotonArbol.add(btnImprimirArbol);

        panelArbol.add(scrollSintactico, BorderLayout.CENTER);
        panelArbol.add(panelBotonArbol, BorderLayout.SOUTH);

        pestañas.addTab("Reporte Estructurado (Árbol)", panelArbol);

        JScrollPane scrollDiccionario = new JScrollPane(tablaDiccionario);
        pestañas.addTab("Glosario de Errores", scrollDiccionario);

        // configuramos la tabla de errores
        String[] colsErrores = {"Línea", "Tipo", "Descripción del Error"};
        errores = new DefaultTableModel(colsErrores, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tablaErrores = new JTable(errores);
        tablaErrores.setForeground(new Color(200, 0, 0));
        tablaErrores.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaErrores.setRowHeight(20);
        tablaErrores.getColumnModel().getColumn(0).setPreferredWidth(60);
        tablaErrores.getColumnModel().getColumn(0).setMaxWidth(80);
        tablaErrores.getColumnModel().getColumn(1).setPreferredWidth(100);
        tablaErrores.getColumnModel().getColumn(1).setMaxWidth(150);

        JScrollPane scrollErrores = new JScrollPane(tablaErrores);
        scrollErrores.setBorder(BorderFactory.createTitledBorder(" Consola de Problemas "));
        scrollErrores.setPreferredSize(new Dimension(1000, 180));

        JSplitPane splitCentral = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pestañas, scrollErrores);
        splitCentral.setResizeWeight(0.60);

        add(panelCodigo, BorderLayout.NORTH);
        add(splitCentral, BorderLayout.CENTER);

        lblResumen = new JLabel(" Listo para analizar.");
        lblResumen.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(lblResumen, BorderLayout.SOUTH);

        btnAnalizar.addActionListener(e -> Analisis());

        // llamamos a los metodos para iniciar estilos y el timer de coloreado
        inicializarEstilos();
        Coloreado();
        colorearTexto();
        actualizarNumerosDeLinea();
    }

    /*
    Este metodo auxiliar limpia todas las tablas y resetea las variables
    Es util para cuando vamos a hacer un nuevo analisis y no queremos datos viejos
     */
    private void limpiarTablas() {
        simbolos.setRowCount(0);
        errores.setRowCount(0);
        txtSintactico.setText("");
        raizAST = null;
        lblResumen.setForeground(Color.BLACK);
        lblResumen.setText("Analizando...");
    }

    /*
    Este metodo ejecuta SOLAMENTE la parte lexica
    Obtiene el texto, llama al motor lexico y llena la tabla de simbolos
    Si encuentra errores lexicos los reporta, pero no hace analisis sintactico
     */
    private void ejecutarAnalisisSoloLexico() {
        limpiarTablas();
        String codigo = txtEntrada.getText();

        MotorLexico.ResultadoLexico resultado = MotorLexico.ejecutar(codigo);

        // llenamos la tabla de simbolos con los resultados
        for (Object[] fila : resultado.datosSimbolos) {
         simbolos.addRow(fila);
        }

        // llenamos errores si los hubo
        for (MotorLexico.ErrorDatos err : resultado.errores) {
            errores.addRow(new Object[]{err.linea, "LÉXICO", err.descripcion});
        }

        if (resultado.errores.isEmpty()) {
            lblResumen.setText(" Análisis LÉXICO finalizado con ÉXITO.");
            lblResumen.setForeground(new Color(0, 128, 0));
            txtSintactico.setText("--- Modo Solo Léxico ---\nEl árbol no se genera en este modo.");
        } else {
            lblResumen.setText(" Se encontraron " + resultado.errores.size() + " errores léxicos.");
            lblResumen.setForeground(Color.RED);
        }
    }

    /*
    Este metodo es el completo, corre lexico y luego sintactico
    Primero verifica que los tokens esten bien y luego intenta armar el arbol
     */
    private void ejecutarAnalisisSintactico() {
        limpiarTablas();
        String codigo = txtEntrada.getText();

        // 1. ejecutamos lexico
        MotorLexico.ResultadoLexico resLexico = MotorLexico.ejecutar(codigo);

        // 2. ejecutamos sintactico con los tokens validos
        MotorSintactico.ResultadoSintactico resSintactico = MotorSintactico.ejecutar(resLexico.tokensValidos);

        this.raizAST = resSintactico.raiz;
        txtSintactico.setText(resSintactico.logArbol);
        txtSintactico.setCaretPosition(0);

        // llenamos la tabla de errores
        for (MotorSintactico.ErrorDatosSintactico err : resSintactico.errores) {
            errores.addRow(new Object[]{err.linea, "SINTÁCTICO", err.descripcion});
        }

        if (resSintactico.errores.isEmpty()) {
            lblResumen.setText(" Sintaxis Correcta (Errores léxicos ignorados).");
            lblResumen.setForeground(new Color(0, 128, 0));
        } else {
            lblResumen.setText(" Se encontraron " + resSintactico.errores.size() + " errores sintácticos.");
            lblResumen.setForeground(Color.RED);
        }
    }

    /*
    Metodo para abrir archivos usa JFileChooser
    Lee linea por linea y lo pone en el textpane, tambien actualiza el titulo de la ventana
     */
    private void abrirArchivo() {
        JFileChooser selector = new JFileChooser();
        selector.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos de texto (.txt)", "txt"));

        int resultado = selector.showOpenDialog(this);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            archivoActual = selector.getSelectedFile();
            try (BufferedReader lector = new BufferedReader(new FileReader(archivoActual))) {
                StringBuilder contenido = new StringBuilder();
                String linea;
                while ((linea = lector.readLine()) != null) {
                    contenido.append(linea).append("\n");
                }
                txtEntrada.setText(contenido.toString());
                colorearTexto();
                actualizarNumerosDeLinea();

                setTitle("Analizador DSL - " + archivoActual.getName());
                lblResumen.setText(" Archivo cargado: " + archivoActual.getName());
                lblResumen.setForeground(new Color(0, 100, 0));

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Guardar archivo simple, si ya tiene nombre sobreescribe
    private void guardarArchivo() {
        if (archivoActual != null) {
            escribirArchivo(archivoActual, txtEntrada.getText());
        } else {
            guardarArchivoComo();
        }
    }

    // Guardar como siempre pide el nombre del archivo
    private void guardarArchivoComo() {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar código como...");
        selector.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos de texto (.txt)", "txt"));

        int userSelection = selector.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File archivoAGuardar = selector.getSelectedFile();
            // aseguramos que tenga extension .txt
            if (!archivoAGuardar.getName().toLowerCase().endsWith(".txt")) {
                archivoAGuardar = new File(archivoAGuardar.getAbsolutePath() + ".txt");
            }

            escribirArchivo(archivoAGuardar, txtEntrada.getText());
            archivoActual = archivoAGuardar;
            setTitle("Analizador DSL - " + archivoActual.getName());
        }
    }

    /*
    Este metodo guarda el arbol que se muestra en el area de texto en un archivo
    Es util para tener un reporte del analisis
     */
    private void imprimirArbolSintactico() {
        String contenidoArbol = txtSintactico.getText();
        if (contenidoArbol.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El árbol está vacío. Primero ejecuta el análisis.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar Árbol Sintáctico");
        selector.setSelectedFile(new File("arbol_sintactico.txt"));
        selector.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos de texto (.txt)", "txt"));

        int userSelection = selector.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File archivoAGuardar = selector.getSelectedFile();
            if (!archivoAGuardar.getName().toLowerCase().endsWith(".txt")) {
                archivoAGuardar = new File(archivoAGuardar.getAbsolutePath() + ".txt");
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoAGuardar))) {
                writer.write("REPORTE DE DERIVACIÓN SINTÁCTICA (ÁRBOL)\n");
                writer.write("========================================\n\n");
                writer.write(contenidoArbol);
                JOptionPane.showMessageDialog(this, "Árbol guardado exitosamente en:\n" + archivoAGuardar.getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar el árbol: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Metodo generico para escribir en disco
    private void escribirArchivo(File archivo, String contenido) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            writer.write(contenido);
            lblResumen.setText(" Archivo guardado correctamente.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar el archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // actualiza los numeros de linea de la izquierda basandose en la cantidad de lineas del documento
    private void actualizarNumerosDeLinea() {
        int lineas = doc.getDefaultRootElement().getElementCount();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= lineas; i++) {
            sb.append(i).append(System.lineSeparator());
        }
        txtNumerosLineas.setText(sb.toString());
    }

    /*
    Aqui definimos los colores para cada tipo de token
    Se usan estilos de StyledDocument para aplicar colores y negritas
     */
    private void inicializarEstilos() {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        normal = sc.addStyle("normal", null);
        StyleConstants.setForeground(normal, Color.BLACK);

        reservada = sc.addStyle("reservada", null);
        StyleConstants.setForeground(reservada, new Color(0, 0, 180));
        StyleConstants.setBold(reservada, true);

        numero = sc.addStyle("numero", null);
        StyleConstants.setForeground(numero, new Color(150, 0, 150));

        operador = sc.addStyle("operador", null);
        StyleConstants.setForeground(operador, Color.DARK_GRAY);

        errorStyle = sc.addStyle("error", null);
        StyleConstants.setForeground(errorStyle, Color.RED);

        verdeComentario = sc.addStyle("comentario", null);
        StyleConstants.setForeground(verdeComentario, new Color(0, 128, 0));
        StyleConstants.setItalic(verdeComentario, true);

        estructuraDato = sc.addStyle("estructura", null);
        StyleConstants.setForeground(estructuraDato, new Color(255, 140, 0));
        StyleConstants.setBold(estructuraDato, true);

        cadenaStyle = sc.addStyle("cadena", null);
        StyleConstants.setForeground(cadenaStyle, new Color(200, 20, 20));
    }

    /*
    Este metodo recorre el texto y aplica los estilos usando expresiones regulares
    Primero pintamos todo de negro (normal) y luego vamos buscando patrones para pintar encima
     */
    private void colorearTexto() {
        if (coloreando) {
            return;
        }
        coloreando = true;

        SwingUtilities.invokeLater(() -> {
            try {
                String texto = txtEntrada.getText();
                doc.setCharacterAttributes(0, texto.length(), normal, true);

                Matcher m;

                // pintamos palabras reservadas y estructuras
                m = Pattern.compile("\\b[A-Za-z_][A-Za-z0-9_]*\\b").matcher(texto);
                while (m.find()) {
                    String palabra = m.group().toUpperCase();
                    if (ESTRUCTURAS_DATOS.contains(palabra)) {
                        doc.setCharacterAttributes(m.start(), m.end() - m.start(), estructuraDato, false);
                    } else if (PALABRAS_RESERVADAS.contains(palabra)) {
                        doc.setCharacterAttributes(m.start(), m.end() - m.start(), reservada, false);
                    }
                }

                // pintamos numeros
                m = Pattern.compile("\\b\\d+\\b").matcher(texto);
                while (m.find()) {
                    doc.setCharacterAttributes(m.start(), m.end() - m.start(), numero, false);
                }

                // pintamos operadores
                m = Pattern.compile("[=+\\-*/<>;(){},]").matcher(texto);
                while (m.find()) {
                    doc.setCharacterAttributes(m.start(), 1, operador, false);
                }

                // pintamos cadenas
                m = Pattern.compile("\"[^\"]*\"").matcher(texto);
                while (m.find()) {
                    doc.setCharacterAttributes(m.start(), m.end() - m.start(), cadenaStyle, false);
                }

                // y por ultimo comentarios
                m = Pattern.compile("//.*").matcher(texto);
                while (m.find()) {
                    doc.setCharacterAttributes(m.start(), m.end() - m.start(), verdeComentario, false);
                }

            } catch (Exception e) {

            } finally {
                coloreando = false;
            }
        });
    }

    // Inicializa el timer del coloreado para que se ejecute cuando el usuario deja de escribir
    private void Coloreado() {
        timerColoreo = new Timer(300, e -> colorearTexto());
        timerColoreo.setRepeats(false);

        txtEntrada.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                actualizarNumerosDeLinea();
                timerColoreo.restart();
            }

            public void removeUpdate(DocumentEvent e) {
                actualizarNumerosDeLinea();
                timerColoreo.restart();
            }

            public void changedUpdate(DocumentEvent e) {
            }
        });
    }

    /*
    METODO PRINCIPAL DE ANALISIS
    Aqui se orquesta todo el proceso:
    1. Se llama al tokenizador
    2. Se usa el automata para validar lexicamente
    3. Se llama al analizador sintactico
    4. Se llenan todas las tablas y reportes
     */
    private void Analisis() {
        String codigo = txtEntrada.getText();

        simbolos.setRowCount(0);
        errores.setRowCount(0);
        txtSintactico.setText("");
        raizAST = null;
        lblResumen.setForeground(Color.BLACK);
        lblResumen.setText("Analizando...");

        List<ErrorReporte> listaErroresUnificada = new ArrayList<>();

        try {

            // llamamos al tokenizador del core
            Token[] tokens = DSL.CrearToken(codigo);
            // construimos el automata con los datos del DSL
            Automata auto = new Automata(DSL.getEstadosAceptacion(), DSL.getAlfabetoDSL(), DSL.getTransiciones(), "INICIO", DSL.getEstadosAceptacion());

            Token[] resultadosLexicos = auto.aceptar(tokens);

            List<Token> tokensValidos = new ArrayList<>();

            // filtramos los tokens validos e invalidos
            for (Token t : resultadosLexicos) {

                if (t.getTipoToken().startsWith("ERROR") || !t.existeSimbolo()) {

                    String codigoError = "DSL(100)";
                    if (t.getTipoToken().contains("CADENA")) {
                        codigoError = "DSL(102)";
                    } else if (t.getTipoToken().contains("SIMBOLO")) {
                        codigoError = "DSL(101)";
                    } else if (t.getTipoToken().contains("MALFORMADO")) {
                        codigoError = "DSL(103)";
                    }

                    String desc = String.format("%s Lexema '%s' no válido. Causa: %s",
                            codigoError, t.getLexema(), t.getTipoToken());

                    listaErroresUnificada.add(new ErrorReporte(t.getLinea(), "LÉXICO", desc));
                } else {

                    tokensValidos.add(t);
                    simbolos.addRow(new Object[]{
                        t.getLexema(), t.getLinea(), t.getColumna(), t.getTipoToken()
                    });
                }
            }

            // si hay tokens validos pasamos al sintactico
            if (!tokensValidos.isEmpty()) {
                Token[] arrayTokensValidos = tokensValidos.toArray(new Token[0]);

                AnalizadorSintactico sintactico = new AnalizadorSintactico(arrayTokensValidos, ts, te);
                raizAST = sintactico.analizar();

                // mostramos el log del arbol
                List<String> log = sintactico.getArbolDerivacion();
                StringBuilder sb = new StringBuilder();
                for (String paso : log) {
                    sb.append(paso).append("\n");
                }
                txtSintactico.setText(sb.toString());
                txtSintactico.setCaretPosition(0);

                // recolectamos errores sintacticos
                List<String> erroresSin = sintactico.getErrores();
                for (String errStr : erroresSin) {
                    int linea = 0;
                    String descripcion = errStr;

                    // parseamos el string del error para sacar la linea si es posible
                    try {
                        if (errStr.contains("[Línea ")) {
                            int inicioNum = errStr.indexOf("[Línea ") + 7;
                            int finNum = errStr.indexOf("]");

                            if (inicioNum < finNum) {
                                String numStr = errStr.substring(inicioNum, finNum);
                                linea = Integer.parseInt(numStr.trim());
                            }

                            if (errStr.contains("]: ")) {
                                String idPart = errStr.substring(0, errStr.indexOf("["));
                                String msgPart = errStr.substring(errStr.indexOf("]: ") + 3);
                                descripcion = idPart + msgPart;
                            }
                        }
                    } catch (Exception ex) {
                        linea = 0;
                    }

                    listaErroresUnificada.add(new ErrorReporte(linea, "SINTÁCTICO", descripcion));
                }
            } else {
                txtSintactico.setText("No hay tokens válidos para analizar sintácticamente.");
            }

            // ordenamos los errores por linea para que salgan en orden
            Collections.sort(listaErroresUnificada);

            for (ErrorReporte err : listaErroresUnificada) {
                errores.addRow(new Object[]{
                    (err.linea > 0 ? err.linea : "-"),
                    err.tipo,
                    err.descripcion
                });
            }

            // resumen final en la etiqueta de abajo
            int totalErrores = listaErroresUnificada.size();
            if (totalErrores == 0) {
                lblResumen.setText("  Análisis Finalizado con ÉXITO. El código es correcto.");
                lblResumen.setForeground(new Color(0, 128, 0));

            } else {
                lblResumen.setText("  Se encontraron " + totalErrores + " errores.");
                lblResumen.setForeground(Color.RED);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error crítico en el compilador: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> new Compilador().setVisible(true));
    }

    // clase auxiliar para ordenar los errores antes de meterlos a la tabla
    private static class ErrorReporte implements Comparable<ErrorReporte> {

        int linea;
        String tipo;
        String descripcion;

        public ErrorReporte(int linea, String tipo, String descripcion) {
            this.linea = linea;
            this.tipo = tipo;
            this.descripcion = descripcion;
        }

        @Override
        public int compareTo(ErrorReporte o) {
            return Integer.compare(this.linea, o.linea);
        }
    }

    // datos fijos para el diccionario de errores comunes
    private Object[][] getDatosDiccionario() {
        return new Object[][]{
            {"DSL(101)", "Léxico", "Símbolo no reconocido en el alfabeto del lenguaje."},
            {"DSL(102)", "Léxico", "Cadena de texto (string) sin cerrar o mal formada."},
            {"DSL(103)", "Léxico", "Número o identificador mal formado (ej. 123abc)."},
            {"DSL(201)", "Sintáctico", "Falta el punto y coma (;) al final de la sentencia."},
            {"DSL(202)", "Sintáctico", "Falta un delimitador de bloque ( } ) o paréntesis ( ) )."},
            {"DSL(203)", "Sintáctico", "Sentencia no válida o palabra reservada mal posicionada."},
            {"DSL(204)", "Sintáctico", "Tipo de estructura de datos desconocido o no soportado."},
            {"DSL(205)", "Sintáctico", "Falta la palabra clave 'EN' necesaria para la operación."},
            {"DSL(206)", "Sintáctico", "Se esperaba un operador relacional (==, !=, <, >, etc)."},
            {"DSL(207)", "Sintáctico", "Expresión matemática o lógica mal formada."},
            {"DSL(208)", "Sintáctico", "Se esperaba un nombre de variable (IDENTIFICADOR)."},
            {"DSL(299)", "Sintáctico", "Final de archivo inesperado (instrucción incompleta)."},
            {"DSL(999)", "Sistema", "Error crítico irrecuperable en el proceso de análisis."}
        };
    }
}
