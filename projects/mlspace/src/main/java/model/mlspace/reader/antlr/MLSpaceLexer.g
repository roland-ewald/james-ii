lexer grammar MLSpaceLexer;

//options{
//  superClass=TokenAwareLexer;
//}

L_BRACKET : '[';
R_BRACKET : ']';
    
L_BRACE : '{'; 
R_BRACE : '}';

COLON : ':'; // seperator for attribute name and value
SEMIC : ';'; // seperator for attribute groups (spatial, other)
COMMA : ','; // seperator for attributes
ARROW :     '->';
AT  :    '@';
    
HASH : '#';    

EQ : '=';
LESSTHAN : '<';
GREATERTHAN : '>';

DOTS : '..'  | '...' ; // for ranges

DOT : '.';

FOR: 'FOR'|'for';

FREE: 'free';
OCC: ('occupied' | 'occ');
BIND: 'bind';
RELEASE: 'release';
REPLACE: 'replace';

IN: 'IN'|'in';

MODELNAMEKW : 'MLSpaceModel';
