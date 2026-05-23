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
    ARBOL_QUEUE dw 128 dup(0)
    ARBOL_Q_FRONT dw 0
    ARBOL_Q_REAR dw 0
    miArbol_root dw 0
    T1 dw 0
    T2 dw 0
    T3 dw 0
    T4 dw 0
    T5 dw 0
    T6 dw 0
    T7 dw 0

.code
main proc
    mov ax, @data
    mov ds, ax
    mov dx, offset titulo
    call print_string

    ; CREAR ARBOL_BINARIO miArbol
    ; AGREGARNODO 50 EN miArbol (nodo HEAP: valor, izq, der)
    mov ax, 50
    mov si, [HEAP_PTR]
    add word ptr [HEAP_PTR], 6
    mov HEAP[si], ax
    mov word ptr HEAP[si+2], 0
    mov word ptr HEAP[si+4], 0
    cmp word ptr [miArbol_root], 0
    jne ASM_L1
    mov [miArbol_root], si
    jmp ASM_L6
ASM_L1:
    mov bx, [miArbol_root]
ASM_L2:
    cmp ax, HEAP[bx]
    jg ASM_L3
    cmp word ptr HEAP[bx+2], 0
    je ASM_L4
    mov bx, HEAP[bx+2]
    jmp ASM_L2
ASM_L4:
    mov HEAP[bx+2], si
    jmp ASM_L6
ASM_L3:
    cmp word ptr HEAP[bx+4], 0
    je ASM_L5
    mov bx, HEAP[bx+4]
    jmp ASM_L2
ASM_L5:
    mov HEAP[bx+4], si
ASM_L6:
    ; AGREGARNODO 30 EN miArbol (nodo HEAP: valor, izq, der)
    mov ax, 30
    mov si, [HEAP_PTR]
    add word ptr [HEAP_PTR], 6
    mov HEAP[si], ax
    mov word ptr HEAP[si+2], 0
    mov word ptr HEAP[si+4], 0
    cmp word ptr [miArbol_root], 0
    jne ASM_L7
    mov [miArbol_root], si
    jmp ASM_L12
ASM_L7:
    mov bx, [miArbol_root]
ASM_L8:
    cmp ax, HEAP[bx]
    jg ASM_L9
    cmp word ptr HEAP[bx+2], 0
    je ASM_L10
    mov bx, HEAP[bx+2]
    jmp ASM_L8
ASM_L10:
    mov HEAP[bx+2], si
    jmp ASM_L12
ASM_L9:
    cmp word ptr HEAP[bx+4], 0
    je ASM_L11
    mov bx, HEAP[bx+4]
    jmp ASM_L8
ASM_L11:
    mov HEAP[bx+4], si
ASM_L12:
    ; AGREGARNODO 70 EN miArbol (nodo HEAP: valor, izq, der)
    mov ax, 70
    mov si, [HEAP_PTR]
    add word ptr [HEAP_PTR], 6
    mov HEAP[si], ax
    mov word ptr HEAP[si+2], 0
    mov word ptr HEAP[si+4], 0
    cmp word ptr [miArbol_root], 0
    jne ASM_L13
    mov [miArbol_root], si
    jmp ASM_L18
ASM_L13:
    mov bx, [miArbol_root]
ASM_L14:
    cmp ax, HEAP[bx]
    jg ASM_L15
    cmp word ptr HEAP[bx+2], 0
    je ASM_L16
    mov bx, HEAP[bx+2]
    jmp ASM_L14
ASM_L16:
    mov HEAP[bx+2], si
    jmp ASM_L18
ASM_L15:
    cmp word ptr HEAP[bx+4], 0
    je ASM_L17
    mov bx, HEAP[bx+4]
    jmp ASM_L14
ASM_L17:
    mov HEAP[bx+4], si
ASM_L18:
    ; AGREGARNODO 20 EN miArbol (nodo HEAP: valor, izq, der)
    mov ax, 20
    mov si, [HEAP_PTR]
    add word ptr [HEAP_PTR], 6
    mov HEAP[si], ax
    mov word ptr HEAP[si+2], 0
    mov word ptr HEAP[si+4], 0
    cmp word ptr [miArbol_root], 0
    jne ASM_L19
    mov [miArbol_root], si
    jmp ASM_L24
ASM_L19:
    mov bx, [miArbol_root]
ASM_L20:
    cmp ax, HEAP[bx]
    jg ASM_L21
    cmp word ptr HEAP[bx+2], 0
    je ASM_L22
    mov bx, HEAP[bx+2]
    jmp ASM_L20
ASM_L22:
    mov HEAP[bx+2], si
    jmp ASM_L24
ASM_L21:
    cmp word ptr HEAP[bx+4], 0
    je ASM_L23
    mov bx, HEAP[bx+4]
    jmp ASM_L20
ASM_L23:
    mov HEAP[bx+4], si
ASM_L24:
    ; AGREGARNODO 40 EN miArbol (nodo HEAP: valor, izq, der)
    mov ax, 40
    mov si, [HEAP_PTR]
    add word ptr [HEAP_PTR], 6
    mov HEAP[si], ax
    mov word ptr HEAP[si+2], 0
    mov word ptr HEAP[si+4], 0
    cmp word ptr [miArbol_root], 0
    jne ASM_L25
    mov [miArbol_root], si
    jmp ASM_L30
ASM_L25:
    mov bx, [miArbol_root]
ASM_L26:
    cmp ax, HEAP[bx]
    jg ASM_L27
    cmp word ptr HEAP[bx+2], 0
    je ASM_L28
    mov bx, HEAP[bx+2]
    jmp ASM_L26
ASM_L28:
    mov HEAP[bx+2], si
    jmp ASM_L30
ASM_L27:
    cmp word ptr HEAP[bx+4], 0
    je ASM_L29
    mov bx, HEAP[bx+4]
    jmp ASM_L26
ASM_L29:
    mov HEAP[bx+4], si
ASM_L30:
    ; AGREGARNODO 60 EN miArbol (nodo HEAP: valor, izq, der)
    mov ax, 60
    mov si, [HEAP_PTR]
    add word ptr [HEAP_PTR], 6
    mov HEAP[si], ax
    mov word ptr HEAP[si+2], 0
    mov word ptr HEAP[si+4], 0
    cmp word ptr [miArbol_root], 0
    jne ASM_L31
    mov [miArbol_root], si
    jmp ASM_L36
ASM_L31:
    mov bx, [miArbol_root]
ASM_L32:
    cmp ax, HEAP[bx]
    jg ASM_L33
    cmp word ptr HEAP[bx+2], 0
    je ASM_L34
    mov bx, HEAP[bx+2]
    jmp ASM_L32
ASM_L34:
    mov HEAP[bx+2], si
    jmp ASM_L36
ASM_L33:
    cmp word ptr HEAP[bx+4], 0
    je ASM_L35
    mov bx, HEAP[bx+4]
    jmp ASM_L32
ASM_L35:
    mov HEAP[bx+4], si
ASM_L36:
    ; AGREGARNODO 80 EN miArbol (nodo HEAP: valor, izq, der)
    mov ax, 80
    mov si, [HEAP_PTR]
    add word ptr [HEAP_PTR], 6
    mov HEAP[si], ax
    mov word ptr HEAP[si+2], 0
    mov word ptr HEAP[si+4], 0
    cmp word ptr [miArbol_root], 0
    jne ASM_L37
    mov [miArbol_root], si
    jmp ASM_L42
ASM_L37:
    mov bx, [miArbol_root]
ASM_L38:
    cmp ax, HEAP[bx]
    jg ASM_L39
    cmp word ptr HEAP[bx+2], 0
    je ASM_L40
    mov bx, HEAP[bx+2]
    jmp ASM_L38
ASM_L40:
    mov HEAP[bx+2], si
    jmp ASM_L42
ASM_L39:
    cmp word ptr HEAP[bx+4], 0
    je ASM_L41
    mov bx, HEAP[bx+4]
    jmp ASM_L38
ASM_L41:
    mov HEAP[bx+4], si
ASM_L42:
    ; AGREGARNODO 10 EN miArbol (nodo HEAP: valor, izq, der)
    mov ax, 10
    mov si, [HEAP_PTR]
    add word ptr [HEAP_PTR], 6
    mov HEAP[si], ax
    mov word ptr HEAP[si+2], 0
    mov word ptr HEAP[si+4], 0
    cmp word ptr [miArbol_root], 0
    jne ASM_L43
    mov [miArbol_root], si
    jmp ASM_L48
ASM_L43:
    mov bx, [miArbol_root]
ASM_L44:
    cmp ax, HEAP[bx]
    jg ASM_L45
    cmp word ptr HEAP[bx+2], 0
    je ASM_L46
    mov bx, HEAP[bx+2]
    jmp ASM_L44
ASM_L46:
    mov HEAP[bx+2], si
    jmp ASM_L48
ASM_L45:
    cmp word ptr HEAP[bx+4], 0
    je ASM_L47
    mov bx, HEAP[bx+4]
    jmp ASM_L44
ASM_L47:
    mov HEAP[bx+4], si
ASM_L48:
    ; AGREGARNODO 25 EN miArbol (nodo HEAP: valor, izq, der)
    mov ax, 25
    mov si, [HEAP_PTR]
    add word ptr [HEAP_PTR], 6
    mov HEAP[si], ax
    mov word ptr HEAP[si+2], 0
    mov word ptr HEAP[si+4], 0
    cmp word ptr [miArbol_root], 0
    jne ASM_L49
    mov [miArbol_root], si
    jmp ASM_L54
ASM_L49:
    mov bx, [miArbol_root]
ASM_L50:
    cmp ax, HEAP[bx]
    jg ASM_L51
    cmp word ptr HEAP[bx+2], 0
    je ASM_L52
    mov bx, HEAP[bx+2]
    jmp ASM_L50
ASM_L52:
    mov HEAP[bx+2], si
    jmp ASM_L54
ASM_L51:
    cmp word ptr HEAP[bx+4], 0
    je ASM_L53
    mov bx, HEAP[bx+4]
    jmp ASM_L50
ASM_L53:
    mov HEAP[bx+4], si
ASM_L54:
    ; INORDEN EN miArbol usando recursion con pila 8086
    mov word ptr [T1], 0
    mov bx, [miArbol_root]
    call ASM_L55
    mov ax, [T1]
    call print_num
    mov dx, offset newline
    call print_string
    jmp ASM_L57
ASM_L57:
    ; PRINT T1
    mov ax, [T1]
    call print_num
    mov dx, offset newline
    call print_string
    ; PREORDEN EN miArbol usando recursion con pila 8086
    mov word ptr [T2], 0
    mov bx, [miArbol_root]
    call ASM_L58
    mov ax, [T2]
    call print_num
    mov dx, offset newline
    call print_string
    jmp ASM_L60
ASM_L60:
    ; PRINT T2
    mov ax, [T2]
    call print_num
    mov dx, offset newline
    call print_string
    ; POSTORDEN EN miArbol usando recursion con pila 8086
    mov word ptr [T3], 0
    mov bx, [miArbol_root]
    call ASM_L61
    mov ax, [T3]
    call print_num
    mov dx, offset newline
    call print_string
    jmp ASM_L63
ASM_L63:
    ; PRINT T3
    mov ax, [T3]
    call print_num
    mov dx, offset newline
    call print_string
    ; RECORRIDOPORNIVELES EN miArbol usando cola estatica segura
    mov word ptr [T4], 0
    mov word ptr [ARBOL_Q_FRONT], 0
    mov word ptr [ARBOL_Q_REAR], 0
    mov ax, [miArbol_root]
    cmp ax, 0
    je ASM_L64
    mov bx, [ARBOL_Q_REAR]
    shl bx, 1
    mov ARBOL_QUEUE[bx], ax
    inc word ptr [ARBOL_Q_REAR]
ASM_L65:
    mov ax, [ARBOL_Q_FRONT]
    cmp ax, [ARBOL_Q_REAR]
    jge ASM_L64
    mov bx, ax
    shl bx, 1
    mov bx, ARBOL_QUEUE[bx]
    inc word ptr [ARBOL_Q_FRONT]
    cmp bx, 0
    je ASM_L66
    mov ax, HEAP[bx]
    mov [T4], ax
    push bx
    call print_num
    mov dx, offset newline
    call print_string
    pop bx
    mov ax, HEAP[bx+2]
    cmp ax, 0
    je ASM_L67
    mov si, [ARBOL_Q_REAR]
    cmp si, 128
    jge ASM_L67
    shl si, 1
    mov ARBOL_QUEUE[si], ax
    inc word ptr [ARBOL_Q_REAR]
ASM_L67:
    mov ax, HEAP[bx+4]
    cmp ax, 0
    je ASM_L68
    mov si, [ARBOL_Q_REAR]
    cmp si, 128
    jge ASM_L68
    shl si, 1
    mov ARBOL_QUEUE[si], ax
    inc word ptr [ARBOL_Q_REAR]
ASM_L68:
ASM_L66:
    jmp ASM_L65
ASM_L64:
    ; PRINT T4
    mov ax, [T4]
    call print_num
    mov dx, offset newline
    call print_string
    ; BUSCAR EN 40
    mov ax, [40]
    mov [miArbol], ax
    ; BUSCAR EN 99
    mov ax, [99]
    mov [miArbol], ax
    ; ELIMINAR EN miArbol
    mov word ptr [miArbol], 0
    ; ELIMINAR EN miArbol
    mov word ptr [miArbol], 0
    ; ELIMINAR EN miArbol
    mov word ptr [miArbol], 0
    ; ELIMINAR EN miArbol
    mov word ptr [miArbol], 0
    ; INORDEN EN miArbol usando recursion con pila 8086
    mov word ptr [T5], 0
    mov bx, [miArbol_root]
    call ASM_L69
    mov ax, [T5]
    call print_num
    mov dx, offset newline
    call print_string
    jmp ASM_L71
ASM_L71:
    ; PRINT T5
    mov ax, [T5]
    call print_num
    mov dx, offset newline
    call print_string
    ; AGREGARNODO 35 EN miArbol (nodo HEAP: valor, izq, der)
    mov ax, 35
    mov si, [HEAP_PTR]
    add word ptr [HEAP_PTR], 6
    mov HEAP[si], ax
    mov word ptr HEAP[si+2], 0
    mov word ptr HEAP[si+4], 0
    cmp word ptr [miArbol_root], 0
    jne ASM_L72
    mov [miArbol_root], si
    jmp ASM_L77
ASM_L72:
    mov bx, [miArbol_root]
ASM_L73:
    cmp ax, HEAP[bx]
    jg ASM_L74
    cmp word ptr HEAP[bx+2], 0
    je ASM_L75
    mov bx, HEAP[bx+2]
    jmp ASM_L73
ASM_L75:
    mov HEAP[bx+2], si
    jmp ASM_L77
ASM_L74:
    cmp word ptr HEAP[bx+4], 0
    je ASM_L76
    mov bx, HEAP[bx+4]
    jmp ASM_L73
ASM_L76:
    mov HEAP[bx+4], si
ASM_L77:
    ; AGREGARNODO 65 EN miArbol (nodo HEAP: valor, izq, der)
    mov ax, 65
    mov si, [HEAP_PTR]
    add word ptr [HEAP_PTR], 6
    mov HEAP[si], ax
    mov word ptr HEAP[si+2], 0
    mov word ptr HEAP[si+4], 0
    cmp word ptr [miArbol_root], 0
    jne ASM_L78
    mov [miArbol_root], si
    jmp ASM_L83
ASM_L78:
    mov bx, [miArbol_root]
ASM_L79:
    cmp ax, HEAP[bx]
    jg ASM_L80
    cmp word ptr HEAP[bx+2], 0
    je ASM_L81
    mov bx, HEAP[bx+2]
    jmp ASM_L79
ASM_L81:
    mov HEAP[bx+2], si
    jmp ASM_L83
ASM_L80:
    cmp word ptr HEAP[bx+4], 0
    je ASM_L82
    mov bx, HEAP[bx+4]
    jmp ASM_L79
ASM_L82:
    mov HEAP[bx+4], si
ASM_L83:
    ; AGREGARNODO 45 EN miArbol (nodo HEAP: valor, izq, der)
    mov ax, 45
    mov si, [HEAP_PTR]
    add word ptr [HEAP_PTR], 6
    mov HEAP[si], ax
    mov word ptr HEAP[si+2], 0
    mov word ptr HEAP[si+4], 0
    cmp word ptr [miArbol_root], 0
    jne ASM_L84
    mov [miArbol_root], si
    jmp ASM_L89
ASM_L84:
    mov bx, [miArbol_root]
ASM_L85:
    cmp ax, HEAP[bx]
    jg ASM_L86
    cmp word ptr HEAP[bx+2], 0
    je ASM_L87
    mov bx, HEAP[bx+2]
    jmp ASM_L85
ASM_L87:
    mov HEAP[bx+2], si
    jmp ASM_L89
ASM_L86:
    cmp word ptr HEAP[bx+4], 0
    je ASM_L88
    mov bx, HEAP[bx+4]
    jmp ASM_L85
ASM_L88:
    mov HEAP[bx+4], si
ASM_L89:
    ; INORDEN EN miArbol usando recursion con pila 8086
    mov word ptr [T6], 0
    mov bx, [miArbol_root]
    call ASM_L90
    mov ax, [T6]
    call print_num
    mov dx, offset newline
    call print_string
    jmp ASM_L92
ASM_L92:
    ; PRINT T6
    mov ax, [T6]
    call print_num
    mov dx, offset newline
    call print_string
    ; RECORRIDOPORNIVELES EN miArbol usando cola estatica segura
    mov word ptr [T7], 0
    mov word ptr [ARBOL_Q_FRONT], 0
    mov word ptr [ARBOL_Q_REAR], 0
    mov ax, [miArbol_root]
    cmp ax, 0
    je ASM_L93
    mov bx, [ARBOL_Q_REAR]
    shl bx, 1
    mov ARBOL_QUEUE[bx], ax
    inc word ptr [ARBOL_Q_REAR]
ASM_L94:
    mov ax, [ARBOL_Q_FRONT]
    cmp ax, [ARBOL_Q_REAR]
    jge ASM_L93
    mov bx, ax
    shl bx, 1
    mov bx, ARBOL_QUEUE[bx]
    inc word ptr [ARBOL_Q_FRONT]
    cmp bx, 0
    je ASM_L95
    mov ax, HEAP[bx]
    mov [T7], ax
    push bx
    call print_num
    mov dx, offset newline
    call print_string
    pop bx
    mov ax, HEAP[bx+2]
    cmp ax, 0
    je ASM_L96
    mov si, [ARBOL_Q_REAR]
    cmp si, 128
    jge ASM_L96
    shl si, 1
    mov ARBOL_QUEUE[si], ax
    inc word ptr [ARBOL_Q_REAR]
ASM_L96:
    mov ax, HEAP[bx+4]
    cmp ax, 0
    je ASM_L97
    mov si, [ARBOL_Q_REAR]
    cmp si, 128
    jge ASM_L97
    shl si, 1
    mov ARBOL_QUEUE[si], ax
    inc word ptr [ARBOL_Q_REAR]
ASM_L97:
ASM_L95:
    jmp ASM_L94
ASM_L93:
    ; PRINT T7
    mov ax, [T7]
    call print_num
    mov dx, offset newline
    call print_string
    ; PRINT 999
    mov ax, 999
    call print_num
    mov dx, offset newline
    call print_string

; ============================================
; FIN DEL PROGRAMA
; ============================================
    jmp ASM_L98

; ============================================
; RUTINAS RECURSIVAS DE ARBOL
; ============================================
ASM_L55:
    cmp bx, 0
    je ASM_L56
    push bx
    mov bx, HEAP[bx+2]
    call ASM_L55
    pop bx
    mov ax, HEAP[bx]
    mov [T1], ax
    push bx
    call print_num
    mov dx, offset newline
    call print_string
    pop bx
    push bx
    mov bx, HEAP[bx+4]
    call ASM_L55
    pop bx
ASM_L56:
    ret
ASM_L58:
    cmp bx, 0
    je ASM_L59
    mov ax, HEAP[bx]
    mov [T2], ax
    push bx
    call print_num
    mov dx, offset newline
    call print_string
    pop bx
    push bx
    mov bx, HEAP[bx+2]
    call ASM_L58
    pop bx
    push bx
    mov bx, HEAP[bx+4]
    call ASM_L58
    pop bx
ASM_L59:
    ret
ASM_L61:
    cmp bx, 0
    je ASM_L62
    push bx
    mov bx, HEAP[bx+2]
    call ASM_L61
    pop bx
    push bx
    mov bx, HEAP[bx+4]
    call ASM_L61
    pop bx
    mov ax, HEAP[bx]
    mov [T3], ax
    push bx
    call print_num
    mov dx, offset newline
    call print_string
    pop bx
ASM_L62:
    ret
ASM_L69:
    cmp bx, 0
    je ASM_L70
    push bx
    mov bx, HEAP[bx+2]
    call ASM_L69
    pop bx
    mov ax, HEAP[bx]
    mov [T5], ax
    push bx
    call print_num
    mov dx, offset newline
    call print_string
    pop bx
    push bx
    mov bx, HEAP[bx+4]
    call ASM_L69
    pop bx
ASM_L70:
    ret
ASM_L90:
    cmp bx, 0
    je ASM_L91
    push bx
    mov bx, HEAP[bx+2]
    call ASM_L90
    pop bx
    mov ax, HEAP[bx]
    mov [T6], ax
    push bx
    call print_num
    mov dx, offset newline
    call print_string
    pop bx
    push bx
    mov bx, HEAP[bx+4]
    call ASM_L90
    pop bx
ASM_L91:
    ret

ASM_L98:
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

