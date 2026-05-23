package compilador.codegen.asm;

import java.util.Map;

final class RutinasGraficasEstructuras {
    private RutinasGraficasEstructuras() {
    }

    static void agregar(GeneradorEnsambladorGrafico g, Map<String, String> tipos, Map<String, Integer> tamanos) {
        // Esta clase no dibuja directamente en Java: agrega texto ASM al
        // generador grafico. Por cada estructura declarada en el DSL se emite
        // una subrutina GRAFICAR_<TIPO>_<NOMBRE>, que luego llama GRAFICAR_TODO.
        for (Map.Entry<String, String> e : tipos.entrySet()) {
            String nombre = e.getKey();
            String tipo = e.getValue();
            if ("PILA".equals(tipo)) {
                rutinaGraficaPila(g, nombre);
            } else if ("COLA".equals(tipo)) {
                rutinaGraficaCola(g, nombre, tamanos.getOrDefault(nombre, 100));
            } else if ("LISTA".equals(tipo)) {
                rutinaGraficaLista(g, nombre);
            } else if ("ARBOL".equals(tipo)) {
                rutinaGraficaArbol(g, nombre);
                rutinaRecorridosArbol(g, nombre);
            } else if ("GRAFO".equals(tipo)) {
                rutinaGraficaGrafo(g, nombre);
            } else if ("HASH".equals(tipo)) {
                rutinaGraficaHash(g, nombre);
            }
        }
    }

    private static void rutinaGraficaPila(GeneradorEnsambladorGrafico g, String nombre) {
        // La pila se almacena como arreglo y un contador <nombre>_top.
        // Se recorre desde 0 hasta top-1 y se imprime verticalmente de abajo
        // hacia arriba para que visualmente parezca una pila.
        String loop = nombre + "_gp_loop";
        String fin = nombre + "_gp_fin";
        g.emitir("GRAFICAR_PILA_" + nombre + " proc");
        g.emitir("    mov word ptr [gfx_i], 0");
        g.emitir(loop + ":");
        g.emitir("    mov ax, [gfx_i]");
        g.emitir("    cmp ax, [" + nombre + "_top]");
        g.emitir("    jge " + fin);
        g.emitir("    mov bx, ax");
        g.emitir("    shl bx, 1");
        g.emitir("    mov ax, " + nombre + "[bx]");
        g.emitir("    mov [gfx_valor], ax");
        g.emitir("    mov ax, [gfx_i]");
        g.emitir("    mov bx, 12");
        g.emitir("    mul bx");
        g.emitir("    mov dx, 180");
        g.emitir("    sub dx, ax");
        g.emitir("    mov cx, 10");
        g.emitir("    call SET_CURSOR_PIXEL");
        g.emitir("    call PRINT_VALOR_CORCHETES");
        g.emitir("    inc word ptr [gfx_i]");
        g.emitir("    jmp " + loop);
        g.emitir(fin + ":");
        g.emitir("    ret");
        g.emitir("GRAFICAR_PILA_" + nombre + " endp");
        g.emitir("");
    }

    private static void rutinaGraficaCola(GeneradorEnsambladorGrafico g, String nombre, int capacidad) {
        // La cola usa un arreglo circular: front indica el primer elemento y
        // count dice cuantos valores hay. Si el indice rebasa la capacidad, se
        // resta capacidad para volver al inicio del arreglo.
        String loop = nombre + "_gc_loop";
        String fin = nombre + "_gc_fin";
        g.emitir("GRAFICAR_COLA_" + nombre + " proc");
        g.emitir("    mov word ptr [gfx_i], 0");
        g.emitir(loop + ":");
        g.emitir("    mov ax, [gfx_i]");
        g.emitir("    cmp ax, [" + nombre + "_count]");
        g.emitir("    jge " + fin);
        g.emitir("    mov bx, [" + nombre + "_front]");
        g.emitir("    add bx, ax");
        g.emitir("    cmp bx, " + capacidad);
        g.emitir("    jl " + nombre + "_gc_idx_ok");
        g.emitir("    sub bx, " + capacidad);
        g.emitir(nombre + "_gc_idx_ok:");
        g.emitir("    shl bx, 1");
        g.emitir("    mov ax, " + nombre + "[bx]");
        g.emitir("    mov [gfx_valor], ax");
        g.emitir("    mov ax, [gfx_i]");
        g.emitir("    mov bx, 38");
        g.emitir("    mul bx");
        g.emitir("    mov cx, 70");
        g.emitir("    add cx, ax");
        g.emitir("    mov dx, 20");
        g.emitir("    call SET_CURSOR_PIXEL");
        g.emitir("    call PRINT_VALOR_CORCHETES");
        g.emitir("    inc word ptr [gfx_i]");
        g.emitir("    jmp " + loop);
        g.emitir(fin + ":");
        g.emitir("    ret");
        g.emitir("GRAFICAR_COLA_" + nombre + " endp");
        g.emitir("");
    }

    private static void rutinaGraficaLista(GeneradorEnsambladorGrafico g, String nombre) {
        // La lista enlazada vive en HEAP. Cada nodo ocupa dos palabras:
        // HEAP[nodo] = valor y HEAP[nodo+2] = direccion del siguiente nodo.
        // La rutina avanza con el puntero next hasta llegar a 0.
        String loop = nombre + "_gl_loop";
        String fin = nombre + "_gl_fin";
        g.emitir("GRAFICAR_LISTA_" + nombre + " proc");
        g.emitir("    mov bx, [" + nombre + "_head]");
        g.emitir("    mov word ptr [gfx_i], 0");
        g.emitir(loop + ":");
        g.emitir("    cmp bx, 0");
        g.emitir("    je " + fin);
        g.emitir("    mov ax, HEAP[bx]");
        g.emitir("    mov [gfx_valor], ax");
        g.emitir("    push bx");
        g.emitir("    mov ax, [gfx_i]");
        g.emitir("    mov bx, 42");
        g.emitir("    mul bx");
        g.emitir("    mov cx, 10");
        g.emitir("    add cx, ax");
        g.emitir("    mov dx, 48");
        g.emitir("    call SET_CURSOR_PIXEL");
        g.emitir("    call PRINT_VALOR_CORCHETES");
        g.emitir("    pop bx");
        g.emitir("    mov bx, HEAP[bx+2]");
        g.emitir("    inc word ptr [gfx_i]");
        g.emitir("    jmp " + loop);
        g.emitir(fin + ":");
        g.emitir("    ret");
        g.emitir("GRAFICAR_LISTA_" + nombre + " endp");
        g.emitir("");
    }

    private static void rutinaGraficaArbol(GeneradorEnsambladorGrafico g, String nombre) {
        // El arbol binario tambien usa HEAP, pero cada nodo ocupa tres
        // palabras: valor, hijo izquierdo y hijo derecho. La rutina recursiva
        // recibe en BX el nodo actual, en CX/DX la posicion y en SI la distancia
        // horizontal que separa a los hijos.
        String rec = "GRAFICAR_ARBOL_REC_" + nombre;
        String fin = "GRAFICAR_ARBOL_FIN_" + nombre;
        String retorno = "GRAFICAR_ARBOL_RET_" + nombre;
        String izqOffsetOk = "GRAFICAR_ARBOL_IZQ_OFFSET_OK_" + nombre;
        String derOffsetOk = "GRAFICAR_ARBOL_DER_OFFSET_OK_" + nombre;
        String sinIzq = "GRAFICAR_ARBOL_SIN_IZQ_" + nombre;
        String sinDer = "GRAFICAR_ARBOL_SIN_DER_" + nombre;

        g.emitir("GRAFICAR_ARBOL_" + nombre + " proc");
        g.emitir("    mov bx, [" + nombre + "_root]");
        g.emitir("    mov cx, 150");
        g.emitir("    mov dx, 78");
        g.emitir("    mov si, 60");
        g.emitir("    call " + rec);
        g.emitir("    ret");
        g.emitir("GRAFICAR_ARBOL_" + nombre + " endp");
        g.emitir("");
        g.emitir(rec + " proc");
        g.emitir("    cmp bx, 0");
        g.emitir("    je " + fin);
        g.emitir("    push bx");
        g.emitir("    push cx");
        g.emitir("    push dx");
        g.emitir("    push si");
        g.emitir("    mov ax, HEAP[bx]");
        g.emitir("    mov [gfx_valor], ax");
        g.emitir("    mov si, 28");
        g.emitir("    call SET_CURSOR_PIXEL");
        g.emitir("    mov ax, [gfx_valor]");
        g.emitir("    call PRINT_NUM_GRAFICO");
        g.emitir("    pop si");
        g.emitir("    pop dx");
        g.emitir("    pop cx");
        g.emitir("    pop bx");
        g.emitir("");
        g.emitir("    ; Hijo izquierdo");
        g.emitir("    mov ax, HEAP[bx+2]");
        g.emitir("    cmp ax, 0");
        g.emitir("    je " + sinIzq);
        g.emitir("    push ax");
        g.emitir("    push bx");
        g.emitir("    push cx");
        g.emitir("    push dx");
        g.emitir("    push si");
        g.emitir("    mov ax, si");
        g.emitir("    shr ax, 1");
        g.emitir("    sub cx, ax");
        g.emitir("    add dx, 12");
        g.emitir("    call SET_CURSOR_PIXEL");
        g.emitir("    mov al, '/'");
        g.emitir("    mov ah, 0Eh");
        g.emitir("    int 10h");
        g.emitir("    pop si");
        g.emitir("    pop dx");
        g.emitir("    pop cx");
        g.emitir("    pop bx");
        g.emitir("    pop ax");
        g.emitir("    push bx");
        g.emitir("    push cx");
        g.emitir("    push dx");
        g.emitir("    push si");
        g.emitir("    mov bx, ax");
        g.emitir("    sub cx, si");
        g.emitir("    add dx, 24");
        g.emitir("    shr si, 1");
        g.emitir("    cmp si, 8");
        g.emitir("    jge " + izqOffsetOk);
        g.emitir("    mov si, 8");
        g.emitir(izqOffsetOk + ":");
        g.emitir("    call " + rec);
        g.emitir("    pop si");
        g.emitir("    pop dx");
        g.emitir("    pop cx");
        g.emitir("    pop bx");
        g.emitir(sinIzq + ":");
        g.emitir("");
        g.emitir("    ; Hijo derecho");
        g.emitir("    mov ax, HEAP[bx+4]");
        g.emitir("    cmp ax, 0");
        g.emitir("    je " + sinDer);
        g.emitir("    push ax");
        g.emitir("    push bx");
        g.emitir("    push cx");
        g.emitir("    push dx");
        g.emitir("    push si");
        g.emitir("    mov ax, si");
        g.emitir("    shr ax, 1");
        g.emitir("    add cx, ax");
        g.emitir("    add dx, 12");
        g.emitir("    call SET_CURSOR_PIXEL");
        g.emitir("    mov al, '\\'");
        g.emitir("    mov ah, 0Eh");
        g.emitir("    int 10h");
        g.emitir("    pop si");
        g.emitir("    pop dx");
        g.emitir("    pop cx");
        g.emitir("    pop bx");
        g.emitir("    pop ax");
        g.emitir("    push bx");
        g.emitir("    push cx");
        g.emitir("    push dx");
        g.emitir("    push si");
        g.emitir("    mov bx, ax");
        g.emitir("    add cx, si");
        g.emitir("    add dx, 24");
        g.emitir("    shr si, 1");
        g.emitir("    cmp si, 8");
        g.emitir("    jge " + derOffsetOk);
        g.emitir("    mov si, 8");
        g.emitir(derOffsetOk + ":");
        g.emitir("    call " + rec);
        g.emitir("    pop si");
        g.emitir("    pop dx");
        g.emitir("    pop cx");
        g.emitir("    pop bx");
        g.emitir(sinDer + ":");
        g.emitir("    jmp " + retorno);
        g.emitir(fin + ":");
        g.emitir("    ; Nodo NULL: retorno directo, sin push locales pendientes");
        g.emitir("    ret");
        g.emitir(retorno + ":");
        g.emitir("    ret");
        g.emitir(rec + " endp");
        g.emitir("");
    }

    private static void rutinaRecorridosArbol(GeneradorEnsambladorGrafico g, String nombre) {
        // Ademas del dibujo del arbol, se emiten recorridos clasicos para poder
        // responder operaciones del DSL como PREORDEN, INORDEN, POSTORDEN y
        // RECORRIDOPORNIVELES.
        rutinaRecorridoPreorden(g, nombre);
        rutinaRecorridoInorden(g, nombre);
        rutinaRecorridoPostorden(g, nombre);
        rutinaRecorridoNiveles(g, nombre);
    }

    private static void emitirImprimirNodoRecorrido(GeneradorEnsambladorGrafico g) {
        // Fragmento compartido por los recorridos: imprime el valor del nodo
        // actual y despues un espacio para separar la secuencia en pantalla.
        g.emitir("    mov ax, HEAP[bx]");
        g.emitir("    call PRINT_NUM_GRAFICO");
        g.emitir("    call PRINT_ESPACIO_GRAFICO");
    }

    private static void rutinaRecorridoPreorden(GeneradorEnsambladorGrafico g, String nombre) {
        // Preorden: primero visita la raiz, luego el subarbol izquierdo y por
        // ultimo el derecho.
        String proc = "RECORRIDO_PREORDEN_" + nombre;
        String fin = proc + "_FIN";
        g.emitir(proc + " proc");
        g.emitir("    cmp bx, 0");
        g.emitir("    je " + fin);
        emitirImprimirNodoRecorrido(g);
        g.emitir("    push bx");
        g.emitir("    mov bx, HEAP[bx+2]");
        g.emitir("    call " + proc);
        g.emitir("    pop bx");
        g.emitir("    mov bx, HEAP[bx+4]");
        g.emitir("    call " + proc);
        g.emitir(fin + ":");
        g.emitir("    ret");
        g.emitir(proc + " endp");
        g.emitir("");
    }

    private static void rutinaRecorridoInorden(GeneradorEnsambladorGrafico g, String nombre) {
        // Inorden: primero el hijo izquierdo, luego la raiz y finalmente el
        // hijo derecho. En un arbol binario de busqueda produce valores ordenados.
        String proc = "RECORRIDO_INORDEN_" + nombre;
        String fin = proc + "_FIN";
        g.emitir(proc + " proc");
        g.emitir("    cmp bx, 0");
        g.emitir("    je " + fin);
        g.emitir("    push bx");
        g.emitir("    mov bx, HEAP[bx+2]");
        g.emitir("    call " + proc);
        g.emitir("    pop bx");
        emitirImprimirNodoRecorrido(g);
        g.emitir("    mov bx, HEAP[bx+4]");
        g.emitir("    call " + proc);
        g.emitir(fin + ":");
        g.emitir("    ret");
        g.emitir(proc + " endp");
        g.emitir("");
    }

    private static void rutinaRecorridoPostorden(GeneradorEnsambladorGrafico g, String nombre) {
        // Postorden: procesa los dos hijos antes de imprimir la raiz.
        String proc = "RECORRIDO_POSTORDEN_" + nombre;
        String fin = proc + "_FIN";
        g.emitir(proc + " proc");
        g.emitir("    cmp bx, 0");
        g.emitir("    je " + fin);
        g.emitir("    push bx");
        g.emitir("    mov bx, HEAP[bx+2]");
        g.emitir("    call " + proc);
        g.emitir("    pop bx");
        g.emitir("    push bx");
        g.emitir("    mov bx, HEAP[bx+4]");
        g.emitir("    call " + proc);
        g.emitir("    pop bx");
        emitirImprimirNodoRecorrido(g);
        g.emitir(fin + ":");
        g.emitir("    ret");
        g.emitir(proc + " endp");
        g.emitir("");
    }

    private static void rutinaRecorridoNiveles(GeneradorEnsambladorGrafico g, String nombre) {
        // Recorrido por niveles: usa una cola auxiliar gfx_queue para visitar
        // el arbol de arriba hacia abajo y de izquierda a derecha.
        String proc = "RECORRIDO_NIVELES_" + nombre;
        String loop = proc + "_LOOP";
        String sinIzq = proc + "_SIN_IZQ";
        String sinDer = proc + "_SIN_DER";
        String fin = proc + "_FIN";
        g.emitir(proc + " proc");
        g.emitir("    cmp bx, 0");
        g.emitir("    je " + fin);
        g.emitir("    mov word ptr [gfx_q_front], 0");
        g.emitir("    mov word ptr [gfx_q_rear], 0");
        g.emitir("    mov si, [gfx_q_rear]");
        g.emitir("    shl si, 1");
        g.emitir("    mov gfx_queue[si], bx");
        g.emitir("    inc word ptr [gfx_q_rear]");
        g.emitir(loop + ":");
        g.emitir("    mov ax, [gfx_q_front]");
        g.emitir("    cmp ax, [gfx_q_rear]");
        g.emitir("    jge " + fin);
        g.emitir("    mov si, ax");
        g.emitir("    shl si, 1");
        g.emitir("    mov bx, gfx_queue[si]");
        g.emitir("    inc word ptr [gfx_q_front]");
        emitirImprimirNodoRecorrido(g);
        g.emitir("    mov ax, HEAP[bx+2]");
        g.emitir("    cmp ax, 0");
        g.emitir("    je " + sinIzq);
        g.emitir("    mov si, [gfx_q_rear]");
        g.emitir("    cmp si, 128");
        g.emitir("    jge " + sinIzq);
        g.emitir("    shl si, 1");
        g.emitir("    mov gfx_queue[si], ax");
        g.emitir("    inc word ptr [gfx_q_rear]");
        g.emitir(sinIzq + ":");
        g.emitir("    mov ax, HEAP[bx+4]");
        g.emitir("    cmp ax, 0");
        g.emitir("    je " + sinDer);
        g.emitir("    mov si, [gfx_q_rear]");
        g.emitir("    cmp si, 128");
        g.emitir("    jge " + sinDer);
        g.emitir("    shl si, 1");
        g.emitir("    mov gfx_queue[si], ax");
        g.emitir("    inc word ptr [gfx_q_rear]");
        g.emitir(sinDer + ":");
        g.emitir("    jmp " + loop);
        g.emitir(fin + ":");
        g.emitir("    ret");
        g.emitir(proc + " endp");
        g.emitir("");
    }

    private static void rutinaGraficaGrafo(GeneradorEnsambladorGrafico g, String nombre) {
        // El grafo se representa con dos listas: una de nodos y otra de aristas.
        // Las aristas se guardan en dos arreglos paralelos: from[i] -> to[i].
        // La visualizacion textual muestra primero los nodos y luego cada arista.
        String loopNodos = nombre + "_gg_nodos_loop";
        String sinConector = nombre + "_gg_sin_conector";
        String finNodos = nombre + "_gg_nodos_fin";
        String loopAristas = nombre + "_gg_aristas_loop";
        String finAristas = nombre + "_gg_aristas_fin";
        g.emitir("GRAFICAR_GRAFO_" + nombre + " proc");
        g.emitir("    mov cx, 15");
        g.emitir("    mov dx, 120");
        g.emitir("    call SET_CURSOR_PIXEL");
        g.emitirTextoGrafico("NODOS: ");
        g.emitir("    mov word ptr [gfx_i], 0");
        g.emitir(loopNodos + ":");
        g.emitir("    mov ax, [gfx_i]");
        g.emitir("    cmp ax, [" + nombre + "_node_count]");
        g.emitir("    jge " + finNodos);
        g.emitir("    mov bx, ax");
        g.emitir("    shl bx, 1");
        g.emitir("    mov ax, " + nombre + "_nodes[bx]");
        g.emitir("    mov [gfx_valor], ax");
        g.emitir("    call PRINT_VALOR_CORCHETES");
        g.emitir("    mov ax, [gfx_i]");
        g.emitir("    inc ax");
        g.emitir("    cmp ax, [" + nombre + "_node_count]");
        g.emitir("    jge " + sinConector);
        g.emitirTextoGrafico(" -- ");
        g.emitir(sinConector + ":");
        g.emitir("    inc word ptr [gfx_i]");
        g.emitir("    jmp " + loopNodos);
        g.emitir(finNodos + ":");
        g.emitir("");
        g.emitir("    mov cx, 15");
        g.emitir("    mov dx, 136");
        g.emitir("    call SET_CURSOR_PIXEL");
        g.emitirTextoGrafico("ARISTAS:");
        g.emitir("    mov word ptr [gfx_i], 0");
        g.emitir(loopAristas + ":");
        g.emitir("    mov ax, [gfx_i]");
        g.emitir("    cmp ax, [" + nombre + "_edge_count]");
        g.emitir("    jge " + finAristas);
        g.emitir("    mov bx, 10");
        g.emitir("    mul bx");
        g.emitir("    mov dx, 148");
        g.emitir("    add dx, ax");
        g.emitir("    mov cx, 15");
        g.emitir("    call SET_CURSOR_PIXEL");
        g.emitir("    mov bx, [gfx_i]");
        g.emitir("    shl bx, 1");
        g.emitir("    mov ax, " + nombre + "_edges_from[bx]");
        g.emitir("    mov [gfx_valor], ax");
        g.emitir("    call PRINT_VALOR_CORCHETES");
        g.emitirTextoGrafico(" -> ");
        g.emitir("    mov bx, [gfx_i]");
        g.emitir("    shl bx, 1");
        g.emitir("    mov ax, " + nombre + "_edges_to[bx]");
        g.emitir("    mov [gfx_valor], ax");
        g.emitir("    call PRINT_VALOR_CORCHETES");
        g.emitir("    inc word ptr [gfx_i]");
        g.emitir("    jmp " + loopAristas);
        g.emitir(finAristas + ":");
        g.emitir("    ret");
        g.emitir("GRAFICAR_GRAFO_" + nombre + " endp");
        g.emitir("");
    }

    private static void rutinaGraficaHash(GeneradorEnsambladorGrafico g, String nombre) {
        // La tabla hash se dibuja como tabla: indice, clave y valor. Para que
        // quepa en la pantalla grafica de 320x200, solo se muestran las primeras
        // 8 entradas aunque internamente pueda haber mas capacidad.
        String loop = nombre + "_gh_loop";
        String fin = nombre + "_gh_fin";
        String finBorde = nombre + "_gh_fin_borde";
        g.emitir("GRAFICAR_HASH_" + nombre + " proc");
        g.emitir("    mov cx, 104");
        g.emitir("    mov dx, 104");
        g.emitir("    call SET_CURSOR_PIXEL");
        g.emitirTextoGrafico("+--------+-------+-------+");
        g.emitir("    mov cx, 104");
        g.emitir("    mov dx, 112");
        g.emitir("    call SET_CURSOR_PIXEL");
        g.emitirTextoGrafico("| INDICE | CLAVE | VALOR |");
        g.emitir("    mov cx, 104");
        g.emitir("    mov dx, 120");
        g.emitir("    call SET_CURSOR_PIXEL");
        g.emitirTextoGrafico("+--------+-------+-------+");
        g.emitir("    mov word ptr [gfx_i], 0");
        g.emitir(loop + ":");
        g.emitir("    mov ax, [gfx_i]");
        g.emitir("    cmp ax, [" + nombre + "_count]");
        g.emitir("    jge " + fin);
        g.emitir("    cmp ax, 8");
        g.emitir("    jge " + fin);
        g.emitir("    mov ax, [gfx_i]");
        g.emitir("    mov bx, 8");
        g.emitir("    mul bx");
        g.emitir("    mov dx, 128");
        g.emitir("    add dx, ax");
        g.emitir("    mov cx, 104");
        g.emitir("    call SET_CURSOR_PIXEL");
        g.emitirTextoGrafico("|        |       |       |");
        g.emitir("    mov ax, [gfx_i]");
        g.emitir("    mov bx, 8");
        g.emitir("    mul bx");
        g.emitir("    mov dx, 128");
        g.emitir("    add dx, ax");
        g.emitir("    mov cx, 136");
        g.emitir("    call SET_CURSOR_PIXEL");
        g.emitir("    mov ax, [gfx_i]");
        g.emitir("    inc ax");
        g.emitir("    call PRINT_NUM_GRAFICO");
        g.emitir("    mov ax, [gfx_i]");
        g.emitir("    mov si, 8");
        g.emitir("    mul si");
        g.emitir("    mov dx, 128");
        g.emitir("    add dx, ax");
        g.emitir("    mov cx, 200");
        g.emitir("    call SET_CURSOR_PIXEL");
        g.emitir("    mov bx, [gfx_i]");
        g.emitir("    shl bx, 1");
        g.emitir("    mov ax, " + nombre + "_keys[bx]");
        g.emitir("    call PRINT_NUM_GRAFICO");
        g.emitir("    mov ax, [gfx_i]");
        g.emitir("    mov si, 8");
        g.emitir("    mul si");
        g.emitir("    mov dx, 128");
        g.emitir("    add dx, ax");
        g.emitir("    mov cx, 264");
        g.emitir("    call SET_CURSOR_PIXEL");
        g.emitir("    mov bx, [gfx_i]");
        g.emitir("    shl bx, 1");
        g.emitir("    mov ax, " + nombre + "_values[bx]");
        g.emitir("    call PRINT_NUM_GRAFICO");
        g.emitir("    inc word ptr [gfx_i]");
        g.emitir("    jmp " + loop);
        g.emitir(fin + ":");
        g.emitir("    mov ax, [" + nombre + "_count]");
        g.emitir("    cmp ax, 8");
        g.emitir("    jle " + finBorde);
        g.emitir("    mov ax, 8");
        g.emitir(finBorde + ":");
        g.emitir("    mov bx, 8");
        g.emitir("    mul bx");
        g.emitir("    mov dx, 128");
        g.emitir("    add dx, ax");
        g.emitir("    mov cx, 104");
        g.emitir("    call SET_CURSOR_PIXEL");
        g.emitirTextoGrafico("+--------+-------+-------+");
        g.emitir("    ret");
        g.emitir("GRAFICAR_HASH_" + nombre + " endp");
        g.emitir("");
    }
}
