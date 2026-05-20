; ============================================
; CODIGO ENSAMBLADOR GENERADO - DSL
; Arquitectura objetivo: Intel 8086 / DOS
; ============================================

.model small
.stack 100h

.data
    titulo db 'EJECUCION DE PROGRAMA DSL', 0Dh, 0Ah, '$'
    newline db 0Dh, 0Ah, '$'
    msg_error db 'Error en la operacion', 0Dh, 0Ah, '$'

    HEAP dw 1000 dup(0)
    HEAP_PTR dw 2
    miPila dw 100 dup(0)
    miPila_top dw 0
    miCola dw 50 dup(0)
    miCola_front dw 0
    miCola_rear dw 0
    miCola_count dw 0
    miLista_head dw 0
    miLista_tail dw 0
    miArbol_root dw 0
    miGrafo_nodes dw 100 dup(0)
    miGrafo_node_count dw 0
    miGrafo_edges_from dw 100 dup(0)
    miGrafo_edges_to dw 100 dup(0)
    miGrafo_edge_count dw 0
    miHash_keys dw 100 dup(0)
    miHash_values dw 100 dup(0)
    miHash_count dw 0
    T1 dw 0
    T2 dw 0
    T3 dw 0
    T4 dw 0
    T5 dw 0
    T6 dw 0
    T7 dw 0
    T8 dw 0
    resultado dw 0
    T10 dw 0
    T12 dw 0
    T14 dw 0
    T16 dw 0
    temp dw 0
    T17 dw 0
    T18 dw 0
    T19 dw 0
    T20 dw 0
    valorTope dw 0
    T21 dw 0
    T22 dw 0

.code
main proc
    mov ax, @data
    mov ds, ax
    mov dx, offset titulo
    call print_string

    ; CREAR PILA miPila TAMANO 100
    ; CREAR COLA miCola TAMANO 50
    ; APILAR 1 EN miPila
    mov ax, 1
    mov bx, [miPila_top]
    shl bx, 1
    mov miPila[bx], ax
    inc word ptr [miPila_top]
    ; APILAR 2 EN miPila
    mov ax, 2
    mov bx, [miPila_top]
    shl bx, 1
    mov miPila[bx], ax
    inc word ptr [miPila_top]
    ; APILAR 3 EN miPila
    mov ax, 3
    mov bx, [miPila_top]
    shl bx, 1
    mov miPila[bx], ax
    inc word ptr [miPila_top]
    ; TOPE EN miPila
    cmp word ptr [miPila_top], 0
    je ASM_L1
    mov bx, [miPila_top]
    dec bx
    shl bx, 1
    mov ax, miPila[bx]
    mov [T1], ax
    jmp ASM_L2
ASM_L1:
    mov word ptr [T1], 0
ASM_L2:
    ; PRINT T1
    mov ax, [T1]
    call print_num
    mov dx, offset newline
    call print_string
    ; VACIA
    mov word ptr [T2], 0
    cmp word ptr [miPila_top], 0
    je ASM_L3
    jmp ASM_L4
ASM_L3:
    mov word ptr [T2], 1
ASM_L4:
    ; IF_FALSE T2 GOTO L1
    mov ax, [T2]
    cmp ax, 0
    je L1
    ; ERROR: Estructura vacía 
    mov dx, offset msg_error
    call print_string
L1:
    ; DESAPILAR EN miPila
    cmp word ptr [miPila_top], 0
    je ASM_L5
    dec word ptr [miPila_top]
    mov bx, [miPila_top]
    shl bx, 1
    mov word ptr miPila[bx], 0
ASM_L5:
    ; TOPE EN miPila
    cmp word ptr [miPila_top], 0
    je ASM_L6
    mov bx, [miPila_top]
    dec bx
    shl bx, 1
    mov ax, miPila[bx]
    mov [T3], ax
    jmp ASM_L7
ASM_L6:
    mov word ptr [T3], 0
ASM_L7:
    ; PRINT T3
    mov ax, [T3]
    call print_num
    mov dx, offset newline
    call print_string
    ; CREAR LISTA miLista
    ; INSERTAR_FINAL 5 EN miLista (nodo HEAP: valor, sig)
    mov ax, 5
    mov si, [HEAP_PTR]
    add word ptr [HEAP_PTR], 4
    mov HEAP[si], ax
    mov word ptr HEAP[si+2], 0
    cmp word ptr [miLista_head], 0
    jne ASM_L8
    mov [miLista_head], si
    mov [miLista_tail], si
    jmp ASM_L9
ASM_L8:
    mov bx, [miLista_tail]
    mov HEAP[bx+2], si
    mov [miLista_tail], si
ASM_L9:
    ; INSERTAR_FINAL 10 EN miLista (nodo HEAP: valor, sig)
    mov ax, 10
    mov si, [HEAP_PTR]
    add word ptr [HEAP_PTR], 4
    mov HEAP[si], ax
    mov word ptr HEAP[si+2], 0
    cmp word ptr [miLista_head], 0
    jne ASM_L10
    mov [miLista_head], si
    mov [miLista_tail], si
    jmp ASM_L11
ASM_L10:
    mov bx, [miLista_tail]
    mov HEAP[bx+2], si
    mov [miLista_tail], si
ASM_L11:
    ; INSERTAR_FINAL 15 EN miLista (nodo HEAP: valor, sig)
    mov ax, 15
    mov si, [HEAP_PTR]
    add word ptr [HEAP_PTR], 4
    mov HEAP[si], ax
    mov word ptr HEAP[si+2], 0
    cmp word ptr [miLista_head], 0
    jne ASM_L12
    mov [miLista_head], si
    mov [miLista_tail], si
    jmp ASM_L13
ASM_L12:
    mov bx, [miLista_tail]
    mov HEAP[bx+2], si
    mov [miLista_tail], si
ASM_L13:
    ; CREAR ARBOL_BINARIO miArbol
    ; AGREGARNODO 25 EN miArbol (nodo HEAP: valor, izq, der)
    mov ax, 25
    mov si, [HEAP_PTR]
    add word ptr [HEAP_PTR], 6
    mov HEAP[si], ax
    mov word ptr HEAP[si+2], 0
    mov word ptr HEAP[si+4], 0
    cmp word ptr [miArbol_root], 0
    jne ASM_L14
    mov [miArbol_root], si
    jmp ASM_L19
ASM_L14:
    mov bx, [miArbol_root]
ASM_L15:
    cmp ax, HEAP[bx]
    jg ASM_L16
    cmp word ptr HEAP[bx+2], 0
    je ASM_L17
    mov bx, HEAP[bx+2]
    jmp ASM_L15
ASM_L17:
    mov HEAP[bx+2], si
    jmp ASM_L19
ASM_L16:
    cmp word ptr HEAP[bx+4], 0
    je ASM_L18
    mov bx, HEAP[bx+4]
    jmp ASM_L15
ASM_L18:
    mov HEAP[bx+4], si
ASM_L19:
    ; AGREGARNODO 15 EN miArbol (nodo HEAP: valor, izq, der)
    mov ax, 15
    mov si, [HEAP_PTR]
    add word ptr [HEAP_PTR], 6
    mov HEAP[si], ax
    mov word ptr HEAP[si+2], 0
    mov word ptr HEAP[si+4], 0
    cmp word ptr [miArbol_root], 0
    jne ASM_L20
    mov [miArbol_root], si
    jmp ASM_L25
ASM_L20:
    mov bx, [miArbol_root]
ASM_L21:
    cmp ax, HEAP[bx]
    jg ASM_L22
    cmp word ptr HEAP[bx+2], 0
    je ASM_L23
    mov bx, HEAP[bx+2]
    jmp ASM_L21
ASM_L23:
    mov HEAP[bx+2], si
    jmp ASM_L25
ASM_L22:
    cmp word ptr HEAP[bx+4], 0
    je ASM_L24
    mov bx, HEAP[bx+4]
    jmp ASM_L21
ASM_L24:
    mov HEAP[bx+4], si
ASM_L25:
    ; AGREGARNODO 35 EN miArbol (nodo HEAP: valor, izq, der)
    mov ax, 35
    mov si, [HEAP_PTR]
    add word ptr [HEAP_PTR], 6
    mov HEAP[si], ax
    mov word ptr HEAP[si+2], 0
    mov word ptr HEAP[si+4], 0
    cmp word ptr [miArbol_root], 0
    jne ASM_L26
    mov [miArbol_root], si
    jmp ASM_L31
ASM_L26:
    mov bx, [miArbol_root]
ASM_L27:
    cmp ax, HEAP[bx]
    jg ASM_L28
    cmp word ptr HEAP[bx+2], 0
    je ASM_L29
    mov bx, HEAP[bx+2]
    jmp ASM_L27
ASM_L29:
    mov HEAP[bx+2], si
    jmp ASM_L31
ASM_L28:
    cmp word ptr HEAP[bx+4], 0
    je ASM_L30
    mov bx, HEAP[bx+4]
    jmp ASM_L27
ASM_L30:
    mov HEAP[bx+4], si
ASM_L31:
    ; AGREGARNODO 10 EN miArbol (nodo HEAP: valor, izq, der)
    mov ax, 10
    mov si, [HEAP_PTR]
    add word ptr [HEAP_PTR], 6
    mov HEAP[si], ax
    mov word ptr HEAP[si+2], 0
    mov word ptr HEAP[si+4], 0
    cmp word ptr [miArbol_root], 0
    jne ASM_L32
    mov [miArbol_root], si
    jmp ASM_L37
ASM_L32:
    mov bx, [miArbol_root]
ASM_L33:
    cmp ax, HEAP[bx]
    jg ASM_L34
    cmp word ptr HEAP[bx+2], 0
    je ASM_L35
    mov bx, HEAP[bx+2]
    jmp ASM_L33
ASM_L35:
    mov HEAP[bx+2], si
    jmp ASM_L37
ASM_L34:
    cmp word ptr HEAP[bx+4], 0
    je ASM_L36
    mov bx, HEAP[bx+4]
    jmp ASM_L33
ASM_L36:
    mov HEAP[bx+4], si
ASM_L37:
    ; AGREGARNODO 20 EN miArbol (nodo HEAP: valor, izq, der)
    mov ax, 20
    mov si, [HEAP_PTR]
    add word ptr [HEAP_PTR], 6
    mov HEAP[si], ax
    mov word ptr HEAP[si+2], 0
    mov word ptr HEAP[si+4], 0
    cmp word ptr [miArbol_root], 0
    jne ASM_L38
    mov [miArbol_root], si
    jmp ASM_L43
ASM_L38:
    mov bx, [miArbol_root]
ASM_L39:
    cmp ax, HEAP[bx]
    jg ASM_L40
    cmp word ptr HEAP[bx+2], 0
    je ASM_L41
    mov bx, HEAP[bx+2]
    jmp ASM_L39
ASM_L41:
    mov HEAP[bx+2], si
    jmp ASM_L43
ASM_L40:
    cmp word ptr HEAP[bx+4], 0
    je ASM_L42
    mov bx, HEAP[bx+4]
    jmp ASM_L39
ASM_L42:
    mov HEAP[bx+4], si
ASM_L43:
    ; AGREGARNODO 30 EN miArbol (nodo HEAP: valor, izq, der)
    mov ax, 30
    mov si, [HEAP_PTR]
    add word ptr [HEAP_PTR], 6
    mov HEAP[si], ax
    mov word ptr HEAP[si+2], 0
    mov word ptr HEAP[si+4], 0
    cmp word ptr [miArbol_root], 0
    jne ASM_L44
    mov [miArbol_root], si
    jmp ASM_L49
ASM_L44:
    mov bx, [miArbol_root]
ASM_L45:
    cmp ax, HEAP[bx]
    jg ASM_L46
    cmp word ptr HEAP[bx+2], 0
    je ASM_L47
    mov bx, HEAP[bx+2]
    jmp ASM_L45
ASM_L47:
    mov HEAP[bx+2], si
    jmp ASM_L49
ASM_L46:
    cmp word ptr HEAP[bx+4], 0
    je ASM_L48
    mov bx, HEAP[bx+4]
    jmp ASM_L45
ASM_L48:
    mov HEAP[bx+4], si
ASM_L49:
    ; AGREGARNODO 40 EN miArbol (nodo HEAP: valor, izq, der)
    mov ax, 40
    mov si, [HEAP_PTR]
    add word ptr [HEAP_PTR], 6
    mov HEAP[si], ax
    mov word ptr HEAP[si+2], 0
    mov word ptr HEAP[si+4], 0
    cmp word ptr [miArbol_root], 0
    jne ASM_L50
    mov [miArbol_root], si
    jmp ASM_L55
ASM_L50:
    mov bx, [miArbol_root]
ASM_L51:
    cmp ax, HEAP[bx]
    jg ASM_L52
    cmp word ptr HEAP[bx+2], 0
    je ASM_L53
    mov bx, HEAP[bx+2]
    jmp ASM_L51
ASM_L53:
    mov HEAP[bx+2], si
    jmp ASM_L55
ASM_L52:
    cmp word ptr HEAP[bx+4], 0
    je ASM_L54
    mov bx, HEAP[bx+4]
    jmp ASM_L51
ASM_L54:
    mov HEAP[bx+4], si
ASM_L55:
    ; PREORDEN EN miArbol recorriendo enlaces HEAP
    mov bx, [miArbol_root]
    xor cx, cx
    mov word ptr [T4], 0
ASM_L56:
    cmp bx, 0
    je ASM_L57
    inc cx
    mov ax, HEAP[bx]
    mov [T4], ax
    mov bx, HEAP[bx+4]
    jmp ASM_L56
ASM_L57:
    ; PRINT T4
    mov ax, [T4]
    call print_num
    mov dx, offset newline
    call print_string
    ; INORDEN EN miArbol recorriendo enlaces HEAP
    mov bx, [miArbol_root]
    xor cx, cx
    mov word ptr [T5], 0
ASM_L58:
    cmp bx, 0
    je ASM_L59
    inc cx
    mov ax, HEAP[bx]
    mov [T5], ax
    mov bx, HEAP[bx+2]
    jmp ASM_L58
ASM_L59:
    ; PRINT T5
    mov ax, [T5]
    call print_num
    mov dx, offset newline
    call print_string
    ; POSTORDEN EN miArbol recorriendo enlaces HEAP
    mov bx, [miArbol_root]
    xor cx, cx
    mov word ptr [T6], 0
ASM_L60:
    cmp bx, 0
    je ASM_L61
    inc cx
    mov ax, HEAP[bx]
    mov [T6], ax
    mov bx, HEAP[bx+4]
    jmp ASM_L60
ASM_L61:
    ; PRINT T6
    mov ax, [T6]
    call print_num
    mov dx, offset newline
    call print_string
    ; RECORRIDOPORNIVELES EN miArbol recorriendo enlaces HEAP
    mov bx, [miArbol_root]
    xor cx, cx
    mov word ptr [T7], 0
ASM_L62:
    cmp bx, 0
    je ASM_L63
    inc cx
    mov ax, HEAP[bx]
    mov [T7], ax
    mov bx, HEAP[bx+4]
    jmp ASM_L62
ASM_L63:
    ; PRINT T7
    mov ax, [T7]
    call print_num
    mov dx, offset newline
    call print_string
    ; CREAR GRAFO miGrafo CAPACIDAD 100
    ; AGREGARNODO 1 EN miGrafo
    cmp word ptr [miGrafo_node_count], 100
    jge ASM_L64
    mov ax, 1
    mov bx, [miGrafo_node_count]
    shl bx, 1
    mov miGrafo_nodes[bx], ax
    inc word ptr [miGrafo_node_count]
ASM_L64:
    ; AGREGARNODO 2 EN miGrafo
    cmp word ptr [miGrafo_node_count], 100
    jge ASM_L65
    mov ax, 2
    mov bx, [miGrafo_node_count]
    shl bx, 1
    mov miGrafo_nodes[bx], ax
    inc word ptr [miGrafo_node_count]
ASM_L65:
    ; AGREGARNODO 3 EN miGrafo
    cmp word ptr [miGrafo_node_count], 100
    jge ASM_L66
    mov ax, 3
    mov bx, [miGrafo_node_count]
    shl bx, 1
    mov miGrafo_nodes[bx], ax
    inc word ptr [miGrafo_node_count]
ASM_L66:
    ; AGREGARNODO 4 EN miGrafo
    cmp word ptr [miGrafo_node_count], 100
    jge ASM_L67
    mov ax, 4
    mov bx, [miGrafo_node_count]
    shl bx, 1
    mov miGrafo_nodes[bx], ax
    inc word ptr [miGrafo_node_count]
ASM_L67:
    ; AGREGARNODO 5 EN miGrafo
    cmp word ptr [miGrafo_node_count], 100
    jge ASM_L68
    mov ax, 5
    mov bx, [miGrafo_node_count]
    shl bx, 1
    mov miGrafo_nodes[bx], ax
    inc word ptr [miGrafo_node_count]
ASM_L68:
    ; AGREGARARISTA 1 2 EN miGrafo
    cmp word ptr [miGrafo_edge_count], 100
    jge ASM_L69
    mov bx, [miGrafo_edge_count]
    shl bx, 1
    mov ax, 1
    mov miGrafo_edges_from[bx], ax
    mov ax, 2
    mov miGrafo_edges_to[bx], ax
    inc word ptr [miGrafo_edge_count]
ASM_L69:
    ; AGREGARARISTA 2 3 EN miGrafo
    cmp word ptr [miGrafo_edge_count], 100
    jge ASM_L70
    mov bx, [miGrafo_edge_count]
    shl bx, 1
    mov ax, 2
    mov miGrafo_edges_from[bx], ax
    mov ax, 3
    mov miGrafo_edges_to[bx], ax
    inc word ptr [miGrafo_edge_count]
ASM_L70:
    ; AGREGARARISTA 3 4 EN miGrafo
    cmp word ptr [miGrafo_edge_count], 100
    jge ASM_L71
    mov bx, [miGrafo_edge_count]
    shl bx, 1
    mov ax, 3
    mov miGrafo_edges_from[bx], ax
    mov ax, 4
    mov miGrafo_edges_to[bx], ax
    inc word ptr [miGrafo_edge_count]
ASM_L71:
    ; AGREGARARISTA 4 5 EN miGrafo
    cmp word ptr [miGrafo_edge_count], 100
    jge ASM_L72
    mov bx, [miGrafo_edge_count]
    shl bx, 1
    mov ax, 4
    mov miGrafo_edges_from[bx], ax
    mov ax, 5
    mov miGrafo_edges_to[bx], ax
    inc word ptr [miGrafo_edge_count]
ASM_L72:
    ; AGREGARARISTA 1 5 EN miGrafo
    cmp word ptr [miGrafo_edge_count], 100
    jge ASM_L73
    mov bx, [miGrafo_edge_count]
    shl bx, 1
    mov ax, 1
    mov miGrafo_edges_from[bx], ax
    mov ax, 5
    mov miGrafo_edges_to[bx], ax
    inc word ptr [miGrafo_edge_count]
ASM_L73:
    ; VECINOS EN 1
    mov ax, 0
    mov [T8], ax
    ; PRINT T8
    mov ax, [T8]
    call print_num
    mov dx, offset newline
    call print_string
    ; CREAR TABLA_HASH miHash CAPACIDAD 100
    ; INSERTAR 101 1000 EN miHash
    cmp word ptr [miHash_count], 100
    jge ASM_L74
    mov bx, [miHash_count]
    shl bx, 1
    mov ax, 101
    mov miHash_keys[bx], ax
    mov ax, 1000
    mov miHash_values[bx], ax
    inc word ptr [miHash_count]
ASM_L74:
    ; INSERTAR 102 2000 EN miHash
    cmp word ptr [miHash_count], 100
    jge ASM_L75
    mov bx, [miHash_count]
    shl bx, 1
    mov ax, 102
    mov miHash_keys[bx], ax
    mov ax, 2000
    mov miHash_values[bx], ax
    inc word ptr [miHash_count]
ASM_L75:
    ; INSERTAR 103 3000 EN miHash
    cmp word ptr [miHash_count], 100
    jge ASM_L76
    mov bx, [miHash_count]
    shl bx, 1
    mov ax, 103
    mov miHash_keys[bx], ax
    mov ax, 3000
    mov miHash_values[bx], ax
    inc word ptr [miHash_count]
ASM_L76:
    ; resultado = 0
    mov ax, 0
    mov [resultado], ax
    ; T10 = 5 + 6
    mov ax, 5
    mov bx, 6
    add ax, bx
    mov [T10], ax
    ; resultado = T10
    mov ax, [T10]
    mov [resultado], ax
    ; T12 = 8 * 2
    mov ax, 8
    mov bx, 2
    imul bx
    mov [T12], ax
    ; resultado = T12
    mov ax, [T12]
    mov [resultado], ax
    ; T14 = 5 + 3
    mov ax, 5
    mov bx, 3
    add ax, bx
    mov [T14], ax
    ; resultado = T14
    mov ax, [T14]
    mov [resultado], ax
    ; T16 = 10 * 2
    mov ax, 10
    mov bx, 2
    imul bx
    mov [T16], ax
    ; resultado = T16
    mov ax, [T16]
    mov [resultado], ax
    ; PRINT resultado
    mov ax, [resultado]
    call print_num
    mov dx, offset newline
    call print_string
    ; temp = 0
    mov ax, 0
    mov [temp], ax
    ; TOPE EN miPila
    cmp word ptr [miPila_top], 0
    je ASM_L77
    mov bx, [miPila_top]
    dec bx
    shl bx, 1
    mov ax, miPila[bx]
    mov [T17], ax
    jmp ASM_L78
ASM_L77:
    mov word ptr [T17], 0
ASM_L78:
    ; temp = T17
    mov ax, [T17]
    mov [temp], ax
    ; T18 = temp + 1
    mov ax, [temp]
    mov bx, 1
    add ax, bx
    mov [T18], ax
    ; temp = T18
    mov ax, [T18]
    mov [temp], ax
    ; PRINT temp
    mov ax, [temp]
    call print_num
    mov dx, offset newline
    call print_string
    ; VACIA
    mov word ptr [T19], 0
    cmp word ptr [miPila_top], 0
    je ASM_L79
    jmp ASM_L80
ASM_L79:
    mov word ptr [T19], 1
ASM_L80:
    ; IF_FALSE T19 GOTO L2
    mov ax, [T19]
    cmp ax, 0
    je L2
    ; PRINT 0
    mov ax, 0
    call print_num
    mov dx, offset newline
    call print_string
L2:
    ; VACIA
    mov word ptr [T20], 0
    cmp word ptr [miCola_count], 0
    je ASM_L81
    jmp ASM_L82
ASM_L81:
    mov word ptr [T20], 1
ASM_L82:
    ; IF_FALSE T20 GOTO L4
    mov ax, [T20]
    cmp ax, 0
    je L4
    ; PRINT 0
    mov ax, 0
    call print_num
    mov dx, offset newline
    call print_string
L4:
    ; ELIMINAR_FINAL EN miLista usando punteros HEAP
    cmp word ptr [miLista_head], 0
    je ASM_L86
    mov bx, [miLista_head]
    cmp bx, [miLista_tail]
    je ASM_L83
ASM_L84:
    mov si, HEAP[bx+2]
    cmp si, [miLista_tail]
    je ASM_L85
    cmp si, 0
    je ASM_L86
    mov bx, si
    jmp ASM_L84
ASM_L85:
    mov word ptr HEAP[bx+2], 0
    mov [miLista_tail], bx
    jmp ASM_L86
ASM_L83:
    mov word ptr [miLista_head], 0
    mov word ptr [miLista_tail], 0
ASM_L86:
    ; ELIMINAR_INICIO EN miLista usando punteros HEAP
    cmp word ptr [miLista_head], 0
    je ASM_L87
    mov bx, [miLista_head]
    mov ax, HEAP[bx+2]
    mov [miLista_head], ax
    cmp ax, 0
    jne ASM_L87
    mov word ptr [miLista_tail], 0
ASM_L87:
    ; INSERTAR 200 999 EN miHash
    cmp word ptr [miHash_count], 100
    jge ASM_L88
    mov bx, [miHash_count]
    shl bx, 1
    mov ax, 200
    mov miHash_keys[bx], ax
    mov ax, 999
    mov miHash_values[bx], ax
    inc word ptr [miHash_count]
ASM_L88:
    ; valorTope = 0
    mov ax, 0
    mov [valorTope], ax
    ; TOPE EN miPila
    cmp word ptr [miPila_top], 0
    je ASM_L89
    mov bx, [miPila_top]
    dec bx
    shl bx, 1
    mov ax, miPila[bx]
    mov [T21], ax
    jmp ASM_L90
ASM_L89:
    mov word ptr [T21], 0
ASM_L90:
    ; valorTope = T21
    mov ax, [T21]
    mov [valorTope], ax
    ; T22 = valorTope > 5
    mov ax, [valorTope]
    mov bx, 5
    cmp ax, bx
    jg ASM_L91
    mov [T22], 0
    jmp ASM_L92
ASM_L91:
    mov [T22], 1
ASM_L92:
    ; IF_FALSE T22 GOTO L6
    mov ax, [T22]
    cmp ax, 0
    je L6
    ; PRINT valorTope
    mov ax, [valorTope]
    call print_num
    mov dx, offset newline
    call print_string
L6:

; ============================================
; FIN DEL PROGRAMA
; ============================================
    mov ax, 4C00h
    int 21h

main endp

; ============================================
; RUTINAS AUXILIARES
; ============================================

print_string proc
    mov ah, 09h
    int 21h
    ret
print_string endp

print_num proc
    push ax
    push bx
    push cx
    push dx
    cmp ax, 0
    jne pn_convert
    mov dl, '0'
    mov ah, 02h
    int 21h
    jmp pn_done
pn_convert:
    xor cx, cx
    mov bx, 10
pn_loop:
    xor dx, dx
    div bx
    push dx
    inc cx
    cmp ax, 0
    jne pn_loop
pn_print:
    pop dx
    add dl, '0'
    mov ah, 02h
    int 21h
    loop pn_print
pn_done:
    pop dx
    pop cx
    pop bx
    pop ax
    ret
print_num endp

end main

