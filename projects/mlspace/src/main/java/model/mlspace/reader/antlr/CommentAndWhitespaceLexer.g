lexer grammar CommentAndWhitespaceLexer;

COMMENT :
    '//'
    ~(
        '\n'
        | '\r'
     )*
    '\r'? '\n' 
               {
                $channel = HIDDEN;
               }
    | '/*' (options {greedy=false;}: .)* '*/' 
                                              {
                                               $channel = HIDDEN;
                                              };


WS :
    (
        ' '
        | '\t'
        | '\r'
        | '\n'
    )
    
     {
      $channel = HIDDEN;
     };
