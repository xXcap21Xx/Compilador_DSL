; ============================================
; CODIGO ENSAMBLADOR GRAFICO GENERADO - DSL
; Intel 8086 / EMU8086 / MASM - Modo 13h
; ============================================

.model small
.stack 100h

.data
    titulo db 'DSL - VISUALIZACION GRAFICA', 0

    gfx_i dw 0
    gfx_valor dw 0
    rect_x dw 0
    rect_y dw 0
    rect_w dw 0
    rect_h dw 0
    rect_color db 0
    miPila dw 100 dup(0)
    miPila_top dw 0
    T1 dw 0
    T3 dw 0

.code
main proc
    mov ax, @data
    mov ds, ax

    ; Modo grafico 13h: 320x200, 256 colores
    mov ax, 0013h
    int 10h

    ; CREAR PILA miPila TAMANO 100
    call GRAFICAR_TODO
    ; APILAR 1 EN miPila
    cmp word ptr [miPila_top], 100
    jge GFX_L1
    mov ax, 1
    mov bx, [miPila_top]
    shl bx, 1
    mov miPila[bx], ax
    inc word ptr [miPila_top]
GFX_L1:
    call GRAFICAR_TODO
    ; APILAR 2 EN miPila
    cmp word ptr [miPila_top], 100
    jge GFX_L2
    mov ax, 2
    mov bx, [miPila_top]
    shl bx, 1
    mov miPila[bx], ax
    inc word ptr [miPila_top]
GFX_L2:
    call GRAFICAR_TODO
    ; APILAR 3 EN miPila
    cmp word ptr [miPila_top], 100
    jge GFX_L3
    mov ax, 3
    mov bx, [miPila_top]
    shl bx, 1
    mov miPila[bx], ax
    inc word ptr [miPila_top]
GFX_L3:
    call GRAFICAR_TODO
    ; Operacion grafica pendiente: TOPE miPila  -> T1
    ; MOSTRAR T1 en modo grafico
    mov ax, [T1]
    call PRINT_NUM_GRAFICO
    ; Operacion grafica pendiente: VACIA miPila  -> T2
    ; Operacion grafica pendiente: IF_FALSE T2 GOTO -> L1
    ; Operacion grafica pendiente: ERROR Estructura vacía  -> 
    ; Operacion grafica pendiente: ETIQUETA   -> L1
    ; DESAPILAR EN miPila
    cmp word ptr [miPila_top], 0
    je GFX_L4
    dec word ptr [miPila_top]
    mov bx, [miPila_top]
    shl bx, 1
    mov word ptr miPila[bx], 0
GFX_L4:
    call GRAFICAR_TODO
    ; Operacion grafica pendiente: TOPE miPila  -> T3
    ; MOSTRAR T3 en modo grafico
    mov ax, [T3]
    call PRINT_NUM_GRAFICO
    call GRAFICAR_TODO
    mov ah, 00h
    int 16h
    mov ax, 0003h
    int 10h
    mov ax, 4C00h
    int 21h
main endp

; ============================================
; RUTINAS GRAFICAS
; ============================================

DIBUJAR_PIXEL proc
    push ax
    push bx
    mov ah, 0Ch
    mov bh, 00h
    int 10h
    pop bx
    pop ax
    ret
DIBUJAR_PIXEL endp

DIBUJAR_RECTANGULO proc
    push ax
    push bx
    push cx
    push dx
    push si
    push di
    mov [rect_x], cx
    mov [rect_y], dx
    mov [rect_w], si
    mov [rect_h], di
    mov [rect_color], al
dr_fila:
    mov cx, [rect_x]
    mov si, [rect_w]
dr_columna:
    mov dx, [rect_y]
    mov al, [rect_color]
    call DIBUJAR_PIXEL
    inc cx
    dec si
    jnz dr_columna
    inc word ptr [rect_y]
    dec word ptr [rect_h]
    jnz dr_fila
    pop di
    pop si
    pop dx
    pop cx
    pop bx
    pop ax
    ret
DIBUJAR_RECTANGULO endp

LIMPIAR_PANTALLA proc
    push ax
    push bx
    push cx
    push dx
    mov ax, 0600h
    mov bh, 00h
    mov cx, 0000h
    mov dx, 1827h
    int 10h
    pop dx
    pop cx
    pop bx
    pop ax
    ret
LIMPIAR_PANTALLA endp

SET_CURSOR_PIXEL proc
    push ax
    push bx
    push cx
    push dx
    mov ax, dx
    mov bl, 8
    div bl
    mov dh, al
    mov ax, cx
    mov bl, 8
    div bl
    mov dl, al
    mov ah, 02h
    mov bh, 00h
    int 10h
    pop dx
    pop cx
    pop bx
    pop ax
    ret
SET_CURSOR_PIXEL endp

PRINT_NUM_GRAFICO proc
    push ax
    push bx
    push cx
    push dx
    cmp ax, 0
    jne png_convertir
    mov al, '0'
    mov ah, 0Eh
    int 10h
    jmp png_fin
png_convertir:
    xor cx, cx
    mov bx, 10
png_dividir:
    xor dx, dx
    div bx
    push dx
    inc cx
    cmp ax, 0
    jne png_dividir
png_imprimir:
    pop dx
    mov al, dl
    add al, '0'
    mov ah, 0Eh
    int 10h
    loop png_imprimir
png_fin:
    pop dx
    pop cx
    pop bx
    pop ax
    ret
PRINT_NUM_GRAFICO endp

GRAFICAR_TODO proc
    call LIMPIAR_PANTALLA
    call GRAFICAR_PILA_miPila
    ret
GRAFICAR_TODO endp

GRAFICAR_PILA_miPila proc
    mov word ptr [gfx_i], 0
miPila_gp_loop:
    mov ax, [gfx_i]
    cmp ax, [miPila_top]
    jge miPila_gp_fin
    mov bx, ax
    shl bx, 1
    mov ax, miPila[bx]
    mov [gfx_valor], ax
    mov ax, [gfx_i]
    mov bx, 12
    mul bx
    mov dx, 180
    sub dx, ax
    mov cx, 10
    mov si, 42
    mov di, 10
    mov al, 0Ah
    call DIBUJAR_RECTANGULO
    mov cx, 22
    call SET_CURSOR_PIXEL
    mov ax, [gfx_valor]
    call PRINT_NUM_GRAFICO
    inc word ptr [gfx_i]
    jmp miPila_gp_loop
miPila_gp_fin:
    ret
GRAFICAR_PILA_miPila endp

end main
