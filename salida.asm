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

    msg_1 db 'Código Intermedio OK', '$'

.code
main proc
    mov ax, @data
    mov ds, ax
    mov dx, offset titulo
    call print_string

    ; PRINT 15
    mov ax, 15
    call print_num
    mov dx, offset newline
    call print_string
    ; PRINT 5
    mov ax, 5
    call print_num
    mov dx, offset newline
    call print_string
    ; PRINT 50
    mov ax, 50
    call print_num
    mov dx, offset newline
    call print_string
    ; PRINT "Código Intermedio OK"
    mov dx, offset msg_1
    call print_string
    mov dx, offset newline
    call print_string

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

