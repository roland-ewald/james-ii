// $ANTLR 3.3 Nov 30, 2010 12:50:56 model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g

package model.mlspace.reader.antlr;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Set;
// import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import org.jamesii.core.util.collection.ArrayMap;
import org.jamesii.core.util.collection.ListUtils;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.core.math.match.*;
import org.jamesii.core.math.simpletree.*;
import org.jamesii.core.math.simpletree.logical.*;
import org.jamesii.core.math.simpletree.mixed.*;
import org.jamesii.core.math.simpletree.nullary.*;
import org.jamesii.core.math.simpletree.binary.*;
import org.jamesii.core.math.simpletree.unary.*;

import model.mlspace.MLSpaceModel;
import model.mlspace.entities.Species;
import model.mlspace.entities.RuleEntity;
import model.mlspace.entities.InitEntity;
import model.mlspace.entities.binding.BindingSites;
import model.mlspace.entities.binding.AllMatchingRuleEntity;
import model.mlspace.entities.binding.InitEntityWithBindings;
import model.mlspace.entities.binding.RuleEntityWithBindings;
import model.mlspace.entities.binding.RuleEntityWithBindings.BindingAction;
import model.mlspace.entities.values.AbstractValueRange;
import model.mlspace.entities.values.ValueMatchRange;
import model.mlspace.reader.*;
import model.mlspace.reader.ValueModifier.*;
import model.mlspace.reader.MLSpaceRuleCreator;
import model.mlspace.rules.RuleCollection;
import model.mlspace.rules.RuleSide;
import model.mlspace.rules.MLSpaceRule;

import org.jamesii.core.util.logging.ApplicationLogger;
import java.util.logging.Level;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;

public class MLSpaceSmallParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "L_BRACKET", "R_BRACKET", "L_BRACE", "R_BRACE", "COLON", "SEMIC", "COMMA", "ARROW", "AT", "HASH", "EQ", "LESSTHAN", "GREATERTHAN", "DOTS", "DOT", "FOR", "FREE", "OCC", "BIND", "RELEASE", "REPLACE", "IN", "MODELNAMEKW", "L_PAREN", "R_PAREN", "BECOMES", "EXPONENT", "FLOAT", "PLUS", "MINUS", "TIMES", "DIV", "POW", "SQR", "CUB", "MIN", "MAX", "IF", "THEN", "ELSE", "DEGREES", "ID", "COMMENT", "WS", "STRING", "Tokens", "NOTEQ", "INT"
    };
    public static final int EOF=-1;
    public static final int L_BRACKET=4;
    public static final int R_BRACKET=5;
    public static final int L_BRACE=6;
    public static final int R_BRACE=7;
    public static final int COLON=8;
    public static final int SEMIC=9;
    public static final int COMMA=10;
    public static final int ARROW=11;
    public static final int AT=12;
    public static final int HASH=13;
    public static final int EQ=14;
    public static final int LESSTHAN=15;
    public static final int GREATERTHAN=16;
    public static final int DOTS=17;
    public static final int DOT=18;
    public static final int FOR=19;
    public static final int FREE=20;
    public static final int OCC=21;
    public static final int BIND=22;
    public static final int RELEASE=23;
    public static final int REPLACE=24;
    public static final int IN=25;
    public static final int MODELNAMEKW=26;
    public static final int L_PAREN=27;
    public static final int R_PAREN=28;
    public static final int BECOMES=29;
    public static final int EXPONENT=30;
    public static final int FLOAT=31;
    public static final int PLUS=32;
    public static final int MINUS=33;
    public static final int TIMES=34;
    public static final int DIV=35;
    public static final int POW=36;
    public static final int SQR=37;
    public static final int CUB=38;
    public static final int MIN=39;
    public static final int MAX=40;
    public static final int IF=41;
    public static final int THEN=42;
    public static final int ELSE=43;
    public static final int DEGREES=44;
    public static final int ID=45;
    public static final int COMMENT=46;
    public static final int WS=47;
    public static final int STRING=48;
    public static final int Tokens=49;
    public static final int NOTEQ=50;
    public static final int INT=51;

    // delegates
    // delegators


        public MLSpaceSmallParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public MLSpaceSmallParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return MLSpaceSmallParser.tokenNames; }
    public String getGrammarFileName() { return "model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g"; }


    private final Map<String, AbstractValueRange<?>> variables = new NonNullMap<>();
    private final Map<String, Number> numVariables = new NonNullMap<>();
    private final Map<String, List<Double>> forLoopVariables = new NonNullMap<>();
    private final Set<String> actuallyUsedVariables = new NonNullSet<>();
    private boolean instantTransferOnInit = false;
    private MLSpaceParserHelper parseTool;
    private boolean varsAllowed;

    private String projectName;
    private String fileName;
    private Map<String,?> varOverrides = Collections.EMPTY_MAP;
    private Set<String> localRuleVariables = null;

    private static final Collection<String> RANDOM_VECTOR_KEYWORDS = Arrays.asList("rand","Rand","RAND");

      public MLSpaceSmallParser(String fileName, Map<String,?> parameters, boolean testFlag) throws java.io.IOException {
        this(new CommonTokenStream(new MLSpaceCompositeLexer(new ANTLRFileStream(fileName))),testFlag);
        this.projectName = fileName.substring(fileName.lastIndexOf("/")+1,
                                         fileName.lastIndexOf('.'));
        if (parameters != null) { this.varOverrides = parameters; }
        this.fileName = fileName;
      }

      /**
       * Wrap string in Token stream
       * @param toParse String to parse
       * @return TokenStream
       * @throws IOException
       */
      public static CommonTokenStream stringToTokenStream(String toParse) throws IOException {
        return new CommonTokenStream(new MLSpaceCompositeLexer(
                 new ANTLRInputStream(new ByteArrayInputStream(toParse.getBytes()))));
          }
      
      public MLSpaceSmallParser(String toParse, boolean testFlag) throws java.io.IOException {
        this(stringToTokenStream(toParse), testFlag);
        this.fileName = " (custom string to parse) " + toParse;
      }
      
      public MLSpaceSmallParser(TokenStream input,boolean testFlag) {
        this(input);
        this.parseTool = new MLSpaceParserHelper(testFlag);
        this.fileName = " (custom token stream to parse)";
      }
      
      
      public Map<String,?> getDefinedConstants() {
        return variables;
      }
      
      public Set<String> getUsedConstants() {
        return actuallyUsedVariables;
      }
      
      private void addVariable(String name, Object val) {
          variables.put(name,AbstractValueRange.newSingleValue(val));
          if (val instanceof Number)
            numVariables.put(name,((Number) val));
      }

      private void addVariable(String name, AbstractValueRange<?> vals) {
          variables.put(name,vals);
          if (vals.size()==1) {
            Object val = vals.getRandomValue(null);
            if (val instanceof Number)
              numVariables.put(name,((Number) val));
          }    
      }

      private void removeVariable(String name) {
          variables.remove(name);
          numVariables.remove(name);
      }
      
      Deque<String> forVarNames = new LinkedList<String>();
      Deque<Integer> forVarNextPos = new LinkedList<Integer>();
      Map<String, AbstractValueRange<?>> forVarOverridden = new NonNullMap<String, AbstractValueRange<?>>();

      /** @return true if range contains no values (and loop should be ignored) */
      private boolean handleForVar(String varName, List<?> varRange) {
        if (varName.equals(forVarNames.peek())) {
          // at least one loop has been run
          int pos = forVarNextPos.pop();
          addVariable(varName,varRange.get(pos));
          forVarNextPos.push(++pos);
          return false;
        }
        if (varRange.isEmpty()) { return true;}
        // variable is new
        if (variables.containsKey(varName)) {
          warnWithLine(Level.SEVERE,"Variable " + varName
              + " already defined. Previous value "
              + variables.get(varName)
              + " overridden in FOR loop expression by " + varRange);
          forVarOverridden.put(varName, variables.get(varName));
        }
        forVarNames.push(varName);
        forVarNextPos.push(1);
        addVariable(varName,varRange.get(0));
        return false;
        
      }
      
      private boolean wasLastLoop(String varName, List<?> varRange) {
        assert varName.equals(forVarNames.peek());
        if (varRange.size() > forVarNextPos.peek())
          return false;
        // else return true, but remove var from internal stack(s) first
        removeLastLoopVar(varName);
        return true;
      }
      
      private void removeLastLoopVar(String varName) {
        String poppedName = forVarNames.pop();
        assert poppedName.equals(varName);
        forVarNextPos.pop();
        AbstractValueRange<?> prevVal = forVarOverridden.remove(varName);
        if (prevVal != null) addVariable(varName,prevVal);
          else removeVariable(varName);
      }
      
      private Double getSingleNumValFromVar(String varname) {
        switch (varname) {
          case "PI":
          case "pi": return Math.PI;
          case "E":
          case "e": return Math.E;
          default:
            AbstractValueRange<?> vr = variables.get(varname);
            if (vr == null) return null;
            Double numVal;
            if (vr.size() == 1) { 
            Object value = vr.iterator().next();
            if (!(value instanceof Number)) return null;
            numVal = ((Number) value).doubleValue();
            } else {
              numVal = parseTool.getRandomValue(vr);
            }
            actuallyUsedVariables.add(varname);
            return numVal;
        }
      }
      
      @Override
      public void emitErrorMessage(String msg) {
        ApplicationLogger.log(Level.SEVERE, msg, 3);
      }
      
      public void warnWithLine(Level level, String msg) {
        Token token = input.LT(-1);
        ApplicationLogger.log(level,
            "in " + this.fileName + " at " + token.getLine() + ":"
                + token.getCharPositionInLine() + " " + token.getText() + "\n" + msg, 1);
      }
      
      private static final List<String> DEFAULT_OBS_ASPECT = Collections.singletonList("#");
      
      private final Collection<String> localRuleVars = new LinkedHashSet<>();
      
      private void addLocalRuleVar(String varName) {
        if (!localRuleVars.add(varName))
          warnWithLine(Level.SEVERE, "Local variable " + varName + " defined (at least) twice in same rule. One value will be ignored.");
      } 

      private void checkLocalRuleVar(String varName) {
        if (localRuleVars.add(varName))
          warnWithLine(Level.SEVERE, "Variable " + varName + " not defined in current rule. Match or assignment will fail.");
      } 



    public static class entsepleft_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "entsepleft"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:237:10: fragment entsepleft : PLUS ;
    public final MLSpaceSmallParser.entsepleft_return entsepleft() throws RecognitionException {
        MLSpaceSmallParser.entsepleft_return retval = new MLSpaceSmallParser.entsepleft_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token PLUS1=null;

        Object PLUS1_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:237:21: ( PLUS )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:237:23: PLUS
            {
            root_0 = (Object)adaptor.nil();

            PLUS1=(Token)match(input,PLUS,FOLLOW_PLUS_in_entsepleft55); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            PLUS1_tree = (Object)adaptor.create(PLUS1);
            adaptor.addChild(root_0, PLUS1_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "entsepleft"

    public static class entsepright_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "entsepright"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:238:10: fragment entsepright : ( PLUS | DOT );
    public final MLSpaceSmallParser.entsepright_return entsepright() throws RecognitionException {
        MLSpaceSmallParser.entsepright_return retval = new MLSpaceSmallParser.entsepright_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set2=null;

        Object set2_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:238:22: ( PLUS | DOT )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:
            {
            root_0 = (Object)adaptor.nil();

            set2=(Token)input.LT(1);
            if ( input.LA(1)==DOT||input.LA(1)==PLUS ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set2));
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "entsepright"

    public static class fullmodel_return extends ParserRuleReturnScope {
        public MLSpaceModel model;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "fullmodel"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:240:1: fullmodel returns [MLSpaceModel model] : variable_defs species_defs ( ( ( init )=>i1= init[false] ( SEMIC r1= rules )? ) | (r2= rules SEMIC i2= init[false] ) ) EOF ;
    public final MLSpaceSmallParser.fullmodel_return fullmodel() throws RecognitionException {
        MLSpaceSmallParser.fullmodel_return retval = new MLSpaceSmallParser.fullmodel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMIC5=null;
        Token SEMIC6=null;
        Token EOF7=null;
        MLSpaceSmallParser.init_return i1 = null;

        MLSpaceSmallParser.rules_return r1 = null;

        MLSpaceSmallParser.rules_return r2 = null;

        MLSpaceSmallParser.init_return i2 = null;

        MLSpaceSmallParser.variable_defs_return variable_defs3 = null;

        MLSpaceSmallParser.species_defs_return species_defs4 = null;


        Object SEMIC5_tree=null;
        Object SEMIC6_tree=null;
        Object EOF7_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:242:1: ( variable_defs species_defs ( ( ( init )=>i1= init[false] ( SEMIC r1= rules )? ) | (r2= rules SEMIC i2= init[false] ) ) EOF )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:242:3: variable_defs species_defs ( ( ( init )=>i1= init[false] ( SEMIC r1= rules )? ) | (r2= rules SEMIC i2= init[false] ) ) EOF
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variable_defs_in_fullmodel86);
            variable_defs3=variable_defs();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variable_defs3.getTree());
            pushFollow(FOLLOW_species_defs_in_fullmodel90);
            species_defs4=species_defs();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, species_defs4.getTree());
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:244:2: ( ( ( init )=>i1= init[false] ( SEMIC r1= rules )? ) | (r2= rules SEMIC i2= init[false] ) )
            int alt2=2;
            alt2 = dfa2.predict(input);
            switch (alt2) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:244:3: ( ( init )=>i1= init[false] ( SEMIC r1= rules )? )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:244:3: ( ( init )=>i1= init[false] ( SEMIC r1= rules )? )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:244:4: ( init )=>i1= init[false] ( SEMIC r1= rules )?
                    {
                    pushFollow(FOLLOW_init_in_fullmodel102);
                    i1=init(false);

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, i1.getTree());
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:244:28: ( SEMIC r1= rules )?
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==SEMIC) ) {
                        alt1=1;
                    }
                    switch (alt1) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:244:29: SEMIC r1= rules
                            {
                            SEMIC5=(Token)match(input,SEMIC,FOLLOW_SEMIC_in_fullmodel106); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            SEMIC5_tree = (Object)adaptor.create(SEMIC5);
                            adaptor.addChild(root_0, SEMIC5_tree);
                            }
                            pushFollow(FOLLOW_rules_in_fullmodel110);
                            r1=rules();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, r1.getTree());

                            }
                            break;

                    }

                    if ( state.backtracking==0 ) {
                      retval.model = parseTool.getModel((i1!=null?i1.map:null),(r1!=null?r1.rules:null),instantTransferOnInit,variables);
                    }

                    }


                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:245:3: (r2= rules SEMIC i2= init[false] )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:245:3: (r2= rules SEMIC i2= init[false] )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:245:4: r2= rules SEMIC i2= init[false]
                    {
                    pushFollow(FOLLOW_rules_in_fullmodel123);
                    r2=rules();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, r2.getTree());
                    SEMIC6=(Token)match(input,SEMIC,FOLLOW_SEMIC_in_fullmodel125); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SEMIC6_tree = (Object)adaptor.create(SEMIC6);
                    adaptor.addChild(root_0, SEMIC6_tree);
                    }
                    pushFollow(FOLLOW_init_in_fullmodel129);
                    i2=init(false);

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, i2.getTree());
                    if ( state.backtracking==0 ) {
                      retval.model = parseTool.getModel((i2!=null?i2.map:null),(r2!=null?r2.rules:null),instantTransferOnInit,variables);
                    }

                    }


                    }
                    break;

            }

            EOF7=(Token)match(input,EOF,FOLLOW_EOF_in_fullmodel138); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            EOF7_tree = (Object)adaptor.create(EOF7);
            adaptor.addChild(root_0, EOF7_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) {
              retval.model.setName(projectName);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "fullmodel"

    public static class variable_defs_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "variable_defs"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:248:1: variable_defs : ( variable_def ( SEMIC )? )* ;
    public final MLSpaceSmallParser.variable_defs_return variable_defs() throws RecognitionException {
        MLSpaceSmallParser.variable_defs_return retval = new MLSpaceSmallParser.variable_defs_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMIC9=null;
        MLSpaceSmallParser.variable_def_return variable_def8 = null;


        Object SEMIC9_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:249:1: ( ( variable_def ( SEMIC )? )* )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:249:3: ( variable_def ( SEMIC )? )*
            {
            root_0 = (Object)adaptor.nil();

            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:249:3: ( variable_def ( SEMIC )? )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==ID) ) {
                    int LA4_1 = input.LA(2);

                    if ( (LA4_1==EQ) ) {
                        alt4=1;
                    }


                }


                switch (alt4) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:249:4: variable_def ( SEMIC )?
            	    {
            	    pushFollow(FOLLOW_variable_def_in_variable_defs147);
            	    variable_def8=variable_def();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, variable_def8.getTree());
            	    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:249:22: ( SEMIC )?
            	    int alt3=2;
            	    int LA3_0 = input.LA(1);

            	    if ( (LA3_0==SEMIC) ) {
            	        alt3=1;
            	    }
            	    switch (alt3) {
            	        case 1 :
            	            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:0:0: SEMIC
            	            {
            	            SEMIC9=(Token)match(input,SEMIC,FOLLOW_SEMIC_in_variable_defs149); if (state.failed) return retval;

            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "variable_defs"

    public static class variable_def_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "variable_def"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:251:1: variable_def : ID EQ n= valset_or_const ;
    public final MLSpaceSmallParser.variable_def_return variable_def() throws RecognitionException {
        MLSpaceSmallParser.variable_def_return retval = new MLSpaceSmallParser.variable_def_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID10=null;
        Token EQ11=null;
        MLSpaceSmallParser.valset_or_const_return n = null;


        Object ID10_tree=null;
        Object EQ11_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:251:14: ( ID EQ n= valset_or_const )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:251:16: ID EQ n= valset_or_const
            {
            root_0 = (Object)adaptor.nil();

            ID10=(Token)match(input,ID,FOLLOW_ID_in_variable_def161); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID10_tree = (Object)adaptor.create(ID10);
            adaptor.addChild(root_0, ID10_tree);
            }
            EQ11=(Token)match(input,EQ,FOLLOW_EQ_in_variable_def163); if (state.failed) return retval;
            pushFollow(FOLLOW_valset_or_const_in_variable_def168);
            n=valset_or_const();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, n.getTree());
            if ( state.backtracking==0 ) {
               Object ovVal = varOverrides.get((ID10!=null?ID10.getText():null));
                if (ovVal == null)
                  addVariable((ID10!=null?ID10.getText():null),(n!=null?n.val:null));
                else if (ovVal instanceof Number) 
                    addVariable((ID10!=null?ID10.getText():null),AbstractValueRange.newSingleValue(((Number) ovVal).doubleValue()));
                else
                    addVariable((ID10!=null?ID10.getText():null),AbstractValueRange.newSingleValue(((String) ovVal)));
                if ((ID10!=null?ID10.getText():null).equalsIgnoreCase(MLSpaceParserHelper.PERIODIC_BOUNDARIES_SETTING) &&
                  parseTool.setPeriodicBoundaries(variables.get((ID10!=null?ID10.getText():null)))!=null) {
                  actuallyUsedVariables.add((ID10!=null?ID10.getText():null));
                }
                if (MLSpaceParserHelper.INSTANT_TRANSFER_ON_INIT.contains((ID10!=null?ID10.getText():null).toLowerCase())) {
                  actuallyUsedVariables.add((ID10!=null?ID10.getText():null));
                  if (Arrays.asList("true", "1", true, 1.0, "yes", "TRUE").containsAll(variables.get((ID10!=null?ID10.getText():null)).toList())) {
                     instantTransferOnInit = true;
                  }
                }  
               
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "variable_def"

    public static class valset_or_const_return extends ParserRuleReturnScope {
        public AbstractValueRange<?> val;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "valset_or_const"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:271:1: valset_or_const returns [AbstractValueRange<?> val] : ( ( ( interval )=> interval ) | ( ( range )=> range ) | ( ( set )=> set ) | ( ( vector )=> vector ) | ( numexpr ) | ( STRING ) | ( ID ) );
    public final MLSpaceSmallParser.valset_or_const_return valset_or_const() throws RecognitionException {
        MLSpaceSmallParser.valset_or_const_return retval = new MLSpaceSmallParser.valset_or_const_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING17=null;
        Token ID18=null;
        MLSpaceSmallParser.interval_return interval12 = null;

        MLSpaceSmallParser.range_return range13 = null;

        MLSpaceSmallParser.set_return set14 = null;

        MLSpaceSmallParser.vector_return vector15 = null;

        MLSpaceSmallParser.numexpr_return numexpr16 = null;


        Object STRING17_tree=null;
        Object ID18_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:271:52: ( ( ( interval )=> interval ) | ( ( range )=> range ) | ( ( set )=> set ) | ( ( vector )=> vector ) | ( numexpr ) | ( STRING ) | ( ID ) )
            int alt5=7;
            alt5 = dfa5.predict(input);
            switch (alt5) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:272:2: ( ( interval )=> interval )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:272:2: ( ( interval )=> interval )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:272:3: ( interval )=> interval
                    {
                    pushFollow(FOLLOW_interval_in_valset_or_const189);
                    interval12=interval();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, interval12.getTree());
                    if ( state.backtracking==0 ) {
                      retval.val =AbstractValueRange.newInterval((interval12!=null?interval12.lower:0.0),(interval12!=null?interval12.upper:0.0),(interval12!=null?interval12.incLower:false),(interval12!=null?interval12.incUpper:false));
                    }

                    }


                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:273:2: ( ( range )=> range )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:273:2: ( ( range )=> range )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:273:3: ( range )=> range
                    {
                    pushFollow(FOLLOW_range_in_valset_or_const201);
                    range13=range();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, range13.getTree());
                    if ( state.backtracking==0 ) {
                      retval.val =AbstractValueRange.newRange((range13!=null?range13.lower:0.0),(range13!=null?range13.step:0.0),(range13!=null?range13.upper:0.0));
                    }

                    }


                    }
                    break;
                case 3 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:274:2: ( ( set )=> set )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:274:2: ( ( set )=> set )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:274:3: ( set )=> set
                    {
                    pushFollow(FOLLOW_set_in_valset_or_const213);
                    set14=set();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, set14.getTree());
                    if ( state.backtracking==0 ) {
                      retval.val =AbstractValueRange.newSet((set14!=null?set14.set:null));
                    }

                    }


                    }
                    break;
                case 4 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:275:2: ( ( vector )=> vector )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:275:2: ( ( vector )=> vector )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:275:3: ( vector )=> vector
                    {
                    pushFollow(FOLLOW_vector_in_valset_or_const225);
                    vector15=vector();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, vector15.getTree());
                    if ( state.backtracking==0 ) {
                      retval.val =AbstractValueRange.newSingleValue((vector15!=null?vector15.vec:null));
                    }

                    }


                    }
                    break;
                case 5 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:276:2: ( numexpr )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:276:2: ( numexpr )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:276:3: numexpr
                    {
                    pushFollow(FOLLOW_numexpr_in_valset_or_const232);
                    numexpr16=numexpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, numexpr16.getTree());
                    if ( state.backtracking==0 ) {
                      retval.val =AbstractValueRange.newSingleValue((numexpr16!=null?numexpr16.val:null));
                    }

                    }


                    }
                    break;
                case 6 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:277:2: ( STRING )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:277:2: ( STRING )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:277:3: STRING
                    {
                    STRING17=(Token)match(input,STRING,FOLLOW_STRING_in_valset_or_const239); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    STRING17_tree = (Object)adaptor.create(STRING17);
                    adaptor.addChild(root_0, STRING17_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.val =AbstractValueRange.newSingleValue((STRING17!=null?STRING17.getText():null));
                    }

                    }


                    }
                    break;
                case 7 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:278:2: ( ID )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:278:2: ( ID )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:278:3: ID
                    {
                    ID18=(Token)match(input,ID,FOLLOW_ID_in_valset_or_const246); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ID18_tree = (Object)adaptor.create(ID18);
                    adaptor.addChild(root_0, ID18_tree);
                    }
                    if ( state.backtracking==0 ) {
                      if (variables.containsKey((ID18!=null?ID18.getText():null))) {retval.val =variables.get((ID18!=null?ID18.getText():null)); actuallyUsedVariables.add((ID18!=null?ID18.getText():null));} else retval.val =AbstractValueRange.newSingleValue((ID18!=null?ID18.getText():null));
                    }

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "valset_or_const"

    public static class attributes_match_return extends ParserRuleReturnScope {
        public Map<String,Pair<? extends ValueMatch,String>> attMap;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "attributes_match"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:280:1: attributes_match returns [Map<String,Pair<? extends ValueMatch,String>> attMap] : a1= attribute_match ( COMMA a2= attribute_match )* ;
    public final MLSpaceSmallParser.attributes_match_return attributes_match() throws RecognitionException {
        MLSpaceSmallParser.attributes_match_return retval = new MLSpaceSmallParser.attributes_match_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COMMA19=null;
        MLSpaceSmallParser.attribute_match_return a1 = null;

        MLSpaceSmallParser.attribute_match_return a2 = null;


        Object COMMA19_tree=null;

        retval.attMap = new NonNullMap<>();
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:281:38: (a1= attribute_match ( COMMA a2= attribute_match )* )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:282:1: a1= attribute_match ( COMMA a2= attribute_match )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_attribute_match_in_attributes_match271);
            a1=attribute_match();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, a1.getTree());
            if ( state.backtracking==0 ) {
              retval.attMap.put((a1!=null?a1.name:null),new Pair<>((a1!=null?a1.val:null),(a1!=null?a1.varName:null)));
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:283:1: ( COMMA a2= attribute_match )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==COMMA) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:283:2: COMMA a2= attribute_match
            	    {
            	    COMMA19=(Token)match(input,COMMA,FOLLOW_COMMA_in_attributes_match276); if (state.failed) return retval;
            	    pushFollow(FOLLOW_attribute_match_in_attributes_match281);
            	    a2=attribute_match();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, a2.getTree());
            	    if ( state.backtracking==0 ) {
            	      retval.attMap.put((a2!=null?a2.name:null),new Pair<>((a2!=null?a2.val:null),(a2!=null?a2.varName:null)));
            	    }

            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "attributes_match"

    public static class attribute_match_return extends ParserRuleReturnScope {
        public String name;
        public ValueMatch val;
        public String varName;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "attribute_match"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:286:1: attribute_match returns [String name, ValueMatch val,String varName] : ( ( L_PAREN varn= ID EQ attn= ID (vari= var_interval )? R_PAREN ) | (n1= ID ( ( EQ att1= ID (vi1= var_interval )? ) | (vi2= var_interval ) ) ) );
    public final MLSpaceSmallParser.attribute_match_return attribute_match() throws RecognitionException {
        MLSpaceSmallParser.attribute_match_return retval = new MLSpaceSmallParser.attribute_match_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token varn=null;
        Token attn=null;
        Token n1=null;
        Token att1=null;
        Token L_PAREN20=null;
        Token EQ21=null;
        Token R_PAREN22=null;
        Token EQ23=null;
        MLSpaceSmallParser.var_interval_return vari = null;

        MLSpaceSmallParser.var_interval_return vi1 = null;

        MLSpaceSmallParser.var_interval_return vi2 = null;


        Object varn_tree=null;
        Object attn_tree=null;
        Object n1_tree=null;
        Object att1_tree=null;
        Object L_PAREN20_tree=null;
        Object EQ21_tree=null;
        Object R_PAREN22_tree=null;
        Object EQ23_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:286:69: ( ( L_PAREN varn= ID EQ attn= ID (vari= var_interval )? R_PAREN ) | (n1= ID ( ( EQ att1= ID (vi1= var_interval )? ) | (vi2= var_interval ) ) ) )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==L_PAREN) ) {
                alt10=1;
            }
            else if ( (LA10_0==ID) ) {
                alt10=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:287:3: ( L_PAREN varn= ID EQ attn= ID (vari= var_interval )? R_PAREN )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:287:3: ( L_PAREN varn= ID EQ attn= ID (vari= var_interval )? R_PAREN )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:287:4: L_PAREN varn= ID EQ attn= ID (vari= var_interval )? R_PAREN
                    {
                    L_PAREN20=(Token)match(input,L_PAREN,FOLLOW_L_PAREN_in_attribute_match300); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    L_PAREN20_tree = (Object)adaptor.create(L_PAREN20);
                    adaptor.addChild(root_0, L_PAREN20_tree);
                    }
                    varn=(Token)match(input,ID,FOLLOW_ID_in_attribute_match304); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    varn_tree = (Object)adaptor.create(varn);
                    adaptor.addChild(root_0, varn_tree);
                    }
                    EQ21=(Token)match(input,EQ,FOLLOW_EQ_in_attribute_match306); if (state.failed) return retval;
                    attn=(Token)match(input,ID,FOLLOW_ID_in_attribute_match311); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    attn_tree = (Object)adaptor.create(attn);
                    adaptor.addChild(root_0, attn_tree);
                    }
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:287:32: (vari= var_interval )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( ((LA7_0>=EQ && LA7_0<=GREATERTHAN)||LA7_0==IN) ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:287:33: vari= var_interval
                            {
                            pushFollow(FOLLOW_var_interval_in_attribute_match316);
                            vari=var_interval();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, vari.getTree());

                            }
                            break;

                    }

                    R_PAREN22=(Token)match(input,R_PAREN,FOLLOW_R_PAREN_in_attribute_match320); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    R_PAREN22_tree = (Object)adaptor.create(R_PAREN22);
                    adaptor.addChild(root_0, R_PAREN22_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.name =(attn!=null?attn.getText():null);retval.val =(vari!=null?vari.val:null); retval.varName =(varn!=null?varn.getText():null);addLocalRuleVar(retval.varName);
                    }

                    }


                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:289:3: (n1= ID ( ( EQ att1= ID (vi1= var_interval )? ) | (vi2= var_interval ) ) )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:289:3: (n1= ID ( ( EQ att1= ID (vi1= var_interval )? ) | (vi2= var_interval ) ) )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:289:4: n1= ID ( ( EQ att1= ID (vi1= var_interval )? ) | (vi2= var_interval ) )
                    {
                    n1=(Token)match(input,ID,FOLLOW_ID_in_attribute_match334); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    n1_tree = (Object)adaptor.create(n1);
                    adaptor.addChild(root_0, n1_tree);
                    }
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:289:10: ( ( EQ att1= ID (vi1= var_interval )? ) | (vi2= var_interval ) )
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==EQ) ) {
                        int LA9_1 = input.LA(2);

                        if ( (LA9_1==ID) ) {
                            alt9=1;
                        }
                        else if ( (LA9_1==EQ) ) {
                            alt9=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 9, 1, input);

                            throw nvae;
                        }
                    }
                    else if ( ((LA9_0>=LESSTHAN && LA9_0<=GREATERTHAN)||LA9_0==IN) ) {
                        alt9=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 9, 0, input);

                        throw nvae;
                    }
                    switch (alt9) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:289:11: ( EQ att1= ID (vi1= var_interval )? )
                            {
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:289:11: ( EQ att1= ID (vi1= var_interval )? )
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:289:12: EQ att1= ID (vi1= var_interval )?
                            {
                            EQ23=(Token)match(input,EQ,FOLLOW_EQ_in_attribute_match338); if (state.failed) return retval;
                            att1=(Token)match(input,ID,FOLLOW_ID_in_attribute_match343); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            att1_tree = (Object)adaptor.create(att1);
                            adaptor.addChild(root_0, att1_tree);
                            }
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:289:24: (vi1= var_interval )?
                            int alt8=2;
                            int LA8_0 = input.LA(1);

                            if ( ((LA8_0>=EQ && LA8_0<=GREATERTHAN)||LA8_0==IN) ) {
                                alt8=1;
                            }
                            switch (alt8) {
                                case 1 :
                                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:289:25: vi1= var_interval
                                    {
                                    pushFollow(FOLLOW_var_interval_in_attribute_match348);
                                    vi1=var_interval();

                                    state._fsp--;
                                    if (state.failed) return retval;
                                    if ( state.backtracking==0 ) adaptor.addChild(root_0, vi1.getTree());

                                    }
                                    break;

                            }

                            if ( state.backtracking==0 ) {

                                           retval.name =(att1!=null?att1.getText():null);retval.val =(vi1!=null?vi1.val:null); retval.varName =(n1!=null?n1.getText():null);addLocalRuleVar(retval.varName);
                                           if (retval.val != null)
                                             warnWithLine(Level.WARNING, "Grouping variable assignment " + (n1!=null?n1.getText():null) +"="+(att1!=null?att1.getText():null) +" in parenthesis is preferable.\n");
                                          
                            }

                            }


                            }
                            break;
                        case 2 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:295:11: (vi2= var_interval )
                            {
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:295:11: (vi2= var_interval )
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:295:12: vi2= var_interval
                            {
                            pushFollow(FOLLOW_var_interval_in_attribute_match381);
                            vi2=var_interval();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, vi2.getTree());
                            if ( state.backtracking==0 ) {
                              retval.name =(n1!=null?n1.getText():null);retval.val =(vi2!=null?vi2.val:null);
                            }

                            }


                            }
                            break;

                    }


                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "attribute_match"

    public static class var_interval_return extends ParserRuleReturnScope {
        public ValueMatch val;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "var_interval"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:297:1: var_interval returns [ValueMatch val] : ( ( EQ EQ varexpr ) | ( EQ EQ STRING ) | ( GREATERTHAN EQ varexpr ) | ( GREATERTHAN varexpr ) | ( LESSTHAN EQ varexpr ) | ( LESSTHAN varexpr ) | ( IN l= ( L_PAREN | L_BRACKET ) low= varexpr ( COMMA | DOTS ) up= varexpr r= ( R_PAREN | R_BRACKET ) ) );
    public final MLSpaceSmallParser.var_interval_return var_interval() throws RecognitionException {
        MLSpaceSmallParser.var_interval_return retval = new MLSpaceSmallParser.var_interval_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token l=null;
        Token r=null;
        Token EQ24=null;
        Token EQ25=null;
        Token EQ27=null;
        Token EQ28=null;
        Token STRING29=null;
        Token GREATERTHAN30=null;
        Token EQ31=null;
        Token GREATERTHAN33=null;
        Token LESSTHAN35=null;
        Token EQ36=null;
        Token LESSTHAN38=null;
        Token IN40=null;
        Token set41=null;
        MLSpaceSmallParser.varexpr_return low = null;

        MLSpaceSmallParser.varexpr_return up = null;

        MLSpaceSmallParser.varexpr_return varexpr26 = null;

        MLSpaceSmallParser.varexpr_return varexpr32 = null;

        MLSpaceSmallParser.varexpr_return varexpr34 = null;

        MLSpaceSmallParser.varexpr_return varexpr37 = null;

        MLSpaceSmallParser.varexpr_return varexpr39 = null;


        Object l_tree=null;
        Object r_tree=null;
        Object EQ24_tree=null;
        Object EQ25_tree=null;
        Object EQ27_tree=null;
        Object EQ28_tree=null;
        Object STRING29_tree=null;
        Object GREATERTHAN30_tree=null;
        Object EQ31_tree=null;
        Object GREATERTHAN33_tree=null;
        Object LESSTHAN35_tree=null;
        Object EQ36_tree=null;
        Object LESSTHAN38_tree=null;
        Object IN40_tree=null;
        Object set41_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:297:38: ( ( EQ EQ varexpr ) | ( EQ EQ STRING ) | ( GREATERTHAN EQ varexpr ) | ( GREATERTHAN varexpr ) | ( LESSTHAN EQ varexpr ) | ( LESSTHAN varexpr ) | ( IN l= ( L_PAREN | L_BRACKET ) low= varexpr ( COMMA | DOTS ) up= varexpr r= ( R_PAREN | R_BRACKET ) ) )
            int alt11=7;
            alt11 = dfa11.predict(input);
            switch (alt11) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:298:3: ( EQ EQ varexpr )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:298:3: ( EQ EQ varexpr )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:298:4: EQ EQ varexpr
                    {
                    EQ24=(Token)match(input,EQ,FOLLOW_EQ_in_var_interval400); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQ24_tree = (Object)adaptor.create(EQ24);
                    adaptor.addChild(root_0, EQ24_tree);
                    }
                    EQ25=(Token)match(input,EQ,FOLLOW_EQ_in_var_interval402); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQ25_tree = (Object)adaptor.create(EQ25);
                    adaptor.addChild(root_0, EQ25_tree);
                    }
                    pushFollow(FOLLOW_varexpr_in_var_interval404);
                    varexpr26=varexpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, varexpr26.getTree());
                    if ( state.backtracking==0 ) {
                      retval.val = ValueMatches.newEquals((varexpr26!=null?varexpr26.node:null));
                    }

                    }


                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:299:3: ( EQ EQ STRING )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:299:3: ( EQ EQ STRING )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:299:4: EQ EQ STRING
                    {
                    EQ27=(Token)match(input,EQ,FOLLOW_EQ_in_var_interval412); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQ27_tree = (Object)adaptor.create(EQ27);
                    adaptor.addChild(root_0, EQ27_tree);
                    }
                    EQ28=(Token)match(input,EQ,FOLLOW_EQ_in_var_interval414); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQ28_tree = (Object)adaptor.create(EQ28);
                    adaptor.addChild(root_0, EQ28_tree);
                    }
                    STRING29=(Token)match(input,STRING,FOLLOW_STRING_in_var_interval416); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    STRING29_tree = (Object)adaptor.create(STRING29);
                    adaptor.addChild(root_0, STRING29_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.val = ValueMatches.newEqualsString(STRING29.getText());
                    }

                    }


                    }
                    break;
                case 3 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:300:3: ( GREATERTHAN EQ varexpr )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:300:3: ( GREATERTHAN EQ varexpr )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:300:4: GREATERTHAN EQ varexpr
                    {
                    GREATERTHAN30=(Token)match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_var_interval424); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATERTHAN30_tree = (Object)adaptor.create(GREATERTHAN30);
                    adaptor.addChild(root_0, GREATERTHAN30_tree);
                    }
                    EQ31=(Token)match(input,EQ,FOLLOW_EQ_in_var_interval426); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQ31_tree = (Object)adaptor.create(EQ31);
                    adaptor.addChild(root_0, EQ31_tree);
                    }
                    pushFollow(FOLLOW_varexpr_in_var_interval428);
                    varexpr32=varexpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, varexpr32.getTree());
                    if ( state.backtracking==0 ) {
                      retval.val = new RangeMatches.GreaterOrEqual((varexpr32!=null?varexpr32.node:null));
                    }

                    }


                    }
                    break;
                case 4 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:301:3: ( GREATERTHAN varexpr )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:301:3: ( GREATERTHAN varexpr )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:301:4: GREATERTHAN varexpr
                    {
                    GREATERTHAN33=(Token)match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_var_interval436); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATERTHAN33_tree = (Object)adaptor.create(GREATERTHAN33);
                    adaptor.addChild(root_0, GREATERTHAN33_tree);
                    }
                    pushFollow(FOLLOW_varexpr_in_var_interval438);
                    varexpr34=varexpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, varexpr34.getTree());
                    if ( state.backtracking==0 ) {
                      retval.val = new RangeMatches.GreaterThan((varexpr34!=null?varexpr34.node:null));
                    }

                    }


                    }
                    break;
                case 5 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:302:3: ( LESSTHAN EQ varexpr )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:302:3: ( LESSTHAN EQ varexpr )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:302:4: LESSTHAN EQ varexpr
                    {
                    LESSTHAN35=(Token)match(input,LESSTHAN,FOLLOW_LESSTHAN_in_var_interval446); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LESSTHAN35_tree = (Object)adaptor.create(LESSTHAN35);
                    adaptor.addChild(root_0, LESSTHAN35_tree);
                    }
                    EQ36=(Token)match(input,EQ,FOLLOW_EQ_in_var_interval448); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQ36_tree = (Object)adaptor.create(EQ36);
                    adaptor.addChild(root_0, EQ36_tree);
                    }
                    pushFollow(FOLLOW_varexpr_in_var_interval450);
                    varexpr37=varexpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, varexpr37.getTree());
                    if ( state.backtracking==0 ) {
                      retval.val = new RangeMatches.LessOrEqual((varexpr37!=null?varexpr37.node:null));
                    }

                    }


                    }
                    break;
                case 6 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:303:3: ( LESSTHAN varexpr )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:303:3: ( LESSTHAN varexpr )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:303:4: LESSTHAN varexpr
                    {
                    LESSTHAN38=(Token)match(input,LESSTHAN,FOLLOW_LESSTHAN_in_var_interval458); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LESSTHAN38_tree = (Object)adaptor.create(LESSTHAN38);
                    adaptor.addChild(root_0, LESSTHAN38_tree);
                    }
                    pushFollow(FOLLOW_varexpr_in_var_interval460);
                    varexpr39=varexpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, varexpr39.getTree());
                    if ( state.backtracking==0 ) {
                      retval.val = new RangeMatches.LessThan((varexpr39!=null?varexpr39.node:null));
                    }

                    }


                    }
                    break;
                case 7 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:304:3: ( IN l= ( L_PAREN | L_BRACKET ) low= varexpr ( COMMA | DOTS ) up= varexpr r= ( R_PAREN | R_BRACKET ) )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:304:3: ( IN l= ( L_PAREN | L_BRACKET ) low= varexpr ( COMMA | DOTS ) up= varexpr r= ( R_PAREN | R_BRACKET ) )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:304:4: IN l= ( L_PAREN | L_BRACKET ) low= varexpr ( COMMA | DOTS ) up= varexpr r= ( R_PAREN | R_BRACKET )
                    {
                    IN40=(Token)match(input,IN,FOLLOW_IN_in_var_interval468); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IN40_tree = (Object)adaptor.create(IN40);
                    adaptor.addChild(root_0, IN40_tree);
                    }
                    l=(Token)input.LT(1);
                    if ( input.LA(1)==L_BRACKET||input.LA(1)==L_PAREN ) {
                        input.consume();
                        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(l));
                        state.errorRecovery=false;state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    pushFollow(FOLLOW_varexpr_in_var_interval480);
                    low=varexpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, low.getTree());
                    set41=(Token)input.LT(1);
                    if ( input.LA(1)==COMMA||input.LA(1)==DOTS ) {
                        input.consume();
                        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set41));
                        state.errorRecovery=false;state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    pushFollow(FOLLOW_varexpr_in_var_interval490);
                    up=varexpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, up.getTree());
                    r=(Token)input.LT(1);
                    if ( input.LA(1)==R_BRACKET||input.LA(1)==R_PAREN ) {
                        input.consume();
                        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(r));
                        state.errorRecovery=false;state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    if ( state.backtracking==0 ) {
                      retval.val = RangeMatches.newInterval((low!=null?low.node:null),l.getType()==L_BRACKET,(up!=null?up.node:null),r.getType()==R_BRACKET);
                    }

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "var_interval"

    public static class numexpr_return extends ParserRuleReturnScope {
        public Double val;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "numexpr"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:308:1: numexpr returns [Double val] : expr ;
    public final MLSpaceSmallParser.numexpr_return numexpr() throws RecognitionException {
        MLSpaceSmallParser.numexpr_return retval = new MLSpaceSmallParser.numexpr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        MLSpaceSmallParser.expr_return expr42 = null;



        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:308:29: ( expr )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:309:3: expr
            {
            root_0 = (Object)adaptor.nil();

            if ( state.backtracking==0 ) {
              varsAllowed = false;
            }
            pushFollow(FOLLOW_expr_in_numexpr523);
            expr42=expr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expr42.getTree());
            if ( state.backtracking==0 ) {
              try {retval.val =(expr42!=null?expr42.node:null).calculateValue(numVariables);} catch (UndefinedVariableException ex) {throw new IllegalStateException(ex);}
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "numexpr"

    public static class varexpr_return extends ParserRuleReturnScope {
        public DoubleNode node;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "varexpr"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:311:1: varexpr returns [DoubleNode node] : expr ;
    public final MLSpaceSmallParser.varexpr_return varexpr() throws RecognitionException {
        MLSpaceSmallParser.varexpr_return retval = new MLSpaceSmallParser.varexpr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        MLSpaceSmallParser.expr_return expr43 = null;



        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:311:34: ( expr )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:312:3: expr
            {
            root_0 = (Object)adaptor.nil();

            if ( state.backtracking==0 ) {
              varsAllowed = true;
            }
            pushFollow(FOLLOW_expr_in_varexpr540);
            expr43=expr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expr43.getTree());
            if ( state.backtracking==0 ) {
              retval.node = (expr43!=null?expr43.node:null);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "varexpr"

    public static class expr_return extends ParserRuleReturnScope {
        public DoubleNode node;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "expr"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:314:1: expr returns [DoubleNode node] : ( (e= multNode ( PLUS e2= multNode | MINUS e2= multNode )* ) | ( IF boolNode THEN et= expr ( ELSE ef= expr )? ) );
    public final MLSpaceSmallParser.expr_return expr() throws RecognitionException {
        MLSpaceSmallParser.expr_return retval = new MLSpaceSmallParser.expr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token PLUS44=null;
        Token MINUS45=null;
        Token IF46=null;
        Token THEN48=null;
        Token ELSE49=null;
        MLSpaceSmallParser.multNode_return e = null;

        MLSpaceSmallParser.multNode_return e2 = null;

        MLSpaceSmallParser.expr_return et = null;

        MLSpaceSmallParser.expr_return ef = null;

        MLSpaceSmallParser.boolNode_return boolNode47 = null;


        Object PLUS44_tree=null;
        Object MINUS45_tree=null;
        Object IF46_tree=null;
        Object THEN48_tree=null;
        Object ELSE49_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:314:31: ( (e= multNode ( PLUS e2= multNode | MINUS e2= multNode )* ) | ( IF boolNode THEN et= expr ( ELSE ef= expr )? ) )
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==L_BRACKET||LA14_0==L_PAREN||(LA14_0>=FLOAT && LA14_0<=MINUS)||(LA14_0>=MIN && LA14_0<=MAX)||LA14_0==ID||LA14_0==INT) ) {
                alt14=1;
            }
            else if ( (LA14_0==IF) ) {
                alt14=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:315:3: (e= multNode ( PLUS e2= multNode | MINUS e2= multNode )* )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:315:3: (e= multNode ( PLUS e2= multNode | MINUS e2= multNode )* )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:315:4: e= multNode ( PLUS e2= multNode | MINUS e2= multNode )*
                    {
                    pushFollow(FOLLOW_multNode_in_expr560);
                    e=multNode();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, e.getTree());
                    if ( state.backtracking==0 ) {
                      retval.node = (e!=null?e.node:null);
                    }
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:316:9: ( PLUS e2= multNode | MINUS e2= multNode )*
                    loop12:
                    do {
                        int alt12=3;
                        int LA12_0 = input.LA(1);

                        if ( (LA12_0==PLUS) ) {
                            alt12=1;
                        }
                        else if ( (LA12_0==MINUS) ) {
                            alt12=2;
                        }


                        switch (alt12) {
                    	case 1 :
                    	    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:316:10: PLUS e2= multNode
                    	    {
                    	    PLUS44=(Token)match(input,PLUS,FOLLOW_PLUS_in_expr574); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    PLUS44_tree = (Object)adaptor.create(PLUS44);
                    	    adaptor.addChild(root_0, PLUS44_tree);
                    	    }
                    	    pushFollow(FOLLOW_multNode_in_expr578);
                    	    e2=multNode();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, e2.getTree());
                    	    if ( state.backtracking==0 ) {
                    	      retval.node = new AddNode(retval.node,(e2!=null?e2.node:null));
                    	    }

                    	    }
                    	    break;
                    	case 2 :
                    	    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:317:10: MINUS e2= multNode
                    	    {
                    	    MINUS45=(Token)match(input,MINUS,FOLLOW_MINUS_in_expr591); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    MINUS45_tree = (Object)adaptor.create(MINUS45);
                    	    adaptor.addChild(root_0, MINUS45_tree);
                    	    }
                    	    pushFollow(FOLLOW_multNode_in_expr595);
                    	    e2=multNode();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, e2.getTree());
                    	    if ( state.backtracking==0 ) {
                    	      retval.node = new SubtractNode(retval.node,(e2!=null?e2.node:null));
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop12;
                        }
                    } while (true);


                    }


                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:319:3: ( IF boolNode THEN et= expr ( ELSE ef= expr )? )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:319:3: ( IF boolNode THEN et= expr ( ELSE ef= expr )? )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:319:4: IF boolNode THEN et= expr ( ELSE ef= expr )?
                    {
                    IF46=(Token)match(input,IF,FOLLOW_IF_in_expr614); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IF46_tree = (Object)adaptor.create(IF46);
                    adaptor.addChild(root_0, IF46_tree);
                    }
                    pushFollow(FOLLOW_boolNode_in_expr616);
                    boolNode47=boolNode();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, boolNode47.getTree());
                    THEN48=(Token)match(input,THEN,FOLLOW_THEN_in_expr618); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    THEN48_tree = (Object)adaptor.create(THEN48);
                    adaptor.addChild(root_0, THEN48_tree);
                    }
                    pushFollow(FOLLOW_expr_in_expr622);
                    et=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, et.getTree());
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:319:29: ( ELSE ef= expr )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0==ELSE) ) {
                        int LA13_1 = input.LA(2);

                        if ( (synpred34_MLSpaceSmallParser()) ) {
                            alt13=1;
                        }
                    }
                    switch (alt13) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:319:30: ELSE ef= expr
                            {
                            ELSE49=(Token)match(input,ELSE,FOLLOW_ELSE_in_expr625); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            ELSE49_tree = (Object)adaptor.create(ELSE49);
                            adaptor.addChild(root_0, ELSE49_tree);
                            }
                            pushFollow(FOLLOW_expr_in_expr629);
                            ef=expr();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, ef.getTree());

                            }
                            break;

                    }

                    if ( state.backtracking==0 ) {
                      retval.node = new IfThenElseNode((boolNode47!=null?boolNode47.node:null),(et!=null?et.node:null),(ef!=null?ef.node:null));
                    }

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "expr"

    public static class multNode_return extends ParserRuleReturnScope {
        public DoubleNode node;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "multNode"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:322:1: multNode returns [DoubleNode node] : e= atomNode ( TIMES e2= atomNode | DIV e2= atomNode )* ;
    public final MLSpaceSmallParser.multNode_return multNode() throws RecognitionException {
        MLSpaceSmallParser.multNode_return retval = new MLSpaceSmallParser.multNode_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TIMES50=null;
        Token DIV51=null;
        MLSpaceSmallParser.atomNode_return e = null;

        MLSpaceSmallParser.atomNode_return e2 = null;


        Object TIMES50_tree=null;
        Object DIV51_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:322:35: (e= atomNode ( TIMES e2= atomNode | DIV e2= atomNode )* )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:323:3: e= atomNode ( TIMES e2= atomNode | DIV e2= atomNode )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_atomNode_in_multNode651);
            e=atomNode();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, e.getTree());
            if ( state.backtracking==0 ) {
              retval.node = (e!=null?e.node:null);
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:324:8: ( TIMES e2= atomNode | DIV e2= atomNode )*
            loop15:
            do {
                int alt15=3;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==TIMES) ) {
                    alt15=1;
                }
                else if ( (LA15_0==DIV) ) {
                    alt15=2;
                }


                switch (alt15) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:324:9: TIMES e2= atomNode
            	    {
            	    TIMES50=(Token)match(input,TIMES,FOLLOW_TIMES_in_multNode663); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    TIMES50_tree = (Object)adaptor.create(TIMES50);
            	    adaptor.addChild(root_0, TIMES50_tree);
            	    }
            	    pushFollow(FOLLOW_atomNode_in_multNode667);
            	    e2=atomNode();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, e2.getTree());
            	    if ( state.backtracking==0 ) {
            	      retval.node = new MultNode(retval.node,(e2!=null?e2.node:null));
            	    }

            	    }
            	    break;
            	case 2 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:325:10: DIV e2= atomNode
            	    {
            	    DIV51=(Token)match(input,DIV,FOLLOW_DIV_in_multNode680); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    DIV51_tree = (Object)adaptor.create(DIV51);
            	    adaptor.addChild(root_0, DIV51_tree);
            	    }
            	    pushFollow(FOLLOW_atomNode_in_multNode684);
            	    e2=atomNode();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, e2.getTree());
            	    if ( state.backtracking==0 ) {
            	      retval.node = new DivNode(retval.node,(e2!=null?e2.node:null));
            	    }

            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "multNode"

    public static class atomNode_return extends ParserRuleReturnScope {
        public DoubleNode node;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "atomNode"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:328:1: atomNode returns [DoubleNode node] : ( ( MINUS | PLUS )? (n= numval | {...}? ID | ( L_PAREN e= expr R_PAREN ) | ( L_PAREN boolNode R_PAREN ) | ( L_BRACKET e= expr R_BRACKET ) | ( MIN L_PAREN e1= expr COMMA e2= expr R_PAREN ) | ( MAX L_PAREN e1= expr COMMA e2= expr R_PAREN ) ) ) ( ( SQR ) | ( CUB ) | ( DEGREES ) | ( POW a= atomNode ) )? ;
    public final MLSpaceSmallParser.atomNode_return atomNode() throws RecognitionException {
        MLSpaceSmallParser.atomNode_return retval = new MLSpaceSmallParser.atomNode_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token MINUS52=null;
        Token PLUS53=null;
        Token ID54=null;
        Token L_PAREN55=null;
        Token R_PAREN56=null;
        Token L_PAREN57=null;
        Token R_PAREN59=null;
        Token L_BRACKET60=null;
        Token R_BRACKET61=null;
        Token MIN62=null;
        Token L_PAREN63=null;
        Token COMMA64=null;
        Token R_PAREN65=null;
        Token MAX66=null;
        Token L_PAREN67=null;
        Token COMMA68=null;
        Token R_PAREN69=null;
        Token SQR70=null;
        Token CUB71=null;
        Token DEGREES72=null;
        Token POW73=null;
        MLSpaceSmallParser.numval_return n = null;

        MLSpaceSmallParser.expr_return e = null;

        MLSpaceSmallParser.expr_return e1 = null;

        MLSpaceSmallParser.expr_return e2 = null;

        MLSpaceSmallParser.atomNode_return a = null;

        MLSpaceSmallParser.boolNode_return boolNode58 = null;


        Object MINUS52_tree=null;
        Object PLUS53_tree=null;
        Object ID54_tree=null;
        Object L_PAREN55_tree=null;
        Object R_PAREN56_tree=null;
        Object L_PAREN57_tree=null;
        Object R_PAREN59_tree=null;
        Object L_BRACKET60_tree=null;
        Object R_BRACKET61_tree=null;
        Object MIN62_tree=null;
        Object L_PAREN63_tree=null;
        Object COMMA64_tree=null;
        Object R_PAREN65_tree=null;
        Object MAX66_tree=null;
        Object L_PAREN67_tree=null;
        Object COMMA68_tree=null;
        Object R_PAREN69_tree=null;
        Object SQR70_tree=null;
        Object CUB71_tree=null;
        Object DEGREES72_tree=null;
        Object POW73_tree=null;

        double sign = 1.;
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:328:61: ( ( ( MINUS | PLUS )? (n= numval | {...}? ID | ( L_PAREN e= expr R_PAREN ) | ( L_PAREN boolNode R_PAREN ) | ( L_BRACKET e= expr R_BRACKET ) | ( MIN L_PAREN e1= expr COMMA e2= expr R_PAREN ) | ( MAX L_PAREN e1= expr COMMA e2= expr R_PAREN ) ) ) ( ( SQR ) | ( CUB ) | ( DEGREES ) | ( POW a= atomNode ) )? )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:329:3: ( ( MINUS | PLUS )? (n= numval | {...}? ID | ( L_PAREN e= expr R_PAREN ) | ( L_PAREN boolNode R_PAREN ) | ( L_BRACKET e= expr R_BRACKET ) | ( MIN L_PAREN e1= expr COMMA e2= expr R_PAREN ) | ( MAX L_PAREN e1= expr COMMA e2= expr R_PAREN ) ) ) ( ( SQR ) | ( CUB ) | ( DEGREES ) | ( POW a= atomNode ) )?
            {
            root_0 = (Object)adaptor.nil();

            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:329:3: ( ( MINUS | PLUS )? (n= numval | {...}? ID | ( L_PAREN e= expr R_PAREN ) | ( L_PAREN boolNode R_PAREN ) | ( L_BRACKET e= expr R_BRACKET ) | ( MIN L_PAREN e1= expr COMMA e2= expr R_PAREN ) | ( MAX L_PAREN e1= expr COMMA e2= expr R_PAREN ) ) )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:329:4: ( MINUS | PLUS )? (n= numval | {...}? ID | ( L_PAREN e= expr R_PAREN ) | ( L_PAREN boolNode R_PAREN ) | ( L_BRACKET e= expr R_BRACKET ) | ( MIN L_PAREN e1= expr COMMA e2= expr R_PAREN ) | ( MAX L_PAREN e1= expr COMMA e2= expr R_PAREN ) )
            {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:329:4: ( MINUS | PLUS )?
            int alt16=3;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==MINUS) ) {
                alt16=1;
            }
            else if ( (LA16_0==PLUS) ) {
                alt16=2;
            }
            switch (alt16) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:329:5: MINUS
                    {
                    MINUS52=(Token)match(input,MINUS,FOLLOW_MINUS_in_atomNode722); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    MINUS52_tree = (Object)adaptor.create(MINUS52);
                    adaptor.addChild(root_0, MINUS52_tree);
                    }
                    if ( state.backtracking==0 ) {
                      sign = -1.;
                    }

                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:329:27: PLUS
                    {
                    PLUS53=(Token)match(input,PLUS,FOLLOW_PLUS_in_atomNode728); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    PLUS53_tree = (Object)adaptor.create(PLUS53);
                    adaptor.addChild(root_0, PLUS53_tree);
                    }

                    }
                    break;

            }

            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:330:4: (n= numval | {...}? ID | ( L_PAREN e= expr R_PAREN ) | ( L_PAREN boolNode R_PAREN ) | ( L_BRACKET e= expr R_BRACKET ) | ( MIN L_PAREN e1= expr COMMA e2= expr R_PAREN ) | ( MAX L_PAREN e1= expr COMMA e2= expr R_PAREN ) )
            int alt17=7;
            alt17 = dfa17.predict(input);
            switch (alt17) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:330:5: n= numval
                    {
                    pushFollow(FOLLOW_numval_in_atomNode739);
                    n=numval();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, n.getTree());
                    if ( state.backtracking==0 ) {
                      retval.node = new FixedValueNode((n!=null?n.val:null));
                    }

                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:331:5: {...}? ID
                    {
                    if ( !((varsAllowed)) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "atomNode", "varsAllowed");
                    }
                    ID54=(Token)match(input,ID,FOLLOW_ID_in_atomNode749); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ID54_tree = (Object)adaptor.create(ID54);
                    adaptor.addChild(root_0, ID54_tree);
                    }
                    if ( state.backtracking==0 ) {
                      checkLocalRuleVar((ID54!=null?ID54.getText():null)); retval.node = new VariableNode((ID54!=null?ID54.getText():null));
                    }

                    }
                    break;
                case 3 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:332:5: ( L_PAREN e= expr R_PAREN )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:332:5: ( L_PAREN e= expr R_PAREN )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:332:6: L_PAREN e= expr R_PAREN
                    {
                    L_PAREN55=(Token)match(input,L_PAREN,FOLLOW_L_PAREN_in_atomNode758); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    L_PAREN55_tree = (Object)adaptor.create(L_PAREN55);
                    adaptor.addChild(root_0, L_PAREN55_tree);
                    }
                    pushFollow(FOLLOW_expr_in_atomNode762);
                    e=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, e.getTree());
                    R_PAREN56=(Token)match(input,R_PAREN,FOLLOW_R_PAREN_in_atomNode764); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    R_PAREN56_tree = (Object)adaptor.create(R_PAREN56);
                    adaptor.addChild(root_0, R_PAREN56_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.node =(e!=null?e.node:null);
                    }

                    }


                    }
                    break;
                case 4 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:333:5: ( L_PAREN boolNode R_PAREN )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:333:5: ( L_PAREN boolNode R_PAREN )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:333:6: L_PAREN boolNode R_PAREN
                    {
                    L_PAREN57=(Token)match(input,L_PAREN,FOLLOW_L_PAREN_in_atomNode774); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    L_PAREN57_tree = (Object)adaptor.create(L_PAREN57);
                    adaptor.addChild(root_0, L_PAREN57_tree);
                    }
                    pushFollow(FOLLOW_boolNode_in_atomNode776);
                    boolNode58=boolNode();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, boolNode58.getTree());
                    R_PAREN59=(Token)match(input,R_PAREN,FOLLOW_R_PAREN_in_atomNode778); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    R_PAREN59_tree = (Object)adaptor.create(R_PAREN59);
                    adaptor.addChild(root_0, R_PAREN59_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.node =new SwitchNode((boolNode58!=null?boolNode58.node:null));
                    }

                    }


                    }
                    break;
                case 5 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:334:5: ( L_BRACKET e= expr R_BRACKET )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:334:5: ( L_BRACKET e= expr R_BRACKET )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:334:6: L_BRACKET e= expr R_BRACKET
                    {
                    L_BRACKET60=(Token)match(input,L_BRACKET,FOLLOW_L_BRACKET_in_atomNode788); if (state.failed) return retval;
                    pushFollow(FOLLOW_expr_in_atomNode793);
                    e=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, e.getTree());
                    R_BRACKET61=(Token)match(input,R_BRACKET,FOLLOW_R_BRACKET_in_atomNode795); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      retval.node = new IntNode((e!=null?e.node:null));
                    }

                    }


                    }
                    break;
                case 6 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:335:5: ( MIN L_PAREN e1= expr COMMA e2= expr R_PAREN )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:335:5: ( MIN L_PAREN e1= expr COMMA e2= expr R_PAREN )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:335:6: MIN L_PAREN e1= expr COMMA e2= expr R_PAREN
                    {
                    MIN62=(Token)match(input,MIN,FOLLOW_MIN_in_atomNode806); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    MIN62_tree = (Object)adaptor.create(MIN62);
                    adaptor.addChild(root_0, MIN62_tree);
                    }
                    L_PAREN63=(Token)match(input,L_PAREN,FOLLOW_L_PAREN_in_atomNode808); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    L_PAREN63_tree = (Object)adaptor.create(L_PAREN63);
                    adaptor.addChild(root_0, L_PAREN63_tree);
                    }
                    pushFollow(FOLLOW_expr_in_atomNode812);
                    e1=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, e1.getTree());
                    COMMA64=(Token)match(input,COMMA,FOLLOW_COMMA_in_atomNode814); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    COMMA64_tree = (Object)adaptor.create(COMMA64);
                    adaptor.addChild(root_0, COMMA64_tree);
                    }
                    pushFollow(FOLLOW_expr_in_atomNode818);
                    e2=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, e2.getTree());
                    R_PAREN65=(Token)match(input,R_PAREN,FOLLOW_R_PAREN_in_atomNode820); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    R_PAREN65_tree = (Object)adaptor.create(R_PAREN65);
                    adaptor.addChild(root_0, R_PAREN65_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.node = new MinNode((e1!=null?e1.node:null),(e2!=null?e2.node:null));
                    }

                    }


                    }
                    break;
                case 7 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:336:5: ( MAX L_PAREN e1= expr COMMA e2= expr R_PAREN )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:336:5: ( MAX L_PAREN e1= expr COMMA e2= expr R_PAREN )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:336:6: MAX L_PAREN e1= expr COMMA e2= expr R_PAREN
                    {
                    MAX66=(Token)match(input,MAX,FOLLOW_MAX_in_atomNode833); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    MAX66_tree = (Object)adaptor.create(MAX66);
                    adaptor.addChild(root_0, MAX66_tree);
                    }
                    L_PAREN67=(Token)match(input,L_PAREN,FOLLOW_L_PAREN_in_atomNode835); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    L_PAREN67_tree = (Object)adaptor.create(L_PAREN67);
                    adaptor.addChild(root_0, L_PAREN67_tree);
                    }
                    pushFollow(FOLLOW_expr_in_atomNode839);
                    e1=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, e1.getTree());
                    COMMA68=(Token)match(input,COMMA,FOLLOW_COMMA_in_atomNode841); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    COMMA68_tree = (Object)adaptor.create(COMMA68);
                    adaptor.addChild(root_0, COMMA68_tree);
                    }
                    pushFollow(FOLLOW_expr_in_atomNode845);
                    e2=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, e2.getTree());
                    R_PAREN69=(Token)match(input,R_PAREN,FOLLOW_R_PAREN_in_atomNode847); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    R_PAREN69_tree = (Object)adaptor.create(R_PAREN69);
                    adaptor.addChild(root_0, R_PAREN69_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.node = new MaxNode((e1!=null?e1.node:null),(e2!=null?e2.node:null));
                    }

                    }


                    }
                    break;

            }


            }

            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:338:3: ( ( SQR ) | ( CUB ) | ( DEGREES ) | ( POW a= atomNode ) )?
            int alt18=5;
            switch ( input.LA(1) ) {
                case SQR:
                    {
                    alt18=1;
                    }
                    break;
                case CUB:
                    {
                    alt18=2;
                    }
                    break;
                case DEGREES:
                    {
                    alt18=3;
                    }
                    break;
                case POW:
                    {
                    alt18=4;
                    }
                    break;
            }

            switch (alt18) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:339:5: ( SQR )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:339:5: ( SQR )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:339:6: SQR
                    {
                    SQR70=(Token)match(input,SQR,FOLLOW_SQR_in_atomNode870); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SQR70_tree = (Object)adaptor.create(SQR70);
                    adaptor.addChild(root_0, SQR70_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.node = new SquareNode(retval.node);
                    }

                    }


                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:340:5: ( CUB )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:340:5: ( CUB )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:340:6: CUB
                    {
                    CUB71=(Token)match(input,CUB,FOLLOW_CUB_in_atomNode881); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CUB71_tree = (Object)adaptor.create(CUB71);
                    adaptor.addChild(root_0, CUB71_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.node = new PowerNode(retval.node, new FixedValueNode(3.));
                    }

                    }


                    }
                    break;
                case 3 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:341:5: ( DEGREES )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:341:5: ( DEGREES )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:341:6: DEGREES
                    {
                    DEGREES72=(Token)match(input,DEGREES,FOLLOW_DEGREES_in_atomNode891); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    DEGREES72_tree = (Object)adaptor.create(DEGREES72);
                    adaptor.addChild(root_0, DEGREES72_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.node = new MultNode(retval.node, new FixedValueNode(Math.PI / 180.));
                    }

                    }


                    }
                    break;
                case 4 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:342:5: ( POW a= atomNode )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:342:5: ( POW a= atomNode )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:342:6: POW a= atomNode
                    {
                    POW73=(Token)match(input,POW,FOLLOW_POW_in_atomNode901); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    POW73_tree = (Object)adaptor.create(POW73);
                    adaptor.addChild(root_0, POW73_tree);
                    }
                    pushFollow(FOLLOW_atomNode_in_atomNode905);
                    a=atomNode();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, a.getTree());
                    if ( state.backtracking==0 ) {
                      retval.node = new PowerNode(retval.node,(a!=null?a.node:null));
                    }

                    }


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              if (sign==-1) retval.node = new MinusNode(retval.node);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "atomNode"

    public static class boolNode_return extends ParserRuleReturnScope {
        public LogicalNode node;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "boolNode"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:346:1: boolNode returns [LogicalNode node] : el= expr c= compareOp er= expr ;
    public final MLSpaceSmallParser.boolNode_return boolNode() throws RecognitionException {
        MLSpaceSmallParser.boolNode_return retval = new MLSpaceSmallParser.boolNode_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        MLSpaceSmallParser.expr_return el = null;

        MLSpaceSmallParser.compareOp_return c = null;

        MLSpaceSmallParser.expr_return er = null;



        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:346:36: (el= expr c= compareOp er= expr )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:347:3: el= expr c= compareOp er= expr
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expr_in_boolNode935);
            el=expr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, el.getTree());
            pushFollow(FOLLOW_compareOp_in_boolNode939);
            c=compareOp();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, c.getTree());
            pushFollow(FOLLOW_expr_in_boolNode943);
            er=expr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, er.getTree());
            if ( state.backtracking==0 ) {
              retval.node = Comparisons.newFromCompString((el!=null?el.node:null),(c!=null?input.toString(c.start,c.stop):null),(er!=null?er.node:null));
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "boolNode"

    public static class compareOp_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "compareOp"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:350:1: compareOp : ( LESSTHAN ( EQ )? | GREATERTHAN ( EQ )? | NOTEQ | EQ EQ );
    public final MLSpaceSmallParser.compareOp_return compareOp() throws RecognitionException {
        MLSpaceSmallParser.compareOp_return retval = new MLSpaceSmallParser.compareOp_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LESSTHAN74=null;
        Token EQ75=null;
        Token GREATERTHAN76=null;
        Token EQ77=null;
        Token NOTEQ78=null;
        Token EQ79=null;
        Token EQ80=null;

        Object LESSTHAN74_tree=null;
        Object EQ75_tree=null;
        Object GREATERTHAN76_tree=null;
        Object EQ77_tree=null;
        Object NOTEQ78_tree=null;
        Object EQ79_tree=null;
        Object EQ80_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:350:10: ( LESSTHAN ( EQ )? | GREATERTHAN ( EQ )? | NOTEQ | EQ EQ )
            int alt21=4;
            switch ( input.LA(1) ) {
            case LESSTHAN:
                {
                alt21=1;
                }
                break;
            case GREATERTHAN:
                {
                alt21=2;
                }
                break;
            case NOTEQ:
                {
                alt21=3;
                }
                break;
            case EQ:
                {
                alt21=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;
            }

            switch (alt21) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:350:12: LESSTHAN ( EQ )?
                    {
                    root_0 = (Object)adaptor.nil();

                    LESSTHAN74=(Token)match(input,LESSTHAN,FOLLOW_LESSTHAN_in_compareOp955); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LESSTHAN74_tree = (Object)adaptor.create(LESSTHAN74);
                    adaptor.addChild(root_0, LESSTHAN74_tree);
                    }
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:350:21: ( EQ )?
                    int alt19=2;
                    int LA19_0 = input.LA(1);

                    if ( (LA19_0==EQ) ) {
                        alt19=1;
                    }
                    switch (alt19) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:0:0: EQ
                            {
                            EQ75=(Token)match(input,EQ,FOLLOW_EQ_in_compareOp957); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            EQ75_tree = (Object)adaptor.create(EQ75);
                            adaptor.addChild(root_0, EQ75_tree);
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:350:27: GREATERTHAN ( EQ )?
                    {
                    root_0 = (Object)adaptor.nil();

                    GREATERTHAN76=(Token)match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_compareOp962); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATERTHAN76_tree = (Object)adaptor.create(GREATERTHAN76);
                    adaptor.addChild(root_0, GREATERTHAN76_tree);
                    }
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:350:39: ( EQ )?
                    int alt20=2;
                    int LA20_0 = input.LA(1);

                    if ( (LA20_0==EQ) ) {
                        alt20=1;
                    }
                    switch (alt20) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:0:0: EQ
                            {
                            EQ77=(Token)match(input,EQ,FOLLOW_EQ_in_compareOp964); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            EQ77_tree = (Object)adaptor.create(EQ77);
                            adaptor.addChild(root_0, EQ77_tree);
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:350:45: NOTEQ
                    {
                    root_0 = (Object)adaptor.nil();

                    NOTEQ78=(Token)match(input,NOTEQ,FOLLOW_NOTEQ_in_compareOp969); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    NOTEQ78_tree = (Object)adaptor.create(NOTEQ78);
                    adaptor.addChild(root_0, NOTEQ78_tree);
                    }

                    }
                    break;
                case 4 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:350:53: EQ EQ
                    {
                    root_0 = (Object)adaptor.nil();

                    EQ79=(Token)match(input,EQ,FOLLOW_EQ_in_compareOp973); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQ79_tree = (Object)adaptor.create(EQ79);
                    adaptor.addChild(root_0, EQ79_tree);
                    }
                    EQ80=(Token)match(input,EQ,FOLLOW_EQ_in_compareOp975); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQ80_tree = (Object)adaptor.create(EQ80);
                    adaptor.addChild(root_0, EQ80_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "compareOp"

    public static class interval_return extends ParserRuleReturnScope {
        public double lower;
        public double upper;
        public boolean incLower;
        public boolean incUpper;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "interval"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:352:1: interval returns [double lower, double upper,boolean incLower, boolean incUpper] : ( ( L_BRACKET low= numexpr DOTS up= numexpr R_BRACKET ) | ( GREATERTHAN EQ nge= numexpr ) | ( LESSTHAN EQ nle= numexpr ) | ( GREATERTHAN ngt= numexpr ) | ( LESSTHAN nlt= numexpr ) );
    public final MLSpaceSmallParser.interval_return interval() throws RecognitionException {
        MLSpaceSmallParser.interval_return retval = new MLSpaceSmallParser.interval_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token L_BRACKET81=null;
        Token DOTS82=null;
        Token R_BRACKET83=null;
        Token GREATERTHAN84=null;
        Token EQ85=null;
        Token LESSTHAN86=null;
        Token EQ87=null;
        Token GREATERTHAN88=null;
        Token LESSTHAN89=null;
        MLSpaceSmallParser.numexpr_return low = null;

        MLSpaceSmallParser.numexpr_return up = null;

        MLSpaceSmallParser.numexpr_return nge = null;

        MLSpaceSmallParser.numexpr_return nle = null;

        MLSpaceSmallParser.numexpr_return ngt = null;

        MLSpaceSmallParser.numexpr_return nlt = null;


        Object L_BRACKET81_tree=null;
        Object DOTS82_tree=null;
        Object R_BRACKET83_tree=null;
        Object GREATERTHAN84_tree=null;
        Object EQ85_tree=null;
        Object LESSTHAN86_tree=null;
        Object EQ87_tree=null;
        Object GREATERTHAN88_tree=null;
        Object LESSTHAN89_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:352:81: ( ( L_BRACKET low= numexpr DOTS up= numexpr R_BRACKET ) | ( GREATERTHAN EQ nge= numexpr ) | ( LESSTHAN EQ nle= numexpr ) | ( GREATERTHAN ngt= numexpr ) | ( LESSTHAN nlt= numexpr ) )
            int alt22=5;
            switch ( input.LA(1) ) {
            case L_BRACKET:
                {
                alt22=1;
                }
                break;
            case GREATERTHAN:
                {
                int LA22_2 = input.LA(2);

                if ( (LA22_2==EQ) ) {
                    alt22=2;
                }
                else if ( (LA22_2==L_BRACKET||LA22_2==L_PAREN||(LA22_2>=FLOAT && LA22_2<=MINUS)||(LA22_2>=MIN && LA22_2<=IF)||LA22_2==ID||LA22_2==INT) ) {
                    alt22=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 22, 2, input);

                    throw nvae;
                }
                }
                break;
            case LESSTHAN:
                {
                int LA22_3 = input.LA(2);

                if ( (LA22_3==EQ) ) {
                    alt22=3;
                }
                else if ( (LA22_3==L_BRACKET||LA22_3==L_PAREN||(LA22_3>=FLOAT && LA22_3<=MINUS)||(LA22_3>=MIN && LA22_3<=IF)||LA22_3==ID||LA22_3==INT) ) {
                    alt22=5;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 22, 3, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 22, 0, input);

                throw nvae;
            }

            switch (alt22) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:353:3: ( L_BRACKET low= numexpr DOTS up= numexpr R_BRACKET )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:353:3: ( L_BRACKET low= numexpr DOTS up= numexpr R_BRACKET )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:353:4: L_BRACKET low= numexpr DOTS up= numexpr R_BRACKET
                    {
                    L_BRACKET81=(Token)match(input,L_BRACKET,FOLLOW_L_BRACKET_in_interval991); if (state.failed) return retval;
                    pushFollow(FOLLOW_numexpr_in_interval996);
                    low=numexpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, low.getTree());
                    if ( state.backtracking==0 ) {
                      retval.lower = ((low!=null?low.val:null));
                    }
                    DOTS82=(Token)match(input,DOTS,FOLLOW_DOTS_in_interval1000); if (state.failed) return retval;
                    pushFollow(FOLLOW_numexpr_in_interval1005);
                    up=numexpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, up.getTree());
                    if ( state.backtracking==0 ) {
                      retval.upper = ((up!=null?up.val:null));retval.incLower = true; retval.incUpper = true;
                    }
                    R_BRACKET83=(Token)match(input,R_BRACKET,FOLLOW_R_BRACKET_in_interval1009); if (state.failed) return retval;

                    }


                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:354:5: ( GREATERTHAN EQ nge= numexpr )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:354:5: ( GREATERTHAN EQ nge= numexpr )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:354:6: GREATERTHAN EQ nge= numexpr
                    {
                    GREATERTHAN84=(Token)match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_interval1018); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATERTHAN84_tree = (Object)adaptor.create(GREATERTHAN84);
                    adaptor.addChild(root_0, GREATERTHAN84_tree);
                    }
                    EQ85=(Token)match(input,EQ,FOLLOW_EQ_in_interval1020); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQ85_tree = (Object)adaptor.create(EQ85);
                    adaptor.addChild(root_0, EQ85_tree);
                    }
                    pushFollow(FOLLOW_numexpr_in_interval1026);
                    nge=numexpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, nge.getTree());
                    if ( state.backtracking==0 ) {
                      retval.lower = (nge!=null?nge.val:null);	retval.upper = Double.POSITIVE_INFINITY;retval.incLower = true; retval.incUpper = true;
                    }

                    }


                    }
                    break;
                case 3 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:355:5: ( LESSTHAN EQ nle= numexpr )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:355:5: ( LESSTHAN EQ nle= numexpr )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:355:6: LESSTHAN EQ nle= numexpr
                    {
                    LESSTHAN86=(Token)match(input,LESSTHAN,FOLLOW_LESSTHAN_in_interval1036); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LESSTHAN86_tree = (Object)adaptor.create(LESSTHAN86);
                    adaptor.addChild(root_0, LESSTHAN86_tree);
                    }
                    EQ87=(Token)match(input,EQ,FOLLOW_EQ_in_interval1038); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQ87_tree = (Object)adaptor.create(EQ87);
                    adaptor.addChild(root_0, EQ87_tree);
                    }
                    pushFollow(FOLLOW_numexpr_in_interval1044);
                    nle=numexpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, nle.getTree());
                    if ( state.backtracking==0 ) {
                      retval.upper = (nle!=null?nle.val:null);	retval.lower = Double.NEGATIVE_INFINITY;retval.incLower = true; retval.incUpper = true;
                    }

                    }


                    }
                    break;
                case 4 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:356:5: ( GREATERTHAN ngt= numexpr )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:356:5: ( GREATERTHAN ngt= numexpr )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:356:6: GREATERTHAN ngt= numexpr
                    {
                    GREATERTHAN88=(Token)match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_interval1054); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATERTHAN88_tree = (Object)adaptor.create(GREATERTHAN88);
                    adaptor.addChild(root_0, GREATERTHAN88_tree);
                    }
                    pushFollow(FOLLOW_numexpr_in_interval1060);
                    ngt=numexpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ngt.getTree());
                    if ( state.backtracking==0 ) {
                      retval.lower = (ngt!=null?ngt.val:null);	retval.upper = Double.POSITIVE_INFINITY;retval.incLower = false; retval.incUpper = true;
                    }

                    }


                    }
                    break;
                case 5 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:357:5: ( LESSTHAN nlt= numexpr )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:357:5: ( LESSTHAN nlt= numexpr )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:357:6: LESSTHAN nlt= numexpr
                    {
                    LESSTHAN89=(Token)match(input,LESSTHAN,FOLLOW_LESSTHAN_in_interval1070); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LESSTHAN89_tree = (Object)adaptor.create(LESSTHAN89);
                    adaptor.addChild(root_0, LESSTHAN89_tree);
                    }
                    pushFollow(FOLLOW_numexpr_in_interval1076);
                    nlt=numexpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, nlt.getTree());
                    if ( state.backtracking==0 ) {
                      retval.upper = (nlt!=null?nlt.val:null);	retval.lower = Double.NEGATIVE_INFINITY;retval.incLower = true; retval.incUpper = false;
                    }

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "interval"

    public static class range_return extends ParserRuleReturnScope {
        public double lower;
        public double step;
        public double upper;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "range"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:360:1: range returns [double lower, double step, double upper] : (one= numexpr COLON two= numexpr ( COLON three= numexpr )? ) ;
    public final MLSpaceSmallParser.range_return range() throws RecognitionException {
        MLSpaceSmallParser.range_return retval = new MLSpaceSmallParser.range_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLON90=null;
        Token COLON91=null;
        MLSpaceSmallParser.numexpr_return one = null;

        MLSpaceSmallParser.numexpr_return two = null;

        MLSpaceSmallParser.numexpr_return three = null;


        Object COLON90_tree=null;
        Object COLON91_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:360:56: ( (one= numexpr COLON two= numexpr ( COLON three= numexpr )? ) )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:361:3: (one= numexpr COLON two= numexpr ( COLON three= numexpr )? )
            {
            root_0 = (Object)adaptor.nil();

            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:361:3: (one= numexpr COLON two= numexpr ( COLON three= numexpr )? )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:361:4: one= numexpr COLON two= numexpr ( COLON three= numexpr )?
            {
            pushFollow(FOLLOW_numexpr_in_range1099);
            one=numexpr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, one.getTree());
            COLON90=(Token)match(input,COLON,FOLLOW_COLON_in_range1101); if (state.failed) return retval;
            pushFollow(FOLLOW_numexpr_in_range1106);
            two=numexpr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, two.getTree());
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:361:35: ( COLON three= numexpr )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==COLON) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:361:36: COLON three= numexpr
                    {
                    COLON91=(Token)match(input,COLON,FOLLOW_COLON_in_range1109); if (state.failed) return retval;
                    pushFollow(FOLLOW_numexpr_in_range1114);
                    three=numexpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, three.getTree());

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              retval.lower = (one!=null?one.val:null); 
                   if((three!=null?three.val:null) == null) 
                     {retval.step = 1; retval.upper = (two!=null?two.val:null);}
                   else
                    {retval.step = (two!=null?two.val:null); retval.upper = (three!=null?three.val:null);}
                   
            }

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "range"

    public static class set_return extends ParserRuleReturnScope {
        public Set<?> set;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "set"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:370:1: set returns [Set<?> set] : ( numset | idset );
    public final MLSpaceSmallParser.set_return set() throws RecognitionException {
        MLSpaceSmallParser.set_return retval = new MLSpaceSmallParser.set_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        MLSpaceSmallParser.numset_return numset92 = null;

        MLSpaceSmallParser.idset_return idset93 = null;



        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:370:25: ( numset | idset )
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==L_BRACE) ) {
                int LA24_1 = input.LA(2);

                if ( (LA24_1==L_BRACKET||LA24_1==L_PAREN||(LA24_1>=FLOAT && LA24_1<=MINUS)||(LA24_1>=MIN && LA24_1<=IF)||LA24_1==ID||LA24_1==INT) ) {
                    alt24=1;
                }
                else if ( (LA24_1==STRING) ) {
                    alt24=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 24, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 24, 0, input);

                throw nvae;
            }
            switch (alt24) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:371:3: numset
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_numset_in_set1140);
                    numset92=numset();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, numset92.getTree());
                    if ( state.backtracking==0 ) {
                      retval.set = (numset92!=null?numset92.set:null);
                    }

                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:372:3: idset
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_idset_in_set1146);
                    idset93=idset();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, idset93.getTree());
                    if ( state.backtracking==0 ) {
                      retval.set = (idset93!=null?idset93.set:null);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "set"

    public static class idset_return extends ParserRuleReturnScope {
        public Set<String> set;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "idset"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:374:1: idset returns [Set<String> set] : L_BRACE (i1= STRING ( COMMA i2= STRING )* ) R_BRACE ;
    public final MLSpaceSmallParser.idset_return idset() throws RecognitionException {
        MLSpaceSmallParser.idset_return retval = new MLSpaceSmallParser.idset_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token i1=null;
        Token i2=null;
        Token L_BRACE94=null;
        Token COMMA95=null;
        Token R_BRACE96=null;

        Object i1_tree=null;
        Object i2_tree=null;
        Object L_BRACE94_tree=null;
        Object COMMA95_tree=null;
        Object R_BRACE96_tree=null;

        retval.set = new NonNullSet<String>();
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:375:41: ( L_BRACE (i1= STRING ( COMMA i2= STRING )* ) R_BRACE )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:376:2: L_BRACE (i1= STRING ( COMMA i2= STRING )* ) R_BRACE
            {
            root_0 = (Object)adaptor.nil();

            L_BRACE94=(Token)match(input,L_BRACE,FOLLOW_L_BRACE_in_idset1165); if (state.failed) return retval;
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:376:11: (i1= STRING ( COMMA i2= STRING )* )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:376:12: i1= STRING ( COMMA i2= STRING )*
            {
            i1=(Token)match(input,STRING,FOLLOW_STRING_in_idset1171); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            i1_tree = (Object)adaptor.create(i1);
            adaptor.addChild(root_0, i1_tree);
            }
            if ( state.backtracking==0 ) {
              retval.set.add((i1!=null?i1.getText():null));
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:376:44: ( COMMA i2= STRING )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==COMMA) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:376:45: COMMA i2= STRING
            	    {
            	    COMMA95=(Token)match(input,COMMA,FOLLOW_COMMA_in_idset1176); if (state.failed) return retval;
            	    i2=(Token)match(input,STRING,FOLLOW_STRING_in_idset1181); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    i2_tree = (Object)adaptor.create(i2);
            	    adaptor.addChild(root_0, i2_tree);
            	    }
            	    if ( state.backtracking==0 ) {
            	      retval.set.add((i2!=null?i2.getText():null));
            	    }

            	    }
            	    break;

            	default :
            	    break loop25;
                }
            } while (true);


            }

            R_BRACE96=(Token)match(input,R_BRACE,FOLLOW_R_BRACE_in_idset1188); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "idset"

    public static class numset_return extends ParserRuleReturnScope {
        public Set<Double> set;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "numset"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:378:1: numset returns [Set<Double> set] : L_BRACE (i1= numexpr ( COMMA i2= numexpr )* ) R_BRACE ;
    public final MLSpaceSmallParser.numset_return numset() throws RecognitionException {
        MLSpaceSmallParser.numset_return retval = new MLSpaceSmallParser.numset_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token L_BRACE97=null;
        Token COMMA98=null;
        Token R_BRACE99=null;
        MLSpaceSmallParser.numexpr_return i1 = null;

        MLSpaceSmallParser.numexpr_return i2 = null;


        Object L_BRACE97_tree=null;
        Object COMMA98_tree=null;
        Object R_BRACE99_tree=null;

        retval.set = new NonNullSet<Double>();
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:379:41: ( L_BRACE (i1= numexpr ( COMMA i2= numexpr )* ) R_BRACE )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:380:2: L_BRACE (i1= numexpr ( COMMA i2= numexpr )* ) R_BRACE
            {
            root_0 = (Object)adaptor.nil();

            L_BRACE97=(Token)match(input,L_BRACE,FOLLOW_L_BRACE_in_numset1206); if (state.failed) return retval;
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:380:11: (i1= numexpr ( COMMA i2= numexpr )* )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:380:12: i1= numexpr ( COMMA i2= numexpr )*
            {
            pushFollow(FOLLOW_numexpr_in_numset1212);
            i1=numexpr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, i1.getTree());
            if ( state.backtracking==0 ) {
              retval.set.add((i1!=null?i1.val:null));
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:380:44: ( COMMA i2= numexpr )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( (LA26_0==COMMA) ) {
                    alt26=1;
                }


                switch (alt26) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:380:45: COMMA i2= numexpr
            	    {
            	    COMMA98=(Token)match(input,COMMA,FOLLOW_COMMA_in_numset1217); if (state.failed) return retval;
            	    pushFollow(FOLLOW_numexpr_in_numset1222);
            	    i2=numexpr();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, i2.getTree());
            	    if ( state.backtracking==0 ) {
            	      retval.set.add((i2!=null?i2.val:null));
            	    }

            	    }
            	    break;

            	default :
            	    break loop26;
                }
            } while (true);


            }

            R_BRACE99=(Token)match(input,R_BRACE,FOLLOW_R_BRACE_in_numset1229); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "numset"

    public static class vector_return extends ParserRuleReturnScope {
        public double[] vec;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "vector"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:382:1: vector returns [double[] vec] : L_PAREN n1= numexpr ( COMMA n2= numexpr )+ R_PAREN ;
    public final MLSpaceSmallParser.vector_return vector() throws RecognitionException {
        MLSpaceSmallParser.vector_return retval = new MLSpaceSmallParser.vector_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token L_PAREN100=null;
        Token COMMA101=null;
        Token R_PAREN102=null;
        MLSpaceSmallParser.numexpr_return n1 = null;

        MLSpaceSmallParser.numexpr_return n2 = null;


        Object L_PAREN100_tree=null;
        Object COMMA101_tree=null;
        Object R_PAREN102_tree=null;

        List<Double> tmp = new ArrayList<Double>();
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:383:52: ( L_PAREN n1= numexpr ( COMMA n2= numexpr )+ R_PAREN )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:384:1: L_PAREN n1= numexpr ( COMMA n2= numexpr )+ R_PAREN
            {
            root_0 = (Object)adaptor.nil();

            L_PAREN100=(Token)match(input,L_PAREN,FOLLOW_L_PAREN_in_vector1246); if (state.failed) return retval;
            pushFollow(FOLLOW_numexpr_in_vector1251);
            n1=numexpr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, n1.getTree());
            if ( state.backtracking==0 ) {
              tmp.add((n1!=null?n1.val:null));
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:384:41: ( COMMA n2= numexpr )+
            int cnt27=0;
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0==COMMA) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:384:42: COMMA n2= numexpr
            	    {
            	    COMMA101=(Token)match(input,COMMA,FOLLOW_COMMA_in_vector1256); if (state.failed) return retval;
            	    pushFollow(FOLLOW_numexpr_in_vector1261);
            	    n2=numexpr();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, n2.getTree());
            	    if ( state.backtracking==0 ) {
            	      tmp.add((n2!=null?n2.val:null));
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt27 >= 1 ) break loop27;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(27, input);
                        throw eee;
                }
                cnt27++;
            } while (true);

            R_PAREN102=(Token)match(input,R_PAREN,FOLLOW_R_PAREN_in_vector1267); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
              retval.vec = new double[tmp.size()];
              for(int i=0;i<retval.vec.length;i++) 
              retval.vec[i] = tmp.get(i);

            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "vector"

    public static class intval_or_var_return extends ParserRuleReturnScope {
        public int val;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "intval_or_var"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:390:1: intval_or_var returns [int val] : numexpr ;
    public final MLSpaceSmallParser.intval_or_var_return intval_or_var() throws RecognitionException {
        MLSpaceSmallParser.intval_or_var_return retval = new MLSpaceSmallParser.intval_or_var_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        MLSpaceSmallParser.numexpr_return numexpr103 = null;



        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:390:33: ( numexpr )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:391:5: numexpr
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_numexpr_in_intval_or_var1286);
            numexpr103=numexpr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, numexpr103.getTree());
            if ( state.backtracking==0 ) {
              Double nval = (numexpr103!=null?numexpr103.val:null);
              	   retval.val = nval.intValue(); 
              	   if (nval - retval.val != 0.) 
              	     warnWithLine(Level.WARNING, "double value converted to int: " + nval);
              	  
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "intval_or_var"

    public static class numval_return extends ParserRuleReturnScope {
        public Double val;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "numval"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:398:1: numval returns [Double val] : ( INT | FLOAT | {...}? => ID );
    public final MLSpaceSmallParser.numval_return numval() throws RecognitionException {
        MLSpaceSmallParser.numval_return retval = new MLSpaceSmallParser.numval_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token INT104=null;
        Token FLOAT105=null;
        Token ID106=null;

        Object INT104_tree=null;
        Object FLOAT105_tree=null;
        Object ID106_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:398:28: ( INT | FLOAT | {...}? => ID )
            int alt28=3;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==INT) ) {
                alt28=1;
            }
            else if ( (LA28_0==FLOAT) ) {
                alt28=2;
            }
            else if ( (LA28_0==ID) && ((getSingleNumValFromVar(input.LT(1).getText())!=null))) {
                alt28=3;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 28, 0, input);

                throw nvae;
            }
            switch (alt28) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:399:5: INT
                    {
                    root_0 = (Object)adaptor.nil();

                    INT104=(Token)match(input,INT,FOLLOW_INT_in_numval1310); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    INT104_tree = (Object)adaptor.create(INT104);
                    adaptor.addChild(root_0, INT104_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.val = Double.parseDouble((INT104!=null?INT104.getText():null));
                    }

                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:400:5: FLOAT
                    {
                    root_0 = (Object)adaptor.nil();

                    FLOAT105=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_numval1318); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    FLOAT105_tree = (Object)adaptor.create(FLOAT105);
                    adaptor.addChild(root_0, FLOAT105_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.val = Double.parseDouble((FLOAT105!=null?FLOAT105.getText():null));
                    }

                    }
                    break;
                case 3 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:401:5: {...}? => ID
                    {
                    root_0 = (Object)adaptor.nil();

                    if ( !((getSingleNumValFromVar(input.LT(1).getText())!=null)) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "numval", "getSingleNumValFromVar(input.LT(1).getText())!=null");
                    }
                    ID106=(Token)match(input,ID,FOLLOW_ID_in_numval1329); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ID106_tree = (Object)adaptor.create(ID106);
                    adaptor.addChild(root_0, ID106_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.val = getSingleNumValFromVar((ID106!=null?ID106.getText():null));
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "numval"

    public static class species_defs_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "species_defs"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:405:1: species_defs : ( species_def ( SEMIC )? )+ ;
    public final MLSpaceSmallParser.species_defs_return species_defs() throws RecognitionException {
        MLSpaceSmallParser.species_defs_return retval = new MLSpaceSmallParser.species_defs_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMIC108=null;
        MLSpaceSmallParser.species_def_return species_def107 = null;


        Object SEMIC108_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:405:14: ( ( species_def ( SEMIC )? )+ )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:405:16: ( species_def ( SEMIC )? )+
            {
            root_0 = (Object)adaptor.nil();

            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:405:16: ( species_def ( SEMIC )? )+
            int cnt30=0;
            loop30:
            do {
                int alt30=2;
                alt30 = dfa30.predict(input);
                switch (alt30) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:405:17: species_def ( SEMIC )?
            	    {
            	    pushFollow(FOLLOW_species_def_in_species_defs1350);
            	    species_def107=species_def();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, species_def107.getTree());
            	    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:405:29: ( SEMIC )?
            	    int alt29=2;
            	    int LA29_0 = input.LA(1);

            	    if ( (LA29_0==SEMIC) ) {
            	        alt29=1;
            	    }
            	    switch (alt29) {
            	        case 1 :
            	            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:0:0: SEMIC
            	            {
            	            SEMIC108=(Token)match(input,SEMIC,FOLLOW_SEMIC_in_species_defs1352); if (state.failed) return retval;
            	            if ( state.backtracking==0 ) {
            	            SEMIC108_tree = (Object)adaptor.create(SEMIC108);
            	            adaptor.addChild(root_0, SEMIC108_tree);
            	            }

            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt30 >= 1 ) break loop30;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(30, input);
                        throw eee;
                }
                cnt30++;
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "species_defs"

    public static class species_def_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "species_def"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:407:1: species_def : ID L_PAREN (spa= attributes_def )? R_PAREN ( bindingsitesdef )? ;
    public final MLSpaceSmallParser.species_def_return species_def() throws RecognitionException {
        MLSpaceSmallParser.species_def_return retval = new MLSpaceSmallParser.species_def_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID109=null;
        Token L_PAREN110=null;
        Token R_PAREN111=null;
        MLSpaceSmallParser.attributes_def_return spa = null;

        MLSpaceSmallParser.bindingsitesdef_return bindingsitesdef112 = null;


        Object ID109_tree=null;
        Object L_PAREN110_tree=null;
        Object R_PAREN111_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:407:13: ( ID L_PAREN (spa= attributes_def )? R_PAREN ( bindingsitesdef )? )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:407:15: ID L_PAREN (spa= attributes_def )? R_PAREN ( bindingsitesdef )?
            {
            root_0 = (Object)adaptor.nil();

            ID109=(Token)match(input,ID,FOLLOW_ID_in_species_def1363); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID109_tree = (Object)adaptor.create(ID109);
            adaptor.addChild(root_0, ID109_tree);
            }
            L_PAREN110=(Token)match(input,L_PAREN,FOLLOW_L_PAREN_in_species_def1365); if (state.failed) return retval;
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:407:30: (spa= attributes_def )?
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==ID) ) {
                alt31=1;
            }
            switch (alt31) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:0:0: spa= attributes_def
                    {
                    pushFollow(FOLLOW_attributes_def_in_species_def1370);
                    spa=attributes_def();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, spa.getTree());

                    }
                    break;

            }

            R_PAREN111=(Token)match(input,R_PAREN,FOLLOW_R_PAREN_in_species_def1374); if (state.failed) return retval;
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:407:57: ( bindingsitesdef )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==LESSTHAN) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:0:0: bindingsitesdef
                    {
                    pushFollow(FOLLOW_bindingsitesdef_in_species_def1377);
                    bindingsitesdef112=bindingsitesdef();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, bindingsitesdef112.getTree());

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              parseTool.registerSpeciesDef((ID109!=null?ID109.getText():null),(spa!=null?spa.attMap:null),(bindingsitesdef112!=null?bindingsitesdef112.bs:null));
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "species_def"

    public static class attributes_def_return extends ParserRuleReturnScope {
        public Map<String,AbstractValueRange<?>> attMap;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "attributes_def"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:410:1: attributes_def returns [Map<String,AbstractValueRange<?>> attMap] : a1= attribute_def ( COMMA a2= attribute_def )* ;
    public final MLSpaceSmallParser.attributes_def_return attributes_def() throws RecognitionException {
        MLSpaceSmallParser.attributes_def_return retval = new MLSpaceSmallParser.attributes_def_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COMMA113=null;
        MLSpaceSmallParser.attribute_def_return a1 = null;

        MLSpaceSmallParser.attribute_def_return a2 = null;


        Object COMMA113_tree=null;

        retval.attMap = new NonNullMap<>();
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:411:38: (a1= attribute_def ( COMMA a2= attribute_def )* )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:412:1: a1= attribute_def ( COMMA a2= attribute_def )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_attribute_def_in_attributes_def1400);
            a1=attribute_def();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, a1.getTree());
            if ( state.backtracking==0 ) {
              retval.attMap.put((a1!=null?a1.name:null),(a1!=null?a1.val:null));
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:413:1: ( COMMA a2= attribute_def )*
            loop33:
            do {
                int alt33=2;
                int LA33_0 = input.LA(1);

                if ( (LA33_0==COMMA) ) {
                    alt33=1;
                }


                switch (alt33) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:413:2: COMMA a2= attribute_def
            	    {
            	    COMMA113=(Token)match(input,COMMA,FOLLOW_COMMA_in_attributes_def1405); if (state.failed) return retval;
            	    pushFollow(FOLLOW_attribute_def_in_attributes_def1410);
            	    a2=attribute_def();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, a2.getTree());
            	    if ( state.backtracking==0 ) {
            	      retval.attMap.put((a2!=null?a2.name:null),(a2!=null?a2.val:null));
            	    }

            	    }
            	    break;

            	default :
            	    break loop33;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "attributes_def"

    public static class attribute_def_return extends ParserRuleReturnScope {
        public String name;
        public AbstractValueRange<?> val;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "attribute_def"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:416:1: attribute_def returns [String name, AbstractValueRange<?> val] : att= ID COLON v= valset_or_const ;
    public final MLSpaceSmallParser.attribute_def_return attribute_def() throws RecognitionException {
        MLSpaceSmallParser.attribute_def_return retval = new MLSpaceSmallParser.attribute_def_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token att=null;
        Token COLON114=null;
        MLSpaceSmallParser.valset_or_const_return v = null;


        Object att_tree=null;
        Object COLON114_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:416:63: (att= ID COLON v= valset_or_const )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:417:3: att= ID COLON v= valset_or_const
            {
            root_0 = (Object)adaptor.nil();

            att=(Token)match(input,ID,FOLLOW_ID_in_attribute_def1430); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            att_tree = (Object)adaptor.create(att);
            adaptor.addChild(root_0, att_tree);
            }
            if ( state.backtracking==0 ) {
              retval.name = (att!=null?att.getText():null);
            }
            COLON114=(Token)match(input,COLON,FOLLOW_COLON_in_attribute_def1434); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            COLON114_tree = (Object)adaptor.create(COLON114);
            adaptor.addChild(root_0, COLON114_tree);
            }
            pushFollow(FOLLOW_valset_or_const_in_attribute_def1439);
            v=valset_or_const();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, v.getTree());
            if ( state.backtracking==0 ) {
              retval.val = (v!=null?v.val:null);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "attribute_def"

    public static class bindingsitesdef_return extends ParserRuleReturnScope {
        public BindingSites bs;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "bindingsitesdef"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:419:1: bindingsitesdef returns [BindingSites bs] : LESSTHAN bsd1= bindingsitedef ( COMMA bsd= bindingsitedef )* GREATERTHAN ;
    public final MLSpaceSmallParser.bindingsitesdef_return bindingsitesdef() throws RecognitionException {
        MLSpaceSmallParser.bindingsitesdef_return retval = new MLSpaceSmallParser.bindingsitesdef_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LESSTHAN115=null;
        Token COMMA116=null;
        Token GREATERTHAN117=null;
        MLSpaceSmallParser.bindingsitedef_return bsd1 = null;

        MLSpaceSmallParser.bindingsitedef_return bsd = null;


        Object LESSTHAN115_tree=null;
        Object COMMA116_tree=null;
        Object GREATERTHAN117_tree=null;

        BindingSites.Builder bsb = new BindingSites.Builder();
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:420:63: ( LESSTHAN bsd1= bindingsitedef ( COMMA bsd= bindingsitedef )* GREATERTHAN )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:421:2: LESSTHAN bsd1= bindingsitedef ( COMMA bsd= bindingsitedef )* GREATERTHAN
            {
            root_0 = (Object)adaptor.nil();

            LESSTHAN115=(Token)match(input,LESSTHAN,FOLLOW_LESSTHAN_in_bindingsitesdef1458); if (state.failed) return retval;
            pushFollow(FOLLOW_bindingsitedef_in_bindingsitesdef1465);
            bsd1=bindingsitedef();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, bsd1.getTree());
            if ( state.backtracking==0 ) {
              bsb.addSite((bsd1!=null?bsd1.name:null),(bsd1!=null?bsd1.relAngle:null));
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:423:2: ( COMMA bsd= bindingsitedef )*
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);

                if ( (LA34_0==COMMA) ) {
                    alt34=1;
                }


                switch (alt34) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:423:3: COMMA bsd= bindingsitedef
            	    {
            	    COMMA116=(Token)match(input,COMMA,FOLLOW_COMMA_in_bindingsitesdef1471); if (state.failed) return retval;
            	    pushFollow(FOLLOW_bindingsitedef_in_bindingsitesdef1476);
            	    bsd=bindingsitedef();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, bsd.getTree());
            	    if ( state.backtracking==0 ) {
            	      bsb.addSite((bsd!=null?bsd.name:null),(bsd!=null?bsd.relAngle:null));
            	    }

            	    }
            	    break;

            	default :
            	    break loop34;
                }
            } while (true);

            GREATERTHAN117=(Token)match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_bindingsitesdef1484); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
              retval.bs = bsb.build();
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "bindingsitesdef"

    public static class bindingsitedef_return extends ParserRuleReturnScope {
        public String name;
        public Double relAngle;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "bindingsitedef"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:426:1: bindingsitedef returns [String name, Double relAngle] : ID COLON ({...}? => ID | numexpr ) ;
    public final MLSpaceSmallParser.bindingsitedef_return bindingsitedef() throws RecognitionException {
        MLSpaceSmallParser.bindingsitedef_return retval = new MLSpaceSmallParser.bindingsitedef_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID118=null;
        Token COLON119=null;
        Token ID120=null;
        MLSpaceSmallParser.numexpr_return numexpr121 = null;


        Object ID118_tree=null;
        Object COLON119_tree=null;
        Object ID120_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:426:54: ( ID COLON ({...}? => ID | numexpr ) )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:427:3: ID COLON ({...}? => ID | numexpr )
            {
            root_0 = (Object)adaptor.nil();

            ID118=(Token)match(input,ID,FOLLOW_ID_in_bindingsitedef1501); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID118_tree = (Object)adaptor.create(ID118);
            adaptor.addChild(root_0, ID118_tree);
            }
            COLON119=(Token)match(input,COLON,FOLLOW_COLON_in_bindingsitedef1503); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
              retval.name = (ID118!=null?ID118.getText():null);
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:428:3: ({...}? => ID | numexpr )
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==ID) ) {
                int LA35_1 = input.LA(2);

                if ( ((synpred71_MLSpaceSmallParser()&&(input.LT(1).getText().toLowerCase().equals("any")))) ) {
                    alt35=1;
                }
                else if ( (true) ) {
                    alt35=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 35, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA35_0==L_BRACKET||LA35_0==L_PAREN||(LA35_0>=FLOAT && LA35_0<=MINUS)||(LA35_0>=MIN && LA35_0<=IF)||LA35_0==INT) ) {
                alt35=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 35, 0, input);

                throw nvae;
            }
            switch (alt35) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:428:4: {...}? => ID
                    {
                    if ( !((input.LT(1).getText().toLowerCase().equals("any"))) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "bindingsitedef", "input.LT(1).getText().toLowerCase().equals(\"any\")");
                    }
                    ID120=(Token)match(input,ID,FOLLOW_ID_in_bindingsitedef1514); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ID120_tree = (Object)adaptor.create(ID120);
                    adaptor.addChild(root_0, ID120_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.relAngle =null;
                    }

                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:429:5: numexpr
                    {
                    pushFollow(FOLLOW_numexpr_in_bindingsitedef1523);
                    numexpr121=numexpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, numexpr121.getTree());
                    if ( state.backtracking==0 ) {
                      retval.relAngle = (numexpr121!=null?numexpr121.val:null);
                    }

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "bindingsitedef"

    public static class species_return extends ParserRuleReturnScope {
        public String specName;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "species"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:431:1: species returns [String specName] : {...}? ID ;
    public final MLSpaceSmallParser.species_return species() throws RecognitionException {
        MLSpaceSmallParser.species_return retval = new MLSpaceSmallParser.species_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID122=null;

        Object ID122_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:431:34: ({...}? ID )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:432:1: {...}? ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !((parseTool.isValidSpecies(input.LT(1).getText()))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "species", "parseTool.isValidSpecies(input.LT(1).getText())");
            }
            ID122=(Token)match(input,ID,FOLLOW_ID_in_species1539); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID122_tree = (Object)adaptor.create(ID122);
            adaptor.addChild(root_0, ID122_tree);
            }
            if ( state.backtracking==0 ) {
              retval.specName = (ID122!=null?ID122.getText():null);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "species"

    public static class rules_return extends ParserRuleReturnScope {
        public RuleCollection rules;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rules"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:435:1: rules returns [RuleCollection rules] : ( rule ( SEMIC )? {...}?)+ ;
    public final MLSpaceSmallParser.rules_return rules() throws RecognitionException {
        MLSpaceSmallParser.rules_return retval = new MLSpaceSmallParser.rules_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMIC124=null;
        MLSpaceSmallParser.rule_return rule123 = null;


        Object SEMIC124_tree=null;

        retval.rules = new RuleCollection();
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:436:38: ( ( rule ( SEMIC )? {...}?)+ )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:437:3: ( rule ( SEMIC )? {...}?)+
            {
            root_0 = (Object)adaptor.nil();

            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:437:3: ( rule ( SEMIC )? {...}?)+
            int cnt37=0;
            loop37:
            do {
                int alt37=2;
                int LA37_0 = input.LA(1);

                if ( (LA37_0==ID) ) {
                    alt37=1;
                }


                switch (alt37) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:437:4: rule ( SEMIC )? {...}?
            	    {
            	    pushFollow(FOLLOW_rule_in_rules1561);
            	    rule123=rule();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, rule123.getTree());
            	    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:437:9: ( SEMIC )?
            	    int alt36=2;
            	    alt36 = dfa36.predict(input);
            	    switch (alt36) {
            	        case 1 :
            	            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:0:0: SEMIC
            	            {
            	            SEMIC124=(Token)match(input,SEMIC,FOLLOW_SEMIC_in_rules1563); if (state.failed) return retval;
            	            if ( state.backtracking==0 ) {
            	            SEMIC124_tree = (Object)adaptor.create(SEMIC124);
            	            adaptor.addChild(root_0, SEMIC124_tree);
            	            }

            	            }
            	            break;

            	    }

            	    if ( !(((rule123!=null?rule123.rv:null) != null)) ) {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        throw new FailedPredicateException(input, "rules", "$rule.rv != null");
            	    }
            	    if ( state.backtracking==0 ) {
            	      retval.rules.add((rule123!=null?rule123.rv:null));
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt37 >= 1 ) break loop37;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(37, input);
                        throw eee;
                }
                cnt37++;
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rules"

    public static class rule_return extends ParserRuleReturnScope {
        public MLSpaceRule rv;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rule"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:439:1: rule returns [MLSpaceRule rv] : ( ID COLON )? lhs= rule_left_hand_side ARROW (rhs= rule_right_hand_side )? AT ( ( rpmark )=> rpmark )? n= varexpr ;
    public final MLSpaceSmallParser.rule_return rule() throws RecognitionException {
        MLSpaceSmallParser.rule_return retval = new MLSpaceSmallParser.rule_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID125=null;
        Token COLON126=null;
        Token ARROW127=null;
        Token AT128=null;
        MLSpaceSmallParser.rule_left_hand_side_return lhs = null;

        MLSpaceSmallParser.rule_right_hand_side_return rhs = null;

        MLSpaceSmallParser.varexpr_return n = null;

        MLSpaceSmallParser.rpmark_return rpmark129 = null;


        Object ID125_tree=null;
        Object COLON126_tree=null;
        Object ARROW127_tree=null;
        Object AT128_tree=null;

        localRuleVars.clear();String rateOrProb = ""; String name = null;
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:439:103: ( ( ID COLON )? lhs= rule_left_hand_side ARROW (rhs= rule_right_hand_side )? AT ( ( rpmark )=> rpmark )? n= varexpr )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:440:3: ( ID COLON )? lhs= rule_left_hand_side ARROW (rhs= rule_right_hand_side )? AT ( ( rpmark )=> rpmark )? n= varexpr
            {
            root_0 = (Object)adaptor.nil();

            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:440:3: ( ID COLON )?
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==ID) ) {
                int LA38_1 = input.LA(2);

                if ( (LA38_1==COLON) ) {
                    alt38=1;
                }
            }
            switch (alt38) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:440:4: ID COLON
                    {
                    ID125=(Token)match(input,ID,FOLLOW_ID_in_rule1590); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ID125_tree = (Object)adaptor.create(ID125);
                    adaptor.addChild(root_0, ID125_tree);
                    }
                    COLON126=(Token)match(input,COLON,FOLLOW_COLON_in_rule1592); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      name = (ID125!=null?ID125.getText():null);
                    }

                    }
                    break;

            }

            pushFollow(FOLLOW_rule_left_hand_side_in_rule1603);
            lhs=rule_left_hand_side();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, lhs.getTree());
            ARROW127=(Token)match(input,ARROW,FOLLOW_ARROW_in_rule1607); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ARROW127_tree = (Object)adaptor.create(ARROW127);
            adaptor.addChild(root_0, ARROW127_tree);
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:443:6: (rhs= rule_right_hand_side )?
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( (LA39_0==ID) ) {
                alt39=1;
            }
            switch (alt39) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:0:0: rhs= rule_right_hand_side
                    {
                    pushFollow(FOLLOW_rule_right_hand_side_in_rule1614);
                    rhs=rule_right_hand_side();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rhs.getTree());

                    }
                    break;

            }

            AT128=(Token)match(input,AT,FOLLOW_AT_in_rule1621); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            AT128_tree = (Object)adaptor.create(AT128);
            adaptor.addChild(root_0, AT128_tree);
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:445:3: ( ( rpmark )=> rpmark )?
            int alt40=2;
            int LA40_0 = input.LA(1);

            if ( (LA40_0==ID) ) {
                int LA40_1 = input.LA(2);

                if ( (LA40_1==EQ) && (synpred76_MLSpaceSmallParser())) {
                    alt40=1;
                }
            }
            switch (alt40) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:445:4: ( rpmark )=> rpmark
                    {
                    pushFollow(FOLLOW_rpmark_in_rule1632);
                    rpmark129=rpmark();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rpmark129.getTree());
                    if ( state.backtracking==0 ) {
                      rateOrProb = (rpmark129!=null?rpmark129.value:null);
                    }

                    }
                    break;

            }

            pushFollow(FOLLOW_varexpr_in_rule1642);
            n=varexpr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, n.getTree());
            if ( state.backtracking==0 ) {
              retval.rv =parseTool.parseRule(name,(lhs!=null?lhs.lhs:null),(rhs!=null?rhs.context:null),(rhs!=null?rhs.rhs:null),(n!=null?n.node:null),rateOrProb);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rule"

    public static class rpmark_return extends ParserRuleReturnScope {
        public String value;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rpmark"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:449:1: rpmark returns [String value] : ( ID {...}? EQ ) ;
    public final MLSpaceSmallParser.rpmark_return rpmark() throws RecognitionException {
        MLSpaceSmallParser.rpmark_return retval = new MLSpaceSmallParser.rpmark_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID130=null;
        Token EQ131=null;

        Object ID130_tree=null;
        Object EQ131_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:449:30: ( ( ID {...}? EQ ) )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:450:1: ( ID {...}? EQ )
            {
            root_0 = (Object)adaptor.nil();

            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:450:1: ( ID {...}? EQ )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:450:2: ID {...}? EQ
            {
            ID130=(Token)match(input,ID,FOLLOW_ID_in_rpmark1659); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID130_tree = (Object)adaptor.create(ID130);
            adaptor.addChild(root_0, ID130_tree);
            }
            if ( !((MLSpaceRuleCreator.MARKERS.contains((ID130!=null?ID130.getText():null)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "rpmark", "MLSpaceRuleCreator.MARKERS.contains($ID.text)");
            }
            EQ131=(Token)match(input,EQ,FOLLOW_EQ_in_rpmark1663); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            EQ131_tree = (Object)adaptor.create(EQ131);
            adaptor.addChild(root_0, EQ131_tree);
            }
            if ( state.backtracking==0 ) {
              retval.value = (ID130!=null?ID130.getText():null);
            }

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rpmark"

    public static class rule_left_hand_side_return extends ParserRuleReturnScope {
        public RuleSide lhs;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rule_left_hand_side"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:452:1: rule_left_hand_side returns [RuleSide lhs] : e1= entity_match ( ( L_BRACKET | entsepleft ) (e2= entity_match ( entsepleft en= entity_match )* )? ({...}? R_BRACKET | ) )? ;
    public final MLSpaceSmallParser.rule_left_hand_side_return rule_left_hand_side() throws RecognitionException {
        MLSpaceSmallParser.rule_left_hand_side_return retval = new MLSpaceSmallParser.rule_left_hand_side_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token L_BRACKET132=null;
        Token R_BRACKET135=null;
        MLSpaceSmallParser.entity_match_return e1 = null;

        MLSpaceSmallParser.entity_match_return e2 = null;

        MLSpaceSmallParser.entity_match_return en = null;

        MLSpaceSmallParser.entsepleft_return entsepleft133 = null;

        MLSpaceSmallParser.entsepleft_return entsepleft134 = null;


        Object L_BRACKET132_tree=null;
        Object R_BRACKET135_tree=null;

        RuleSide.Builder lhsBuilder = new RuleSide.Builder();
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:454:35: (e1= entity_match ( ( L_BRACKET | entsepleft ) (e2= entity_match ( entsepleft en= entity_match )* )? ({...}? R_BRACKET | ) )? )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:455:3: e1= entity_match ( ( L_BRACKET | entsepleft ) (e2= entity_match ( entsepleft en= entity_match )* )? ({...}? R_BRACKET | ) )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_entity_match_in_rule_left_hand_side1694);
            e1=entity_match();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, e1.getTree());
            if ( state.backtracking==0 ) {
              lhsBuilder.addEntity((e1!=null?e1.ent:null));
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:456:3: ( ( L_BRACKET | entsepleft ) (e2= entity_match ( entsepleft en= entity_match )* )? ({...}? R_BRACKET | ) )?
            int alt45=2;
            int LA45_0 = input.LA(1);

            if ( (LA45_0==L_BRACKET||LA45_0==PLUS) ) {
                alt45=1;
            }
            switch (alt45) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:456:4: ( L_BRACKET | entsepleft ) (e2= entity_match ( entsepleft en= entity_match )* )? ({...}? R_BRACKET | )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:456:4: ( L_BRACKET | entsepleft )
                    int alt41=2;
                    int LA41_0 = input.LA(1);

                    if ( (LA41_0==L_BRACKET) ) {
                        alt41=1;
                    }
                    else if ( (LA41_0==PLUS) ) {
                        alt41=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 41, 0, input);

                        throw nvae;
                    }
                    switch (alt41) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:456:5: L_BRACKET
                            {
                            L_BRACKET132=(Token)match(input,L_BRACKET,FOLLOW_L_BRACKET_in_rule_left_hand_side1702); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            L_BRACKET132_tree = (Object)adaptor.create(L_BRACKET132);
                            adaptor.addChild(root_0, L_BRACKET132_tree);
                            }
                            if ( state.backtracking==0 ) {
                              lhsBuilder.makeLastContext();
                            }

                            }
                            break;
                        case 2 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:457:5: entsepleft
                            {
                            pushFollow(FOLLOW_entsepleft_in_rule_left_hand_side1710);
                            entsepleft133=entsepleft();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, entsepleft133.getTree());

                            }
                            break;

                    }

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:458:4: (e2= entity_match ( entsepleft en= entity_match )* )?
                    int alt43=2;
                    int LA43_0 = input.LA(1);

                    if ( (LA43_0==ID) ) {
                        alt43=1;
                    }
                    switch (alt43) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:458:5: e2= entity_match ( entsepleft en= entity_match )*
                            {
                            pushFollow(FOLLOW_entity_match_in_rule_left_hand_side1719);
                            e2=entity_match();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, e2.getTree());
                            if ( state.backtracking==0 ) {
                              lhsBuilder.addEntity((e2!=null?e2.ent:null));
                            }
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:459:5: ( entsepleft en= entity_match )*
                            loop42:
                            do {
                                int alt42=2;
                                int LA42_0 = input.LA(1);

                                if ( (LA42_0==PLUS) ) {
                                    alt42=1;
                                }


                                switch (alt42) {
                            	case 1 :
                            	    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:459:6: entsepleft en= entity_match
                            	    {
                            	    pushFollow(FOLLOW_entsepleft_in_rule_left_hand_side1728);
                            	    entsepleft134=entsepleft();

                            	    state._fsp--;
                            	    if (state.failed) return retval;
                            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, entsepleft134.getTree());
                            	    pushFollow(FOLLOW_entity_match_in_rule_left_hand_side1732);
                            	    en=entity_match();

                            	    state._fsp--;
                            	    if (state.failed) return retval;
                            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, en.getTree());
                            	    if ( state.backtracking==0 ) {
                            	      lhsBuilder.addEntity((en!=null?en.ent:null));
                            	    }

                            	    }
                            	    break;

                            	default :
                            	    break loop42;
                                }
                            } while (true);


                            }
                            break;

                    }

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:460:4: ({...}? R_BRACKET | )
                    int alt44=2;
                    int LA44_0 = input.LA(1);

                    if ( (LA44_0==R_BRACKET) ) {
                        alt44=1;
                    }
                    else if ( (LA44_0==ARROW) ) {
                        alt44=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 44, 0, input);

                        throw nvae;
                    }
                    switch (alt44) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:460:5: {...}? R_BRACKET
                            {
                            if ( !((lhsBuilder.isContextSet())) ) {
                                if (state.backtracking>0) {state.failed=true; return retval;}
                                throw new FailedPredicateException(input, "rule_left_hand_side", "lhsBuilder.isContextSet()");
                            }
                            R_BRACKET135=(Token)match(input,R_BRACKET,FOLLOW_R_BRACKET_in_rule_left_hand_side1746); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            R_BRACKET135_tree = (Object)adaptor.create(R_BRACKET135);
                            adaptor.addChild(root_0, R_BRACKET135_tree);
                            }

                            }
                            break;
                        case 2 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:460:46: 
                            {
                            if ( state.backtracking==0 ) {
                            }

                            }
                            break;

                    }


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) {
              retval.lhs = lhsBuilder.build();
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rule_left_hand_side"

    public static class rule_right_hand_side_return extends ParserRuleReturnScope {
        public ModEntity context;
        public List<ModEntity> rhs;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rule_right_hand_side"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:463:1: rule_right_hand_side returns [ModEntity context, List<ModEntity> rhs] : e1= entity_result[false] ( ( L_BRACKET | entsepright ) (e2= entity_result[false] ( entsepright en= entity_result[false] )* )? ({...}? R_BRACKET | ) )? ;
    public final MLSpaceSmallParser.rule_right_hand_side_return rule_right_hand_side() throws RecognitionException {
        MLSpaceSmallParser.rule_right_hand_side_return retval = new MLSpaceSmallParser.rule_right_hand_side_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token L_BRACKET136=null;
        Token R_BRACKET139=null;
        MLSpaceSmallParser.entity_result_return e1 = null;

        MLSpaceSmallParser.entity_result_return e2 = null;

        MLSpaceSmallParser.entity_result_return en = null;

        MLSpaceSmallParser.entsepright_return entsepright137 = null;

        MLSpaceSmallParser.entsepright_return entsepright138 = null;


        Object L_BRACKET136_tree=null;
        Object R_BRACKET139_tree=null;

         retval.rhs = new ArrayList<>();
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:464:34: (e1= entity_result[false] ( ( L_BRACKET | entsepright ) (e2= entity_result[false] ( entsepright en= entity_result[false] )* )? ({...}? R_BRACKET | ) )? )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:465:3: e1= entity_result[false] ( ( L_BRACKET | entsepright ) (e2= entity_result[false] ( entsepright en= entity_result[false] )* )? ({...}? R_BRACKET | ) )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_entity_result_in_rule_right_hand_side1773);
            e1=entity_result(false);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, e1.getTree());
            if ( state.backtracking==0 ) {
              retval.rhs.add((e1!=null?e1.ent:null));
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:466:3: ( ( L_BRACKET | entsepright ) (e2= entity_result[false] ( entsepright en= entity_result[false] )* )? ({...}? R_BRACKET | ) )?
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==L_BRACKET||LA50_0==DOT||LA50_0==PLUS) ) {
                alt50=1;
            }
            switch (alt50) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:466:4: ( L_BRACKET | entsepright ) (e2= entity_result[false] ( entsepright en= entity_result[false] )* )? ({...}? R_BRACKET | )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:466:4: ( L_BRACKET | entsepright )
                    int alt46=2;
                    int LA46_0 = input.LA(1);

                    if ( (LA46_0==L_BRACKET) ) {
                        alt46=1;
                    }
                    else if ( (LA46_0==DOT||LA46_0==PLUS) ) {
                        alt46=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 46, 0, input);

                        throw nvae;
                    }
                    switch (alt46) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:466:5: L_BRACKET
                            {
                            L_BRACKET136=(Token)match(input,L_BRACKET,FOLLOW_L_BRACKET_in_rule_right_hand_side1782); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            L_BRACKET136_tree = (Object)adaptor.create(L_BRACKET136);
                            adaptor.addChild(root_0, L_BRACKET136_tree);
                            }
                            if ( state.backtracking==0 ) {
                              retval.context = retval.rhs.remove(0);
                            }

                            }
                            break;
                        case 2 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:466:46: entsepright
                            {
                            pushFollow(FOLLOW_entsepright_in_rule_right_hand_side1788);
                            entsepright137=entsepright();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, entsepright137.getTree());

                            }
                            break;

                    }

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:467:4: (e2= entity_result[false] ( entsepright en= entity_result[false] )* )?
                    int alt48=2;
                    int LA48_0 = input.LA(1);

                    if ( (LA48_0==ID) ) {
                        alt48=1;
                    }
                    switch (alt48) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:467:5: e2= entity_result[false] ( entsepright en= entity_result[false] )*
                            {
                            pushFollow(FOLLOW_entity_result_in_rule_right_hand_side1797);
                            e2=entity_result(false);

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, e2.getTree());
                            if ( state.backtracking==0 ) {
                              retval.rhs.add((e2!=null?e2.ent:null));
                            }
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:468:4: ( entsepright en= entity_result[false] )*
                            loop47:
                            do {
                                int alt47=2;
                                int LA47_0 = input.LA(1);

                                if ( (LA47_0==DOT||LA47_0==PLUS) ) {
                                    alt47=1;
                                }


                                switch (alt47) {
                            	case 1 :
                            	    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:468:5: entsepright en= entity_result[false]
                            	    {
                            	    pushFollow(FOLLOW_entsepright_in_rule_right_hand_side1806);
                            	    entsepright138=entsepright();

                            	    state._fsp--;
                            	    if (state.failed) return retval;
                            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, entsepright138.getTree());
                            	    pushFollow(FOLLOW_entity_result_in_rule_right_hand_side1810);
                            	    en=entity_result(false);

                            	    state._fsp--;
                            	    if (state.failed) return retval;
                            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, en.getTree());
                            	    if ( state.backtracking==0 ) {
                            	      retval.rhs.add((en!=null?en.ent:null));
                            	    }

                            	    }
                            	    break;

                            	default :
                            	    break loop47;
                                }
                            } while (true);


                            }
                            break;

                    }

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:469:4: ({...}? R_BRACKET | )
                    int alt49=2;
                    int LA49_0 = input.LA(1);

                    if ( (LA49_0==R_BRACKET) ) {
                        alt49=1;
                    }
                    else if ( (LA49_0==EOF||LA49_0==AT) ) {
                        alt49=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 49, 0, input);

                        throw nvae;
                    }
                    switch (alt49) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:469:5: {...}? R_BRACKET
                            {
                            if ( !((retval.context != null)) ) {
                                if (state.backtracking>0) {state.failed=true; return retval;}
                                throw new FailedPredicateException(input, "rule_right_hand_side", "$context != null");
                            }
                            R_BRACKET139=(Token)match(input,R_BRACKET,FOLLOW_R_BRACKET_in_rule_right_hand_side1825); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            R_BRACKET139_tree = (Object)adaptor.create(R_BRACKET139);
                            adaptor.addChild(root_0, R_BRACKET139_tree);
                            }

                            }
                            break;
                        case 2 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:469:37: 
                            {
                            if ( state.backtracking==0 ) {
                            }

                            }
                            break;

                    }


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rule_right_hand_side"

    public static class entity_match_return extends ParserRuleReturnScope {
        public RuleEntityWithBindings ent;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "entity_match"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:472:1: entity_match returns [RuleEntityWithBindings ent] : ( species )=> species ( L_PAREN (atts= attributes_match )? R_PAREN )? (bs= bindingsites )? ;
    public final MLSpaceSmallParser.entity_match_return entity_match() throws RecognitionException {
        MLSpaceSmallParser.entity_match_return retval = new MLSpaceSmallParser.entity_match_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token L_PAREN141=null;
        Token R_PAREN142=null;
        MLSpaceSmallParser.attributes_match_return atts = null;

        MLSpaceSmallParser.bindingsites_return bs = null;

        MLSpaceSmallParser.species_return species140 = null;


        Object L_PAREN141_tree=null;
        Object R_PAREN142_tree=null;

        Species spec = null;
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:473:28: ( ( species )=> species ( L_PAREN (atts= attributes_match )? R_PAREN )? (bs= bindingsites )? )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:474:3: ( species )=> species ( L_PAREN (atts= attributes_match )? R_PAREN )? (bs= bindingsites )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_species_in_entity_match1854);
            species140=species();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, species140.getTree());
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:474:22: ( L_PAREN (atts= attributes_match )? R_PAREN )?
            int alt52=2;
            int LA52_0 = input.LA(1);

            if ( (LA52_0==L_PAREN) ) {
                alt52=1;
            }
            switch (alt52) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:474:23: L_PAREN (atts= attributes_match )? R_PAREN
                    {
                    L_PAREN141=(Token)match(input,L_PAREN,FOLLOW_L_PAREN_in_entity_match1857); if (state.failed) return retval;
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:474:36: (atts= attributes_match )?
                    int alt51=2;
                    int LA51_0 = input.LA(1);

                    if ( (LA51_0==L_PAREN||LA51_0==ID) ) {
                        alt51=1;
                    }
                    switch (alt51) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:0:0: atts= attributes_match
                            {
                            pushFollow(FOLLOW_attributes_match_in_entity_match1862);
                            atts=attributes_match();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, atts.getTree());

                            }
                            break;

                    }

                    R_PAREN142=(Token)match(input,R_PAREN,FOLLOW_R_PAREN_in_entity_match1865); if (state.failed) return retval;

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              parseTool.checkEntityDefPlausibility((species140!=null?species140.specName:null),(atts!=null?atts.attMap:null));
                  spec = parseTool.getSpeciesForName((species140!=null?species140.specName:null));
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:477:5: (bs= bindingsites )?
            int alt53=2;
            int LA53_0 = input.LA(1);

            if ( (LA53_0==LESSTHAN) ) {
                alt53=1;
            }
            switch (alt53) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:0:0: bs= bindingsites
                    {
                    pushFollow(FOLLOW_bindingsites_in_entity_match1879);
                    bs=bindingsites();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, bs.getTree());

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              retval.ent = new RuleEntityWithBindings(spec,(atts!=null?atts.attMap:null),(bs!=null?bs.map:null));
                  if ((bs!=null?bs.map:null) != null)
                  parseTool.validateBindingSites(spec,(bs!=null?bs.map:null).keySet());
                 
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "entity_match"

    public static class bindingsites_return extends ParserRuleReturnScope {
        public Map<String,RuleEntityWithBindings> map;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "bindingsites"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:483:1: bindingsites returns [Map<String,RuleEntityWithBindings> map] : LESSTHAN bsm1= bindingsite ( COMMA bsm= bindingsite )* GREATERTHAN ;
    public final MLSpaceSmallParser.bindingsites_return bindingsites() throws RecognitionException {
        MLSpaceSmallParser.bindingsites_return retval = new MLSpaceSmallParser.bindingsites_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LESSTHAN143=null;
        Token COMMA144=null;
        Token GREATERTHAN145=null;
        MLSpaceSmallParser.bindingsite_return bsm1 = null;

        MLSpaceSmallParser.bindingsite_return bsm = null;


        Object LESSTHAN143_tree=null;
        Object COMMA144_tree=null;
        Object GREATERTHAN145_tree=null;

        retval.map = new NonNullMap<String,RuleEntityWithBindings>(false,true);
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:484:74: ( LESSTHAN bsm1= bindingsite ( COMMA bsm= bindingsite )* GREATERTHAN )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:485:2: LESSTHAN bsm1= bindingsite ( COMMA bsm= bindingsite )* GREATERTHAN
            {
            root_0 = (Object)adaptor.nil();

            LESSTHAN143=(Token)match(input,LESSTHAN,FOLLOW_LESSTHAN_in_bindingsites1903); if (state.failed) return retval;
            pushFollow(FOLLOW_bindingsite_in_bindingsites1910);
            bsm1=bindingsite();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, bsm1.getTree());
            if ( state.backtracking==0 ) {
              retval.map.put((bsm1!=null?bsm1.name:null),(bsm1!=null?bsm1.ent:null));
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:487:2: ( COMMA bsm= bindingsite )*
            loop54:
            do {
                int alt54=2;
                int LA54_0 = input.LA(1);

                if ( (LA54_0==COMMA) ) {
                    alt54=1;
                }


                switch (alt54) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:487:3: COMMA bsm= bindingsite
            	    {
            	    COMMA144=(Token)match(input,COMMA,FOLLOW_COMMA_in_bindingsites1916); if (state.failed) return retval;
            	    pushFollow(FOLLOW_bindingsite_in_bindingsites1921);
            	    bsm=bindingsite();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, bsm.getTree());
            	    if ( state.backtracking==0 ) {
            	      retval.map.put((bsm!=null?bsm.name:null),(bsm!=null?bsm.ent:null));
            	    }

            	    }
            	    break;

            	default :
            	    break loop54;
                }
            } while (true);

            GREATERTHAN145=(Token)match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_bindingsites1929); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "bindingsites"

    public static class bindingsite_return extends ParserRuleReturnScope {
        public String name;
        public RuleEntityWithBindings ent;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "bindingsite"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:490:1: bindingsite returns [String name, RuleEntityWithBindings ent] : ID COLON (e= entity_match | FREE | OCC ) ;
    public final MLSpaceSmallParser.bindingsite_return bindingsite() throws RecognitionException {
        MLSpaceSmallParser.bindingsite_return retval = new MLSpaceSmallParser.bindingsite_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID146=null;
        Token COLON147=null;
        Token FREE148=null;
        Token OCC149=null;
        MLSpaceSmallParser.entity_match_return e = null;


        Object ID146_tree=null;
        Object COLON147_tree=null;
        Object FREE148_tree=null;
        Object OCC149_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:490:62: ( ID COLON (e= entity_match | FREE | OCC ) )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:491:3: ID COLON (e= entity_match | FREE | OCC )
            {
            root_0 = (Object)adaptor.nil();

            ID146=(Token)match(input,ID,FOLLOW_ID_in_bindingsite1944); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID146_tree = (Object)adaptor.create(ID146);
            adaptor.addChild(root_0, ID146_tree);
            }
            COLON147=(Token)match(input,COLON,FOLLOW_COLON_in_bindingsite1946); if (state.failed) return retval;
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:492:3: (e= entity_match | FREE | OCC )
            int alt55=3;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt55=1;
                }
                break;
            case FREE:
                {
                alt55=2;
                }
                break;
            case OCC:
                {
                alt55=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 55, 0, input);

                throw nvae;
            }

            switch (alt55) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:492:4: e= entity_match
                    {
                    pushFollow(FOLLOW_entity_match_in_bindingsite1955);
                    e=entity_match();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, e.getTree());
                    if ( state.backtracking==0 ) {
                      retval.name = (ID146!=null?ID146.getText():null); retval.ent = (e!=null?e.ent:null);
                    }

                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:493:4: FREE
                    {
                    FREE148=(Token)match(input,FREE,FOLLOW_FREE_in_bindingsite1963); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    FREE148_tree = (Object)adaptor.create(FREE148);
                    adaptor.addChild(root_0, FREE148_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.name = (ID146!=null?ID146.getText():null); retval.ent = null;
                    }

                    }
                    break;
                case 3 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:494:4: OCC
                    {
                    OCC149=(Token)match(input,OCC,FOLLOW_OCC_in_bindingsite1970); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    OCC149_tree = (Object)adaptor.create(OCC149);
                    adaptor.addChild(root_0, OCC149_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.name = (ID146!=null?ID146.getText():null); retval.ent = new AllMatchingRuleEntity(true);
                    }

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "bindingsite"

    public static class entities_result_return extends ParserRuleReturnScope {
        public List<ModEntity> list;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "entities_result"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:497:1: entities_result[boolean ignore] returns [List<ModEntity> list] : (e= entity_result[ignore] ( entsepright e2= entity_result[ignore] )* )? ;
    public final MLSpaceSmallParser.entities_result_return entities_result(boolean ignore) throws RecognitionException {
        MLSpaceSmallParser.entities_result_return retval = new MLSpaceSmallParser.entities_result_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        MLSpaceSmallParser.entity_result_return e = null;

        MLSpaceSmallParser.entity_result_return e2 = null;

        MLSpaceSmallParser.entsepright_return entsepright150 = null;



        retval.list = new ArrayList<>();
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:498:36: ( (e= entity_result[ignore] ( entsepright e2= entity_result[ignore] )* )? )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:499:2: (e= entity_result[ignore] ( entsepright e2= entity_result[ignore] )* )?
            {
            root_0 = (Object)adaptor.nil();

            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:499:2: (e= entity_result[ignore] ( entsepright e2= entity_result[ignore] )* )?
            int alt57=2;
            int LA57_0 = input.LA(1);

            if ( (LA57_0==ID) ) {
                alt57=1;
            }
            switch (alt57) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:499:3: e= entity_result[ignore] ( entsepright e2= entity_result[ignore] )*
                    {
                    pushFollow(FOLLOW_entity_result_in_entities_result2002);
                    e=entity_result(ignore);

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, e.getTree());
                    if ( state.backtracking==0 ) {
                      retval.list.add((e!=null?e.ent:null));
                    }
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:499:48: ( entsepright e2= entity_result[ignore] )*
                    loop56:
                    do {
                        int alt56=2;
                        int LA56_0 = input.LA(1);

                        if ( (LA56_0==DOT||LA56_0==PLUS) ) {
                            alt56=1;
                        }


                        switch (alt56) {
                    	case 1 :
                    	    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:499:49: entsepright e2= entity_result[ignore]
                    	    {
                    	    pushFollow(FOLLOW_entsepright_in_entities_result2008);
                    	    entsepright150=entsepright();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    pushFollow(FOLLOW_entity_result_in_entities_result2013);
                    	    e2=entity_result(ignore);

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, e2.getTree());
                    	    if ( state.backtracking==0 ) {
                    	      retval.list.add((e2!=null?e2.ent:null));
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop56;
                        }
                    } while (true);


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "entities_result"

    public static class entity_result_return extends ParserRuleReturnScope {
        public ModEntity ent;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "entity_result"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:501:1: entity_result[boolean ignore] returns [ModEntity ent] : species ( L_PAREN (name= ID m= valmod ( COMMA name= ID m= valmod )* )? R_PAREN )? (ba= bindingactions )? ;
    public final MLSpaceSmallParser.entity_result_return entity_result(boolean ignore) throws RecognitionException {
        MLSpaceSmallParser.entity_result_return retval = new MLSpaceSmallParser.entity_result_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token name=null;
        Token L_PAREN152=null;
        Token COMMA153=null;
        Token R_PAREN154=null;
        MLSpaceSmallParser.valmod_return m = null;

        MLSpaceSmallParser.bindingactions_return ba = null;

        MLSpaceSmallParser.species_return species151 = null;


        Object name_tree=null;
        Object L_PAREN152_tree=null;
        Object COMMA153_tree=null;
        Object R_PAREN154_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:501:54: ( species ( L_PAREN (name= ID m= valmod ( COMMA name= ID m= valmod )* )? R_PAREN )? (ba= bindingactions )? )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:502:3: species ( L_PAREN (name= ID m= valmod ( COMMA name= ID m= valmod )* )? R_PAREN )? (ba= bindingactions )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_species_in_entity_result2034);
            species151=species();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, species151.getTree());
            if ( state.backtracking==0 ) {
              retval.ent = new ModEntity((species151!=null?species151.specName:null));
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:503:3: ( L_PAREN (name= ID m= valmod ( COMMA name= ID m= valmod )* )? R_PAREN )?
            int alt60=2;
            int LA60_0 = input.LA(1);

            if ( (LA60_0==L_PAREN) ) {
                alt60=1;
            }
            switch (alt60) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:503:4: L_PAREN (name= ID m= valmod ( COMMA name= ID m= valmod )* )? R_PAREN
                    {
                    L_PAREN152=(Token)match(input,L_PAREN,FOLLOW_L_PAREN_in_entity_result2042); if (state.failed) return retval;
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:503:13: (name= ID m= valmod ( COMMA name= ID m= valmod )* )?
                    int alt59=2;
                    int LA59_0 = input.LA(1);

                    if ( (LA59_0==ID) ) {
                        alt59=1;
                    }
                    switch (alt59) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:503:14: name= ID m= valmod ( COMMA name= ID m= valmod )*
                            {
                            name=(Token)match(input,ID,FOLLOW_ID_in_entity_result2048); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            name_tree = (Object)adaptor.create(name);
                            adaptor.addChild(root_0, name_tree);
                            }
                            pushFollow(FOLLOW_valmod_in_entity_result2052);
                            m=valmod();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, m.getTree());
                            if ( state.backtracking==0 ) {
                              retval.ent.addAttMod((name!=null?name.getText():null),(m!=null?m.val:null));
                            }
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:504:7: ( COMMA name= ID m= valmod )*
                            loop58:
                            do {
                                int alt58=2;
                                int LA58_0 = input.LA(1);

                                if ( (LA58_0==COMMA) ) {
                                    alt58=1;
                                }


                                switch (alt58) {
                            	case 1 :
                            	    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:504:8: COMMA name= ID m= valmod
                            	    {
                            	    COMMA153=(Token)match(input,COMMA,FOLLOW_COMMA_in_entity_result2064); if (state.failed) return retval;
                            	    if ( state.backtracking==0 ) {
                            	    COMMA153_tree = (Object)adaptor.create(COMMA153);
                            	    adaptor.addChild(root_0, COMMA153_tree);
                            	    }
                            	    name=(Token)match(input,ID,FOLLOW_ID_in_entity_result2068); if (state.failed) return retval;
                            	    if ( state.backtracking==0 ) {
                            	    name_tree = (Object)adaptor.create(name);
                            	    adaptor.addChild(root_0, name_tree);
                            	    }
                            	    pushFollow(FOLLOW_valmod_in_entity_result2072);
                            	    m=valmod();

                            	    state._fsp--;
                            	    if (state.failed) return retval;
                            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, m.getTree());
                            	    if ( state.backtracking==0 ) {
                            	      retval.ent.addAttMod((name!=null?name.getText():null),(m!=null?m.val:null));
                            	    }

                            	    }
                            	    break;

                            	default :
                            	    break loop58;
                                }
                            } while (true);


                            }
                            break;

                    }

                    R_PAREN154=(Token)match(input,R_PAREN,FOLLOW_R_PAREN_in_entity_result2084); if (state.failed) return retval;

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              if (!ignore) parseTool.checkEntityDefPlausibility((species151!=null?species151.specName:null),retval.ent.getAttributes());
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:507:3: (ba= bindingactions )?
            int alt61=2;
            int LA61_0 = input.LA(1);

            if ( (LA61_0==LESSTHAN) ) {
                alt61=1;
            }
            switch (alt61) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:507:4: ba= bindingactions
                    {
                    pushFollow(FOLLOW_bindingactions_in_entity_result2099);
                    ba=bindingactions();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ba.getTree());
                    if ( state.backtracking==0 ) {
                      retval.ent.addBindMods((ba!=null?ba.map:null));
                    }

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "entity_result"

    public static class valmod_return extends ParserRuleReturnScope {
        public ValueModifier val;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "valmod"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:510:1: valmod returns [ValueModifier val] : ( | ( PLUS PLUS ) | ( MINUS MINUS ) | ( op EQ nn= varexpr ) | ( EQ n= varexpr ) | ( EQ STRING ) | ( COLON STRING ) | ( COLON v= valset_or_const ) | ( EQ {...}? => ID L_PAREN l= varexpr R_PAREN ) );
    public final MLSpaceSmallParser.valmod_return valmod() throws RecognitionException {
        MLSpaceSmallParser.valmod_return retval = new MLSpaceSmallParser.valmod_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token PLUS155=null;
        Token PLUS156=null;
        Token MINUS157=null;
        Token MINUS158=null;
        Token EQ160=null;
        Token EQ161=null;
        Token EQ162=null;
        Token STRING163=null;
        Token COLON164=null;
        Token STRING165=null;
        Token COLON166=null;
        Token EQ167=null;
        Token ID168=null;
        Token L_PAREN169=null;
        Token R_PAREN170=null;
        MLSpaceSmallParser.varexpr_return nn = null;

        MLSpaceSmallParser.varexpr_return n = null;

        MLSpaceSmallParser.valset_or_const_return v = null;

        MLSpaceSmallParser.varexpr_return l = null;

        MLSpaceSmallParser.op_return op159 = null;


        Object PLUS155_tree=null;
        Object PLUS156_tree=null;
        Object MINUS157_tree=null;
        Object MINUS158_tree=null;
        Object EQ160_tree=null;
        Object EQ161_tree=null;
        Object EQ162_tree=null;
        Object STRING163_tree=null;
        Object COLON164_tree=null;
        Object STRING165_tree=null;
        Object COLON166_tree=null;
        Object EQ167_tree=null;
        Object ID168_tree=null;
        Object L_PAREN169_tree=null;
        Object R_PAREN170_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:510:35: ( | ( PLUS PLUS ) | ( MINUS MINUS ) | ( op EQ nn= varexpr ) | ( EQ n= varexpr ) | ( EQ STRING ) | ( COLON STRING ) | ( COLON v= valset_or_const ) | ( EQ {...}? => ID L_PAREN l= varexpr R_PAREN ) )
            int alt62=9;
            alt62 = dfa62.predict(input);
            switch (alt62) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:511:3: 
                    {
                    root_0 = (Object)adaptor.nil();

                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:511:4: ( PLUS PLUS )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:511:4: ( PLUS PLUS )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:511:5: PLUS PLUS
                    {
                    PLUS155=(Token)match(input,PLUS,FOLLOW_PLUS_in_valmod2119); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    PLUS155_tree = (Object)adaptor.create(PLUS155);
                    adaptor.addChild(root_0, PLUS155_tree);
                    }
                    PLUS156=(Token)match(input,PLUS,FOLLOW_PLUS_in_valmod2121); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    PLUS156_tree = (Object)adaptor.create(PLUS156);
                    adaptor.addChild(root_0, PLUS156_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.val = new ValueModifier.SimpleValueModifier("+",1.);
                    }

                    }


                    }
                    break;
                case 3 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:512:4: ( MINUS MINUS )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:512:4: ( MINUS MINUS )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:512:5: MINUS MINUS
                    {
                    MINUS157=(Token)match(input,MINUS,FOLLOW_MINUS_in_valmod2131); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    MINUS157_tree = (Object)adaptor.create(MINUS157);
                    adaptor.addChild(root_0, MINUS157_tree);
                    }
                    MINUS158=(Token)match(input,MINUS,FOLLOW_MINUS_in_valmod2133); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    MINUS158_tree = (Object)adaptor.create(MINUS158);
                    adaptor.addChild(root_0, MINUS158_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.val = new ValueModifier.SimpleValueModifier("-",1.);
                    }

                    }


                    }
                    break;
                case 4 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:513:4: ( op EQ nn= varexpr )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:513:4: ( op EQ nn= varexpr )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:513:5: op EQ nn= varexpr
                    {
                    pushFollow(FOLLOW_op_in_valmod2143);
                    op159=op();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, op159.getTree());
                    EQ160=(Token)match(input,EQ,FOLLOW_EQ_in_valmod2145); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQ160_tree = (Object)adaptor.create(EQ160);
                    adaptor.addChild(root_0, EQ160_tree);
                    }
                    pushFollow(FOLLOW_varexpr_in_valmod2149);
                    nn=varexpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, nn.getTree());
                    if ( state.backtracking==0 ) {
                      retval.val = new ValueModifier.TreeValueModifier((op159!=null?input.toString(op159.start,op159.stop):null),(nn!=null?nn.node:null));
                    }

                    }


                    }
                    break;
                case 5 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:514:4: ( EQ n= varexpr )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:514:4: ( EQ n= varexpr )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:514:5: EQ n= varexpr
                    {
                    EQ161=(Token)match(input,EQ,FOLLOW_EQ_in_valmod2158); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQ161_tree = (Object)adaptor.create(EQ161);
                    adaptor.addChild(root_0, EQ161_tree);
                    }
                    pushFollow(FOLLOW_varexpr_in_valmod2162);
                    n=varexpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, n.getTree());
                    if ( state.backtracking==0 ) {
                      retval.val =new TreeValueModifier((n!=null?n.node:null));
                    }

                    }


                    }
                    break;
                case 6 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:515:4: ( EQ STRING )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:515:4: ( EQ STRING )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:515:5: EQ STRING
                    {
                    EQ162=(Token)match(input,EQ,FOLLOW_EQ_in_valmod2171); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQ162_tree = (Object)adaptor.create(EQ162);
                    adaptor.addChild(root_0, EQ162_tree);
                    }
                    STRING163=(Token)match(input,STRING,FOLLOW_STRING_in_valmod2173); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    STRING163_tree = (Object)adaptor.create(STRING163);
                    adaptor.addChild(root_0, STRING163_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.val =new StringAssignmentModifier(STRING163.getText());
                    }

                    }


                    }
                    break;
                case 7 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:516:4: ( COLON STRING )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:516:4: ( COLON STRING )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:516:5: COLON STRING
                    {
                    COLON164=(Token)match(input,COLON,FOLLOW_COLON_in_valmod2182); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    COLON164_tree = (Object)adaptor.create(COLON164);
                    adaptor.addChild(root_0, COLON164_tree);
                    }
                    STRING165=(Token)match(input,STRING,FOLLOW_STRING_in_valmod2184); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    STRING165_tree = (Object)adaptor.create(STRING165);
                    adaptor.addChild(root_0, STRING165_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.val =new StringAssignmentModifier(STRING165.getText());
                    }

                    }


                    }
                    break;
                case 8 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:517:4: ( COLON v= valset_or_const )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:517:4: ( COLON v= valset_or_const )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:517:5: COLON v= valset_or_const
                    {
                    COLON166=(Token)match(input,COLON,FOLLOW_COLON_in_valmod2193); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    COLON166_tree = (Object)adaptor.create(COLON166);
                    adaptor.addChild(root_0, COLON166_tree);
                    }
                    pushFollow(FOLLOW_valset_or_const_in_valmod2197);
                    v=valset_or_const();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, v.getTree());
                    if ( state.backtracking==0 ) {
                      retval.val =new RangeValueModifier((v!=null?v.val:null));
                    }

                    }


                    }
                    break;
                case 9 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:518:4: ( EQ {...}? => ID L_PAREN l= varexpr R_PAREN )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:518:4: ( EQ {...}? => ID L_PAREN l= varexpr R_PAREN )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:518:5: EQ {...}? => ID L_PAREN l= varexpr R_PAREN
                    {
                    EQ167=(Token)match(input,EQ,FOLLOW_EQ_in_valmod2206); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQ167_tree = (Object)adaptor.create(EQ167);
                    adaptor.addChild(root_0, EQ167_tree);
                    }
                    if ( !((RANDOM_VECTOR_KEYWORDS.contains(input.LT(1).getText()))) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "valmod", "RANDOM_VECTOR_KEYWORDS.contains(input.LT(1).getText())");
                    }
                    ID168=(Token)match(input,ID,FOLLOW_ID_in_valmod2212); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ID168_tree = (Object)adaptor.create(ID168);
                    adaptor.addChild(root_0, ID168_tree);
                    }
                    L_PAREN169=(Token)match(input,L_PAREN,FOLLOW_L_PAREN_in_valmod2214); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    L_PAREN169_tree = (Object)adaptor.create(L_PAREN169);
                    adaptor.addChild(root_0, L_PAREN169_tree);
                    }
                    pushFollow(FOLLOW_varexpr_in_valmod2218);
                    l=varexpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, l.getTree());
                    R_PAREN170=(Token)match(input,R_PAREN,FOLLOW_R_PAREN_in_valmod2220); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    R_PAREN170_tree = (Object)adaptor.create(R_PAREN170);
                    adaptor.addChild(root_0, R_PAREN170_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.val =new RandomVectorModifier((l!=null?l.node:null));
                    }

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "valmod"

    public static class op_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "op"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:520:1: op : ( PLUS | MINUS | TIMES | DIV );
    public final MLSpaceSmallParser.op_return op() throws RecognitionException {
        MLSpaceSmallParser.op_return retval = new MLSpaceSmallParser.op_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set171=null;

        Object set171_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:520:3: ( PLUS | MINUS | TIMES | DIV )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:
            {
            root_0 = (Object)adaptor.nil();

            set171=(Token)input.LT(1);
            if ( (input.LA(1)>=PLUS && input.LA(1)<=DIV) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set171));
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "op"

    public static class bindingactions_return extends ParserRuleReturnScope {
        public Map<String,BindingAction> map;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "bindingactions"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:522:1: bindingactions returns [Map<String,BindingAction> map] : LESSTHAN bm= bindingaction ( COMMA bm2= bindingaction )* GREATERTHAN ;
    public final MLSpaceSmallParser.bindingactions_return bindingactions() throws RecognitionException {
        MLSpaceSmallParser.bindingactions_return retval = new MLSpaceSmallParser.bindingactions_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LESSTHAN172=null;
        Token COMMA173=null;
        Token GREATERTHAN174=null;
        MLSpaceSmallParser.bindingaction_return bm = null;

        MLSpaceSmallParser.bindingaction_return bm2 = null;


        Object LESSTHAN172_tree=null;
        Object COMMA173_tree=null;
        Object GREATERTHAN174_tree=null;

        retval.map = new NonNullMap<String,BindingAction>();
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:523:55: ( LESSTHAN bm= bindingaction ( COMMA bm2= bindingaction )* GREATERTHAN )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:524:2: LESSTHAN bm= bindingaction ( COMMA bm2= bindingaction )* GREATERTHAN
            {
            root_0 = (Object)adaptor.nil();

            LESSTHAN172=(Token)match(input,LESSTHAN,FOLLOW_LESSTHAN_in_bindingactions2259); if (state.failed) return retval;
            pushFollow(FOLLOW_bindingaction_in_bindingactions2266);
            bm=bindingaction();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, bm.getTree());
            if ( state.backtracking==0 ) {
              retval.map.put((bm!=null?bm.name:null),(bm!=null?bm.action:null));
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:526:2: ( COMMA bm2= bindingaction )*
            loop63:
            do {
                int alt63=2;
                int LA63_0 = input.LA(1);

                if ( (LA63_0==COMMA) ) {
                    alt63=1;
                }


                switch (alt63) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:526:3: COMMA bm2= bindingaction
            	    {
            	    COMMA173=(Token)match(input,COMMA,FOLLOW_COMMA_in_bindingactions2272); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    COMMA173_tree = (Object)adaptor.create(COMMA173);
            	    adaptor.addChild(root_0, COMMA173_tree);
            	    }
            	    pushFollow(FOLLOW_bindingaction_in_bindingactions2276);
            	    bm2=bindingaction();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, bm2.getTree());
            	    if ( state.backtracking==0 ) {
            	      retval.map.put((bm2!=null?bm2.name:null),(bm2!=null?bm2.action:null));
            	    }

            	    }
            	    break;

            	default :
            	    break loop63;
                }
            } while (true);

            GREATERTHAN174=(Token)match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_bindingactions2283); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "bindingactions"

    public static class bindingaction_return extends ParserRuleReturnScope {
        public String name;
        public BindingAction action;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "bindingaction"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:529:1: bindingaction returns [String name,BindingAction action] : ID COLON ( BIND | RELEASE | REPLACE ) ;
    public final MLSpaceSmallParser.bindingaction_return bindingaction() throws RecognitionException {
        MLSpaceSmallParser.bindingaction_return retval = new MLSpaceSmallParser.bindingaction_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID175=null;
        Token COLON176=null;
        Token BIND177=null;
        Token RELEASE178=null;
        Token REPLACE179=null;

        Object ID175_tree=null;
        Object COLON176_tree=null;
        Object BIND177_tree=null;
        Object RELEASE178_tree=null;
        Object REPLACE179_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:529:57: ( ID COLON ( BIND | RELEASE | REPLACE ) )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:530:3: ID COLON ( BIND | RELEASE | REPLACE )
            {
            root_0 = (Object)adaptor.nil();

            ID175=(Token)match(input,ID,FOLLOW_ID_in_bindingaction2298); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID175_tree = (Object)adaptor.create(ID175);
            adaptor.addChild(root_0, ID175_tree);
            }
            COLON176=(Token)match(input,COLON,FOLLOW_COLON_in_bindingaction2300); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
              retval.name = (ID175!=null?ID175.getText():null);
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:531:3: ( BIND | RELEASE | REPLACE )
            int alt64=3;
            switch ( input.LA(1) ) {
            case BIND:
                {
                alt64=1;
                }
                break;
            case RELEASE:
                {
                alt64=2;
                }
                break;
            case REPLACE:
                {
                alt64=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 64, 0, input);

                throw nvae;
            }

            switch (alt64) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:531:5: BIND
                    {
                    BIND177=(Token)match(input,BIND,FOLLOW_BIND_in_bindingaction2309); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    BIND177_tree = (Object)adaptor.create(BIND177);
                    adaptor.addChild(root_0, BIND177_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.action = BindingAction.BIND;
                    }

                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:532:5: RELEASE
                    {
                    RELEASE178=(Token)match(input,RELEASE,FOLLOW_RELEASE_in_bindingaction2318); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RELEASE178_tree = (Object)adaptor.create(RELEASE178);
                    adaptor.addChild(root_0, RELEASE178_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.action = BindingAction.RELEASE;
                    }

                    }
                    break;
                case 3 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:533:5: REPLACE
                    {
                    REPLACE179=(Token)match(input,REPLACE,FOLLOW_REPLACE_in_bindingaction2326); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    REPLACE179_tree = (Object)adaptor.create(REPLACE179);
                    adaptor.addChild(root_0, REPLACE179_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.action = BindingAction.REPLACE;
                    }

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "bindingaction"

    public static class init_return extends ParserRuleReturnScope {
        public Map<InitEntity,Integer> map;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "init"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:537:1: init[boolean ignore] returns [Map<InitEntity,Integer> map] : i1= init_element[ignore] ( entsepleft in= init_element[ignore] )* ;
    public final MLSpaceSmallParser.init_return init(boolean ignore) throws RecognitionException {
        MLSpaceSmallParser.init_return retval = new MLSpaceSmallParser.init_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        MLSpaceSmallParser.init_element_return i1 = null;

        MLSpaceSmallParser.init_element_return in = null;

        MLSpaceSmallParser.entsepleft_return entsepleft180 = null;



        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:537:59: (i1= init_element[ignore] ( entsepleft in= init_element[ignore] )* )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:538:1: i1= init_element[ignore] ( entsepleft in= init_element[ignore] )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_init_element_in_init2349);
            i1=init_element(ignore);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, i1.getTree());
            if ( state.backtracking==0 ) {
              retval.map = (i1!=null?i1.map:null);
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:539:1: ( entsepleft in= init_element[ignore] )*
            loop65:
            do {
                int alt65=2;
                int LA65_0 = input.LA(1);

                if ( (LA65_0==PLUS) ) {
                    alt65=1;
                }


                switch (alt65) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:539:2: entsepleft in= init_element[ignore]
            	    {
            	    pushFollow(FOLLOW_entsepleft_in_init2355);
            	    entsepleft180=entsepleft();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    pushFollow(FOLLOW_init_element_in_init2360);
            	    in=init_element(ignore);

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, in.getTree());
            	    if ( state.backtracking==0 ) {
            	      retval.map.putAll((in!=null?in.map:null));
            	    }

            	    }
            	    break;

            	default :
            	    break loop65;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "init"

    public static class init_element_return extends ParserRuleReturnScope {
        public Map<InitEntity,Integer> map;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "init_element"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:541:1: init_element[boolean ignore] returns [Map<InitEntity,Integer> map] : ( ( for_each[ignore] ) | ( intval_or_var ( (e= entity_result[ignore] ) | ( L_BRACKET eba= entities_result[ignore] R_BRACKET ) ) ( L_BRACKET init[ignore] R_BRACKET )? ( SEMIC )? ) );
    public final MLSpaceSmallParser.init_element_return init_element(boolean ignore) throws RecognitionException {
        MLSpaceSmallParser.init_element_return retval = new MLSpaceSmallParser.init_element_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token L_BRACKET183=null;
        Token R_BRACKET184=null;
        Token L_BRACKET185=null;
        Token R_BRACKET187=null;
        Token SEMIC188=null;
        MLSpaceSmallParser.entity_result_return e = null;

        MLSpaceSmallParser.entities_result_return eba = null;

        MLSpaceSmallParser.for_each_return for_each181 = null;

        MLSpaceSmallParser.intval_or_var_return intval_or_var182 = null;

        MLSpaceSmallParser.init_return init186 = null;


        Object L_BRACKET183_tree=null;
        Object R_BRACKET184_tree=null;
        Object L_BRACKET185_tree=null;
        Object R_BRACKET187_tree=null;
        Object SEMIC188_tree=null;


          retval.map = new NonNullMap<InitEntity,Integer>(); 
          InitEntity tmpEnt = null; 

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:545:2: ( ( for_each[ignore] ) | ( intval_or_var ( (e= entity_result[ignore] ) | ( L_BRACKET eba= entities_result[ignore] R_BRACKET ) ) ( L_BRACKET init[ignore] R_BRACKET )? ( SEMIC )? ) )
            int alt69=2;
            int LA69_0 = input.LA(1);

            if ( (LA69_0==FOR) ) {
                alt69=1;
            }
            else if ( (LA69_0==L_BRACKET||LA69_0==L_PAREN||(LA69_0>=FLOAT && LA69_0<=MINUS)||(LA69_0>=MIN && LA69_0<=IF)||LA69_0==ID||LA69_0==INT) ) {
                alt69=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 69, 0, input);

                throw nvae;
            }
            switch (alt69) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:546:2: ( for_each[ignore] )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:546:2: ( for_each[ignore] )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:546:3: for_each[ignore]
                    {
                    pushFollow(FOLLOW_for_each_in_init_element2384);
                    for_each181=for_each(ignore);

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, for_each181.getTree());
                    if ( state.backtracking==0 ) {
                      retval.map = (for_each181!=null?for_each181.map:null);
                    }

                    }


                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:547:3: ( intval_or_var ( (e= entity_result[ignore] ) | ( L_BRACKET eba= entities_result[ignore] R_BRACKET ) ) ( L_BRACKET init[ignore] R_BRACKET )? ( SEMIC )? )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:547:3: ( intval_or_var ( (e= entity_result[ignore] ) | ( L_BRACKET eba= entities_result[ignore] R_BRACKET ) ) ( L_BRACKET init[ignore] R_BRACKET )? ( SEMIC )? )
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:547:4: intval_or_var ( (e= entity_result[ignore] ) | ( L_BRACKET eba= entities_result[ignore] R_BRACKET ) ) ( L_BRACKET init[ignore] R_BRACKET )? ( SEMIC )?
                    {
                    pushFollow(FOLLOW_intval_or_var_in_init_element2393);
                    intval_or_var182=intval_or_var();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, intval_or_var182.getTree());
                    if ( state.backtracking==0 ) {
                      ignore = ignore || (intval_or_var182!=null?intval_or_var182.val:0) <= 0;
                    }
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:548:5: ( (e= entity_result[ignore] ) | ( L_BRACKET eba= entities_result[ignore] R_BRACKET ) )
                    int alt66=2;
                    int LA66_0 = input.LA(1);

                    if ( (LA66_0==ID) ) {
                        alt66=1;
                    }
                    else if ( (LA66_0==L_BRACKET) ) {
                        alt66=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 66, 0, input);

                        throw nvae;
                    }
                    switch (alt66) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:548:6: (e= entity_result[ignore] )
                            {
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:548:6: (e= entity_result[ignore] )
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:548:7: e= entity_result[ignore]
                            {
                            pushFollow(FOLLOW_entity_result_in_init_element2407);
                            e=entity_result(ignore);

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, e.getTree());
                            if ( state.backtracking==0 ) {
                              tmpEnt = MLSpaceParserHelper.modToInitEntity((e!=null?e.ent:null));
                            }

                            }


                            }
                            break;
                        case 2 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:550:6: ( L_BRACKET eba= entities_result[ignore] R_BRACKET )
                            {
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:550:6: ( L_BRACKET eba= entities_result[ignore] R_BRACKET )
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:550:7: L_BRACKET eba= entities_result[ignore] R_BRACKET
                            {
                            L_BRACKET183=(Token)match(input,L_BRACKET,FOLLOW_L_BRACKET_in_init_element2426); if (state.failed) return retval;
                            pushFollow(FOLLOW_entities_result_in_init_element2431);
                            eba=entities_result(ignore);

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, eba.getTree());
                            R_BRACKET184=(Token)match(input,R_BRACKET,FOLLOW_R_BRACKET_in_init_element2434); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                              tmpEnt = new InitEntityWithBindings(MLSpaceParserHelper.modToInitEntities((eba!=null?eba.list:null)));
                            }

                            }


                            }
                            break;

                    }

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:552:6: ( L_BRACKET init[ignore] R_BRACKET )?
                    int alt67=2;
                    int LA67_0 = input.LA(1);

                    if ( (LA67_0==L_BRACKET) ) {
                        alt67=1;
                    }
                    switch (alt67) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:552:7: L_BRACKET init[ignore] R_BRACKET
                            {
                            L_BRACKET185=(Token)match(input,L_BRACKET,FOLLOW_L_BRACKET_in_init_element2452); if (state.failed) return retval;
                            pushFollow(FOLLOW_init_in_init_element2455);
                            init186=init(ignore);

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, init186.getTree());
                            R_BRACKET187=(Token)match(input,R_BRACKET,FOLLOW_R_BRACKET_in_init_element2458); if (state.failed) return retval;

                            }
                            break;

                    }

                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:552:50: ( SEMIC )?
                    int alt68=2;
                    int LA68_0 = input.LA(1);

                    if ( (LA68_0==SEMIC) ) {
                        int LA68_1 = input.LA(2);

                        if ( (LA68_1==EOF||LA68_1==R_BRACKET||LA68_1==R_BRACE||LA68_1==SEMIC||LA68_1==PLUS) ) {
                            alt68=1;
                        }
                    }
                    switch (alt68) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:0:0: SEMIC
                            {
                            SEMIC188=(Token)match(input,SEMIC,FOLLOW_SEMIC_in_init_element2464); if (state.failed) return retval;

                            }
                            break;

                    }

                    if ( state.backtracking==0 ) {
                      if ((init186!=null?init186.map:null) != null)
                            tmpEnt.updateSubEntities((init186!=null?init186.map:null));
                           if (!ignore)
                             retval.map.put(tmpEnt, (intval_or_var182!=null?intval_or_var182.val:0));
                          
                    }

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "init_element"

    public static class for_each_return extends ParserRuleReturnScope {
        public Map<InitEntity,Integer> map;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "for_each"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:559:1: for_each[boolean ignore] returns [Map<InitEntity,Integer> map] : ( FOR for_var L_BRACE init[ignore] R_BRACE )+ ;
    public final MLSpaceSmallParser.for_each_return for_each(boolean ignore) throws RecognitionException {
        MLSpaceSmallParser.for_each_return retval = new MLSpaceSmallParser.for_each_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token FOR189=null;
        Token L_BRACE191=null;
        Token R_BRACE193=null;
        MLSpaceSmallParser.for_var_return for_var190 = null;

        MLSpaceSmallParser.init_return init192 = null;


        Object FOR189_tree=null;
        Object L_BRACE191_tree=null;
        Object R_BRACE193_tree=null;

        int mark = -1;
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:560:22: ( ( FOR for_var L_BRACE init[ignore] R_BRACE )+ )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:561:1: ( FOR for_var L_BRACE init[ignore] R_BRACE )+
            {
            root_0 = (Object)adaptor.nil();

            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:561:1: ( FOR for_var L_BRACE init[ignore] R_BRACE )+
            int cnt70=0;
            loop70:
            do {
                int alt70=2;
                int LA70_0 = input.LA(1);

                if ( (LA70_0==FOR) ) {
                    alt70=1;
                }


                switch (alt70) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:561:3: FOR for_var L_BRACE init[ignore] R_BRACE
            	    {
            	    if ( state.backtracking==0 ) {
            	      mark = input.mark();
            	    }
            	    FOR189=(Token)match(input,FOR,FOLLOW_FOR_in_for_each2494); if (state.failed) return retval;
            	    pushFollow(FOLLOW_for_var_in_for_each2497);
            	    for_var190=for_var();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, for_var190.getTree());
            	    if ( state.backtracking==0 ) {
            	      ignore = ignore | handleForVar((for_var190!=null?for_var190.name:null),(for_var190!=null?for_var190.range:null));
            	    }
            	    L_BRACE191=(Token)match(input,L_BRACE,FOLLOW_L_BRACE_in_for_each2502); if (state.failed) return retval;
            	    pushFollow(FOLLOW_init_in_for_each2505);
            	    init192=init(ignore);

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, init192.getTree());
            	    if ( state.backtracking==0 ) {
            	      if (retval.map == null) retval.map = (init192!=null?init192.map:null); else 
            	       for (Map.Entry<InitEntity,Integer> e: (init192!=null?init192.map:null).entrySet()) {
            	         retval.map.put(e.getKey(), e.getValue() + (retval.map.containsKey(e.getKey()) ? retval.map.get(e.getKey()) : 0)); 
            	       }
            	    }
            	    R_BRACE193=(Token)match(input,R_BRACE,FOLLOW_R_BRACE_in_for_each2511); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	      if (ignore) removeLastLoopVar((for_var190!=null?for_var190.name:null)); else if (!wasLastLoop((for_var190!=null?for_var190.name:null),(for_var190!=null?for_var190.range:null))) input.rewind(mark);
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt70 >= 1 ) break loop70;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(70, input);
                        throw eee;
                }
                cnt70++;
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "for_each"

    public static class for_var_return extends ParserRuleReturnScope {
        public String name;
        public List<?> range;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "for_var"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:572:1: for_var returns [String name, List<?> range] : ID EQ (r= range | set ) ;
    public final MLSpaceSmallParser.for_var_return for_var() throws RecognitionException {
        MLSpaceSmallParser.for_var_return retval = new MLSpaceSmallParser.for_var_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID194=null;
        Token EQ195=null;
        MLSpaceSmallParser.range_return r = null;

        MLSpaceSmallParser.set_return set196 = null;


        Object ID194_tree=null;
        Object EQ195_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:572:45: ( ID EQ (r= range | set ) )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:573:1: ID EQ (r= range | set )
            {
            root_0 = (Object)adaptor.nil();

            ID194=(Token)match(input,ID,FOLLOW_ID_in_for_var2530); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID194_tree = (Object)adaptor.create(ID194);
            adaptor.addChild(root_0, ID194_tree);
            }
            if ( state.backtracking==0 ) {
              retval.name =(ID194!=null?ID194.getText():null);
            }
            EQ195=(Token)match(input,EQ,FOLLOW_EQ_in_for_var2534); if (state.failed) return retval;
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:574:1: (r= range | set )
            int alt71=2;
            int LA71_0 = input.LA(1);

            if ( (LA71_0==L_BRACKET||LA71_0==L_PAREN||(LA71_0>=FLOAT && LA71_0<=MINUS)||(LA71_0>=MIN && LA71_0<=IF)||LA71_0==ID||LA71_0==INT) ) {
                alt71=1;
            }
            else if ( (LA71_0==L_BRACE) ) {
                alt71=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 71, 0, input);

                throw nvae;
            }
            switch (alt71) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:574:2: r= range
                    {
                    pushFollow(FOLLOW_range_in_for_var2541);
                    r=range();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, r.getTree());
                    if ( state.backtracking==0 ) {
                      retval.range = AbstractValueRange.newRange((r!=null?r.lower:0.0),(r!=null?r.step:0.0),(r!=null?r.upper:0.0)).toList();
                    }

                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:575:2: set
                    {
                    pushFollow(FOLLOW_set_in_for_var2548);
                    set196=set();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, set196.getTree());
                    if ( state.backtracking==0 ) {
                      retval.range = new ArrayList<Object>((set196!=null?set196.set:null));
                    }

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "for_var"

    public static class observationTargets_return extends ParserRuleReturnScope {
        public Map<List<? extends RuleEntity>,List<String>> obs;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "observationTargets"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:579:1: observationTargets returns [Map<List<? extends RuleEntity>,List<String>> obs] : oTE1= obsTargetEntry ( SEMIC oTE2= obsTargetEntry )* ;
    public final MLSpaceSmallParser.observationTargets_return observationTargets() throws RecognitionException {
        MLSpaceSmallParser.observationTargets_return retval = new MLSpaceSmallParser.observationTargets_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMIC197=null;
        MLSpaceSmallParser.obsTargetEntry_return oTE1 = null;

        MLSpaceSmallParser.obsTargetEntry_return oTE2 = null;


        Object SEMIC197_tree=null;

        retval.obs = new ArrayMap<List<? extends RuleEntity>,List<String>>();
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:580:72: (oTE1= obsTargetEntry ( SEMIC oTE2= obsTargetEntry )* )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:581:3: oTE1= obsTargetEntry ( SEMIC oTE2= obsTargetEntry )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_obsTargetEntry_in_observationTargets2577);
            oTE1=obsTargetEntry();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, oTE1.getTree());
            if ( state.backtracking==0 ) {
               for (List<? extends RuleEntity> lst: (oTE1!=null?oTE1.oe:null)) retval.obs.put(lst,(oTE1!=null?oTE1.what:null));
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:582:3: ( SEMIC oTE2= obsTargetEntry )*
            loop72:
            do {
                int alt72=2;
                int LA72_0 = input.LA(1);

                if ( (LA72_0==SEMIC) ) {
                    alt72=1;
                }


                switch (alt72) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:582:4: SEMIC oTE2= obsTargetEntry
            	    {
            	    SEMIC197=(Token)match(input,SEMIC,FOLLOW_SEMIC_in_observationTargets2584); if (state.failed) return retval;
            	    pushFollow(FOLLOW_obsTargetEntry_in_observationTargets2589);
            	    oTE2=obsTargetEntry();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, oTE2.getTree());
            	    if ( state.backtracking==0 ) {
            	       for (List<? extends RuleEntity> lst: (oTE2!=null?oTE2.oe:null)) retval.obs.put(lst,(oTE2!=null?oTE2.what:null));
            	    }

            	    }
            	    break;

            	default :
            	    break loop72;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "observationTargets"

    public static class obsTargetEntry_return extends ParserRuleReturnScope {
        public List<? extends List<? extends RuleEntity>> oe;
        public List<String> what;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "obsTargetEntry"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:585:1: obsTargetEntry returns [List<? extends List<? extends RuleEntity>> oe, List<String> what] : e1= obs_matches ( IN e2= obs_matches )* ( EQ idlist )? ;
    public final MLSpaceSmallParser.obsTargetEntry_return obsTargetEntry() throws RecognitionException {
        MLSpaceSmallParser.obsTargetEntry_return retval = new MLSpaceSmallParser.obsTargetEntry_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token IN198=null;
        Token EQ199=null;
        MLSpaceSmallParser.obs_matches_return e1 = null;

        MLSpaceSmallParser.obs_matches_return e2 = null;

        MLSpaceSmallParser.idlist_return idlist200 = null;


        Object IN198_tree=null;
        Object EQ199_tree=null;

        List<List<? extends RuleEntity>> tmp = new ArrayList<List<? extends RuleEntity>>();retval.what = DEFAULT_OBS_ASPECT;
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:587:44: (e1= obs_matches ( IN e2= obs_matches )* ( EQ idlist )? )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:588:3: e1= obs_matches ( IN e2= obs_matches )* ( EQ idlist )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_obs_matches_in_obsTargetEntry2621);
            e1=obs_matches();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, e1.getTree());
            if ( state.backtracking==0 ) {
              tmp.add((e1!=null?e1.list:null));
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:589:3: ( IN e2= obs_matches )*
            loop73:
            do {
                int alt73=2;
                int LA73_0 = input.LA(1);

                if ( (LA73_0==IN) ) {
                    alt73=1;
                }


                switch (alt73) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:589:4: IN e2= obs_matches
            	    {
            	    IN198=(Token)match(input,IN,FOLLOW_IN_in_obsTargetEntry2629); if (state.failed) return retval;
            	    pushFollow(FOLLOW_obs_matches_in_obsTargetEntry2634);
            	    e2=obs_matches();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, e2.getTree());
            	    if ( state.backtracking==0 ) {
            	      tmp.add((e2!=null?e2.list:null));
            	    }

            	    }
            	    break;

            	default :
            	    break loop73;
                }
            } while (true);

            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:590:3: ( EQ idlist )?
            int alt74=2;
            int LA74_0 = input.LA(1);

            if ( (LA74_0==EQ) ) {
                alt74=1;
            }
            switch (alt74) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:590:4: EQ idlist
                    {
                    EQ199=(Token)match(input,EQ,FOLLOW_EQ_in_obsTargetEntry2643); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQ199_tree = (Object)adaptor.create(EQ199);
                    adaptor.addChild(root_0, EQ199_tree);
                    }
                    pushFollow(FOLLOW_idlist_in_obsTargetEntry2645);
                    idlist200=idlist();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, idlist200.getTree());
                    if ( state.backtracking==0 ) {
                      retval.what =(idlist200!=null?idlist200.list:null);
                    }

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) {
              retval.oe = ListUtils.combinations(tmp);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "obsTargetEntry"

    public static class obs_matches_return extends ParserRuleReturnScope {
        public List<RuleEntityWithBindings> list;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "obs_matches"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:593:1: obs_matches returns [List<RuleEntityWithBindings> list] : (e= entity_match ( COMMA e2= entity_match )* )? ;
    public final MLSpaceSmallParser.obs_matches_return obs_matches() throws RecognitionException {
        MLSpaceSmallParser.obs_matches_return retval = new MLSpaceSmallParser.obs_matches_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COMMA201=null;
        MLSpaceSmallParser.entity_match_return e = null;

        MLSpaceSmallParser.entity_match_return e2 = null;


        Object COMMA201_tree=null;

        retval.list = new ArrayList<RuleEntityWithBindings>();
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:594:58: ( (e= entity_match ( COMMA e2= entity_match )* )? )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:595:2: (e= entity_match ( COMMA e2= entity_match )* )?
            {
            root_0 = (Object)adaptor.nil();

            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:595:2: (e= entity_match ( COMMA e2= entity_match )* )?
            int alt76=2;
            int LA76_0 = input.LA(1);

            if ( (LA76_0==ID) ) {
                alt76=1;
            }
            switch (alt76) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:595:3: e= entity_match ( COMMA e2= entity_match )*
                    {
                    pushFollow(FOLLOW_entity_match_in_obs_matches2678);
                    e=entity_match();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, e.getTree());
                    if ( state.backtracking==0 ) {
                      retval.list.add((e!=null?e.ent:null));
                    }
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:596:3: ( COMMA e2= entity_match )*
                    loop75:
                    do {
                        int alt75=2;
                        int LA75_0 = input.LA(1);

                        if ( (LA75_0==COMMA) ) {
                            alt75=1;
                        }


                        switch (alt75) {
                    	case 1 :
                    	    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:596:4: COMMA e2= entity_match
                    	    {
                    	    COMMA201=(Token)match(input,COMMA,FOLLOW_COMMA_in_obs_matches2686); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    COMMA201_tree = (Object)adaptor.create(COMMA201);
                    	    adaptor.addChild(root_0, COMMA201_tree);
                    	    }
                    	    pushFollow(FOLLOW_entity_match_in_obs_matches2690);
                    	    e2=entity_match();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, e2.getTree());
                    	    if ( state.backtracking==0 ) {
                    	      retval.list.add((e2!=null?e2.ent:null));
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop75;
                        }
                    } while (true);


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "obs_matches"

    public static class idlist_return extends ParserRuleReturnScope {
        public List<String> list;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "idlist"
    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:599:2: idlist returns [List<String> list] : (i1= ID | HASH ) ( COMMA i2= ID | HASH )* ;
    public final MLSpaceSmallParser.idlist_return idlist() throws RecognitionException {
        MLSpaceSmallParser.idlist_return retval = new MLSpaceSmallParser.idlist_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token i1=null;
        Token i2=null;
        Token HASH202=null;
        Token COMMA203=null;
        Token HASH204=null;

        Object i1_tree=null;
        Object i2_tree=null;
        Object HASH202_tree=null;
        Object COMMA203_tree=null;
        Object HASH204_tree=null;

        retval.list = new ArrayList<String>();
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:600:42: ( (i1= ID | HASH ) ( COMMA i2= ID | HASH )* )
            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:601:2: (i1= ID | HASH ) ( COMMA i2= ID | HASH )*
            {
            root_0 = (Object)adaptor.nil();

            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:601:2: (i1= ID | HASH )
            int alt77=2;
            int LA77_0 = input.LA(1);

            if ( (LA77_0==ID) ) {
                alt77=1;
            }
            else if ( (LA77_0==HASH) ) {
                alt77=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 77, 0, input);

                throw nvae;
            }
            switch (alt77) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:601:3: i1= ID
                    {
                    i1=(Token)match(input,ID,FOLLOW_ID_in_idlist2720); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    i1_tree = (Object)adaptor.create(i1);
                    adaptor.addChild(root_0, i1_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.list.add((i1!=null?i1.getText():null));
                    }

                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:601:34: HASH
                    {
                    HASH202=(Token)match(input,HASH,FOLLOW_HASH_in_idlist2726); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    HASH202_tree = (Object)adaptor.create(HASH202);
                    adaptor.addChild(root_0, HASH202_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.list.add("#");
                    }

                    }
                    break;

            }

            // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:601:58: ( COMMA i2= ID | HASH )*
            loop78:
            do {
                int alt78=3;
                int LA78_0 = input.LA(1);

                if ( (LA78_0==COMMA) ) {
                    alt78=1;
                }
                else if ( (LA78_0==HASH) ) {
                    alt78=2;
                }


                switch (alt78) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:601:59: COMMA i2= ID
            	    {
            	    COMMA203=(Token)match(input,COMMA,FOLLOW_COMMA_in_idlist2732); if (state.failed) return retval;
            	    i2=(Token)match(input,ID,FOLLOW_ID_in_idlist2737); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    i2_tree = (Object)adaptor.create(i2);
            	    adaptor.addChild(root_0, i2_tree);
            	    }
            	    if ( state.backtracking==0 ) {
            	      retval.list.add((i2!=null?i2.getText():null));
            	    }

            	    }
            	    break;
            	case 2 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:601:97: HASH
            	    {
            	    HASH204=(Token)match(input,HASH,FOLLOW_HASH_in_idlist2743); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    HASH204_tree = (Object)adaptor.create(HASH204);
            	    adaptor.addChild(root_0, HASH204_tree);
            	    }
            	    if ( state.backtracking==0 ) {
            	      retval.list.add("#");
            	    }

            	    }
            	    break;

            	default :
            	    break loop78;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "idlist"

    // $ANTLR start synpred8_MLSpaceSmallParser
    public final void synpred8_MLSpaceSmallParser_fragment() throws RecognitionException {   
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:272:2: ( ( ( interval )=> interval ) )
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:272:2: ( ( interval )=> interval )
        {
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:272:2: ( ( interval )=> interval )
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:272:3: ( interval )=> interval
        {
        pushFollow(FOLLOW_interval_in_synpred8_MLSpaceSmallParser189);
        interval();

        state._fsp--;
        if (state.failed) return ;

        }


        }
    }
    // $ANTLR end synpred8_MLSpaceSmallParser

    // $ANTLR start synpred10_MLSpaceSmallParser
    public final void synpred10_MLSpaceSmallParser_fragment() throws RecognitionException {   
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:273:2: ( ( ( range )=> range ) )
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:273:2: ( ( range )=> range )
        {
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:273:2: ( ( range )=> range )
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:273:3: ( range )=> range
        {
        pushFollow(FOLLOW_range_in_synpred10_MLSpaceSmallParser201);
        range();

        state._fsp--;
        if (state.failed) return ;

        }


        }
    }
    // $ANTLR end synpred10_MLSpaceSmallParser

    // $ANTLR start synpred14_MLSpaceSmallParser
    public final void synpred14_MLSpaceSmallParser_fragment() throws RecognitionException {   
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:275:2: ( ( ( vector )=> vector ) )
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:275:2: ( ( vector )=> vector )
        {
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:275:2: ( ( vector )=> vector )
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:275:3: ( vector )=> vector
        {
        pushFollow(FOLLOW_vector_in_synpred14_MLSpaceSmallParser225);
        vector();

        state._fsp--;
        if (state.failed) return ;

        }


        }
    }
    // $ANTLR end synpred14_MLSpaceSmallParser

    // $ANTLR start synpred15_MLSpaceSmallParser
    public final void synpred15_MLSpaceSmallParser_fragment() throws RecognitionException {   
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:276:2: ( ( numexpr ) )
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:276:2: ( numexpr )
        {
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:276:2: ( numexpr )
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:276:3: numexpr
        {
        pushFollow(FOLLOW_numexpr_in_synpred15_MLSpaceSmallParser232);
        numexpr();

        state._fsp--;
        if (state.failed) return ;

        }


        }
    }
    // $ANTLR end synpred15_MLSpaceSmallParser

    // $ANTLR start synpred34_MLSpaceSmallParser
    public final void synpred34_MLSpaceSmallParser_fragment() throws RecognitionException {   
        MLSpaceSmallParser.expr_return ef = null;


        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:319:30: ( ELSE ef= expr )
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:319:30: ELSE ef= expr
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred34_MLSpaceSmallParser625); if (state.failed) return ;
        pushFollow(FOLLOW_expr_in_synpred34_MLSpaceSmallParser629);
        ef=expr();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred34_MLSpaceSmallParser

    // $ANTLR start synpred39_MLSpaceSmallParser
    public final void synpred39_MLSpaceSmallParser_fragment() throws RecognitionException {   
        MLSpaceSmallParser.numval_return n = null;


        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:330:5: (n= numval )
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:330:5: n= numval
        {
        pushFollow(FOLLOW_numval_in_synpred39_MLSpaceSmallParser739);
        n=numval();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred39_MLSpaceSmallParser

    // $ANTLR start synpred40_MLSpaceSmallParser
    public final void synpred40_MLSpaceSmallParser_fragment() throws RecognitionException {   
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:331:5: ({...}? ID )
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:331:5: {...}? ID
        {
        if ( !((varsAllowed)) ) {
            if (state.backtracking>0) {state.failed=true; return ;}
            throw new FailedPredicateException(input, "synpred40_MLSpaceSmallParser", "varsAllowed");
        }
        match(input,ID,FOLLOW_ID_in_synpred40_MLSpaceSmallParser749); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred40_MLSpaceSmallParser

    // $ANTLR start synpred41_MLSpaceSmallParser
    public final void synpred41_MLSpaceSmallParser_fragment() throws RecognitionException {   
        MLSpaceSmallParser.expr_return e = null;


        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:332:5: ( ( L_PAREN e= expr R_PAREN ) )
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:332:5: ( L_PAREN e= expr R_PAREN )
        {
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:332:5: ( L_PAREN e= expr R_PAREN )
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:332:6: L_PAREN e= expr R_PAREN
        {
        match(input,L_PAREN,FOLLOW_L_PAREN_in_synpred41_MLSpaceSmallParser758); if (state.failed) return ;
        pushFollow(FOLLOW_expr_in_synpred41_MLSpaceSmallParser762);
        e=expr();

        state._fsp--;
        if (state.failed) return ;
        match(input,R_PAREN,FOLLOW_R_PAREN_in_synpred41_MLSpaceSmallParser764); if (state.failed) return ;

        }


        }
    }
    // $ANTLR end synpred41_MLSpaceSmallParser

    // $ANTLR start synpred42_MLSpaceSmallParser
    public final void synpred42_MLSpaceSmallParser_fragment() throws RecognitionException {   
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:333:5: ( ( L_PAREN boolNode R_PAREN ) )
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:333:5: ( L_PAREN boolNode R_PAREN )
        {
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:333:5: ( L_PAREN boolNode R_PAREN )
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:333:6: L_PAREN boolNode R_PAREN
        {
        match(input,L_PAREN,FOLLOW_L_PAREN_in_synpred42_MLSpaceSmallParser774); if (state.failed) return ;
        pushFollow(FOLLOW_boolNode_in_synpred42_MLSpaceSmallParser776);
        boolNode();

        state._fsp--;
        if (state.failed) return ;
        match(input,R_PAREN,FOLLOW_R_PAREN_in_synpred42_MLSpaceSmallParser778); if (state.failed) return ;

        }


        }
    }
    // $ANTLR end synpred42_MLSpaceSmallParser

    // $ANTLR start synpred66_MLSpaceSmallParser
    public final void synpred66_MLSpaceSmallParser_fragment() throws RecognitionException {   
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:405:17: ( species_def ( SEMIC )? )
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:405:17: species_def ( SEMIC )?
        {
        pushFollow(FOLLOW_species_def_in_synpred66_MLSpaceSmallParser1350);
        species_def();

        state._fsp--;
        if (state.failed) return ;
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:405:29: ( SEMIC )?
        int alt86=2;
        int LA86_0 = input.LA(1);

        if ( (LA86_0==SEMIC) ) {
            alt86=1;
        }
        switch (alt86) {
            case 1 :
                // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:0:0: SEMIC
                {
                match(input,SEMIC,FOLLOW_SEMIC_in_synpred66_MLSpaceSmallParser1352); if (state.failed) return ;

                }
                break;

        }


        }
    }
    // $ANTLR end synpred66_MLSpaceSmallParser

    // $ANTLR start synpred71_MLSpaceSmallParser
    public final void synpred71_MLSpaceSmallParser_fragment() throws RecognitionException {   
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:428:4: ({...}? => ID )
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:428:4: {...}? => ID
        {
        if ( !((input.LT(1).getText().toLowerCase().equals("any"))) ) {
            if (state.backtracking>0) {state.failed=true; return ;}
            throw new FailedPredicateException(input, "synpred71_MLSpaceSmallParser", "input.LT(1).getText().toLowerCase().equals(\"any\")");
        }
        match(input,ID,FOLLOW_ID_in_synpred71_MLSpaceSmallParser1514); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred71_MLSpaceSmallParser

    // $ANTLR start synpred76_MLSpaceSmallParser
    public final void synpred76_MLSpaceSmallParser_fragment() throws RecognitionException {   
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:445:4: ( rpmark )
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:445:5: rpmark
        {
        pushFollow(FOLLOW_rpmark_in_synpred76_MLSpaceSmallParser1627);
        rpmark();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred76_MLSpaceSmallParser

    // $ANTLR start synpred106_MLSpaceSmallParser
    public final void synpred106_MLSpaceSmallParser_fragment() throws RecognitionException {   
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:516:4: ( ( COLON STRING ) )
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:516:4: ( COLON STRING )
        {
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:516:4: ( COLON STRING )
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:516:5: COLON STRING
        {
        match(input,COLON,FOLLOW_COLON_in_synpred106_MLSpaceSmallParser2182); if (state.failed) return ;
        match(input,STRING,FOLLOW_STRING_in_synpred106_MLSpaceSmallParser2184); if (state.failed) return ;

        }


        }
    }
    // $ANTLR end synpred106_MLSpaceSmallParser

    // $ANTLR start synpred107_MLSpaceSmallParser
    public final void synpred107_MLSpaceSmallParser_fragment() throws RecognitionException {   
        MLSpaceSmallParser.valset_or_const_return v = null;


        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:517:4: ( ( COLON v= valset_or_const ) )
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:517:4: ( COLON v= valset_or_const )
        {
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:517:4: ( COLON v= valset_or_const )
        // model\\mlspace\\reader\\antlr\\MLSpaceSmallParser.g:517:5: COLON v= valset_or_const
        {
        match(input,COLON,FOLLOW_COLON_in_synpred107_MLSpaceSmallParser2193); if (state.failed) return ;
        pushFollow(FOLLOW_valset_or_const_in_synpred107_MLSpaceSmallParser2197);
        v=valset_or_const();

        state._fsp--;
        if (state.failed) return ;

        }


        }
    }
    // $ANTLR end synpred107_MLSpaceSmallParser

    // Delegated rules

    public final boolean synpred71_MLSpaceSmallParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred71_MLSpaceSmallParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred107_MLSpaceSmallParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred107_MLSpaceSmallParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred39_MLSpaceSmallParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred39_MLSpaceSmallParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred34_MLSpaceSmallParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred34_MLSpaceSmallParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred40_MLSpaceSmallParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred40_MLSpaceSmallParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred41_MLSpaceSmallParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred41_MLSpaceSmallParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred8_MLSpaceSmallParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred8_MLSpaceSmallParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred10_MLSpaceSmallParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred10_MLSpaceSmallParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred76_MLSpaceSmallParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred76_MLSpaceSmallParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred42_MLSpaceSmallParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred42_MLSpaceSmallParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred14_MLSpaceSmallParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred14_MLSpaceSmallParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred106_MLSpaceSmallParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred106_MLSpaceSmallParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred15_MLSpaceSmallParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred15_MLSpaceSmallParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred66_MLSpaceSmallParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred66_MLSpaceSmallParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA2 dfa2 = new DFA2(this);
    protected DFA5 dfa5 = new DFA5(this);
    protected DFA11 dfa11 = new DFA11(this);
    protected DFA17 dfa17 = new DFA17(this);
    protected DFA30 dfa30 = new DFA30(this);
    protected DFA36 dfa36 = new DFA36(this);
    protected DFA62 dfa62 = new DFA62(this);
    static final String DFA2_eotS =
        "\45\uffff";
    static final String DFA2_eofS =
        "\10\uffff\1\1\34\uffff";
    static final String DFA2_minS =
        "\1\4\1\uffff\1\4\1\uffff\1\4\1\5\1\4\1\5\2\4\1\33\2\55\1\4\1\10"+
        "\1\5\1\10\1\5\1\4\1\24\1\33\1\55\1\12\1\10\1\5\1\10\1\33\1\4\1\24"+
        "\1\10\1\12\1\4\1\33\1\12\1\10\1\4\1\12";
    static final String DFA2_maxS =
        "\1\63\1\uffff\1\55\1\uffff\1\63\2\55\2\40\1\63\4\55\1\43\1\40\1"+
        "\10\1\40\1\63\3\55\1\54\1\43\1\40\1\10\1\55\1\63\1\55\1\43\1\54"+
        "\1\63\1\55\1\54\1\43\1\63\1\54";
    static final String DFA2_acceptS =
        "\1\uffff\1\1\1\uffff\1\2\41\uffff";
    static final String DFA2_specialS =
        "\45\uffff}>";
    static final String[] DFA2_transitionS = {
            "\1\1\16\uffff\1\1\7\uffff\1\1\3\uffff\3\1\5\uffff\3\1\3\uffff"+
            "\1\2\5\uffff\1\1",
            "",
            "\1\5\3\uffff\1\3\2\uffff\1\3\3\uffff\1\3\13\uffff\1\3\4\uffff"+
            "\1\4\6\1\5\uffff\2\1",
            "",
            "\1\1\1\3\5\uffff\1\3\17\uffff\1\1\3\uffff\3\1\5\uffff\2\1"+
            "\4\uffff\1\6\5\uffff\1\1",
            "\1\10\5\uffff\1\3\41\uffff\1\7",
            "\1\1\1\3\5\uffff\1\3\3\uffff\1\3\13\uffff\1\3\4\uffff\1\11"+
            "\6\1\5\uffff\2\1",
            "\1\10\5\uffff\1\3\3\uffff\1\13\2\uffff\1\1\10\uffff\1\12\4"+
            "\uffff\1\14",
            "\1\1\4\uffff\1\1\1\uffff\1\3\24\uffff\1\1",
            "\1\1\26\uffff\1\1\3\uffff\3\1\5\uffff\2\1\4\uffff\1\15\5\uffff"+
            "\1\1",
            "\1\3\1\17\20\uffff\1\16",
            "\1\20",
            "\1\21",
            "\1\1\1\3\5\uffff\1\3\3\uffff\1\3\13\uffff\1\3\4\uffff\1\11"+
            "\6\1\5\uffff\2\1",
            "\1\1\1\uffff\1\1\3\uffff\1\22\2\3\10\uffff\1\3\2\uffff\1\1"+
            "\3\uffff\4\1",
            "\1\10\5\uffff\1\3\3\uffff\1\13\2\uffff\1\1\15\uffff\1\14",
            "\1\23",
            "\1\10\5\uffff\1\3\3\uffff\1\25\2\uffff\1\1\10\uffff\1\24\4"+
            "\uffff\1\14",
            "\1\1\11\uffff\1\3\14\uffff\1\1\3\uffff\3\1\5\uffff\3\1\3\uffff"+
            "\1\26\2\uffff\1\1\2\uffff\1\1",
            "\2\3\3\1\24\uffff\1\3",
            "\1\3\1\30\20\uffff\1\27",
            "\1\31",
            "\1\32\3\uffff\3\3\10\uffff\1\3\1\uffff\1\1\1\17\3\uffff\7"+
            "\1\5\uffff\1\1",
            "\1\1\1\uffff\1\1\3\uffff\1\33\2\3\10\uffff\1\3\2\uffff\1\1"+
            "\3\uffff\4\1",
            "\1\10\5\uffff\1\3\3\uffff\1\25\2\uffff\1\1\15\uffff\1\14",
            "\1\34",
            "\1\3\21\uffff\1\35",
            "\1\1\11\uffff\1\3\14\uffff\1\1\3\uffff\3\1\5\uffff\3\1\3\uffff"+
            "\1\36\2\uffff\1\1\2\uffff\1\1",
            "\2\3\3\1\24\uffff\1\3",
            "\1\1\1\uffff\1\1\3\uffff\1\37\2\3\10\uffff\1\3\2\uffff\1\1"+
            "\3\uffff\4\1",
            "\1\40\3\uffff\3\3\10\uffff\1\3\1\uffff\1\1\1\30\3\uffff\7"+
            "\1\5\uffff\1\1",
            "\1\1\11\uffff\1\3\14\uffff\1\1\3\uffff\3\1\5\uffff\3\1\3\uffff"+
            "\1\41\2\uffff\1\1\2\uffff\1\1",
            "\1\3\21\uffff\1\42",
            "\1\32\3\uffff\3\3\10\uffff\1\3\1\uffff\1\1\1\17\3\uffff\7"+
            "\1\5\uffff\1\1",
            "\1\1\1\uffff\1\1\3\uffff\1\43\2\3\10\uffff\1\3\2\uffff\1\1"+
            "\3\uffff\4\1",
            "\1\1\11\uffff\1\3\14\uffff\1\1\3\uffff\3\1\5\uffff\3\1\3\uffff"+
            "\1\44\2\uffff\1\1\2\uffff\1\1",
            "\1\40\3\uffff\3\3\10\uffff\1\3\1\uffff\1\1\1\30\3\uffff\7"+
            "\1\5\uffff\1\1"
    };

    static final short[] DFA2_eot = DFA.unpackEncodedString(DFA2_eotS);
    static final short[] DFA2_eof = DFA.unpackEncodedString(DFA2_eofS);
    static final char[] DFA2_min = DFA.unpackEncodedStringToUnsignedChars(DFA2_minS);
    static final char[] DFA2_max = DFA.unpackEncodedStringToUnsignedChars(DFA2_maxS);
    static final short[] DFA2_accept = DFA.unpackEncodedString(DFA2_acceptS);
    static final short[] DFA2_special = DFA.unpackEncodedString(DFA2_specialS);
    static final short[][] DFA2_transition;

    static {
        int numStates = DFA2_transitionS.length;
        DFA2_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA2_transition[i] = DFA.unpackEncodedString(DFA2_transitionS[i]);
        }
    }

    class DFA2 extends DFA {

        public DFA2(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 2;
            this.eot = DFA2_eot;
            this.eof = DFA2_eof;
            this.min = DFA2_min;
            this.max = DFA2_max;
            this.accept = DFA2_accept;
            this.special = DFA2_special;
            this.transition = DFA2_transition;
        }
        public String getDescription() {
            return "244:2: ( ( ( init )=>i1= init[false] ( SEMIC r1= rules )? ) | (r2= rules SEMIC i2= init[false] ) )";
        }
    }
    static final String DFA5_eotS =
        "\23\uffff";
    static final String DFA5_eofS =
        "\23\uffff";
    static final String DFA5_minS =
        "\1\4\1\0\2\uffff\11\0\6\uffff";
    static final String DFA5_maxS =
        "\1\63\1\0\2\uffff\11\0\6\uffff";
    static final String DFA5_acceptS =
        "\2\uffff\1\1\12\uffff\1\3\1\6\1\2\1\5\1\7\1\4";
    static final String DFA5_specialS =
        "\1\uffff\1\0\2\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\6\uffff}>";
    static final String[] DFA5_transitionS = {
            "\1\1\1\uffff\1\15\10\uffff\2\2\12\uffff\1\11\3\uffff\1\7\1"+
            "\5\1\4\5\uffff\1\12\1\13\1\14\3\uffff\1\10\2\uffff\1\16\2\uffff"+
            "\1\6",
            "\1\uffff",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA5_eot = DFA.unpackEncodedString(DFA5_eotS);
    static final short[] DFA5_eof = DFA.unpackEncodedString(DFA5_eofS);
    static final char[] DFA5_min = DFA.unpackEncodedStringToUnsignedChars(DFA5_minS);
    static final char[] DFA5_max = DFA.unpackEncodedStringToUnsignedChars(DFA5_maxS);
    static final short[] DFA5_accept = DFA.unpackEncodedString(DFA5_acceptS);
    static final short[] DFA5_special = DFA.unpackEncodedString(DFA5_specialS);
    static final short[][] DFA5_transition;

    static {
        int numStates = DFA5_transitionS.length;
        DFA5_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA5_transition[i] = DFA.unpackEncodedString(DFA5_transitionS[i]);
        }
    }

    class DFA5 extends DFA {

        public DFA5(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 5;
            this.eot = DFA5_eot;
            this.eof = DFA5_eof;
            this.min = DFA5_min;
            this.max = DFA5_max;
            this.accept = DFA5_accept;
            this.special = DFA5_special;
            this.transition = DFA5_transition;
        }
        public String getDescription() {
            return "271:1: valset_or_const returns [AbstractValueRange<?> val] : ( ( ( interval )=> interval ) | ( ( range )=> range ) | ( ( set )=> set ) | ( ( vector )=> vector ) | ( numexpr ) | ( STRING ) | ( ID ) );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA5_1 = input.LA(1);

                         
                        int index5_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_MLSpaceSmallParser()) ) {s = 2;}

                        else if ( (synpred10_MLSpaceSmallParser()) ) {s = 15;}

                        else if ( (synpred15_MLSpaceSmallParser()) ) {s = 16;}

                         
                        input.seek(index5_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA5_4 = input.LA(1);

                         
                        int index5_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_MLSpaceSmallParser()) ) {s = 15;}

                        else if ( (synpred15_MLSpaceSmallParser()) ) {s = 16;}

                         
                        input.seek(index5_4);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA5_5 = input.LA(1);

                         
                        int index5_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_MLSpaceSmallParser()) ) {s = 15;}

                        else if ( (synpred15_MLSpaceSmallParser()) ) {s = 16;}

                         
                        input.seek(index5_5);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA5_6 = input.LA(1);

                         
                        int index5_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_MLSpaceSmallParser()) ) {s = 15;}

                        else if ( (synpred15_MLSpaceSmallParser()) ) {s = 16;}

                         
                        input.seek(index5_6);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA5_7 = input.LA(1);

                         
                        int index5_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_MLSpaceSmallParser()) ) {s = 15;}

                        else if ( (synpred15_MLSpaceSmallParser()) ) {s = 16;}

                         
                        input.seek(index5_7);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA5_8 = input.LA(1);

                         
                        int index5_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_MLSpaceSmallParser()) ) {s = 15;}

                        else if ( (synpred15_MLSpaceSmallParser()) ) {s = 16;}

                        else if ( (true) ) {s = 17;}

                         
                        input.seek(index5_8);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA5_9 = input.LA(1);

                         
                        int index5_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_MLSpaceSmallParser()) ) {s = 15;}

                        else if ( (synpred14_MLSpaceSmallParser()) ) {s = 18;}

                        else if ( (synpred15_MLSpaceSmallParser()) ) {s = 16;}

                         
                        input.seek(index5_9);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA5_10 = input.LA(1);

                         
                        int index5_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_MLSpaceSmallParser()) ) {s = 15;}

                        else if ( (synpred15_MLSpaceSmallParser()) ) {s = 16;}

                         
                        input.seek(index5_10);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA5_11 = input.LA(1);

                         
                        int index5_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_MLSpaceSmallParser()) ) {s = 15;}

                        else if ( (synpred15_MLSpaceSmallParser()) ) {s = 16;}

                         
                        input.seek(index5_11);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA5_12 = input.LA(1);

                         
                        int index5_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_MLSpaceSmallParser()) ) {s = 15;}

                        else if ( (synpred15_MLSpaceSmallParser()) ) {s = 16;}

                         
                        input.seek(index5_12);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 5, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA11_eotS =
        "\14\uffff";
    static final String DFA11_eofS =
        "\14\uffff";
    static final String DFA11_minS =
        "\2\16\2\4\1\uffff\1\4\6\uffff";
    static final String DFA11_maxS =
        "\1\31\1\16\2\63\1\uffff\1\63\6\uffff";
    static final String DFA11_acceptS =
        "\4\uffff\1\7\1\uffff\1\3\1\4\1\5\1\6\1\2\1\1";
    static final String DFA11_specialS =
        "\14\uffff}>";
    static final String[] DFA11_transitionS = {
            "\1\1\1\3\1\2\10\uffff\1\4",
            "\1\5",
            "\1\7\11\uffff\1\6\14\uffff\1\7\3\uffff\3\7\5\uffff\3\7\3\uffff"+
            "\1\7\5\uffff\1\7",
            "\1\11\11\uffff\1\10\14\uffff\1\11\3\uffff\3\11\5\uffff\3\11"+
            "\3\uffff\1\11\5\uffff\1\11",
            "",
            "\1\13\26\uffff\1\13\3\uffff\3\13\5\uffff\3\13\3\uffff\1\13"+
            "\2\uffff\1\12\2\uffff\1\13",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA11_eot = DFA.unpackEncodedString(DFA11_eotS);
    static final short[] DFA11_eof = DFA.unpackEncodedString(DFA11_eofS);
    static final char[] DFA11_min = DFA.unpackEncodedStringToUnsignedChars(DFA11_minS);
    static final char[] DFA11_max = DFA.unpackEncodedStringToUnsignedChars(DFA11_maxS);
    static final short[] DFA11_accept = DFA.unpackEncodedString(DFA11_acceptS);
    static final short[] DFA11_special = DFA.unpackEncodedString(DFA11_specialS);
    static final short[][] DFA11_transition;

    static {
        int numStates = DFA11_transitionS.length;
        DFA11_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA11_transition[i] = DFA.unpackEncodedString(DFA11_transitionS[i]);
        }
    }

    class DFA11 extends DFA {

        public DFA11(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 11;
            this.eot = DFA11_eot;
            this.eof = DFA11_eof;
            this.min = DFA11_min;
            this.max = DFA11_max;
            this.accept = DFA11_accept;
            this.special = DFA11_special;
            this.transition = DFA11_transition;
        }
        public String getDescription() {
            return "297:1: var_interval returns [ValueMatch val] : ( ( EQ EQ varexpr ) | ( EQ EQ STRING ) | ( GREATERTHAN EQ varexpr ) | ( GREATERTHAN varexpr ) | ( LESSTHAN EQ varexpr ) | ( LESSTHAN varexpr ) | ( IN l= ( L_PAREN | L_BRACKET ) low= varexpr ( COMMA | DOTS ) up= varexpr r= ( R_PAREN | R_BRACKET ) ) );";
        }
    }
    static final String DFA17_eotS =
        "\13\uffff";
    static final String DFA17_eofS =
        "\13\uffff";
    static final String DFA17_minS =
        "\1\4\2\uffff\2\0\6\uffff";
    static final String DFA17_maxS =
        "\1\63\2\uffff\2\0\6\uffff";
    static final String DFA17_acceptS =
        "\1\uffff\1\1\3\uffff\1\5\1\6\1\7\1\2\1\3\1\4";
    static final String DFA17_specialS =
        "\3\uffff\1\0\1\1\6\uffff}>";
    static final String[] DFA17_transitionS = {
            "\1\5\26\uffff\1\4\3\uffff\1\1\7\uffff\1\6\1\7\4\uffff\1\3\5"+
            "\uffff\1\1",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA17_eot = DFA.unpackEncodedString(DFA17_eotS);
    static final short[] DFA17_eof = DFA.unpackEncodedString(DFA17_eofS);
    static final char[] DFA17_min = DFA.unpackEncodedStringToUnsignedChars(DFA17_minS);
    static final char[] DFA17_max = DFA.unpackEncodedStringToUnsignedChars(DFA17_maxS);
    static final short[] DFA17_accept = DFA.unpackEncodedString(DFA17_acceptS);
    static final short[] DFA17_special = DFA.unpackEncodedString(DFA17_specialS);
    static final short[][] DFA17_transition;

    static {
        int numStates = DFA17_transitionS.length;
        DFA17_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA17_transition[i] = DFA.unpackEncodedString(DFA17_transitionS[i]);
        }
    }

    class DFA17 extends DFA {

        public DFA17(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 17;
            this.eot = DFA17_eot;
            this.eof = DFA17_eof;
            this.min = DFA17_min;
            this.max = DFA17_max;
            this.accept = DFA17_accept;
            this.special = DFA17_special;
            this.transition = DFA17_transition;
        }
        public String getDescription() {
            return "330:4: (n= numval | {...}? ID | ( L_PAREN e= expr R_PAREN ) | ( L_PAREN boolNode R_PAREN ) | ( L_BRACKET e= expr R_BRACKET ) | ( MIN L_PAREN e1= expr COMMA e2= expr R_PAREN ) | ( MAX L_PAREN e1= expr COMMA e2= expr R_PAREN ) )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA17_3 = input.LA(1);

                         
                        int index17_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((synpred39_MLSpaceSmallParser()&&(getSingleNumValFromVar(input.LT(1).getText())!=null))) ) {s = 1;}

                        else if ( ((synpred40_MLSpaceSmallParser()&&(varsAllowed))) ) {s = 8;}

                         
                        input.seek(index17_3);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA17_4 = input.LA(1);

                         
                        int index17_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred41_MLSpaceSmallParser()) ) {s = 9;}

                        else if ( (synpred42_MLSpaceSmallParser()) ) {s = 10;}

                         
                        input.seek(index17_4);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 17, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA30_eotS =
        "\15\uffff";
    static final String DFA30_eofS =
        "\15\uffff";
    static final String DFA30_minS =
        "\1\4\5\uffff\1\0\6\uffff";
    static final String DFA30_maxS =
        "\1\63\5\uffff\1\0\6\uffff";
    static final String DFA30_acceptS =
        "\1\uffff\1\2\12\uffff\1\1";
    static final String DFA30_specialS =
        "\6\uffff\1\0\6\uffff}>";
    static final String[] DFA30_transitionS = {
            "\1\1\16\uffff\1\1\7\uffff\1\1\3\uffff\3\1\5\uffff\3\1\3\uffff"+
            "\1\6\5\uffff\1\1",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA30_eot = DFA.unpackEncodedString(DFA30_eotS);
    static final short[] DFA30_eof = DFA.unpackEncodedString(DFA30_eofS);
    static final char[] DFA30_min = DFA.unpackEncodedStringToUnsignedChars(DFA30_minS);
    static final char[] DFA30_max = DFA.unpackEncodedStringToUnsignedChars(DFA30_maxS);
    static final short[] DFA30_accept = DFA.unpackEncodedString(DFA30_acceptS);
    static final short[] DFA30_special = DFA.unpackEncodedString(DFA30_specialS);
    static final short[][] DFA30_transition;

    static {
        int numStates = DFA30_transitionS.length;
        DFA30_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA30_transition[i] = DFA.unpackEncodedString(DFA30_transitionS[i]);
        }
    }

    class DFA30 extends DFA {

        public DFA30(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 30;
            this.eot = DFA30_eot;
            this.eof = DFA30_eof;
            this.min = DFA30_min;
            this.max = DFA30_max;
            this.accept = DFA30_accept;
            this.special = DFA30_special;
            this.transition = DFA30_transition;
        }
        public String getDescription() {
            return "()+ loopback of 405:16: ( species_def ( SEMIC )? )+";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA30_6 = input.LA(1);

                         
                        int index30_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred66_MLSpaceSmallParser()) ) {s = 12;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index30_6);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 30, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA36_eotS =
        "\46\uffff";
    static final String DFA36_eofS =
        "\1\2\1\3\6\uffff\1\2\35\uffff";
    static final String DFA36_minS =
        "\1\11\1\4\2\uffff\1\4\1\5\1\4\1\5\2\4\1\33\2\55\1\4\1\10\1\5\1"+
        "\10\1\5\2\4\1\24\1\33\1\55\1\12\1\10\1\5\1\10\1\33\1\4\1\24\1\10"+
        "\1\12\1\4\1\33\1\12\1\10\1\4\1\12";
    static final String DFA36_maxS =
        "\1\55\1\63\2\uffff\2\55\1\63\2\40\4\55\1\63\1\43\1\40\1\10\1\40"+
        "\1\55\1\63\3\55\1\54\1\43\1\40\1\10\1\55\1\63\1\55\1\43\1\54\1\63"+
        "\1\55\1\54\1\43\1\63\1\54";
    static final String DFA36_acceptS =
        "\2\uffff\1\2\1\1\42\uffff";
    static final String DFA36_specialS =
        "\46\uffff}>";
    static final String[] DFA36_transitionS = {
            "\1\1\43\uffff\1\2",
            "\1\2\4\uffff\1\3\11\uffff\1\2\7\uffff\1\2\3\uffff\3\2\5\uffff"+
            "\3\2\3\uffff\1\4\5\uffff\1\2",
            "",
            "",
            "\1\5\3\uffff\1\3\2\uffff\1\3\3\uffff\1\3\13\uffff\1\3\4\uffff"+
            "\1\6\6\2\5\uffff\2\2",
            "\1\10\5\uffff\1\3\41\uffff\1\7",
            "\1\2\1\3\5\uffff\1\3\17\uffff\1\2\3\uffff\3\2\5\uffff\2\2"+
            "\4\uffff\1\11\5\uffff\1\2",
            "\1\10\5\uffff\1\3\3\uffff\1\13\2\uffff\1\2\10\uffff\1\12\4"+
            "\uffff\1\14",
            "\1\2\4\uffff\1\2\1\uffff\1\3\24\uffff\1\2",
            "\1\2\1\3\5\uffff\1\3\3\uffff\1\3\13\uffff\1\3\4\uffff\1\15"+
            "\6\2\5\uffff\2\2",
            "\1\3\1\17\20\uffff\1\16",
            "\1\20",
            "\1\21",
            "\1\2\26\uffff\1\2\3\uffff\3\2\5\uffff\2\2\4\uffff\1\22\5\uffff"+
            "\1\2",
            "\1\2\1\uffff\1\2\3\uffff\1\23\2\3\10\uffff\1\3\2\uffff\1\2"+
            "\3\uffff\4\2",
            "\1\10\5\uffff\1\3\3\uffff\1\13\2\uffff\1\2\15\uffff\1\14",
            "\1\24",
            "\1\10\5\uffff\1\3\3\uffff\1\26\2\uffff\1\2\10\uffff\1\25\4"+
            "\uffff\1\14",
            "\1\2\1\3\5\uffff\1\3\3\uffff\1\3\13\uffff\1\3\4\uffff\1\15"+
            "\6\2\5\uffff\2\2",
            "\1\2\11\uffff\1\3\14\uffff\1\2\3\uffff\3\2\5\uffff\3\2\3\uffff"+
            "\1\27\2\uffff\1\2\2\uffff\1\2",
            "\2\3\3\2\24\uffff\1\3",
            "\1\3\1\31\20\uffff\1\30",
            "\1\32",
            "\1\33\3\uffff\3\3\10\uffff\1\3\1\uffff\1\2\1\17\3\uffff\7"+
            "\2\5\uffff\1\2",
            "\1\2\1\uffff\1\2\3\uffff\1\34\2\3\10\uffff\1\3\2\uffff\1\2"+
            "\3\uffff\4\2",
            "\1\10\5\uffff\1\3\3\uffff\1\26\2\uffff\1\2\15\uffff\1\14",
            "\1\35",
            "\1\3\21\uffff\1\36",
            "\1\2\11\uffff\1\3\14\uffff\1\2\3\uffff\3\2\5\uffff\3\2\3\uffff"+
            "\1\37\2\uffff\1\2\2\uffff\1\2",
            "\2\3\3\2\24\uffff\1\3",
            "\1\2\1\uffff\1\2\3\uffff\1\40\2\3\10\uffff\1\3\2\uffff\1\2"+
            "\3\uffff\4\2",
            "\1\41\3\uffff\3\3\10\uffff\1\3\1\uffff\1\2\1\31\3\uffff\7"+
            "\2\5\uffff\1\2",
            "\1\2\11\uffff\1\3\14\uffff\1\2\3\uffff\3\2\5\uffff\3\2\3\uffff"+
            "\1\42\2\uffff\1\2\2\uffff\1\2",
            "\1\3\21\uffff\1\43",
            "\1\33\3\uffff\3\3\10\uffff\1\3\1\uffff\1\2\1\17\3\uffff\7"+
            "\2\5\uffff\1\2",
            "\1\2\1\uffff\1\2\3\uffff\1\44\2\3\10\uffff\1\3\2\uffff\1\2"+
            "\3\uffff\4\2",
            "\1\2\11\uffff\1\3\14\uffff\1\2\3\uffff\3\2\5\uffff\3\2\3\uffff"+
            "\1\45\2\uffff\1\2\2\uffff\1\2",
            "\1\41\3\uffff\3\3\10\uffff\1\3\1\uffff\1\2\1\31\3\uffff\7"+
            "\2\5\uffff\1\2"
    };

    static final short[] DFA36_eot = DFA.unpackEncodedString(DFA36_eotS);
    static final short[] DFA36_eof = DFA.unpackEncodedString(DFA36_eofS);
    static final char[] DFA36_min = DFA.unpackEncodedStringToUnsignedChars(DFA36_minS);
    static final char[] DFA36_max = DFA.unpackEncodedStringToUnsignedChars(DFA36_maxS);
    static final short[] DFA36_accept = DFA.unpackEncodedString(DFA36_acceptS);
    static final short[] DFA36_special = DFA.unpackEncodedString(DFA36_specialS);
    static final short[][] DFA36_transition;

    static {
        int numStates = DFA36_transitionS.length;
        DFA36_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA36_transition[i] = DFA.unpackEncodedString(DFA36_transitionS[i]);
        }
    }

    class DFA36 extends DFA {

        public DFA36(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 36;
            this.eot = DFA36_eot;
            this.eof = DFA36_eof;
            this.min = DFA36_min;
            this.max = DFA36_max;
            this.accept = DFA36_accept;
            this.special = DFA36_special;
            this.transition = DFA36_transition;
        }
        public String getDescription() {
            return "437:9: ( SEMIC )?";
        }
    }
    static final String DFA62_eotS =
        "\20\uffff";
    static final String DFA62_eofS =
        "\1\1\12\uffff\1\12\4\uffff";
    static final String DFA62_minS =
        "\1\10\1\uffff\2\16\1\uffff\2\4\4\uffff\1\12\1\0\3\uffff";
    static final String DFA62_maxS =
        "\1\43\1\uffff\1\40\1\41\1\uffff\2\63\4\uffff\1\54\1\0\3\uffff";
    static final String DFA62_acceptS =
        "\1\uffff\1\1\2\uffff\1\4\2\uffff\1\2\1\3\1\6\1\5\2\uffff\1\10\1"+
        "\11\1\7";
    static final String DFA62_specialS =
        "\14\uffff\1\0\3\uffff}>";
    static final String[] DFA62_transitionS = {
            "\1\6\1\uffff\1\1\3\uffff\1\5\15\uffff\1\1\3\uffff\1\2\1\3\2"+
            "\4",
            "",
            "\1\4\21\uffff\1\7",
            "\1\4\22\uffff\1\10",
            "",
            "\1\12\26\uffff\1\12\3\uffff\3\12\5\uffff\3\12\3\uffff\1\13"+
            "\2\uffff\1\11\2\uffff\1\12",
            "\1\15\1\uffff\1\15\10\uffff\2\15\12\uffff\1\15\3\uffff\3\15"+
            "\5\uffff\3\15\3\uffff\1\15\2\uffff\1\14\2\uffff\1\15",
            "",
            "",
            "",
            "",
            "\1\12\20\uffff\1\16\1\12\3\uffff\7\12\5\uffff\1\12",
            "\1\uffff",
            "",
            "",
            ""
    };

    static final short[] DFA62_eot = DFA.unpackEncodedString(DFA62_eotS);
    static final short[] DFA62_eof = DFA.unpackEncodedString(DFA62_eofS);
    static final char[] DFA62_min = DFA.unpackEncodedStringToUnsignedChars(DFA62_minS);
    static final char[] DFA62_max = DFA.unpackEncodedStringToUnsignedChars(DFA62_maxS);
    static final short[] DFA62_accept = DFA.unpackEncodedString(DFA62_acceptS);
    static final short[] DFA62_special = DFA.unpackEncodedString(DFA62_specialS);
    static final short[][] DFA62_transition;

    static {
        int numStates = DFA62_transitionS.length;
        DFA62_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA62_transition[i] = DFA.unpackEncodedString(DFA62_transitionS[i]);
        }
    }

    class DFA62 extends DFA {

        public DFA62(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 62;
            this.eot = DFA62_eot;
            this.eof = DFA62_eof;
            this.min = DFA62_min;
            this.max = DFA62_max;
            this.accept = DFA62_accept;
            this.special = DFA62_special;
            this.transition = DFA62_transition;
        }
        public String getDescription() {
            return "510:1: valmod returns [ValueModifier val] : ( | ( PLUS PLUS ) | ( MINUS MINUS ) | ( op EQ nn= varexpr ) | ( EQ n= varexpr ) | ( EQ STRING ) | ( COLON STRING ) | ( COLON v= valset_or_const ) | ( EQ {...}? => ID L_PAREN l= varexpr R_PAREN ) );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA62_12 = input.LA(1);

                         
                        int index62_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred106_MLSpaceSmallParser()) ) {s = 15;}

                        else if ( (synpred107_MLSpaceSmallParser()) ) {s = 13;}

                         
                        input.seek(index62_12);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 62, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_PLUS_in_entsepleft55 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_entsepright0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_defs_in_fullmodel86 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_species_defs_in_fullmodel90 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_init_in_fullmodel102 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_SEMIC_in_fullmodel106 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_rules_in_fullmodel110 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_rules_in_fullmodel123 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_SEMIC_in_fullmodel125 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_init_in_fullmodel129 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_fullmodel138 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_def_in_variable_defs147 = new BitSet(new long[]{0x0000200000000202L});
    public static final BitSet FOLLOW_SEMIC_in_variable_defs149 = new BitSet(new long[]{0x0000200000000202L});
    public static final BitSet FOLLOW_ID_in_variable_def161 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_EQ_in_variable_def163 = new BitSet(new long[]{0x0009238388098050L});
    public static final BitSet FOLLOW_valset_or_const_in_variable_def168 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interval_in_valset_or_const189 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_range_in_valset_or_const201 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_valset_or_const213 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_vector_in_valset_or_const225 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numexpr_in_valset_or_const232 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_valset_or_const239 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_valset_or_const246 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attribute_match_in_attributes_match271 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_COMMA_in_attributes_match276 = new BitSet(new long[]{0x0000200008000000L});
    public static final BitSet FOLLOW_attribute_match_in_attributes_match281 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_L_PAREN_in_attribute_match300 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_ID_in_attribute_match304 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_EQ_in_attribute_match306 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_ID_in_attribute_match311 = new BitSet(new long[]{0x000000001201C000L});
    public static final BitSet FOLLOW_var_interval_in_attribute_match316 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_R_PAREN_in_attribute_match320 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_attribute_match334 = new BitSet(new long[]{0x000000000201C000L});
    public static final BitSet FOLLOW_EQ_in_attribute_match338 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_ID_in_attribute_match343 = new BitSet(new long[]{0x000000000201C002L});
    public static final BitSet FOLLOW_var_interval_in_attribute_match348 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_interval_in_attribute_match381 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQ_in_var_interval400 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_EQ_in_var_interval402 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_varexpr_in_var_interval404 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQ_in_var_interval412 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_EQ_in_var_interval414 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_STRING_in_var_interval416 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATERTHAN_in_var_interval424 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_EQ_in_var_interval426 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_varexpr_in_var_interval428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATERTHAN_in_var_interval436 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_varexpr_in_var_interval438 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESSTHAN_in_var_interval446 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_EQ_in_var_interval448 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_varexpr_in_var_interval450 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESSTHAN_in_var_interval458 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_varexpr_in_var_interval460 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_var_interval468 = new BitSet(new long[]{0x0000000008000010L});
    public static final BitSet FOLLOW_set_in_var_interval472 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_varexpr_in_var_interval480 = new BitSet(new long[]{0x0000000000020400L});
    public static final BitSet FOLLOW_set_in_var_interval482 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_varexpr_in_var_interval490 = new BitSet(new long[]{0x0000000010000020L});
    public static final BitSet FOLLOW_set_in_var_interval494 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_numexpr523 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_varexpr540 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multNode_in_expr560 = new BitSet(new long[]{0x0000000300000002L});
    public static final BitSet FOLLOW_PLUS_in_expr574 = new BitSet(new long[]{0x0008218388000010L});
    public static final BitSet FOLLOW_multNode_in_expr578 = new BitSet(new long[]{0x0000000300000002L});
    public static final BitSet FOLLOW_MINUS_in_expr591 = new BitSet(new long[]{0x0008218388000010L});
    public static final BitSet FOLLOW_multNode_in_expr595 = new BitSet(new long[]{0x0000000300000002L});
    public static final BitSet FOLLOW_IF_in_expr614 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_boolNode_in_expr616 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_THEN_in_expr618 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_expr_in_expr622 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_ELSE_in_expr625 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_expr_in_expr629 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atomNode_in_multNode651 = new BitSet(new long[]{0x0000000C00000002L});
    public static final BitSet FOLLOW_TIMES_in_multNode663 = new BitSet(new long[]{0x0008218388000010L});
    public static final BitSet FOLLOW_atomNode_in_multNode667 = new BitSet(new long[]{0x0000000C00000002L});
    public static final BitSet FOLLOW_DIV_in_multNode680 = new BitSet(new long[]{0x0008218388000010L});
    public static final BitSet FOLLOW_atomNode_in_multNode684 = new BitSet(new long[]{0x0000000C00000002L});
    public static final BitSet FOLLOW_MINUS_in_atomNode722 = new BitSet(new long[]{0x0008218088000010L});
    public static final BitSet FOLLOW_PLUS_in_atomNode728 = new BitSet(new long[]{0x0008218088000010L});
    public static final BitSet FOLLOW_numval_in_atomNode739 = new BitSet(new long[]{0x0000107000000002L});
    public static final BitSet FOLLOW_ID_in_atomNode749 = new BitSet(new long[]{0x0000107000000002L});
    public static final BitSet FOLLOW_L_PAREN_in_atomNode758 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_expr_in_atomNode762 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_R_PAREN_in_atomNode764 = new BitSet(new long[]{0x0000107000000002L});
    public static final BitSet FOLLOW_L_PAREN_in_atomNode774 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_boolNode_in_atomNode776 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_R_PAREN_in_atomNode778 = new BitSet(new long[]{0x0000107000000002L});
    public static final BitSet FOLLOW_L_BRACKET_in_atomNode788 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_expr_in_atomNode793 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_R_BRACKET_in_atomNode795 = new BitSet(new long[]{0x0000107000000002L});
    public static final BitSet FOLLOW_MIN_in_atomNode806 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_L_PAREN_in_atomNode808 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_expr_in_atomNode812 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_COMMA_in_atomNode814 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_expr_in_atomNode818 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_R_PAREN_in_atomNode820 = new BitSet(new long[]{0x0000107000000002L});
    public static final BitSet FOLLOW_MAX_in_atomNode833 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_L_PAREN_in_atomNode835 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_expr_in_atomNode839 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_COMMA_in_atomNode841 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_expr_in_atomNode845 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_R_PAREN_in_atomNode847 = new BitSet(new long[]{0x0000107000000002L});
    public static final BitSet FOLLOW_SQR_in_atomNode870 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CUB_in_atomNode881 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DEGREES_in_atomNode891 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_POW_in_atomNode901 = new BitSet(new long[]{0x0008218388000010L});
    public static final BitSet FOLLOW_atomNode_in_atomNode905 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_boolNode935 = new BitSet(new long[]{0x000400000001C000L});
    public static final BitSet FOLLOW_compareOp_in_boolNode939 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_expr_in_boolNode943 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESSTHAN_in_compareOp955 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_EQ_in_compareOp957 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATERTHAN_in_compareOp962 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_EQ_in_compareOp964 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOTEQ_in_compareOp969 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQ_in_compareOp973 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_EQ_in_compareOp975 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_L_BRACKET_in_interval991 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_numexpr_in_interval996 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_DOTS_in_interval1000 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_numexpr_in_interval1005 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_R_BRACKET_in_interval1009 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATERTHAN_in_interval1018 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_EQ_in_interval1020 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_numexpr_in_interval1026 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESSTHAN_in_interval1036 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_EQ_in_interval1038 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_numexpr_in_interval1044 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATERTHAN_in_interval1054 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_numexpr_in_interval1060 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESSTHAN_in_interval1070 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_numexpr_in_interval1076 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numexpr_in_range1099 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_COLON_in_range1101 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_numexpr_in_range1106 = new BitSet(new long[]{0x0000000000000102L});
    public static final BitSet FOLLOW_COLON_in_range1109 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_numexpr_in_range1114 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numset_in_set1140 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_idset_in_set1146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_L_BRACE_in_idset1165 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_STRING_in_idset1171 = new BitSet(new long[]{0x0000000000000480L});
    public static final BitSet FOLLOW_COMMA_in_idset1176 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_STRING_in_idset1181 = new BitSet(new long[]{0x0000000000000480L});
    public static final BitSet FOLLOW_R_BRACE_in_idset1188 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_L_BRACE_in_numset1206 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_numexpr_in_numset1212 = new BitSet(new long[]{0x0000000000000480L});
    public static final BitSet FOLLOW_COMMA_in_numset1217 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_numexpr_in_numset1222 = new BitSet(new long[]{0x0000000000000480L});
    public static final BitSet FOLLOW_R_BRACE_in_numset1229 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_L_PAREN_in_vector1246 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_numexpr_in_vector1251 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_COMMA_in_vector1256 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_numexpr_in_vector1261 = new BitSet(new long[]{0x0000000010000400L});
    public static final BitSet FOLLOW_R_PAREN_in_vector1267 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numexpr_in_intval_or_var1286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_numval1310 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_numval1318 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_numval1329 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_species_def_in_species_defs1350 = new BitSet(new long[]{0x0000200000000202L});
    public static final BitSet FOLLOW_SEMIC_in_species_defs1352 = new BitSet(new long[]{0x0000200000000002L});
    public static final BitSet FOLLOW_ID_in_species_def1363 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_L_PAREN_in_species_def1365 = new BitSet(new long[]{0x0000200010000000L});
    public static final BitSet FOLLOW_attributes_def_in_species_def1370 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_R_PAREN_in_species_def1374 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_bindingsitesdef_in_species_def1377 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attribute_def_in_attributes_def1400 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_COMMA_in_attributes_def1405 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_attribute_def_in_attributes_def1410 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_ID_in_attribute_def1430 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_COLON_in_attribute_def1434 = new BitSet(new long[]{0x0009238388098050L});
    public static final BitSet FOLLOW_valset_or_const_in_attribute_def1439 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESSTHAN_in_bindingsitesdef1458 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_bindingsitedef_in_bindingsitesdef1465 = new BitSet(new long[]{0x0000000000010400L});
    public static final BitSet FOLLOW_COMMA_in_bindingsitesdef1471 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_bindingsitedef_in_bindingsitesdef1476 = new BitSet(new long[]{0x0000000000010400L});
    public static final BitSet FOLLOW_GREATERTHAN_in_bindingsitesdef1484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_bindingsitedef1501 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_COLON_in_bindingsitedef1503 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_ID_in_bindingsitedef1514 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numexpr_in_bindingsitedef1523 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_species1539 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_rules1561 = new BitSet(new long[]{0x0008238388080212L});
    public static final BitSet FOLLOW_SEMIC_in_rules1563 = new BitSet(new long[]{0x0008238388080012L});
    public static final BitSet FOLLOW_ID_in_rule1590 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_COLON_in_rule1592 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_rule_left_hand_side_in_rule1603 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_ARROW_in_rule1607 = new BitSet(new long[]{0x0008238388081010L});
    public static final BitSet FOLLOW_rule_right_hand_side_in_rule1614 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_AT_in_rule1621 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_rpmark_in_rule1632 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_varexpr_in_rule1642 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_rpmark1659 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_EQ_in_rpmark1663 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entity_match_in_rule_left_hand_side1694 = new BitSet(new long[]{0x0000000100000012L});
    public static final BitSet FOLLOW_L_BRACKET_in_rule_left_hand_side1702 = new BitSet(new long[]{0x0008238388080032L});
    public static final BitSet FOLLOW_entsepleft_in_rule_left_hand_side1710 = new BitSet(new long[]{0x0008238388080032L});
    public static final BitSet FOLLOW_entity_match_in_rule_left_hand_side1719 = new BitSet(new long[]{0x0000000100000032L});
    public static final BitSet FOLLOW_entsepleft_in_rule_left_hand_side1728 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_entity_match_in_rule_left_hand_side1732 = new BitSet(new long[]{0x0000000100000032L});
    public static final BitSet FOLLOW_R_BRACKET_in_rule_left_hand_side1746 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entity_result_in_rule_right_hand_side1773 = new BitSet(new long[]{0x0000000100040012L});
    public static final BitSet FOLLOW_L_BRACKET_in_rule_right_hand_side1782 = new BitSet(new long[]{0x0008238388080032L});
    public static final BitSet FOLLOW_entsepright_in_rule_right_hand_side1788 = new BitSet(new long[]{0x0008238388080032L});
    public static final BitSet FOLLOW_entity_result_in_rule_right_hand_side1797 = new BitSet(new long[]{0x0000000100040032L});
    public static final BitSet FOLLOW_entsepright_in_rule_right_hand_side1806 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_entity_result_in_rule_right_hand_side1810 = new BitSet(new long[]{0x0000000100040032L});
    public static final BitSet FOLLOW_R_BRACKET_in_rule_right_hand_side1825 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_species_in_entity_match1854 = new BitSet(new long[]{0x0000000008008002L});
    public static final BitSet FOLLOW_L_PAREN_in_entity_match1857 = new BitSet(new long[]{0x0000200018000000L});
    public static final BitSet FOLLOW_attributes_match_in_entity_match1862 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_R_PAREN_in_entity_match1865 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_bindingsites_in_entity_match1879 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESSTHAN_in_bindingsites1903 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_bindingsite_in_bindingsites1910 = new BitSet(new long[]{0x0000000000010400L});
    public static final BitSet FOLLOW_COMMA_in_bindingsites1916 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_bindingsite_in_bindingsites1921 = new BitSet(new long[]{0x0000000000010400L});
    public static final BitSet FOLLOW_GREATERTHAN_in_bindingsites1929 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_bindingsite1944 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_COLON_in_bindingsite1946 = new BitSet(new long[]{0x0008238388380010L});
    public static final BitSet FOLLOW_entity_match_in_bindingsite1955 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FREE_in_bindingsite1963 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OCC_in_bindingsite1970 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entity_result_in_entities_result2002 = new BitSet(new long[]{0x0000000100040012L});
    public static final BitSet FOLLOW_entsepright_in_entities_result2008 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_entity_result_in_entities_result2013 = new BitSet(new long[]{0x0000000100040012L});
    public static final BitSet FOLLOW_species_in_entity_result2034 = new BitSet(new long[]{0x0000000008008002L});
    public static final BitSet FOLLOW_L_PAREN_in_entity_result2042 = new BitSet(new long[]{0x0000200010000000L});
    public static final BitSet FOLLOW_ID_in_entity_result2048 = new BitSet(new long[]{0x0000000F10004500L});
    public static final BitSet FOLLOW_valmod_in_entity_result2052 = new BitSet(new long[]{0x0000000010000400L});
    public static final BitSet FOLLOW_COMMA_in_entity_result2064 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_ID_in_entity_result2068 = new BitSet(new long[]{0x0000000F10004500L});
    public static final BitSet FOLLOW_valmod_in_entity_result2072 = new BitSet(new long[]{0x0000000010000400L});
    public static final BitSet FOLLOW_R_PAREN_in_entity_result2084 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_bindingactions_in_entity_result2099 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_valmod2119 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_PLUS_in_valmod2121 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_valmod2131 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_MINUS_in_valmod2133 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_op_in_valmod2143 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_EQ_in_valmod2145 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_varexpr_in_valmod2149 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQ_in_valmod2158 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_varexpr_in_valmod2162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQ_in_valmod2171 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_STRING_in_valmod2173 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_valmod2182 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_STRING_in_valmod2184 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_valmod2193 = new BitSet(new long[]{0x0009238388098050L});
    public static final BitSet FOLLOW_valset_or_const_in_valmod2197 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQ_in_valmod2206 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_ID_in_valmod2212 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_L_PAREN_in_valmod2214 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_varexpr_in_valmod2218 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_R_PAREN_in_valmod2220 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_op0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESSTHAN_in_bindingactions2259 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_bindingaction_in_bindingactions2266 = new BitSet(new long[]{0x0000000000010400L});
    public static final BitSet FOLLOW_COMMA_in_bindingactions2272 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_bindingaction_in_bindingactions2276 = new BitSet(new long[]{0x0000000000010400L});
    public static final BitSet FOLLOW_GREATERTHAN_in_bindingactions2283 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_bindingaction2298 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_COLON_in_bindingaction2300 = new BitSet(new long[]{0x0000000001C00000L});
    public static final BitSet FOLLOW_BIND_in_bindingaction2309 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RELEASE_in_bindingaction2318 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REPLACE_in_bindingaction2326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_init_element_in_init2349 = new BitSet(new long[]{0x0000000100000012L});
    public static final BitSet FOLLOW_entsepleft_in_init2355 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_init_element_in_init2360 = new BitSet(new long[]{0x0000000100000012L});
    public static final BitSet FOLLOW_for_each_in_init_element2384 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_intval_or_var_in_init_element2393 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_entity_result_in_init_element2407 = new BitSet(new long[]{0x0000000000000212L});
    public static final BitSet FOLLOW_L_BRACKET_in_init_element2426 = new BitSet(new long[]{0x0008238388080030L});
    public static final BitSet FOLLOW_entities_result_in_init_element2431 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_R_BRACKET_in_init_element2434 = new BitSet(new long[]{0x0000000000000212L});
    public static final BitSet FOLLOW_L_BRACKET_in_init_element2452 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_init_in_init_element2455 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_R_BRACKET_in_init_element2458 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_SEMIC_in_init_element2464 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FOR_in_for_each2494 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_for_var_in_for_each2497 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_L_BRACE_in_for_each2502 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_init_in_for_each2505 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_R_BRACE_in_for_each2511 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_ID_in_for_var2530 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_EQ_in_for_var2534 = new BitSet(new long[]{0x0008238388080050L});
    public static final BitSet FOLLOW_range_in_for_var2541 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_for_var2548 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_obsTargetEntry_in_observationTargets2577 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_SEMIC_in_observationTargets2584 = new BitSet(new long[]{0x000823838A084010L});
    public static final BitSet FOLLOW_obsTargetEntry_in_observationTargets2589 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_obs_matches_in_obsTargetEntry2621 = new BitSet(new long[]{0x0000000002004002L});
    public static final BitSet FOLLOW_IN_in_obsTargetEntry2629 = new BitSet(new long[]{0x000823838A084010L});
    public static final BitSet FOLLOW_obs_matches_in_obsTargetEntry2634 = new BitSet(new long[]{0x0000000002004002L});
    public static final BitSet FOLLOW_EQ_in_obsTargetEntry2643 = new BitSet(new long[]{0x0000200000002000L});
    public static final BitSet FOLLOW_idlist_in_obsTargetEntry2645 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entity_match_in_obs_matches2678 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_COMMA_in_obs_matches2686 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_entity_match_in_obs_matches2690 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_ID_in_idlist2720 = new BitSet(new long[]{0x0000000000002402L});
    public static final BitSet FOLLOW_HASH_in_idlist2726 = new BitSet(new long[]{0x0000000000002402L});
    public static final BitSet FOLLOW_COMMA_in_idlist2732 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_ID_in_idlist2737 = new BitSet(new long[]{0x0000000000002402L});
    public static final BitSet FOLLOW_HASH_in_idlist2743 = new BitSet(new long[]{0x0000000000002402L});
    public static final BitSet FOLLOW_interval_in_synpred8_MLSpaceSmallParser189 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_range_in_synpred10_MLSpaceSmallParser201 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_vector_in_synpred14_MLSpaceSmallParser225 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numexpr_in_synpred15_MLSpaceSmallParser232 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELSE_in_synpred34_MLSpaceSmallParser625 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_expr_in_synpred34_MLSpaceSmallParser629 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numval_in_synpred39_MLSpaceSmallParser739 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_synpred40_MLSpaceSmallParser749 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_L_PAREN_in_synpred41_MLSpaceSmallParser758 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_expr_in_synpred41_MLSpaceSmallParser762 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_R_PAREN_in_synpred41_MLSpaceSmallParser764 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_L_PAREN_in_synpred42_MLSpaceSmallParser774 = new BitSet(new long[]{0x0008238388080010L});
    public static final BitSet FOLLOW_boolNode_in_synpred42_MLSpaceSmallParser776 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_R_PAREN_in_synpred42_MLSpaceSmallParser778 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_species_def_in_synpred66_MLSpaceSmallParser1350 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_SEMIC_in_synpred66_MLSpaceSmallParser1352 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_synpred71_MLSpaceSmallParser1514 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rpmark_in_synpred76_MLSpaceSmallParser1627 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_synpred106_MLSpaceSmallParser2182 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_STRING_in_synpred106_MLSpaceSmallParser2184 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_synpred107_MLSpaceSmallParser2193 = new BitSet(new long[]{0x0009238388098050L});
    public static final BitSet FOLLOW_valset_or_const_in_synpred107_MLSpaceSmallParser2197 = new BitSet(new long[]{0x0000000000000002L});

}
