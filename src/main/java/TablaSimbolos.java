import java.util.HashMap;
import java.util.Map;

/*
 ESTA CLASE ES LA "MEMORIA" DEL COMPILADOR (TABLA DE SÍMBOLOS).
 Su función es recordar todas las variables que el usuario ha declarado.
 
 Cada vez que escribes "CREAR ENTERO X = 10;", esta clase guarda:
 - Clave: "X" (para buscarla rápido)
 - Datos: {nombre: "X", tipo: "ENTERO", valor: 10}
 */
public class TablaSimbolos {

    /*
     EL ALMACÉN DE DATOS (Map)
     Usamos un HashMap porque funciona como un diccionario:
     Tú le das una palabra clave (el nombre de la variable) y él te devuelve
     la definición (el objeto Simbolo) casi instantáneamente.
     
     - Key (String): El nombre de la variable (ej: "contador").
     - Value (Simbolo): El objeto con toda la info.
     */
    private final Map<String, Simbolo> simbolos;

    // CONSTRUCTOR
    // Prepara la "libreta" vacía para empezar a anotar variables.
    public TablaSimbolos() {
        this.simbolos = new HashMap<>();
    }

    /*
     MÉTODO: INSERTAR (GUARDAR VARIABLE)
     Este método se usa en dos momentos:
     1. Cuando declaras una variable: "CREAR ENTERO A;"
     2. Cuando actualizas una variable: "A = 20;" (aquí se sobrescribe el valor anterior).
     
     Recibe:
     - nombre: "A"
     - tipo: "ENTERO"
     - valor: 20 (Se usa Object porque puede ser int, String, boolean, etc.)
     */
    public void insertar(String nombre, String tipo, Object valor) {
        simbolos.put(nombre, new Simbolo(nombre, tipo, valor));
    }

    /*
     MÉTODO: EXISTE (VALIDACIÓN)
     El compilador usa esto para saber si cometiste un error.
     Ejemplo: Si escribes "X = 5;" pero nunca creaste X, 
     el compilador pregunta: ¿existe("X")? -> Si devuelve false, lanza error "Variable no declarada".
     */
    public boolean existe(String nombre) {
        return simbolos.containsKey(nombre);
    }

    /*
     MÉTODO: OBTENER SÍMBOLO
     Recupera toda la información de una variable.
     Se usa cuando necesitas saber el TIPO de una variable para validar operaciones.
     Ejemplo: Si intentas sumar "X + Y", el compilador pide los símbolos de X e Y
     para ver si ambos son números.
     */
    public Simbolo getSimbolo(String nombre) {
        return simbolos.get(nombre);
    }

    // Limpia la memoria. Útil si reinicias la compilación sin cerrar el programa.
    public void limpiar() {
        simbolos.clear();
    }

    /*
     CLASE INTERNA: SIMBOLO
     Es una clase auxiliar  que actúa como una "caja".
     Solo sirve para agrupar los tres datos importantes de una variable en un solo objeto.
     */
    public static class Simbolo {
        String nombre;
        String tipo;    // Ej: "ENTERO", "TEXTO", "PILA"
        Object valor;   // Ej: 10, "Hola", null

        public Simbolo(String nombre, String tipo, Object valor) {
            this.nombre = nombre;
            this.tipo = tipo;
            this.valor = valor;
        }
    }
}