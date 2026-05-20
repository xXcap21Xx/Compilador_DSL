package compilador.codegen;

import java.util.*;
import compilador.core.Cuadruplo;

/**
 * Optimizador sencillo de Código Intermedio (Cuádruplos)
 * Implementa algunas optimizaciones simples:
 *  - Eliminación de asignaciones redundantes (t = t)
 *  - Plegado de constantes para operaciones básicas con literales
 *  - Eliminación de cuádruplos no usados (muy simple, basada en uso de resultados)
 */
public class OptimizadorCGI {

    public List<Cuadruplo> optimizar(List<Cuadruplo> codigo) {
        if (codigo == null) return Collections.emptyList();

        // 1) Plegado de constantes y eliminación local simple
        List<Cuadruplo> paso1 = new ArrayList<>();
        for (Cuadruplo c : codigo) {
            if (c == null) continue;

            // eliminar asignaciones redundantes: x = x
            if ("=".equals(c.operador) && c.resultado != null && c.argumento1 != null
                    && c.resultado.equals(c.argumento1)) {
                continue;
            }

            // plegado de constantes: a = 2 + 3 -> a = 5
            if (c.argumento1 != null && c.argumento2 != null && isNumeric(c.argumento1) && isNumeric(c.argumento2)
                    && isArithmeticOperator(c.operador)) {
                try {
                    long v1 = Long.parseLong(c.argumento1);
                    long v2 = Long.parseLong(c.argumento2);
                    long res = 0;
                    switch (c.operador) {
                        case "+": res = v1 + v2; break;
                        case "-": res = v1 - v2; break;
                        case "*": res = v1 * v2; break;
                        case "/": if (v2 != 0) res = v1 / v2; else { paso1.add(c); continue; } break;
                        default: paso1.add(c); continue;
                    }
                    paso1.add(new Cuadruplo("=", Long.toString(res), null, c.resultado));
                    continue;
                } catch (NumberFormatException ex) {
                    // caemos al manejo normal
                }
            }

            paso1.add(c);
        }

        // 2) Propagación de copias (copy propagation) - sustitución simple de x = y
        List<Cuadruplo> paso2 = copyPropagation(paso1);

        // 3) Eliminación de código muerto iterativa basada en uso (backward liveness)
        List<Cuadruplo> paso3 = eliminarCodigoMuerto(paso2);

        return paso3;
    }

    private List<Cuadruplo> copyPropagation(List<Cuadruplo> codigo) {
        Map<String, String> copia = new HashMap<>();
        List<Cuadruplo> salida = new ArrayList<>();

        for (Cuadruplo c : codigo) {
            if (c == null) continue;

            // remplazar argumentos por copia conocida
            if (c.argumento1 != null && copia.containsKey(c.argumento1)) {
                c.argumento1 = copia.get(c.argumento1);
            }
            if (c.argumento2 != null && copia.containsKey(c.argumento2)) {
                c.argumento2 = copia.get(c.argumento2);
            }

            // Si es una asignación simple: t = x  (operador "=") y no implica operaciones
            if ("=".equals(c.operador) && c.argumento2 == null && c.argumento1 != null) {
                // registrar copia t -> x
                copia.put(c.resultado, c.argumento1);
            } else {
                // cualquier escritura invalida copias que referencien a la variable result
                if (c.resultado != null) {
                    // eliminar mapeos que definen o usan la variable reescrita
                    copia.remove(c.resultado);
                    // también limpiar entradas cuyo valor sea la variable reescrita
                    copia.values().removeIf(v -> v.equals(c.resultado));
                }
            }

            salida.add(c);
        }

        return salida;
    }

    private List<Cuadruplo> eliminarCodigoMuerto(List<Cuadruplo> codigo) {
        List<Cuadruplo> current = new ArrayList<>(codigo);
        boolean changed = true;

        while (changed) {
            changed = false;
            Set<String> usados = new HashSet<>();

            // Semilla: variables usadas por efectos secundarios y saltos
            for (Cuadruplo c : current) {
                if (c == null) continue;
                if (esEfectoSecundario(c) || "GOTO".equals(c.operador) || c.operador.startsWith("IF")) {
                    if (c.argumento1 != null) usados.add(c.argumento1);
                    if (c.argumento2 != null) usados.add(c.argumento2);
                }
            }

            List<Cuadruplo> nuevo = new ArrayList<>();

            // Recorrer hacia atrás y conservar instrucciones que definan variables usadas
            for (int i = current.size() - 1; i >= 0; i--) {
                Cuadruplo c = current.get(i);
                if (c == null) continue;

                boolean conservar = false;

                if ("ETIQUETA".equals(c.operador) || "GOTO".equals(c.operador) || c.operador.startsWith("IF") || esEfectoSecundario(c)) {
                    // siempre conservar saltos, etiquetas y efectos
                    conservar = true;
                } else if (c.resultado == null) {
                    // instrucciones sin resultado, conservar
                    conservar = true;
                } else if (usados.contains(c.resultado)) {
                    conservar = true;
                }

                if (conservar) {
                    nuevo.add(0, c);
                    if (c.argumento1 != null) usados.add(c.argumento1);
                    if (c.argumento2 != null) usados.add(c.argumento2);
                } else {
                    // se elimina: marcar cambio
                    changed = true;
                }
            }

            current = nuevo;
        }

        return current;
    }

    private boolean isNumeric(String s) {
        if (s == null) return false;
        return s.matches("-?\\d+");
    }

    private boolean isArithmeticOperator(String op) {
        return "+".equals(op) || "-".equals(op) || "*".equals(op) || "/".equals(op);
    }

    private boolean esEfectoSecundario(Cuadruplo c) {
        if (c == null) return false;
        if (c.operador == null) return false;

        String op = c.operador.toUpperCase();
        return Set.of(
                "PRINT", "MOSTRAR", "ALLOC", "FREE", "ERROR",
                "INSERTAR", "INSERTAR_FINAL", "INSERTAR_INICIO", "INSERTAR_EN_POSICION",
                "INSERTAR_FRENTE", "AGREGARNODO", "ELIMINARNODO",
                "APILAR", "PUSH", "DESAPILAR", "POP",
                "ENCOLAR", "ENQUEUE", "DESENCOLAR", "DEQUEUE",
                "ELIMINAR", "ELIMINAR_INICIO", "ELIMINAR_FINAL", "ELIMINAR_FRENTE",
                "ELIMINAR_POSICION", "BUSCAR", "RECORRER", "BFS", "DFS",
                "AGREGARARISTA", "ELIMINARARISTA", "ACTUALIZAR", "REHASH",
                "CAMINOCORTO"
        ).contains(op);
    }
}
