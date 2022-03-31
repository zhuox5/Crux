grammar Crux;
program
 : declarationList EOF
 ;

declarationList
 : declaration*
 ;

declaration
 : variableDeclaration
 | arrayDeclaration
 | functionDefinition
 ;

variableDeclaration
 : type Identifier ';'
 ;

type
 : Identifier
 ;

literal
 : Integer
 | True
 | False
 ;

designator
 : IDENTIFIER  '[' '[' expression0 ']' ']'
 ;

op0 : '>=' | '<=' | '!=' | '==' | '>' | '<' ;
op1 : '+' | '-' | '||' ;
op2 : '*' | '/' | '&&' ;

expression0 : expression1 op0 expression1;
expression1 : expression2
       | expression1 op1 expression2;
expression2 : expression3
       | expression2 op2 expression3;
expression3 : '!' expression3
       | '(' expression0 ')'
       | designator
       | call_expression
       | literal;

call_expression : IDENTIFIER '(' expression_list ')';
expression_list : '[' expression0 '{' ',' expression0 '}' ']';

parameter : type IDENTIFIER;
parameter_list : '[' parameter '{' ',' parameter '}' ']';

variable_declaration : type IDENTIFIER SemiColon;
arrayDeclaration : type IDENTIFIER '[' INTEGER ']' SemiColon;
functionDefinition : type IDENTIFIER '(' parameter_list ')' statement_block;
//declaration : variable_declaration | array_declaration | function_definition;
//declarationList : '{' declaration '}';
assignment_statement : designator '=' expression0 SemiColon;
assignment_statement_no_semi : designator '=' expression0;
call_statement : call_expression SemiColon;
if_statement : 'if' expression0 statement_block '[' 'else' statement_block ']';
for_statement : 'for' '(' assignment_statement expression0 SemiColon assignment_statement_no_semi')' statement_block;
break_statement : 'break' SemiColon ;
return_statement : 'return' expression0 SemiColon ;
statement : variable_declaration
           | call_statement
           | assignment_statement
           | if_statement
           | for_statement
           | break_statement
           | return_statement ;
statement_list : statement* ;
statement_block : '{' statement_list '}';



SemiColon: ';';
OPEN_PAREN: '(';
CLOSE_PAREN: ')';
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
OPEN_BRACKET: '[';
CLOSE_BRACKET: ']';
ADD: '+';
SUB: '-';
MUL: '*';
DIV: '/';
GREATER_EQUAL: '>=';
LESSER_EQUAL: '<=';
NOT_EQUAL: '!=';
EQUAL: '==';
GREATER_THAN: '>';
LESS_THAN: '<';
ASSIGN: '=';
COMMA: ',';
//SemiColon: ';';

AND: '&&';
OR: '||';
NOT: '!';
IF: 'if';
ELSE: 'else';
FOR: 'for';
BREAK: 'break';
RETURN: 'return';

Integer
 : '0'
 | [1-9] [0-9]*
 ;

True: 'true';
False: 'false';

Identifier
 : [a-zA-Z] [a-zA-Z0-9_]*
 ;

WhiteSpaces
 : [ \t\r\n]+ -> skip
 ;

Comment
 : '//' ~[\r\n]* -> skip
 ;
