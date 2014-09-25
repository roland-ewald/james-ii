grammar Carule;

@header {
/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.reader.antlr.parser;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.model.carules.CARule;
import org.jamesii.model.carules.ICACondition;
import org.jamesii.model.carules.grid.neighborhood.FreeNeighborhood;
import org.jamesii.model.carules.grid.neighborhood.INeighborhood;
import org.jamesii.model.carules.grid.neighborhood.MooreNeighborhood;
import org.jamesii.model.carules.grid.neighborhood.NeumannNeighborhood;

import org.antlr.runtime.*;

}
@lexer::header {
/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.reader.antlr.parser;
}


@lexer::members {
  private final List<CAProblemToken> problemTokens=new ArrayList<CAProblemToken>();

  @Override
  public void displayRecognitionError(String[] tokenNames,
                                      RecognitionException e) {
    String msg = getErrorMessage(e, tokenNames);
    if (e.token != null && e.token.getText() != null)
      problemTokens.add(new CAProblemToken(
          CAProblemToken.RECOGNITION_ERROR, ((CommonToken) e.token)
              .getStartIndex(), e.token.getText().length(), msg, e.token.getText()));
  }
  
  public Iterable<CAProblemToken> getProblemTokens() {
    return problemTokens;
  }
  
  @Override
  public Token nextToken() {
    return super.nextToken();
  }
}

@members {
  private List<String> states=new ArrayList<String>();
  private List<CARule> rules=new ArrayList<CARule>();
  private List<CAProblemToken> problemTokens=new ArrayList<CAProblemToken>();
  private int dimensions=1;
  private INeighborhood neighborhood=null;
  
  /**
   * Extracts text from javadoc style comments.
   * 
   * @param comment
   *          the comment
   * 
   * @return the extracted text
   */
  private static String extractTextFromComment(String comment) {
    if (comment == null)
      return null;
    return comment.replaceFirst("(?s)/[*][*][\n\r]*(.*?)[*]/", "$1").trim();
  }
    
  private boolean addState(String state) {
    if (state==null) return true;
    if (!states.contains(state))
      states.add(state);
    else 
      return false;
    return true;
  }
  
  private void addRule(CARule rule) {
    if (!rules.contains(rule) && rule!=null)
      rules.add(rule);
  }
  
  private void addProblemToken(int type, int tokenIndex, String msg) {
    if (tokenIndex < 0 || tokenIndex >= this.getTokenStream().size()) {
    } else {
      CommonToken t = ((CommonToken) this.getTokenStream().get(tokenIndex));
      problemTokens.add(new CAProblemToken(type, t.getStartIndex(), t.getText()
          .length(), msg, t.getText()));
    }
  }

  @Override
  public void displayRecognitionError(String[] tokenNames,
                                      RecognitionException e) {
    String msg = getErrorMessage(e, tokenNames);
    if (e.token != null && e.token.getText() != null)
		  problemTokens.add(new CAProblemToken(
		      CAProblemToken.RECOGNITION_ERROR, ((CommonToken) e.token)
		          .getStartIndex(), e.token.getText().length(), msg, e.token.getText()));
  }
  
  public String getCommentForToken(int i) {
    for (int j = i - 1; j >= 0; j--) {
      Token token = getTokenStream().get(j);
      if (token.getChannel() != HIDDEN)
        break;
      if (token.getType() == WHITESPACE || token.getType() == LINECOMMENT
          || token.getType() == GROUPCOMMENT) {
        // TODO sr137: concatenate to formating
      } else
      if (token.getType() == LANGUAGECOMMENT) {
        // TODO sr137: add comment to element
        return extractTextFromComment(token.getText());
      } else
        break;
    }
    return null;
  }
}

      camodel returns [List<String> states, List<CARule> rules, List<CAProblemToken> problems, double version, String versionComment, int dimensions, INeighborhood neighborhood, boolean isWolfram, int wolframRule]
      @init {
        states=new ArrayList<String>();
        rules=new ArrayList<CARule>();
        problemTokens=new ArrayList<CAProblemToken>();
        $states=states;
        $rules=rules;
        $problems=problemTokens;
        $version=-1d;
        $versionComment=null;
        $dimensions=1;
        $neighborhood=null;
        boolean neighborhoodDefined=false;
        $isWolfram=false;
        $wolframRule=0;
      }
      
      	:  (v=version {$version=v.version; $versionComment=v.comment;}) ((r=wolframrule {
																				      	       addState("DEAD");
																				      	       addState("ALIVE");
																				               addRule(new CARule(new BooleanCondition(true), new WolframCondition($r.rule),1,1));
																				               addRule(new CARule(new BooleanCondition(true), new AntiWolframCondition($r.rule),0,1));
																				               $neighborhood=new NeumannNeighborhood(1,null);
																				               neighborhood=$neighborhood;
																				               $isWolfram=true;
																				               $wolframRule=$r.rule;
      	                                             })
      	                              | ((d=dimension {
      	                                               $dimensions=$d.dimension; 
      	                                               dimensions=$d.dimension;
      	                                              }) 
      	                                (n=neighborhood {
      	                                                 $neighborhood=$n.neighborhood;
      	                                                 neighborhood=$neighborhood;
      	                                                 neighborhoodDefined=true;
      	                                                })? 
      	                                 statedef+ carule+) {
																											        if ($neighborhood==null && !neighborhoodDefined) {
																											          //use standard neumann neighborhood
																											          if (NeumannNeighborhood.isDimensionSupported($dimensions)) {
																											            $neighborhood=new NeumannNeighborhood($dimensions, null);
																											            neighborhood=$neighborhood;
																											          } else {
																											            addProblemToken(CAProblemToken.VALUE_OUT_OF_RANGE, $dimension.start.getTokenIndex(), "Default Neighborhood (Neumann) does not support the specified dimension.");
																											          }
																											        }
      	                                                    })  EOF 
        ;

      wolframrule returns [int rule]
      @init {
        $rule=0;
      }
        : WOLFRAMRULE COLON? i=INTEGER SEMICOLON {
          $rule=Integer.valueOf($i.text); 
          if ($rule<0 || $rule>255) {
            $rule=0;
            addProblemToken(CAProblemToken.VALUE_OUT_OF_RANGE, $i.index, $i.text+" not in range of 0 and 255");
          };
        }
        ;

      dimension returns [int dimension]
      @init {
        $dimension=1;
      }
        : DIMENSIONS COLON? (i=INTEGER) SEMICOLON {try {$dimension=Integer.valueOf($i.text);} catch (NumberFormatException e) {}} 
        ;

      version returns [double version, String comment]
        : CAVERSION (n=DOUBLE | n=INTEGER) SEMICOLON {
          $version=Double.valueOf($n.text);
          //check tokens before version for comments
          int i=$CAVERSION.getTokenIndex();
          $comment=getCommentForToken(i);
          if ($version!=1d) {
            addProblemToken(CAProblemToken.RECOGNITION_ERROR, $n.index, "Only verison 1.0 of CA grammar supported!");
          }
        }
        ;

      neighborhood returns [INeighborhood neighborhood]
        : NEIGHBORHOOD COLON? (
          MOORE {
            if (MooreNeighborhood.isDimensionSupported(dimensions)) {
              String comment=getCommentForToken($NEIGHBORHOOD.getTokenIndex());
              $neighborhood=new MooreNeighborhood(dimensions, comment);
            } else {
              addProblemToken(CAProblemToken.VALUE_OUT_OF_RANGE, $MOORE.index, "Moore neighborhood not supported for specified dimension -> "+dimensions);
            }
          }
         |NEUMANN {
            if (NeumannNeighborhood.isDimensionSupported(dimensions)) {
              String comment=getCommentForToken($NEIGHBORHOOD.getTokenIndex());
              $neighborhood=new NeumannNeighborhood(dimensions, null);
            } else {
              addProblemToken(CAProblemToken.VALUE_OUT_OF_RANGE, $MOORE.index, "Neumann neighborhood not supported for specified dimension -> "+dimensions);
            }
          }
         |FREE? {
            String comment=getCommentForToken($NEIGHBORHOOD.getTokenIndex());
         } 
         f=freeneighborhood[comment] {$neighborhood=$f.neighborhood;}) SEMICOLON
        ;

      freeneighborhood [String comment] returns [FreeNeighborhood neighborhood]
      @init {
        $neighborhood=new FreeNeighborhood(dimensions, $comment);
        List<Integer> l=new ArrayList<Integer>();
        int d=0; 
      }
        : LEFT_CURLEY ((LEFT_BRACE 
                                    i=INTEGER {
                                      try {
                                        l.add(Integer.valueOf($i.text)); 
                                        d++;
                                      } catch (NumberFormatException e) {
                                      }
                                    } 
                                    (COMMA? j=INTEGER {
                                      if (d>=dimensions) {
                                        addProblemToken(CAProblemToken.VALUE_OUT_OF_RANGE, $j.index, "Too many arguments for the specified dimension.");
                                      } else {
	                                      try {
	                                        l.add(Integer.valueOf($j.text));
	                                        d++;
	                                      } catch (NumberFormatException e) {
	                                      }
                                      }
                                    })* 
                        RIGHT_BRACE) {
                          if (l.size()<dimensions) {
                            addProblemToken(CAProblemToken.VALUE_OUT_OF_RANGE, $LEFT_BRACE.index, "Not enough arguments for specified dimension.");
                          } else
	                          try {
	                            Integer[] cell=l.toArray(new Integer[0]);
                                int[] c=new int[cell.length];
                                for (int k=0;k<c.length;k++)
                                  c[k]=cell[k].intValue();
	                            if ($neighborhood.containsCell(c)) {
                                addProblemToken(CAProblemToken.VALUE_OUT_OF_RANGE, $LEFT_BRACE.index, "Cell already in neighborhood.");
	                            } else
	                              $neighborhood.addCell(c); 
		                        } catch (IllegalArgumentException e) {
		                        }
                          l.clear();
                          d=0;
                        })+ RIGHT_CURLEY
        ;
         //TODO auto completion might show a 2D grid where somebody can select the cells that should be in the neighborhood or even a 3D view
      
      statedef
      	:	STATE COLON? i=ID {
          if (!addState($i.text)) {
            addProblemToken(CAProblemToken.STATE_ALREADY_DEFINED, $i.index, "State "+$i.text+" already defined!");
          }
        }
        
        (COMMA? j=ID {
          if (!addState($j.text)) {
            addProblemToken(CAProblemToken.STATE_ALREADY_DEFINED, $j.index, "State "+$j.text+" already defined!");
          }
      	})* SEMICOLON 
      	;

      carule
      @init {
        ICACondition preCondition=new BooleanCondition(true);
      }
        : prob=rule COLON (con=orexpression {preCondition=$con.condition;})? ARROW target=state SEMICOLON 
        { 
          String comment=getCommentForToken($rule.start.getTokenIndex());
          addRule(new CARule($prob.currentCondition, preCondition, states.indexOf($target.state), $prob.probability, comment));
        };
       
      rule returns [double probability, ICACondition currentCondition]
      @init{
        $probability=1d;
        $currentCondition=new BooleanCondition(true); 
      }
        : RULE (c=current {$currentCondition=$c.condition;})? (pr=probability  {$probability=$pr.probability;})?  
        ;
       
      current returns [CurrentStateCondition condition]
      @init { 
        $condition=new CurrentStateCondition();
      }
        : LEFT_CURLEY (i=ID {
                            int state=states.indexOf($i.text);
                            if (state<0) {
                              if ($i.index>=0)
                                addProblemToken(CAProblemToken.STATE_NOT_DEFINED, $i.index, "State '"+$i.text+"' not defined!");
                            } else
                              $condition.addState(state);
                          }) (COMMA? j=ID {
                            int state=states.indexOf($j.text);
                            if (state<0) {
                              if ($j.index>=0)
                                addProblemToken(CAProblemToken.STATE_NOT_DEFINED, $j.index, "State '"+$j.text+"' not defined!");
                            } else
                              $condition.addState(state);
                          })* RIGHT_CURLEY
        ; //TODO check ID for existence and for double listed IDs as well as return a current state condition with all listed IDs
       
      state returns [String state]
      	: ID {
	      	 $state=$ID.text;
	         if (!states.contains($state) && $ID.index>=0) {
	           addProblemToken(CAProblemToken.STATE_NOT_DEFINED, $ID.index, "State '"+$ID.text+"' not defined!");
	         }
      	}
      	;        
        
      orexpression returns [OrExpression condition]
        : (a=andexpression {$condition=new OrExpression($a.condition);}) (OR a1=andexpression {$condition.addCondition($a1.condition);})*
        ;
    
      andexpression returns [AndExpression condition]
        : (n=notexpression {$condition=new AndExpression($n.condition);}) (AND n1=notexpression {$condition.addCondition($n1.condition);})*
        ;
    
      notexpression returns [ICACondition condition]
        : NOT co=atom {$condition=new NotCondition($co.condition);} 
        | co=atom {$condition=$co.condition;} 
        ;
        
      atom returns [ICACondition condition]
        : con=condition  {$condition=$con.condition;}
        | LEFT_PAREN co=orexpression RIGHT_PAREN {$condition=$co.condition;}
        ;
    
      condition returns [ICACondition condition]
        : TRUE {$condition=new BooleanCondition(true);}
        | FALSE  {$condition=new BooleanCondition(false);}
        | id {$condition=new StateCondition(states.indexOf($id.state), $id.min, $id.max);}
        ;

      id returns [String state, int min, int max]
      @init {
        $min=1;
        $max=1;
      }      
	      : i=ID (LEFT_CURLEY a=amountcondition RIGHT_CURLEY {$min=$a.min; $max=$a.max;})? 
	      {
	       $state=$ID.text;
         if (!states.contains($state)) {
           addProblemToken(CAProblemToken.STATE_NOT_DEFINED, $i.index, "State '"+$i.text+"' not defined!");
         }
         //check whether min and max can ever match according to cell count in neighborhood
         if ((neighborhood!=null && neighborhood.getCellCount()<$min)) {
           addProblemToken(CAProblemToken.VALUE_OUT_OF_RANGE, $i.index, "State amount minimum higher than neighborhood size.");
         }
         if ($min>$max) {
           addProblemToken(CAProblemToken.VALUE_OUT_OF_RANGE, $i.index, "minimum value greater than maximum value.");
         }
	      } ;
	
	//TODO sr137: add method that can create a problem token from one index to another
	
      amountcondition returns [int min, int max]
      @init {
        $min=0;
        $max=Integer.MAX_VALUE;
      }
      	: (mi=minamount {$min=Integer.valueOf($mi.text);})? COMMA (ma=maxamount {$max=Integer.valueOf($ma.text);})? 
      	| e=equalsamount {$min=Integer.valueOf($e.text); $max=Integer.valueOf($e.text);}
      	;
      	
      minamount
      	:	INTEGER;
      	
      maxamount 
      	:	INTEGER;
      
      equalsamount
      	:	INTEGER;
	
      probability returns [double probability]
      	:	LEFT_BRACE (d=DOUBLE | d=INTEGER) RIGHT_BRACE { 
      	 $probability=Double.valueOf($d.text);
         if ($probability<0d || $probability>1d) {
           addProblemToken(CAProblemToken.VALUE_OUT_OF_RANGE, $d.index, "Probability "+$probability+" not in range between 0 and 1!");
         }
        }
      ;
      	

STATE	:	'state';
RULE	:	'rule';
WOLFRAMRULE
      : 'wolframrule';
DIMENSIONS 
      : 'dimensions';
NEIGHBORHOOD 
      : 'neighborhood'|'neighbourhood';
MOORE : 'moore';
NEUMANN
      : 'neumann';
FREE  : 'free';
ARROW	:	'->';
// Grouping
LEFT_PAREN
	    : '(';
RIGHT_PAREN
	    : ')';
LEFT_CURLEY
	    : '{';
RIGHT_CURLEY
	    : '}';
LEFT_BRACE
	    : '[';
RIGHT_BRACE
	    : ']';
COMMA	:	',';
COLON 	
      :	':';
SEMICOLON
	    :	';';
OR	  :	'|'|'||'|'or'|'+'|'v';
AND   :	'&'|'&&'|'and'|'^';
NOT	  :	'!'|'not'|'~';
TRUE	:	'true';
FALSE	:	'false';

INTEGER  
      : '-'?'0'..'9'+;

DOUBLE
	    : '-'?'0'..'9'* ('.' '0'..'9'*)?
	    ;

CAVERSION
      : '@' C A V E R S I O N
      ;  //@caversion insensitive to case so @CAVersion and so on are also matched       

//Identifier
ID	  : ('A'..'Z')+('A'..'Z'| '0'..'9' | '_')* ;

WHITESPACE
    	: (' ' | '\t' | '\r' | '\n' | '\f')+ {$channel=HIDDEN;}
    	;

LINECOMMENT
     	: ('//' (~('\n'|'\r'))* ('\r\n'|'\n'|'\r')?)  {$channel=HIDDEN;}
    	;

LANGUAGECOMMENT
      : '/**' ( options {greedy=false;} : . )* '*/'  {$channel=HIDDEN;}
      ;
     	 
GROUPCOMMENT
    	: '/*'~'*' ( options {greedy=false;} : . )* '*/'  {$channel = HIDDEN;}
    	;

UNEXPECTED
      : 'a'..'z'+ | .
      ;
      
fragment A:('a'|'A');
fragment B:('b'|'B');
fragment C:('c'|'C');
fragment D:('d'|'D');
fragment E:('e'|'E');
fragment F:('f'|'F');
fragment G:('g'|'G');
fragment H:('h'|'H');
fragment I:('i'|'I');
fragment J:('j'|'J');
fragment K:('k'|'K');
fragment L:('l'|'L');
fragment M:('m'|'M');
fragment N:('n'|'N');
fragment O:('o'|'O');
fragment P:('p'|'P');
fragment Q:('q'|'Q');
fragment R:('r'|'R');
fragment S:('s'|'S');
fragment T:('t'|'T');
fragment U:('u'|'U');
fragment V:('v'|'V');
fragment W:('w'|'W');
fragment X:('x'|'X');
fragment Y:('y'|'Y');
fragment Z:('z'|'Z');
  	  	
   	  	

