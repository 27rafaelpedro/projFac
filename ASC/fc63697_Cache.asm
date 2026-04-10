; fc63697
SECTION .rodata
errormsg: db "Introduz pelo menos um endereço!",0
errormsg_len: EQU $-errormsg

SECTION .bss ; variáveis complementares
saveTag resw 1
saveOffset resb 1
saveIndex resb 1

SECTION .text
; chamar a biblioteca e as suas respetivas funções
extern set_validation_bit
extern set_tag
extern get_data
extern display_table
extern get_validation_bit
extern get_tag
global _start

_start:
mov r14, [rsp]
cmp r14, 1 ; verificar se existe argumentos
jle _errormsg ; caso não exista, jump para a msg de erro
dec r14 ; remover argv[0]

lea rbp, [rsp+16] ; apontador para argv[1]
xor r13, r13; contador
xor rax, rax

_beginCache:
mov r15, [rbp+r13*8] ; r15 contêm o endereço da string
mov rax, [r15] ; rax contêm a string

_beginMask:
; preparar variáveis para mascáras
mov [saveIndex], al
mov [saveTag], ax
mov [saveOffset], al
xor rax,rax ; zerar rax pois este é o registo utilizado para retornar o resultado de uma chamada de função

;INDEX
shr byte[saveIndex], 2
and byte[saveIndex], 0x0F

;TAG
shr word[saveTag], 6

;OFFSET
and byte[saveOffset], 0x03

_getValidationBit:
mov rdi, [saveIndex]
call get_validation_bit
cmp rax, 0
je _setVBit
jmp _getTag

_setVBit:
mov rdi, [saveIndex]
call set_validation_bit
jmp _setTag

_getTag:
mov rdi, [saveIndex]
call get_tag
cmp rax, [saveTag]
je _getData
jmp _setTag

_setTag:
mov rdi, [saveIndex]
mov rsi, [saveTag]
call set_tag

_getData:
mov rdi, [saveIndex]
mov rsi, [saveOffset]
call get_data
call display_table

inc r13
cmp r13, r14 ; percorrer os argumentos fornecidos
je _finish
jmp _beginCache

_errormsg: ; msg de erro
mov rax, 1
mov rdi, 1
mov rsi, errormsg
mov rdx, errormsg_len
syscall

_finish: ; encerrar programa
mov rax, 60
xor rdi, rdi
syscall
