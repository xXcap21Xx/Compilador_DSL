import java.util.List;

public class AnalizadorSemantico {

    private final TablaSimbolos tablaSimbolos;
    private final TablaErrores tablaErrores;

    public AnalizadorSemantico(TablaSimbolos ts, TablaErrores te) {
        this.tablaSimbolos = ts;
        this.tablaErrores = te;
    }

    public void analizar(NodoAST raiz) {
        if (raiz == null) return;
        tablaSimbolos.limpiar(); // Limpiamos la memoria antes de empezar la ejecución
        evaluarNodo(raiz);
    }

    private void evaluarNodo(NodoAST nodo) {
        if (nodo == null) return;

        String tipoNodo = nodo.getTipo() != null ? nodo.getTipo().toUpperCase().trim() : "";
        String valorNodo = nodo.getValor() != null ? nodo.getValor().trim() : "";
        int linea = nodo.getLinea();

        // ==========================================================
        // 1. GESTIÓN DE ÁMBITOS (ENTRAR)
        // ==========================================================
        boolean esBloque = tipoNodo.equals("IF") || tipoNodo.equals("WHILE") || 
                           tipoNodo.equals("FOR") || tipoNodo.equals("DO") || tipoNodo.equals("BLOQUE");
        if (esBloque) {
            tablaSimbolos.entrarAmbito(); // ¡Creamos una caja temporal de memoria!
        }

        // ==========================================================
        // 2. DECLARAR VARIABLES (Guarda en la memoria y audita tipos)
        // ==========================================================
        if (tipoNodo.equals("DECLARACION")) {
            String tipoVar = "";
            String nombreVar = "";
            NodoAST nodoValor = null;

            // Extraemos las partes de la declaración
            tipoVar = nodo.getHijos().get(0).getValor().toUpperCase().trim();
            nombreVar = nodo.getHijos().get(1).getValor().trim();
            if (nodo.getHijos().size() > 2) {
                nodoValor = nodo.getHijos().get(2);
            }

            if (!tipoVar.isEmpty() && !nombreVar.isEmpty()) {
                if (!tablaSimbolos.existe(nombreVar)) {
                    Object valorInicial = "Vacio"; // Valor por defecto si no se inicializa
                    if (nodoValor != null) {
                        String tipoValor = evaluarTipoExpresion(nodoValor);
                        valorInicial = nodoValor.getValor(); // Placeholder

                        if (!tipoVar.equals(tipoValor) && !tipoValor.equals("DESCONOCIDO")) {
                            tablaErrores.reporte(linea, "Semántico", "DSL(303) Incompatibilidad: No puedes asignar un valor de tipo " + tipoValor + " a la variable '" + nombreVar + "' que es de tipo " + tipoVar + ".");
                        }
                    }
                    tablaSimbolos.insertar(nombreVar, tipoVar, valorInicial);
                } else {
                    tablaErrores.reporte(linea, "Semántico", "DSL(302) La variable '" + nombreVar + "' ya fue declarada previamente en este contexto.");
                }
            }
            return; // Detenemos el análisis aquí para no procesar a los hijos como variables usadas
        }

        // ==========================================================
        // 3. USO DE VARIABLES (Reglas 301, 305 y 306)
        // ==========================================================
        if (tipoNodo.equals("ID_ESTRUCTURA") || tipoNodo.equals("ID_VAR") || 
            tipoNodo.equals("IDENTIFICADOR") || tipoNodo.equals("GRAFO") || tipoNodo.equals("ID")) {
            
            if (!tablaSimbolos.existe(valorNodo)) {
                // MODIFICADO: Agregamos que pudo haber sido destruida
                tablaErrores.reporte(linea, "Semántico", 
                    "DSL(301) La variable '" + valorNodo + "' no existe o ya fue DESTRUIDA al salir de su bloque (Fuera de Scope).");
            } else {
                TablaSimbolos.Simbolo sim = tablaSimbolos.getSimbolo(valorNodo);
                if (sim != null && (sim.tipo.equals("NUMERO") || sim.tipo.equals("TEXTO"))) {
                    if (sim.valor.equals("Vacio")) {
                        tablaErrores.reporte(linea, "Semántico", 
                            "DSL(306) Peligro de Null: La variable '" + valorNodo + "' se está utilizando pero no tiene ningún valor.");
                    }
                }
            }
        }

        if (tipoNodo.equals("COMPARACION") || 
           (tipoNodo.equals("OPERACION") && (valorNodo.equals("+") || valorNodo.equals("-") || valorNodo.equals("*") || valorNodo.equals("/")))) {
            if (nodo.getHijos() != null) {
                for (NodoAST hijo : nodo.getHijos()) {
                    String etiquetaHijo = hijo.getTipo() != null ? hijo.getTipo().toUpperCase().trim() : "";
                    if (etiquetaHijo.equals("ID_ESTRUCTURA") || etiquetaHijo.equals("ID_VAR") || 
                        etiquetaHijo.equals("ID") || etiquetaHijo.equals("IDENTIFICADOR")) {
                        String nombreVariable = hijo.getValor().trim();
                        if (tablaSimbolos.existe(nombreVariable)) {
                            TablaSimbolos.Simbolo sim = tablaSimbolos.getSimbolo(nombreVariable);
                            if (sim != null && !sim.tipo.toUpperCase().equals("NUMERO") && !sim.tipo.toUpperCase().equals("TEXTO")) {
                                tablaErrores.reporte(linea, "Semántico", 
                                    "DSL(305) Operación Inválida: No puedes comparar ni hacer matemáticas con la estructura '" + nombreVariable + "'.");
                            }
                        }
                    }
                }
            }
        }

        // ==========================================================
        // 4. COMPATIBILIDAD DE VERBOS (Regla 304)
        // ==========================================================
        if (tipoNodo.equals("OPERACION") || tipoNodo.equals("PROPIEDAD")) {
            String verbo = valorNodo.toUpperCase(); 
            String nombreEstructura = "";
            
            if (nodo.getHijos() != null) {
                for (NodoAST hijo : nodo.getHijos()) {
                    String etiqueta = hijo.getTipo() != null ? hijo.getTipo().toUpperCase().trim() : "";
                    if (etiqueta.equals("ID_ESTRUCTURA") || etiqueta.equals("GRAFO") || etiqueta.equals("ID")) {
                        nombreEstructura = hijo.getValor().trim();
                        break;
                    }
                }
            }

            if (!nombreEstructura.isEmpty() && tablaSimbolos.existe(nombreEstructura)) {
                TablaSimbolos.Simbolo sim = tablaSimbolos.getSimbolo(nombreEstructura);
                if (sim != null && sim.tipo != null) {
                    validarCompatibilidad(verbo, sim.tipo.toUpperCase(), nombreEstructura, linea);
                }
            }
        }

        // ==========================================================
        // 5. CONTINUAR RECORRIENDO EL ÁRBOL
        // ==========================================================
        if (nodo.getHijos() != null) {
            for (NodoAST hijo : nodo.getHijos()) {
                evaluarNodo(hijo);
            }
        }

        // ==========================================================
        // 6. GESTIÓN DE ÁMBITOS (SALIR Y DESTRUIR)
        // ==========================================================
        if (esBloque) {
            tablaSimbolos.salirAmbito(); // ¡Al cerrar la llave, destruimos la memoria temporal!
        }
    }

    /**
     * Evalúa recursivamente el tipo de una expresión (NUMERO, TEXTO, o DESCONOCIDO).
     * Esto ayuda a validar asignaciones y operaciones.
     */
    private String evaluarTipoExpresion(NodoAST nodo) {
        if (nodo == null) return "DESCONOCIDO";

        String tipoNodo = nodo.getTipo().toUpperCase();

        switch (tipoNodo) {
            case "NUMERO":
            case "LITERAL_NUMERICA":
                return "NUMERO";
            case "CADENA":
            case "LITERAL_CADENA":
                return "TEXTO";
            case "IDENTIFICADOR":
            case "ID":
                if (tablaSimbolos.existe(nodo.getValor())) {
                    return tablaSimbolos.getSimbolo(nodo.getValor()).tipo.toUpperCase();
                }
                return "DESCONOCIDO"; // El error de "no existe" se reporta en otra parte
            case "OPERACION":
            case "COMPARACION":
                if (nodo.getHijos().size() < 2) return "DESCONOCIDO";
                String tipoIzq = evaluarTipoExpresion(nodo.getHijos().get(0));
                String tipoDer = evaluarTipoExpresion(nodo.getHijos().get(1));

                if (tipoIzq.equals("NUMERO") && tipoDer.equals("NUMERO")) {
                    return "NUMERO"; // Operaciones matemáticas entre números dan un número
                }
                if (!tipoIzq.equals(tipoDer)) {
                    tablaErrores.reporte(nodo.getLinea(), "Semántico", "DSL(303) Incompatibilidad: No se pueden mezclar tipos " + tipoIzq + " y " + tipoDer + " en una operación.");
                    return "DESCONOCIDO";
                }
                return tipoIzq; // Si ambos son iguales (ej. TEXTO + TEXTO)
            default:
                return "DESCONOCIDO";
        }
    }

    /**
     * Módulo de Reglas Estrictas de Estructuras de Datos.
     */
private void validarCompatibilidad(String verbo, String tipoEstructura, String nombreVar, int linea) {
        boolean esValido = false;

        switch (tipoEstructura) {
            case "PILA":
            case "PILA_CIRCULAR":
                esValido = verbo.equals("APILAR") || verbo.equals("PUSH") || 
                           verbo.equals("DESAPILAR") || verbo.equals("POP") || 
                           verbo.equals("TOPE") || verbo.equals("PEEK") || 
                           verbo.equals("VACIA") || verbo.equals("MOSTRAR");
                break;
                
            case "COLA":
            case "BICOLA":
                esValido = verbo.equals("ENCOLAR") || verbo.equals("ENQUEUE") || 
                           verbo.equals("DESENCOLAR") || verbo.equals("DEQUEUE") || 
                           verbo.equals("FRENTE") || verbo.equals("FRONT") || 
                           verbo.equals("VER_FILA") || verbo.equals("VERFILA") || 
                           verbo.equals("VACIA") || verbo.equals("MOSTRAR");
                break;

            // --- REGLAS PARA LISTAS (SEPARADAS PARA MAYOR PRECISIÓN) ---
            case "LISTA_ENLAZADA":
            case "LISTA_CIRCULAR":
                esValido = verbo.equals("INSERTAR") || verbo.equals("INSERTAR_FINAL") || 
                           verbo.equals("INSERTAR_INICIO") || verbo.equals("INSERTAR_EN_POSICION") ||
                           verbo.equals("ELIMINAR") || verbo.equals("ELIMINAR_INICIO") || 
                           verbo.equals("ELIMINAR_FINAL") || verbo.equals("ELIMINAR_POSICION") ||
                           verbo.equals("BUSCAR") || verbo.equals("RECORRER") ||
                           verbo.equals("TAMANO") || verbo.equals("MOSTRAR");
                break;

            case "LISTA_DOBLE_ENLAZADA":
                // Hereda todos los de la lista simple y agrega los de recorrido bidireccional
                esValido = verbo.equals("INSERTAR") || verbo.equals("INSERTAR_FINAL") || 
                           verbo.equals("INSERTAR_INICIO") || verbo.equals("INSERTAR_EN_POSICION") ||
                           verbo.equals("ELIMINAR") || verbo.equals("ELIMINAR_INICIO") || 
                           verbo.equals("ELIMINAR_FINAL") || verbo.equals("ELIMINAR_POSICION") ||
                           verbo.equals("BUSCAR") || verbo.equals("RECORRER") ||
                           verbo.equals("RECORRERADELANTE") || verbo.equals("RECORRERATRAS") || // <-- Solo aquí
                           verbo.equals("TAMANO") || verbo.equals("MOSTRAR");
                break;

            // --- ¡NUEVAS REGLAS PARA GRAFOS! ---
            case "GRAFO":
                esValido = verbo.equals("AGREGARARISTA") || verbo.equals("ELIMINARARISTA") || 
                           verbo.equals("BFS") || verbo.equals("DFS") || 
                           verbo.equals("VECINOS") || verbo.equals("CAMINOCORTO") || 
                           verbo.equals("GRADO") || verbo.equals("MOSTRAR");
                break;

            // --- ¡NUEVAS REGLAS PARA ÁRBOLES! ---
            case "ARBOL_BINARIO":
                esValido = verbo.equals("AGREGARNODO") || verbo.equals("ELIMINARNODO") || 
                           verbo.equals("INSERTARIZQUIERDA") || verbo.equals("INSERTARDERECHA") ||
                           verbo.equals("PREORDEN") || verbo.equals("INORDEN") || 
                           verbo.equals("POSTORDEN") || verbo.equals("RECORRIDOPORNIVELES") ||
                           verbo.equals("ALTURA") || verbo.equals("HOJAS") || 
                           verbo.equals("NODOS") || verbo.equals("MOSTRAR");
                break;

            // --- ¡NUEVAS REGLAS PARA TABLAS HASH! ---
            case "TABLA_HASH":
                esValido = verbo.equals("INSERTAR") || verbo.equals("ELIMINAR") || 
                           verbo.equals("BUSCAR") || verbo.equals("ACTUALIZAR") || 
                           verbo.equals("REHASH") || verbo.equals("CLAVE") || 
                           verbo.equals("MOSTRAR");
                break;
                
            case "NUMERO":
            case "TEXTO":
                esValido = verbo.equals("MOSTRAR"); 
                break;
                
            default:
                esValido = true; 
        }

        if (!esValido) {
            tablaErrores.reporte(linea, "Semántico", 
                "DSL(304) Incompatibilidad: El comando '" + verbo + "' NO pertenece a la estructura " + tipoEstructura + " ('" + nombreVar + "').");
        }
    }
}