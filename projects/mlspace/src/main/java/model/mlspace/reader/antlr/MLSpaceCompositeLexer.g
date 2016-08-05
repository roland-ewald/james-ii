lexer grammar MLSpaceCompositeLexer;
import MLSpaceLexer,ExpressionLexer,CommentAndWhitespaceLexer;

@header {
 package model.mlspace.reader.antlr;
 
 import java.util.logging.Level;
 import org.jamesii.core.util.logging.ApplicationLogger;
}

@members {
  @Override
  public void emitErrorMessage(String msg) {
    ApplicationLogger.log(Level.SEVERE, msg, 3);
  }
}

STRING : '"' ID '"' | '\'' ID '\'';