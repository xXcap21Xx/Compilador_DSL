// Importamos las librerías necesarias de Java para manejar listas (List) y conjuntos de datos únicos (Set).
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/*
 ESTA CLASE ES EL "ANALIZADOR SINTÁCTICO" (PARSER).
 Recibe las palabras (tokens) que encontró el Analizador Léxico
 y verifica si el orden de esas palabras tiene sentido según las reglas del lenguaje.
 
 Además, construye un "Árbol de Sintaxis Abstracta" (AST), que es una representación jerárquica y ordenada del código.
 */
public class AnalizadorSintactico {

    // --- ATRIBUTOS (VARIABLES GLOBALES DE LA CLASE) ---

    // Aquí guardamos la lista de palabras (tokens) que vamos a analizar.
    private final Token[] tokens;
    
    // Este entero actúa como un "cursor" o "dedo apuntador". Nos dice en qué posición de la lista de tokens estamos.
    private int actual;
    
    // Una lista de textos para ir guardando paso a paso cómo se construye el árbol (útil para ver qué hizo el programa).
    private final List<String> logDerivacion;
    
    // Una lista para anotar todos los errores gramaticales que encontremos.
    private final List<String> errores;
    
    // Estas tablas son para guardar variables y errores de forma estructurada (para uso externo o visual).
    private TablaSimbolos tablaSimbolos; 
    private TablaErrores tablaErrores;

    // --- EXCEPCIÓN PERSONALIZADA ---
    /*
     Creamos nuestro propio tipo de error. Esto nos sirve para detener el análisis de golpe
     cuando encontramos algo que no tiene sentido y poder reportarlo limpiamente.
     */
    private static class ParserException extends RuntimeException {
        public ParserException(String message) {
            super(message);
        }
    }

    // --- CONSTRUCTOR ---
    /*
     Este es el método que "nace" cuando creamos una instancia del analizador.
     Inicializa todas las listas vacías y pone el cursor (actual) en 0 (el principio).
     */
    public AnalizadorSintactico(Token[] tokens, TablaSimbolos ts, TablaErrores te) {
        this.tokens = tokens;
        this.tablaSimbolos = ts;
        this.tablaErrores = te;
        this.actual = 0;
        this.logDerivacion = new ArrayList<>();
        this.errores = new ArrayList<>();
    }

    // --- GETTERS (MÉTODOS PARA OBTENER INFORMACIÓN) ---
    // Permiten que otras clases (como la interfaz gráfica) saquen el reporte del árbol y de los errores.
    public List<String> getArbolDerivacion() {
        return logDerivacion;
    }

    public List<String> getErrores() {
        return errores;
    }

    // --- MÉTODO PRINCIPAL: ANALIZAR ---
    /*
    Inicia todo el proceso.
     1. Limpia los registros anteriores.
     2. Crea el nodo "RAÍZ" del árbol.
     3. Llama a la regla "programa()" para empezar a leer el código.
     4. Si hay errores críticos, los atrapa (catch).
     5. Al final (finally), siempre dibuja el árbol visualmente.
     */
    public NodoAST analizar() {
        logDerivacion.clear();
        errores.clear();
        this.actual = 0;

        // Creamos la raíz del árbol que contendrá todo el código.
        NodoAST raiz = new NodoAST("PROGRAMA", "RAIZ", 0);

        try {
            // Validación de seguridad: Si el código empieza con una llave de cierre '}', es un error obvio.
            if (!esFin() && checar("}")) {
                throw error("Error de flujo: Se encontró '}' sin apertura previa.", 202);
            }

            //  Llamamos a la regla que lee todo el programa.
            NodoAST nodoPrograma = programa();
            
            // Si obtuvimos un programa válido (o parcialmente válido), lo pegamos a la raíz.
            if (nodoPrograma != null) {
                raiz.agregarHijo(nodoPrograma);
            }

            // Validación final: Si sobró una llave de cierre '}', avisamos.
            if (!esFin() && checar("}")) {
                throw error("Error de flujo: Llave de cierre '}' inesperada al final.", 202);
            }

        } catch (ParserException e) {
            // Si el error fue de sintaxis (nuestro error personalizado).
            errores.add(e.getMessage());
        } catch (Exception e) {
            // Si pasó algo muy grave inesperado (bug de programación o error crítico).
            errores.add("DSL(999) Error crítico: " + e.getMessage());
        } finally {
            // Pase lo que pase, generamos el dibujo en texto del árbol para mostrarlo.
            dibujarArbolEnLog(raiz, "", true);
        }
        
        return raiz;
    }

    // --- REGLAS GRAMATICALES ---

    /*
     REGLA: PROGRAMA
     Un programa no es más que una lista de sentencias (instrucciones) una tras otra.
     Este método usa un ciclo 'while' para leer instrucción por instrucción hasta que se acabe el archivo.
     */
    private NodoAST programa() {
        NodoAST nodoBloque = new NodoAST("BLOQUE_CODIGO", "Lista Sentencias", 0);

        while (!esFin()) {
            // Si encontramos una llave de cierre '}', significa que terminó un bloque (como el fin de un IF o WHILE).
            // Rompemos el ciclo para regresar el control al método anterior.
            if (checar("}")) {
                break;
            }

            try {
                // Intentamos leer una sentencia completa.
                NodoAST sent = sentencia();
                if (sent != null) {
                    nodoBloque.agregarHijo(sent);
                }
            } catch (ParserException e) {
                /* RECUPERACIÓN DE ERRORES (MODO PÁNICO):
                 Si una línea tiene error, no queremos detener todo el compilador.
                 1. Guardamos el error.
                 2. Creamos un nodo visual de error en el árbol.
                 3. Llamamos a 'sincronizar()' que salta texto hasta encontrar un punto y coma
                    o una palabra conocida para intentar seguir analizando la siguiente línea.
                 */
                errores.add(e.getMessage());
                
                NodoAST nodoError = new NodoAST("!!! ERROR SINTÁCTICO !!!", "ERROR", tokenActual().getLinea());
                nodoError.agregarHijo(new NodoAST(e.getMessage(), "DETALLE", tokenActual().getLinea()));
                nodoBloque.agregarHijo(nodoError);
                
                sincronizar(); 
            }
        }
        return nodoBloque;
    }

    /*
     REGLA: SENTENCIA (EL DISTRIBUIDOR DE TRÁFICO)
     Este método mira la primera palabra de la línea actual y decide a qué método llamar.
     Si la palabra es "IF", llama a flujoIf(). Si es "CREAR", llama a declaracion(), etc.
     */
    private NodoAST sentencia() {
        if (esFin()) return null;

        String lexema = tokenActual().getLexema().toUpperCase();
        String tipo = tokenActual().getTipoToken();
        int linea = tokenActual().getLinea();

        // Validaciones rápidas de cosas que no deberían estar aquí solas.
        if (lexema.equals("}")) {
            throw error("Error de sintaxis: Se encontró '}' sin una estructura que la abra.", 202);
        }
        if (lexema.equals(";")) {
            throw error("Sentencia vacía detectada (Punto y coma huérfano).", 201);
        }

        // El "Switch" lógico que decide qué hacer según la palabra clave.
        if (lexema.equals("CREAR")) {
            return declaracion();
        } 
        else if (lexema.equals("IF")) {
            return flujoIf();
        } 
        else if (lexema.equals("WHILE")) {
            return bucleWhile();
        } 
        else if (lexema.equals("FOR")) {
            return bucleFor();
        } 
        else if (lexema.equals("DO")) {
            return bucleDoWhile();
        } 
        else if (lexema.equals("MOSTRAR")) {
            return salida();
        } 
        else if (esVerboOperacion(lexema)) { // Palabras como APILAR, INSERTAR, BUSCAR
            return operacionEstructura();
        } 
        else if (tipo.equals("IDENTIFICADOR")) { // Si empieza con el nombre de una variable (ej: x = 5;)
            return asignacion();
        } 
        else {
            throw error("Instrucción no reconocida o inválida: " + lexema, 203);
        }
    }

    /*
     REGLA: DECLARACIÓN
     Maneja la creación de variables. Estructura esperada: CREAR [TIPO] [NOMBRE];
     También soporta inicialización: CREAR ENTERO X = 10;
     */
    private NodoAST declaracion() {
        int linea = tokenActual().getLinea();
        consumir("CREAR"); // Obliga a que la palabra sea CREAR

        String tipoDato = tokenActual().getLexema().toUpperCase();
        if (!esTipoValido(tipoDato)) {
            throw error("Tipo de dato desconocido: " + tipoDato, 204);
        }
        avanzar(); // Consumimos el tipo de dato

        String nombreVar = tokenActual().getLexema();
        consumir("IDENTIFICADOR"); // Obliga a que siga un nombre

        // Creamos la rama del árbol para la declaración
        NodoAST nodoDecl = new NodoAST("DECLARACION", "Declaracion", linea);
        nodoDecl.agregarHijo(new NodoAST(tipoDato, "TIPO", linea));
        nodoDecl.agregarHijo(new NodoAST(nombreVar, "ID", linea));

        // Verificamos si hay un signo '=' para asignarle valor inicial
        if (checar("=")) {
            consumir("=");
            if (tipoDato.equals("TEXTO")) {
                String val = tokenActual().getLexema();
                consumir("CADENA"); 
                nodoDecl.agregarHijo(new NodoAST(val, "CADENA", linea));
            } else {
                // Si es numérico u otro, evaluamos la expresión matemática
                nodoDecl.agregarHijo(expresion());
            }
        } else if (tokenActual().getTipoToken().contains("NUMERO")) {
            // Soporte para sintaxis antigua sin el signo '='
            String val = tokenActual().getLexema();
            avanzar();
            nodoDecl.agregarHijo(new NodoAST(val, "NUMERO", linea));
        }

        consumir(";"); // Siempre debe terminar en punto y coma
        return nodoDecl;
    }

    /*
     REGLA: OPERACIONES DE ESTRUCTURA
     Esta es la lógica compleja para comandos como APILAR, INSERTAR, AGREGARARISTA.
     Tiene lógica especial para asegurarse de que el usuario no olvide poner los valores.
     */
    private NodoAST operacionEstructura() {
        int linea = tokenActual().getLinea();
        String verbo = tokenActual().getLexema().toUpperCase();
        
        NodoAST nodoOp = new NodoAST(verbo, "OPERACION", linea);
        consumir(verbo);

        // --- CASO 1: Verbos simples (Ej: POP EN PILA) ---
        // No requieren valores extra, así que no leemos expresiones aquí.
        if (esVerboSinParametros(verbo)) {
            // Nada que hacer, pasamos directo al bloque 'EN'.
        } 
        
        // --- CASO 2: Insertar específico (Ej: INSERTAR_EN_POSICION 5 10 EN LISTA) ---
        else if (verbo.equals("INSERTAR_EN_POSICION")) {
            nodoOp.agregarHijo(expresion()); // 1. Leemos la Posición
            
            // VALIDACIÓN IMPORTANTE: Si vemos la palabra "EN" aquí, significa que faltó el segundo número.
            if (checar("EN")) {
                throw error("Falta el VALOR a insertar para " + verbo + ". Sintaxis: POSICION VALOR EN ID", 205);
            }
            
            nodoOp.agregarHijo(expresion()); // 2. Leemos el Valor
        }
        
        // --- CASO 3: Grafos (Ej: AGREGARARISTA A B) ---
        else if (verbo.equals("AGREGARARISTA") || verbo.equals("CAMINOCORTO")) {
            nodoOp.agregarHijo(expresion()); // Origen
            
            // Si vemos "EN" aquí, faltó el destino.
            if (checar("EN")) {
                throw error("Falta el nodo DESTINO para " + verbo, 205);
            }
            
            nodoOp.agregarHijo(expresion()); // Destino
        }
        
        // --- CASO 4: Eliminar posición ---
        else if (verbo.equals("ELIMINAR_POSICION")) {
             if (checar("EN")) {
                throw error("Falta la POSICIÓN/ÍNDICE a eliminar.", 205);
            }
            nodoOp.agregarHijo(expresion());
        }
        
        // --- CASO 5: Verbos Estándar (Ej: APILAR 10 EN PILA) ---
        else {
            // SI ENCONTRAMOS "EN" INMEDIATAMENTE, LANZAMOS ERROR.
            // Esto corrige el bug donde "APILAR EN PILA" era válido. Ahora exige un valor.
            if (checar("EN")) {
                throw error("Error de sintaxis: El comando '" + verbo + "' requiere un VALOR antes de 'EN'.", 205);
            }
            
            // Leemos el valor a insertar
            nodoOp.agregarHijo(expresion());
        }

        // --- PARTE COMÚN OBLIGATORIA: "EN [NOMBRE_ESTRUCTURA]" ---
        
        consumir("EN"); // Verifica que esté la palabra EN
        
        String idEst = tokenActual().getLexema();
        consumir("IDENTIFICADOR"); // Verifica que siga el nombre de la estructura
        nodoOp.agregarHijo(new NodoAST(idEst, "ID_ESTRUCTURA", linea));

        // Argumentos opcionales para grafos (Peso)
        if (checar("CON") || checar("PESO")) {
            avanzar(); 
            if (checar(";")) {
                 throw error("Se esperaba un valor numérico para el PESO, se encontró ';'", 207);
            }
            nodoOp.agregarHijo(expresion()); 
        }

        consumir(";");
        return nodoOp;
    }

    // Regla: ASIGNACIÓN SIMPLE (Ej: x = 5 + 3;)
    private NodoAST asignacion() {
        int linea = tokenActual().getLinea();
        String id = tokenActual().getLexema();
        
        consumir("IDENTIFICADOR");
        consumir("=");
        
        NodoAST expr = expresion(); // Evaluamos lo que hay a la derecha del igual
        consumir(";");
        
        NodoAST nodoAsig = new NodoAST("=", "ASIGNACION", linea);
        nodoAsig.agregarHijo(new NodoAST(id, "ID_VAR", linea));
        nodoAsig.agregarHijo(expr);
        return nodoAsig;
    }

    // Regla: MOSTRAR (Imprimir en pantalla)
    private NodoAST salida() {
        int linea = tokenActual().getLinea();
        consumir("MOSTRAR");
        NodoAST nodoMostrar = new NodoAST("MOSTRAR", "Salida", linea);

        // Soporte especial para mostrar vecinos de un grafo
        if (checar("VECINOS")) {
            consumir("VECINOS");
            NodoAST nodoVecinos = new NodoAST("VECINOS", "OpGrafo", linea);
            nodoVecinos.agregarHijo(expresion()); // El nodo del cual queremos vecinos
            consumir("EN");
            String idG = tokenActual().getLexema();
            consumir("IDENTIFICADOR");
            nodoVecinos.agregarHijo(new NodoAST(idG, "GRAFO", linea));
            nodoMostrar.agregarHijo(nodoVecinos);
        } else {
            // Mostrar normal (un número, variable o texto)
            nodoMostrar.agregarHijo(expresion());
        }
        
        consumir(";");
        return nodoMostrar;
    }

    // --- ESTRUCTURAS DE CONTROL (IF, WHILE, FOR) ---
    // Estas funciones manejan bloques de código anidados (código dentro de llaves {})

    private NodoAST flujoIf() {
        int l = tokenActual().getLinea();
        consumir("IF");
        consumir("(");
        NodoAST cond = condicion(); // Evaluamos la condición lógica (ej: a > b)
        consumir(")");
        
        consumir("{");
        NodoAST bloqueTrue = programa(); // Recursividad: Llamamos a 'programa' para leer lo que hay dentro de las llaves
        consumir("}");

        NodoAST nodoIf = new NodoAST("IF", "Control", l);
        nodoIf.agregarHijo(cond);
        
        NodoAST siVerdadero = new NodoAST("ENTONCES", "Bloque", l);
        siVerdadero.agregarHijo(bloqueTrue);
        nodoIf.agregarHijo(siVerdadero);

        // Si existe un ELSE, procesamos el bloque alternativo
        if (coincide("ELSE")) {
            consumir("{");
            NodoAST bloqueFalse = programa();
            consumir("}");
            
            NodoAST siFalso = new NodoAST("SINO", "Bloque", l);
            siFalso.agregarHijo(bloqueFalse);
            nodoIf.agregarHijo(siFalso);
        }
        return nodoIf;
    }

    private NodoAST bucleWhile() {
        int l = tokenActual().getLinea();
        consumir("WHILE");
        consumir("(");
        NodoAST cond = condicion();
        consumir(")");
        
        consumir("{");
        NodoAST cuerpo = programa(); // Cuerpo del ciclo
        consumir("}");
        
        NodoAST nodo = new NodoAST("WHILE", "Bucle", l);
        nodo.agregarHijo(cond);
        nodo.agregarHijo(cuerpo);
        return nodo;
    }

    private NodoAST bucleDoWhile() {
        int l = tokenActual().getLinea();
        consumir("DO");
        consumir("{");
        NodoAST cuerpo = programa();
        consumir("}");
        
        consumir("WHILE");
        consumir("(");
        NodoAST cond = condicion();
        consumir(")");
        consumir(";");
        
        NodoAST nodo = new NodoAST("DO_WHILE", "Bucle", l);
        nodo.agregarHijo(cuerpo);
        nodo.agregarHijo(cond);
        return nodo;
    }

    private NodoAST bucleFor() {
        int l = tokenActual().getLinea();
        consumir("FOR");
        consumir("(");
        
        NodoAST init = asignacionParaFor(); // Parte 1: i = 0
        consumir(";");
        NodoAST cond = condicion();         // Parte 2: i < 10
        consumir(";");
        NodoAST step = asignacionParaFor(); // Parte 3: i = i + 1
        
        consumir(")");
        consumir("{");
        NodoAST cuerpo = programa();        // Bloque a repetir
        consumir("}");
        
        NodoAST nodo = new NodoAST("FOR", "Bucle", l);
        nodo.agregarHijo(init);
        nodo.agregarHijo(cond);
        nodo.agregarHijo(step);
        nodo.agregarHijo(cuerpo);
        return nodo;
    }

    // Método especial para la asignación dentro del FOR, ya que no lleva ';' al final.
    private NodoAST asignacionParaFor() {
        int l = tokenActual().getLinea();
        String id = tokenActual().getLexema();
        
        consumir("IDENTIFICADOR");
        consumir("=");
        NodoAST expr = expresion();
        
        NodoAST n = new NodoAST("=", "ACTUALIZACION", l);
        n.agregarHijo(new NodoAST(id, "ID", l));
        n.agregarHijo(expr);
        return n;
    }

    // --- EXPRESIONES MATEMÁTICAS Y LÓGICAS ---
    /*
     Aquí manejamos la "precedencia de operadores".
     Funciona en capas: 
     1. Condicion (>, <, ==)
     2. Expresion (+, -) -> Menor prioridad, se procesa al último
     3. Termino (*, /) -> Mayor prioridad que suma
     4. Factor (Números, Parentesis) -> Máxima prioridad
     */

    private NodoAST condicion() {
        NodoAST izq = expresion();
        String op = tokenActual().getLexema();
        
        // Verificamos si es un operador de comparación
        if (Set.of("==", "!=", "<", ">", "<=", ">=").contains(op)) {
            avanzar();
            NodoAST der = expresion();
            NodoAST n = new NodoAST(op, "COMPARACION", 0);
            n.agregarHijo(izq);
            n.agregarHijo(der);
            return n;
        } else {
            throw error("Se esperaba un operador relacional (==, <, >...), se encontró: " + op, 206);
        }
    }

    private NodoAST expresion() {
        NodoAST izq = termino(); // Primero resolvemos multiplicaciones (términos)
        // Luego resolvemos sumas y restas
        while (checar("+") || checar("-")) {
            String op = tokenActual().getLexema();
            int l = tokenActual().getLinea();
            avanzar();
            NodoAST der = termino();
            NodoAST n = new NodoAST(op, "OPERACION", l);
            n.agregarHijo(izq);
            n.agregarHijo(der);
            izq = n;
        }
        return izq;
    }

    private NodoAST termino() {
        NodoAST izq = factor(); // Primero resolvemos factores (números o paréntesis)
        // Luego resolvemos multiplicaciones y divisiones
        while (checar("*") || checar("/")) {
            String op = tokenActual().getLexema();
            int l = tokenActual().getLinea();
            avanzar();
            NodoAST der = factor();
            NodoAST n = new NodoAST(op, "OPERACION", l);
            n.agregarHijo(izq);
            n.agregarHijo(der);
            izq = n;
        }
        return izq;
    }

    // El átomo de una expresión: un número, una variable o algo entre paréntesis.
    private NodoAST factor() {
        String lexema = tokenActual().getLexema().toUpperCase();
        String tipo = tokenActual().getTipoToken();
        int linea = tokenActual().getLinea();

        // Valores Booleanos
        if (lexema.equals("VERDADERO") || lexema.equals("FALSO")) {
            avanzar();
            return new NodoAST(lexema, "BOOLEANO", linea);
        }

        // Propiedades de Estructuras (ej: TOPE EN PILA)
        if (esPropiedad(lexema)) {
            String prop = lexema;
            consumir(prop);
            
            NodoAST nodoProp = new NodoAST(prop, "PROPIEDAD", linea);
            
            // Caso especial para VECINOS
            if (prop.equals("VECINOS")) {
                 nodoProp.agregarHijo(expresion()); 
            }

            consumir("EN");
            String idEst = tokenActual().getLexema();
            consumir("IDENTIFICADOR");
            nodoProp.agregarHijo(new NodoAST(idEst, "ID_ESTRUCTURA", linea));
            return nodoProp;
        }

        // Paréntesis: ( 5 + 3 ) -> Reinicia la evaluación de expresión adentro
        if (lexema.equals("(")) {
            consumir("(");
            NodoAST e = expresion();
            consumir(")");
            return e;
        }

        // Identificadores (Variables)
        if (tipo.equals("IDENTIFICADOR")) {
            String val = tokenActual().getLexema();
            avanzar();
            return new NodoAST(val, "IDENTIFICADOR", linea);
        }
        // Números
        if (tipo.contains("NUMERO") || tipo.contains("LITERAL_NUMERICA")) {
            String val = tokenActual().getLexema();
            avanzar();
            return new NodoAST(val, "NUMERO", linea);
        }
        // Cadenas de texto
        if (tipo.contains("CADENA") || tipo.contains("TEXTO")) {
            String val = tokenActual().getLexema();
            avanzar();
            return new NodoAST(val, "CADENA", linea);
        }

        throw error("Factor inválido en expresión: " + lexema, 207);
    }

    // --- UTILIDADES Y NAVEGACIÓN (HERRAMIENTAS INTERNAS) ---

    // Método "Exigente": Verifica que el token actual sea X. Si lo es, avanza. Si no, lanza error.
    private void consumir(String esperado) {
        if (checar(esperado)) {
            avanzar();
        } else {
            String encontrado = esFin() ? "FIN_DE_ARCHIVO" : tokenActual().getLexema();
            throw error("Se esperaba '" + esperado + "', pero se encontró '" + encontrado + "'", 203);
        }
    }

    // Método "Observador": Revisa si el token actual es X, pero no avanza el cursor.
    private boolean checar(String s) {
        if (esFin()) return false;
        Token t = tokenActual();
        return t.getLexema().equalsIgnoreCase(s) || t.getTipoToken().equals(s);
    }

    // Método "Oportunista": Si el token es X, lo consume y retorna true. Si no, retorna false (no lanza error).
    private boolean coincide(String s) {
        if (checar(s)) {
            avanzar();
            return true;
        }
        return false;
    }

    // Mueve el cursor a la siguiente palabra.
    private void avanzar() {
        if (!esFin()) actual++;
    }

    // Nos dice si ya nos acabamos la lista de tokens.
    private boolean esFin() {
        return actual >= tokens.length;
    }

    // Obtiene el objeto Token actual sin riesgo de desbordar el arreglo.
    private Token tokenActual() {
        if (actual >= tokens.length) return tokens[tokens.length - 1];
        return tokens[actual];
    }

    // Genera la excepción con formato bonito.
    private ParserException error(String m, int codigo) {
        int linea = tokenActual().getLinea();
        return new ParserException("DSL(" + codigo + ") [Línea " + linea + "]: " + m);
    }

    // --- RECUPERACIÓN DE ERRORES (MODO PÁNICO) ---
    /*
     Cuando ocurre un error, este método "come" tokens a lo loco hasta encontrar
     un punto y coma (;) o una palabra clave de inicio (IF, WHILE, etc.).
     Esto permite que el compilador siga revisando las siguientes líneas en lugar de detenerse totalmente.
     */
    private void sincronizar() {
        avanzar();
        while (!esFin()) {
            if (tokenActual().getLexema().equals(";")) {
                avanzar();
                return;
            }
            // Si encontramos una palabra reservada de inicio, asumimos que hemos recuperado el flujo
            String lex = tokenActual().getLexema().toUpperCase();
            if (Set.of("CREAR", "IF", "WHILE", "FOR", "DO", "MOSTRAR", "INSERTAR", "APILAR", "ENCOLAR", "ELIMINAR", "BFS", "DFS").contains(lex)) {
                return;
            }
            avanzar();
        }
    }

    // --- GENERADOR VISUAL DE ÁRBOL ---
    /*
     Este método usa recursividad para dibujar el árbol bonito con líneas.
     Se llama a sí mismo para dibujar a los hijos, agregando indentación (│   )
     */
    private void dibujarArbolEnLog(NodoAST nodo, String prefijo, boolean esUltimo) {
        if (nodo == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append(prefijo).append(esUltimo ? "└── " : "├── ").append(nodo.getValor());
        logDerivacion.add(sb.toString());

        List<NodoAST> hijos = nodo.getHijos();
        for (int i = 0; i < hijos.size(); i++) {
            dibujarArbolEnLog(hijos.get(i), prefijo + (esUltimo ? "    " : "│   "), i == hijos.size() - 1);
        }
    }

    // --- LISTAS DE PALABRAS RESERVADAS (SETS) ---
    // Usamos Sets porque buscar en ellos es extremadamente rápido.

    private boolean esVerboOperacion(String s) {
        return Set.of(
            // Lista de todos los verbos que modifican estructuras
            "INSERTAR", "INSERTAR_FINAL", "INSERTAR_INICIO", "INSERTAR_EN_POSICION",
            "INSERTARIZQUIERDA", "INSERTARDERECHA", "AGREGARNODO", "APILAR", "ENCOLAR",
            "PUSH", "ENQUEUE", "ELIMINAR", "ELIMINAR_INICIO", "ELIMINAR_FINAL",
            "ELIMINAR_FRENTE", "ELIMINAR_POSICION", "ELIMINARNODO", "DESAPILAR", "POP",
            "DESENCOLAR", "DEQUEUE", "BUSCAR", "RECORRER", "BFS", "DFS", "AGREGARARISTA",
            "ELIMINARARISTA", "ACTUALIZAR", "REHASH", "CAMINOCORTO",
            "INSERTAR_FRENTE", "VER_FILA", "VERFILA"
        ).contains(s);
    }

    private boolean esVerboSinParametros(String s) {
        // Lista de verbos que solo requieren "EN [ESTRUCTURA]" sin valor numérico
        return Set.of(
            "ELIMINAR", "DESAPILAR", "POP", "DESENCOLAR", "DEQUEUE",
            "ELIMINAR_INICIO", "ELIMINAR_FINAL", "ELIMINAR_FRENTE",
            "RECORRER", "RECORRERADELANTE", "RECORRERATRAS", "BFS", "DFS",
            "PREORDEN", "INORDEN", "POSTORDEN", "RECORRIDOPORNIVELES", "VACIA"
        ).contains(s);
    }

    private boolean esPropiedad(String s) {
        // Lista de propiedades que devuelven un valor (altura, tamaño, tope, etc.)
        return Set.of(
            "TOPE", "FRENTE", "FRONT", "PEEK", "CLAVE", "TAMANO", "ALTURA", 
            "HOJAS", "NODOS", "VECINOS",
            "VACIA", "LLENA", "GRADO", "VER_FILA", "VERFILA", "PREORDEN", "INORDEN", "POSTORDEN"
        ).contains(s);
    }
    
    private boolean esTipoValido(String s) {
        // Lista de tipos de datos permitidos para declarar variables
        return Set.of(
            "PILA", "COLA", "BICOLA", "LISTA_ENLAZADA", "LISTA_CIRCULAR", "LISTA_DOBLE_ENLAZADA",
            "ARBOL_BINARIO", "TABLA_HASH", "GRAFO", "PILA_CIRCULAR", "NUMERO", "TEXTO"
        ).contains(s);
    }
}