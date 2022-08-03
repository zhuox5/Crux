    .globl main
main:
    enter $(8 * 108), $0
    /* $t1 = 3 */
    movq $3, %r10
    movq %r10, -8(%rbp)
    /* $t2 = 5 */
    movq $5, %r10
    movq %r10, -16(%rbp)
    /* CompareInst */ 
    movq $0, %rax
    movq $1, %r10
    movq -8(%rbp), %r11
    cmp -16(%rbp), %r11
    cmovl %r10, %rax
    movq %rax, -24(%rbp)
    /* $t0 = $t3 */
    movq -24(%rbp), %r10
    movq %r10, -32(%rbp)
    /* jump $t0 */
    movq -32(%rbp), %r10
    cmp $1, %r10
    je L1
L2:
    /* call Symbol(printBool:func(TypeList(bool)):void) ($t0) */
    movq -32(%rbp), %rdi
    call printBool
    /* call Symbol(println:func(TypeList()):void) () */
    call println
    /* $t9 = 3 */
    movq $3, %r10
    movq %r10, -40(%rbp)
    /* $t10 = 5 */
    movq $5, %r10
    movq %r10, -48(%rbp)
    /* CompareInst */ 
    movq $0, %rax
    movq $1, %r10
    movq -40(%rbp), %r11
    cmp -48(%rbp), %r11
    cmovl %r10, %rax
    movq %rax, -56(%rbp)
    /* $t8 = $t11 */
    movq -56(%rbp), %r10
    movq %r10, -64(%rbp)
    /* jump $t8 */
    movq -64(%rbp), %r10
    cmp $1, %r10
    je L3
L4:
    /* $t7 = $t8 */
    movq -64(%rbp), %r10
    movq %r10, -72(%rbp)
    /* jump $t7 */
    movq -72(%rbp), %r10
    cmp $1, %r10
    je L5
    /* $t15 = 1 */
    movq $1, %r10
    movq %r10, -80(%rbp)
    /* $t16 = 2 */
    movq $2, %r10
    movq %r10, -88(%rbp)
    /* CompareInst */ 
    movq $0, %rax
    movq $1, %r10
    movq -80(%rbp), %r11
    cmp -88(%rbp), %r11
    cmovl %r10, %rax
    movq %rax, -96(%rbp)
    /* $t7 = $t17 */
    movq -96(%rbp), %r10
    movq %r10, -72(%rbp)
L5:
    /* call Symbol(printBool:func(TypeList(bool)):void) ($t7) */
    movq -72(%rbp), %rdi
    call printBool
    /* call Symbol(println:func(TypeList()):void) () */
    call println
    /* $t19 = 1 */
    movq $1, %r10
    movq %r10, -104(%rbp)
    /* $t20 = 2 */
    movq $2, %r10
    movq %r10, -112(%rbp)
    /* BinaryOperator: Add */
    movq -104(%rbp), %r10
    addq -112(%rbp), %r10
    movq %r10, -120(%rbp)
    /* $t22 = 4 */
    movq $4, %r10
    movq %r10, -128(%rbp)
    /* CompareInst */ 
    movq $0, %rax
    movq $1, %r10
    movq -120(%rbp), %r11
    cmp -128(%rbp), %r11
    cmovl %r10, %rax
    movq %rax, -136(%rbp)
    /* $t18 = $t23 */
    movq -136(%rbp), %r10
    movq %r10, -144(%rbp)
    /* jump $t18 */
    movq -144(%rbp), %r10
    cmp $1, %r10
    je L6
L7:
    /* call Symbol(printBool:func(TypeList(bool)):void) ($t18) */
    movq -144(%rbp), %rdi
    call printBool
    /* call Symbol(println:func(TypeList()):void) () */
    call println
    /* $t29 = false */
    movq $0, -152(%rbp)
    /* $t28 = $t29 */
    movq -152(%rbp), %r10
    movq %r10, -160(%rbp)
    /* jump $t28 */
    movq -160(%rbp), %r10
    cmp $1, %r10
    je L8
L9:
    /* $t27 = $t28 */
    movq -160(%rbp), %r10
    movq %r10, -168(%rbp)
    /* jump $t27 */
    movq -168(%rbp), %r10
    cmp $1, %r10
    je L10
    /* $t31 = false */
    movq $0, -176(%rbp)
    /* $t27 = $t31 */
    movq -176(%rbp), %r10
    movq %r10, -168(%rbp)
L10:
    /* call Symbol(printBool:func(TypeList(bool)):void) ($t27) */
    movq -168(%rbp), %rdi
    call printBool
    /* call Symbol(println:func(TypeList()):void) () */
    call println
    /* $t34 = false */
    movq $0, -184(%rbp)
    /* $t33 = $t34 */
    movq -184(%rbp), %r10
    movq %r10, -192(%rbp)
    /* jump $t33 */
    movq -192(%rbp), %r10
    cmp $1, %r10
    je L11
L12:
    /* $t32 = $t33 */
    movq -192(%rbp), %r10
    movq %r10, -200(%rbp)
    /* jump $t32 */
    movq -200(%rbp), %r10
    cmp $1, %r10
    je L13
    /* $t36 = true */
    movq $1, -208(%rbp)
    /* $t32 = $t36 */
    movq -208(%rbp), %r10
    movq %r10, -200(%rbp)
L13:
    /* call Symbol(printBool:func(TypeList(bool)):void) ($t32) */
    movq -200(%rbp), %rdi
    call printBool
    /* call Symbol(println:func(TypeList()):void) () */
    call println
    /* $t39 = false */
    movq $0, -216(%rbp)
    /* $t38 = $t39 */
    movq -216(%rbp), %r10
    movq %r10, -224(%rbp)
    /* jump $t38 */
    movq -224(%rbp), %r10
    cmp $1, %r10
    je L14
L15:
    /* $t37 = $t38 */
    movq -224(%rbp), %r10
    movq %r10, -232(%rbp)
    /* jump $t37 */
    movq -232(%rbp), %r10
    cmp $1, %r10
    je L16
    /* $t41 = false */
    movq $0, -240(%rbp)
    /* $t37 = $t41 */
    movq -240(%rbp), %r10
    movq %r10, -232(%rbp)
L16:
    /* call Symbol(printBool:func(TypeList(bool)):void) ($t37) */
    movq -232(%rbp), %rdi
    call printBool
    /* call Symbol(println:func(TypeList()):void) () */
    call println
    /* $t44 = false */
    movq $0, -248(%rbp)
    /* $t43 = $t44 */
    movq -248(%rbp), %r10
    movq %r10, -256(%rbp)
    /* jump $t43 */
    movq -256(%rbp), %r10
    cmp $1, %r10
    je L17
L18:
    /* $t42 = $t43 */
    movq -256(%rbp), %r10
    movq %r10, -264(%rbp)
    /* jump $t42 */
    movq -264(%rbp), %r10
    cmp $1, %r10
    je L19
    /* $t46 = true */
    movq $1, -272(%rbp)
    /* $t42 = $t46 */
    movq -272(%rbp), %r10
    movq %r10, -264(%rbp)
L19:
    /* call Symbol(printBool:func(TypeList(bool)):void) ($t42) */
    movq -264(%rbp), %rdi
    call printBool
    /* call Symbol(println:func(TypeList()):void) () */
    call println
    /* $t49 = true */
    movq $1, -280(%rbp)
    /* $t48 = $t49 */
    movq -280(%rbp), %r10
    movq %r10, -288(%rbp)
    /* jump $t48 */
    movq -288(%rbp), %r10
    cmp $1, %r10
    je L20
L21:
    /* $t47 = $t48 */
    movq -288(%rbp), %r10
    movq %r10, -296(%rbp)
    /* jump $t47 */
    movq -296(%rbp), %r10
    cmp $1, %r10
    je L22
    /* $t51 = false */
    movq $0, -304(%rbp)
    /* $t47 = $t51 */
    movq -304(%rbp), %r10
    movq %r10, -296(%rbp)
L22:
    /* call Symbol(printBool:func(TypeList(bool)):void) ($t47) */
    movq -296(%rbp), %rdi
    call printBool
    /* call Symbol(println:func(TypeList()):void) () */
    call println
    /* $t54 = true */
    movq $1, -312(%rbp)
    /* $t53 = $t54 */
    movq -312(%rbp), %r10
    movq %r10, -320(%rbp)
    /* jump $t53 */
    movq -320(%rbp), %r10
    cmp $1, %r10
    je L23
L24:
    /* $t52 = $t53 */
    movq -320(%rbp), %r10
    movq %r10, -328(%rbp)
    /* jump $t52 */
    movq -328(%rbp), %r10
    cmp $1, %r10
    je L25
    /* $t56 = true */
    movq $1, -336(%rbp)
    /* $t52 = $t56 */
    movq -336(%rbp), %r10
    movq %r10, -328(%rbp)
L25:
    /* call Symbol(printBool:func(TypeList(bool)):void) ($t52) */
    movq -328(%rbp), %rdi
    call printBool
    /* call Symbol(println:func(TypeList()):void) () */
    call println
    /* $t59 = true */
    movq $1, -344(%rbp)
    /* $t58 = $t59 */
    movq -344(%rbp), %r10
    movq %r10, -352(%rbp)
    /* jump $t58 */
    movq -352(%rbp), %r10
    cmp $1, %r10
    je L26
L27:
    /* $t57 = $t58 */
    movq -352(%rbp), %r10
    movq %r10, -360(%rbp)
    /* jump $t57 */
    movq -360(%rbp), %r10
    cmp $1, %r10
    je L28
    /* $t61 = false */
    movq $0, -368(%rbp)
    /* $t57 = $t61 */
    movq -368(%rbp), %r10
    movq %r10, -360(%rbp)
L28:
    /* call Symbol(printBool:func(TypeList(bool)):void) ($t57) */
    movq -360(%rbp), %rdi
    call printBool
    /* call Symbol(println:func(TypeList()):void) () */
    call println
    /* $t64 = true */
    movq $1, -376(%rbp)
    /* $t63 = $t64 */
    movq -376(%rbp), %r10
    movq %r10, -384(%rbp)
    /* jump $t63 */
    movq -384(%rbp), %r10
    cmp $1, %r10
    je L29
L30:
    /* $t62 = $t63 */
    movq -384(%rbp), %r10
    movq %r10, -392(%rbp)
    /* jump $t62 */
    movq -392(%rbp), %r10
    cmp $1, %r10
    je L31
    /* $t66 = true */
    movq $1, -400(%rbp)
    /* $t62 = $t66 */
    movq -400(%rbp), %r10
    movq %r10, -392(%rbp)
L31:
    /* call Symbol(printBool:func(TypeList(bool)):void) ($t62) */
    movq -392(%rbp), %rdi
    call printBool
    /* call Symbol(println:func(TypeList()):void) () */
    call println
    /* $t68 = false */
    movq $0, -408(%rbp)
    /* $t67 = $t68 */
    movq -408(%rbp), %r10
    movq %r10, -416(%rbp)
    /* jump $t67 */
    movq -416(%rbp), %r10
    cmp $1, %r10
    je L32
    /* $t70 = false */
    movq $0, -424(%rbp)
    /* $t69 = $t70 */
    movq -424(%rbp), %r10
    movq %r10, -432(%rbp)
    /* jump $t69 */
    movq -432(%rbp), %r10
    cmp $1, %r10
    je L54
L55:
    /* $t67 = $t69 */
    movq -432(%rbp), %r10
    movq %r10, -416(%rbp)
    jmp L32
L54:
    /* $t71 = false */
    movq $0, -440(%rbp)
    /* $t69 = $t71 */
    movq -440(%rbp), %r10
    movq %r10, -432(%rbp)
    jmp L55
L32:
    /* call Symbol(printBool:func(TypeList(bool)):void) ($t67) */
    movq -416(%rbp), %rdi
    call printBool
    /* call Symbol(println:func(TypeList()):void) () */
    call println
    /* $t73 = false */
    movq $0, -448(%rbp)
    /* $t72 = $t73 */
    movq -448(%rbp), %r10
    movq %r10, -456(%rbp)
    /* jump $t72 */
    movq -456(%rbp), %r10
    cmp $1, %r10
    je L33
    /* $t75 = false */
    movq $0, -464(%rbp)
    /* $t74 = $t75 */
    movq -464(%rbp), %r10
    movq %r10, -472(%rbp)
    /* jump $t74 */
    movq -472(%rbp), %r10
    cmp $1, %r10
    je L52
L53:
    /* $t72 = $t74 */
    movq -472(%rbp), %r10
    movq %r10, -456(%rbp)
    jmp L33
L52:
    /* $t76 = true */
    movq $1, -480(%rbp)
    /* $t74 = $t76 */
    movq -480(%rbp), %r10
    movq %r10, -472(%rbp)
    jmp L53
L33:
    /* call Symbol(printBool:func(TypeList(bool)):void) ($t72) */
    movq -456(%rbp), %rdi
    call printBool
    /* call Symbol(println:func(TypeList()):void) () */
    call println
    /* $t78 = false */
    movq $0, -488(%rbp)
    /* $t77 = $t78 */
    movq -488(%rbp), %r10
    movq %r10, -496(%rbp)
    /* jump $t77 */
    movq -496(%rbp), %r10
    cmp $1, %r10
    je L34
    /* $t80 = true */
    movq $1, -504(%rbp)
    /* $t79 = $t80 */
    movq -504(%rbp), %r10
    movq %r10, -512(%rbp)
    /* jump $t79 */
    movq -512(%rbp), %r10
    cmp $1, %r10
    je L50
L51:
    /* $t77 = $t79 */
    movq -512(%rbp), %r10
    movq %r10, -496(%rbp)
    jmp L34
L50:
    /* $t81 = false */
    movq $0, -520(%rbp)
    /* $t79 = $t81 */
    movq -520(%rbp), %r10
    movq %r10, -512(%rbp)
    jmp L51
L34:
    /* call Symbol(printBool:func(TypeList(bool)):void) ($t77) */
    movq -496(%rbp), %rdi
    call printBool
    /* call Symbol(println:func(TypeList()):void) () */
    call println
    /* $t83 = false */
    movq $0, -528(%rbp)
    /* $t82 = $t83 */
    movq -528(%rbp), %r10
    movq %r10, -536(%rbp)
    /* jump $t82 */
    movq -536(%rbp), %r10
    cmp $1, %r10
    je L35
    /* $t85 = true */
    movq $1, -544(%rbp)
    /* $t84 = $t85 */
    movq -544(%rbp), %r10
    movq %r10, -552(%rbp)
    /* jump $t84 */
    movq -552(%rbp), %r10
    cmp $1, %r10
    je L48
L49:
    /* $t82 = $t84 */
    movq -552(%rbp), %r10
    movq %r10, -536(%rbp)
    jmp L35
L48:
    /* $t86 = true */
    movq $1, -560(%rbp)
    /* $t84 = $t86 */
    movq -560(%rbp), %r10
    movq %r10, -552(%rbp)
    jmp L49
L35:
    /* call Symbol(printBool:func(TypeList(bool)):void) ($t82) */
    movq -536(%rbp), %rdi
    call printBool
    /* call Symbol(println:func(TypeList()):void) () */
    call println
    /* $t88 = true */
    movq $1, -568(%rbp)
    /* $t87 = $t88 */
    movq -568(%rbp), %r10
    movq %r10, -576(%rbp)
    /* jump $t87 */
    movq -576(%rbp), %r10
    cmp $1, %r10
    je L36
    /* $t90 = false */
    movq $0, -584(%rbp)
    /* $t89 = $t90 */
    movq -584(%rbp), %r10
    movq %r10, -592(%rbp)
    /* jump $t89 */
    movq -592(%rbp), %r10
    cmp $1, %r10
    je L46
L47:
    /* $t87 = $t89 */
    movq -592(%rbp), %r10
    movq %r10, -576(%rbp)
    jmp L36
L46:
    /* $t91 = false */
    movq $0, -600(%rbp)
    /* $t89 = $t91 */
    movq -600(%rbp), %r10
    movq %r10, -592(%rbp)
    jmp L47
L36:
    /* call Symbol(printBool:func(TypeList(bool)):void) ($t87) */
    movq -576(%rbp), %rdi
    call printBool
    /* call Symbol(println:func(TypeList()):void) () */
    call println
    /* $t93 = true */
    movq $1, -608(%rbp)
    /* $t92 = $t93 */
    movq -608(%rbp), %r10
    movq %r10, -616(%rbp)
    /* jump $t92 */
    movq -616(%rbp), %r10
    cmp $1, %r10
    je L37
    /* $t95 = false */
    movq $0, -624(%rbp)
    /* $t94 = $t95 */
    movq -624(%rbp), %r10
    movq %r10, -632(%rbp)
    /* jump $t94 */
    movq -632(%rbp), %r10
    cmp $1, %r10
    je L44
L45:
    /* $t92 = $t94 */
    movq -632(%rbp), %r10
    movq %r10, -616(%rbp)
    jmp L37
L44:
    /* $t96 = true */
    movq $1, -640(%rbp)
    /* $t94 = $t96 */
    movq -640(%rbp), %r10
    movq %r10, -632(%rbp)
    jmp L45
L37:
    /* call Symbol(printBool:func(TypeList(bool)):void) ($t92) */
    movq -616(%rbp), %rdi
    call printBool
    /* call Symbol(println:func(TypeList()):void) () */
    call println
    /* $t98 = true */
    movq $1, -648(%rbp)
    /* $t97 = $t98 */
    movq -648(%rbp), %r10
    movq %r10, -656(%rbp)
    /* jump $t97 */
    movq -656(%rbp), %r10
    cmp $1, %r10
    je L38
    /* $t100 = true */
    movq $1, -664(%rbp)
    /* $t99 = $t100 */
    movq -664(%rbp), %r10
    movq %r10, -672(%rbp)
    /* jump $t99 */
    movq -672(%rbp), %r10
    cmp $1, %r10
    je L42
L43:
    /* $t97 = $t99 */
    movq -672(%rbp), %r10
    movq %r10, -656(%rbp)
    jmp L38
L42:
    /* $t101 = false */
    movq $0, -680(%rbp)
    /* $t99 = $t101 */
    movq -680(%rbp), %r10
    movq %r10, -672(%rbp)
    jmp L43
L38:
    /* call Symbol(printBool:func(TypeList(bool)):void) ($t97) */
    movq -656(%rbp), %rdi
    call printBool
    /* call Symbol(println:func(TypeList()):void) () */
    call println
    /* $t103 = true */
    movq $1, -688(%rbp)
    /* $t102 = $t103 */
    movq -688(%rbp), %r10
    movq %r10, -696(%rbp)
    /* jump $t102 */
    movq -696(%rbp), %r10
    cmp $1, %r10
    je L39
    /* $t105 = true */
    movq $1, -704(%rbp)
    /* $t104 = $t105 */
    movq -704(%rbp), %r10
    movq %r10, -712(%rbp)
    /* jump $t104 */
    movq -712(%rbp), %r10
    cmp $1, %r10
    je L40
L41:
    /* $t102 = $t104 */
    movq -712(%rbp), %r10
    movq %r10, -696(%rbp)
    jmp L39
L40:
    /* $t106 = true */
    movq $1, -720(%rbp)
    /* $t104 = $t106 */
    movq -720(%rbp), %r10
    movq %r10, -712(%rbp)
    jmp L41
L39:
    /* call Symbol(printBool:func(TypeList(bool)):void) ($t102) */
    movq -696(%rbp), %rdi
    call printBool
    /* call Symbol(println:func(TypeList()):void) () */
    call println
    leave
    ret
L29:
    /* $t65 = true */
    movq $1, -728(%rbp)
    /* $t63 = $t65 */
    movq -728(%rbp), %r10
    movq %r10, -384(%rbp)
    jmp L30
L26:
    /* $t60 = true */
    movq $1, -736(%rbp)
    /* $t58 = $t60 */
    movq -736(%rbp), %r10
    movq %r10, -352(%rbp)
    jmp L27
L23:
    /* $t55 = false */
    movq $0, -744(%rbp)
    /* $t53 = $t55 */
    movq -744(%rbp), %r10
    movq %r10, -320(%rbp)
    jmp L24
L20:
    /* $t50 = false */
    movq $0, -752(%rbp)
    /* $t48 = $t50 */
    movq -752(%rbp), %r10
    movq %r10, -288(%rbp)
    jmp L21
L17:
    /* $t45 = true */
    movq $1, -760(%rbp)
    /* $t43 = $t45 */
    movq -760(%rbp), %r10
    movq %r10, -256(%rbp)
    jmp L18
L14:
    /* $t40 = true */
    movq $1, -768(%rbp)
    /* $t38 = $t40 */
    movq -768(%rbp), %r10
    movq %r10, -224(%rbp)
    jmp L15
L11:
    /* $t35 = false */
    movq $0, -776(%rbp)
    /* $t33 = $t35 */
    movq -776(%rbp), %r10
    movq %r10, -192(%rbp)
    jmp L12
L8:
    /* $t30 = false */
    movq $0, -784(%rbp)
    /* $t28 = $t30 */
    movq -784(%rbp), %r10
    movq %r10, -160(%rbp)
    jmp L9
L6:
    /* $t24 = 2 */
    movq $2, %r10
    movq %r10, -792(%rbp)
    /* $t25 = 1 */
    movq $1, %r10
    movq %r10, -800(%rbp)
    /* CompareInst */ 
    movq $0, %rax
    movq $1, %r10
    movq -792(%rbp), %r11
    cmp -800(%rbp), %r11
    cmovg %r10, %rax
    movq %rax, -808(%rbp)
    /* $t18 = $t26 */
    movq -808(%rbp), %r10
    movq %r10, -144(%rbp)
    jmp L7
L3:
    /* $t12 = 2 */
    movq $2, %r10
    movq %r10, -816(%rbp)
    /* $t13 = 1 */
    movq $1, %r10
    movq %r10, -824(%rbp)
    /* CompareInst */ 
    movq $0, %rax
    movq $1, %r10
    movq -816(%rbp), %r11
    cmp -824(%rbp), %r11
    cmovl %r10, %rax
    movq %rax, -832(%rbp)
    /* $t8 = $t14 */
    movq -832(%rbp), %r10
    movq %r10, -64(%rbp)
    jmp L4
L1:
    /* $t4 = 4 */
    movq $4, %r10
    movq %r10, -840(%rbp)
    /* $t5 = 2 */
    movq $2, %r10
    movq %r10, -848(%rbp)
    /* CompareInst */ 
    movq $0, %rax
    movq $1, %r10
    movq -840(%rbp), %r11
    cmp -848(%rbp), %r11
    cmovg %r10, %rax
    movq %rax, -856(%rbp)
    /* $t0 = $t6 */
    movq -856(%rbp), %r10
    movq %r10, -32(%rbp)
    jmp L2
