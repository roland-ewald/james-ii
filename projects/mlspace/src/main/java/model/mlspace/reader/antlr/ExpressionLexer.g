lexer grammar ExpressionLexer;

L_PAREN : '(';
R_PAREN : ')';
    
BECOMES : ':='; // assignment operator for constants/variables

FLOAT :
    ('0'..'9')+
    | ('0'..'9')+ '.' ('0'..'9')* EXPONENT?
    | '.' ('0'..'9')+ EXPONENT?
    | ('0'..'9')+ EXPONENT
    | 'Infinity';

PLUS  : '+';
MINUS : '-';
TIMES   : '*';
DIV   : '/';
POW   : '^';
SQR   : '\u00B2';
CUB   : '\u00B3';

MIN : 'min'|'Min'|'MIN';
MAX : 'max'|'Max'|'MAX';

IF : 'if'|'If'|'IF';
THEN : 'then'|'Then'|'THEN';
ELSE : 'else'|'Else'|'ELSE';

DEGREES : '\u00B0';
    
    
fragment EXPONENT :  ('e' | 'E' ) ('+' | '-')?('0'..'9')+;
    
ID : ( 'a'..'z' | 'A'..'Z')
     ( 'a'..'z' | 'A'..'Z' | '0'..'9' | '_')*;
    