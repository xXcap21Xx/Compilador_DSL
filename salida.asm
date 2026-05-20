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

    miPila dw 10 dup(0)
    miPila_top dw 0
    miContador dw 0
    T1 dw 0
    T2 dw 0
    revisarTope dw 0
    T3 dw 0
    T4 dw 0

.code
main proc
    mov ax, @data
    mov ds, ax
    mov dx, offset titulo
    call print_string

    ; CREAR PILA miPila TAMANO 10
    ; miContador = 5
    mov ax, 5
    mov [miContador], ax
    ; APILAR 20 EN miPila
    mov ax, 20
    mov bx, [miPila_top]
    shl bx, 1
    mov miPila[bx], ax
    inc word ptr [miPila_top]
    ; APILAR 80 EN miPila
    mov ax, 80
    mov bx, [miPila_top]
    shl bx, 1
    mov miPila[bx], ax
    inc word ptr [miPila_top]
    ; VACIA
    mov word ptr [T1], 0
    cmp word ptr [miPila_top], 0
    je ASM_L1
    jmp ASM_L2
ASM_L1:
    mov word ptr [T1], 1
ASM_L2:
    ; IF_FALSE T1 GOTO L1
    mov ax, [T1]
    cmp ax, 0
    je L1
    ; ERROR: Estructura vacía 
    mov dx, offset msg_error
    call print_string
L1:
    ; DESAPILAR EN miPila
    cmp word ptr [miPila_top], 0
    je ASM_L3
    dec word ptr [miPila_top]
    mov bx, [miPila_top]
    shl bx, 1
    mov word ptr miPila[bx], 0
ASM_L3:
    ; T2 = miContador + 10
    mov ax, [miContador]
    mov bx, 10
    add ax, bx
    mov [T2], ax
    ; miContador = T2
    mov ax, [T2]
    mov [miContador], ax
    ; PRINT miContador
    mov ax, [miContador]
    call print_num
    mov dx, offset newline
    call print_string
    ; revisarTope = 0
    mov ax, 0
    mov [revisarTope], ax
    ; TOPE EN miPila
    cmp word ptr [miPila_top], 0
    je ASM_L4
    mov bx, [miPila_top]
    dec bx
    shl bx, 1
    mov ax, miPila[bx]
    mov [T3], ax
    jmp ASM_L5
ASM_L4:
    mov word ptr [T3], 0
ASM_L5:
    ; revisarTope = T3
    mov ax, [T3]
    mov [revisarTope], ax
    ; T4 = revisarTope > 15
    mov ax, [revisarTope]
    mov bx, 15
    cmp ax, bx
    jg ASM_L6
    mov [T4], 0
    jmp ASM_L7
ASM_L6:
    mov [T4], 1
ASM_L7:
    ; IF_FALSE T4 GOTO L2
    mov ax, [T4]
    cmp ax, 0
    je L2
    ; PRINT revisarTope
    mov ax, [revisarTope]
    call print_num
    mov dx, offset newline
    call print_string
L2:

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

