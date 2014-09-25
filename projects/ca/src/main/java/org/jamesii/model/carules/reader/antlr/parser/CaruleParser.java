// $ANTLR 3.2 Sep 23, 2009 14:05:07 C:/Documents and Settings/stefan/Desktop/James II Dev/James II Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g 2009-10-30 15:44:48

/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.reader.antlr.parser;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.*;
import org.jamesii.model.cacore.neighborhood.FreeNeighborhood;
import org.jamesii.model.cacore.neighborhood.INeighborhood;
import org.jamesii.model.cacore.neighborhood.MooreNeighborhood;
import org.jamesii.model.cacore.neighborhood.NeumannNeighborhood;
import org.jamesii.model.carules.CARule;
import org.jamesii.model.carules.ICACondition;

public class CaruleParser extends Parser {
  public static final String[] tokenNames = new String[] { "<invalid>",
      "<EOR>", "<DOWN>", "<UP>", "WOLFRAMRULE", "COLON", "INTEGER",
      "SEMICOLON", "DIMENSIONS", "CAVERSION", "DOUBLE", "NEIGHBORHOOD",
      "MOORE", "NEUMANN", "FREE", "LEFT_CURLEY", "LEFT_BRACE", "COMMA",
      "RIGHT_BRACE", "RIGHT_CURLEY", "STATE", "ID", "ARROW", "RULE", "OR",
      "AND", "NOT", "LEFT_PAREN", "RIGHT_PAREN", "TRUE", "FALSE", "C", "A",
      "V", "E", "R", "S", "I", "O", "N", "WHITESPACE", "LINECOMMENT",
      "LANGUAGECOMMENT", "GROUPCOMMENT", "UNEXPECTED", "B", "D", "F", "G", "H",
      "J", "K", "L", "M", "P", "Q", "T", "U", "W", "X", "Y", "Z" };

  public static final int WOLFRAMRULE = 4;

  public static final int RIGHT_CURLEY = 19;

  public static final int NOT = 26;

  public static final int LINECOMMENT = 41;

  public static final int ID = 21;

  public static final int AND = 25;

  public static final int EOF = -1;

  public static final int UNEXPECTED = 44;

  public static final int FREE = 14;

  public static final int DIMENSIONS = 8;

  public static final int RIGHT_PAREN = 28;

  public static final int NEUMANN = 13;

  public static final int COMMA = 17;

  public static final int DOUBLE = 10;

  public static final int CAVERSION = 9;

  public static final int D = 46;

  public static final int RIGHT_BRACE = 18;

  public static final int INTEGER = 6;

  public static final int E = 34;

  public static final int F = 47;

  public static final int STATE = 20;

  public static final int G = 48;

  public static final int A = 32;

  public static final int B = 45;

  public static final int RULE = 23;

  public static final int C = 31;

  public static final int L = 52;

  public static final int M = 53;

  public static final int N = 39;

  public static final int O = 38;

  public static final int H = 49;

  public static final int NEIGHBORHOOD = 11;

  public static final int I = 37;

  public static final int J = 50;

  public static final int K = 51;

  public static final int U = 57;

  public static final int T = 56;

  public static final int W = 58;

  public static final int WHITESPACE = 40;

  public static final int LANGUAGECOMMENT = 42;

  public static final int V = 33;

  public static final int Q = 55;

  public static final int SEMICOLON = 7;

  public static final int P = 54;

  public static final int S = 36;

  public static final int R = 35;

  public static final int TRUE = 29;

  public static final int Y = 60;

  public static final int X = 59;

  public static final int Z = 61;

  public static final int COLON = 5;

  public static final int LEFT_BRACE = 16;

  public static final int MOORE = 12;

  public static final int OR = 24;

  public static final int LEFT_PAREN = 27;

  public static final int ARROW = 22;

  public static final int GROUPCOMMENT = 43;

  public static final int FALSE = 30;

  public static final int LEFT_CURLEY = 15;

  // delegates
  // delegators

  public CaruleParser(TokenStream input) {
    this(input, new RecognizerSharedState());
  }

  public CaruleParser(TokenStream input, RecognizerSharedState state) {
    super(input, state);

  }

  @Override
  public String[] getTokenNames() {
    return CaruleParser.tokenNames;
  }

  @Override
  public String getGrammarFileName() {
    return "C:/Documents and Settings/stefan/Desktop/James II Dev/James II Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g";
  }

  private List<String> states = new ArrayList<>();

  private List<CARule> rules = new ArrayList<>();

  private List<CAProblemToken> problemTokens = new ArrayList<>();

  private int dimensions = 1;

  private INeighborhood neighborhood = null;

  /**
   * Extracts text from javadoc style comments.
   * 
   * @param comment
   *          the comment
   * 
   * @return the extracted text
   */
  private static String extractTextFromComment(String comment) {
    if (comment == null) {
      return null;
    }
    return comment.replaceFirst("(?s)/[*][*][\n\r]*(.*?)[*]/", "$1").trim();
  }

  private boolean addState(String state) {
    if (state == null) {
      return true;
    }
    if (!states.contains(state)) {
      states.add(state);
    } else {
      return false;
    }
    return true;
  }

  private void addRule(CARule rule) {
    if (!rules.contains(rule) && rule != null) {
      rules.add(rule);
    }
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
    if (e.token != null && e.token.getText() != null) {
      problemTokens.add(new CAProblemToken(CAProblemToken.RECOGNITION_ERROR,
          ((CommonToken) e.token).getStartIndex(), e.token.getText().length(),
          msg, e.token.getText()));
    }
  }

  public String getCommentForToken(int i) {
    for (int j = i - 1; j >= 0; j--) {
      Token token = getTokenStream().get(j);
      if (token.getChannel() != HIDDEN) {
        break;
      }
      if (token.getType() == WHITESPACE || token.getType() == LINECOMMENT
          || token.getType() == GROUPCOMMENT) {
        // TODO sr137: concatenate to formating
      } else if (token.getType() == LANGUAGECOMMENT) {
        // TODO sr137: add comment to element
        return extractTextFromComment(token.getText());
      } else {
        break;
      }
    }
    return null;
  }

  public static class camodel_return extends ParserRuleReturnScope {
    public List<String> states;

    public List<CARule> rules;

    public List<CAProblemToken> problems;

    public double version;

    public String versionComment;

    public int dimensions;

    public INeighborhood neighborhood;

    public boolean isWolfram;

    public int wolframRule;
  }

  // $ANTLR start "camodel"
  // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
  // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:132:7:
  // camodel returns [List<String> states, List<CARule> rules,
  // List<CAProblemToken> problems, double version, String versionComment, int
  // dimensions, INeighborhood neighborhood, boolean isWolfram, int wolframRule]
  // : (v= version ) ( (r= wolframrule ) | ( (d= dimension ) (n= neighborhood )?
  // ( statedef )+ ( carule )+ ) ) EOF ;
  public final CaruleParser.camodel_return camodel()
      throws RecognitionException {
    CaruleParser.camodel_return retval = new CaruleParser.camodel_return();
    retval.start = input.LT(1);

    CaruleParser.version_return v = null;

    int r = 0;

    CaruleParser.dimension_return d = null;

    INeighborhood n = null;

    states = new ArrayList<>();
    rules = new ArrayList<>();
    problemTokens = new ArrayList<>();
    retval.states = states;
    retval.rules = rules;
    retval.problems = problemTokens;
    retval.version = -1d;
    retval.versionComment = null;
    retval.dimensions = 1;
    retval.neighborhood = null;
    boolean neighborhoodDefined = false;
    retval.isWolfram = false;
    retval.wolframRule = 0;

    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:149:8:
      // ( (v= version ) ( (r= wolframrule ) | ( (d= dimension ) (n=
      // neighborhood )? ( statedef )+ ( carule )+ ) ) EOF )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:149:11:
      // (v= version ) ( (r= wolframrule ) | ( (d= dimension ) (n= neighborhood
      // )? ( statedef )+ ( carule )+ ) ) EOF
      {
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:149:11:
        // (v= version )
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:149:12:
        // v= version
        {
          pushFollow(FOLLOW_version_in_camodel79);
          v = version();

          state._fsp--;

          retval.version = v.version;
          retval.versionComment = v.comment;

        }

        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:149:72:
        // ( (r= wolframrule ) | ( (d= dimension ) (n= neighborhood )? (
        // statedef )+ ( carule )+ ) )
        int alt4 = 2;
        int LA4_0 = input.LA(1);

        if ((LA4_0 == WOLFRAMRULE)) {
          alt4 = 1;
        } else if ((LA4_0 == DIMENSIONS)) {
          alt4 = 2;
        } else {
          NoViableAltException nvae = new NoViableAltException("", 4, 0, input);

          throw nvae;
        }
        switch (alt4) {
        case 1:
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:149:73:
        // (r= wolframrule )
        {
          // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
          // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:149:73:
          // (r= wolframrule )
          // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
          // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:149:74:
          // r= wolframrule
          {
            pushFollow(FOLLOW_wolframrule_in_camodel88);
            r = wolframrule();

            state._fsp--;

            addState("DEAD");
            addState("ALIVE");
            addRule(new CARule(new BooleanCondition(true),
                new WolframCondition(r), 1, 1));
            addRule(new CARule(new BooleanCondition(true),
                new AntiWolframCondition(r), 0, 1));
            retval.neighborhood = new NeumannNeighborhood(1, null);
            neighborhood = retval.neighborhood;
            retval.isWolfram = true;
            retval.wolframRule = r;

          }

        }
          break;
        case 2:
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:159:40:
        // ( (d= dimension ) (n= neighborhood )? ( statedef )+ ( carule )+ )
        {
          // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
          // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:159:40:
          // ( (d= dimension ) (n= neighborhood )? ( statedef )+ ( carule )+ )
          // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
          // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:159:41:
          // (d= dimension ) (n= neighborhood )? ( statedef )+ ( carule )+
          {
            // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
            // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:159:41:
            // (d= dimension )
            // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
            // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:159:42:
            // d= dimension
            {
              pushFollow(FOLLOW_dimension_in_camodel136);
              d = dimension();

              state._fsp--;

              retval.dimensions = (d != null ? d.dimension : 0);
              dimensions = (d != null ? d.dimension : 0);

            }

            // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
            // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:163:40:
            // (n= neighborhood )?
            int alt1 = 2;
            int LA1_0 = input.LA(1);

            if ((LA1_0 == NEIGHBORHOOD)) {
              alt1 = 1;
            }
            switch (alt1) {
            case 1:
            // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
            // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:163:41:
            // n= neighborhood
            {
              pushFollow(FOLLOW_neighborhood_in_camodel184);
              n = neighborhood();

              state._fsp--;

              retval.neighborhood = n;
              neighborhood = retval.neighborhood;
              neighborhoodDefined = true;

            }
              break;

            }

            // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
            // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:168:41:
            // ( statedef )+
            int cnt2 = 0;
            loop2: do {
              int alt2 = 2;
              int LA2_0 = input.LA(1);

              if ((LA2_0 == STATE)) {
                alt2 = 1;
              }

              switch (alt2) {
              case 1:
              // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
              // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:168:41:
              // statedef
              {
                pushFollow(FOLLOW_statedef_in_camodel231);
                statedef();

                state._fsp--;

              }
                break;

              default:
                if (cnt2 >= 1) {
                  break loop2;
                }
                EarlyExitException eee = new EarlyExitException(2, input);
                throw eee;
              }
              cnt2++;
            } while (true);

            // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
            // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:168:51:
            // ( carule )+
            int cnt3 = 0;
            loop3: do {
              int alt3 = 2;
              int LA3_0 = input.LA(1);

              if ((LA3_0 == RULE)) {
                alt3 = 1;
              }

              switch (alt3) {
              case 1:
              // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
              // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:168:51:
              // carule
              {
                pushFollow(FOLLOW_carule_in_camodel234);
                carule();

                state._fsp--;

              }
                break;

              default:
                if (cnt3 >= 1) {
                  break loop3;
                }
                EarlyExitException eee = new EarlyExitException(3, input);
                throw eee;
              }
              cnt3++;
            } while (true);

          }

          if (retval.neighborhood == null && !neighborhoodDefined) {
            // use standard neumann neighborhood
            if (NeumannNeighborhood.isDimensionSupported(retval.dimensions)) {
              retval.neighborhood =
                  new NeumannNeighborhood(retval.dimensions, null);
              neighborhood = retval.neighborhood;
            } else {
              addProblemToken(CAProblemToken.VALUE_OUT_OF_RANGE,
                  (d != null ? ((Token) d.start) : null).getTokenIndex(),
                  "Default Neighborhood (Neumann) does not support the specified dimension.");
            }
          }

        }
          break;

        }

        match(input, EOF, FOLLOW_EOF_in_camodel242);

      }

      retval.stop = input.LT(-1);

    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
    } finally {
    }
    return retval;
  }

  // $ANTLR end "camodel"

  // $ANTLR start "wolframrule"
  // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
  // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:181:7:
  // wolframrule returns [int rule] : WOLFRAMRULE ( COLON )? i= INTEGER
  // SEMICOLON ;
  public final int wolframrule() throws RecognitionException {
    int rule = 0;

    Token i = null;

    rule = 0;

    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:185:9:
      // ( WOLFRAMRULE ( COLON )? i= INTEGER SEMICOLON )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:185:11:
      // WOLFRAMRULE ( COLON )? i= INTEGER SEMICOLON
      {
        match(input, WOLFRAMRULE, FOLLOW_WOLFRAMRULE_in_wolframrule289);
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:185:23:
        // ( COLON )?
        int alt5 = 2;
        int LA5_0 = input.LA(1);

        if ((LA5_0 == COLON)) {
          alt5 = 1;
        }
        switch (alt5) {
        case 1:
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:185:23:
        // COLON
        {
          match(input, COLON, FOLLOW_COLON_in_wolframrule291);

        }
          break;

        }

        i = (Token) match(input, INTEGER, FOLLOW_INTEGER_in_wolframrule296);
        match(input, SEMICOLON, FOLLOW_SEMICOLON_in_wolframrule298);

        rule = Integer.valueOf((i != null ? i.getText() : null));
        if (rule < 0 || rule > 255) {
          rule = 0;
          addProblemToken(CAProblemToken.VALUE_OUT_OF_RANGE,
              (i != null ? i.getTokenIndex() : 0), (i != null ? i.getText()
                  : null) + " not in range of 0 and 255");
        }

      }

    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
    } finally {
    }
    return rule;
  }

  // $ANTLR end "wolframrule"

  public static class dimension_return extends ParserRuleReturnScope {
    public int dimension;
  }

  // $ANTLR start "dimension"
  // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
  // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:194:7:
  // dimension returns [int dimension] : DIMENSIONS ( COLON )? (i= INTEGER )
  // SEMICOLON ;
  public final CaruleParser.dimension_return dimension()
      throws RecognitionException {
    CaruleParser.dimension_return retval = new CaruleParser.dimension_return();
    retval.start = input.LT(1);

    Token i = null;

    retval.dimension = 1;

    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:198:9:
      // ( DIMENSIONS ( COLON )? (i= INTEGER ) SEMICOLON )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:198:11:
      // DIMENSIONS ( COLON )? (i= INTEGER ) SEMICOLON
      {
        match(input, DIMENSIONS, FOLLOW_DIMENSIONS_in_dimension346);
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:198:22:
        // ( COLON )?
        int alt6 = 2;
        int LA6_0 = input.LA(1);

        if ((LA6_0 == COLON)) {
          alt6 = 1;
        }
        switch (alt6) {
        case 1:
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:198:22:
        // COLON
        {
          match(input, COLON, FOLLOW_COLON_in_dimension348);

        }
          break;

        }

        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:198:29:
        // (i= INTEGER )
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:198:30:
        // i= INTEGER
        {
          i = (Token) match(input, INTEGER, FOLLOW_INTEGER_in_dimension354);

        }

        match(input, SEMICOLON, FOLLOW_SEMICOLON_in_dimension357);
        try {
          retval.dimension = Integer.valueOf((i != null ? i.getText() : null));
        } catch (NumberFormatException e) {
        }

      }

      retval.stop = input.LT(-1);

    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
    } finally {
    }
    return retval;
  }

  // $ANTLR end "dimension"

  public static class version_return extends ParserRuleReturnScope {
    public double version;

    public String comment;
  }

  // $ANTLR start "version"
  // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
  // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:201:7:
  // version returns [double version, String comment] : CAVERSION (n= DOUBLE |
  // n= INTEGER ) SEMICOLON ;
  public final CaruleParser.version_return version()
      throws RecognitionException {
    CaruleParser.version_return retval = new CaruleParser.version_return();
    retval.start = input.LT(1);

    Token n = null;
    Token CAVERSION1 = null;

    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:202:9:
      // ( CAVERSION (n= DOUBLE | n= INTEGER ) SEMICOLON )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:202:11:
      // CAVERSION (n= DOUBLE | n= INTEGER ) SEMICOLON
      {
        CAVERSION1 =
            (Token) match(input, CAVERSION, FOLLOW_CAVERSION_in_version395);
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:202:21:
        // (n= DOUBLE | n= INTEGER )
        int alt7 = 2;
        int LA7_0 = input.LA(1);

        if ((LA7_0 == DOUBLE)) {
          alt7 = 1;
        } else if ((LA7_0 == INTEGER)) {
          alt7 = 2;
        } else {
          NoViableAltException nvae = new NoViableAltException("", 7, 0, input);

          throw nvae;
        }
        switch (alt7) {
        case 1:
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:202:22:
        // n= DOUBLE
        {
          n = (Token) match(input, DOUBLE, FOLLOW_DOUBLE_in_version400);

        }
          break;
        case 2:
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:202:33:
        // n= INTEGER
        {
          n = (Token) match(input, INTEGER, FOLLOW_INTEGER_in_version406);

        }
          break;

        }

        match(input, SEMICOLON, FOLLOW_SEMICOLON_in_version409);

        retval.version = Double.valueOf((n != null ? n.getText() : null));
        // check tokens before version for comments
        int i = CAVERSION1.getTokenIndex();
        retval.comment = getCommentForToken(i);
        if (retval.version != 1d) {
          addProblemToken(CAProblemToken.RECOGNITION_ERROR,
              (n != null ? n.getTokenIndex() : 0),
              "Only verison 1.0 of CA grammar supported!");
        }

      }

      retval.stop = input.LT(-1);

    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
    } finally {
    }
    return retval;
  }

  // $ANTLR end "version"

  // $ANTLR start "neighborhood"
  // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
  // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:213:7:
  // neighborhood returns [INeighborhood neighborhood] : NEIGHBORHOOD ( COLON )?
  // ( MOORE | NEUMANN | ( FREE )? f= freeneighborhood[comment] ) SEMICOLON ;
  public final INeighborhood neighborhood() throws RecognitionException {
    INeighborhood neighborhood = null;

    Token NEIGHBORHOOD2 = null;
    Token MOORE3 = null;
    FreeNeighborhood f = null;

    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:214:9:
      // ( NEIGHBORHOOD ( COLON )? ( MOORE | NEUMANN | ( FREE )? f=
      // freeneighborhood[comment] ) SEMICOLON )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:214:11:
      // NEIGHBORHOOD ( COLON )? ( MOORE | NEUMANN | ( FREE )? f=
      // freeneighborhood[comment] ) SEMICOLON
      {
        NEIGHBORHOOD2 =
            (Token) match(input, NEIGHBORHOOD,
                FOLLOW_NEIGHBORHOOD_in_neighborhood446);
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:214:24:
        // ( COLON )?
        int alt8 = 2;
        int LA8_0 = input.LA(1);

        if ((LA8_0 == COLON)) {
          alt8 = 1;
        }
        switch (alt8) {
        case 1:
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:214:24:
        // COLON
        {
          match(input, COLON, FOLLOW_COLON_in_neighborhood448);

        }
          break;

        }

        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:214:31:
        // ( MOORE | NEUMANN | ( FREE )? f= freeneighborhood[comment] )
        int alt10 = 3;
        switch (input.LA(1)) {
        case MOORE: {
          alt10 = 1;
        }
          break;
        case NEUMANN: {
          alt10 = 2;
        }
          break;
        case FREE:
        case LEFT_CURLEY: {
          alt10 = 3;
        }
          break;
        default:
          NoViableAltException nvae =
              new NoViableAltException("", 10, 0, input);

          throw nvae;
        }

        switch (alt10) {
        case 1:
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:215:11:
        // MOORE
        {
          MOORE3 = (Token) match(input, MOORE, FOLLOW_MOORE_in_neighborhood463);

          if (MooreNeighborhood.isDimensionSupported(dimensions)) {
            String comment = getCommentForToken(NEIGHBORHOOD2.getTokenIndex());
            neighborhood = new MooreNeighborhood(dimensions, comment);
          } else {
            addProblemToken(CAProblemToken.VALUE_OUT_OF_RANGE,
                (MOORE3 != null ? MOORE3.getTokenIndex() : 0),
                "Moore neighborhood not supported for specified dimension -> "
                    + dimensions);
          }

        }
          break;
        case 2:
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:223:11:
        // NEUMANN
        {
          match(input, NEUMANN, FOLLOW_NEUMANN_in_neighborhood477);

          if (NeumannNeighborhood.isDimensionSupported(dimensions)) {
            String comment = getCommentForToken(NEIGHBORHOOD2.getTokenIndex());
            neighborhood = new NeumannNeighborhood(dimensions, null);
          } else {
            addProblemToken(CAProblemToken.VALUE_OUT_OF_RANGE,
                (MOORE3 != null ? MOORE3.getTokenIndex() : 0),
                "Neumann neighborhood not supported for specified dimension -> "
                    + dimensions);
          }

        }
          break;
        case 3:
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:231:11:
        // ( FREE )? f= freeneighborhood[comment]
        {
          // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
          // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:231:11:
          // ( FREE )?
          int alt9 = 2;
          int LA9_0 = input.LA(1);

          if ((LA9_0 == FREE)) {
            alt9 = 1;
          }
          switch (alt9) {
          case 1:
          // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
          // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:231:11:
          // FREE
          {
            match(input, FREE, FOLLOW_FREE_in_neighborhood491);

          }
            break;

          }

          String comment = getCommentForToken(NEIGHBORHOOD2.getTokenIndex());

          pushFollow(FOLLOW_freeneighborhood_in_neighborhood508);
          f = freeneighborhood(comment);

          state._fsp--;

          neighborhood = f;

        }
          break;

        }

        match(input, SEMICOLON, FOLLOW_SEMICOLON_in_neighborhood514);

      }

    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
    } finally {
    }
    return neighborhood;
  }

  // $ANTLR end "neighborhood"

  // $ANTLR start "freeneighborhood"
  // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
  // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:237:7:
  // freeneighborhood[String comment] returns [FreeNeighborhood neighborhood] :
  // LEFT_CURLEY ( ( LEFT_BRACE i= INTEGER ( ( COMMA )? j= INTEGER )*
  // RIGHT_BRACE ) )+ RIGHT_CURLEY ;
  public final FreeNeighborhood freeneighborhood(String comment)
      throws RecognitionException {
    FreeNeighborhood neighborhood = null;

    Token i = null;
    Token j = null;
    Token LEFT_BRACE4 = null;

    neighborhood = new FreeNeighborhood(dimensions, comment);
    List<Integer> l = new ArrayList<>();
    int d = 0;

    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:243:9:
      // ( LEFT_CURLEY ( ( LEFT_BRACE i= INTEGER ( ( COMMA )? j= INTEGER )*
      // RIGHT_BRACE ) )+ RIGHT_CURLEY )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:243:11:
      // LEFT_CURLEY ( ( LEFT_BRACE i= INTEGER ( ( COMMA )? j= INTEGER )*
      // RIGHT_BRACE ) )+ RIGHT_CURLEY
      {
        match(input, LEFT_CURLEY, FOLLOW_LEFT_CURLEY_in_freeneighborhood562);
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:243:23:
        // ( ( LEFT_BRACE i= INTEGER ( ( COMMA )? j= INTEGER )* RIGHT_BRACE ) )+
        int cnt13 = 0;
        loop13: do {
          int alt13 = 2;
          int LA13_0 = input.LA(1);

          if ((LA13_0 == LEFT_BRACE)) {
            alt13 = 1;
          }

          switch (alt13) {
          case 1:
          // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
          // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:243:24:
          // ( LEFT_BRACE i= INTEGER ( ( COMMA )? j= INTEGER )* RIGHT_BRACE )
          {
            // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
            // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:243:24:
            // ( LEFT_BRACE i= INTEGER ( ( COMMA )? j= INTEGER )* RIGHT_BRACE )
            // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
            // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:243:25:
            // LEFT_BRACE i= INTEGER ( ( COMMA )? j= INTEGER )* RIGHT_BRACE
            {
              LEFT_BRACE4 =
                  (Token) match(input, LEFT_BRACE,
                      FOLLOW_LEFT_BRACE_in_freeneighborhood566);
              i =
                  (Token) match(input, INTEGER,
                      FOLLOW_INTEGER_in_freeneighborhood607);

              try {
                l.add(Integer.valueOf((i != null ? i.getText() : null)));
                d++;
              } catch (NumberFormatException e) {
              }

              // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
              // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:251:37:
              // ( ( COMMA )? j= INTEGER )*
              loop12: do {
                int alt12 = 2;
                int LA12_0 = input.LA(1);

                if ((LA12_0 == INTEGER || LA12_0 == COMMA)) {
                  alt12 = 1;
                }

                switch (alt12) {
                case 1:
                // C:/Documents and Settings/stefan/Desktop/James II Dev/James
                // II
                // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:251:38:
                // ( COMMA )? j= INTEGER
                {
                  // C:/Documents and Settings/stefan/Desktop/James II Dev/James
                  // II
                  // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:251:38:
                  // ( COMMA )?
                  int alt11 = 2;
                  int LA11_0 = input.LA(1);

                  if ((LA11_0 == COMMA)) {
                    alt11 = 1;
                  }
                  switch (alt11) {
                  case 1:
                  // C:/Documents and Settings/stefan/Desktop/James II Dev/James
                  // II
                  // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:251:38:
                  // COMMA
                  {
                    match(input, COMMA, FOLLOW_COMMA_in_freeneighborhood649);

                  }
                    break;

                  }

                  j =
                      (Token) match(input, INTEGER,
                          FOLLOW_INTEGER_in_freeneighborhood654);

                  if (d >= dimensions) {
                    addProblemToken(CAProblemToken.VALUE_OUT_OF_RANGE,
                        (j != null ? j.getTokenIndex() : 0),
                        "Too many arguments for the specified dimension.");
                  } else {
                    try {
                      l.add(Integer.valueOf((j != null ? j.getText() : null)));
                      d++;
                    } catch (NumberFormatException e) {
                    }
                  }

                }
                  break;

                default:
                  break loop12;
                }
              } while (true);

              match(input, RIGHT_BRACE,
                  FOLLOW_RIGHT_BRACE_in_freeneighborhood685);

            }

            if (l.size() < dimensions) {
              addProblemToken(CAProblemToken.VALUE_OUT_OF_RANGE,
                  (LEFT_BRACE4 != null ? LEFT_BRACE4.getTokenIndex() : 0),
                  "Not enough arguments for specified dimension.");
            } else {
              try {
                Integer[] cell = l.toArray(new Integer[0]);
                int[] c = new int[cell.length];
                for (int k = 0; k < c.length; k++) {
                  c[k] = cell[k].intValue();
                }
                if (neighborhood.containsCell(c)) {
                  addProblemToken(CAProblemToken.VALUE_OUT_OF_RANGE,
                      (LEFT_BRACE4 != null ? LEFT_BRACE4.getTokenIndex() : 0),
                      "Cell already in neighborhood.");
                } else {
                  neighborhood.addCell(c);
                }
              } catch (IllegalArgumentException e) {
              }
            }
            l.clear();
            d = 0;

          }
            break;

          default:
            if (cnt13 >= 1) {
              break loop13;
            }
            EarlyExitException eee = new EarlyExitException(13, input);
            throw eee;
          }
          cnt13++;
        } while (true);

        match(input, RIGHT_CURLEY, FOLLOW_RIGHT_CURLEY_in_freeneighborhood692);

      }

    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
    } finally {
    }
    return neighborhood;
  }

  // $ANTLR end "freeneighborhood"

  // $ANTLR start "statedef"
  // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
  // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:283:7:
  // statedef : STATE ( COLON )? i= ID ( ( COMMA )? j= ID )* SEMICOLON ;
  public final void statedef() throws RecognitionException {
    Token i = null;
    Token j = null;

    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:284:8:
      // ( STATE ( COLON )? i= ID ( ( COMMA )? j= ID )* SEMICOLON )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:284:10:
      // STATE ( COLON )? i= ID ( ( COMMA )? j= ID )* SEMICOLON
      {
        match(input, STATE, FOLLOW_STATE_in_statedef738);
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:284:16:
        // ( COLON )?
        int alt14 = 2;
        int LA14_0 = input.LA(1);

        if ((LA14_0 == COLON)) {
          alt14 = 1;
        }
        switch (alt14) {
        case 1:
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:284:16:
        // COLON
        {
          match(input, COLON, FOLLOW_COLON_in_statedef740);

        }
          break;

        }

        i = (Token) match(input, ID, FOLLOW_ID_in_statedef745);

        if (!addState((i != null ? i.getText() : null))) {
          addProblemToken(CAProblemToken.STATE_ALREADY_DEFINED,
              (i != null ? i.getTokenIndex() : 0),
              "State " + (i != null ? i.getText() : null) + " already defined!");
        }

        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:290:9:
        // ( ( COMMA )? j= ID )*
        loop16: do {
          int alt16 = 2;
          int LA16_0 = input.LA(1);

          if ((LA16_0 == COMMA || LA16_0 == ID)) {
            alt16 = 1;
          }

          switch (alt16) {
          case 1:
          // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
          // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:290:10:
          // ( COMMA )? j= ID
          {
            // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
            // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:290:10:
            // ( COMMA )?
            int alt15 = 2;
            int LA15_0 = input.LA(1);

            if ((LA15_0 == COMMA)) {
              alt15 = 1;
            }
            switch (alt15) {
            case 1:
            // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
            // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:290:10:
            // COMMA
            {
              match(input, COMMA, FOLLOW_COMMA_in_statedef767);

            }
              break;

            }

            j = (Token) match(input, ID, FOLLOW_ID_in_statedef772);

            if (!addState((j != null ? j.getText() : null))) {
              addProblemToken(CAProblemToken.STATE_ALREADY_DEFINED,
                  (j != null ? j.getTokenIndex() : 0), "State "
                      + (j != null ? j.getText() : null) + " already defined!");
            }

          }
            break;

          default:
            break loop16;
          }
        } while (true);

        match(input, SEMICOLON, FOLLOW_SEMICOLON_in_statedef778);

      }

    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
    } finally {
    }
    return;
  }

  // $ANTLR end "statedef"

  // $ANTLR start "carule"
  // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
  // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:297:7:
  // carule : prob= rule COLON (con= orexpression )? ARROW target= state
  // SEMICOLON ;
  public final void carule() throws RecognitionException {
    CaruleParser.rule_return prob = null;

    OrExpression con = null;

    String target = null;

    ICACondition preCondition = new BooleanCondition(true);

    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:301:9:
      // (prob= rule COLON (con= orexpression )? ARROW target= state SEMICOLON )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:301:11:
      // prob= rule COLON (con= orexpression )? ARROW target= state SEMICOLON
      {
        pushFollow(FOLLOW_rule_in_carule822);
        prob = rule();

        state._fsp--;

        match(input, COLON, FOLLOW_COLON_in_carule824);
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:301:27:
        // (con= orexpression )?
        int alt17 = 2;
        int LA17_0 = input.LA(1);

        if ((LA17_0 == ID || (LA17_0 >= NOT && LA17_0 <= LEFT_PAREN) || (LA17_0 >= TRUE && LA17_0 <= FALSE))) {
          alt17 = 1;
        }
        switch (alt17) {
        case 1:
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:301:28:
        // con= orexpression
        {
          pushFollow(FOLLOW_orexpression_in_carule829);
          con = orexpression();

          state._fsp--;

          preCondition = con;

        }
          break;

        }

        match(input, ARROW, FOLLOW_ARROW_in_carule835);
        pushFollow(FOLLOW_state_in_carule839);
        target = state();

        state._fsp--;

        match(input, SEMICOLON, FOLLOW_SEMICOLON_in_carule841);

        String comment =
            getCommentForToken((prob != null ? ((Token) prob.start) : null)
                .getTokenIndex());
        addRule(new CARule((prob != null ? prob.currentCondition : null),
            preCondition, states.indexOf(target),
            (prob != null ? prob.probability : 0.0), comment));

      }

    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
    } finally {
    }
    return;
  }

  // $ANTLR end "carule"

  public static class rule_return extends ParserRuleReturnScope {
    public double probability;

    public ICACondition currentCondition;
  }

  // $ANTLR start "rule"
  // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
  // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:307:7:
  // rule returns [double probability, ICACondition currentCondition] : RULE (c=
  // current )? (pr= probability )? ;
  public final CaruleParser.rule_return rule() throws RecognitionException {
    CaruleParser.rule_return retval = new CaruleParser.rule_return();
    retval.start = input.LT(1);

    CurrentStateCondition c = null;

    double pr = 0.0;

    retval.probability = 1d;
    retval.currentCondition = new BooleanCondition(true);

    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:312:9:
      // ( RULE (c= current )? (pr= probability )? )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:312:11:
      // RULE (c= current )? (pr= probability )?
      {
        match(input, RULE, FOLLOW_RULE_in_rule895);
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:312:16:
        // (c= current )?
        int alt18 = 2;
        int LA18_0 = input.LA(1);

        if ((LA18_0 == LEFT_CURLEY)) {
          alt18 = 1;
        }
        switch (alt18) {
        case 1:
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:312:17:
        // c= current
        {
          pushFollow(FOLLOW_current_in_rule900);
          c = current();

          state._fsp--;

          retval.currentCondition = c;

        }
          break;

        }

        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:312:63:
        // (pr= probability )?
        int alt19 = 2;
        int LA19_0 = input.LA(1);

        if ((LA19_0 == LEFT_BRACE)) {
          alt19 = 1;
        }
        switch (alt19) {
        case 1:
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:312:64:
        // pr= probability
        {
          pushFollow(FOLLOW_probability_in_rule909);
          pr = probability();

          state._fsp--;

          retval.probability = pr;

        }
          break;

        }

      }

      retval.stop = input.LT(-1);

    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
    } finally {
    }
    return retval;
  }

  // $ANTLR end "rule"

  // $ANTLR start "current"
  // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
  // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:315:7:
  // current returns [CurrentStateCondition condition] : LEFT_CURLEY (i= ID ) (
  // ( COMMA )? j= ID )* RIGHT_CURLEY ;
  public final CurrentStateCondition current() throws RecognitionException {
    CurrentStateCondition condition = null;

    Token i = null;
    Token j = null;

    condition = new CurrentStateCondition();

    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:319:9:
      // ( LEFT_CURLEY (i= ID ) ( ( COMMA )? j= ID )* RIGHT_CURLEY )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:319:11:
      // LEFT_CURLEY (i= ID ) ( ( COMMA )? j= ID )* RIGHT_CURLEY
      {
        match(input, LEFT_CURLEY, FOLLOW_LEFT_CURLEY_in_current969);
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:319:23:
        // (i= ID )
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:319:24:
        // i= ID
        {
          i = (Token) match(input, ID, FOLLOW_ID_in_current974);

          int state = states.indexOf((i != null ? i.getText() : null));
          if (state < 0) {
            if ((i != null ? i.getTokenIndex() : 0) >= 0) {
              addProblemToken(CAProblemToken.STATE_NOT_DEFINED,
                  (i != null ? i.getTokenIndex() : 0), "State '"
                      + (i != null ? i.getText() : null) + "' not defined!");
            }
          } else {
            condition.addState(state);
          }

        }

        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:326:30:
        // ( ( COMMA )? j= ID )*
        loop21: do {
          int alt21 = 2;
          int LA21_0 = input.LA(1);

          if ((LA21_0 == COMMA || LA21_0 == ID)) {
            alt21 = 1;
          }

          switch (alt21) {
          case 1:
          // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
          // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:326:31:
          // ( COMMA )? j= ID
          {
            // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
            // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:326:31:
            // ( COMMA )?
            int alt20 = 2;
            int LA20_0 = input.LA(1);

            if ((LA20_0 == COMMA)) {
              alt20 = 1;
            }
            switch (alt20) {
            case 1:
            // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
            // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:326:31:
            // COMMA
            {
              match(input, COMMA, FOLLOW_COMMA_in_current980);

            }
              break;

            }

            j = (Token) match(input, ID, FOLLOW_ID_in_current985);

            int state = states.indexOf((j != null ? j.getText() : null));
            if (state < 0) {
              if ((j != null ? j.getTokenIndex() : 0) >= 0) {
                addProblemToken(CAProblemToken.STATE_NOT_DEFINED,
                    (j != null ? j.getTokenIndex() : 0), "State '"
                        + (j != null ? j.getText() : null) + "' not defined!");
              }
            } else {
              condition.addState(state);
            }

          }
            break;

          default:
            break loop21;
          }
        } while (true);

        match(input, RIGHT_CURLEY, FOLLOW_RIGHT_CURLEY_in_current991);

      }

    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
    } finally {
    }
    return condition;
  }

  // $ANTLR end "current"

  // $ANTLR start "state"
  // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
  // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:336:7:
  // state returns [String state] : ID ;
  public final String state() throws RecognitionException {
    String state = null;

    Token ID5 = null;

    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:337:8:
      // ( ID )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:337:10:
      // ID
      {
        ID5 = (Token) match(input, ID, FOLLOW_ID_in_state1033);

        state = (ID5 != null ? ID5.getText() : null);
        if (!states.contains(state)
            && (ID5 != null ? ID5.getTokenIndex() : 0) >= 0) {
          addProblemToken(CAProblemToken.STATE_NOT_DEFINED,
              (ID5 != null ? ID5.getTokenIndex() : 0), "State '"
                  + (ID5 != null ? ID5.getText() : null) + "' not defined!");
        }

      }

    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
    } finally {
    }
    return state;
  }

  // $ANTLR end "state"

  // $ANTLR start "orexpression"
  // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
  // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:345:7:
  // orexpression returns [OrExpression condition] : (a= andexpression ) ( OR
  // a1= andexpression )* ;
  public final OrExpression orexpression() throws RecognitionException {
    OrExpression condition = null;

    AndExpression a = null;

    AndExpression a1 = null;

    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:346:9:
      // ( (a= andexpression ) ( OR a1= andexpression )* )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:346:11:
      // (a= andexpression ) ( OR a1= andexpression )*
      {
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:346:11:
        // (a= andexpression )
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:346:12:
        // a= andexpression
        {
          pushFollow(FOLLOW_andexpression_in_orexpression1088);
          a = andexpression();

          state._fsp--;

          condition = new OrExpression(a);

        }

        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:346:74:
        // ( OR a1= andexpression )*
        loop22: do {
          int alt22 = 2;
          int LA22_0 = input.LA(1);

          if ((LA22_0 == OR)) {
            alt22 = 1;
          }

          switch (alt22) {
          case 1:
          // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
          // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:346:75:
          // OR a1= andexpression
          {
            match(input, OR, FOLLOW_OR_in_orexpression1094);
            pushFollow(FOLLOW_andexpression_in_orexpression1098);
            a1 = andexpression();

            state._fsp--;

            condition.addCondition(a1);

          }
            break;

          default:
            break loop22;
          }
        } while (true);

      }

    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
    } finally {
    }
    return condition;
  }

  // $ANTLR end "orexpression"

  // $ANTLR start "andexpression"
  // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
  // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:349:7:
  // andexpression returns [AndExpression condition] : (n= notexpression ) ( AND
  // n1= notexpression )* ;
  public final AndExpression andexpression() throws RecognitionException {
    AndExpression condition = null;

    ICACondition n = null;

    ICACondition n1 = null;

    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:350:9:
      // ( (n= notexpression ) ( AND n1= notexpression )* )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:350:11:
      // (n= notexpression ) ( AND n1= notexpression )*
      {
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:350:11:
        // (n= notexpression )
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:350:12:
        // n= notexpression
        {
          pushFollow(FOLLOW_notexpression_in_andexpression1144);
          n = notexpression();

          state._fsp--;

          condition = new AndExpression(n);

        }

        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:350:75:
        // ( AND n1= notexpression )*
        loop23: do {
          int alt23 = 2;
          int LA23_0 = input.LA(1);

          if ((LA23_0 == AND)) {
            alt23 = 1;
          }

          switch (alt23) {
          case 1:
          // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
          // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:350:76:
          // AND n1= notexpression
          {
            match(input, AND, FOLLOW_AND_in_andexpression1150);
            pushFollow(FOLLOW_notexpression_in_andexpression1154);
            n1 = notexpression();

            state._fsp--;

            condition.addCondition(n1);

          }
            break;

          default:
            break loop23;
          }
        } while (true);

      }

    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
    } finally {
    }
    return condition;
  }

  // $ANTLR end "andexpression"

  // $ANTLR start "notexpression"
  // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
  // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:353:7:
  // notexpression returns [ICACondition condition] : ( NOT co= atom | co= atom
  // );
  public final ICACondition notexpression() throws RecognitionException {
    ICACondition condition = null;

    ICACondition co = null;

    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:354:9:
      // ( NOT co= atom | co= atom )
      int alt24 = 2;
      int LA24_0 = input.LA(1);

      if ((LA24_0 == NOT)) {
        alt24 = 1;
      } else if ((LA24_0 == ID || LA24_0 == LEFT_PAREN || (LA24_0 >= TRUE && LA24_0 <= FALSE))) {
        alt24 = 2;
      } else {
        NoViableAltException nvae = new NoViableAltException("", 24, 0, input);

        throw nvae;
      }
      switch (alt24) {
      case 1:
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:354:11:
      // NOT co= atom
      {
        match(input, NOT, FOLLOW_NOT_in_notexpression1197);
        pushFollow(FOLLOW_atom_in_notexpression1201);
        co = atom();

        state._fsp--;

        condition = new NotCondition(co);

      }
        break;
      case 2:
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:355:11:
      // co= atom
      {
        pushFollow(FOLLOW_atom_in_notexpression1218);
        co = atom();

        state._fsp--;

        condition = co;

      }
        break;

      }
    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
    } finally {
    }
    return condition;
  }

  // $ANTLR end "notexpression"

  // $ANTLR start "atom"
  // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
  // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:358:7:
  // atom returns [ICACondition condition] : (con= condition | LEFT_PAREN co=
  // orexpression RIGHT_PAREN );
  public final ICACondition atom() throws RecognitionException {
    ICACondition condition = null;

    ICACondition con = null;

    OrExpression co = null;

    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:359:9:
      // (con= condition | LEFT_PAREN co= orexpression RIGHT_PAREN )
      int alt25 = 2;
      int LA25_0 = input.LA(1);

      if ((LA25_0 == ID || (LA25_0 >= TRUE && LA25_0 <= FALSE))) {
        alt25 = 1;
      } else if ((LA25_0 == LEFT_PAREN)) {
        alt25 = 2;
      } else {
        NoViableAltException nvae = new NoViableAltException("", 25, 0, input);

        throw nvae;
      }
      switch (alt25) {
      case 1:
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:359:11:
      // con= condition
      {
        pushFollow(FOLLOW_condition_in_atom1266);
        con = condition();

        state._fsp--;

        condition = con;

      }
        break;
      case 2:
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:360:11:
      // LEFT_PAREN co= orexpression RIGHT_PAREN
      {
        match(input, LEFT_PAREN, FOLLOW_LEFT_PAREN_in_atom1281);
        pushFollow(FOLLOW_orexpression_in_atom1285);
        co = orexpression();

        state._fsp--;

        match(input, RIGHT_PAREN, FOLLOW_RIGHT_PAREN_in_atom1287);
        condition = co;

      }
        break;

      }
    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
    } finally {
    }
    return condition;
  }

  // $ANTLR end "atom"

  // $ANTLR start "condition"
  // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
  // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:363:7:
  // condition returns [ICACondition condition] : ( TRUE | FALSE | id );
  public final ICACondition condition() throws RecognitionException {
    ICACondition condition = null;

    CaruleParser.id_return id6 = null;

    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:364:9:
      // ( TRUE | FALSE | id )
      int alt26 = 3;
      switch (input.LA(1)) {
      case TRUE: {
        alt26 = 1;
      }
        break;
      case FALSE: {
        alt26 = 2;
      }
        break;
      case ID: {
        alt26 = 3;
      }
        break;
      default:
        NoViableAltException nvae = new NoViableAltException("", 26, 0, input);

        throw nvae;
      }

      switch (alt26) {
      case 1:
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:364:11:
      // TRUE
      {
        match(input, TRUE, FOLLOW_TRUE_in_condition1328);
        condition = new BooleanCondition(true);

      }
        break;
      case 2:
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:365:11:
      // FALSE
      {
        match(input, FALSE, FOLLOW_FALSE_in_condition1342);
        condition = new BooleanCondition(false);

      }
        break;
      case 3:
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:366:11:
      // id
      {
        pushFollow(FOLLOW_id_in_condition1357);
        id6 = id();

        state._fsp--;

        condition =
            new StateCondition(
                states.indexOf((id6 != null ? id6.state : null)),
                (id6 != null ? id6.min : 0), (id6 != null ? id6.max : 0));

      }
        break;

      }
    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
    } finally {
    }
    return condition;
  }

  // $ANTLR end "condition"

  public static class id_return extends ParserRuleReturnScope {
    public String state;

    public int min;

    public int max;
  }

  // $ANTLR start "id"
  // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
  // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:369:7:
  // id returns [String state, int min, int max] : i= ID ( LEFT_CURLEY a=
  // amountcondition RIGHT_CURLEY )? ;
  public final CaruleParser.id_return id() throws RecognitionException {
    CaruleParser.id_return retval = new CaruleParser.id_return();
    retval.start = input.LT(1);

    Token i = null;
    CaruleParser.amountcondition_return a = null;

    retval.min = 1;
    retval.max = 1;

    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:374:8:
      // (i= ID ( LEFT_CURLEY a= amountcondition RIGHT_CURLEY )? )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:374:10:
      // i= ID ( LEFT_CURLEY a= amountcondition RIGHT_CURLEY )?
      {
        i = (Token) match(input, ID, FOLLOW_ID_in_id1412);
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:374:15:
        // ( LEFT_CURLEY a= amountcondition RIGHT_CURLEY )?
        int alt27 = 2;
        int LA27_0 = input.LA(1);

        if ((LA27_0 == LEFT_CURLEY)) {
          alt27 = 1;
        }
        switch (alt27) {
        case 1:
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:374:16:
        // LEFT_CURLEY a= amountcondition RIGHT_CURLEY
        {
          match(input, LEFT_CURLEY, FOLLOW_LEFT_CURLEY_in_id1415);
          pushFollow(FOLLOW_amountcondition_in_id1419);
          a = amountcondition();

          state._fsp--;

          match(input, RIGHT_CURLEY, FOLLOW_RIGHT_CURLEY_in_id1421);
          retval.min = (a != null ? a.min : 0);
          retval.max = (a != null ? a.max : 0);

        }
          break;

        }

        retval.state = (i != null ? i.getText() : null);
        if (!states.contains(retval.state)) {
          addProblemToken(CAProblemToken.STATE_NOT_DEFINED,
              (i != null ? i.getTokenIndex() : 0),
              "State '" + (i != null ? i.getText() : null) + "' not defined!");
        }
        // check whether min and max can ever match according to cell count in
        // neighborhood
        if ((neighborhood != null && neighborhood.getCellCount() < retval.min)) {
          addProblemToken(CAProblemToken.VALUE_OUT_OF_RANGE,
              (i != null ? i.getTokenIndex() : 0),
              "State amount minimum higher than neighborhood size.");
        }
        if (retval.min > retval.max) {
          addProblemToken(CAProblemToken.VALUE_OUT_OF_RANGE,
              (i != null ? i.getTokenIndex() : 0),
              "minimum value greater than maximum value.");
        }

      }

      retval.stop = input.LT(-1);

    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
    } finally {
    }
    return retval;
  }

  // $ANTLR end "id"

  public static class amountcondition_return extends ParserRuleReturnScope {
    public int min;

    public int max;
  }

  // $ANTLR start "amountcondition"
  // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
  // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:391:7:
  // amountcondition returns [int min, int max] : ( (mi= minamount )? COMMA (ma=
  // maxamount )? | e= equalsamount );
  public final CaruleParser.amountcondition_return amountcondition()
      throws RecognitionException {
    CaruleParser.amountcondition_return retval =
        new CaruleParser.amountcondition_return();
    retval.start = input.LT(1);

    CaruleParser.minamount_return mi = null;

    CaruleParser.maxamount_return ma = null;

    CaruleParser.equalsamount_return e = null;

    retval.min = 0;
    retval.max = Integer.MAX_VALUE;

    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:396:8:
      // ( (mi= minamount )? COMMA (ma= maxamount )? | e= equalsamount )
      int alt30 = 2;
      int LA30_0 = input.LA(1);

      if ((LA30_0 == INTEGER)) {
        int LA30_1 = input.LA(2);

        if ((LA30_1 == COMMA)) {
          alt30 = 1;
        } else if ((LA30_1 == RIGHT_CURLEY)) {
          alt30 = 2;
        } else {
          NoViableAltException nvae =
              new NoViableAltException("", 30, 1, input);

          throw nvae;
        }
      } else if ((LA30_0 == COMMA)) {
        alt30 = 1;
      } else {
        NoViableAltException nvae = new NoViableAltException("", 30, 0, input);

        throw nvae;
      }
      switch (alt30) {
      case 1:
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:396:10:
      // (mi= minamount )? COMMA (ma= maxamount )?
      {
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:396:10:
        // (mi= minamount )?
        int alt28 = 2;
        int LA28_0 = input.LA(1);

        if ((LA28_0 == INTEGER)) {
          alt28 = 1;
        }
        switch (alt28) {
        case 1:
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:396:11:
        // mi= minamount
        {
          pushFollow(FOLLOW_minamount_in_amountcondition1480);
          mi = minamount();

          state._fsp--;

          retval.min =
              Integer.valueOf((mi != null ? input.toString(mi.start, mi.stop)
                  : null));

        }
          break;

        }

        match(input, COMMA, FOLLOW_COMMA_in_amountcondition1486);
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:396:66:
        // (ma= maxamount )?
        int alt29 = 2;
        int LA29_0 = input.LA(1);

        if ((LA29_0 == INTEGER)) {
          alt29 = 1;
        }
        switch (alt29) {
        case 1:
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:396:67:
        // ma= maxamount
        {
          pushFollow(FOLLOW_maxamount_in_amountcondition1491);
          ma = maxamount();

          state._fsp--;

          retval.max =
              Integer.valueOf((ma != null ? input.toString(ma.start, ma.stop)
                  : null));

        }
          break;

        }

      }
        break;
      case 2:
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:397:10:
      // e= equalsamount
      {
        pushFollow(FOLLOW_equalsamount_in_amountcondition1509);
        e = equalsamount();

        state._fsp--;

        retval.min =
            Integer
                .valueOf((e != null ? input.toString(e.start, e.stop) : null));
        retval.max =
            Integer
                .valueOf((e != null ? input.toString(e.start, e.stop) : null));

      }
        break;

      }
      retval.stop = input.LT(-1);

    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
    } finally {
    }
    return retval;
  }

  // $ANTLR end "amountcondition"

  public static class minamount_return extends ParserRuleReturnScope {
  }

  // $ANTLR start "minamount"
  // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
  // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:400:7:
  // minamount : INTEGER ;
  public final CaruleParser.minamount_return minamount()
      throws RecognitionException {
    CaruleParser.minamount_return retval = new CaruleParser.minamount_return();
    retval.start = input.LT(1);

    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:401:8:
      // ( INTEGER )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:401:10:
      // INTEGER
      {
        match(input, INTEGER, FOLLOW_INTEGER_in_minamount1547);

      }

      retval.stop = input.LT(-1);

    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
    } finally {
    }
    return retval;
  }

  // $ANTLR end "minamount"

  public static class maxamount_return extends ParserRuleReturnScope {
  }

  // $ANTLR start "maxamount"
  // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
  // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:403:7:
  // maxamount : INTEGER ;
  public final CaruleParser.maxamount_return maxamount()
      throws RecognitionException {
    CaruleParser.maxamount_return retval = new CaruleParser.maxamount_return();
    retval.start = input.LT(1);

    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:404:8:
      // ( INTEGER )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:404:10:
      // INTEGER
      {
        match(input, INTEGER, FOLLOW_INTEGER_in_maxamount1576);

      }

      retval.stop = input.LT(-1);

    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
    } finally {
    }
    return retval;
  }

  // $ANTLR end "maxamount"

  public static class equalsamount_return extends ParserRuleReturnScope {
  }

  // $ANTLR start "equalsamount"
  // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
  // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:406:7:
  // equalsamount : INTEGER ;
  public final CaruleParser.equalsamount_return equalsamount()
      throws RecognitionException {
    CaruleParser.equalsamount_return retval =
        new CaruleParser.equalsamount_return();
    retval.start = input.LT(1);

    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:407:8:
      // ( INTEGER )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:407:10:
      // INTEGER
      {
        match(input, INTEGER, FOLLOW_INTEGER_in_equalsamount1603);

      }

      retval.stop = input.LT(-1);

    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
    } finally {
    }
    return retval;
  }

  // $ANTLR end "equalsamount"

  // $ANTLR start "probability"
  // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
  // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:409:7:
  // probability returns [double probability] : LEFT_BRACE (d= DOUBLE | d=
  // INTEGER ) RIGHT_BRACE ;
  public final double probability() throws RecognitionException {
    double probability = 0.0;

    Token d = null;

    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:410:8:
      // ( LEFT_BRACE (d= DOUBLE | d= INTEGER ) RIGHT_BRACE )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:410:10:
      // LEFT_BRACE (d= DOUBLE | d= INTEGER ) RIGHT_BRACE
      {
        match(input, LEFT_BRACE, FOLLOW_LEFT_BRACE_in_probability1629);
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:410:21:
        // (d= DOUBLE | d= INTEGER )
        int alt31 = 2;
        int LA31_0 = input.LA(1);

        if ((LA31_0 == DOUBLE)) {
          alt31 = 1;
        } else if ((LA31_0 == INTEGER)) {
          alt31 = 2;
        } else {
          NoViableAltException nvae =
              new NoViableAltException("", 31, 0, input);

          throw nvae;
        }
        switch (alt31) {
        case 1:
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:410:22:
        // d= DOUBLE
        {
          d = (Token) match(input, DOUBLE, FOLLOW_DOUBLE_in_probability1634);

        }
          break;
        case 2:
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:410:33:
        // d= INTEGER
        {
          d = (Token) match(input, INTEGER, FOLLOW_INTEGER_in_probability1640);

        }
          break;

        }

        match(input, RIGHT_BRACE, FOLLOW_RIGHT_BRACE_in_probability1643);

        probability = Double.valueOf((d != null ? d.getText() : null));
        if (probability < 0d || probability > 1d) {
          addProblemToken(CAProblemToken.VALUE_OUT_OF_RANGE,
              (d != null ? d.getTokenIndex() : 0), "Probability " + probability
                  + " not in range between 0 and 1!");
        }

      }

    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
    } finally {
    }
    return probability;
  }

  // $ANTLR end "probability"

  // Delegated rules

  public static final BitSet FOLLOW_version_in_camodel79 = new BitSet(
      new long[] { 0x0000000000000110L });

  public static final BitSet FOLLOW_wolframrule_in_camodel88 = new BitSet(
      new long[] { 0x0000000000000000L });

  public static final BitSet FOLLOW_dimension_in_camodel136 = new BitSet(
      new long[] { 0x0000000000100800L });

  public static final BitSet FOLLOW_neighborhood_in_camodel184 = new BitSet(
      new long[] { 0x0000000000100800L });

  public static final BitSet FOLLOW_statedef_in_camodel231 = new BitSet(
      new long[] { 0x0000000000900800L });

  public static final BitSet FOLLOW_carule_in_camodel234 = new BitSet(
      new long[] { 0x0000000000800000L });

  public static final BitSet FOLLOW_EOF_in_camodel242 = new BitSet(
      new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_WOLFRAMRULE_in_wolframrule289 = new BitSet(
      new long[] { 0x0000000000000060L });

  public static final BitSet FOLLOW_COLON_in_wolframrule291 = new BitSet(
      new long[] { 0x0000000000000040L });

  public static final BitSet FOLLOW_INTEGER_in_wolframrule296 = new BitSet(
      new long[] { 0x0000000000000080L });

  public static final BitSet FOLLOW_SEMICOLON_in_wolframrule298 = new BitSet(
      new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_DIMENSIONS_in_dimension346 = new BitSet(
      new long[] { 0x0000000000000060L });

  public static final BitSet FOLLOW_COLON_in_dimension348 = new BitSet(
      new long[] { 0x0000000000000040L });

  public static final BitSet FOLLOW_INTEGER_in_dimension354 = new BitSet(
      new long[] { 0x0000000000000080L });

  public static final BitSet FOLLOW_SEMICOLON_in_dimension357 = new BitSet(
      new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_CAVERSION_in_version395 = new BitSet(
      new long[] { 0x0000000000000440L });

  public static final BitSet FOLLOW_DOUBLE_in_version400 = new BitSet(
      new long[] { 0x0000000000000080L });

  public static final BitSet FOLLOW_INTEGER_in_version406 = new BitSet(
      new long[] { 0x0000000000000080L });

  public static final BitSet FOLLOW_SEMICOLON_in_version409 = new BitSet(
      new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_NEIGHBORHOOD_in_neighborhood446 =
      new BitSet(new long[] { 0x000000000000F020L });

  public static final BitSet FOLLOW_COLON_in_neighborhood448 = new BitSet(
      new long[] { 0x000000000000F020L });

  public static final BitSet FOLLOW_MOORE_in_neighborhood463 = new BitSet(
      new long[] { 0x0000000000000080L });

  public static final BitSet FOLLOW_NEUMANN_in_neighborhood477 = new BitSet(
      new long[] { 0x0000000000000080L });

  public static final BitSet FOLLOW_FREE_in_neighborhood491 = new BitSet(
      new long[] { 0x000000000000F020L });

  public static final BitSet FOLLOW_freeneighborhood_in_neighborhood508 =
      new BitSet(new long[] { 0x0000000000000080L });

  public static final BitSet FOLLOW_SEMICOLON_in_neighborhood514 = new BitSet(
      new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_LEFT_CURLEY_in_freeneighborhood562 =
      new BitSet(new long[] { 0x0000000000010000L });

  public static final BitSet FOLLOW_LEFT_BRACE_in_freeneighborhood566 =
      new BitSet(new long[] { 0x0000000000000040L });

  public static final BitSet FOLLOW_INTEGER_in_freeneighborhood607 =
      new BitSet(new long[] { 0x0000000000060040L });

  public static final BitSet FOLLOW_COMMA_in_freeneighborhood649 = new BitSet(
      new long[] { 0x0000000000000040L });

  public static final BitSet FOLLOW_INTEGER_in_freeneighborhood654 =
      new BitSet(new long[] { 0x0000000000060040L });

  public static final BitSet FOLLOW_RIGHT_BRACE_in_freeneighborhood685 =
      new BitSet(new long[] { 0x0000000000090000L });

  public static final BitSet FOLLOW_RIGHT_CURLEY_in_freeneighborhood692 =
      new BitSet(new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_STATE_in_statedef738 = new BitSet(
      new long[] { 0x0000000000200020L });

  public static final BitSet FOLLOW_COLON_in_statedef740 = new BitSet(
      new long[] { 0x0000000000200000L });

  public static final BitSet FOLLOW_ID_in_statedef745 = new BitSet(
      new long[] { 0x0000000000220080L });

  public static final BitSet FOLLOW_COMMA_in_statedef767 = new BitSet(
      new long[] { 0x0000000000200000L });

  public static final BitSet FOLLOW_ID_in_statedef772 = new BitSet(
      new long[] { 0x0000000000220080L });

  public static final BitSet FOLLOW_SEMICOLON_in_statedef778 = new BitSet(
      new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_rule_in_carule822 = new BitSet(
      new long[] { 0x0000000000000020L });

  public static final BitSet FOLLOW_COLON_in_carule824 = new BitSet(
      new long[] { 0x000000006C600000L });

  public static final BitSet FOLLOW_orexpression_in_carule829 = new BitSet(
      new long[] { 0x0000000000400000L });

  public static final BitSet FOLLOW_ARROW_in_carule835 = new BitSet(
      new long[] { 0x0000000000200000L });

  public static final BitSet FOLLOW_state_in_carule839 = new BitSet(
      new long[] { 0x0000000000000080L });

  public static final BitSet FOLLOW_SEMICOLON_in_carule841 = new BitSet(
      new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_RULE_in_rule895 = new BitSet(
      new long[] { 0x0000000000018002L });

  public static final BitSet FOLLOW_current_in_rule900 = new BitSet(
      new long[] { 0x0000000000010002L });

  public static final BitSet FOLLOW_probability_in_rule909 = new BitSet(
      new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_LEFT_CURLEY_in_current969 = new BitSet(
      new long[] { 0x0000000000200000L });

  public static final BitSet FOLLOW_ID_in_current974 = new BitSet(
      new long[] { 0x00000000002A0000L });

  public static final BitSet FOLLOW_COMMA_in_current980 = new BitSet(
      new long[] { 0x0000000000200000L });

  public static final BitSet FOLLOW_ID_in_current985 = new BitSet(
      new long[] { 0x00000000002A0000L });

  public static final BitSet FOLLOW_RIGHT_CURLEY_in_current991 = new BitSet(
      new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_ID_in_state1033 = new BitSet(
      new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_andexpression_in_orexpression1088 =
      new BitSet(new long[] { 0x0000000001000002L });

  public static final BitSet FOLLOW_OR_in_orexpression1094 = new BitSet(
      new long[] { 0x000000006C200000L });

  public static final BitSet FOLLOW_andexpression_in_orexpression1098 =
      new BitSet(new long[] { 0x0000000001000002L });

  public static final BitSet FOLLOW_notexpression_in_andexpression1144 =
      new BitSet(new long[] { 0x0000000002000002L });

  public static final BitSet FOLLOW_AND_in_andexpression1150 = new BitSet(
      new long[] { 0x000000006C200000L });

  public static final BitSet FOLLOW_notexpression_in_andexpression1154 =
      new BitSet(new long[] { 0x0000000002000002L });

  public static final BitSet FOLLOW_NOT_in_notexpression1197 = new BitSet(
      new long[] { 0x000000006C200000L });

  public static final BitSet FOLLOW_atom_in_notexpression1201 = new BitSet(
      new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_atom_in_notexpression1218 = new BitSet(
      new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_condition_in_atom1266 = new BitSet(
      new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_LEFT_PAREN_in_atom1281 = new BitSet(
      new long[] { 0x000000006C200000L });

  public static final BitSet FOLLOW_orexpression_in_atom1285 = new BitSet(
      new long[] { 0x0000000010000000L });

  public static final BitSet FOLLOW_RIGHT_PAREN_in_atom1287 = new BitSet(
      new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_TRUE_in_condition1328 = new BitSet(
      new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_FALSE_in_condition1342 = new BitSet(
      new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_id_in_condition1357 = new BitSet(
      new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_ID_in_id1412 = new BitSet(
      new long[] { 0x0000000000008002L });

  public static final BitSet FOLLOW_LEFT_CURLEY_in_id1415 = new BitSet(
      new long[] { 0x0000000000020040L });

  public static final BitSet FOLLOW_amountcondition_in_id1419 = new BitSet(
      new long[] { 0x0000000000080000L });

  public static final BitSet FOLLOW_RIGHT_CURLEY_in_id1421 = new BitSet(
      new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_minamount_in_amountcondition1480 =
      new BitSet(new long[] { 0x0000000000020000L });

  public static final BitSet FOLLOW_COMMA_in_amountcondition1486 = new BitSet(
      new long[] { 0x0000000000000042L });

  public static final BitSet FOLLOW_maxamount_in_amountcondition1491 =
      new BitSet(new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_equalsamount_in_amountcondition1509 =
      new BitSet(new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_INTEGER_in_minamount1547 = new BitSet(
      new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_INTEGER_in_maxamount1576 = new BitSet(
      new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_INTEGER_in_equalsamount1603 = new BitSet(
      new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_LEFT_BRACE_in_probability1629 = new BitSet(
      new long[] { 0x0000000000000440L });

  public static final BitSet FOLLOW_DOUBLE_in_probability1634 = new BitSet(
      new long[] { 0x0000000000040000L });

  public static final BitSet FOLLOW_INTEGER_in_probability1640 = new BitSet(
      new long[] { 0x0000000000040000L });

  public static final BitSet FOLLOW_RIGHT_BRACE_in_probability1643 =
      new BitSet(new long[] { 0x0000000000000002L });

}