// $ANTLR 3.3 Nov 30, 2010 12:50:56 model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g

package model.mlspace.reader.antlr;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Set;
// import java.util.LinkedHashMap;
// import java.util.LinkedHashSet;
import java.util.LinkedList;

import org.jamesii.core.util.collection.ListUtils;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.core.math.match.*;
import org.jamesii.core.math.simpletree.*;
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
import model.mlspace.rules.RuleCollection;
import model.mlspace.rules.RuleSide;
import model.mlspace.rules.MLSpaceRule;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;

public class MLSpaceDirectParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "L_BRACKET", "R_BRACKET", "L_BRACE", "R_BRACE", "COLON", "SEMIC", "COMMA", "ARROW", "AT", "HASH", "EQ", "LESSTHAN", "GREATERTHAN", "DOTS", "DOT", "FOR", "FREE", "OCC", "BIND", "RELEASE", "REPLACE", "IN", "MODELNAMEKW", "L_PAREN", "R_PAREN", "BECOMES", "EXPONENT", "FLOAT", "PLUS", "MINUS", "TIMES", "DIV", "POW", "SQR", "CUB", "MIN", "MAX", "IF", "THEN", "ELSE", "DEGREES", "ID", "COMMENT", "WS", "STRING", "Tokens"
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

    // delegates
    // delegators


        public MLSpaceDirectParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public MLSpaceDirectParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return MLSpaceDirectParser.tokenNames; }
    public String getGrammarFileName() { return "model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g"; }


    private final Map<String, AbstractValueRange<?>> variables = new NonNullMap<String, AbstractValueRange<?>>();
    private final Map<String, List<Double>> forLoopVariables = new NonNullMap<String, List<Double>>();
    private final Set<String> actuallyUsedVariables = new NonNullSet<String>();
    private boolean useExperimentalPostponedRegionInit = false;
    private MLSpaceParserHelper parseTool;

    private String projectName;
    private Map<String,?> varOverrides = Collections.EMPTY_MAP;
    private Set<String> localRuleVariables = null;

      public MLSpaceDirectParser(String fileName, Map<String,?> parameters, boolean testFlag) throws java.io.IOException {
        this(new CommonTokenStream(
                 new MLSpaceCompositeLexer(
                      new ANTLRFileStream(fileName))),
             testFlag);
        projectName = fileName.substring(fileName.lastIndexOf("/")+1,
                                         fileName.lastIndexOf('.'));
        if (parameters != null) {
            this.varOverrides = parameters;
        }
      }
      
          /**
           * Wrap string in Token stream
           * @param toParse String to parse
           * @return TokenStream
           * @throws IOException
           */
          public static CommonTokenStream stringToTokenStream(String toParse)
              throws IOException {
            return new CommonTokenStream(
                      new MLSpaceCompositeLexer(
                           new ANTLRInputStream(
                                new ByteArrayInputStream(toParse.getBytes()))));
          }
      

      public MLSpaceDirectParser(String toParse, boolean testFlag) throws java.io.IOException {
        this(stringToTokenStream(toParse), testFlag);
      }
      
      public MLSpaceDirectParser(TokenStream input,boolean testFlag) {
        this(input);
        this.parseTool = new MLSpaceParserHelper(testFlag);
      }
      
      
      public Map<String,?> getDefinedConstants() {
        return variables;
      }
      
      public Set<String> getUsedConstants() {
        return actuallyUsedVariables;
      }
      
      Deque<String> forVarNames = new LinkedList<String>();
      Deque<Integer> forVarNextPos = new LinkedList<Integer>();
      Map<String, AbstractValueRange<?>> forVarOverridden = new NonNullMap<String, AbstractValueRange<?>>();

      private void handleForVar(String varName, List<?> varRange) {
        if (varName.equals(forVarNames.peek())) {
          // at least one loop has been run
          int pos = forVarNextPos.pop();
          variables.put(varName,AbstractValueRange.newSingleValue(varRange.get(pos)));
          forVarNextPos.push(++pos);
        } else {
          // variable is new
          if (variables.containsKey(varName)) {
            System.err.println("Variable " + varName
                + " already defined. Previous value "
                + variables.get(varName)
                + " overridden in FOR loop expression by " + varRange);
            forVarOverridden.put(varName, variables.get(varName));
          }
          forVarNames.push(varName);
          forVarNextPos.push(1);
          variables.put(varName,AbstractValueRange.newSingleValue(varRange.size() > 0 ? varRange.get(0) : Double.NaN));
        }
      }
      
      private boolean wasLastLoop(String varName, List<?> varRange) {
        assert varName.equals(forVarNames.peek());
        if (varRange.size() > forVarNextPos.peek())
          return false;
        // else return true, but remove var from internal stack(s) first
        forVarNames.pop();
        forVarNextPos.pop();
        AbstractValueRange<?> prevVal = forVarOverridden.remove(varName);
        if (prevVal != null) variables.put(varName,prevVal);
        else variables.remove(varName);
        return true;
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
            if (vr.size() != 1) return null;
            Object value = vr.iterator().next();
            if (!(value instanceof Number)) return null;
            Double numVal = ((Number) value).doubleValue();
            actuallyUsedVariables.add(varname);
            return numVal;
        }
      }


    public static class entsep_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "entsep"
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:171:10: fragment entsep : ( COMMA | PLUS | DOT );
    public final MLSpaceDirectParser.entsep_return entsep() throws RecognitionException {
        MLSpaceDirectParser.entsep_return retval = new MLSpaceDirectParser.entsep_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set1=null;

        Object set1_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:171:17: ( COMMA | PLUS | DOT )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:
            {
            root_0 = (Object)adaptor.nil();

            set1=(Token)input.LT(1);
            if ( input.LA(1)==COMMA||input.LA(1)==DOT||input.LA(1)==PLUS ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set1));
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
    // $ANTLR end "entsep"

    public static class fullmodel_return extends ParserRuleReturnScope {
        public MLSpaceModel model;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "fullmodel"
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:173:1: fullmodel returns [MLSpaceModel model] : ( model_name ( SEMIC )? )? variable_defs species_defs ( ( ( init )=>i1= init[false] ( SEMIC r1= rules )? ) | (r2= rules SEMIC i2= init[false] ) ) EOF ;
    public final MLSpaceDirectParser.fullmodel_return fullmodel() throws RecognitionException {
        MLSpaceDirectParser.fullmodel_return retval = new MLSpaceDirectParser.fullmodel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMIC3=null;
        Token SEMIC6=null;
        Token SEMIC7=null;
        Token EOF8=null;
        MLSpaceDirectParser.init_return i1 = null;

        MLSpaceDirectParser.rules_return r1 = null;

        MLSpaceDirectParser.rules_return r2 = null;

        MLSpaceDirectParser.init_return i2 = null;

        MLSpaceDirectParser.model_name_return model_name2 = null;

        MLSpaceDirectParser.variable_defs_return variable_defs4 = null;

        MLSpaceDirectParser.species_defs_return species_defs5 = null;


        Object SEMIC3_tree=null;
        Object SEMIC6_tree=null;
        Object SEMIC7_tree=null;
        Object EOF8_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:175:1: ( ( model_name ( SEMIC )? )? variable_defs species_defs ( ( ( init )=>i1= init[false] ( SEMIC r1= rules )? ) | (r2= rules SEMIC i2= init[false] ) ) EOF )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:175:3: ( model_name ( SEMIC )? )? variable_defs species_defs ( ( ( init )=>i1= init[false] ( SEMIC r1= rules )? ) | (r2= rules SEMIC i2= init[false] ) ) EOF
            {
            root_0 = (Object)adaptor.nil();

            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:175:3: ( model_name ( SEMIC )? )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==MODELNAMEKW) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:175:4: model_name ( SEMIC )?
                    {
                    pushFollow(FOLLOW_model_name_in_fullmodel80);
                    model_name2=model_name();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, model_name2.getTree());
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:175:15: ( SEMIC )?
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==SEMIC) ) {
                        alt1=1;
                    }
                    switch (alt1) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:0:0: SEMIC
                            {
                            SEMIC3=(Token)match(input,SEMIC,FOLLOW_SEMIC_in_fullmodel82); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            SEMIC3_tree = (Object)adaptor.create(SEMIC3);
                            adaptor.addChild(root_0, SEMIC3_tree);
                            }

                            }
                            break;

                    }


                    }
                    break;

            }

            pushFollow(FOLLOW_variable_defs_in_fullmodel89);
            variable_defs4=variable_defs();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variable_defs4.getTree());
            pushFollow(FOLLOW_species_defs_in_fullmodel93);
            species_defs5=species_defs();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, species_defs5.getTree());
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:178:2: ( ( ( init )=>i1= init[false] ( SEMIC r1= rules )? ) | (r2= rules SEMIC i2= init[false] ) )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==L_BRACKET||LA4_0==FOR||LA4_0==L_PAREN||(LA4_0>=FLOAT && LA4_0<=MINUS)) ) {
                alt4=1;
            }
            else if ( (LA4_0==ID) ) {
                int LA4_5 = input.LA(2);

                if ( ((synpred7_MLSpaceDirectParser()&&(getSingleNumValFromVar(input.LT(1).getText())!=null))) ) {
                    alt4=1;
                }
                else if ( ((parseTool.isValidSpecies(input.LT(1).getText()))) ) {
                    alt4=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 5, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:178:3: ( ( init )=>i1= init[false] ( SEMIC r1= rules )? )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:178:3: ( ( init )=>i1= init[false] ( SEMIC r1= rules )? )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:178:4: ( init )=>i1= init[false] ( SEMIC r1= rules )?
                    {
                    pushFollow(FOLLOW_init_in_fullmodel105);
                    i1=init(false);

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, i1.getTree());
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:178:28: ( SEMIC r1= rules )?
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==SEMIC) ) {
                        alt3=1;
                    }
                    switch (alt3) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:178:29: SEMIC r1= rules
                            {
                            SEMIC6=(Token)match(input,SEMIC,FOLLOW_SEMIC_in_fullmodel109); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            SEMIC6_tree = (Object)adaptor.create(SEMIC6);
                            adaptor.addChild(root_0, SEMIC6_tree);
                            }
                            pushFollow(FOLLOW_rules_in_fullmodel113);
                            r1=rules();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, r1.getTree());

                            }
                            break;

                    }

                    if ( state.backtracking==0 ) {
                      retval.model = parseTool.getModel((i1!=null?i1.map:null),(r1!=null?r1.rules:null),useExperimentalPostponedRegionInit,variables);
                    }

                    }


                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:179:3: (r2= rules SEMIC i2= init[false] )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:179:3: (r2= rules SEMIC i2= init[false] )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:179:4: r2= rules SEMIC i2= init[false]
                    {
                    pushFollow(FOLLOW_rules_in_fullmodel126);
                    r2=rules();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, r2.getTree());
                    SEMIC7=(Token)match(input,SEMIC,FOLLOW_SEMIC_in_fullmodel128); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SEMIC7_tree = (Object)adaptor.create(SEMIC7);
                    adaptor.addChild(root_0, SEMIC7_tree);
                    }
                    pushFollow(FOLLOW_init_in_fullmodel132);
                    i2=init(false);

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, i2.getTree());
                    if ( state.backtracking==0 ) {
                      retval.model = parseTool.getModel((i2!=null?i2.map:null),(r2!=null?r2.rules:null),useExperimentalPostponedRegionInit,variables);
                    }

                    }


                    }
                    break;

            }

            EOF8=(Token)match(input,EOF,FOLLOW_EOF_in_fullmodel141); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            EOF8_tree = (Object)adaptor.create(EOF8);
            adaptor.addChild(root_0, EOF8_tree);
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

    public static class model_name_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "model_name"
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:182:1: model_name : ( MODELNAMEKW ID ) ;
    public final MLSpaceDirectParser.model_name_return model_name() throws RecognitionException {
        MLSpaceDirectParser.model_name_return retval = new MLSpaceDirectParser.model_name_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token MODELNAMEKW9=null;
        Token ID10=null;

        Object MODELNAMEKW9_tree=null;
        Object ID10_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:182:12: ( ( MODELNAMEKW ID ) )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:182:14: ( MODELNAMEKW ID )
            {
            root_0 = (Object)adaptor.nil();

            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:182:14: ( MODELNAMEKW ID )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:182:15: MODELNAMEKW ID
            {
            MODELNAMEKW9=(Token)match(input,MODELNAMEKW,FOLLOW_MODELNAMEKW_in_model_name150); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            MODELNAMEKW9_tree = (Object)adaptor.create(MODELNAMEKW9);
            adaptor.addChild(root_0, MODELNAMEKW9_tree);
            }
            ID10=(Token)match(input,ID,FOLLOW_ID_in_model_name152); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID10_tree = (Object)adaptor.create(ID10);
            adaptor.addChild(root_0, ID10_tree);
            }
            if ( state.backtracking==0 ) {
              projectName = (ID10!=null?ID10.getText():null);
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
    // $ANTLR end "model_name"

    public static class variable_defs_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "variable_defs"
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:184:1: variable_defs : ( variable_def ( SEMIC )? )* ;
    public final MLSpaceDirectParser.variable_defs_return variable_defs() throws RecognitionException {
        MLSpaceDirectParser.variable_defs_return retval = new MLSpaceDirectParser.variable_defs_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMIC12=null;
        MLSpaceDirectParser.variable_def_return variable_def11 = null;


        Object SEMIC12_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:185:1: ( ( variable_def ( SEMIC )? )* )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:185:3: ( variable_def ( SEMIC )? )*
            {
            root_0 = (Object)adaptor.nil();

            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:185:3: ( variable_def ( SEMIC )? )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==ID) ) {
                    int LA6_1 = input.LA(2);

                    if ( (LA6_1==EQ||LA6_1==BECOMES) ) {
                        alt6=1;
                    }


                }


                switch (alt6) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:185:4: variable_def ( SEMIC )?
            	    {
            	    pushFollow(FOLLOW_variable_def_in_variable_defs164);
            	    variable_def11=variable_def();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, variable_def11.getTree());
            	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:185:22: ( SEMIC )?
            	    int alt5=2;
            	    int LA5_0 = input.LA(1);

            	    if ( (LA5_0==SEMIC) ) {
            	        alt5=1;
            	    }
            	    switch (alt5) {
            	        case 1 :
            	            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:0:0: SEMIC
            	            {
            	            SEMIC12=(Token)match(input,SEMIC,FOLLOW_SEMIC_in_variable_defs166); if (state.failed) return retval;

            	            }
            	            break;

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
    // $ANTLR end "variable_defs"

    public static class variable_def_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "variable_def"
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:187:1: variable_def : ID ( BECOMES | EQ ) n= valset_or_const ;
    public final MLSpaceDirectParser.variable_def_return variable_def() throws RecognitionException {
        MLSpaceDirectParser.variable_def_return retval = new MLSpaceDirectParser.variable_def_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID13=null;
        Token BECOMES14=null;
        Token EQ15=null;
        MLSpaceDirectParser.valset_or_const_return n = null;


        Object ID13_tree=null;
        Object BECOMES14_tree=null;
        Object EQ15_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:187:14: ( ID ( BECOMES | EQ ) n= valset_or_const )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:187:16: ID ( BECOMES | EQ ) n= valset_or_const
            {
            root_0 = (Object)adaptor.nil();

            ID13=(Token)match(input,ID,FOLLOW_ID_in_variable_def178); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID13_tree = (Object)adaptor.create(ID13);
            adaptor.addChild(root_0, ID13_tree);
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:187:19: ( BECOMES | EQ )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==BECOMES) ) {
                alt7=1;
            }
            else if ( (LA7_0==EQ) ) {
                alt7=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:187:20: BECOMES
                    {
                    BECOMES14=(Token)match(input,BECOMES,FOLLOW_BECOMES_in_variable_def181); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:187:29: EQ
                    {
                    EQ15=(Token)match(input,EQ,FOLLOW_EQ_in_variable_def184); if (state.failed) return retval;

                    }
                    break;

            }

            pushFollow(FOLLOW_valset_or_const_in_variable_def190);
            n=valset_or_const();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, n.getTree());
            if ( state.backtracking==0 ) {
               Object ovVal = varOverrides.get((ID13!=null?ID13.getText():null));
                if (ovVal == null)
                  variables.put((ID13!=null?ID13.getText():null),(n!=null?n.val:null));
                else if (ovVal instanceof Number) 
                    variables.put((ID13!=null?ID13.getText():null),AbstractValueRange.newSingleValue(((Number) ovVal).doubleValue()));
                else
                    variables.put((ID13!=null?ID13.getText():null),AbstractValueRange.newSingleValue(((String) ovVal)));
                if ((ID13!=null?ID13.getText():null).equalsIgnoreCase(MLSpaceParserHelper.PERIODIC_BOUNDARIES_SETTING) &&
                  parseTool.setPeriodicBoundaries(variables.get((ID13!=null?ID13.getText():null)))!=null) {
                  actuallyUsedVariables.add((ID13!=null?ID13.getText():null));
                }
                if (MLSpaceParserHelper.INSTANT_TRANSFER_ON_INIT.contains((ID13!=null?ID13.getText():null).toLowerCase()) &&
                   Arrays.asList("true", "1", true, 1.0, "yes", "TRUE").containsAll(variables.get((ID13!=null?ID13.getText():null)).toList())) {
                     actuallyUsedVariables.add((ID13!=null?ID13.getText():null));
                     useExperimentalPostponedRegionInit = true;
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

    public static class attributes_def_return extends ParserRuleReturnScope {
        public Map<String,AbstractValueRange<?>> attMap;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "attributes_def"
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:206:1: attributes_def returns [Map<String,AbstractValueRange<?>> attMap] : a1= attribute_def ( ( COMMA | SEMIC ) a2= attribute_def )* ;
    public final MLSpaceDirectParser.attributes_def_return attributes_def() throws RecognitionException {
        MLSpaceDirectParser.attributes_def_return retval = new MLSpaceDirectParser.attributes_def_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COMMA16=null;
        Token SEMIC17=null;
        MLSpaceDirectParser.attribute_def_return a1 = null;

        MLSpaceDirectParser.attribute_def_return a2 = null;


        Object COMMA16_tree=null;
        Object SEMIC17_tree=null;

        retval.attMap = new NonNullMap<>();
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:207:38: (a1= attribute_def ( ( COMMA | SEMIC ) a2= attribute_def )* )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:208:1: a1= attribute_def ( ( COMMA | SEMIC ) a2= attribute_def )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_attribute_def_in_attributes_def212);
            a1=attribute_def();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, a1.getTree());
            if ( state.backtracking==0 ) {
              retval.attMap.put((a1!=null?a1.name:null),(a1!=null?a1.val:null));
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:209:1: ( ( COMMA | SEMIC ) a2= attribute_def )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( ((LA9_0>=SEMIC && LA9_0<=COMMA)) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:209:2: ( COMMA | SEMIC ) a2= attribute_def
            	    {
            	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:209:2: ( COMMA | SEMIC )
            	    int alt8=2;
            	    int LA8_0 = input.LA(1);

            	    if ( (LA8_0==COMMA) ) {
            	        alt8=1;
            	    }
            	    else if ( (LA8_0==SEMIC) ) {
            	        alt8=2;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 8, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt8) {
            	        case 1 :
            	            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:209:3: COMMA
            	            {
            	            COMMA16=(Token)match(input,COMMA,FOLLOW_COMMA_in_attributes_def218); if (state.failed) return retval;

            	            }
            	            break;
            	        case 2 :
            	            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:209:10: SEMIC
            	            {
            	            SEMIC17=(Token)match(input,SEMIC,FOLLOW_SEMIC_in_attributes_def221); if (state.failed) return retval;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_attribute_def_in_attributes_def227);
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
            	    break loop9;
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
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:212:1: attribute_def returns [String name, AbstractValueRange<?> val] : att= ID COLON v= valset_or_const ;
    public final MLSpaceDirectParser.attribute_def_return attribute_def() throws RecognitionException {
        MLSpaceDirectParser.attribute_def_return retval = new MLSpaceDirectParser.attribute_def_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token att=null;
        Token COLON18=null;
        MLSpaceDirectParser.valset_or_const_return v = null;


        Object att_tree=null;
        Object COLON18_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:212:63: (att= ID COLON v= valset_or_const )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:213:3: att= ID COLON v= valset_or_const
            {
            root_0 = (Object)adaptor.nil();

            att=(Token)match(input,ID,FOLLOW_ID_in_attribute_def247); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            att_tree = (Object)adaptor.create(att);
            adaptor.addChild(root_0, att_tree);
            }
            if ( state.backtracking==0 ) {
              retval.name = (att!=null?att.getText():null);
            }
            COLON18=(Token)match(input,COLON,FOLLOW_COLON_in_attribute_def251); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            COLON18_tree = (Object)adaptor.create(COLON18);
            adaptor.addChild(root_0, COLON18_tree);
            }
            pushFollow(FOLLOW_valset_or_const_in_attribute_def256);
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

    public static class valset_or_const_return extends ParserRuleReturnScope {
        public AbstractValueRange<?> val;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "valset_or_const"
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:215:1: valset_or_const returns [AbstractValueRange<?> val] : ( ( ( interval )=> interval ) | ( ( range )=> range ) | ( ( set )=> set ) | ( ( vector )=> vector ) | ( ( numexpr )=> numexpr ) | ( ID ) );
    public final MLSpaceDirectParser.valset_or_const_return valset_or_const() throws RecognitionException {
        MLSpaceDirectParser.valset_or_const_return retval = new MLSpaceDirectParser.valset_or_const_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID24=null;
        MLSpaceDirectParser.interval_return interval19 = null;

        MLSpaceDirectParser.range_return range20 = null;

        MLSpaceDirectParser.set_return set21 = null;

        MLSpaceDirectParser.vector_return vector22 = null;

        MLSpaceDirectParser.numexpr_return numexpr23 = null;


        Object ID24_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:215:52: ( ( ( interval )=> interval ) | ( ( range )=> range ) | ( ( set )=> set ) | ( ( vector )=> vector ) | ( ( numexpr )=> numexpr ) | ( ID ) )
            int alt10=6;
            alt10 = dfa10.predict(input);
            switch (alt10) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:216:2: ( ( interval )=> interval )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:216:2: ( ( interval )=> interval )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:216:3: ( interval )=> interval
                    {
                    pushFollow(FOLLOW_interval_in_valset_or_const276);
                    interval19=interval();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, interval19.getTree());
                    if ( state.backtracking==0 ) {
                      retval.val =AbstractValueRange.newInterval((interval19!=null?interval19.lower:0.0),(interval19!=null?interval19.upper:0.0),(interval19!=null?interval19.incLower:false),(interval19!=null?interval19.incUpper:false));
                    }

                    }


                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:217:2: ( ( range )=> range )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:217:2: ( ( range )=> range )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:217:3: ( range )=> range
                    {
                    pushFollow(FOLLOW_range_in_valset_or_const288);
                    range20=range();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, range20.getTree());
                    if ( state.backtracking==0 ) {
                      retval.val =AbstractValueRange.newRange((range20!=null?range20.lower:0.0),(range20!=null?range20.step:0.0),(range20!=null?range20.upper:0.0));
                    }

                    }


                    }
                    break;
                case 3 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:218:2: ( ( set )=> set )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:218:2: ( ( set )=> set )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:218:3: ( set )=> set
                    {
                    pushFollow(FOLLOW_set_in_valset_or_const300);
                    set21=set();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, set21.getTree());
                    if ( state.backtracking==0 ) {
                      retval.val =AbstractValueRange.newSet((set21!=null?set21.set:null));
                    }

                    }


                    }
                    break;
                case 4 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:219:2: ( ( vector )=> vector )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:219:2: ( ( vector )=> vector )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:219:3: ( vector )=> vector
                    {
                    pushFollow(FOLLOW_vector_in_valset_or_const312);
                    vector22=vector();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, vector22.getTree());
                    if ( state.backtracking==0 ) {
                      retval.val =AbstractValueRange.newSingleValue((vector22!=null?vector22.vec:null));
                    }

                    }


                    }
                    break;
                case 5 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:220:2: ( ( numexpr )=> numexpr )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:220:2: ( ( numexpr )=> numexpr )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:220:3: ( numexpr )=> numexpr
                    {
                    pushFollow(FOLLOW_numexpr_in_valset_or_const324);
                    numexpr23=numexpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, numexpr23.getTree());
                    if ( state.backtracking==0 ) {
                      retval.val =AbstractValueRange.newSingleValue((numexpr23!=null?numexpr23.val:null));
                    }

                    }


                    }
                    break;
                case 6 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:221:2: ( ID )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:221:2: ( ID )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:221:3: ID
                    {
                    ID24=(Token)match(input,ID,FOLLOW_ID_in_valset_or_const331); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ID24_tree = (Object)adaptor.create(ID24);
                    adaptor.addChild(root_0, ID24_tree);
                    }
                    if ( state.backtracking==0 ) {
                      if (variables.containsKey((ID24!=null?ID24.getText():null))) {retval.val =variables.get((ID24!=null?ID24.getText():null)); actuallyUsedVariables.add((ID24!=null?ID24.getText():null));} else retval.val =AbstractValueRange.newSingleValue((ID24!=null?ID24.getText():null));
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

    public static class attributes_return extends ParserRuleReturnScope {
        public Map<String,Pair<? extends ValueMatch,String>> attMap;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "attributes"
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:223:1: attributes returns [Map<String,Pair<? extends ValueMatch,String>> attMap] : a1= attributeWE ( COMMA a2= attributeWE )* ;
    public final MLSpaceDirectParser.attributes_return attributes() throws RecognitionException {
        MLSpaceDirectParser.attributes_return retval = new MLSpaceDirectParser.attributes_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COMMA25=null;
        MLSpaceDirectParser.attributeWE_return a1 = null;

        MLSpaceDirectParser.attributeWE_return a2 = null;


        Object COMMA25_tree=null;

        retval.attMap = new NonNullMap<>();
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:224:38: (a1= attributeWE ( COMMA a2= attributeWE )* )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:225:1: a1= attributeWE ( COMMA a2= attributeWE )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_attributeWE_in_attributes356);
            a1=attributeWE();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, a1.getTree());
            if ( state.backtracking==0 ) {
              retval.attMap.put((a1!=null?a1.name:null),new Pair<>((a1!=null?a1.val:null),(a1!=null?a1.varName:null)));
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:226:1: ( COMMA a2= attributeWE )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==COMMA) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:226:2: COMMA a2= attributeWE
            	    {
            	    COMMA25=(Token)match(input,COMMA,FOLLOW_COMMA_in_attributes361); if (state.failed) return retval;
            	    pushFollow(FOLLOW_attributeWE_in_attributes366);
            	    a2=attributeWE();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, a2.getTree());
            	    if ( state.backtracking==0 ) {
            	      retval.attMap.put((a2!=null?a2.name:null),new Pair<>((a2!=null?a2.val:null),(a2!=null?a2.varName:null)));
            	    }

            	    }
            	    break;

            	default :
            	    break loop11;
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
    // $ANTLR end "attributes"

    public static class attributeWE_return extends ParserRuleReturnScope {
        public String name;
        public ValueMatch val;
        public String varName;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "attributeWE"
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:229:1: attributeWE returns [String name, ValueMatch val,String varName] : ( ({...}?) (vn= ID BECOMES ( ( attribute )=>attWE= attribute | an= ID ) ) | att= attribute );
    public final MLSpaceDirectParser.attributeWE_return attributeWE() throws RecognitionException {
        MLSpaceDirectParser.attributeWE_return retval = new MLSpaceDirectParser.attributeWE_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token vn=null;
        Token an=null;
        Token BECOMES26=null;
        MLSpaceDirectParser.attribute_return attWE = null;

        MLSpaceDirectParser.attribute_return att = null;


        Object vn_tree=null;
        Object an_tree=null;
        Object BECOMES26_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:229:65: ( ({...}?) (vn= ID BECOMES ( ( attribute )=>attWE= attribute | an= ID ) ) | att= attribute )
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==ID) ) {
                int LA13_1 = input.LA(2);

                if ( (LA13_1==BECOMES) ) {
                    alt13=1;
                }
                else if ( (LA13_1==L_BRACKET||LA13_1==L_BRACE||LA13_1==COLON||(LA13_1>=EQ && LA13_1<=GREATERTHAN)||LA13_1==IN||LA13_1==L_PAREN||(LA13_1>=FLOAT && LA13_1<=MINUS)||LA13_1==ID) ) {
                    alt13=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 13, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:230:3: ({...}?) (vn= ID BECOMES ( ( attribute )=>attWE= attribute | an= ID ) )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:230:3: ({...}?)
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:230:4: {...}?
                    {
                    if ( !((input.LA(2)==BECOMES)) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "attributeWE", "input.LA(2)==BECOMES");
                    }

                    }

                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:230:29: (vn= ID BECOMES ( ( attribute )=>attWE= attribute | an= ID ) )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:230:30: vn= ID BECOMES ( ( attribute )=>attWE= attribute | an= ID )
                    {
                    vn=(Token)match(input,ID,FOLLOW_ID_in_attributeWE391); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    vn_tree = (Object)adaptor.create(vn);
                    adaptor.addChild(root_0, vn_tree);
                    }
                    BECOMES26=(Token)match(input,BECOMES,FOLLOW_BECOMES_in_attributeWE393); if (state.failed) return retval;
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:231:5: ( ( attribute )=>attWE= attribute | an= ID )
                    int alt12=2;
                    alt12 = dfa12.predict(input);
                    switch (alt12) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:231:6: ( attribute )=>attWE= attribute
                            {
                            pushFollow(FOLLOW_attribute_in_attributeWE410);
                            attWE=attribute();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, attWE.getTree());
                            if ( state.backtracking==0 ) {
                              retval.name = (attWE!=null?attWE.name:null); retval.val = (attWE!=null?attWE.val:null); retval.varName = (vn!=null?vn.getText():null);
                            }

                            }
                            break;
                        case 2 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:232:7: an= ID
                            {
                            an=(Token)match(input,ID,FOLLOW_ID_in_attributeWE422); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            an_tree = (Object)adaptor.create(an);
                            adaptor.addChild(root_0, an_tree);
                            }
                            if ( state.backtracking==0 ) {
                              retval.name = (an!=null?an.getText():null); retval.varName = (vn!=null?vn.getText():null);
                            }

                            }
                            break;

                    }


                    }


                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:233:5: att= attribute
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_attribute_in_attributeWE434);
                    att=attribute();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, att.getTree());
                    if ( state.backtracking==0 ) {
                      retval.name = (att!=null?att.name:null); retval.val = (att!=null?att.val:null);
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
    // $ANTLR end "attributeWE"

    public static class attribute_return extends ParserRuleReturnScope {
        public String name;
        public ValueMatch val;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "attribute"
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:235:1: attribute returns [String name, ValueMatch val] : att= ID (vi= var_interval | ( ( COLON )? v= valset_or_const ) ) ;
    public final MLSpaceDirectParser.attribute_return attribute() throws RecognitionException {
        MLSpaceDirectParser.attribute_return retval = new MLSpaceDirectParser.attribute_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token att=null;
        Token COLON27=null;
        MLSpaceDirectParser.var_interval_return vi = null;

        MLSpaceDirectParser.valset_or_const_return v = null;


        Object att_tree=null;
        Object COLON27_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:235:48: (att= ID (vi= var_interval | ( ( COLON )? v= valset_or_const ) ) )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:236:3: att= ID (vi= var_interval | ( ( COLON )? v= valset_or_const ) )
            {
            root_0 = (Object)adaptor.nil();

            att=(Token)match(input,ID,FOLLOW_ID_in_attribute451); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            att_tree = (Object)adaptor.create(att);
            adaptor.addChild(root_0, att_tree);
            }
            if ( state.backtracking==0 ) {
              retval.name = (att!=null?att.getText():null);
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:237:3: (vi= var_interval | ( ( COLON )? v= valset_or_const ) )
            int alt15=2;
            alt15 = dfa15.predict(input);
            switch (alt15) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:237:4: vi= var_interval
                    {
                    pushFollow(FOLLOW_var_interval_in_attribute461);
                    vi=var_interval();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, vi.getTree());
                    if ( state.backtracking==0 ) {
                      retval.val = (vi!=null?vi.val:null);
                    }

                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:238:4: ( ( COLON )? v= valset_or_const )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:238:4: ( ( COLON )? v= valset_or_const )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:238:5: ( COLON )? v= valset_or_const
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:238:5: ( COLON )?
                    int alt14=2;
                    int LA14_0 = input.LA(1);

                    if ( (LA14_0==COLON) ) {
                        alt14=1;
                    }
                    switch (alt14) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:0:0: COLON
                            {
                            COLON27=(Token)match(input,COLON,FOLLOW_COLON_in_attribute470); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            COLON27_tree = (Object)adaptor.create(COLON27);
                            adaptor.addChild(root_0, COLON27_tree);
                            }

                            }
                            break;

                    }

                    pushFollow(FOLLOW_valset_or_const_in_attribute475);
                    v=valset_or_const();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, v.getTree());
                    if ( state.backtracking==0 ) {
                      retval.val =new ValueMatchRange((v!=null?v.val:null));
                    }

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
    // $ANTLR end "attribute"

    public static class var_interval_return extends ParserRuleReturnScope {
        public ValueMatch val;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "var_interval"
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:241:1: var_interval returns [ValueMatch val] : ( ( EQ EQ node ) | ( GREATERTHAN EQ node ) | ( GREATERTHAN node ) | ( LESSTHAN EQ node ) | ( LESSTHAN node ) | ( IN l= ( L_PAREN | L_BRACKET ) low= node ( COMMA | DOTS ) up= node r= ( R_PAREN | R_BRACKET ) ) );
    public final MLSpaceDirectParser.var_interval_return var_interval() throws RecognitionException {
        MLSpaceDirectParser.var_interval_return retval = new MLSpaceDirectParser.var_interval_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token l=null;
        Token r=null;
        Token EQ28=null;
        Token EQ29=null;
        Token GREATERTHAN31=null;
        Token EQ32=null;
        Token GREATERTHAN34=null;
        Token LESSTHAN36=null;
        Token EQ37=null;
        Token LESSTHAN39=null;
        Token IN41=null;
        Token set42=null;
        MLSpaceDirectParser.node_return low = null;

        MLSpaceDirectParser.node_return up = null;

        MLSpaceDirectParser.node_return node30 = null;

        MLSpaceDirectParser.node_return node33 = null;

        MLSpaceDirectParser.node_return node35 = null;

        MLSpaceDirectParser.node_return node38 = null;

        MLSpaceDirectParser.node_return node40 = null;


        Object l_tree=null;
        Object r_tree=null;
        Object EQ28_tree=null;
        Object EQ29_tree=null;
        Object GREATERTHAN31_tree=null;
        Object EQ32_tree=null;
        Object GREATERTHAN34_tree=null;
        Object LESSTHAN36_tree=null;
        Object EQ37_tree=null;
        Object LESSTHAN39_tree=null;
        Object IN41_tree=null;
        Object set42_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:241:38: ( ( EQ EQ node ) | ( GREATERTHAN EQ node ) | ( GREATERTHAN node ) | ( LESSTHAN EQ node ) | ( LESSTHAN node ) | ( IN l= ( L_PAREN | L_BRACKET ) low= node ( COMMA | DOTS ) up= node r= ( R_PAREN | R_BRACKET ) ) )
            int alt16=6;
            switch ( input.LA(1) ) {
            case EQ:
                {
                alt16=1;
                }
                break;
            case GREATERTHAN:
                {
                int LA16_2 = input.LA(2);

                if ( (LA16_2==EQ) ) {
                    alt16=2;
                }
                else if ( (LA16_2==L_BRACKET||LA16_2==L_PAREN||(LA16_2>=FLOAT && LA16_2<=MINUS)||(LA16_2>=MIN && LA16_2<=MAX)||LA16_2==ID) ) {
                    alt16=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 16, 2, input);

                    throw nvae;
                }
                }
                break;
            case LESSTHAN:
                {
                int LA16_3 = input.LA(2);

                if ( (LA16_3==EQ) ) {
                    alt16=4;
                }
                else if ( (LA16_3==L_BRACKET||LA16_3==L_PAREN||(LA16_3>=FLOAT && LA16_3<=MINUS)||(LA16_3>=MIN && LA16_3<=MAX)||LA16_3==ID) ) {
                    alt16=5;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 16, 3, input);

                    throw nvae;
                }
                }
                break;
            case IN:
                {
                alt16=6;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;
            }

            switch (alt16) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:242:3: ( EQ EQ node )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:242:3: ( EQ EQ node )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:242:4: EQ EQ node
                    {
                    EQ28=(Token)match(input,EQ,FOLLOW_EQ_in_var_interval496); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQ28_tree = (Object)adaptor.create(EQ28);
                    adaptor.addChild(root_0, EQ28_tree);
                    }
                    EQ29=(Token)match(input,EQ,FOLLOW_EQ_in_var_interval498); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQ29_tree = (Object)adaptor.create(EQ29);
                    adaptor.addChild(root_0, EQ29_tree);
                    }
                    pushFollow(FOLLOW_node_in_var_interval500);
                    node30=node();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, node30.getTree());
                    if ( state.backtracking==0 ) {
                      retval.val = ValueMatches.newEquals((node30!=null?node30.node:null));
                    }

                    }


                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:243:3: ( GREATERTHAN EQ node )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:243:3: ( GREATERTHAN EQ node )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:243:4: GREATERTHAN EQ node
                    {
                    GREATERTHAN31=(Token)match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_var_interval508); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATERTHAN31_tree = (Object)adaptor.create(GREATERTHAN31);
                    adaptor.addChild(root_0, GREATERTHAN31_tree);
                    }
                    EQ32=(Token)match(input,EQ,FOLLOW_EQ_in_var_interval510); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQ32_tree = (Object)adaptor.create(EQ32);
                    adaptor.addChild(root_0, EQ32_tree);
                    }
                    pushFollow(FOLLOW_node_in_var_interval512);
                    node33=node();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, node33.getTree());
                    if ( state.backtracking==0 ) {
                      retval.val = new RangeMatches.GreaterOrEqual((node33!=null?node33.node:null));
                    }

                    }


                    }
                    break;
                case 3 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:244:3: ( GREATERTHAN node )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:244:3: ( GREATERTHAN node )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:244:4: GREATERTHAN node
                    {
                    GREATERTHAN34=(Token)match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_var_interval520); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATERTHAN34_tree = (Object)adaptor.create(GREATERTHAN34);
                    adaptor.addChild(root_0, GREATERTHAN34_tree);
                    }
                    pushFollow(FOLLOW_node_in_var_interval522);
                    node35=node();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, node35.getTree());
                    if ( state.backtracking==0 ) {
                      retval.val = new RangeMatches.GreaterThan((node35!=null?node35.node:null));
                    }

                    }


                    }
                    break;
                case 4 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:245:3: ( LESSTHAN EQ node )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:245:3: ( LESSTHAN EQ node )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:245:4: LESSTHAN EQ node
                    {
                    LESSTHAN36=(Token)match(input,LESSTHAN,FOLLOW_LESSTHAN_in_var_interval530); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LESSTHAN36_tree = (Object)adaptor.create(LESSTHAN36);
                    adaptor.addChild(root_0, LESSTHAN36_tree);
                    }
                    EQ37=(Token)match(input,EQ,FOLLOW_EQ_in_var_interval532); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQ37_tree = (Object)adaptor.create(EQ37);
                    adaptor.addChild(root_0, EQ37_tree);
                    }
                    pushFollow(FOLLOW_node_in_var_interval534);
                    node38=node();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, node38.getTree());
                    if ( state.backtracking==0 ) {
                      retval.val = new RangeMatches.LessOrEqual((node38!=null?node38.node:null));
                    }

                    }


                    }
                    break;
                case 5 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:246:3: ( LESSTHAN node )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:246:3: ( LESSTHAN node )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:246:4: LESSTHAN node
                    {
                    LESSTHAN39=(Token)match(input,LESSTHAN,FOLLOW_LESSTHAN_in_var_interval542); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LESSTHAN39_tree = (Object)adaptor.create(LESSTHAN39);
                    adaptor.addChild(root_0, LESSTHAN39_tree);
                    }
                    pushFollow(FOLLOW_node_in_var_interval544);
                    node40=node();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, node40.getTree());
                    if ( state.backtracking==0 ) {
                      retval.val = new RangeMatches.LessThan((node40!=null?node40.node:null));
                    }

                    }


                    }
                    break;
                case 6 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:247:3: ( IN l= ( L_PAREN | L_BRACKET ) low= node ( COMMA | DOTS ) up= node r= ( R_PAREN | R_BRACKET ) )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:247:3: ( IN l= ( L_PAREN | L_BRACKET ) low= node ( COMMA | DOTS ) up= node r= ( R_PAREN | R_BRACKET ) )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:247:4: IN l= ( L_PAREN | L_BRACKET ) low= node ( COMMA | DOTS ) up= node r= ( R_PAREN | R_BRACKET )
                    {
                    IN41=(Token)match(input,IN,FOLLOW_IN_in_var_interval552); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IN41_tree = (Object)adaptor.create(IN41);
                    adaptor.addChild(root_0, IN41_tree);
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

                    pushFollow(FOLLOW_node_in_var_interval564);
                    low=node();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, low.getTree());
                    set42=(Token)input.LT(1);
                    if ( input.LA(1)==COMMA||input.LA(1)==DOTS ) {
                        input.consume();
                        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set42));
                        state.errorRecovery=false;state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    pushFollow(FOLLOW_node_in_var_interval574);
                    up=node();

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

    public static class node_return extends ParserRuleReturnScope {
        public DoubleNode node;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "node"
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:251:1: node returns [DoubleNode node] : e= multNode ( PLUS e2= multNode | MINUS e2= multNode )* ;
    public final MLSpaceDirectParser.node_return node() throws RecognitionException {
        MLSpaceDirectParser.node_return retval = new MLSpaceDirectParser.node_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token PLUS43=null;
        Token MINUS44=null;
        MLSpaceDirectParser.multNode_return e = null;

        MLSpaceDirectParser.multNode_return e2 = null;


        Object PLUS43_tree=null;
        Object MINUS44_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:251:31: (e= multNode ( PLUS e2= multNode | MINUS e2= multNode )* )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:252:3: e= multNode ( PLUS e2= multNode | MINUS e2= multNode )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_multNode_in_node607);
            e=multNode();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, e.getTree());
            if ( state.backtracking==0 ) {
              retval.node = (e!=null?e.node:null);
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:253:9: ( PLUS e2= multNode | MINUS e2= multNode )*
            loop17:
            do {
                int alt17=3;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==PLUS) ) {
                    alt17=1;
                }
                else if ( (LA17_0==MINUS) ) {
                    alt17=2;
                }


                switch (alt17) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:253:10: PLUS e2= multNode
            	    {
            	    PLUS43=(Token)match(input,PLUS,FOLLOW_PLUS_in_node621); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    PLUS43_tree = (Object)adaptor.create(PLUS43);
            	    adaptor.addChild(root_0, PLUS43_tree);
            	    }
            	    pushFollow(FOLLOW_multNode_in_node625);
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
            	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:254:10: MINUS e2= multNode
            	    {
            	    MINUS44=(Token)match(input,MINUS,FOLLOW_MINUS_in_node638); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    MINUS44_tree = (Object)adaptor.create(MINUS44);
            	    adaptor.addChild(root_0, MINUS44_tree);
            	    }
            	    pushFollow(FOLLOW_multNode_in_node642);
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
            	    break loop17;
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
    // $ANTLR end "node"

    public static class multNode_return extends ParserRuleReturnScope {
        public DoubleNode node;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "multNode"
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:257:1: multNode returns [DoubleNode node] : e= atomNode ( TIMES e2= atomNode | DIV e2= atomNode )* ;
    public final MLSpaceDirectParser.multNode_return multNode() throws RecognitionException {
        MLSpaceDirectParser.multNode_return retval = new MLSpaceDirectParser.multNode_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TIMES45=null;
        Token DIV46=null;
        MLSpaceDirectParser.atomNode_return e = null;

        MLSpaceDirectParser.atomNode_return e2 = null;


        Object TIMES45_tree=null;
        Object DIV46_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:257:35: (e= atomNode ( TIMES e2= atomNode | DIV e2= atomNode )* )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:258:3: e= atomNode ( TIMES e2= atomNode | DIV e2= atomNode )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_atomNode_in_multNode671);
            e=atomNode();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, e.getTree());
            if ( state.backtracking==0 ) {
              retval.node = (e!=null?e.node:null);
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:259:8: ( TIMES e2= atomNode | DIV e2= atomNode )*
            loop18:
            do {
                int alt18=3;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==TIMES) ) {
                    alt18=1;
                }
                else if ( (LA18_0==DIV) ) {
                    alt18=2;
                }


                switch (alt18) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:259:9: TIMES e2= atomNode
            	    {
            	    TIMES45=(Token)match(input,TIMES,FOLLOW_TIMES_in_multNode683); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    TIMES45_tree = (Object)adaptor.create(TIMES45);
            	    adaptor.addChild(root_0, TIMES45_tree);
            	    }
            	    pushFollow(FOLLOW_atomNode_in_multNode687);
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
            	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:260:10: DIV e2= atomNode
            	    {
            	    DIV46=(Token)match(input,DIV,FOLLOW_DIV_in_multNode700); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    DIV46_tree = (Object)adaptor.create(DIV46);
            	    adaptor.addChild(root_0, DIV46_tree);
            	    }
            	    pushFollow(FOLLOW_atomNode_in_multNode704);
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
            	    break loop18;
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
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:263:1: atomNode returns [DoubleNode node] : ( ( MINUS | PLUS )? (n= numval | ID | ( L_PAREN e= node R_PAREN ) | ( L_BRACKET e= node R_BRACKET ) | ( MIN L_PAREN e1= node COMMA e2= node R_PAREN ) | ( MAX L_PAREN e1= node COMMA e2= node R_PAREN ) ) ) ( ( SQR ) | ( CUB ) | ( DEGREES ) | ( POW a= atomNode ) )? ;
    public final MLSpaceDirectParser.atomNode_return atomNode() throws RecognitionException {
        MLSpaceDirectParser.atomNode_return retval = new MLSpaceDirectParser.atomNode_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token MINUS47=null;
        Token PLUS48=null;
        Token ID49=null;
        Token L_PAREN50=null;
        Token R_PAREN51=null;
        Token L_BRACKET52=null;
        Token R_BRACKET53=null;
        Token MIN54=null;
        Token L_PAREN55=null;
        Token COMMA56=null;
        Token R_PAREN57=null;
        Token MAX58=null;
        Token L_PAREN59=null;
        Token COMMA60=null;
        Token R_PAREN61=null;
        Token SQR62=null;
        Token CUB63=null;
        Token DEGREES64=null;
        Token POW65=null;
        MLSpaceDirectParser.numval_return n = null;

        MLSpaceDirectParser.node_return e = null;

        MLSpaceDirectParser.node_return e1 = null;

        MLSpaceDirectParser.node_return e2 = null;

        MLSpaceDirectParser.atomNode_return a = null;


        Object MINUS47_tree=null;
        Object PLUS48_tree=null;
        Object ID49_tree=null;
        Object L_PAREN50_tree=null;
        Object R_PAREN51_tree=null;
        Object L_BRACKET52_tree=null;
        Object R_BRACKET53_tree=null;
        Object MIN54_tree=null;
        Object L_PAREN55_tree=null;
        Object COMMA56_tree=null;
        Object R_PAREN57_tree=null;
        Object MAX58_tree=null;
        Object L_PAREN59_tree=null;
        Object COMMA60_tree=null;
        Object R_PAREN61_tree=null;
        Object SQR62_tree=null;
        Object CUB63_tree=null;
        Object DEGREES64_tree=null;
        Object POW65_tree=null;

        double sign = 1.;
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:263:61: ( ( ( MINUS | PLUS )? (n= numval | ID | ( L_PAREN e= node R_PAREN ) | ( L_BRACKET e= node R_BRACKET ) | ( MIN L_PAREN e1= node COMMA e2= node R_PAREN ) | ( MAX L_PAREN e1= node COMMA e2= node R_PAREN ) ) ) ( ( SQR ) | ( CUB ) | ( DEGREES ) | ( POW a= atomNode ) )? )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:264:3: ( ( MINUS | PLUS )? (n= numval | ID | ( L_PAREN e= node R_PAREN ) | ( L_BRACKET e= node R_BRACKET ) | ( MIN L_PAREN e1= node COMMA e2= node R_PAREN ) | ( MAX L_PAREN e1= node COMMA e2= node R_PAREN ) ) ) ( ( SQR ) | ( CUB ) | ( DEGREES ) | ( POW a= atomNode ) )?
            {
            root_0 = (Object)adaptor.nil();

            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:264:3: ( ( MINUS | PLUS )? (n= numval | ID | ( L_PAREN e= node R_PAREN ) | ( L_BRACKET e= node R_BRACKET ) | ( MIN L_PAREN e1= node COMMA e2= node R_PAREN ) | ( MAX L_PAREN e1= node COMMA e2= node R_PAREN ) ) )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:264:4: ( MINUS | PLUS )? (n= numval | ID | ( L_PAREN e= node R_PAREN ) | ( L_BRACKET e= node R_BRACKET ) | ( MIN L_PAREN e1= node COMMA e2= node R_PAREN ) | ( MAX L_PAREN e1= node COMMA e2= node R_PAREN ) )
            {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:264:4: ( MINUS | PLUS )?
            int alt19=3;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==MINUS) ) {
                alt19=1;
            }
            else if ( (LA19_0==PLUS) ) {
                alt19=2;
            }
            switch (alt19) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:264:5: MINUS
                    {
                    MINUS47=(Token)match(input,MINUS,FOLLOW_MINUS_in_atomNode742); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    MINUS47_tree = (Object)adaptor.create(MINUS47);
                    adaptor.addChild(root_0, MINUS47_tree);
                    }
                    if ( state.backtracking==0 ) {
                      sign = -1.;
                    }

                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:264:27: PLUS
                    {
                    PLUS48=(Token)match(input,PLUS,FOLLOW_PLUS_in_atomNode748); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    PLUS48_tree = (Object)adaptor.create(PLUS48);
                    adaptor.addChild(root_0, PLUS48_tree);
                    }

                    }
                    break;

            }

            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:265:4: (n= numval | ID | ( L_PAREN e= node R_PAREN ) | ( L_BRACKET e= node R_BRACKET ) | ( MIN L_PAREN e1= node COMMA e2= node R_PAREN ) | ( MAX L_PAREN e1= node COMMA e2= node R_PAREN ) )
            int alt20=6;
            switch ( input.LA(1) ) {
            case FLOAT:
                {
                alt20=1;
                }
                break;
            case ID:
                {
                int LA20_2 = input.LA(2);

                if ( ((synpred42_MLSpaceDirectParser()&&(getSingleNumValFromVar(input.LT(1).getText())!=null))) ) {
                    alt20=1;
                }
                else if ( (synpred43_MLSpaceDirectParser()) ) {
                    alt20=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 20, 2, input);

                    throw nvae;
                }
                }
                break;
            case L_PAREN:
                {
                alt20=3;
                }
                break;
            case L_BRACKET:
                {
                alt20=4;
                }
                break;
            case MIN:
                {
                alt20=5;
                }
                break;
            case MAX:
                {
                alt20=6;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;
            }

            switch (alt20) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:265:6: n= numval
                    {
                    pushFollow(FOLLOW_numval_in_atomNode760);
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
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:266:6: ID
                    {
                    ID49=(Token)match(input,ID,FOLLOW_ID_in_atomNode769); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ID49_tree = (Object)adaptor.create(ID49);
                    adaptor.addChild(root_0, ID49_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.node = new VariableNode((ID49!=null?ID49.getText():null));
                    }

                    }
                    break;
                case 3 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:267:6: ( L_PAREN e= node R_PAREN )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:267:6: ( L_PAREN e= node R_PAREN )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:267:7: L_PAREN e= node R_PAREN
                    {
                    L_PAREN50=(Token)match(input,L_PAREN,FOLLOW_L_PAREN_in_atomNode779); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    L_PAREN50_tree = (Object)adaptor.create(L_PAREN50);
                    adaptor.addChild(root_0, L_PAREN50_tree);
                    }
                    pushFollow(FOLLOW_node_in_atomNode783);
                    e=node();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, e.getTree());
                    R_PAREN51=(Token)match(input,R_PAREN,FOLLOW_R_PAREN_in_atomNode785); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    R_PAREN51_tree = (Object)adaptor.create(R_PAREN51);
                    adaptor.addChild(root_0, R_PAREN51_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.node =(e!=null?e.node:null);
                    }

                    }


                    }
                    break;
                case 4 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:268:6: ( L_BRACKET e= node R_BRACKET )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:268:6: ( L_BRACKET e= node R_BRACKET )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:268:7: L_BRACKET e= node R_BRACKET
                    {
                    L_BRACKET52=(Token)match(input,L_BRACKET,FOLLOW_L_BRACKET_in_atomNode796); if (state.failed) return retval;
                    pushFollow(FOLLOW_node_in_atomNode801);
                    e=node();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, e.getTree());
                    R_BRACKET53=(Token)match(input,R_BRACKET,FOLLOW_R_BRACKET_in_atomNode803); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      retval.node = new IntNode((e!=null?e.node:null));
                    }

                    }


                    }
                    break;
                case 5 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:269:6: ( MIN L_PAREN e1= node COMMA e2= node R_PAREN )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:269:6: ( MIN L_PAREN e1= node COMMA e2= node R_PAREN )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:269:7: MIN L_PAREN e1= node COMMA e2= node R_PAREN
                    {
                    MIN54=(Token)match(input,MIN,FOLLOW_MIN_in_atomNode815); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    MIN54_tree = (Object)adaptor.create(MIN54);
                    adaptor.addChild(root_0, MIN54_tree);
                    }
                    L_PAREN55=(Token)match(input,L_PAREN,FOLLOW_L_PAREN_in_atomNode817); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    L_PAREN55_tree = (Object)adaptor.create(L_PAREN55);
                    adaptor.addChild(root_0, L_PAREN55_tree);
                    }
                    pushFollow(FOLLOW_node_in_atomNode821);
                    e1=node();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, e1.getTree());
                    COMMA56=(Token)match(input,COMMA,FOLLOW_COMMA_in_atomNode823); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    COMMA56_tree = (Object)adaptor.create(COMMA56);
                    adaptor.addChild(root_0, COMMA56_tree);
                    }
                    pushFollow(FOLLOW_node_in_atomNode827);
                    e2=node();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, e2.getTree());
                    R_PAREN57=(Token)match(input,R_PAREN,FOLLOW_R_PAREN_in_atomNode829); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    R_PAREN57_tree = (Object)adaptor.create(R_PAREN57);
                    adaptor.addChild(root_0, R_PAREN57_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.node = new MinNode((e1!=null?e1.node:null),(e2!=null?e2.node:null));
                    }

                    }


                    }
                    break;
                case 6 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:270:6: ( MAX L_PAREN e1= node COMMA e2= node R_PAREN )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:270:6: ( MAX L_PAREN e1= node COMMA e2= node R_PAREN )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:270:7: MAX L_PAREN e1= node COMMA e2= node R_PAREN
                    {
                    MAX58=(Token)match(input,MAX,FOLLOW_MAX_in_atomNode843); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    MAX58_tree = (Object)adaptor.create(MAX58);
                    adaptor.addChild(root_0, MAX58_tree);
                    }
                    L_PAREN59=(Token)match(input,L_PAREN,FOLLOW_L_PAREN_in_atomNode845); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    L_PAREN59_tree = (Object)adaptor.create(L_PAREN59);
                    adaptor.addChild(root_0, L_PAREN59_tree);
                    }
                    pushFollow(FOLLOW_node_in_atomNode849);
                    e1=node();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, e1.getTree());
                    COMMA60=(Token)match(input,COMMA,FOLLOW_COMMA_in_atomNode851); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    COMMA60_tree = (Object)adaptor.create(COMMA60);
                    adaptor.addChild(root_0, COMMA60_tree);
                    }
                    pushFollow(FOLLOW_node_in_atomNode855);
                    e2=node();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, e2.getTree());
                    R_PAREN61=(Token)match(input,R_PAREN,FOLLOW_R_PAREN_in_atomNode857); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    R_PAREN61_tree = (Object)adaptor.create(R_PAREN61);
                    adaptor.addChild(root_0, R_PAREN61_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.node = new MaxNode((e1!=null?e1.node:null),(e2!=null?e2.node:null));
                    }

                    }


                    }
                    break;

            }


            }

            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:272:3: ( ( SQR ) | ( CUB ) | ( DEGREES ) | ( POW a= atomNode ) )?
            int alt21=5;
            switch ( input.LA(1) ) {
                case SQR:
                    {
                    alt21=1;
                    }
                    break;
                case CUB:
                    {
                    alt21=2;
                    }
                    break;
                case DEGREES:
                    {
                    alt21=3;
                    }
                    break;
                case POW:
                    {
                    alt21=4;
                    }
                    break;
            }

            switch (alt21) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:273:5: ( SQR )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:273:5: ( SQR )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:273:6: SQR
                    {
                    SQR62=(Token)match(input,SQR,FOLLOW_SQR_in_atomNode880); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SQR62_tree = (Object)adaptor.create(SQR62);
                    adaptor.addChild(root_0, SQR62_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.node = new SquareNode(retval.node);
                    }

                    }


                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:274:5: ( CUB )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:274:5: ( CUB )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:274:6: CUB
                    {
                    CUB63=(Token)match(input,CUB,FOLLOW_CUB_in_atomNode891); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CUB63_tree = (Object)adaptor.create(CUB63);
                    adaptor.addChild(root_0, CUB63_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.node = new PowerNode(retval.node, new FixedValueNode(3.));
                    }

                    }


                    }
                    break;
                case 3 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:275:5: ( DEGREES )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:275:5: ( DEGREES )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:275:6: DEGREES
                    {
                    DEGREES64=(Token)match(input,DEGREES,FOLLOW_DEGREES_in_atomNode901); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    DEGREES64_tree = (Object)adaptor.create(DEGREES64);
                    adaptor.addChild(root_0, DEGREES64_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.node = new MultNode(retval.node, new FixedValueNode(Math.PI / 180.));
                    }

                    }


                    }
                    break;
                case 4 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:276:5: ( POW a= atomNode )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:276:5: ( POW a= atomNode )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:276:6: POW a= atomNode
                    {
                    POW65=(Token)match(input,POW,FOLLOW_POW_in_atomNode911); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    POW65_tree = (Object)adaptor.create(POW65);
                    adaptor.addChild(root_0, POW65_tree);
                    }
                    pushFollow(FOLLOW_atomNode_in_atomNode915);
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

    public static class interval_return extends ParserRuleReturnScope {
        public double lower;
        public double upper;
        public boolean incLower;
        public boolean incUpper;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "interval"
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:280:1: interval returns [double lower, double upper,boolean incLower, boolean incUpper] : ( ( L_BRACKET low= numval DOTS up= numval R_BRACKET ) | ( GREATERTHAN EQ nge= numval ) | ( LESSTHAN EQ nle= numval ) | ( GREATERTHAN ngt= numval ) | ( LESSTHAN nlt= numval ) );
    public final MLSpaceDirectParser.interval_return interval() throws RecognitionException {
        MLSpaceDirectParser.interval_return retval = new MLSpaceDirectParser.interval_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token L_BRACKET66=null;
        Token DOTS67=null;
        Token R_BRACKET68=null;
        Token GREATERTHAN69=null;
        Token EQ70=null;
        Token LESSTHAN71=null;
        Token EQ72=null;
        Token GREATERTHAN73=null;
        Token LESSTHAN74=null;
        MLSpaceDirectParser.numval_return low = null;

        MLSpaceDirectParser.numval_return up = null;

        MLSpaceDirectParser.numval_return nge = null;

        MLSpaceDirectParser.numval_return nle = null;

        MLSpaceDirectParser.numval_return ngt = null;

        MLSpaceDirectParser.numval_return nlt = null;


        Object L_BRACKET66_tree=null;
        Object DOTS67_tree=null;
        Object R_BRACKET68_tree=null;
        Object GREATERTHAN69_tree=null;
        Object EQ70_tree=null;
        Object LESSTHAN71_tree=null;
        Object EQ72_tree=null;
        Object GREATERTHAN73_tree=null;
        Object LESSTHAN74_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:280:81: ( ( L_BRACKET low= numval DOTS up= numval R_BRACKET ) | ( GREATERTHAN EQ nge= numval ) | ( LESSTHAN EQ nle= numval ) | ( GREATERTHAN ngt= numval ) | ( LESSTHAN nlt= numval ) )
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
                else if ( (LA22_2==FLOAT||LA22_2==ID) ) {
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
                else if ( (LA22_3==FLOAT||LA22_3==ID) ) {
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
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:281:3: ( L_BRACKET low= numval DOTS up= numval R_BRACKET )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:281:3: ( L_BRACKET low= numval DOTS up= numval R_BRACKET )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:281:4: L_BRACKET low= numval DOTS up= numval R_BRACKET
                    {
                    L_BRACKET66=(Token)match(input,L_BRACKET,FOLLOW_L_BRACKET_in_interval944); if (state.failed) return retval;
                    pushFollow(FOLLOW_numval_in_interval949);
                    low=numval();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, low.getTree());
                    if ( state.backtracking==0 ) {
                      retval.lower = ((low!=null?low.val:null));
                    }
                    DOTS67=(Token)match(input,DOTS,FOLLOW_DOTS_in_interval953); if (state.failed) return retval;
                    pushFollow(FOLLOW_numval_in_interval958);
                    up=numval();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, up.getTree());
                    if ( state.backtracking==0 ) {
                      retval.upper = ((up!=null?up.val:null));retval.incLower = true; retval.incUpper = true;
                    }
                    R_BRACKET68=(Token)match(input,R_BRACKET,FOLLOW_R_BRACKET_in_interval962); if (state.failed) return retval;

                    }


                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:282:5: ( GREATERTHAN EQ nge= numval )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:282:5: ( GREATERTHAN EQ nge= numval )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:282:6: GREATERTHAN EQ nge= numval
                    {
                    GREATERTHAN69=(Token)match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_interval971); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATERTHAN69_tree = (Object)adaptor.create(GREATERTHAN69);
                    adaptor.addChild(root_0, GREATERTHAN69_tree);
                    }
                    EQ70=(Token)match(input,EQ,FOLLOW_EQ_in_interval973); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQ70_tree = (Object)adaptor.create(EQ70);
                    adaptor.addChild(root_0, EQ70_tree);
                    }
                    pushFollow(FOLLOW_numval_in_interval979);
                    nge=numval();

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
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:283:5: ( LESSTHAN EQ nle= numval )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:283:5: ( LESSTHAN EQ nle= numval )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:283:6: LESSTHAN EQ nle= numval
                    {
                    LESSTHAN71=(Token)match(input,LESSTHAN,FOLLOW_LESSTHAN_in_interval989); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LESSTHAN71_tree = (Object)adaptor.create(LESSTHAN71);
                    adaptor.addChild(root_0, LESSTHAN71_tree);
                    }
                    EQ72=(Token)match(input,EQ,FOLLOW_EQ_in_interval991); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQ72_tree = (Object)adaptor.create(EQ72);
                    adaptor.addChild(root_0, EQ72_tree);
                    }
                    pushFollow(FOLLOW_numval_in_interval997);
                    nle=numval();

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
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:284:5: ( GREATERTHAN ngt= numval )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:284:5: ( GREATERTHAN ngt= numval )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:284:6: GREATERTHAN ngt= numval
                    {
                    GREATERTHAN73=(Token)match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_interval1007); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATERTHAN73_tree = (Object)adaptor.create(GREATERTHAN73);
                    adaptor.addChild(root_0, GREATERTHAN73_tree);
                    }
                    pushFollow(FOLLOW_numval_in_interval1013);
                    ngt=numval();

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
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:285:5: ( LESSTHAN nlt= numval )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:285:5: ( LESSTHAN nlt= numval )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:285:6: LESSTHAN nlt= numval
                    {
                    LESSTHAN74=(Token)match(input,LESSTHAN,FOLLOW_LESSTHAN_in_interval1023); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LESSTHAN74_tree = (Object)adaptor.create(LESSTHAN74);
                    adaptor.addChild(root_0, LESSTHAN74_tree);
                    }
                    pushFollow(FOLLOW_numval_in_interval1029);
                    nlt=numval();

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
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:288:1: range returns [double lower, double step, double upper] : (one= numexpr COLON two= numexpr ( COLON three= numexpr )? ) ;
    public final MLSpaceDirectParser.range_return range() throws RecognitionException {
        MLSpaceDirectParser.range_return retval = new MLSpaceDirectParser.range_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLON75=null;
        Token COLON76=null;
        MLSpaceDirectParser.numexpr_return one = null;

        MLSpaceDirectParser.numexpr_return two = null;

        MLSpaceDirectParser.numexpr_return three = null;


        Object COLON75_tree=null;
        Object COLON76_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:288:56: ( (one= numexpr COLON two= numexpr ( COLON three= numexpr )? ) )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:289:3: (one= numexpr COLON two= numexpr ( COLON three= numexpr )? )
            {
            root_0 = (Object)adaptor.nil();

            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:289:3: (one= numexpr COLON two= numexpr ( COLON three= numexpr )? )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:289:4: one= numexpr COLON two= numexpr ( COLON three= numexpr )?
            {
            pushFollow(FOLLOW_numexpr_in_range1052);
            one=numexpr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, one.getTree());
            COLON75=(Token)match(input,COLON,FOLLOW_COLON_in_range1054); if (state.failed) return retval;
            pushFollow(FOLLOW_numexpr_in_range1059);
            two=numexpr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, two.getTree());
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:289:35: ( COLON three= numexpr )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==COLON) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:289:36: COLON three= numexpr
                    {
                    COLON76=(Token)match(input,COLON,FOLLOW_COLON_in_range1062); if (state.failed) return retval;
                    pushFollow(FOLLOW_numexpr_in_range1067);
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
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:298:1: set returns [Set<?> set] : ( ( numset )=> numset | idset );
    public final MLSpaceDirectParser.set_return set() throws RecognitionException {
        MLSpaceDirectParser.set_return retval = new MLSpaceDirectParser.set_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        MLSpaceDirectParser.numset_return numset77 = null;

        MLSpaceDirectParser.idset_return idset78 = null;



        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:298:25: ( ( numset )=> numset | idset )
            int alt24=2;
            alt24 = dfa24.predict(input);
            switch (alt24) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:299:3: ( numset )=> numset
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_numset_in_set1098);
                    numset77=numset();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, numset77.getTree());
                    if ( state.backtracking==0 ) {
                      retval.set = (numset77!=null?numset77.set:null);
                    }

                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:300:3: idset
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_idset_in_set1104);
                    idset78=idset();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, idset78.getTree());
                    if ( state.backtracking==0 ) {
                      retval.set = (idset78!=null?idset78.set:null);
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
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:302:1: idset returns [Set<String> set] : L_BRACE (i1= ID ( COMMA i2= ID )+ )? R_BRACE ;
    public final MLSpaceDirectParser.idset_return idset() throws RecognitionException {
        MLSpaceDirectParser.idset_return retval = new MLSpaceDirectParser.idset_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token i1=null;
        Token i2=null;
        Token L_BRACE79=null;
        Token COMMA80=null;
        Token R_BRACE81=null;

        Object i1_tree=null;
        Object i2_tree=null;
        Object L_BRACE79_tree=null;
        Object COMMA80_tree=null;
        Object R_BRACE81_tree=null;

        retval.set = new NonNullSet<String>();
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:303:41: ( L_BRACE (i1= ID ( COMMA i2= ID )+ )? R_BRACE )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:304:2: L_BRACE (i1= ID ( COMMA i2= ID )+ )? R_BRACE
            {
            root_0 = (Object)adaptor.nil();

            L_BRACE79=(Token)match(input,L_BRACE,FOLLOW_L_BRACE_in_idset1123); if (state.failed) return retval;
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:304:11: (i1= ID ( COMMA i2= ID )+ )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==ID) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:304:12: i1= ID ( COMMA i2= ID )+
                    {
                    i1=(Token)match(input,ID,FOLLOW_ID_in_idset1129); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    i1_tree = (Object)adaptor.create(i1);
                    adaptor.addChild(root_0, i1_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.set.add((i1!=null?i1.getText():null));
                    }
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:304:40: ( COMMA i2= ID )+
                    int cnt25=0;
                    loop25:
                    do {
                        int alt25=2;
                        int LA25_0 = input.LA(1);

                        if ( (LA25_0==COMMA) ) {
                            alt25=1;
                        }


                        switch (alt25) {
                    	case 1 :
                    	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:304:41: COMMA i2= ID
                    	    {
                    	    COMMA80=(Token)match(input,COMMA,FOLLOW_COMMA_in_idset1134); if (state.failed) return retval;
                    	    i2=(Token)match(input,ID,FOLLOW_ID_in_idset1139); if (state.failed) return retval;
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
                    	    if ( cnt25 >= 1 ) break loop25;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(25, input);
                                throw eee;
                        }
                        cnt25++;
                    } while (true);


                    }
                    break;

            }

            R_BRACE81=(Token)match(input,R_BRACE,FOLLOW_R_BRACE_in_idset1147); if (state.failed) return retval;

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
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:306:1: numset returns [Set<Double> set] : L_BRACE (i1= numexpr ( COMMA i2= numexpr )+ )? R_BRACE ;
    public final MLSpaceDirectParser.numset_return numset() throws RecognitionException {
        MLSpaceDirectParser.numset_return retval = new MLSpaceDirectParser.numset_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token L_BRACE82=null;
        Token COMMA83=null;
        Token R_BRACE84=null;
        MLSpaceDirectParser.numexpr_return i1 = null;

        MLSpaceDirectParser.numexpr_return i2 = null;


        Object L_BRACE82_tree=null;
        Object COMMA83_tree=null;
        Object R_BRACE84_tree=null;

        retval.set = new NonNullSet<Double>();
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:307:41: ( L_BRACE (i1= numexpr ( COMMA i2= numexpr )+ )? R_BRACE )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:308:2: L_BRACE (i1= numexpr ( COMMA i2= numexpr )+ )? R_BRACE
            {
            root_0 = (Object)adaptor.nil();

            L_BRACE82=(Token)match(input,L_BRACE,FOLLOW_L_BRACE_in_numset1165); if (state.failed) return retval;
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:308:11: (i1= numexpr ( COMMA i2= numexpr )+ )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==L_BRACKET||LA28_0==L_PAREN||(LA28_0>=FLOAT && LA28_0<=MINUS)||LA28_0==ID) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:308:12: i1= numexpr ( COMMA i2= numexpr )+
                    {
                    pushFollow(FOLLOW_numexpr_in_numset1171);
                    i1=numexpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, i1.getTree());
                    if ( state.backtracking==0 ) {
                      retval.set.add((i1!=null?i1.val:null));
                    }
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:308:44: ( COMMA i2= numexpr )+
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
                    	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:308:45: COMMA i2= numexpr
                    	    {
                    	    COMMA83=(Token)match(input,COMMA,FOLLOW_COMMA_in_numset1176); if (state.failed) return retval;
                    	    pushFollow(FOLLOW_numexpr_in_numset1181);
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
                    	    if ( cnt27 >= 1 ) break loop27;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(27, input);
                                throw eee;
                        }
                        cnt27++;
                    } while (true);


                    }
                    break;

            }

            R_BRACE84=(Token)match(input,R_BRACE,FOLLOW_R_BRACE_in_numset1189); if (state.failed) return retval;

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
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:310:1: vector returns [double[] vec] : L_PAREN n1= numexpr ( COMMA n2= numexpr )+ R_PAREN ;
    public final MLSpaceDirectParser.vector_return vector() throws RecognitionException {
        MLSpaceDirectParser.vector_return retval = new MLSpaceDirectParser.vector_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token L_PAREN85=null;
        Token COMMA86=null;
        Token R_PAREN87=null;
        MLSpaceDirectParser.numexpr_return n1 = null;

        MLSpaceDirectParser.numexpr_return n2 = null;


        Object L_PAREN85_tree=null;
        Object COMMA86_tree=null;
        Object R_PAREN87_tree=null;

        List<Double> tmp = new ArrayList<Double>();
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:311:52: ( L_PAREN n1= numexpr ( COMMA n2= numexpr )+ R_PAREN )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:312:1: L_PAREN n1= numexpr ( COMMA n2= numexpr )+ R_PAREN
            {
            root_0 = (Object)adaptor.nil();

            L_PAREN85=(Token)match(input,L_PAREN,FOLLOW_L_PAREN_in_vector1206); if (state.failed) return retval;
            pushFollow(FOLLOW_numexpr_in_vector1211);
            n1=numexpr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, n1.getTree());
            if ( state.backtracking==0 ) {
              tmp.add((n1!=null?n1.val:null));
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:312:41: ( COMMA n2= numexpr )+
            int cnt29=0;
            loop29:
            do {
                int alt29=2;
                int LA29_0 = input.LA(1);

                if ( (LA29_0==COMMA) ) {
                    alt29=1;
                }


                switch (alt29) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:312:42: COMMA n2= numexpr
            	    {
            	    COMMA86=(Token)match(input,COMMA,FOLLOW_COMMA_in_vector1216); if (state.failed) return retval;
            	    pushFollow(FOLLOW_numexpr_in_vector1221);
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
            	    if ( cnt29 >= 1 ) break loop29;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(29, input);
                        throw eee;
                }
                cnt29++;
            } while (true);

            R_PAREN87=(Token)match(input,R_PAREN,FOLLOW_R_PAREN_in_vector1227); if (state.failed) return retval;
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
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:318:1: intval_or_var returns [int val] : numexpr ;
    public final MLSpaceDirectParser.intval_or_var_return intval_or_var() throws RecognitionException {
        MLSpaceDirectParser.intval_or_var_return retval = new MLSpaceDirectParser.intval_or_var_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        MLSpaceDirectParser.numexpr_return numexpr88 = null;



        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:318:33: ( numexpr )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:319:5: numexpr
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_numexpr_in_intval_or_var1246);
            numexpr88=numexpr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, numexpr88.getTree());
            if ( state.backtracking==0 ) {
              Double nval = (numexpr88!=null?numexpr88.val:null);
              	   retval.val = nval.intValue(); 
              	   if (nval - retval.val != 0.) 
              	     System.out.println("double value converted to int: " + nval);
              	  
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
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:326:1: numval returns [Double val] : ( FLOAT | {...}? => ID );
    public final MLSpaceDirectParser.numval_return numval() throws RecognitionException {
        MLSpaceDirectParser.numval_return retval = new MLSpaceDirectParser.numval_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token FLOAT89=null;
        Token ID90=null;

        Object FLOAT89_tree=null;
        Object ID90_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:326:28: ( FLOAT | {...}? => ID )
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==FLOAT) ) {
                alt30=1;
            }
            else if ( (LA30_0==ID) && ((getSingleNumValFromVar(input.LT(1).getText())!=null))) {
                alt30=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 30, 0, input);

                throw nvae;
            }
            switch (alt30) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:327:5: FLOAT
                    {
                    root_0 = (Object)adaptor.nil();

                    FLOAT89=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_numval1270); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    FLOAT89_tree = (Object)adaptor.create(FLOAT89);
                    adaptor.addChild(root_0, FLOAT89_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.val = Double.parseDouble((FLOAT89!=null?FLOAT89.getText():null));
                    }

                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:328:5: {...}? => ID
                    {
                    root_0 = (Object)adaptor.nil();

                    if ( !((getSingleNumValFromVar(input.LT(1).getText())!=null)) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "numval", "getSingleNumValFromVar(input.LT(1).getText())!=null");
                    }
                    ID90=(Token)match(input,ID,FOLLOW_ID_in_numval1281); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ID90_tree = (Object)adaptor.create(ID90);
                    adaptor.addChild(root_0, ID90_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.val = getSingleNumValFromVar((ID90!=null?ID90.getText():null));
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
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:331:1: species_defs : ( ( species_def )=> species_def ( SEMIC )? )+ ;
    public final MLSpaceDirectParser.species_defs_return species_defs() throws RecognitionException {
        MLSpaceDirectParser.species_defs_return retval = new MLSpaceDirectParser.species_defs_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMIC92=null;
        MLSpaceDirectParser.species_def_return species_def91 = null;


        Object SEMIC92_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:331:14: ( ( ( species_def )=> species_def ( SEMIC )? )+ )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:331:16: ( ( species_def )=> species_def ( SEMIC )? )+
            {
            root_0 = (Object)adaptor.nil();

            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:331:16: ( ( species_def )=> species_def ( SEMIC )? )+
            int cnt32=0;
            loop32:
            do {
                int alt32=2;
                int LA32_0 = input.LA(1);

                if ( (LA32_0==ID) ) {
                    int LA32_5 = input.LA(2);

                    if ( (synpred63_MLSpaceDirectParser()) ) {
                        alt32=1;
                    }


                }


                switch (alt32) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:331:17: ( species_def )=> species_def ( SEMIC )?
            	    {
            	    pushFollow(FOLLOW_species_def_in_species_defs1306);
            	    species_def91=species_def();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, species_def91.getTree());
            	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:331:45: ( SEMIC )?
            	    int alt31=2;
            	    int LA31_0 = input.LA(1);

            	    if ( (LA31_0==SEMIC) ) {
            	        alt31=1;
            	    }
            	    switch (alt31) {
            	        case 1 :
            	            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:0:0: SEMIC
            	            {
            	            SEMIC92=(Token)match(input,SEMIC,FOLLOW_SEMIC_in_species_defs1308); if (state.failed) return retval;
            	            if ( state.backtracking==0 ) {
            	            SEMIC92_tree = (Object)adaptor.create(SEMIC92);
            	            adaptor.addChild(root_0, SEMIC92_tree);
            	            }

            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt32 >= 1 ) break loop32;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(32, input);
                        throw eee;
                }
                cnt32++;
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
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:333:1: species_def : ID L_PAREN (spa= attributes_def )? R_PAREN ( bindingsitesdef )? ;
    public final MLSpaceDirectParser.species_def_return species_def() throws RecognitionException {
        MLSpaceDirectParser.species_def_return retval = new MLSpaceDirectParser.species_def_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID93=null;
        Token L_PAREN94=null;
        Token R_PAREN95=null;
        MLSpaceDirectParser.attributes_def_return spa = null;

        MLSpaceDirectParser.bindingsitesdef_return bindingsitesdef96 = null;


        Object ID93_tree=null;
        Object L_PAREN94_tree=null;
        Object R_PAREN95_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:333:13: ( ID L_PAREN (spa= attributes_def )? R_PAREN ( bindingsitesdef )? )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:333:15: ID L_PAREN (spa= attributes_def )? R_PAREN ( bindingsitesdef )?
            {
            root_0 = (Object)adaptor.nil();

            ID93=(Token)match(input,ID,FOLLOW_ID_in_species_def1319); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID93_tree = (Object)adaptor.create(ID93);
            adaptor.addChild(root_0, ID93_tree);
            }
            L_PAREN94=(Token)match(input,L_PAREN,FOLLOW_L_PAREN_in_species_def1321); if (state.failed) return retval;
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:333:30: (spa= attributes_def )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==ID) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:0:0: spa= attributes_def
                    {
                    pushFollow(FOLLOW_attributes_def_in_species_def1326);
                    spa=attributes_def();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, spa.getTree());

                    }
                    break;

            }

            R_PAREN95=(Token)match(input,R_PAREN,FOLLOW_R_PAREN_in_species_def1330); if (state.failed) return retval;
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:333:57: ( bindingsitesdef )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==LESSTHAN) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:0:0: bindingsitesdef
                    {
                    pushFollow(FOLLOW_bindingsitesdef_in_species_def1333);
                    bindingsitesdef96=bindingsitesdef();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, bindingsitesdef96.getTree());

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              parseTool.registerSpeciesDef((ID93!=null?ID93.getText():null),(spa!=null?spa.attMap:null),(bindingsitesdef96!=null?bindingsitesdef96.bs:null));
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

    public static class bindingsitesdef_return extends ParserRuleReturnScope {
        public BindingSites bs;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "bindingsitesdef"
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:337:1: bindingsitesdef returns [BindingSites bs] : LESSTHAN bsd1= bindingsitedef ( COMMA bsd= bindingsitedef )* GREATERTHAN ;
    public final MLSpaceDirectParser.bindingsitesdef_return bindingsitesdef() throws RecognitionException {
        MLSpaceDirectParser.bindingsitesdef_return retval = new MLSpaceDirectParser.bindingsitesdef_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LESSTHAN97=null;
        Token COMMA98=null;
        Token GREATERTHAN99=null;
        MLSpaceDirectParser.bindingsitedef_return bsd1 = null;

        MLSpaceDirectParser.bindingsitedef_return bsd = null;


        Object LESSTHAN97_tree=null;
        Object COMMA98_tree=null;
        Object GREATERTHAN99_tree=null;

        BindingSites.Builder bsb = new BindingSites.Builder();
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:338:63: ( LESSTHAN bsd1= bindingsitedef ( COMMA bsd= bindingsitedef )* GREATERTHAN )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:339:2: LESSTHAN bsd1= bindingsitedef ( COMMA bsd= bindingsitedef )* GREATERTHAN
            {
            root_0 = (Object)adaptor.nil();

            LESSTHAN97=(Token)match(input,LESSTHAN,FOLLOW_LESSTHAN_in_bindingsitesdef1355); if (state.failed) return retval;
            pushFollow(FOLLOW_bindingsitedef_in_bindingsitesdef1362);
            bsd1=bindingsitedef();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, bsd1.getTree());
            if ( state.backtracking==0 ) {
              bsb.addSite((bsd1!=null?bsd1.name:null),(bsd1!=null?bsd1.relAngle:null));
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:341:2: ( COMMA bsd= bindingsitedef )*
            loop35:
            do {
                int alt35=2;
                int LA35_0 = input.LA(1);

                if ( (LA35_0==COMMA) ) {
                    alt35=1;
                }


                switch (alt35) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:341:3: COMMA bsd= bindingsitedef
            	    {
            	    COMMA98=(Token)match(input,COMMA,FOLLOW_COMMA_in_bindingsitesdef1368); if (state.failed) return retval;
            	    pushFollow(FOLLOW_bindingsitedef_in_bindingsitesdef1373);
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
            	    break loop35;
                }
            } while (true);

            GREATERTHAN99=(Token)match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_bindingsitesdef1381); if (state.failed) return retval;
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
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:344:1: bindingsitedef returns [String name, Double relAngle] : ID COLON numexpr ;
    public final MLSpaceDirectParser.bindingsitedef_return bindingsitedef() throws RecognitionException {
        MLSpaceDirectParser.bindingsitedef_return retval = new MLSpaceDirectParser.bindingsitedef_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID100=null;
        Token COLON101=null;
        MLSpaceDirectParser.numexpr_return numexpr102 = null;


        Object ID100_tree=null;
        Object COLON101_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:344:54: ( ID COLON numexpr )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:345:3: ID COLON numexpr
            {
            root_0 = (Object)adaptor.nil();

            ID100=(Token)match(input,ID,FOLLOW_ID_in_bindingsitedef1398); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID100_tree = (Object)adaptor.create(ID100);
            adaptor.addChild(root_0, ID100_tree);
            }
            COLON101=(Token)match(input,COLON,FOLLOW_COLON_in_bindingsitedef1400); if (state.failed) return retval;
            pushFollow(FOLLOW_numexpr_in_bindingsitedef1403);
            numexpr102=numexpr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, numexpr102.getTree());
            if ( state.backtracking==0 ) {
              retval.name = (ID100!=null?ID100.getText():null); retval.relAngle = (numexpr102!=null?numexpr102.val:null);
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
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:347:1: species returns [String specName] : {...}? ID ;
    public final MLSpaceDirectParser.species_return species() throws RecognitionException {
        MLSpaceDirectParser.species_return retval = new MLSpaceDirectParser.species_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID103=null;

        Object ID103_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:347:34: ({...}? ID )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:348:1: {...}? ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !((parseTool.isValidSpecies(input.LT(1).getText()))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "species", "parseTool.isValidSpecies(input.LT(1).getText())");
            }
            ID103=(Token)match(input,ID,FOLLOW_ID_in_species1418); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID103_tree = (Object)adaptor.create(ID103);
            adaptor.addChild(root_0, ID103_tree);
            }
            if ( state.backtracking==0 ) {
              retval.specName = (ID103!=null?ID103.getText():null);
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

    public static class entities_match_return extends ParserRuleReturnScope {
        public List<RuleEntityWithBindings> list;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "entities_match"
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:350:1: entities_match[Map<String,String> valExtract] returns [List<RuleEntityWithBindings> list] : (e= entity_match ( entsep e2= entity_match )* )? ;
    public final MLSpaceDirectParser.entities_match_return entities_match(Map<String,String> valExtract) throws RecognitionException {
        MLSpaceDirectParser.entities_match_return retval = new MLSpaceDirectParser.entities_match_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        MLSpaceDirectParser.entity_match_return e = null;

        MLSpaceDirectParser.entity_match_return e2 = null;

        MLSpaceDirectParser.entsep_return entsep104 = null;



        retval.list = new ArrayList<RuleEntityWithBindings>();
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:351:58: ( (e= entity_match ( entsep e2= entity_match )* )? )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:352:2: (e= entity_match ( entsep e2= entity_match )* )?
            {
            root_0 = (Object)adaptor.nil();

            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:352:2: (e= entity_match ( entsep e2= entity_match )* )?
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==ID) ) {
                alt37=1;
            }
            switch (alt37) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:352:3: e= entity_match ( entsep e2= entity_match )*
                    {
                    pushFollow(FOLLOW_entity_match_in_entities_match1445);
                    e=entity_match();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, e.getTree());
                    if ( state.backtracking==0 ) {
                      retval.list.add((e!=null?e.ent:null));
                    }
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:352:39: ( entsep e2= entity_match )*
                    loop36:
                    do {
                        int alt36=2;
                        int LA36_0 = input.LA(1);

                        if ( (LA36_0==COMMA||LA36_0==DOT||LA36_0==PLUS) ) {
                            alt36=1;
                        }


                        switch (alt36) {
                    	case 1 :
                    	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:352:40: entsep e2= entity_match
                    	    {
                    	    pushFollow(FOLLOW_entsep_in_entities_match1450);
                    	    entsep104=entsep();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    pushFollow(FOLLOW_entity_match_in_entities_match1455);
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
                    	    break loop36;
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
    // $ANTLR end "entities_match"

    public static class entity_match_return extends ParserRuleReturnScope {
        public RuleEntityWithBindings ent;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "entity_match"
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:354:1: entity_match returns [RuleEntityWithBindings ent] : ( species )=> species ( L_PAREN (atts= attributes )? R_PAREN )? (bs= bindingsites )? ;
    public final MLSpaceDirectParser.entity_match_return entity_match() throws RecognitionException {
        MLSpaceDirectParser.entity_match_return retval = new MLSpaceDirectParser.entity_match_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token L_PAREN106=null;
        Token R_PAREN107=null;
        MLSpaceDirectParser.attributes_return atts = null;

        MLSpaceDirectParser.bindingsites_return bs = null;

        MLSpaceDirectParser.species_return species105 = null;


        Object L_PAREN106_tree=null;
        Object R_PAREN107_tree=null;

        Species spec = null;
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:355:28: ( ( species )=> species ( L_PAREN (atts= attributes )? R_PAREN )? (bs= bindingsites )? )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:356:3: ( species )=> species ( L_PAREN (atts= attributes )? R_PAREN )? (bs= bindingsites )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_species_in_entity_match1482);
            species105=species();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, species105.getTree());
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:356:22: ( L_PAREN (atts= attributes )? R_PAREN )?
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( (LA39_0==L_PAREN) ) {
                alt39=1;
            }
            switch (alt39) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:356:23: L_PAREN (atts= attributes )? R_PAREN
                    {
                    L_PAREN106=(Token)match(input,L_PAREN,FOLLOW_L_PAREN_in_entity_match1485); if (state.failed) return retval;
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:356:36: (atts= attributes )?
                    int alt38=2;
                    int LA38_0 = input.LA(1);

                    if ( (LA38_0==ID) ) {
                        alt38=1;
                    }
                    switch (alt38) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:0:0: atts= attributes
                            {
                            pushFollow(FOLLOW_attributes_in_entity_match1490);
                            atts=attributes();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, atts.getTree());

                            }
                            break;

                    }

                    R_PAREN107=(Token)match(input,R_PAREN,FOLLOW_R_PAREN_in_entity_match1493); if (state.failed) return retval;

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              parseTool.checkEntityDefPlausibility((species105!=null?species105.specName:null),(atts!=null?atts.attMap:null));
                  spec = parseTool.getSpeciesForName((species105!=null?species105.specName:null));
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:359:5: (bs= bindingsites )?
            int alt40=2;
            int LA40_0 = input.LA(1);

            if ( (LA40_0==LESSTHAN) ) {
                alt40=1;
            }
            switch (alt40) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:0:0: bs= bindingsites
                    {
                    pushFollow(FOLLOW_bindingsites_in_entity_match1507);
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
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:365:1: bindingsites returns [Map<String,RuleEntityWithBindings> map] : LESSTHAN bsm1= bindingsite ( COMMA bsm= bindingsite )* GREATERTHAN ;
    public final MLSpaceDirectParser.bindingsites_return bindingsites() throws RecognitionException {
        MLSpaceDirectParser.bindingsites_return retval = new MLSpaceDirectParser.bindingsites_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LESSTHAN108=null;
        Token COMMA109=null;
        Token GREATERTHAN110=null;
        MLSpaceDirectParser.bindingsite_return bsm1 = null;

        MLSpaceDirectParser.bindingsite_return bsm = null;


        Object LESSTHAN108_tree=null;
        Object COMMA109_tree=null;
        Object GREATERTHAN110_tree=null;

        retval.map = new NonNullMap<String,RuleEntityWithBindings>(false,true);
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:366:74: ( LESSTHAN bsm1= bindingsite ( COMMA bsm= bindingsite )* GREATERTHAN )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:367:2: LESSTHAN bsm1= bindingsite ( COMMA bsm= bindingsite )* GREATERTHAN
            {
            root_0 = (Object)adaptor.nil();

            LESSTHAN108=(Token)match(input,LESSTHAN,FOLLOW_LESSTHAN_in_bindingsites1531); if (state.failed) return retval;
            pushFollow(FOLLOW_bindingsite_in_bindingsites1538);
            bsm1=bindingsite();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, bsm1.getTree());
            if ( state.backtracking==0 ) {
              retval.map.put((bsm1!=null?bsm1.name:null),(bsm1!=null?bsm1.ent:null));
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:369:2: ( COMMA bsm= bindingsite )*
            loop41:
            do {
                int alt41=2;
                int LA41_0 = input.LA(1);

                if ( (LA41_0==COMMA) ) {
                    alt41=1;
                }


                switch (alt41) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:369:3: COMMA bsm= bindingsite
            	    {
            	    COMMA109=(Token)match(input,COMMA,FOLLOW_COMMA_in_bindingsites1544); if (state.failed) return retval;
            	    pushFollow(FOLLOW_bindingsite_in_bindingsites1549);
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
            	    break loop41;
                }
            } while (true);

            GREATERTHAN110=(Token)match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_bindingsites1557); if (state.failed) return retval;

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
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:372:1: bindingsite returns [String name, RuleEntityWithBindings ent] : ID COLON (e= entity_match | FREE | OCC ) ;
    public final MLSpaceDirectParser.bindingsite_return bindingsite() throws RecognitionException {
        MLSpaceDirectParser.bindingsite_return retval = new MLSpaceDirectParser.bindingsite_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID111=null;
        Token COLON112=null;
        Token FREE113=null;
        Token OCC114=null;
        MLSpaceDirectParser.entity_match_return e = null;


        Object ID111_tree=null;
        Object COLON112_tree=null;
        Object FREE113_tree=null;
        Object OCC114_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:372:62: ( ID COLON (e= entity_match | FREE | OCC ) )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:373:3: ID COLON (e= entity_match | FREE | OCC )
            {
            root_0 = (Object)adaptor.nil();

            ID111=(Token)match(input,ID,FOLLOW_ID_in_bindingsite1572); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID111_tree = (Object)adaptor.create(ID111);
            adaptor.addChild(root_0, ID111_tree);
            }
            COLON112=(Token)match(input,COLON,FOLLOW_COLON_in_bindingsite1574); if (state.failed) return retval;
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:374:3: (e= entity_match | FREE | OCC )
            int alt42=3;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt42=1;
                }
                break;
            case FREE:
                {
                alt42=2;
                }
                break;
            case OCC:
                {
                alt42=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 42, 0, input);

                throw nvae;
            }

            switch (alt42) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:374:4: e= entity_match
                    {
                    pushFollow(FOLLOW_entity_match_in_bindingsite1583);
                    e=entity_match();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, e.getTree());
                    if ( state.backtracking==0 ) {
                      retval.name = (ID111!=null?ID111.getText():null); retval.ent = (e!=null?e.ent:null);
                    }

                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:375:4: FREE
                    {
                    FREE113=(Token)match(input,FREE,FOLLOW_FREE_in_bindingsite1591); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    FREE113_tree = (Object)adaptor.create(FREE113);
                    adaptor.addChild(root_0, FREE113_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.name = (ID111!=null?ID111.getText():null); retval.ent = null;
                    }

                    }
                    break;
                case 3 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:376:4: OCC
                    {
                    OCC114=(Token)match(input,OCC,FOLLOW_OCC_in_bindingsite1598); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    OCC114_tree = (Object)adaptor.create(OCC114);
                    adaptor.addChild(root_0, OCC114_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.name = (ID111!=null?ID111.getText():null); retval.ent = new AllMatchingRuleEntity(true);
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
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:379:1: entities_result[boolean ignore] returns [List<ModEntity> list] : (e= entity_result[ignore] ( entsep e2= entity_result[ignore] )* )? ;
    public final MLSpaceDirectParser.entities_result_return entities_result(boolean ignore) throws RecognitionException {
        MLSpaceDirectParser.entities_result_return retval = new MLSpaceDirectParser.entities_result_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        MLSpaceDirectParser.entity_result_return e = null;

        MLSpaceDirectParser.entity_result_return e2 = null;

        MLSpaceDirectParser.entsep_return entsep115 = null;



        retval.list = new ArrayList<>();
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:380:36: ( (e= entity_result[ignore] ( entsep e2= entity_result[ignore] )* )? )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:381:2: (e= entity_result[ignore] ( entsep e2= entity_result[ignore] )* )?
            {
            root_0 = (Object)adaptor.nil();

            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:381:2: (e= entity_result[ignore] ( entsep e2= entity_result[ignore] )* )?
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==ID) ) {
                alt44=1;
            }
            switch (alt44) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:381:3: e= entity_result[ignore] ( entsep e2= entity_result[ignore] )*
                    {
                    pushFollow(FOLLOW_entity_result_in_entities_result1630);
                    e=entity_result(ignore);

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, e.getTree());
                    if ( state.backtracking==0 ) {
                      retval.list.add((e!=null?e.ent:null));
                    }
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:381:48: ( entsep e2= entity_result[ignore] )*
                    loop43:
                    do {
                        int alt43=2;
                        int LA43_0 = input.LA(1);

                        if ( (LA43_0==COMMA||LA43_0==DOT||LA43_0==PLUS) ) {
                            alt43=1;
                        }


                        switch (alt43) {
                    	case 1 :
                    	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:381:49: entsep e2= entity_result[ignore]
                    	    {
                    	    pushFollow(FOLLOW_entsep_in_entities_result1636);
                    	    entsep115=entsep();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    pushFollow(FOLLOW_entity_result_in_entities_result1641);
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
                    	    break loop43;
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
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:383:1: entity_result[boolean ignore] returns [ModEntity ent] : species ( L_PAREN (name= ID m= valmod ( COMMA name= ID m= valmod )* )? R_PAREN )? (ba= bindingactions )? ;
    public final MLSpaceDirectParser.entity_result_return entity_result(boolean ignore) throws RecognitionException {
        MLSpaceDirectParser.entity_result_return retval = new MLSpaceDirectParser.entity_result_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token name=null;
        Token L_PAREN117=null;
        Token COMMA118=null;
        Token R_PAREN119=null;
        MLSpaceDirectParser.valmod_return m = null;

        MLSpaceDirectParser.bindingactions_return ba = null;

        MLSpaceDirectParser.species_return species116 = null;


        Object name_tree=null;
        Object L_PAREN117_tree=null;
        Object COMMA118_tree=null;
        Object R_PAREN119_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:383:54: ( species ( L_PAREN (name= ID m= valmod ( COMMA name= ID m= valmod )* )? R_PAREN )? (ba= bindingactions )? )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:384:3: species ( L_PAREN (name= ID m= valmod ( COMMA name= ID m= valmod )* )? R_PAREN )? (ba= bindingactions )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_species_in_entity_result1662);
            species116=species();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, species116.getTree());
            if ( state.backtracking==0 ) {
              retval.ent = new ModEntity((species116!=null?species116.specName:null));
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:385:3: ( L_PAREN (name= ID m= valmod ( COMMA name= ID m= valmod )* )? R_PAREN )?
            int alt47=2;
            int LA47_0 = input.LA(1);

            if ( (LA47_0==L_PAREN) ) {
                alt47=1;
            }
            switch (alt47) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:385:4: L_PAREN (name= ID m= valmod ( COMMA name= ID m= valmod )* )? R_PAREN
                    {
                    L_PAREN117=(Token)match(input,L_PAREN,FOLLOW_L_PAREN_in_entity_result1670); if (state.failed) return retval;
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:385:13: (name= ID m= valmod ( COMMA name= ID m= valmod )* )?
                    int alt46=2;
                    int LA46_0 = input.LA(1);

                    if ( (LA46_0==ID) ) {
                        alt46=1;
                    }
                    switch (alt46) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:385:14: name= ID m= valmod ( COMMA name= ID m= valmod )*
                            {
                            name=(Token)match(input,ID,FOLLOW_ID_in_entity_result1676); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            name_tree = (Object)adaptor.create(name);
                            adaptor.addChild(root_0, name_tree);
                            }
                            pushFollow(FOLLOW_valmod_in_entity_result1680);
                            m=valmod();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, m.getTree());
                            if ( state.backtracking==0 ) {
                              retval.ent.addAttMod((name!=null?name.getText():null),(m!=null?m.val:null));
                            }
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:386:7: ( COMMA name= ID m= valmod )*
                            loop45:
                            do {
                                int alt45=2;
                                int LA45_0 = input.LA(1);

                                if ( (LA45_0==COMMA) ) {
                                    alt45=1;
                                }


                                switch (alt45) {
                            	case 1 :
                            	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:386:8: COMMA name= ID m= valmod
                            	    {
                            	    COMMA118=(Token)match(input,COMMA,FOLLOW_COMMA_in_entity_result1692); if (state.failed) return retval;
                            	    if ( state.backtracking==0 ) {
                            	    COMMA118_tree = (Object)adaptor.create(COMMA118);
                            	    adaptor.addChild(root_0, COMMA118_tree);
                            	    }
                            	    name=(Token)match(input,ID,FOLLOW_ID_in_entity_result1696); if (state.failed) return retval;
                            	    if ( state.backtracking==0 ) {
                            	    name_tree = (Object)adaptor.create(name);
                            	    adaptor.addChild(root_0, name_tree);
                            	    }
                            	    pushFollow(FOLLOW_valmod_in_entity_result1700);
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
                            	    break loop45;
                                }
                            } while (true);


                            }
                            break;

                    }

                    R_PAREN119=(Token)match(input,R_PAREN,FOLLOW_R_PAREN_in_entity_result1712); if (state.failed) return retval;

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              if (!ignore) parseTool.checkEntityDefPlausibility((species116!=null?species116.specName:null),retval.ent.getAttributes());
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:389:3: (ba= bindingactions )?
            int alt48=2;
            int LA48_0 = input.LA(1);

            if ( (LA48_0==LESSTHAN) ) {
                alt48=1;
            }
            switch (alt48) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:389:4: ba= bindingactions
                    {
                    pushFollow(FOLLOW_bindingactions_in_entity_result1727);
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
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:392:1: valmod returns [ValueModifier val] : ( ( op EQ ( (ne= numexpr ) | (nn= node ) ) ) | ( ( EQ | BECOMES ) n= node ) | ( COLON v= valset_or_const ) );
    public final MLSpaceDirectParser.valmod_return valmod() throws RecognitionException {
        MLSpaceDirectParser.valmod_return retval = new MLSpaceDirectParser.valmod_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQ121=null;
        Token set122=null;
        Token COLON123=null;
        MLSpaceDirectParser.numexpr_return ne = null;

        MLSpaceDirectParser.node_return nn = null;

        MLSpaceDirectParser.node_return n = null;

        MLSpaceDirectParser.valset_or_const_return v = null;

        MLSpaceDirectParser.op_return op120 = null;


        Object EQ121_tree=null;
        Object set122_tree=null;
        Object COLON123_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:392:35: ( ( op EQ ( (ne= numexpr ) | (nn= node ) ) ) | ( ( EQ | BECOMES ) n= node ) | ( COLON v= valset_or_const ) )
            int alt50=3;
            switch ( input.LA(1) ) {
            case PLUS:
            case MINUS:
            case TIMES:
            case DIV:
                {
                alt50=1;
                }
                break;
            case EQ:
            case BECOMES:
                {
                alt50=2;
                }
                break;
            case COLON:
                {
                alt50=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 50, 0, input);

                throw nvae;
            }

            switch (alt50) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:393:4: ( op EQ ( (ne= numexpr ) | (nn= node ) ) )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:393:4: ( op EQ ( (ne= numexpr ) | (nn= node ) ) )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:393:5: op EQ ( (ne= numexpr ) | (nn= node ) )
                    {
                    pushFollow(FOLLOW_op_in_valmod1747);
                    op120=op();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, op120.getTree());
                    EQ121=(Token)match(input,EQ,FOLLOW_EQ_in_valmod1749); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQ121_tree = (Object)adaptor.create(EQ121);
                    adaptor.addChild(root_0, EQ121_tree);
                    }
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:393:11: ( (ne= numexpr ) | (nn= node ) )
                    int alt49=2;
                    alt49 = dfa49.predict(input);
                    switch (alt49) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:393:12: (ne= numexpr )
                            {
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:393:12: (ne= numexpr )
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:393:13: ne= numexpr
                            {
                            pushFollow(FOLLOW_numexpr_in_valmod1755);
                            ne=numexpr();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, ne.getTree());
                            if ( state.backtracking==0 ) {
                              retval.val = new ValueModifier.SimpleValueModifier((op120!=null?input.toString(op120.start,op120.stop):null),(ne!=null?ne.val:null));
                            }

                            }


                            }
                            break;
                        case 2 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:394:12: (nn= node )
                            {
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:394:12: (nn= node )
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:394:13: nn= node
                            {
                            pushFollow(FOLLOW_node_in_valmod1774);
                            nn=node();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, nn.getTree());
                            if ( state.backtracking==0 ) {
                              retval.val = new ValueModifier.TreeValueModifier((op120!=null?input.toString(op120.start,op120.stop):null),(nn!=null?nn.node:null));
                            }

                            }


                            }
                            break;

                    }


                    }


                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:395:4: ( ( EQ | BECOMES ) n= node )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:395:4: ( ( EQ | BECOMES ) n= node )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:395:5: ( EQ | BECOMES ) n= node
                    {
                    set122=(Token)input.LT(1);
                    if ( input.LA(1)==EQ||input.LA(1)==BECOMES ) {
                        input.consume();
                        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set122));
                        state.errorRecovery=false;state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    pushFollow(FOLLOW_node_in_valmod1793);
                    n=node();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, n.getTree());
                    if ( state.backtracking==0 ) {
                      retval.val =new TreeValueModifier((n!=null?n.node:null));
                    }

                    }


                    }
                    break;
                case 3 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:396:4: ( COLON v= valset_or_const )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:396:4: ( COLON v= valset_or_const )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:396:5: COLON v= valset_or_const
                    {
                    COLON123=(Token)match(input,COLON,FOLLOW_COLON_in_valmod1802); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    COLON123_tree = (Object)adaptor.create(COLON123);
                    adaptor.addChild(root_0, COLON123_tree);
                    }
                    pushFollow(FOLLOW_valset_or_const_in_valmod1806);
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
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:398:1: op : ( PLUS | MINUS | TIMES | DIV );
    public final MLSpaceDirectParser.op_return op() throws RecognitionException {
        MLSpaceDirectParser.op_return retval = new MLSpaceDirectParser.op_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set124=null;

        Object set124_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:398:3: ( PLUS | MINUS | TIMES | DIV )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:
            {
            root_0 = (Object)adaptor.nil();

            set124=(Token)input.LT(1);
            if ( (input.LA(1)>=PLUS && input.LA(1)<=DIV) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set124));
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
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:400:1: bindingactions returns [Map<String,BindingAction> map] : LESSTHAN bm= bindingaction ( COMMA bm2= bindingaction )* GREATERTHAN ;
    public final MLSpaceDirectParser.bindingactions_return bindingactions() throws RecognitionException {
        MLSpaceDirectParser.bindingactions_return retval = new MLSpaceDirectParser.bindingactions_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LESSTHAN125=null;
        Token COMMA126=null;
        Token GREATERTHAN127=null;
        MLSpaceDirectParser.bindingaction_return bm = null;

        MLSpaceDirectParser.bindingaction_return bm2 = null;


        Object LESSTHAN125_tree=null;
        Object COMMA126_tree=null;
        Object GREATERTHAN127_tree=null;

        retval.map = new NonNullMap<String,BindingAction>();
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:401:55: ( LESSTHAN bm= bindingaction ( COMMA bm2= bindingaction )* GREATERTHAN )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:402:2: LESSTHAN bm= bindingaction ( COMMA bm2= bindingaction )* GREATERTHAN
            {
            root_0 = (Object)adaptor.nil();

            LESSTHAN125=(Token)match(input,LESSTHAN,FOLLOW_LESSTHAN_in_bindingactions1845); if (state.failed) return retval;
            pushFollow(FOLLOW_bindingaction_in_bindingactions1852);
            bm=bindingaction();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, bm.getTree());
            if ( state.backtracking==0 ) {
              retval.map.put((bm!=null?bm.name:null),(bm!=null?bm.action:null));
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:404:2: ( COMMA bm2= bindingaction )*
            loop51:
            do {
                int alt51=2;
                int LA51_0 = input.LA(1);

                if ( (LA51_0==COMMA) ) {
                    alt51=1;
                }


                switch (alt51) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:404:3: COMMA bm2= bindingaction
            	    {
            	    COMMA126=(Token)match(input,COMMA,FOLLOW_COMMA_in_bindingactions1858); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    COMMA126_tree = (Object)adaptor.create(COMMA126);
            	    adaptor.addChild(root_0, COMMA126_tree);
            	    }
            	    pushFollow(FOLLOW_bindingaction_in_bindingactions1862);
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
            	    break loop51;
                }
            } while (true);

            GREATERTHAN127=(Token)match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_bindingactions1869); if (state.failed) return retval;

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
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:407:1: bindingaction returns [String name,BindingAction action] : ID COLON ( BIND | RELEASE | REPLACE ) ;
    public final MLSpaceDirectParser.bindingaction_return bindingaction() throws RecognitionException {
        MLSpaceDirectParser.bindingaction_return retval = new MLSpaceDirectParser.bindingaction_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID128=null;
        Token COLON129=null;
        Token BIND130=null;
        Token RELEASE131=null;
        Token REPLACE132=null;

        Object ID128_tree=null;
        Object COLON129_tree=null;
        Object BIND130_tree=null;
        Object RELEASE131_tree=null;
        Object REPLACE132_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:407:57: ( ID COLON ( BIND | RELEASE | REPLACE ) )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:408:3: ID COLON ( BIND | RELEASE | REPLACE )
            {
            root_0 = (Object)adaptor.nil();

            ID128=(Token)match(input,ID,FOLLOW_ID_in_bindingaction1884); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID128_tree = (Object)adaptor.create(ID128);
            adaptor.addChild(root_0, ID128_tree);
            }
            COLON129=(Token)match(input,COLON,FOLLOW_COLON_in_bindingaction1886); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
              retval.name = (ID128!=null?ID128.getText():null);
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:409:3: ( BIND | RELEASE | REPLACE )
            int alt52=3;
            switch ( input.LA(1) ) {
            case BIND:
                {
                alt52=1;
                }
                break;
            case RELEASE:
                {
                alt52=2;
                }
                break;
            case REPLACE:
                {
                alt52=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 52, 0, input);

                throw nvae;
            }

            switch (alt52) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:409:5: BIND
                    {
                    BIND130=(Token)match(input,BIND,FOLLOW_BIND_in_bindingaction1895); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    BIND130_tree = (Object)adaptor.create(BIND130);
                    adaptor.addChild(root_0, BIND130_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.action = BindingAction.BIND;
                    }

                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:410:5: RELEASE
                    {
                    RELEASE131=(Token)match(input,RELEASE,FOLLOW_RELEASE_in_bindingaction1904); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RELEASE131_tree = (Object)adaptor.create(RELEASE131);
                    adaptor.addChild(root_0, RELEASE131_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.action = BindingAction.RELEASE;
                    }

                    }
                    break;
                case 3 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:411:5: REPLACE
                    {
                    REPLACE132=(Token)match(input,REPLACE,FOLLOW_REPLACE_in_bindingaction1912); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    REPLACE132_tree = (Object)adaptor.create(REPLACE132);
                    adaptor.addChild(root_0, REPLACE132_tree);
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

    public static class rules_return extends ParserRuleReturnScope {
        public RuleCollection rules;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rules"
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:414:1: rules returns [RuleCollection rules] : ( rule ( SEMIC )? {...}?)+ ;
    public final MLSpaceDirectParser.rules_return rules() throws RecognitionException {
        MLSpaceDirectParser.rules_return retval = new MLSpaceDirectParser.rules_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMIC134=null;
        MLSpaceDirectParser.rule_return rule133 = null;


        Object SEMIC134_tree=null;

        retval.rules = new RuleCollection();
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:415:38: ( ( rule ( SEMIC )? {...}?)+ )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:416:3: ( rule ( SEMIC )? {...}?)+
            {
            root_0 = (Object)adaptor.nil();

            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:416:3: ( rule ( SEMIC )? {...}?)+
            int cnt54=0;
            loop54:
            do {
                int alt54=2;
                int LA54_0 = input.LA(1);

                if ( (LA54_0==ID) ) {
                    alt54=1;
                }


                switch (alt54) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:416:4: rule ( SEMIC )? {...}?
            	    {
            	    pushFollow(FOLLOW_rule_in_rules1939);
            	    rule133=rule();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, rule133.getTree());
            	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:416:9: ( SEMIC )?
            	    int alt53=2;
            	    int LA53_0 = input.LA(1);

            	    if ( (LA53_0==SEMIC) ) {
            	        int LA53_1 = input.LA(2);

            	        if ( (synpred93_MLSpaceDirectParser()) ) {
            	            alt53=1;
            	        }
            	    }
            	    switch (alt53) {
            	        case 1 :
            	            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:0:0: SEMIC
            	            {
            	            SEMIC134=(Token)match(input,SEMIC,FOLLOW_SEMIC_in_rules1941); if (state.failed) return retval;
            	            if ( state.backtracking==0 ) {
            	            SEMIC134_tree = (Object)adaptor.create(SEMIC134);
            	            adaptor.addChild(root_0, SEMIC134_tree);
            	            }

            	            }
            	            break;

            	    }

            	    if ( !(((rule133!=null?rule133.rv:null) != null)) ) {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        throw new FailedPredicateException(input, "rules", "$rule.rv != null");
            	    }
            	    if ( state.backtracking==0 ) {
            	      retval.rules.add((rule133!=null?rule133.rv:null));
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt54 >= 1 ) break loop54;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(54, input);
                        throw eee;
                }
                cnt54++;
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
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:418:1: rule returns [MLSpaceRule rv] : lhs= rule_left_hand_side ARROW (rhs= rule_right_hand_side )? AT n= node ;
    public final MLSpaceDirectParser.rule_return rule() throws RecognitionException {
        MLSpaceDirectParser.rule_return retval = new MLSpaceDirectParser.rule_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ARROW135=null;
        Token AT136=null;
        MLSpaceDirectParser.rule_left_hand_side_return lhs = null;

        MLSpaceDirectParser.rule_right_hand_side_return rhs = null;

        MLSpaceDirectParser.node_return n = null;


        Object ARROW135_tree=null;
        Object AT136_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:418:30: (lhs= rule_left_hand_side ARROW (rhs= rule_right_hand_side )? AT n= node )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:419:3: lhs= rule_left_hand_side ARROW (rhs= rule_right_hand_side )? AT n= node
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_rule_left_hand_side_in_rule1965);
            lhs=rule_left_hand_side();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, lhs.getTree());
            ARROW135=(Token)match(input,ARROW,FOLLOW_ARROW_in_rule1969); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ARROW135_tree = (Object)adaptor.create(ARROW135);
            adaptor.addChild(root_0, ARROW135_tree);
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:421:6: (rhs= rule_right_hand_side )?
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==ID) ) {
                alt55=1;
            }
            switch (alt55) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:0:0: rhs= rule_right_hand_side
                    {
                    pushFollow(FOLLOW_rule_right_hand_side_in_rule1976);
                    rhs=rule_right_hand_side();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rhs.getTree());

                    }
                    break;

            }

            AT136=(Token)match(input,AT,FOLLOW_AT_in_rule1983); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            AT136_tree = (Object)adaptor.create(AT136);
            adaptor.addChild(root_0, AT136_tree);
            }
            pushFollow(FOLLOW_node_in_rule1987);
            n=node();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, n.getTree());
            if ( state.backtracking==0 ) {
              retval.rv =parseTool.parseRule(null,(lhs!=null?lhs.lhs:null),(rhs!=null?rhs.context:null),(rhs!=null?rhs.rhs:null),(n!=null?n.node:null),""); /* rule name not supported here */
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

    public static class rule_left_hand_side_return extends ParserRuleReturnScope {
        public RuleSide lhs;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rule_left_hand_side"
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:425:1: rule_left_hand_side returns [RuleSide lhs] : e1= entity_match ( ( L_BRACKET | entsep ) (e2= entity_match ( entsep en= entity_match )* )? ({...}? R_BRACKET | ) )? ;
    public final MLSpaceDirectParser.rule_left_hand_side_return rule_left_hand_side() throws RecognitionException {
        MLSpaceDirectParser.rule_left_hand_side_return retval = new MLSpaceDirectParser.rule_left_hand_side_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token L_BRACKET137=null;
        Token R_BRACKET140=null;
        MLSpaceDirectParser.entity_match_return e1 = null;

        MLSpaceDirectParser.entity_match_return e2 = null;

        MLSpaceDirectParser.entity_match_return en = null;

        MLSpaceDirectParser.entsep_return entsep138 = null;

        MLSpaceDirectParser.entsep_return entsep139 = null;


        Object L_BRACKET137_tree=null;
        Object R_BRACKET140_tree=null;

        RuleSide.Builder lhsBuilder = new RuleSide.Builder();
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:427:35: (e1= entity_match ( ( L_BRACKET | entsep ) (e2= entity_match ( entsep en= entity_match )* )? ({...}? R_BRACKET | ) )? )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:428:3: e1= entity_match ( ( L_BRACKET | entsep ) (e2= entity_match ( entsep en= entity_match )* )? ({...}? R_BRACKET | ) )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_entity_match_in_rule_left_hand_side2018);
            e1=entity_match();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, e1.getTree());
            if ( state.backtracking==0 ) {
              lhsBuilder.addEntity((e1!=null?e1.ent:null));
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:429:3: ( ( L_BRACKET | entsep ) (e2= entity_match ( entsep en= entity_match )* )? ({...}? R_BRACKET | ) )?
            int alt60=2;
            int LA60_0 = input.LA(1);

            if ( (LA60_0==L_BRACKET||LA60_0==COMMA||LA60_0==DOT||LA60_0==PLUS) ) {
                alt60=1;
            }
            switch (alt60) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:429:4: ( L_BRACKET | entsep ) (e2= entity_match ( entsep en= entity_match )* )? ({...}? R_BRACKET | )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:429:4: ( L_BRACKET | entsep )
                    int alt56=2;
                    int LA56_0 = input.LA(1);

                    if ( (LA56_0==L_BRACKET) ) {
                        alt56=1;
                    }
                    else if ( (LA56_0==COMMA||LA56_0==DOT||LA56_0==PLUS) ) {
                        alt56=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 56, 0, input);

                        throw nvae;
                    }
                    switch (alt56) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:429:5: L_BRACKET
                            {
                            L_BRACKET137=(Token)match(input,L_BRACKET,FOLLOW_L_BRACKET_in_rule_left_hand_side2026); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            L_BRACKET137_tree = (Object)adaptor.create(L_BRACKET137);
                            adaptor.addChild(root_0, L_BRACKET137_tree);
                            }
                            if ( state.backtracking==0 ) {
                              lhsBuilder.makeLastContext();
                            }

                            }
                            break;
                        case 2 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:430:5: entsep
                            {
                            pushFollow(FOLLOW_entsep_in_rule_left_hand_side2034);
                            entsep138=entsep();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, entsep138.getTree());

                            }
                            break;

                    }

                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:431:4: (e2= entity_match ( entsep en= entity_match )* )?
                    int alt58=2;
                    int LA58_0 = input.LA(1);

                    if ( (LA58_0==ID) ) {
                        alt58=1;
                    }
                    switch (alt58) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:431:5: e2= entity_match ( entsep en= entity_match )*
                            {
                            pushFollow(FOLLOW_entity_match_in_rule_left_hand_side2043);
                            e2=entity_match();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, e2.getTree());
                            if ( state.backtracking==0 ) {
                              lhsBuilder.addEntity((e2!=null?e2.ent:null));
                            }
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:432:5: ( entsep en= entity_match )*
                            loop57:
                            do {
                                int alt57=2;
                                int LA57_0 = input.LA(1);

                                if ( (LA57_0==COMMA||LA57_0==DOT||LA57_0==PLUS) ) {
                                    alt57=1;
                                }


                                switch (alt57) {
                            	case 1 :
                            	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:432:6: entsep en= entity_match
                            	    {
                            	    pushFollow(FOLLOW_entsep_in_rule_left_hand_side2052);
                            	    entsep139=entsep();

                            	    state._fsp--;
                            	    if (state.failed) return retval;
                            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, entsep139.getTree());
                            	    pushFollow(FOLLOW_entity_match_in_rule_left_hand_side2056);
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
                            	    break loop57;
                                }
                            } while (true);


                            }
                            break;

                    }

                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:433:4: ({...}? R_BRACKET | )
                    int alt59=2;
                    int LA59_0 = input.LA(1);

                    if ( (LA59_0==R_BRACKET) ) {
                        alt59=1;
                    }
                    else if ( (LA59_0==ARROW) ) {
                        alt59=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 59, 0, input);

                        throw nvae;
                    }
                    switch (alt59) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:433:5: {...}? R_BRACKET
                            {
                            if ( !((lhsBuilder.isContextSet())) ) {
                                if (state.backtracking>0) {state.failed=true; return retval;}
                                throw new FailedPredicateException(input, "rule_left_hand_side", "lhsBuilder.isContextSet()");
                            }
                            R_BRACKET140=(Token)match(input,R_BRACKET,FOLLOW_R_BRACKET_in_rule_left_hand_side2070); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            R_BRACKET140_tree = (Object)adaptor.create(R_BRACKET140);
                            adaptor.addChild(root_0, R_BRACKET140_tree);
                            }

                            }
                            break;
                        case 2 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:433:46: 
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
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:436:1: rule_right_hand_side returns [ModEntity context, List<ModEntity> rhs] : e1= entity_result[false] ( ( L_BRACKET | entsep ) (e2= entity_result[false] ( entsep en= entity_result[false] )* )? ({...}? R_BRACKET | ) )? ;
    public final MLSpaceDirectParser.rule_right_hand_side_return rule_right_hand_side() throws RecognitionException {
        MLSpaceDirectParser.rule_right_hand_side_return retval = new MLSpaceDirectParser.rule_right_hand_side_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token L_BRACKET141=null;
        Token R_BRACKET144=null;
        MLSpaceDirectParser.entity_result_return e1 = null;

        MLSpaceDirectParser.entity_result_return e2 = null;

        MLSpaceDirectParser.entity_result_return en = null;

        MLSpaceDirectParser.entsep_return entsep142 = null;

        MLSpaceDirectParser.entsep_return entsep143 = null;


        Object L_BRACKET141_tree=null;
        Object R_BRACKET144_tree=null;

         retval.rhs = new ArrayList<>();
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:437:34: (e1= entity_result[false] ( ( L_BRACKET | entsep ) (e2= entity_result[false] ( entsep en= entity_result[false] )* )? ({...}? R_BRACKET | ) )? )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:438:3: e1= entity_result[false] ( ( L_BRACKET | entsep ) (e2= entity_result[false] ( entsep en= entity_result[false] )* )? ({...}? R_BRACKET | ) )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_entity_result_in_rule_right_hand_side2097);
            e1=entity_result(false);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, e1.getTree());
            if ( state.backtracking==0 ) {
              retval.rhs.add((e1!=null?e1.ent:null));
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:439:3: ( ( L_BRACKET | entsep ) (e2= entity_result[false] ( entsep en= entity_result[false] )* )? ({...}? R_BRACKET | ) )?
            int alt65=2;
            int LA65_0 = input.LA(1);

            if ( (LA65_0==L_BRACKET||LA65_0==COMMA||LA65_0==DOT||LA65_0==PLUS) ) {
                alt65=1;
            }
            switch (alt65) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:439:4: ( L_BRACKET | entsep ) (e2= entity_result[false] ( entsep en= entity_result[false] )* )? ({...}? R_BRACKET | )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:439:4: ( L_BRACKET | entsep )
                    int alt61=2;
                    int LA61_0 = input.LA(1);

                    if ( (LA61_0==L_BRACKET) ) {
                        alt61=1;
                    }
                    else if ( (LA61_0==COMMA||LA61_0==DOT||LA61_0==PLUS) ) {
                        alt61=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 61, 0, input);

                        throw nvae;
                    }
                    switch (alt61) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:439:5: L_BRACKET
                            {
                            L_BRACKET141=(Token)match(input,L_BRACKET,FOLLOW_L_BRACKET_in_rule_right_hand_side2106); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            L_BRACKET141_tree = (Object)adaptor.create(L_BRACKET141);
                            adaptor.addChild(root_0, L_BRACKET141_tree);
                            }
                            if ( state.backtracking==0 ) {
                              retval.context = retval.rhs.remove(0);
                            }

                            }
                            break;
                        case 2 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:439:46: entsep
                            {
                            pushFollow(FOLLOW_entsep_in_rule_right_hand_side2112);
                            entsep142=entsep();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, entsep142.getTree());

                            }
                            break;

                    }

                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:440:4: (e2= entity_result[false] ( entsep en= entity_result[false] )* )?
                    int alt63=2;
                    int LA63_0 = input.LA(1);

                    if ( (LA63_0==ID) ) {
                        alt63=1;
                    }
                    switch (alt63) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:440:5: e2= entity_result[false] ( entsep en= entity_result[false] )*
                            {
                            pushFollow(FOLLOW_entity_result_in_rule_right_hand_side2121);
                            e2=entity_result(false);

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, e2.getTree());
                            if ( state.backtracking==0 ) {
                              retval.rhs.add((e2!=null?e2.ent:null));
                            }
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:441:4: ( entsep en= entity_result[false] )*
                            loop62:
                            do {
                                int alt62=2;
                                int LA62_0 = input.LA(1);

                                if ( (LA62_0==COMMA||LA62_0==DOT||LA62_0==PLUS) ) {
                                    alt62=1;
                                }


                                switch (alt62) {
                            	case 1 :
                            	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:441:5: entsep en= entity_result[false]
                            	    {
                            	    pushFollow(FOLLOW_entsep_in_rule_right_hand_side2130);
                            	    entsep143=entsep();

                            	    state._fsp--;
                            	    if (state.failed) return retval;
                            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, entsep143.getTree());
                            	    pushFollow(FOLLOW_entity_result_in_rule_right_hand_side2134);
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
                            	    break loop62;
                                }
                            } while (true);


                            }
                            break;

                    }

                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:442:4: ({...}? R_BRACKET | )
                    int alt64=2;
                    int LA64_0 = input.LA(1);

                    if ( (LA64_0==R_BRACKET) ) {
                        alt64=1;
                    }
                    else if ( (LA64_0==EOF||LA64_0==AT) ) {
                        alt64=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 64, 0, input);

                        throw nvae;
                    }
                    switch (alt64) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:442:5: {...}? R_BRACKET
                            {
                            if ( !((retval.context != null)) ) {
                                if (state.backtracking>0) {state.failed=true; return retval;}
                                throw new FailedPredicateException(input, "rule_right_hand_side", "$context != null");
                            }
                            R_BRACKET144=(Token)match(input,R_BRACKET,FOLLOW_R_BRACKET_in_rule_right_hand_side2149); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            R_BRACKET144_tree = (Object)adaptor.create(R_BRACKET144);
                            adaptor.addChild(root_0, R_BRACKET144_tree);
                            }

                            }
                            break;
                        case 2 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:442:37: 
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

    public static class init_return extends ParserRuleReturnScope {
        public Map<InitEntity,Integer> map;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "init"
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:445:1: init[boolean ignore] returns [Map<InitEntity,Integer> map] : i1= init_element[ignore] ( entsep in= init_element[ignore] )* ;
    public final MLSpaceDirectParser.init_return init(boolean ignore) throws RecognitionException {
        MLSpaceDirectParser.init_return retval = new MLSpaceDirectParser.init_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        MLSpaceDirectParser.init_element_return i1 = null;

        MLSpaceDirectParser.init_element_return in = null;

        MLSpaceDirectParser.entsep_return entsep145 = null;



        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:445:59: (i1= init_element[ignore] ( entsep in= init_element[ignore] )* )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:446:1: i1= init_element[ignore] ( entsep in= init_element[ignore] )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_init_element_in_init2171);
            i1=init_element(ignore);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, i1.getTree());
            if ( state.backtracking==0 ) {
              retval.map = (i1!=null?i1.map:null);
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:447:1: ( entsep in= init_element[ignore] )*
            loop66:
            do {
                int alt66=2;
                int LA66_0 = input.LA(1);

                if ( (LA66_0==COMMA||LA66_0==DOT||LA66_0==PLUS) ) {
                    alt66=1;
                }


                switch (alt66) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:447:2: entsep in= init_element[ignore]
            	    {
            	    pushFollow(FOLLOW_entsep_in_init2177);
            	    entsep145=entsep();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    pushFollow(FOLLOW_init_element_in_init2182);
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
            	    break loop66;
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
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:449:1: init_element[boolean ignore] returns [Map<InitEntity,Integer> map] : ( ( for_each[ignore] ) | ( intval_or_var ( (e= entity_result[ignore] ) | ( L_BRACKET eba= entities_result[ignore] R_BRACKET ) ) ( L_BRACKET init[ignore] R_BRACKET )? ( SEMIC )? ) );
    public final MLSpaceDirectParser.init_element_return init_element(boolean ignore) throws RecognitionException {
        MLSpaceDirectParser.init_element_return retval = new MLSpaceDirectParser.init_element_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token L_BRACKET148=null;
        Token R_BRACKET149=null;
        Token L_BRACKET150=null;
        Token R_BRACKET152=null;
        Token SEMIC153=null;
        MLSpaceDirectParser.entity_result_return e = null;

        MLSpaceDirectParser.entities_result_return eba = null;

        MLSpaceDirectParser.for_each_return for_each146 = null;

        MLSpaceDirectParser.intval_or_var_return intval_or_var147 = null;

        MLSpaceDirectParser.init_return init151 = null;


        Object L_BRACKET148_tree=null;
        Object R_BRACKET149_tree=null;
        Object L_BRACKET150_tree=null;
        Object R_BRACKET152_tree=null;
        Object SEMIC153_tree=null;


          retval.map = new NonNullMap<InitEntity,Integer>(); 
          InitEntity tmpEnt = null; 

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:453:2: ( ( for_each[ignore] ) | ( intval_or_var ( (e= entity_result[ignore] ) | ( L_BRACKET eba= entities_result[ignore] R_BRACKET ) ) ( L_BRACKET init[ignore] R_BRACKET )? ( SEMIC )? ) )
            int alt70=2;
            int LA70_0 = input.LA(1);

            if ( (LA70_0==FOR) ) {
                alt70=1;
            }
            else if ( ((LA70_0>=FLOAT && LA70_0<=MINUS)) ) {
                alt70=2;
            }
            else if ( (LA70_0==ID) && ((getSingleNumValFromVar(input.LT(1).getText())!=null))) {
                alt70=2;
            }
            else if ( (LA70_0==L_BRACKET||LA70_0==L_PAREN) ) {
                alt70=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 70, 0, input);

                throw nvae;
            }
            switch (alt70) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:454:2: ( for_each[ignore] )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:454:2: ( for_each[ignore] )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:454:3: for_each[ignore]
                    {
                    pushFollow(FOLLOW_for_each_in_init_element2206);
                    for_each146=for_each(ignore);

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, for_each146.getTree());
                    if ( state.backtracking==0 ) {
                      retval.map = (for_each146!=null?for_each146.map:null);
                    }

                    }


                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:455:3: ( intval_or_var ( (e= entity_result[ignore] ) | ( L_BRACKET eba= entities_result[ignore] R_BRACKET ) ) ( L_BRACKET init[ignore] R_BRACKET )? ( SEMIC )? )
                    {
                    root_0 = (Object)adaptor.nil();

                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:455:3: ( intval_or_var ( (e= entity_result[ignore] ) | ( L_BRACKET eba= entities_result[ignore] R_BRACKET ) ) ( L_BRACKET init[ignore] R_BRACKET )? ( SEMIC )? )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:455:4: intval_or_var ( (e= entity_result[ignore] ) | ( L_BRACKET eba= entities_result[ignore] R_BRACKET ) ) ( L_BRACKET init[ignore] R_BRACKET )? ( SEMIC )?
                    {
                    pushFollow(FOLLOW_intval_or_var_in_init_element2215);
                    intval_or_var147=intval_or_var();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, intval_or_var147.getTree());
                    if ( state.backtracking==0 ) {
                      ignore = ignore || (intval_or_var147!=null?intval_or_var147.val:0) <= 0;
                    }
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:456:5: ( (e= entity_result[ignore] ) | ( L_BRACKET eba= entities_result[ignore] R_BRACKET ) )
                    int alt67=2;
                    int LA67_0 = input.LA(1);

                    if ( (LA67_0==ID) ) {
                        alt67=1;
                    }
                    else if ( (LA67_0==L_BRACKET) ) {
                        alt67=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 67, 0, input);

                        throw nvae;
                    }
                    switch (alt67) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:456:6: (e= entity_result[ignore] )
                            {
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:456:6: (e= entity_result[ignore] )
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:456:7: e= entity_result[ignore]
                            {
                            pushFollow(FOLLOW_entity_result_in_init_element2229);
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
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:458:6: ( L_BRACKET eba= entities_result[ignore] R_BRACKET )
                            {
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:458:6: ( L_BRACKET eba= entities_result[ignore] R_BRACKET )
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:458:7: L_BRACKET eba= entities_result[ignore] R_BRACKET
                            {
                            L_BRACKET148=(Token)match(input,L_BRACKET,FOLLOW_L_BRACKET_in_init_element2248); if (state.failed) return retval;
                            pushFollow(FOLLOW_entities_result_in_init_element2253);
                            eba=entities_result(ignore);

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, eba.getTree());
                            R_BRACKET149=(Token)match(input,R_BRACKET,FOLLOW_R_BRACKET_in_init_element2256); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                              tmpEnt = new InitEntityWithBindings(MLSpaceParserHelper.modToInitEntities((eba!=null?eba.list:null)));
                            }

                            }


                            }
                            break;

                    }

                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:460:6: ( L_BRACKET init[ignore] R_BRACKET )?
                    int alt68=2;
                    int LA68_0 = input.LA(1);

                    if ( (LA68_0==L_BRACKET) ) {
                        alt68=1;
                    }
                    switch (alt68) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:460:7: L_BRACKET init[ignore] R_BRACKET
                            {
                            L_BRACKET150=(Token)match(input,L_BRACKET,FOLLOW_L_BRACKET_in_init_element2274); if (state.failed) return retval;
                            pushFollow(FOLLOW_init_in_init_element2277);
                            init151=init(ignore);

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, init151.getTree());
                            R_BRACKET152=(Token)match(input,R_BRACKET,FOLLOW_R_BRACKET_in_init_element2280); if (state.failed) return retval;

                            }
                            break;

                    }

                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:460:50: ( SEMIC )?
                    int alt69=2;
                    int LA69_0 = input.LA(1);

                    if ( (LA69_0==SEMIC) ) {
                        int LA69_1 = input.LA(2);

                        if ( (LA69_1==EOF||LA69_1==R_BRACKET||LA69_1==R_BRACE||(LA69_1>=SEMIC && LA69_1<=COMMA)||LA69_1==DOT||LA69_1==PLUS) ) {
                            alt69=1;
                        }
                    }
                    switch (alt69) {
                        case 1 :
                            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:0:0: SEMIC
                            {
                            SEMIC153=(Token)match(input,SEMIC,FOLLOW_SEMIC_in_init_element2286); if (state.failed) return retval;

                            }
                            break;

                    }

                    if ( state.backtracking==0 ) {
                      if ((init151!=null?init151.map:null) != null)
                            tmpEnt.updateSubEntities((init151!=null?init151.map:null));
                           if (!ignore)
                             retval.map.put(tmpEnt, (intval_or_var147!=null?intval_or_var147.val:0));
                          
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
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:467:1: for_each[boolean ignore] returns [Map<InitEntity,Integer> map] : ( FOR for_var L_BRACE init[ignore] R_BRACE )+ ;
    public final MLSpaceDirectParser.for_each_return for_each(boolean ignore) throws RecognitionException {
        MLSpaceDirectParser.for_each_return retval = new MLSpaceDirectParser.for_each_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token FOR154=null;
        Token L_BRACE156=null;
        Token R_BRACE158=null;
        MLSpaceDirectParser.for_var_return for_var155 = null;

        MLSpaceDirectParser.init_return init157 = null;


        Object FOR154_tree=null;
        Object L_BRACE156_tree=null;
        Object R_BRACE158_tree=null;

        int mark = -1;
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:468:22: ( ( FOR for_var L_BRACE init[ignore] R_BRACE )+ )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:469:1: ( FOR for_var L_BRACE init[ignore] R_BRACE )+
            {
            root_0 = (Object)adaptor.nil();

            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:469:1: ( FOR for_var L_BRACE init[ignore] R_BRACE )+
            int cnt71=0;
            loop71:
            do {
                int alt71=2;
                int LA71_0 = input.LA(1);

                if ( (LA71_0==FOR) ) {
                    alt71=1;
                }


                switch (alt71) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:469:3: FOR for_var L_BRACE init[ignore] R_BRACE
            	    {
            	    if ( state.backtracking==0 ) {
            	      mark = input.mark();
            	    }
            	    FOR154=(Token)match(input,FOR,FOLLOW_FOR_in_for_each2316); if (state.failed) return retval;
            	    pushFollow(FOLLOW_for_var_in_for_each2319);
            	    for_var155=for_var();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, for_var155.getTree());
            	    if ( state.backtracking==0 ) {
            	      handleForVar((for_var155!=null?for_var155.name:null),(for_var155!=null?for_var155.range:null));
            	    }
            	    L_BRACE156=(Token)match(input,L_BRACE,FOLLOW_L_BRACE_in_for_each2324); if (state.failed) return retval;
            	    pushFollow(FOLLOW_init_in_for_each2327);
            	    init157=init(ignore);

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, init157.getTree());
            	    if ( state.backtracking==0 ) {
            	      if (retval.map == null) retval.map = (init157!=null?init157.map:null); else 
            	       for (Map.Entry<InitEntity,Integer> e: (init157!=null?init157.map:null).entrySet()) {
            	         retval.map.put(e.getKey(), e.getValue() + (retval.map.containsKey(e.getKey()) ? retval.map.get(e.getKey()) : 0)); 
            	       }
            	    }
            	    R_BRACE158=(Token)match(input,R_BRACE,FOLLOW_R_BRACE_in_for_each2333); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	      if (!wasLastLoop((for_var155!=null?for_var155.name:null),(for_var155!=null?for_var155.range:null))) input.rewind(mark);
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt71 >= 1 ) break loop71;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(71, input);
                        throw eee;
                }
                cnt71++;
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
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:480:1: for_var returns [String name, List<?> range] : ID ( EQ | BECOMES ) (r= range | set ) ;
    public final MLSpaceDirectParser.for_var_return for_var() throws RecognitionException {
        MLSpaceDirectParser.for_var_return retval = new MLSpaceDirectParser.for_var_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID159=null;
        Token EQ160=null;
        Token BECOMES161=null;
        MLSpaceDirectParser.range_return r = null;

        MLSpaceDirectParser.set_return set162 = null;


        Object ID159_tree=null;
        Object EQ160_tree=null;
        Object BECOMES161_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:480:45: ( ID ( EQ | BECOMES ) (r= range | set ) )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:481:1: ID ( EQ | BECOMES ) (r= range | set )
            {
            root_0 = (Object)adaptor.nil();

            ID159=(Token)match(input,ID,FOLLOW_ID_in_for_var2352); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID159_tree = (Object)adaptor.create(ID159);
            adaptor.addChild(root_0, ID159_tree);
            }
            if ( state.backtracking==0 ) {
              retval.name =(ID159!=null?ID159.getText():null);
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:481:22: ( EQ | BECOMES )
            int alt72=2;
            int LA72_0 = input.LA(1);

            if ( (LA72_0==EQ) ) {
                alt72=1;
            }
            else if ( (LA72_0==BECOMES) ) {
                alt72=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 72, 0, input);

                throw nvae;
            }
            switch (alt72) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:481:23: EQ
                    {
                    EQ160=(Token)match(input,EQ,FOLLOW_EQ_in_for_var2357); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:481:27: BECOMES
                    {
                    BECOMES161=(Token)match(input,BECOMES,FOLLOW_BECOMES_in_for_var2360); if (state.failed) return retval;

                    }
                    break;

            }

            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:482:1: (r= range | set )
            int alt73=2;
            int LA73_0 = input.LA(1);

            if ( (LA73_0==L_BRACKET||LA73_0==L_PAREN||(LA73_0>=FLOAT && LA73_0<=MINUS)||LA73_0==ID) ) {
                alt73=1;
            }
            else if ( (LA73_0==L_BRACE) ) {
                alt73=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 73, 0, input);

                throw nvae;
            }
            switch (alt73) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:482:2: r= range
                    {
                    pushFollow(FOLLOW_range_in_for_var2368);
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
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:483:2: set
                    {
                    pushFollow(FOLLOW_set_in_for_var2375);
                    set162=set();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, set162.getTree());
                    if ( state.backtracking==0 ) {
                      retval.range = new ArrayList<Object>((set162!=null?set162.set:null));
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

    public static class numexpr_return extends ParserRuleReturnScope {
        public Double val;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "numexpr"
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:486:1: numexpr returns [Double val] : e= multExpr ( PLUS e= multExpr | MINUS e= multExpr )* ;
    public final MLSpaceDirectParser.numexpr_return numexpr() throws RecognitionException {
        MLSpaceDirectParser.numexpr_return retval = new MLSpaceDirectParser.numexpr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token PLUS163=null;
        Token MINUS164=null;
        MLSpaceDirectParser.multExpr_return e = null;


        Object PLUS163_tree=null;
        Object MINUS164_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:486:29: (e= multExpr ( PLUS e= multExpr | MINUS e= multExpr )* )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:487:3: e= multExpr ( PLUS e= multExpr | MINUS e= multExpr )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_multExpr_in_numexpr2396);
            e=multExpr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, e.getTree());
            if ( state.backtracking==0 ) {
              retval.val = (e!=null?e.val:null);
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:488:9: ( PLUS e= multExpr | MINUS e= multExpr )*
            loop74:
            do {
                int alt74=3;
                int LA74_0 = input.LA(1);

                if ( (LA74_0==PLUS) ) {
                    alt74=1;
                }
                else if ( (LA74_0==MINUS) ) {
                    alt74=2;
                }


                switch (alt74) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:488:10: PLUS e= multExpr
            	    {
            	    PLUS163=(Token)match(input,PLUS,FOLLOW_PLUS_in_numexpr2409); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    PLUS163_tree = (Object)adaptor.create(PLUS163);
            	    adaptor.addChild(root_0, PLUS163_tree);
            	    }
            	    pushFollow(FOLLOW_multExpr_in_numexpr2413);
            	    e=multExpr();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, e.getTree());
            	    if ( state.backtracking==0 ) {
            	      retval.val += (e!=null?e.val:null);
            	    }

            	    }
            	    break;
            	case 2 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:489:10: MINUS e= multExpr
            	    {
            	    MINUS164=(Token)match(input,MINUS,FOLLOW_MINUS_in_numexpr2426); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    MINUS164_tree = (Object)adaptor.create(MINUS164);
            	    adaptor.addChild(root_0, MINUS164_tree);
            	    }
            	    pushFollow(FOLLOW_multExpr_in_numexpr2430);
            	    e=multExpr();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, e.getTree());
            	    if ( state.backtracking==0 ) {
            	      retval.val -= (e!=null?e.val:null);
            	    }

            	    }
            	    break;

            	default :
            	    break loop74;
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
    // $ANTLR end "numexpr"

    public static class multExpr_return extends ParserRuleReturnScope {
        public Double val;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "multExpr"
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:492:1: multExpr returns [Double val] : e= atomExpr ( TIMES e= atomExpr | DIV e= atomExpr )* ;
    public final MLSpaceDirectParser.multExpr_return multExpr() throws RecognitionException {
        MLSpaceDirectParser.multExpr_return retval = new MLSpaceDirectParser.multExpr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TIMES165=null;
        Token DIV166=null;
        MLSpaceDirectParser.atomExpr_return e = null;


        Object TIMES165_tree=null;
        Object DIV166_tree=null;

        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:492:30: (e= atomExpr ( TIMES e= atomExpr | DIV e= atomExpr )* )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:493:3: e= atomExpr ( TIMES e= atomExpr | DIV e= atomExpr )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_atomExpr_in_multExpr2459);
            e=atomExpr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, e.getTree());
            if ( state.backtracking==0 ) {
              retval.val = (e!=null?e.val:null);
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:494:8: ( TIMES e= atomExpr | DIV e= atomExpr )*
            loop75:
            do {
                int alt75=3;
                int LA75_0 = input.LA(1);

                if ( (LA75_0==TIMES) ) {
                    alt75=1;
                }
                else if ( (LA75_0==DIV) ) {
                    alt75=2;
                }


                switch (alt75) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:494:9: TIMES e= atomExpr
            	    {
            	    TIMES165=(Token)match(input,TIMES,FOLLOW_TIMES_in_multExpr2472); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    TIMES165_tree = (Object)adaptor.create(TIMES165);
            	    adaptor.addChild(root_0, TIMES165_tree);
            	    }
            	    pushFollow(FOLLOW_atomExpr_in_multExpr2476);
            	    e=atomExpr();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, e.getTree());
            	    if ( state.backtracking==0 ) {
            	      retval.val *= (e!=null?e.val:null);
            	    }

            	    }
            	    break;
            	case 2 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:495:10: DIV e= atomExpr
            	    {
            	    DIV166=(Token)match(input,DIV,FOLLOW_DIV_in_multExpr2489); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    DIV166_tree = (Object)adaptor.create(DIV166);
            	    adaptor.addChild(root_0, DIV166_tree);
            	    }
            	    pushFollow(FOLLOW_atomExpr_in_multExpr2493);
            	    e=atomExpr();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, e.getTree());
            	    if ( state.backtracking==0 ) {
            	      retval.val /= (e!=null?e.val:null);
            	    }

            	    }
            	    break;

            	default :
            	    break loop75;
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
    // $ANTLR end "multExpr"

    public static class atomExpr_return extends ParserRuleReturnScope {
        public Double val;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "atomExpr"
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:498:1: atomExpr returns [Double val] : ( MINUS | PLUS )? ( (n= numval ) | ( L_PAREN e= numexpr R_PAREN ) | ( L_BRACKET e= numexpr R_BRACKET ) ) ( ( SQR ) | ( CUB ) | ( DEGREES ) | ( POW a= atomExpr ) )? ;
    public final MLSpaceDirectParser.atomExpr_return atomExpr() throws RecognitionException {
        MLSpaceDirectParser.atomExpr_return retval = new MLSpaceDirectParser.atomExpr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token MINUS167=null;
        Token PLUS168=null;
        Token L_PAREN169=null;
        Token R_PAREN170=null;
        Token L_BRACKET171=null;
        Token R_BRACKET172=null;
        Token SQR173=null;
        Token CUB174=null;
        Token DEGREES175=null;
        Token POW176=null;
        MLSpaceDirectParser.numval_return n = null;

        MLSpaceDirectParser.numexpr_return e = null;

        MLSpaceDirectParser.atomExpr_return a = null;


        Object MINUS167_tree=null;
        Object PLUS168_tree=null;
        Object L_PAREN169_tree=null;
        Object R_PAREN170_tree=null;
        Object L_BRACKET171_tree=null;
        Object R_BRACKET172_tree=null;
        Object SQR173_tree=null;
        Object CUB174_tree=null;
        Object DEGREES175_tree=null;
        Object POW176_tree=null;

        double sign = 1.;
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:498:56: ( ( MINUS | PLUS )? ( (n= numval ) | ( L_PAREN e= numexpr R_PAREN ) | ( L_BRACKET e= numexpr R_BRACKET ) ) ( ( SQR ) | ( CUB ) | ( DEGREES ) | ( POW a= atomExpr ) )? )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:499:3: ( MINUS | PLUS )? ( (n= numval ) | ( L_PAREN e= numexpr R_PAREN ) | ( L_BRACKET e= numexpr R_BRACKET ) ) ( ( SQR ) | ( CUB ) | ( DEGREES ) | ( POW a= atomExpr ) )?
            {
            root_0 = (Object)adaptor.nil();

            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:499:3: ( MINUS | PLUS )?
            int alt76=3;
            int LA76_0 = input.LA(1);

            if ( (LA76_0==MINUS) ) {
                alt76=1;
            }
            else if ( (LA76_0==PLUS) ) {
                alt76=2;
            }
            switch (alt76) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:499:4: MINUS
                    {
                    MINUS167=(Token)match(input,MINUS,FOLLOW_MINUS_in_atomExpr2530); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    MINUS167_tree = (Object)adaptor.create(MINUS167);
                    adaptor.addChild(root_0, MINUS167_tree);
                    }
                    if ( state.backtracking==0 ) {
                      sign = -1.;
                    }

                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:499:26: PLUS
                    {
                    PLUS168=(Token)match(input,PLUS,FOLLOW_PLUS_in_atomExpr2536); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    PLUS168_tree = (Object)adaptor.create(PLUS168);
                    adaptor.addChild(root_0, PLUS168_tree);
                    }

                    }
                    break;

            }

            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:500:3: ( (n= numval ) | ( L_PAREN e= numexpr R_PAREN ) | ( L_BRACKET e= numexpr R_BRACKET ) )
            int alt77=3;
            switch ( input.LA(1) ) {
            case FLOAT:
            case ID:
                {
                alt77=1;
                }
                break;
            case L_PAREN:
                {
                alt77=2;
                }
                break;
            case L_BRACKET:
                {
                alt77=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 77, 0, input);

                throw nvae;
            }

            switch (alt77) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:500:5: (n= numval )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:500:5: (n= numval )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:500:6: n= numval
                    {
                    pushFollow(FOLLOW_numval_in_atomExpr2547);
                    n=numval();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, n.getTree());
                    if ( state.backtracking==0 ) {
                      retval.val =(n!=null?n.val:null);
                    }

                    }


                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:501:5: ( L_PAREN e= numexpr R_PAREN )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:501:5: ( L_PAREN e= numexpr R_PAREN )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:501:6: L_PAREN e= numexpr R_PAREN
                    {
                    L_PAREN169=(Token)match(input,L_PAREN,FOLLOW_L_PAREN_in_atomExpr2557); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    L_PAREN169_tree = (Object)adaptor.create(L_PAREN169);
                    adaptor.addChild(root_0, L_PAREN169_tree);
                    }
                    pushFollow(FOLLOW_numexpr_in_atomExpr2561);
                    e=numexpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, e.getTree());
                    R_PAREN170=(Token)match(input,R_PAREN,FOLLOW_R_PAREN_in_atomExpr2563); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    R_PAREN170_tree = (Object)adaptor.create(R_PAREN170);
                    adaptor.addChild(root_0, R_PAREN170_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.val =(e!=null?e.val:null);
                    }

                    }


                    }
                    break;
                case 3 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:502:5: ( L_BRACKET e= numexpr R_BRACKET )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:502:5: ( L_BRACKET e= numexpr R_BRACKET )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:502:6: L_BRACKET e= numexpr R_BRACKET
                    {
                    L_BRACKET171=(Token)match(input,L_BRACKET,FOLLOW_L_BRACKET_in_atomExpr2573); if (state.failed) return retval;
                    pushFollow(FOLLOW_numexpr_in_atomExpr2578);
                    e=numexpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, e.getTree());
                    R_BRACKET172=(Token)match(input,R_BRACKET,FOLLOW_R_BRACKET_in_atomExpr2580); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      retval.val = (double) ((e!=null?e.val:null).intValue());
                    }

                    }


                    }
                    break;

            }

            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:504:3: ( ( SQR ) | ( CUB ) | ( DEGREES ) | ( POW a= atomExpr ) )?
            int alt78=5;
            switch ( input.LA(1) ) {
                case SQR:
                    {
                    alt78=1;
                    }
                    break;
                case CUB:
                    {
                    alt78=2;
                    }
                    break;
                case DEGREES:
                    {
                    alt78=3;
                    }
                    break;
                case POW:
                    {
                    alt78=4;
                    }
                    break;
            }

            switch (alt78) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:505:4: ( SQR )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:505:4: ( SQR )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:505:5: SQR
                    {
                    SQR173=(Token)match(input,SQR,FOLLOW_SQR_in_atomExpr2603); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SQR173_tree = (Object)adaptor.create(SQR173);
                    adaptor.addChild(root_0, SQR173_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.val *= retval.val;
                    }

                    }


                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:505:28: ( CUB )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:505:28: ( CUB )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:505:29: CUB
                    {
                    CUB174=(Token)match(input,CUB,FOLLOW_CUB_in_atomExpr2611); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CUB174_tree = (Object)adaptor.create(CUB174);
                    adaptor.addChild(root_0, CUB174_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.val *= retval.val*retval.val;
                    }

                    }


                    }
                    break;
                case 3 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:506:5: ( DEGREES )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:506:5: ( DEGREES )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:506:6: DEGREES
                    {
                    DEGREES175=(Token)match(input,DEGREES,FOLLOW_DEGREES_in_atomExpr2621); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    DEGREES175_tree = (Object)adaptor.create(DEGREES175);
                    adaptor.addChild(root_0, DEGREES175_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.val = retval.val * Math.PI / 180.;
                    }

                    }


                    }
                    break;
                case 4 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:507:5: ( POW a= atomExpr )
                    {
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:507:5: ( POW a= atomExpr )
                    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:507:6: POW a= atomExpr
                    {
                    POW176=(Token)match(input,POW,FOLLOW_POW_in_atomExpr2631); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    POW176_tree = (Object)adaptor.create(POW176);
                    adaptor.addChild(root_0, POW176_tree);
                    }
                    pushFollow(FOLLOW_atomExpr_in_atomExpr2635);
                    a=atomExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, a.getTree());
                    if ( state.backtracking==0 ) {
                      retval.val = Math.pow(retval.val,(a!=null?a.val:null));
                    }

                    }


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              if (retval.val != null) retval.val *= sign;
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
    // $ANTLR end "atomExpr"

    public static class observationTargets_return extends ParserRuleReturnScope {
        public List<List<? extends RuleEntity>> obs;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "observationTargets"
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:513:1: observationTargets returns [List<List<? extends RuleEntity>> obs] : oTE1= obsTargetEntry ( SEMIC oTE2= obsTargetEntry )* ;
    public final MLSpaceDirectParser.observationTargets_return observationTargets() throws RecognitionException {
        MLSpaceDirectParser.observationTargets_return retval = new MLSpaceDirectParser.observationTargets_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMIC177=null;
        MLSpaceDirectParser.obsTargetEntry_return oTE1 = null;

        MLSpaceDirectParser.obsTargetEntry_return oTE2 = null;


        Object SEMIC177_tree=null;

        retval.obs = new ArrayList<List<? extends RuleEntity>>();
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:513:125: (oTE1= obsTargetEntry ( SEMIC oTE2= obsTargetEntry )* )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:514:3: oTE1= obsTargetEntry ( SEMIC oTE2= obsTargetEntry )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_obsTargetEntry_in_observationTargets2673);
            oTE1=obsTargetEntry();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, oTE1.getTree());
            if ( state.backtracking==0 ) {
              retval.obs.addAll((oTE1!=null?oTE1.oe:null));
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:515:3: ( SEMIC oTE2= obsTargetEntry )*
            loop79:
            do {
                int alt79=2;
                int LA79_0 = input.LA(1);

                if ( (LA79_0==SEMIC) ) {
                    alt79=1;
                }


                switch (alt79) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:515:4: SEMIC oTE2= obsTargetEntry
            	    {
            	    SEMIC177=(Token)match(input,SEMIC,FOLLOW_SEMIC_in_observationTargets2680); if (state.failed) return retval;
            	    pushFollow(FOLLOW_obsTargetEntry_in_observationTargets2685);
            	    oTE2=obsTargetEntry();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, oTE2.getTree());
            	    if ( state.backtracking==0 ) {
            	      retval.obs.addAll((oTE2!=null?oTE2.oe:null));
            	    }

            	    }
            	    break;

            	default :
            	    break loop79;
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
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "obsTargetEntry"
    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:517:1: obsTargetEntry returns [List<? extends List<? extends RuleEntity>> oe] : e1= entities_match[null] ( IN e2= entities_match[null] )* ;
    public final MLSpaceDirectParser.obsTargetEntry_return obsTargetEntry() throws RecognitionException {
        MLSpaceDirectParser.obsTargetEntry_return retval = new MLSpaceDirectParser.obsTargetEntry_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token IN178=null;
        MLSpaceDirectParser.entities_match_return e1 = null;

        MLSpaceDirectParser.entities_match_return e2 = null;


        Object IN178_tree=null;

        List<List<? extends RuleEntity>> tmp = new ArrayList<List<? extends RuleEntity>>();
        try {
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:519:44: (e1= entities_match[null] ( IN e2= entities_match[null] )* )
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:520:3: e1= entities_match[null] ( IN e2= entities_match[null] )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_entities_match_in_obsTargetEntry2715);
            e1=entities_match(null);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, e1.getTree());
            if ( state.backtracking==0 ) {
              tmp.add((e1!=null?e1.list:null));
            }
            // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:521:3: ( IN e2= entities_match[null] )*
            loop80:
            do {
                int alt80=2;
                int LA80_0 = input.LA(1);

                if ( (LA80_0==IN) ) {
                    alt80=1;
                }


                switch (alt80) {
            	case 1 :
            	    // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:521:4: IN e2= entities_match[null]
            	    {
            	    IN178=(Token)match(input,IN,FOLLOW_IN_in_obsTargetEntry2724); if (state.failed) return retval;
            	    pushFollow(FOLLOW_entities_match_in_obsTargetEntry2729);
            	    e2=entities_match(null);

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, e2.getTree());
            	    if ( state.backtracking==0 ) {
            	      tmp.add((e2!=null?e2.list:null));
            	    }

            	    }
            	    break;

            	default :
            	    break loop80;
                }
            } while (true);


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

    // $ANTLR start synpred7_MLSpaceDirectParser
    public final void synpred7_MLSpaceDirectParser_fragment() throws RecognitionException {   
        MLSpaceDirectParser.init_return i1 = null;

        MLSpaceDirectParser.rules_return r1 = null;


        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:178:3: ( ( ( init )=>i1= init[false] ( SEMIC r1= rules )? ) )
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:178:3: ( ( init )=>i1= init[false] ( SEMIC r1= rules )? )
        {
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:178:3: ( ( init )=>i1= init[false] ( SEMIC r1= rules )? )
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:178:4: ( init )=>i1= init[false] ( SEMIC r1= rules )?
        {
        pushFollow(FOLLOW_init_in_synpred7_MLSpaceDirectParser105);
        i1=init(false);

        state._fsp--;
        if (state.failed) return ;
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:178:28: ( SEMIC r1= rules )?
        int alt82=2;
        int LA82_0 = input.LA(1);

        if ( (LA82_0==SEMIC) ) {
            alt82=1;
        }
        switch (alt82) {
            case 1 :
                // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:178:29: SEMIC r1= rules
                {
                match(input,SEMIC,FOLLOW_SEMIC_in_synpred7_MLSpaceDirectParser109); if (state.failed) return ;
                pushFollow(FOLLOW_rules_in_synpred7_MLSpaceDirectParser113);
                r1=rules();

                state._fsp--;
                if (state.failed) return ;

                }
                break;

        }


        }


        }
    }
    // $ANTLR end synpred7_MLSpaceDirectParser

    // $ANTLR start synpred14_MLSpaceDirectParser
    public final void synpred14_MLSpaceDirectParser_fragment() throws RecognitionException {   
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:216:2: ( ( ( interval )=> interval ) )
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:216:2: ( ( interval )=> interval )
        {
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:216:2: ( ( interval )=> interval )
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:216:3: ( interval )=> interval
        {
        pushFollow(FOLLOW_interval_in_synpred14_MLSpaceDirectParser276);
        interval();

        state._fsp--;
        if (state.failed) return ;

        }


        }
    }
    // $ANTLR end synpred14_MLSpaceDirectParser

    // $ANTLR start synpred16_MLSpaceDirectParser
    public final void synpred16_MLSpaceDirectParser_fragment() throws RecognitionException {   
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:217:2: ( ( ( range )=> range ) )
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:217:2: ( ( range )=> range )
        {
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:217:2: ( ( range )=> range )
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:217:3: ( range )=> range
        {
        pushFollow(FOLLOW_range_in_synpred16_MLSpaceDirectParser288);
        range();

        state._fsp--;
        if (state.failed) return ;

        }


        }
    }
    // $ANTLR end synpred16_MLSpaceDirectParser

    // $ANTLR start synpred20_MLSpaceDirectParser
    public final void synpred20_MLSpaceDirectParser_fragment() throws RecognitionException {   
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:219:2: ( ( ( vector )=> vector ) )
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:219:2: ( ( vector )=> vector )
        {
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:219:2: ( ( vector )=> vector )
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:219:3: ( vector )=> vector
        {
        pushFollow(FOLLOW_vector_in_synpred20_MLSpaceDirectParser312);
        vector();

        state._fsp--;
        if (state.failed) return ;

        }


        }
    }
    // $ANTLR end synpred20_MLSpaceDirectParser

    // $ANTLR start synpred22_MLSpaceDirectParser
    public final void synpred22_MLSpaceDirectParser_fragment() throws RecognitionException {   
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:220:2: ( ( ( numexpr )=> numexpr ) )
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:220:2: ( ( numexpr )=> numexpr )
        {
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:220:2: ( ( numexpr )=> numexpr )
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:220:3: ( numexpr )=> numexpr
        {
        pushFollow(FOLLOW_numexpr_in_synpred22_MLSpaceDirectParser324);
        numexpr();

        state._fsp--;
        if (state.failed) return ;

        }


        }
    }
    // $ANTLR end synpred22_MLSpaceDirectParser

    // $ANTLR start synpred24_MLSpaceDirectParser
    public final void synpred24_MLSpaceDirectParser_fragment() throws RecognitionException {   
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:231:6: ( attribute )
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:231:7: attribute
        {
        pushFollow(FOLLOW_attribute_in_synpred24_MLSpaceDirectParser403);
        attribute();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred24_MLSpaceDirectParser

    // $ANTLR start synpred26_MLSpaceDirectParser
    public final void synpred26_MLSpaceDirectParser_fragment() throws RecognitionException {   
        MLSpaceDirectParser.var_interval_return vi = null;


        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:237:4: (vi= var_interval )
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:237:4: vi= var_interval
        {
        pushFollow(FOLLOW_var_interval_in_synpred26_MLSpaceDirectParser461);
        vi=var_interval();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred26_MLSpaceDirectParser

    // $ANTLR start synpred42_MLSpaceDirectParser
    public final void synpred42_MLSpaceDirectParser_fragment() throws RecognitionException {   
        MLSpaceDirectParser.numval_return n = null;


        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:265:6: (n= numval )
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:265:6: n= numval
        {
        pushFollow(FOLLOW_numval_in_synpred42_MLSpaceDirectParser760);
        n=numval();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred42_MLSpaceDirectParser

    // $ANTLR start synpred43_MLSpaceDirectParser
    public final void synpred43_MLSpaceDirectParser_fragment() throws RecognitionException {   
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:266:6: ( ID )
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:266:6: ID
        {
        match(input,ID,FOLLOW_ID_in_synpred43_MLSpaceDirectParser769); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred43_MLSpaceDirectParser

    // $ANTLR start synpred56_MLSpaceDirectParser
    public final void synpred56_MLSpaceDirectParser_fragment() throws RecognitionException {   
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:299:3: ( numset )
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:299:4: numset
        {
        pushFollow(FOLLOW_numset_in_synpred56_MLSpaceDirectParser1094);
        numset();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred56_MLSpaceDirectParser

    // $ANTLR start synpred63_MLSpaceDirectParser
    public final void synpred63_MLSpaceDirectParser_fragment() throws RecognitionException {   
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:331:17: ( species_def )
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:331:18: species_def
        {
        pushFollow(FOLLOW_species_def_in_synpred63_MLSpaceDirectParser1302);
        species_def();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred63_MLSpaceDirectParser

    // $ANTLR start synpred83_MLSpaceDirectParser
    public final void synpred83_MLSpaceDirectParser_fragment() throws RecognitionException {   
        MLSpaceDirectParser.numexpr_return ne = null;


        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:393:12: ( (ne= numexpr ) )
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:393:12: (ne= numexpr )
        {
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:393:12: (ne= numexpr )
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:393:13: ne= numexpr
        {
        pushFollow(FOLLOW_numexpr_in_synpred83_MLSpaceDirectParser1755);
        ne=numexpr();

        state._fsp--;
        if (state.failed) return ;

        }


        }
    }
    // $ANTLR end synpred83_MLSpaceDirectParser

    // $ANTLR start synpred93_MLSpaceDirectParser
    public final void synpred93_MLSpaceDirectParser_fragment() throws RecognitionException {   
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:416:9: ( SEMIC )
        // model\\mlspace\\reader\\antlr\\MLSpaceDirectParser.g:416:9: SEMIC
        {
        match(input,SEMIC,FOLLOW_SEMIC_in_synpred93_MLSpaceDirectParser1941); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred93_MLSpaceDirectParser

    // Delegated rules

    public final boolean synpred22_MLSpaceDirectParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred22_MLSpaceDirectParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred26_MLSpaceDirectParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred26_MLSpaceDirectParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred83_MLSpaceDirectParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred83_MLSpaceDirectParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred7_MLSpaceDirectParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred7_MLSpaceDirectParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred16_MLSpaceDirectParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred16_MLSpaceDirectParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred43_MLSpaceDirectParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred43_MLSpaceDirectParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred56_MLSpaceDirectParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred56_MLSpaceDirectParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred93_MLSpaceDirectParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred93_MLSpaceDirectParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred42_MLSpaceDirectParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred42_MLSpaceDirectParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred24_MLSpaceDirectParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred24_MLSpaceDirectParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred14_MLSpaceDirectParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred14_MLSpaceDirectParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred63_MLSpaceDirectParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred63_MLSpaceDirectParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred20_MLSpaceDirectParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred20_MLSpaceDirectParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA10 dfa10 = new DFA10(this);
    protected DFA12 dfa12 = new DFA12(this);
    protected DFA15 dfa15 = new DFA15(this);
    protected DFA24 dfa24 = new DFA24(this);
    protected DFA49 dfa49 = new DFA49(this);
    static final String DFA10_eotS =
        "\16\uffff";
    static final String DFA10_eofS =
        "\16\uffff";
    static final String DFA10_minS =
        "\1\4\1\0\2\uffff\5\0\5\uffff";
    static final String DFA10_maxS =
        "\1\55\1\0\2\uffff\5\0\5\uffff";
    static final String DFA10_acceptS =
        "\2\uffff\1\1\6\uffff\1\3\1\2\1\5\1\6\1\4";
    static final String DFA10_specialS =
        "\1\uffff\1\0\2\uffff\1\1\1\2\1\3\1\4\1\5\5\uffff}>";
    static final String[] DFA10_transitionS = {
            "\1\1\1\uffff\1\11\10\uffff\2\2\12\uffff\1\10\3\uffff\1\6\1"+
            "\5\1\4\13\uffff\1\7",
            "\1\uffff",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA10_eot = DFA.unpackEncodedString(DFA10_eotS);
    static final short[] DFA10_eof = DFA.unpackEncodedString(DFA10_eofS);
    static final char[] DFA10_min = DFA.unpackEncodedStringToUnsignedChars(DFA10_minS);
    static final char[] DFA10_max = DFA.unpackEncodedStringToUnsignedChars(DFA10_maxS);
    static final short[] DFA10_accept = DFA.unpackEncodedString(DFA10_acceptS);
    static final short[] DFA10_special = DFA.unpackEncodedString(DFA10_specialS);
    static final short[][] DFA10_transition;

    static {
        int numStates = DFA10_transitionS.length;
        DFA10_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA10_transition[i] = DFA.unpackEncodedString(DFA10_transitionS[i]);
        }
    }

    class DFA10 extends DFA {

        public DFA10(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 10;
            this.eot = DFA10_eot;
            this.eof = DFA10_eof;
            this.min = DFA10_min;
            this.max = DFA10_max;
            this.accept = DFA10_accept;
            this.special = DFA10_special;
            this.transition = DFA10_transition;
        }
        public String getDescription() {
            return "215:1: valset_or_const returns [AbstractValueRange<?> val] : ( ( ( interval )=> interval ) | ( ( range )=> range ) | ( ( set )=> set ) | ( ( vector )=> vector ) | ( ( numexpr )=> numexpr ) | ( ID ) );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA10_1 = input.LA(1);

                         
                        int index10_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred14_MLSpaceDirectParser()) ) {s = 2;}

                        else if ( (synpred16_MLSpaceDirectParser()) ) {s = 10;}

                        else if ( (synpred22_MLSpaceDirectParser()) ) {s = 11;}

                         
                        input.seek(index10_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA10_4 = input.LA(1);

                         
                        int index10_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred16_MLSpaceDirectParser()) ) {s = 10;}

                        else if ( (synpred22_MLSpaceDirectParser()) ) {s = 11;}

                         
                        input.seek(index10_4);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA10_5 = input.LA(1);

                         
                        int index10_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred16_MLSpaceDirectParser()) ) {s = 10;}

                        else if ( (synpred22_MLSpaceDirectParser()) ) {s = 11;}

                         
                        input.seek(index10_5);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA10_6 = input.LA(1);

                         
                        int index10_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred16_MLSpaceDirectParser()) ) {s = 10;}

                        else if ( (synpred22_MLSpaceDirectParser()) ) {s = 11;}

                         
                        input.seek(index10_6);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA10_7 = input.LA(1);

                         
                        int index10_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((synpred16_MLSpaceDirectParser()&&(getSingleNumValFromVar(input.LT(1).getText())!=null))) ) {s = 10;}

                        else if ( ((synpred22_MLSpaceDirectParser()&&(getSingleNumValFromVar(input.LT(1).getText())!=null))) ) {s = 11;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index10_7);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA10_8 = input.LA(1);

                         
                        int index10_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred16_MLSpaceDirectParser()) ) {s = 10;}

                        else if ( (synpred20_MLSpaceDirectParser()) ) {s = 13;}

                        else if ( (synpred22_MLSpaceDirectParser()) ) {s = 11;}

                         
                        input.seek(index10_8);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 10, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA12_eotS =
        "\17\uffff";
    static final String DFA12_eofS =
        "\1\uffff\1\16\15\uffff";
    static final String DFA12_minS =
        "\1\55\1\4\15\uffff";
    static final String DFA12_maxS =
        "\2\55\15\uffff";
    static final String DFA12_acceptS =
        "\2\uffff\14\1\1\2";
    static final String DFA12_specialS =
        "\1\uffff\1\0\15\uffff}>";
    static final String[] DFA12_transitionS = {
            "\1\1",
            "\1\7\1\uffff\1\15\1\uffff\1\6\1\uffff\1\16\3\uffff\1\2\1\4"+
            "\1\3\10\uffff\1\5\1\uffff\1\14\1\16\2\uffff\1\12\1\11\1\10\13"+
            "\uffff\1\13",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA12_eot = DFA.unpackEncodedString(DFA12_eotS);
    static final short[] DFA12_eof = DFA.unpackEncodedString(DFA12_eofS);
    static final char[] DFA12_min = DFA.unpackEncodedStringToUnsignedChars(DFA12_minS);
    static final char[] DFA12_max = DFA.unpackEncodedStringToUnsignedChars(DFA12_maxS);
    static final short[] DFA12_accept = DFA.unpackEncodedString(DFA12_acceptS);
    static final short[] DFA12_special = DFA.unpackEncodedString(DFA12_specialS);
    static final short[][] DFA12_transition;

    static {
        int numStates = DFA12_transitionS.length;
        DFA12_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA12_transition[i] = DFA.unpackEncodedString(DFA12_transitionS[i]);
        }
    }

    class DFA12 extends DFA {

        public DFA12(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 12;
            this.eot = DFA12_eot;
            this.eof = DFA12_eof;
            this.min = DFA12_min;
            this.max = DFA12_max;
            this.accept = DFA12_accept;
            this.special = DFA12_special;
            this.transition = DFA12_transition;
        }
        public String getDescription() {
            return "231:5: ( ( attribute )=>attWE= attribute | an= ID )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA12_1 = input.LA(1);

                         
                        int index12_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA12_1==EQ) && (synpred24_MLSpaceDirectParser())) {s = 2;}

                        else if ( (LA12_1==GREATERTHAN) && (synpred24_MLSpaceDirectParser())) {s = 3;}

                        else if ( (LA12_1==LESSTHAN) && (synpred24_MLSpaceDirectParser())) {s = 4;}

                        else if ( (LA12_1==IN) && (synpred24_MLSpaceDirectParser())) {s = 5;}

                        else if ( (LA12_1==COLON) && (synpred24_MLSpaceDirectParser())) {s = 6;}

                        else if ( (LA12_1==L_BRACKET) && (synpred24_MLSpaceDirectParser())) {s = 7;}

                        else if ( (LA12_1==MINUS) && (synpred24_MLSpaceDirectParser())) {s = 8;}

                        else if ( (LA12_1==PLUS) && (synpred24_MLSpaceDirectParser())) {s = 9;}

                        else if ( (LA12_1==FLOAT) && (synpred24_MLSpaceDirectParser())) {s = 10;}

                        else if ( (LA12_1==ID) && (synpred24_MLSpaceDirectParser())) {s = 11;}

                        else if ( (LA12_1==L_PAREN) && (synpred24_MLSpaceDirectParser())) {s = 12;}

                        else if ( (LA12_1==L_BRACE) && (synpred24_MLSpaceDirectParser())) {s = 13;}

                        else if ( (LA12_1==EOF||LA12_1==COMMA||LA12_1==R_PAREN) ) {s = 14;}

                         
                        input.seek(index12_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 12, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA15_eotS =
        "\17\uffff";
    static final String DFA15_eofS =
        "\17\uffff";
    static final String DFA15_minS =
        "\1\4\1\uffff\2\4\1\uffff\1\4\2\0\1\4\6\0";
    static final String DFA15_maxS =
        "\1\55\1\uffff\2\55\1\uffff\1\55\2\0\1\55\6\0";
    static final String DFA15_acceptS =
        "\1\uffff\1\1\2\uffff\1\2\12\uffff";
    static final String DFA15_specialS =
        "\6\uffff\1\4\1\2\1\uffff\1\1\1\0\1\5\1\3\1\7\1\6}>";
    static final String[] DFA15_transitionS = {
            "\1\4\1\uffff\1\4\1\uffff\1\4\5\uffff\1\1\1\3\1\2\10\uffff\1"+
            "\1\1\uffff\1\4\3\uffff\3\4\13\uffff\1\4",
            "",
            "\1\1\11\uffff\1\5\14\uffff\1\1\3\uffff\1\6\2\1\5\uffff\2\1"+
            "\4\uffff\1\7",
            "\1\1\11\uffff\1\10\14\uffff\1\1\3\uffff\1\11\2\1\5\uffff\2"+
            "\1\4\uffff\1\12",
            "",
            "\1\1\26\uffff\1\1\3\uffff\1\13\2\1\5\uffff\2\1\4\uffff\1\14",
            "\1\uffff",
            "\1\uffff",
            "\1\1\26\uffff\1\1\3\uffff\1\15\2\1\5\uffff\2\1\4\uffff\1\16",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff"
    };

    static final short[] DFA15_eot = DFA.unpackEncodedString(DFA15_eotS);
    static final short[] DFA15_eof = DFA.unpackEncodedString(DFA15_eofS);
    static final char[] DFA15_min = DFA.unpackEncodedStringToUnsignedChars(DFA15_minS);
    static final char[] DFA15_max = DFA.unpackEncodedStringToUnsignedChars(DFA15_maxS);
    static final short[] DFA15_accept = DFA.unpackEncodedString(DFA15_acceptS);
    static final short[] DFA15_special = DFA.unpackEncodedString(DFA15_specialS);
    static final short[][] DFA15_transition;

    static {
        int numStates = DFA15_transitionS.length;
        DFA15_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA15_transition[i] = DFA.unpackEncodedString(DFA15_transitionS[i]);
        }
    }

    class DFA15 extends DFA {

        public DFA15(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 15;
            this.eot = DFA15_eot;
            this.eof = DFA15_eof;
            this.min = DFA15_min;
            this.max = DFA15_max;
            this.accept = DFA15_accept;
            this.special = DFA15_special;
            this.transition = DFA15_transition;
        }
        public String getDescription() {
            return "237:3: (vi= var_interval | ( ( COLON )? v= valset_or_const ) )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA15_10 = input.LA(1);

                         
                        int index15_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred26_MLSpaceDirectParser()) ) {s = 1;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index15_10);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA15_9 = input.LA(1);

                         
                        int index15_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred26_MLSpaceDirectParser()) ) {s = 1;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index15_9);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA15_7 = input.LA(1);

                         
                        int index15_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred26_MLSpaceDirectParser()) ) {s = 1;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index15_7);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA15_12 = input.LA(1);

                         
                        int index15_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred26_MLSpaceDirectParser()) ) {s = 1;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index15_12);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA15_6 = input.LA(1);

                         
                        int index15_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred26_MLSpaceDirectParser()) ) {s = 1;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index15_6);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA15_11 = input.LA(1);

                         
                        int index15_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred26_MLSpaceDirectParser()) ) {s = 1;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index15_11);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA15_14 = input.LA(1);

                         
                        int index15_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred26_MLSpaceDirectParser()) ) {s = 1;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index15_14);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA15_13 = input.LA(1);

                         
                        int index15_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred26_MLSpaceDirectParser()) ) {s = 1;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index15_13);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 15, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA24_eotS =
        "\41\uffff";
    static final String DFA24_eofS =
        "\41\uffff";
    static final String DFA24_minS =
        "\1\6\1\4\3\uffff\1\12\2\uffff\1\0\10\uffff\1\4\1\uffff\1\7\15\uffff";
    static final String DFA24_maxS =
        "\1\6\1\55\3\uffff\1\54\2\uffff\1\0\10\uffff\1\55\1\uffff\1\54\15"+
        "\uffff";
    static final String DFA24_acceptS =
        "\2\uffff\3\1\1\uffff\2\1\1\uffff\10\1\1\uffff\1\2\1\uffff\15\1";
    static final String DFA24_specialS =
        "\1\uffff\1\2\3\uffff\1\1\2\uffff\1\3\10\uffff\1\4\1\uffff\1\0\15"+
        "\uffff}>";
    static final String[] DFA24_transitionS = {
            "\1\1",
            "\1\7\2\uffff\1\10\23\uffff\1\6\3\uffff\1\4\1\3\1\2\13\uffff"+
            "\1\5",
            "",
            "",
            "",
            "\1\21\25\uffff\1\17\1\20\1\15\1\16\1\14\1\11\1\12\5\uffff"+
            "\1\13",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\30\26\uffff\1\27\3\uffff\1\26\1\25\1\24\13\uffff\1\23",
            "",
            "\1\10\2\uffff\1\21\25\uffff\1\37\1\40\1\35\1\36\1\34\1\31"+
            "\1\32\5\uffff\1\33",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA24_eot = DFA.unpackEncodedString(DFA24_eotS);
    static final short[] DFA24_eof = DFA.unpackEncodedString(DFA24_eofS);
    static final char[] DFA24_min = DFA.unpackEncodedStringToUnsignedChars(DFA24_minS);
    static final char[] DFA24_max = DFA.unpackEncodedStringToUnsignedChars(DFA24_maxS);
    static final short[] DFA24_accept = DFA.unpackEncodedString(DFA24_acceptS);
    static final short[] DFA24_special = DFA.unpackEncodedString(DFA24_specialS);
    static final short[][] DFA24_transition;

    static {
        int numStates = DFA24_transitionS.length;
        DFA24_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA24_transition[i] = DFA.unpackEncodedString(DFA24_transitionS[i]);
        }
    }

    class DFA24 extends DFA {

        public DFA24(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 24;
            this.eot = DFA24_eot;
            this.eof = DFA24_eof;
            this.min = DFA24_min;
            this.max = DFA24_max;
            this.accept = DFA24_accept;
            this.special = DFA24_special;
            this.transition = DFA24_transition;
        }
        public String getDescription() {
            return "298:1: set returns [Set<?> set] : ( ( numset )=> numset | idset );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA24_19 = input.LA(1);

                         
                        int index24_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA24_19==R_BRACE) ) {s = 8;}

                        else if ( (LA24_19==COMMA) ) {s = 17;}

                        else if ( (LA24_19==SQR) && (synpred56_MLSpaceDirectParser())) {s = 25;}

                        else if ( (LA24_19==CUB) && (synpred56_MLSpaceDirectParser())) {s = 26;}

                        else if ( (LA24_19==DEGREES) && (synpred56_MLSpaceDirectParser())) {s = 27;}

                        else if ( (LA24_19==POW) && (synpred56_MLSpaceDirectParser())) {s = 28;}

                        else if ( (LA24_19==TIMES) && (synpred56_MLSpaceDirectParser())) {s = 29;}

                        else if ( (LA24_19==DIV) && (synpred56_MLSpaceDirectParser())) {s = 30;}

                        else if ( (LA24_19==PLUS) && (synpred56_MLSpaceDirectParser())) {s = 31;}

                        else if ( (LA24_19==MINUS) && (synpred56_MLSpaceDirectParser())) {s = 32;}

                         
                        input.seek(index24_19);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA24_5 = input.LA(1);

                         
                        int index24_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA24_5==SQR) && (synpred56_MLSpaceDirectParser())) {s = 9;}

                        else if ( (LA24_5==CUB) && (synpred56_MLSpaceDirectParser())) {s = 10;}

                        else if ( (LA24_5==DEGREES) && (synpred56_MLSpaceDirectParser())) {s = 11;}

                        else if ( (LA24_5==POW) && (synpred56_MLSpaceDirectParser())) {s = 12;}

                        else if ( (LA24_5==TIMES) && (synpred56_MLSpaceDirectParser())) {s = 13;}

                        else if ( (LA24_5==DIV) && (synpred56_MLSpaceDirectParser())) {s = 14;}

                        else if ( (LA24_5==PLUS) && (synpred56_MLSpaceDirectParser())) {s = 15;}

                        else if ( (LA24_5==MINUS) && (synpred56_MLSpaceDirectParser())) {s = 16;}

                        else if ( (LA24_5==COMMA) ) {s = 17;}

                         
                        input.seek(index24_5);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA24_1 = input.LA(1);

                         
                        int index24_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA24_1==MINUS) && (synpred56_MLSpaceDirectParser())) {s = 2;}

                        else if ( (LA24_1==PLUS) && (synpred56_MLSpaceDirectParser())) {s = 3;}

                        else if ( (LA24_1==FLOAT) && (synpred56_MLSpaceDirectParser())) {s = 4;}

                        else if ( (LA24_1==ID) ) {s = 5;}

                        else if ( (LA24_1==L_PAREN) && (synpred56_MLSpaceDirectParser())) {s = 6;}

                        else if ( (LA24_1==L_BRACKET) && (synpred56_MLSpaceDirectParser())) {s = 7;}

                        else if ( (LA24_1==R_BRACE) ) {s = 8;}

                         
                        input.seek(index24_1);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA24_8 = input.LA(1);

                         
                        int index24_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred56_MLSpaceDirectParser()) ) {s = 16;}

                        else if ( (true) ) {s = 18;}

                         
                        input.seek(index24_8);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA24_17 = input.LA(1);

                         
                        int index24_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA24_17==ID) ) {s = 19;}

                        else if ( (LA24_17==MINUS) && (synpred56_MLSpaceDirectParser())) {s = 20;}

                        else if ( (LA24_17==PLUS) && (synpred56_MLSpaceDirectParser())) {s = 21;}

                        else if ( (LA24_17==FLOAT) && (synpred56_MLSpaceDirectParser())) {s = 22;}

                        else if ( (LA24_17==L_PAREN) && (synpred56_MLSpaceDirectParser())) {s = 23;}

                        else if ( (LA24_17==L_BRACKET) && (synpred56_MLSpaceDirectParser())) {s = 24;}

                         
                        input.seek(index24_17);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 24, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA49_eotS =
        "\12\uffff";
    static final String DFA49_eofS =
        "\12\uffff";
    static final String DFA49_minS =
        "\1\4\6\0\3\uffff";
    static final String DFA49_maxS =
        "\1\55\6\0\3\uffff";
    static final String DFA49_acceptS =
        "\7\uffff\1\2\1\uffff\1\1";
    static final String DFA49_specialS =
        "\1\uffff\1\0\1\1\1\2\1\3\1\4\1\5\3\uffff}>";
    static final String[] DFA49_transitionS = {
            "\1\6\26\uffff\1\5\3\uffff\1\3\1\2\1\1\5\uffff\2\7\4\uffff\1"+
            "\4",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            ""
    };

    static final short[] DFA49_eot = DFA.unpackEncodedString(DFA49_eotS);
    static final short[] DFA49_eof = DFA.unpackEncodedString(DFA49_eofS);
    static final char[] DFA49_min = DFA.unpackEncodedStringToUnsignedChars(DFA49_minS);
    static final char[] DFA49_max = DFA.unpackEncodedStringToUnsignedChars(DFA49_maxS);
    static final short[] DFA49_accept = DFA.unpackEncodedString(DFA49_acceptS);
    static final short[] DFA49_special = DFA.unpackEncodedString(DFA49_specialS);
    static final short[][] DFA49_transition;

    static {
        int numStates = DFA49_transitionS.length;
        DFA49_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA49_transition[i] = DFA.unpackEncodedString(DFA49_transitionS[i]);
        }
    }

    class DFA49 extends DFA {

        public DFA49(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 49;
            this.eot = DFA49_eot;
            this.eof = DFA49_eof;
            this.min = DFA49_min;
            this.max = DFA49_max;
            this.accept = DFA49_accept;
            this.special = DFA49_special;
            this.transition = DFA49_transition;
        }
        public String getDescription() {
            return "393:11: ( (ne= numexpr ) | (nn= node ) )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA49_1 = input.LA(1);

                         
                        int index49_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred83_MLSpaceDirectParser()) ) {s = 9;}

                        else if ( (true) ) {s = 7;}

                         
                        input.seek(index49_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA49_2 = input.LA(1);

                         
                        int index49_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred83_MLSpaceDirectParser()) ) {s = 9;}

                        else if ( (true) ) {s = 7;}

                         
                        input.seek(index49_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA49_3 = input.LA(1);

                         
                        int index49_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred83_MLSpaceDirectParser()) ) {s = 9;}

                        else if ( (true) ) {s = 7;}

                         
                        input.seek(index49_3);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA49_4 = input.LA(1);

                         
                        int index49_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((synpred83_MLSpaceDirectParser()&&(getSingleNumValFromVar(input.LT(1).getText())!=null))) ) {s = 9;}

                        else if ( (true) ) {s = 7;}

                         
                        input.seek(index49_4);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA49_5 = input.LA(1);

                         
                        int index49_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred83_MLSpaceDirectParser()) ) {s = 9;}

                        else if ( (true) ) {s = 7;}

                         
                        input.seek(index49_5);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA49_6 = input.LA(1);

                         
                        int index49_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred83_MLSpaceDirectParser()) ) {s = 9;}

                        else if ( (true) ) {s = 7;}

                         
                        input.seek(index49_6);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 49, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_set_in_entsep0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_model_name_in_fullmodel80 = new BitSet(new long[]{0x0000200000000200L});
    public static final BitSet FOLLOW_SEMIC_in_fullmodel82 = new BitSet(new long[]{0x0000200000000200L});
    public static final BitSet FOLLOW_variable_defs_in_fullmodel89 = new BitSet(new long[]{0x0000200000000200L});
    public static final BitSet FOLLOW_species_defs_in_fullmodel93 = new BitSet(new long[]{0x0000200388080010L});
    public static final BitSet FOLLOW_init_in_fullmodel105 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_SEMIC_in_fullmodel109 = new BitSet(new long[]{0x0000200388080010L});
    public static final BitSet FOLLOW_rules_in_fullmodel113 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_rules_in_fullmodel126 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_SEMIC_in_fullmodel128 = new BitSet(new long[]{0x0000200388080010L});
    public static final BitSet FOLLOW_init_in_fullmodel132 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_fullmodel141 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MODELNAMEKW_in_model_name150 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_ID_in_model_name152 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_def_in_variable_defs164 = new BitSet(new long[]{0x0000200000000202L});
    public static final BitSet FOLLOW_SEMIC_in_variable_defs166 = new BitSet(new long[]{0x0000200000000002L});
    public static final BitSet FOLLOW_ID_in_variable_def178 = new BitSet(new long[]{0x0000000020004000L});
    public static final BitSet FOLLOW_BECOMES_in_variable_def181 = new BitSet(new long[]{0x0000200388098050L});
    public static final BitSet FOLLOW_EQ_in_variable_def184 = new BitSet(new long[]{0x0000200388098050L});
    public static final BitSet FOLLOW_valset_or_const_in_variable_def190 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attribute_def_in_attributes_def212 = new BitSet(new long[]{0x0000000000000602L});
    public static final BitSet FOLLOW_COMMA_in_attributes_def218 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_SEMIC_in_attributes_def221 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_attribute_def_in_attributes_def227 = new BitSet(new long[]{0x0000000000000602L});
    public static final BitSet FOLLOW_ID_in_attribute_def247 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_COLON_in_attribute_def251 = new BitSet(new long[]{0x0000200388098050L});
    public static final BitSet FOLLOW_valset_or_const_in_attribute_def256 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interval_in_valset_or_const276 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_range_in_valset_or_const288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_valset_or_const300 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_vector_in_valset_or_const312 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numexpr_in_valset_or_const324 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_valset_or_const331 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attributeWE_in_attributes356 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_COMMA_in_attributes361 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_attributeWE_in_attributes366 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_ID_in_attributeWE391 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_BECOMES_in_attributeWE393 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_attribute_in_attributeWE410 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_attributeWE422 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attribute_in_attributeWE434 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_attribute451 = new BitSet(new long[]{0x000020038A09C150L});
    public static final BitSet FOLLOW_var_interval_in_attribute461 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_attribute470 = new BitSet(new long[]{0x0000200388098050L});
    public static final BitSet FOLLOW_valset_or_const_in_attribute475 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQ_in_var_interval496 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_EQ_in_var_interval498 = new BitSet(new long[]{0x0000218388000010L});
    public static final BitSet FOLLOW_node_in_var_interval500 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATERTHAN_in_var_interval508 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_EQ_in_var_interval510 = new BitSet(new long[]{0x0000218388000010L});
    public static final BitSet FOLLOW_node_in_var_interval512 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATERTHAN_in_var_interval520 = new BitSet(new long[]{0x0000218388000010L});
    public static final BitSet FOLLOW_node_in_var_interval522 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESSTHAN_in_var_interval530 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_EQ_in_var_interval532 = new BitSet(new long[]{0x0000218388000010L});
    public static final BitSet FOLLOW_node_in_var_interval534 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESSTHAN_in_var_interval542 = new BitSet(new long[]{0x0000218388000010L});
    public static final BitSet FOLLOW_node_in_var_interval544 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_var_interval552 = new BitSet(new long[]{0x0000000008000010L});
    public static final BitSet FOLLOW_set_in_var_interval556 = new BitSet(new long[]{0x0000218388000010L});
    public static final BitSet FOLLOW_node_in_var_interval564 = new BitSet(new long[]{0x0000000000020400L});
    public static final BitSet FOLLOW_set_in_var_interval566 = new BitSet(new long[]{0x0000218388000010L});
    public static final BitSet FOLLOW_node_in_var_interval574 = new BitSet(new long[]{0x0000000010000020L});
    public static final BitSet FOLLOW_set_in_var_interval578 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multNode_in_node607 = new BitSet(new long[]{0x0000000300000002L});
    public static final BitSet FOLLOW_PLUS_in_node621 = new BitSet(new long[]{0x0000218388000010L});
    public static final BitSet FOLLOW_multNode_in_node625 = new BitSet(new long[]{0x0000000300000002L});
    public static final BitSet FOLLOW_MINUS_in_node638 = new BitSet(new long[]{0x0000218388000010L});
    public static final BitSet FOLLOW_multNode_in_node642 = new BitSet(new long[]{0x0000000300000002L});
    public static final BitSet FOLLOW_atomNode_in_multNode671 = new BitSet(new long[]{0x0000000C00000002L});
    public static final BitSet FOLLOW_TIMES_in_multNode683 = new BitSet(new long[]{0x0000218388000010L});
    public static final BitSet FOLLOW_atomNode_in_multNode687 = new BitSet(new long[]{0x0000000C00000002L});
    public static final BitSet FOLLOW_DIV_in_multNode700 = new BitSet(new long[]{0x0000218388000010L});
    public static final BitSet FOLLOW_atomNode_in_multNode704 = new BitSet(new long[]{0x0000000C00000002L});
    public static final BitSet FOLLOW_MINUS_in_atomNode742 = new BitSet(new long[]{0x0000218088000010L});
    public static final BitSet FOLLOW_PLUS_in_atomNode748 = new BitSet(new long[]{0x0000218088000010L});
    public static final BitSet FOLLOW_numval_in_atomNode760 = new BitSet(new long[]{0x0000107000000002L});
    public static final BitSet FOLLOW_ID_in_atomNode769 = new BitSet(new long[]{0x0000107000000002L});
    public static final BitSet FOLLOW_L_PAREN_in_atomNode779 = new BitSet(new long[]{0x0000218388000010L});
    public static final BitSet FOLLOW_node_in_atomNode783 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_R_PAREN_in_atomNode785 = new BitSet(new long[]{0x0000107000000002L});
    public static final BitSet FOLLOW_L_BRACKET_in_atomNode796 = new BitSet(new long[]{0x0000218388000010L});
    public static final BitSet FOLLOW_node_in_atomNode801 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_R_BRACKET_in_atomNode803 = new BitSet(new long[]{0x0000107000000002L});
    public static final BitSet FOLLOW_MIN_in_atomNode815 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_L_PAREN_in_atomNode817 = new BitSet(new long[]{0x0000218388000010L});
    public static final BitSet FOLLOW_node_in_atomNode821 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_COMMA_in_atomNode823 = new BitSet(new long[]{0x0000218388000010L});
    public static final BitSet FOLLOW_node_in_atomNode827 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_R_PAREN_in_atomNode829 = new BitSet(new long[]{0x0000107000000002L});
    public static final BitSet FOLLOW_MAX_in_atomNode843 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_L_PAREN_in_atomNode845 = new BitSet(new long[]{0x0000218388000010L});
    public static final BitSet FOLLOW_node_in_atomNode849 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_COMMA_in_atomNode851 = new BitSet(new long[]{0x0000218388000010L});
    public static final BitSet FOLLOW_node_in_atomNode855 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_R_PAREN_in_atomNode857 = new BitSet(new long[]{0x0000107000000002L});
    public static final BitSet FOLLOW_SQR_in_atomNode880 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CUB_in_atomNode891 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DEGREES_in_atomNode901 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_POW_in_atomNode911 = new BitSet(new long[]{0x0000218388000010L});
    public static final BitSet FOLLOW_atomNode_in_atomNode915 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_L_BRACKET_in_interval944 = new BitSet(new long[]{0x0000200080000000L});
    public static final BitSet FOLLOW_numval_in_interval949 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_DOTS_in_interval953 = new BitSet(new long[]{0x0000200080000000L});
    public static final BitSet FOLLOW_numval_in_interval958 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_R_BRACKET_in_interval962 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATERTHAN_in_interval971 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_EQ_in_interval973 = new BitSet(new long[]{0x0000200080000000L});
    public static final BitSet FOLLOW_numval_in_interval979 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESSTHAN_in_interval989 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_EQ_in_interval991 = new BitSet(new long[]{0x0000200080000000L});
    public static final BitSet FOLLOW_numval_in_interval997 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATERTHAN_in_interval1007 = new BitSet(new long[]{0x0000200080000000L});
    public static final BitSet FOLLOW_numval_in_interval1013 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESSTHAN_in_interval1023 = new BitSet(new long[]{0x0000200080000000L});
    public static final BitSet FOLLOW_numval_in_interval1029 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numexpr_in_range1052 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_COLON_in_range1054 = new BitSet(new long[]{0x0000200388080010L});
    public static final BitSet FOLLOW_numexpr_in_range1059 = new BitSet(new long[]{0x0000000000000102L});
    public static final BitSet FOLLOW_COLON_in_range1062 = new BitSet(new long[]{0x0000200388080010L});
    public static final BitSet FOLLOW_numexpr_in_range1067 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numset_in_set1098 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_idset_in_set1104 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_L_BRACE_in_idset1123 = new BitSet(new long[]{0x0000200000000080L});
    public static final BitSet FOLLOW_ID_in_idset1129 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_COMMA_in_idset1134 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_ID_in_idset1139 = new BitSet(new long[]{0x0000000000000480L});
    public static final BitSet FOLLOW_R_BRACE_in_idset1147 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_L_BRACE_in_numset1165 = new BitSet(new long[]{0x0000200388080090L});
    public static final BitSet FOLLOW_numexpr_in_numset1171 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_COMMA_in_numset1176 = new BitSet(new long[]{0x0000200388080010L});
    public static final BitSet FOLLOW_numexpr_in_numset1181 = new BitSet(new long[]{0x0000000000000480L});
    public static final BitSet FOLLOW_R_BRACE_in_numset1189 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_L_PAREN_in_vector1206 = new BitSet(new long[]{0x0000200388080010L});
    public static final BitSet FOLLOW_numexpr_in_vector1211 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_COMMA_in_vector1216 = new BitSet(new long[]{0x0000200388080010L});
    public static final BitSet FOLLOW_numexpr_in_vector1221 = new BitSet(new long[]{0x0000000010000400L});
    public static final BitSet FOLLOW_R_PAREN_in_vector1227 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numexpr_in_intval_or_var1246 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_numval1270 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_numval1281 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_species_def_in_species_defs1306 = new BitSet(new long[]{0x0000200000000202L});
    public static final BitSet FOLLOW_SEMIC_in_species_defs1308 = new BitSet(new long[]{0x0000200000000202L});
    public static final BitSet FOLLOW_ID_in_species_def1319 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_L_PAREN_in_species_def1321 = new BitSet(new long[]{0x0000200010000000L});
    public static final BitSet FOLLOW_attributes_def_in_species_def1326 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_R_PAREN_in_species_def1330 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_bindingsitesdef_in_species_def1333 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESSTHAN_in_bindingsitesdef1355 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_bindingsitedef_in_bindingsitesdef1362 = new BitSet(new long[]{0x0000000000010400L});
    public static final BitSet FOLLOW_COMMA_in_bindingsitesdef1368 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_bindingsitedef_in_bindingsitesdef1373 = new BitSet(new long[]{0x0000000000010400L});
    public static final BitSet FOLLOW_GREATERTHAN_in_bindingsitesdef1381 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_bindingsitedef1398 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_COLON_in_bindingsitedef1400 = new BitSet(new long[]{0x0000200388080010L});
    public static final BitSet FOLLOW_numexpr_in_bindingsitedef1403 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_species1418 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entity_match_in_entities_match1445 = new BitSet(new long[]{0x0000000100040402L});
    public static final BitSet FOLLOW_entsep_in_entities_match1450 = new BitSet(new long[]{0x0000200388080010L});
    public static final BitSet FOLLOW_entity_match_in_entities_match1455 = new BitSet(new long[]{0x0000000100040402L});
    public static final BitSet FOLLOW_species_in_entity_match1482 = new BitSet(new long[]{0x0000000008008002L});
    public static final BitSet FOLLOW_L_PAREN_in_entity_match1485 = new BitSet(new long[]{0x0000200010000000L});
    public static final BitSet FOLLOW_attributes_in_entity_match1490 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_R_PAREN_in_entity_match1493 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_bindingsites_in_entity_match1507 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESSTHAN_in_bindingsites1531 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_bindingsite_in_bindingsites1538 = new BitSet(new long[]{0x0000000000010400L});
    public static final BitSet FOLLOW_COMMA_in_bindingsites1544 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_bindingsite_in_bindingsites1549 = new BitSet(new long[]{0x0000000000010400L});
    public static final BitSet FOLLOW_GREATERTHAN_in_bindingsites1557 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_bindingsite1572 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_COLON_in_bindingsite1574 = new BitSet(new long[]{0x0000200388380010L});
    public static final BitSet FOLLOW_entity_match_in_bindingsite1583 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FREE_in_bindingsite1591 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OCC_in_bindingsite1598 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entity_result_in_entities_result1630 = new BitSet(new long[]{0x0000000100040402L});
    public static final BitSet FOLLOW_entsep_in_entities_result1636 = new BitSet(new long[]{0x0000200388080010L});
    public static final BitSet FOLLOW_entity_result_in_entities_result1641 = new BitSet(new long[]{0x0000000100040402L});
    public static final BitSet FOLLOW_species_in_entity_result1662 = new BitSet(new long[]{0x0000000008008002L});
    public static final BitSet FOLLOW_L_PAREN_in_entity_result1670 = new BitSet(new long[]{0x0000200010000000L});
    public static final BitSet FOLLOW_ID_in_entity_result1676 = new BitSet(new long[]{0x0000000F20004100L});
    public static final BitSet FOLLOW_valmod_in_entity_result1680 = new BitSet(new long[]{0x0000000010000400L});
    public static final BitSet FOLLOW_COMMA_in_entity_result1692 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_ID_in_entity_result1696 = new BitSet(new long[]{0x0000000F20004100L});
    public static final BitSet FOLLOW_valmod_in_entity_result1700 = new BitSet(new long[]{0x0000000010000400L});
    public static final BitSet FOLLOW_R_PAREN_in_entity_result1712 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_bindingactions_in_entity_result1727 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_op_in_valmod1747 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_EQ_in_valmod1749 = new BitSet(new long[]{0x0000218388080010L});
    public static final BitSet FOLLOW_numexpr_in_valmod1755 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_node_in_valmod1774 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_valmod1785 = new BitSet(new long[]{0x0000218388000010L});
    public static final BitSet FOLLOW_node_in_valmod1793 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_valmod1802 = new BitSet(new long[]{0x0000200388098050L});
    public static final BitSet FOLLOW_valset_or_const_in_valmod1806 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_op0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESSTHAN_in_bindingactions1845 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_bindingaction_in_bindingactions1852 = new BitSet(new long[]{0x0000000000010400L});
    public static final BitSet FOLLOW_COMMA_in_bindingactions1858 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_bindingaction_in_bindingactions1862 = new BitSet(new long[]{0x0000000000010400L});
    public static final BitSet FOLLOW_GREATERTHAN_in_bindingactions1869 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_bindingaction1884 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_COLON_in_bindingaction1886 = new BitSet(new long[]{0x0000000001C00000L});
    public static final BitSet FOLLOW_BIND_in_bindingaction1895 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RELEASE_in_bindingaction1904 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REPLACE_in_bindingaction1912 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_rules1939 = new BitSet(new long[]{0x0000200388080212L});
    public static final BitSet FOLLOW_SEMIC_in_rules1941 = new BitSet(new long[]{0x0000200388080012L});
    public static final BitSet FOLLOW_rule_left_hand_side_in_rule1965 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_ARROW_in_rule1969 = new BitSet(new long[]{0x0000200388081010L});
    public static final BitSet FOLLOW_rule_right_hand_side_in_rule1976 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_AT_in_rule1983 = new BitSet(new long[]{0x0000218388000010L});
    public static final BitSet FOLLOW_node_in_rule1987 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entity_match_in_rule_left_hand_side2018 = new BitSet(new long[]{0x0000000100040412L});
    public static final BitSet FOLLOW_L_BRACKET_in_rule_left_hand_side2026 = new BitSet(new long[]{0x0000200388080032L});
    public static final BitSet FOLLOW_entsep_in_rule_left_hand_side2034 = new BitSet(new long[]{0x0000200388080032L});
    public static final BitSet FOLLOW_entity_match_in_rule_left_hand_side2043 = new BitSet(new long[]{0x0000000100040422L});
    public static final BitSet FOLLOW_entsep_in_rule_left_hand_side2052 = new BitSet(new long[]{0x0000200388080010L});
    public static final BitSet FOLLOW_entity_match_in_rule_left_hand_side2056 = new BitSet(new long[]{0x0000000100040422L});
    public static final BitSet FOLLOW_R_BRACKET_in_rule_left_hand_side2070 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entity_result_in_rule_right_hand_side2097 = new BitSet(new long[]{0x0000000100040412L});
    public static final BitSet FOLLOW_L_BRACKET_in_rule_right_hand_side2106 = new BitSet(new long[]{0x0000200388080032L});
    public static final BitSet FOLLOW_entsep_in_rule_right_hand_side2112 = new BitSet(new long[]{0x0000200388080032L});
    public static final BitSet FOLLOW_entity_result_in_rule_right_hand_side2121 = new BitSet(new long[]{0x0000000100040422L});
    public static final BitSet FOLLOW_entsep_in_rule_right_hand_side2130 = new BitSet(new long[]{0x0000200388080010L});
    public static final BitSet FOLLOW_entity_result_in_rule_right_hand_side2134 = new BitSet(new long[]{0x0000000100040422L});
    public static final BitSet FOLLOW_R_BRACKET_in_rule_right_hand_side2149 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_init_element_in_init2171 = new BitSet(new long[]{0x0000000100040402L});
    public static final BitSet FOLLOW_entsep_in_init2177 = new BitSet(new long[]{0x0000200388080010L});
    public static final BitSet FOLLOW_init_element_in_init2182 = new BitSet(new long[]{0x0000000100040402L});
    public static final BitSet FOLLOW_for_each_in_init_element2206 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_intval_or_var_in_init_element2215 = new BitSet(new long[]{0x0000200388080010L});
    public static final BitSet FOLLOW_entity_result_in_init_element2229 = new BitSet(new long[]{0x0000000000000212L});
    public static final BitSet FOLLOW_L_BRACKET_in_init_element2248 = new BitSet(new long[]{0x0000200388080030L});
    public static final BitSet FOLLOW_entities_result_in_init_element2253 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_R_BRACKET_in_init_element2256 = new BitSet(new long[]{0x0000000000000212L});
    public static final BitSet FOLLOW_L_BRACKET_in_init_element2274 = new BitSet(new long[]{0x0000200388080010L});
    public static final BitSet FOLLOW_init_in_init_element2277 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_R_BRACKET_in_init_element2280 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_SEMIC_in_init_element2286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FOR_in_for_each2316 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_for_var_in_for_each2319 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_L_BRACE_in_for_each2324 = new BitSet(new long[]{0x0000200388080010L});
    public static final BitSet FOLLOW_init_in_for_each2327 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_R_BRACE_in_for_each2333 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_ID_in_for_var2352 = new BitSet(new long[]{0x0000000020004000L});
    public static final BitSet FOLLOW_EQ_in_for_var2357 = new BitSet(new long[]{0x0000200388080050L});
    public static final BitSet FOLLOW_BECOMES_in_for_var2360 = new BitSet(new long[]{0x0000200388080050L});
    public static final BitSet FOLLOW_range_in_for_var2368 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_for_var2375 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multExpr_in_numexpr2396 = new BitSet(new long[]{0x0000000300000002L});
    public static final BitSet FOLLOW_PLUS_in_numexpr2409 = new BitSet(new long[]{0x0000200388080010L});
    public static final BitSet FOLLOW_multExpr_in_numexpr2413 = new BitSet(new long[]{0x0000000300000002L});
    public static final BitSet FOLLOW_MINUS_in_numexpr2426 = new BitSet(new long[]{0x0000200388080010L});
    public static final BitSet FOLLOW_multExpr_in_numexpr2430 = new BitSet(new long[]{0x0000000300000002L});
    public static final BitSet FOLLOW_atomExpr_in_multExpr2459 = new BitSet(new long[]{0x0000000C00000002L});
    public static final BitSet FOLLOW_TIMES_in_multExpr2472 = new BitSet(new long[]{0x0000200388080010L});
    public static final BitSet FOLLOW_atomExpr_in_multExpr2476 = new BitSet(new long[]{0x0000000C00000002L});
    public static final BitSet FOLLOW_DIV_in_multExpr2489 = new BitSet(new long[]{0x0000200388080010L});
    public static final BitSet FOLLOW_atomExpr_in_multExpr2493 = new BitSet(new long[]{0x0000000C00000002L});
    public static final BitSet FOLLOW_MINUS_in_atomExpr2530 = new BitSet(new long[]{0x0000200088000010L});
    public static final BitSet FOLLOW_PLUS_in_atomExpr2536 = new BitSet(new long[]{0x0000200088000010L});
    public static final BitSet FOLLOW_numval_in_atomExpr2547 = new BitSet(new long[]{0x0000107000000002L});
    public static final BitSet FOLLOW_L_PAREN_in_atomExpr2557 = new BitSet(new long[]{0x0000200388080010L});
    public static final BitSet FOLLOW_numexpr_in_atomExpr2561 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_R_PAREN_in_atomExpr2563 = new BitSet(new long[]{0x0000107000000002L});
    public static final BitSet FOLLOW_L_BRACKET_in_atomExpr2573 = new BitSet(new long[]{0x0000200388080010L});
    public static final BitSet FOLLOW_numexpr_in_atomExpr2578 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_R_BRACKET_in_atomExpr2580 = new BitSet(new long[]{0x0000107000000002L});
    public static final BitSet FOLLOW_SQR_in_atomExpr2603 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CUB_in_atomExpr2611 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DEGREES_in_atomExpr2621 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_POW_in_atomExpr2631 = new BitSet(new long[]{0x0000200388080010L});
    public static final BitSet FOLLOW_atomExpr_in_atomExpr2635 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_obsTargetEntry_in_observationTargets2673 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_SEMIC_in_observationTargets2680 = new BitSet(new long[]{0x000020038A080010L});
    public static final BitSet FOLLOW_obsTargetEntry_in_observationTargets2685 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_entities_match_in_obsTargetEntry2715 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_IN_in_obsTargetEntry2724 = new BitSet(new long[]{0x000020038A080010L});
    public static final BitSet FOLLOW_entities_match_in_obsTargetEntry2729 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_init_in_synpred7_MLSpaceDirectParser105 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_SEMIC_in_synpred7_MLSpaceDirectParser109 = new BitSet(new long[]{0x0000200388080010L});
    public static final BitSet FOLLOW_rules_in_synpred7_MLSpaceDirectParser113 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interval_in_synpred14_MLSpaceDirectParser276 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_range_in_synpred16_MLSpaceDirectParser288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_vector_in_synpred20_MLSpaceDirectParser312 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numexpr_in_synpred22_MLSpaceDirectParser324 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attribute_in_synpred24_MLSpaceDirectParser403 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_interval_in_synpred26_MLSpaceDirectParser461 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numval_in_synpred42_MLSpaceDirectParser760 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_synpred43_MLSpaceDirectParser769 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numset_in_synpred56_MLSpaceDirectParser1094 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_species_def_in_synpred63_MLSpaceDirectParser1302 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numexpr_in_synpred83_MLSpaceDirectParser1755 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMIC_in_synpred93_MLSpaceDirectParser1941 = new BitSet(new long[]{0x0000000000000002L});

}
