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

    miPila dw 0
    miCola dw 0
    T1 dw 0
    T2 dw 0
    T3 dw 0
    miLista dw 0
    miArbol dw 0
    T4 dw 0
    T5 dw 0
    T6 dw 0
    T7 dw 0
    miGrafo dw 0
    T8 dw 0
    miHash dw 0
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

    ; ALLOC miPila TAMANO 100
    mov word ptr [miPila], 0
    ; ALLOC miCola TAMANO 50
    mov word ptr [miCola], 0
    ; APILAR 1 EN miPila
    mov ax, 1
    mov [miPila], ax
    ; APILAR 2 EN miPila
    mov ax, 2
    mov [miPila], ax
    ; APILAR 3 EN miPila
    mov ax, 3
    mov [miPila], ax
    ; TOPE EN miPila
    mov ax, [miPila]
    mov [T1], ax
    ; PRINT T1
    mov ax, [T1]
    call print_num
    mov dx, offset newline
    call print_string
    ; VACIA EN miPila
    mov ax, [miPila]
    cmp ax, 0
    mov word ptr [T2], 0
    jne ASM_L1
    mov word ptr [T2], 1
ASM_L1:
    ; IF_FALSE T2 GOTO L1
    mov ax, [T2]
    cmp ax, 0
    je L1
    ; ERROR: Estructura vacía 
    mov dx, offset msg_error
    call print_string
L1:
    ; DESAPILAR EN miPila
    mov word ptr [miPila], 0
    ; TOPE EN miPila
    mov ax, [miPila]
    mov [T3], ax
    ; PRINT T3
    mov ax, [T3]
    call print_num
    mov dx, offset newline
    call print_string
    ; INSERTAR_FINAL 5 EN miLista
    mov ax, 5
    mov [miLista], ax
    ; INSERTAR_FINAL 10 EN miLista
    mov ax, 10
    mov [miLista], ax
    ; INSERTAR_FINAL 15 EN miLista
    mov ax, 15
    mov [miLista], ax
    ; AGREGARNODO 1 25 EN miArbol
    mov ax, 25
    mov [miArbol], ax
    ; AGREGARNODO 1 15 EN miArbol
    mov ax, 15
    mov [miArbol], ax
    ; AGREGARNODO 1 35 EN miArbol
    mov ax, 35
    mov [miArbol], ax
    ; AGREGARNODO 1 10 EN miArbol
    mov ax, 10
    mov [miArbol], ax
    ; AGREGARNODO 1 20 EN miArbol
    mov ax, 20
    mov [miArbol], ax
    ; AGREGARNODO 1 30 EN miArbol
    mov ax, 30
    mov [miArbol], ax
    ; AGREGARNODO 1 40 EN miArbol
    mov ax, 40
    mov [miArbol], ax
    ; PREORDEN EN miArbol
    mov ax, [miArbol]
    mov [T4], ax
    ; PRINT T4
    mov ax, [T4]
    call print_num
    mov dx, offset newline
    call print_string
    ; INORDEN EN miArbol
    mov ax, [miArbol]
    mov [T5], ax
    ; PRINT T5
    mov ax, [T5]
    call print_num
    mov dx, offset newline
    call print_string
    ; POSTORDEN EN miArbol
    mov ax, [miArbol]
    mov [T6], ax
    ; PRINT T6
    mov ax, [T6]
    call print_num
    mov dx, offset newline
    call print_string
    ; RECORRIDOPORNIVELES EN miArbol
    mov ax, [miArbol]
    mov [T7], ax
    ; PRINT T7
    mov ax, [T7]
    call print_num
    mov dx, offset newline
    call print_string
    ; AGREGARNODO 1 100 EN miGrafo
    mov ax, 100
    mov [miGrafo], ax
    ; AGREGARNODO 2 200 EN miGrafo
    mov ax, 200
    mov [miGrafo], ax
    ; AGREGARNODO 3 300 EN miGrafo
    mov ax, 300
    mov [miGrafo], ax
    ; AGREGARNODO 4 400 EN miGrafo
    mov ax, 400
    mov [miGrafo], ax
    ; AGREGARNODO 5 500 EN miGrafo
    mov ax, 500
    mov [miGrafo], ax
    ; AGREGARARISTA 1 2 EN miGrafo
    mov ax, 2
    mov [miGrafo], ax
    ; AGREGARARISTA 2 3 EN miGrafo
    mov ax, 3
    mov [miGrafo], ax
    ; AGREGARARISTA 3 4 EN miGrafo
    mov ax, 4
    mov [miGrafo], ax
    ; AGREGARARISTA 4 5 EN miGrafo
    mov ax, 5
    mov [miGrafo], ax
    ; AGREGARARISTA 1 5 EN miGrafo
    mov ax, 5
    mov [miGrafo], ax
    ; VECINOS EN 1
    mov ax, 0
    mov [T8], ax
    ; PRINT T8
    mov ax, [T8]
    call print_num
    mov dx, offset newline
    call print_string
    ; INSERTAR 101 1000 EN miHash
    mov ax, 1000
    mov [miHash], ax
    ; INSERTAR 102 2000 EN miHash
    mov ax, 2000
    mov [miHash], ax
    ; INSERTAR 103 3000 EN miHash
    mov ax, 3000
    mov [miHash], ax
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
    mov ax, [miPila]
    mov [T17], ax
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
    ; VACIA EN miPila
    mov ax, [miPila]
    cmp ax, 0
    mov word ptr [T19], 0
    jne ASM_L2
    mov word ptr [T19], 1
ASM_L2:
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
    ; VACIA EN miCola
    mov ax, [miCola]
    cmp ax, 0
    mov word ptr [T20], 0
    jne ASM_L3
    mov word ptr [T20], 1
ASM_L3:
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
    ; ELIMINAR_FINAL EN miLista
    mov word ptr [miLista], 0
    ; ELIMINAR_INICIO EN miLista
    mov word ptr [miLista], 0
    ; INSERTAR 200 999 EN miHash
    mov ax, 999
    mov [miHash], ax
    ; valorTope = 0
    mov ax, 0
    mov [valorTope], ax
    ; TOPE EN miPila
    mov ax, [miPila]
    mov [T21], ax
    ; valorTope = T21
    mov ax, [T21]
    mov [valorTope], ax
    ; T22 = valorTope > 5
    mov ax, [valorTope]
    mov bx, 5
    cmp ax, bx
    jg ASM_L4
    mov [T22], 0
    jmp ASM_L5
ASM_L4:
    mov [T22], 1
ASM_L5:
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

