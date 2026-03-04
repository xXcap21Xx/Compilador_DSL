import java.util.List;

public class AnalizadorSemantico {

    private final TablaSimbolos tablaSimbolos;
    private final TablaErrores tablaErrores;

    public AnalizadorSemantico(TablaSimbolos ts, TablaErrores te) {
        this.tablaSimbolos = ts;
        this.tablaErrores = te;
    }

    public void analizar(NodoAST nodo) {
        if (nodo == null) return;

        // Recorrido en Post-orden: Primero evaluamos a los hijos, luego al padre
        List<NodoAST> hijos = nodo.getHijos();
        if (hijos != null) {
            for (NodoAST hijo : hijos) {
                analizar(hijo);
            }
        }

        evaluarNodo(nodo);
    }

    private void evaluarNodo(NodoAST nodo) {
        String tipoNodo = nodo.getTipo(); // Ej: "OPERACION", "ID_ESTRUCTURA", "Declaracion"
        String valorNodo = nodo.getValor(); // Ej: "APILAR", "miPila"
        int linea = nodo.getLinea();

        if (tipoNodo == null || tipoNodo.contains("ERROR")) return;

        // --- REGLA 1: VALIDAR QUE LAS VARIABLES USADAS EXISTAN ---
        // Tu Sintáctico clasifica los usos de variables como "ID_ESTRUCTURA" o "ID_VAR"
        if (tipoNodo.equals("ID_ESTRUCTURA") || tipoNodo.equals("ID_VAR")) {
            if (!tablaSimbolos.existe(valorNodo)) {
                tablaErrores.reporte(linea, "Semántico", 
                    "DSL(301) La variable o estructura '" + valorNodo + "' no ha sido declarada con CREAR.");
            }
        }

        // --- REGLA 2: VALIDAR OPERACIONES DE ESTRUCTURAS DE DATOS ---
        // Tu Sintáctico etiqueta los comandos de estructura como "OPERACION" y su valor es el verbo.
        if (tipoNodo.equals("OPERACION")) {
            List<NodoAST> hijos = nodo.getHijos();
            
            if (hijos != null && !hijos.isEmpty()) {
                // Buscamos cuál de los hijos es el identificador de la estructura
                NodoAST nodoEstructura = null;
                for (NodoAST hijo : hijos) {
                    if ("ID_ESTRUCTURA".equals(hijo.getTipo())) {
                        nodoEstructura = hijo;
                        break;
                    }
                }

                if (nodoEstructura != null) {
                    String nombreEstructura = nodoEstructura.getValor();
                    
                    // Si existe en la tabla, validamos que el verbo concuerde con el tipo
                    if (tablaSimbolos.existe(nombreEstructura)) {
                        TablaSimbolos.Simbolo sim = tablaSimbolos.getSimbolo(nombreEstructura);
                        validarCompatibilidad(valorNodo.toUpperCase(), sim.tipo.toUpperCase(), nombreEstructura, linea);
                    }
                }
            }
        }
    }

    /**
     * Módulo central de reglas del lenguaje.
     * Aquí verificamos la compatibilidad de verbos con estructuras.
     */
    private void validarCompatibilidad(String verbo, String tipoEstructura, String nombreVar, int linea) {
        boolean esValido = false;

        switch (tipoEstructura) {
            case "PILA":
            case "PILA_CIRCULAR":
                esValido = verbo.equals("APILAR") || verbo.equals("PUSH") || 
                           verbo.equals("DESAPILAR") || verbo.equals("POP") || 
                           verbo.equals("TOPE") || verbo.equals("VACIA") || verbo.equals("MOSTRAR");
                break;
                
            case "COLA":
            case "BICOLA":
                esValido = verbo.equals("ENCOLAR") || verbo.equals("ENQUEUE") || 
                           verbo.equals("DESENCOLAR") || verbo.equals("DEQUEUE") || 
                           verbo.equals("FRENTE") || verbo.equals("FRONT") || 
                           verbo.equals("VER_FILA") || verbo.equals("VACIA") || verbo.equals("MOSTRAR");
                break;

            case "LISTA_ENLAZADA":
            case "LISTA_CIRCULAR":
                esValido = verbo.equals("INSERTAR") || verbo.equals("ELIMINAR") || 
                           verbo.equals("INSERTAR_FINAL") || verbo.equals("INSERTAR_INICIO") ||
                           verbo.equals("BUSCAR") || verbo.equals("RECORRER") || verbo.equals("MOSTRAR");
                break;

            case "GRAFO":
                esValido = verbo.equals("AGREGARARISTA") || verbo.equals("ELIMINARARISTA") || 
                           verbo.equals("BFS") || verbo.equals("DFS") || 
                           verbo.equals("VECINOS") || verbo.equals("CAMINOCORTO") || verbo.equals("MOSTRAR");
                break;

            case "ARBOL_BINARIO":
                esValido = verbo.equals("AGREGARNODO") || verbo.equals("ELIMINARNODO") || 
                           verbo.equals("PREORDEN") || verbo.equals("INORDEN") || 
                           verbo.equals("POSTORDEN") || verbo.equals("ALTURA") || 
                           verbo.equals("HOJAS") || verbo.equals("MOSTRAR");
                break;
                
            case "NUMERO":
            case "TEXTO":
                esValido = verbo.equals("MOSTRAR"); 
                break;
                
            default:
                esValido = true; // Permite fluir a tipos aún no definidos en las reglas
        }

        if (!esValido) {
            tablaErrores.reporte(linea, "Semántico", 
                "DSL(304) El comando '" + verbo + "' no se puede aplicar a la variable '" + nombreVar + "' porque es de tipo " + tipoEstructura + ".");
        }
    }
}