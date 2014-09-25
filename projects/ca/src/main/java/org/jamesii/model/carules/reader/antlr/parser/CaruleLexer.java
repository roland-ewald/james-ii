// $ANTLR 3.2 Sep 23, 2009 14:05:07 C:/Documents and Settings/stefan/Desktop/James II Dev/James II Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g 2009-10-30 15:44:48

/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.reader.antlr.parser;

import org.antlr.runtime.*;
import java.util.List;
import java.util.ArrayList;

public class CaruleLexer extends Lexer {
  public static final int WOLFRAMRULE = 4;

  public static final int RIGHT_CURLEY = 19;

  public static final int NOT = 26;

  public static final int LINECOMMENT = 41;

  public static final int AND = 25;

  public static final int ID = 21;

  public static final int EOF = -1;

  public static final int UNEXPECTED = 44;

  public static final int DIMENSIONS = 8;

  public static final int FREE = 14;

  public static final int RIGHT_PAREN = 28;

  public static final int NEUMANN = 13;

  public static final int COMMA = 17;

  public static final int DOUBLE = 10;

  public static final int CAVERSION = 9;

  public static final int RIGHT_BRACE = 18;

  public static final int D = 46;

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

  public static final int SEMICOLON = 7;

  public static final int Q = 55;

  public static final int P = 54;

  public static final int S = 36;

  public static final int R = 35;

  public static final int TRUE = 29;

  public static final int Y = 60;

  public static final int X = 59;

  public static final int Z = 61;

  public static final int COLON = 5;

  public static final int MOORE = 12;

  public static final int LEFT_BRACE = 16;

  public static final int OR = 24;

  public static final int LEFT_PAREN = 27;

  public static final int ARROW = 22;

  public static final int GROUPCOMMENT = 43;

  public static final int FALSE = 30;

  public static final int LEFT_CURLEY = 15;

  private final List<CAProblemToken> problemTokens =
      new ArrayList<>();

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

  public Iterable<CAProblemToken> getProblemTokens() {
    return problemTokens;
  }

  @Override
  public Token nextToken() {
    return super.nextToken();
  }

  // delegates
  // delegators

  public CaruleLexer() {
  }

  public CaruleLexer(CharStream input) {
    this(input, new RecognizerSharedState());
  }

  public CaruleLexer(CharStream input, RecognizerSharedState state) {
    super(input, state);

  }

  @Override
  public String getGrammarFileName() {
    return "C:/Documents and Settings/stefan/Desktop/James II Dev/James II Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g";
  }

  // $ANTLR start "STATE"
  public final void mSTATE() throws RecognitionException {
    try {
      int _type = STATE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:419:7:
      // ( 'state' )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:419:9:
      // 'state'
      {
        match("state");

      }

      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "STATE"

  // $ANTLR start "RULE"
  public final void mRULE() throws RecognitionException {
    try {
      int _type = RULE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:420:6:
      // ( 'rule' )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:420:8:
      // 'rule'
      {
        match("rule");

      }

      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "RULE"

  // $ANTLR start "WOLFRAMRULE"
  public final void mWOLFRAMRULE() throws RecognitionException {
    try {
      int _type = WOLFRAMRULE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:422:7:
      // ( 'wolframrule' )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:422:9:
      // 'wolframrule'
      {
        match("wolframrule");

      }

      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "WOLFRAMRULE"

  // $ANTLR start "DIMENSIONS"
  public final void mDIMENSIONS() throws RecognitionException {
    try {
      int _type = DIMENSIONS;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:424:7:
      // ( 'dimensions' )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:424:9:
      // 'dimensions'
      {
        match("dimensions");

      }

      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "DIMENSIONS"

  // $ANTLR start "NEIGHBORHOOD"
  public final void mNEIGHBORHOOD() throws RecognitionException {
    try {
      int _type = NEIGHBORHOOD;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:426:7:
      // ( 'neighborhood' | 'neighbourhood' )
      int alt1 = 2;
      alt1 = dfa1.predict(input);
      switch (alt1) {
      case 1:
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:426:9:
      // 'neighborhood'
      {
        match("neighborhood");

      }
        break;
      case 2:
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:426:24:
      // 'neighbourhood'
      {
        match("neighbourhood");

      }
        break;

      }
      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "NEIGHBORHOOD"

  // $ANTLR start "MOORE"
  public final void mMOORE() throws RecognitionException {
    try {
      int _type = MOORE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:427:7:
      // ( 'moore' )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:427:9:
      // 'moore'
      {
        match("moore");

      }

      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "MOORE"

  // $ANTLR start "NEUMANN"
  public final void mNEUMANN() throws RecognitionException {
    try {
      int _type = NEUMANN;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:429:7:
      // ( 'neumann' )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:429:9:
      // 'neumann'
      {
        match("neumann");

      }

      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "NEUMANN"

  // $ANTLR start "FREE"
  public final void mFREE() throws RecognitionException {
    try {
      int _type = FREE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:430:7:
      // ( 'free' )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:430:9:
      // 'free'
      {
        match("free");

      }

      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "FREE"

  // $ANTLR start "ARROW"
  public final void mARROW() throws RecognitionException {
    try {
      int _type = ARROW;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:431:7:
      // ( '->' )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:431:9:
      // '->'
      {
        match("->");

      }

      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "ARROW"

  // $ANTLR start "LEFT_PAREN"
  public final void mLEFT_PAREN() throws RecognitionException {
    try {
      int _type = LEFT_PAREN;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:434:6:
      // ( '(' )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:434:8:
      // '('
      {
        match('(');

      }

      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "LEFT_PAREN"

  // $ANTLR start "RIGHT_PAREN"
  public final void mRIGHT_PAREN() throws RecognitionException {
    try {
      int _type = RIGHT_PAREN;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:436:6:
      // ( ')' )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:436:8:
      // ')'
      {
        match(')');

      }

      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "RIGHT_PAREN"

  // $ANTLR start "LEFT_CURLEY"
  public final void mLEFT_CURLEY() throws RecognitionException {
    try {
      int _type = LEFT_CURLEY;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:438:6:
      // ( '{' )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:438:8:
      // '{'
      {
        match('{');

      }

      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "LEFT_CURLEY"

  // $ANTLR start "RIGHT_CURLEY"
  public final void mRIGHT_CURLEY() throws RecognitionException {
    try {
      int _type = RIGHT_CURLEY;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:440:6:
      // ( '}' )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:440:8:
      // '}'
      {
        match('}');

      }

      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "RIGHT_CURLEY"

  // $ANTLR start "LEFT_BRACE"
  public final void mLEFT_BRACE() throws RecognitionException {
    try {
      int _type = LEFT_BRACE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:442:6:
      // ( '[' )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:442:8:
      // '['
      {
        match('[');

      }

      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "LEFT_BRACE"

  // $ANTLR start "RIGHT_BRACE"
  public final void mRIGHT_BRACE() throws RecognitionException {
    try {
      int _type = RIGHT_BRACE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:444:6:
      // ( ']' )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:444:8:
      // ']'
      {
        match(']');

      }

      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "RIGHT_BRACE"

  // $ANTLR start "COMMA"
  public final void mCOMMA() throws RecognitionException {
    try {
      int _type = COMMA;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:445:7:
      // ( ',' )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:445:9:
      // ','
      {
        match(',');

      }

      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "COMMA"

  // $ANTLR start "COLON"
  public final void mCOLON() throws RecognitionException {
    try {
      int _type = COLON;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:447:7:
      // ( ':' )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:447:9:
      // ':'
      {
        match(':');

      }

      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "COLON"

  // $ANTLR start "SEMICOLON"
  public final void mSEMICOLON() throws RecognitionException {
    try {
      int _type = SEMICOLON;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:449:6:
      // ( ';' )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:449:8:
      // ';'
      {
        match(';');

      }

      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "SEMICOLON"

  // $ANTLR start "OR"
  public final void mOR() throws RecognitionException {
    try {
      int _type = OR;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:450:6:
      // ( '|' | '||' | 'or' | '+' | 'v' )
      int alt2 = 5;
      switch (input.LA(1)) {
      case '|': {
        int LA2_1 = input.LA(2);

        if ((LA2_1 == '|')) {
          alt2 = 2;
        } else {
          alt2 = 1;
        }
      }
        break;
      case 'o': {
        alt2 = 3;
      }
        break;
      case '+': {
        alt2 = 4;
      }
        break;
      case 'v': {
        alt2 = 5;
      }
        break;
      default:
        NoViableAltException nvae = new NoViableAltException("", 2, 0, input);

        throw nvae;
      }

      switch (alt2) {
      case 1:
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:450:8:
      // '|'
      {
        match('|');

      }
        break;
      case 2:
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:450:12:
      // '||'
      {
        match("||");

      }
        break;
      case 3:
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:450:17:
      // 'or'
      {
        match("or");

      }
        break;
      case 4:
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:450:22:
      // '+'
      {
        match('+');

      }
        break;
      case 5:
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:450:26:
      // 'v'
      {
        match('v');

      }
        break;

      }
      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "OR"

  // $ANTLR start "AND"
  public final void mAND() throws RecognitionException {
    try {
      int _type = AND;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:451:7:
      // ( '&' | '&&' | 'and' | '^' )
      int alt3 = 4;
      switch (input.LA(1)) {
      case '&': {
        int LA3_1 = input.LA(2);

        if ((LA3_1 == '&')) {
          alt3 = 2;
        } else {
          alt3 = 1;
        }
      }
        break;
      case 'a': {
        alt3 = 3;
      }
        break;
      case '^': {
        alt3 = 4;
      }
        break;
      default:
        NoViableAltException nvae = new NoViableAltException("", 3, 0, input);

        throw nvae;
      }

      switch (alt3) {
      case 1:
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:451:9:
      // '&'
      {
        match('&');

      }
        break;
      case 2:
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:451:13:
      // '&&'
      {
        match("&&");

      }
        break;
      case 3:
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:451:18:
      // 'and'
      {
        match("and");

      }
        break;
      case 4:
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:451:24:
      // '^'
      {
        match('^');

      }
        break;

      }
      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "AND"

  // $ANTLR start "NOT"
  public final void mNOT() throws RecognitionException {
    try {
      int _type = NOT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:452:7:
      // ( '!' | 'not' | '~' )
      int alt4 = 3;
      switch (input.LA(1)) {
      case '!': {
        alt4 = 1;
      }
        break;
      case 'n': {
        alt4 = 2;
      }
        break;
      case '~': {
        alt4 = 3;
      }
        break;
      default:
        NoViableAltException nvae = new NoViableAltException("", 4, 0, input);

        throw nvae;
      }

      switch (alt4) {
      case 1:
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:452:9:
      // '!'
      {
        match('!');

      }
        break;
      case 2:
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:452:13:
      // 'not'
      {
        match("not");

      }
        break;
      case 3:
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:452:19:
      // '~'
      {
        match('~');

      }
        break;

      }
      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "NOT"

  // $ANTLR start "TRUE"
  public final void mTRUE() throws RecognitionException {
    try {
      int _type = TRUE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:453:6:
      // ( 'true' )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:453:8:
      // 'true'
      {
        match("true");

      }

      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "TRUE"

  // $ANTLR start "FALSE"
  public final void mFALSE() throws RecognitionException {
    try {
      int _type = FALSE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:454:7:
      // ( 'false' )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:454:9:
      // 'false'
      {
        match("false");

      }

      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "FALSE"

  // $ANTLR start "INTEGER"
  public final void mINTEGER() throws RecognitionException {
    try {
      int _type = INTEGER;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:457:7:
      // ( ( '-' )? ( '0' .. '9' )+ )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:457:9:
      // ( '-' )? ( '0' .. '9' )+
      {
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:457:9:
        // ( '-' )?
        int alt5 = 2;
        int LA5_0 = input.LA(1);

        if ((LA5_0 == '-')) {
          alt5 = 1;
        }
        switch (alt5) {
        case 1:
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:457:9:
        // '-'
        {
          match('-');

        }
          break;

        }

        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:457:13:
        // ( '0' .. '9' )+
        int cnt6 = 0;
        loop6: do {
          int alt6 = 2;
          int LA6_0 = input.LA(1);

          if (((LA6_0 >= '0' && LA6_0 <= '9'))) {
            alt6 = 1;
          }

          switch (alt6) {
          case 1:
          // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
          // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:457:13:
          // '0' .. '9'
          {
            matchRange('0', '9');

          }
            break;

          default:
            if (cnt6 >= 1) {
              break loop6;
            }
            EarlyExitException eee = new EarlyExitException(6, input);
            throw eee;
          }
          cnt6++;
        } while (true);

      }

      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "INTEGER"

  // $ANTLR start "DOUBLE"
  public final void mDOUBLE() throws RecognitionException {
    try {
      int _type = DOUBLE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:460:6:
      // ( ( '-' )? ( '0' .. '9' )* ( '.' ( '0' .. '9' )* )? )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:460:8:
      // ( '-' )? ( '0' .. '9' )* ( '.' ( '0' .. '9' )* )?
      {
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:460:8:
        // ( '-' )?
        int alt7 = 2;
        int LA7_0 = input.LA(1);

        if ((LA7_0 == '-')) {
          alt7 = 1;
        }
        switch (alt7) {
        case 1:
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:460:8:
        // '-'
        {
          match('-');

        }
          break;

        }

        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:460:12:
        // ( '0' .. '9' )*
        loop8: do {
          int alt8 = 2;
          int LA8_0 = input.LA(1);

          if (((LA8_0 >= '0' && LA8_0 <= '9'))) {
            alt8 = 1;
          }

          switch (alt8) {
          case 1:
          // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
          // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:460:12:
          // '0' .. '9'
          {
            matchRange('0', '9');

          }
            break;

          default:
            break loop8;
          }
        } while (true);

        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:460:22:
        // ( '.' ( '0' .. '9' )* )?
        int alt10 = 2;
        int LA10_0 = input.LA(1);

        if ((LA10_0 == '.')) {
          alt10 = 1;
        }
        switch (alt10) {
        case 1:
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:460:23:
        // '.' ( '0' .. '9' )*
        {
          match('.');
          // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
          // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:460:27:
          // ( '0' .. '9' )*
          loop9: do {
            int alt9 = 2;
            int LA9_0 = input.LA(1);

            if (((LA9_0 >= '0' && LA9_0 <= '9'))) {
              alt9 = 1;
            }

            switch (alt9) {
            case 1:
            // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
            // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:460:27:
            // '0' .. '9'
            {
              matchRange('0', '9');

            }
              break;

            default:
              break loop9;
            }
          } while (true);

        }
          break;

        }

      }

      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "DOUBLE"

  // $ANTLR start "CAVERSION"
  public final void mCAVERSION() throws RecognitionException {
    try {
      int _type = CAVERSION;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:464:7:
      // ( '@' C A V E R S I O N )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:464:9:
      // '@' C A V E R S I O N
      {
        match('@');
        mC();
        mA();
        mV();
        mE();
        mR();
        mS();
        mI();
        mO();
        mN();

      }

      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "CAVERSION"

  // $ANTLR start "ID"
  public final void mID() throws RecognitionException {
    try {
      int _type = ID;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:468:6:
      // ( ( 'A' .. 'Z' )+ ( 'A' .. 'Z' | '0' .. '9' | '_' )* )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:468:8:
      // ( 'A' .. 'Z' )+ ( 'A' .. 'Z' | '0' .. '9' | '_' )*
      {
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:468:8:
        // ( 'A' .. 'Z' )+
        int cnt11 = 0;
        loop11: do {
          int alt11 = 2;
          int LA11_0 = input.LA(1);

          if (((LA11_0 >= 'A' && LA11_0 <= 'Z'))) {
            alt11 = 1;
          }

          switch (alt11) {
          case 1:
          // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
          // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:468:9:
          // 'A' .. 'Z'
          {
            matchRange('A', 'Z');

          }
            break;

          default:
            if (cnt11 >= 1) {
              break loop11;
            }
            EarlyExitException eee = new EarlyExitException(11, input);
            throw eee;
          }
          cnt11++;
        } while (true);

        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:468:19:
        // ( 'A' .. 'Z' | '0' .. '9' | '_' )*
        loop12: do {
          int alt12 = 2;
          int LA12_0 = input.LA(1);

          if (((LA12_0 >= '0' && LA12_0 <= '9')
              || (LA12_0 >= 'A' && LA12_0 <= 'Z') || LA12_0 == '_')) {
            alt12 = 1;
          }

          switch (alt12) {
          case 1:
          // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
          // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:
          {
            if ((input.LA(1) >= '0' && input.LA(1) <= '9')
                || (input.LA(1) >= 'A' && input.LA(1) <= 'Z')
                || input.LA(1) == '_') {
              input.consume();

            } else {
              MismatchedSetException mse =
                  new MismatchedSetException(null, input);
              recover(mse);
              throw mse;
            }

          }
            break;

          default:
            break loop12;
          }
        } while (true);

      }

      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "ID"

  // $ANTLR start "WHITESPACE"
  public final void mWHITESPACE() throws RecognitionException {
    try {
      int _type = WHITESPACE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:471:6:
      // ( ( ' ' | '\\t' | '\\r' | '\\n' | '\\f' )+ )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:471:8:
      // ( ' ' | '\\t' | '\\r' | '\\n' | '\\f' )+
      {
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:471:8:
        // ( ' ' | '\\t' | '\\r' | '\\n' | '\\f' )+
        int cnt13 = 0;
        loop13: do {
          int alt13 = 2;
          int LA13_0 = input.LA(1);

          if (((LA13_0 >= '\t' && LA13_0 <= '\n')
              || (LA13_0 >= '\f' && LA13_0 <= '\r') || LA13_0 == ' ')) {
            alt13 = 1;
          }

          switch (alt13) {
          case 1:
          // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
          // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:
          {
            if ((input.LA(1) >= '\t' && input.LA(1) <= '\n')
                || (input.LA(1) >= '\f' && input.LA(1) <= '\r')
                || input.LA(1) == ' ') {
              input.consume();

            } else {
              MismatchedSetException mse =
                  new MismatchedSetException(null, input);
              recover(mse);
              throw mse;
            }

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

        _channel = HIDDEN;

      }

      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "WHITESPACE"

  // $ANTLR start "LINECOMMENT"
  public final void mLINECOMMENT() throws RecognitionException {
    try {
      int _type = LINECOMMENT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:475:7:
      // ( ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r\\n' | '\\n' | '\\r' )? ) )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:475:9:
      // ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r\\n' | '\\n' | '\\r' )? )
      {
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:475:9:
        // ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r\\n' | '\\n' | '\\r' )? )
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:475:10:
        // '//' (~ ( '\\n' | '\\r' ) )* ( '\\r\\n' | '\\n' | '\\r' )?
        {
          match("//");

          // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
          // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:475:15:
          // (~ ( '\\n' | '\\r' ) )*
          loop14: do {
            int alt14 = 2;
            int LA14_0 = input.LA(1);

            if (((LA14_0 >= '\u0000' && LA14_0 <= '\t')
                || (LA14_0 >= '\u000B' && LA14_0 <= '\f') || (LA14_0 >= '\u000E' && LA14_0 <= '\uFFFF'))) {
              alt14 = 1;
            }

            switch (alt14) {
            case 1:
            // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
            // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:475:16:
            // ~ ( '\\n' | '\\r' )
            {
              if ((input.LA(1) >= '\u0000' && input.LA(1) <= '\t')
                  || (input.LA(1) >= '\u000B' && input.LA(1) <= '\f')
                  || (input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF')) {
                input.consume();

              } else {
                MismatchedSetException mse =
                    new MismatchedSetException(null, input);
                recover(mse);
                throw mse;
              }

            }
              break;

            default:
              break loop14;
            }
          } while (true);

          // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
          // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:475:31:
          // ( '\\r\\n' | '\\n' | '\\r' )?
          int alt15 = 4;
          int LA15_0 = input.LA(1);

          if ((LA15_0 == '\r')) {
            int LA15_1 = input.LA(2);

            if ((LA15_1 == '\n')) {
              alt15 = 1;
            }
          } else if ((LA15_0 == '\n')) {
            alt15 = 2;
          }
          switch (alt15) {
          case 1:
          // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
          // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:475:32:
          // '\\r\\n'
          {
            match("\r\n");

          }
            break;
          case 2:
          // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
          // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:475:39:
          // '\\n'
          {
            match('\n');

          }
            break;
          case 3:
          // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
          // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:475:44:
          // '\\r'
          {
            match('\r');

          }
            break;

          }

        }

        _channel = HIDDEN;

      }

      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "LINECOMMENT"

  // $ANTLR start "LANGUAGECOMMENT"
  public final void mLANGUAGECOMMENT() throws RecognitionException {
    try {
      int _type = LANGUAGECOMMENT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:479:7:
      // ( '/**' ( options {greedy=false; } : . )* '*/' )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:479:9:
      // '/**' ( options {greedy=false; } : . )* '*/'
      {
        match("/**");

        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:479:15:
        // ( options {greedy=false; } : . )*
        loop16: do {
          int alt16 = 2;
          int LA16_0 = input.LA(1);

          if ((LA16_0 == '*')) {
            int LA16_1 = input.LA(2);

            if ((LA16_1 == '/')) {
              alt16 = 2;
            } else if (((LA16_1 >= '\u0000' && LA16_1 <= '.') || (LA16_1 >= '0' && LA16_1 <= '\uFFFF'))) {
              alt16 = 1;
            }

          } else if (((LA16_0 >= '\u0000' && LA16_0 <= ')') || (LA16_0 >= '+' && LA16_0 <= '\uFFFF'))) {
            alt16 = 1;
          }

          switch (alt16) {
          case 1:
          // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
          // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:479:43:
          // .
          {
            matchAny();

          }
            break;

          default:
            break loop16;
          }
        } while (true);

        match("*/");

        _channel = HIDDEN;

      }

      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "LANGUAGECOMMENT"

  // $ANTLR start "GROUPCOMMENT"
  public final void mGROUPCOMMENT() throws RecognitionException {
    try {
      int _type = GROUPCOMMENT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:483:6:
      // ( '/*' ~ '*' ( options {greedy=false; } : . )* '*/' )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:483:8:
      // '/*' ~ '*' ( options {greedy=false; } : . )* '*/'
      {
        match("/*");

        if ((input.LA(1) >= '\u0000' && input.LA(1) <= ')')
            || (input.LA(1) >= '+' && input.LA(1) <= '\uFFFF')) {
          input.consume();

        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }

        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:483:17:
        // ( options {greedy=false; } : . )*
        loop17: do {
          int alt17 = 2;
          int LA17_0 = input.LA(1);

          if ((LA17_0 == '*')) {
            int LA17_1 = input.LA(2);

            if ((LA17_1 == '/')) {
              alt17 = 2;
            } else if (((LA17_1 >= '\u0000' && LA17_1 <= '.') || (LA17_1 >= '0' && LA17_1 <= '\uFFFF'))) {
              alt17 = 1;
            }

          } else if (((LA17_0 >= '\u0000' && LA17_0 <= ')') || (LA17_0 >= '+' && LA17_0 <= '\uFFFF'))) {
            alt17 = 1;
          }

          switch (alt17) {
          case 1:
          // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
          // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:483:45:
          // .
          {
            matchAny();

          }
            break;

          default:
            break loop17;
          }
        } while (true);

        match("*/");

        _channel = HIDDEN;

      }

      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "GROUPCOMMENT"

  // $ANTLR start "UNEXPECTED"
  public final void mUNEXPECTED() throws RecognitionException {
    try {
      int _type = UNEXPECTED;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:487:7:
      // ( ( 'a' .. 'z' )+ | . )
      int alt19 = 2;
      int LA19_0 = input.LA(1);

      if (((LA19_0 >= 'a' && LA19_0 <= 'z'))) {
        alt19 = 1;
      } else if (((LA19_0 >= '\u0000' && LA19_0 <= '`') || (LA19_0 >= '{' && LA19_0 <= '\uFFFF'))) {
        alt19 = 2;
      } else {
        NoViableAltException nvae = new NoViableAltException("", 19, 0, input);

        throw nvae;
      }
      switch (alt19) {
      case 1:
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:487:9:
      // ( 'a' .. 'z' )+
      {
        // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
        // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:487:9:
        // ( 'a' .. 'z' )+
        int cnt18 = 0;
        loop18: do {
          int alt18 = 2;
          int LA18_0 = input.LA(1);

          if (((LA18_0 >= 'a' && LA18_0 <= 'z'))) {
            alt18 = 1;
          }

          switch (alt18) {
          case 1:
          // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
          // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:487:9:
          // 'a' .. 'z'
          {
            matchRange('a', 'z');

          }
            break;

          default:
            if (cnt18 >= 1) {
              break loop18;
            }
            EarlyExitException eee = new EarlyExitException(18, input);
            throw eee;
          }
          cnt18++;
        } while (true);

      }
        break;
      case 2:
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:487:21:
      // .
      {
        matchAny();

      }
        break;

      }
      state.type = _type;
      state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "UNEXPECTED"

  // $ANTLR start "A"
  public final void mA() throws RecognitionException {
    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:490:11:
      // ( ( 'a' | 'A' ) )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:490:12:
      // ( 'a' | 'A' )
      {
        if (input.LA(1) == 'A' || input.LA(1) == 'a') {
          input.consume();

        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }

      }

    } finally {
    }
  }

  // $ANTLR end "A"

  // $ANTLR start "B"
  public final void mB() throws RecognitionException {
    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:491:11:
      // ( ( 'b' | 'B' ) )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:491:12:
      // ( 'b' | 'B' )
      {
        if (input.LA(1) == 'B' || input.LA(1) == 'b') {
          input.consume();

        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }

      }

    } finally {
    }
  }

  // $ANTLR end "B"

  // $ANTLR start "C"
  public final void mC() throws RecognitionException {
    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:492:11:
      // ( ( 'c' | 'C' ) )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:492:12:
      // ( 'c' | 'C' )
      {
        if (input.LA(1) == 'C' || input.LA(1) == 'c') {
          input.consume();

        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }

      }

    } finally {
    }
  }

  // $ANTLR end "C"

  // $ANTLR start "D"
  public final void mD() throws RecognitionException {
    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:493:11:
      // ( ( 'd' | 'D' ) )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:493:12:
      // ( 'd' | 'D' )
      {
        if (input.LA(1) == 'D' || input.LA(1) == 'd') {
          input.consume();

        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }

      }

    } finally {
    }
  }

  // $ANTLR end "D"

  // $ANTLR start "E"
  public final void mE() throws RecognitionException {
    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:494:11:
      // ( ( 'e' | 'E' ) )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:494:12:
      // ( 'e' | 'E' )
      {
        if (input.LA(1) == 'E' || input.LA(1) == 'e') {
          input.consume();

        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }

      }

    } finally {
    }
  }

  // $ANTLR end "E"

  // $ANTLR start "F"
  public final void mF() throws RecognitionException {
    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:495:11:
      // ( ( 'f' | 'F' ) )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:495:12:
      // ( 'f' | 'F' )
      {
        if (input.LA(1) == 'F' || input.LA(1) == 'f') {
          input.consume();

        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }

      }

    } finally {
    }
  }

  // $ANTLR end "F"

  // $ANTLR start "G"
  public final void mG() throws RecognitionException {
    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:496:11:
      // ( ( 'g' | 'G' ) )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:496:12:
      // ( 'g' | 'G' )
      {
        if (input.LA(1) == 'G' || input.LA(1) == 'g') {
          input.consume();

        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }

      }

    } finally {
    }
  }

  // $ANTLR end "G"

  // $ANTLR start "H"
  public final void mH() throws RecognitionException {
    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:497:11:
      // ( ( 'h' | 'H' ) )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:497:12:
      // ( 'h' | 'H' )
      {
        if (input.LA(1) == 'H' || input.LA(1) == 'h') {
          input.consume();

        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }

      }

    } finally {
    }
  }

  // $ANTLR end "H"

  // $ANTLR start "I"
  public final void mI() throws RecognitionException {
    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:498:11:
      // ( ( 'i' | 'I' ) )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:498:12:
      // ( 'i' | 'I' )
      {
        if (input.LA(1) == 'I' || input.LA(1) == 'i') {
          input.consume();

        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }

      }

    } finally {
    }
  }

  // $ANTLR end "I"

  // $ANTLR start "J"
  public final void mJ() throws RecognitionException {
    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:499:11:
      // ( ( 'j' | 'J' ) )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:499:12:
      // ( 'j' | 'J' )
      {
        if (input.LA(1) == 'J' || input.LA(1) == 'j') {
          input.consume();

        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }

      }

    } finally {
    }
  }

  // $ANTLR end "J"

  // $ANTLR start "K"
  public final void mK() throws RecognitionException {
    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:500:11:
      // ( ( 'k' | 'K' ) )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:500:12:
      // ( 'k' | 'K' )
      {
        if (input.LA(1) == 'K' || input.LA(1) == 'k') {
          input.consume();

        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }

      }

    } finally {
    }
  }

  // $ANTLR end "K"

  // $ANTLR start "L"
  public final void mL() throws RecognitionException {
    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:501:11:
      // ( ( 'l' | 'L' ) )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:501:12:
      // ( 'l' | 'L' )
      {
        if (input.LA(1) == 'L' || input.LA(1) == 'l') {
          input.consume();

        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }

      }

    } finally {
    }
  }

  // $ANTLR end "L"

  // $ANTLR start "M"
  public final void mM() throws RecognitionException {
    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:502:11:
      // ( ( 'm' | 'M' ) )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:502:12:
      // ( 'm' | 'M' )
      {
        if (input.LA(1) == 'M' || input.LA(1) == 'm') {
          input.consume();

        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }

      }

    } finally {
    }
  }

  // $ANTLR end "M"

  // $ANTLR start "N"
  public final void mN() throws RecognitionException {
    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:503:11:
      // ( ( 'n' | 'N' ) )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:503:12:
      // ( 'n' | 'N' )
      {
        if (input.LA(1) == 'N' || input.LA(1) == 'n') {
          input.consume();

        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }

      }

    } finally {
    }
  }

  // $ANTLR end "N"

  // $ANTLR start "O"
  public final void mO() throws RecognitionException {
    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:504:11:
      // ( ( 'o' | 'O' ) )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:504:12:
      // ( 'o' | 'O' )
      {
        if (input.LA(1) == 'O' || input.LA(1) == 'o') {
          input.consume();

        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }

      }

    } finally {
    }
  }

  // $ANTLR end "O"

  // $ANTLR start "P"
  public final void mP() throws RecognitionException {
    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:505:11:
      // ( ( 'p' | 'P' ) )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:505:12:
      // ( 'p' | 'P' )
      {
        if (input.LA(1) == 'P' || input.LA(1) == 'p') {
          input.consume();

        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }

      }

    } finally {
    }
  }

  // $ANTLR end "P"

  // $ANTLR start "Q"
  public final void mQ() throws RecognitionException {
    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:506:11:
      // ( ( 'q' | 'Q' ) )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:506:12:
      // ( 'q' | 'Q' )
      {
        if (input.LA(1) == 'Q' || input.LA(1) == 'q') {
          input.consume();

        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }

      }

    } finally {
    }
  }

  // $ANTLR end "Q"

  // $ANTLR start "R"
  public final void mR() throws RecognitionException {
    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:507:11:
      // ( ( 'r' | 'R' ) )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:507:12:
      // ( 'r' | 'R' )
      {
        if (input.LA(1) == 'R' || input.LA(1) == 'r') {
          input.consume();

        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }

      }

    } finally {
    }
  }

  // $ANTLR end "R"

  // $ANTLR start "S"
  public final void mS() throws RecognitionException {
    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:508:11:
      // ( ( 's' | 'S' ) )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:508:12:
      // ( 's' | 'S' )
      {
        if (input.LA(1) == 'S' || input.LA(1) == 's') {
          input.consume();

        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }

      }

    } finally {
    }
  }

  // $ANTLR end "S"

  // $ANTLR start "T"
  public final void mT() throws RecognitionException {
    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:509:11:
      // ( ( 't' | 'T' ) )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:509:12:
      // ( 't' | 'T' )
      {
        if (input.LA(1) == 'T' || input.LA(1) == 't') {
          input.consume();

        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }

      }

    } finally {
    }
  }

  // $ANTLR end "T"

  // $ANTLR start "U"
  public final void mU() throws RecognitionException {
    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:510:11:
      // ( ( 'u' | 'U' ) )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:510:12:
      // ( 'u' | 'U' )
      {
        if (input.LA(1) == 'U' || input.LA(1) == 'u') {
          input.consume();

        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }

      }

    } finally {
    }
  }

  // $ANTLR end "U"

  // $ANTLR start "V"
  public final void mV() throws RecognitionException {
    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:511:11:
      // ( ( 'v' | 'V' ) )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:511:12:
      // ( 'v' | 'V' )
      {
        if (input.LA(1) == 'V' || input.LA(1) == 'v') {
          input.consume();

        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }

      }

    } finally {
    }
  }

  // $ANTLR end "V"

  // $ANTLR start "W"
  public final void mW() throws RecognitionException {
    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:512:11:
      // ( ( 'w' | 'W' ) )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:512:12:
      // ( 'w' | 'W' )
      {
        if (input.LA(1) == 'W' || input.LA(1) == 'w') {
          input.consume();

        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }

      }

    } finally {
    }
  }

  // $ANTLR end "W"

  // $ANTLR start "X"
  public final void mX() throws RecognitionException {
    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:513:11:
      // ( ( 'x' | 'X' ) )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:513:12:
      // ( 'x' | 'X' )
      {
        if (input.LA(1) == 'X' || input.LA(1) == 'x') {
          input.consume();

        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }

      }

    } finally {
    }
  }

  // $ANTLR end "X"

  // $ANTLR start "Y"
  public final void mY() throws RecognitionException {
    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:514:11:
      // ( ( 'y' | 'Y' ) )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:514:12:
      // ( 'y' | 'Y' )
      {
        if (input.LA(1) == 'Y' || input.LA(1) == 'y') {
          input.consume();

        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }

      }

    } finally {
    }
  }

  // $ANTLR end "Y"

  // $ANTLR start "Z"
  public final void mZ() throws RecognitionException {
    try {
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:515:11:
      // ( ( 'z' | 'Z' ) )
      // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
      // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:515:12:
      // ( 'z' | 'Z' )
      {
        if (input.LA(1) == 'Z' || input.LA(1) == 'z') {
          input.consume();

        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }

      }

    } finally {
    }
  }

  // $ANTLR end "Z"

  @Override
  public void mTokens() throws RecognitionException {
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:8:
    // ( STATE | RULE | WOLFRAMRULE | DIMENSIONS | NEIGHBORHOOD | MOORE |
    // NEUMANN | FREE | ARROW | LEFT_PAREN | RIGHT_PAREN | LEFT_CURLEY |
    // RIGHT_CURLEY | LEFT_BRACE | RIGHT_BRACE | COMMA | COLON | SEMICOLON | OR
    // | AND | NOT | TRUE | FALSE | INTEGER | DOUBLE | CAVERSION | ID |
    // WHITESPACE | LINECOMMENT | LANGUAGECOMMENT | GROUPCOMMENT | UNEXPECTED )
    int alt20 = 32;
    alt20 = dfa20.predict(input);
    switch (alt20) {
    case 1:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:10:
    // STATE
    {
      mSTATE();

    }
      break;
    case 2:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:16:
    // RULE
    {
      mRULE();

    }
      break;
    case 3:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:21:
    // WOLFRAMRULE
    {
      mWOLFRAMRULE();

    }
      break;
    case 4:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:33:
    // DIMENSIONS
    {
      mDIMENSIONS();

    }
      break;
    case 5:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:44:
    // NEIGHBORHOOD
    {
      mNEIGHBORHOOD();

    }
      break;
    case 6:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:57:
    // MOORE
    {
      mMOORE();

    }
      break;
    case 7:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:63:
    // NEUMANN
    {
      mNEUMANN();

    }
      break;
    case 8:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:71:
    // FREE
    {
      mFREE();

    }
      break;
    case 9:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:76:
    // ARROW
    {
      mARROW();

    }
      break;
    case 10:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:82:
    // LEFT_PAREN
    {
      mLEFT_PAREN();

    }
      break;
    case 11:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:93:
    // RIGHT_PAREN
    {
      mRIGHT_PAREN();

    }
      break;
    case 12:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:105:
    // LEFT_CURLEY
    {
      mLEFT_CURLEY();

    }
      break;
    case 13:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:117:
    // RIGHT_CURLEY
    {
      mRIGHT_CURLEY();

    }
      break;
    case 14:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:130:
    // LEFT_BRACE
    {
      mLEFT_BRACE();

    }
      break;
    case 15:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:141:
    // RIGHT_BRACE
    {
      mRIGHT_BRACE();

    }
      break;
    case 16:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:153:
    // COMMA
    {
      mCOMMA();

    }
      break;
    case 17:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:159:
    // COLON
    {
      mCOLON();

    }
      break;
    case 18:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:165:
    // SEMICOLON
    {
      mSEMICOLON();

    }
      break;
    case 19:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:175:
    // OR
    {
      mOR();

    }
      break;
    case 20:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:178:
    // AND
    {
      mAND();

    }
      break;
    case 21:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:182:
    // NOT
    {
      mNOT();

    }
      break;
    case 22:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:186:
    // TRUE
    {
      mTRUE();

    }
      break;
    case 23:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:191:
    // FALSE
    {
      mFALSE();

    }
      break;
    case 24:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:197:
    // INTEGER
    {
      mINTEGER();

    }
      break;
    case 25:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:205:
    // DOUBLE
    {
      mDOUBLE();

    }
      break;
    case 26:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:212:
    // CAVERSION
    {
      mCAVERSION();

    }
      break;
    case 27:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:222:
    // ID
    {
      mID();

    }
      break;
    case 28:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:225:
    // WHITESPACE
    {
      mWHITESPACE();

    }
      break;
    case 29:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:236:
    // LINECOMMENT
    {
      mLINECOMMENT();

    }
      break;
    case 30:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:248:
    // LANGUAGECOMMENT
    {
      mLANGUAGECOMMENT();

    }
      break;
    case 31:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:264:
    // GROUPCOMMENT
    {
      mGROUPCOMMENT();

    }
      break;
    case 32:
    // C:/Documents and Settings/stefan/Desktop/James II Dev/James II
    // Trunk/plugins/ca_rules_f_antlr/src/model/carules/reader/antlr/parser/Carule.g:1:277:
    // UNEXPECTED
    {
      mUNEXPECTED();

    }
      break;

    }

  }

  protected DFA1 dfa1 = new DFA1(this);

  protected DFA20 dfa20 = new DFA20(this);

  static final String DFA1_eotS = "\12\uffff";

  static final String DFA1_eofS = "\12\uffff";

  static final String DFA1_minS =
      "\1\156\1\145\1\151\1\147\1\150\1\142\1\157\1\162\2\uffff";

  static final String DFA1_maxS =
      "\1\156\1\145\1\151\1\147\1\150\1\142\1\157\1\165\2\uffff";

  static final String DFA1_acceptS = "\10\uffff\1\1\1\2";

  static final String DFA1_specialS = "\12\uffff}>";

  static final String[] DFA1_transitionS = { "\1\1", "\1\2", "\1\3", "\1\4",
      "\1\5", "\1\6", "\1\7", "\1\10\2\uffff\1\11", "", "" };

  static final short[] DFA1_eot = DFA.unpackEncodedString(DFA1_eotS);

  static final short[] DFA1_eof = DFA.unpackEncodedString(DFA1_eofS);

  static final char[] DFA1_min = DFA
      .unpackEncodedStringToUnsignedChars(DFA1_minS);

  static final char[] DFA1_max = DFA
      .unpackEncodedStringToUnsignedChars(DFA1_maxS);

  static final short[] DFA1_accept = DFA.unpackEncodedString(DFA1_acceptS);

  static final short[] DFA1_special = DFA.unpackEncodedString(DFA1_specialS);

  static final short[][] DFA1_transition;

  static {
    int numStates = DFA1_transitionS.length;
    DFA1_transition = new short[numStates][];
    for (int i = 0; i < numStates; i++) {
      DFA1_transition[i] = DFA.unpackEncodedString(DFA1_transitionS[i]);
    }
  }

  class DFA1 extends DFA {

    public DFA1(BaseRecognizer recognizer) {
      this.recognizer = recognizer;
      this.decisionNumber = 1;
      this.eot = DFA1_eot;
      this.eof = DFA1_eof;
      this.min = DFA1_min;
      this.max = DFA1_max;
      this.accept = DFA1_accept;
      this.special = DFA1_special;
      this.transition = DFA1_transition;
    }

    @Override
    public String getDescription() {
      return "425:1: NEIGHBORHOOD : ( 'neighborhood' | 'neighbourhood' );";
    }
  }

  static final String DFA20_eotS =
      "\1\36\7\43\1\36\12\uffff\1\43\1\uffff\1\70\1\uffff\1\43\3\uffff"
          + "\1\43\1\76\2\uffff\1\43\2\uffff\1\43\1\uffff\11\43\1\uffff\1\76"
          + "\12\uffff\1\70\1\uffff\1\43\1\uffff\1\43\6\uffff\6\43\1\74\3\43"
          + "\1\72\1\43\2\uffff\1\43\1\135\5\43\1\143\1\43\1\145\1\146\1\uffff"
          + "\4\43\1\153\1\uffff\1\154\2\uffff\4\43\2\uffff\3\43\1\165\4\43\1"
          + "\uffff\5\43\1\177\2\43\1\u0082\1\uffff\2\43\1\uffff\1\u0085\1\43"
          + "\1\uffff\1\u0085";

  static final String DFA20_eofS = "\u0087\uffff";

  static final String DFA20_minS =
      "\1\0\1\164\1\165\1\157\1\151\1\145\1\157\1\141\1\60\12\uffff\1"
          + "\162\1\uffff\1\141\1\uffff\1\156\3\uffff\1\162\1\56\2\uffff\1\103"
          + "\2\uffff\1\52\1\uffff\1\141\2\154\1\155\1\151\1\164\1\157\1\145"
          + "\1\154\1\uffff\1\56\12\uffff\1\141\1\uffff\1\144\1\uffff\1\165\5"
          + "\uffff\1\0\1\164\1\145\1\146\1\145\1\147\1\155\1\141\1\162\1\145"
          + "\1\163\1\141\1\145\2\uffff\1\145\1\141\1\162\1\156\1\150\1\141\1"
          + "\145\1\141\1\145\2\141\1\uffff\1\141\1\163\1\142\1\156\1\141\1\uffff"
          + "\1\141\2\uffff\1\155\1\151\1\157\1\156\2\uffff\1\162\1\157\1\162"
          + "\1\141\1\165\1\156\1\150\1\162\1\uffff\1\154\1\163\1\157\1\150\1"
          + "\145\1\141\2\157\1\141\1\uffff\1\144\1\157\1\uffff\1\141\1\144\1"
          + "\uffff\1\141";

  static final String DFA20_maxS =
      "\1\uffff\1\164\1\165\1\157\1\151\2\157\1\162\1\76\12\uffff\1\162"
          + "\1\uffff\1\172\1\uffff\1\156\3\uffff\1\162\1\71\2\uffff\1\143\2"
          + "\uffff\1\57\1\uffff\1\141\2\154\1\155\1\165\1\164\1\157\1\145\1"
          + "\154\1\uffff\1\71\12\uffff\1\172\1\uffff\1\144\1\uffff\1\165\5\uffff"
          + "\1\uffff\1\164\1\145\1\146\1\145\1\147\1\155\1\172\1\162\1\145\1"
          + "\163\1\172\1\145\2\uffff\1\145\1\172\1\162\1\156\1\150\1\141\1\145"
          + "\1\172\1\145\2\172\1\uffff\1\141\1\163\1\142\1\156\1\172\1\uffff"
          + "\1\172\2\uffff\1\155\1\151\1\157\1\156\2\uffff\1\162\1\157\1\165"
          + "\1\172\1\165\1\156\1\150\1\162\1\uffff\1\154\1\163\1\157\1\150\1"
          + "\145\1\172\2\157\1\172\1\uffff\1\144\1\157\1\uffff\1\172\1\144\1"
          + "\uffff\1\172";

  static final String DFA20_acceptS =
      "\11\uffff\1\12\1\13\1\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\uffff"
          + "\1\23\1\uffff\1\24\1\uffff\1\24\2\25\2\uffff\2\31\1\uffff\1\33\1"
          + "\34\1\uffff\1\40\11\uffff\1\11\1\uffff\1\12\1\13\1\14\1\15\1\16"
          + "\1\17\1\20\1\21\1\22\1\23\1\uffff\1\24\1\uffff\1\25\1\uffff\1\30"
          + "\1\32\1\33\1\34\1\35\15\uffff\1\36\1\37\13\uffff\1\2\5\uffff\1\10"
          + "\1\uffff\1\26\1\1\4\uffff\1\6\1\27\10\uffff\1\7\11\uffff\1\4\2\uffff"
          + "\1\3\2\uffff\1\5\1\uffff";

  static final String DFA20_specialS = "\1\1\102\uffff\1\0\103\uffff}>";

  static final String[] DFA20_transitionS = {
      "\11\43\2\41\1\43\2\41\22\43\1\41\1\31\4\43\1\26\1\43\1\11\1"
          + "\12\1\43\1\24\1\17\1\10\1\35\1\42\12\34\1\20\1\21\4\43\1\37"
          + "\32\40\1\15\1\43\1\16\1\30\2\43\1\27\2\43\1\4\1\43\1\7\6\43"
          + "\1\6\1\5\1\23\2\43\1\2\1\1\1\33\1\43\1\25\1\3\3\43\1\13\1\22"
          + "\1\14\1\32\uff81\43", "\1\44", "\1\45", "\1\46", "\1\47",
      "\1\50\11\uffff\1\51", "\1\52", "\1\54\20\uffff\1\53",
      "\12\56\4\uffff\1\55", "", "", "", "", "", "", "", "", "", "", "\1\71",
      "", "\32\43", "", "\1\73", "", "", "", "\1\75", "\1\36\1\uffff\12\56",
      "", "", "\1\77\37\uffff\1\77", "", "", "\1\103\4\uffff\1\102", "",
      "\1\104", "\1\105", "\1\106", "\1\107", "\1\110\13\uffff\1\111",
      "\1\112", "\1\113", "\1\114", "\1\115", "", "\1\36\1\uffff\12\56", "",
      "", "", "", "", "", "", "", "", "", "\32\43", "", "\1\116", "", "\1\117",
      "", "", "", "", "", "\52\121\1\120\uffd5\121", "\1\122", "\1\123",
      "\1\124", "\1\125", "\1\126", "\1\127", "\32\43", "\1\130", "\1\131",
      "\1\132", "\32\43", "\1\133", "", "", "\1\134", "\32\43", "\1\136",
      "\1\137", "\1\140", "\1\141", "\1\142", "\32\43", "\1\144", "\32\43",
      "\32\43", "", "\1\147", "\1\150", "\1\151", "\1\152", "\32\43", "",
      "\32\43", "", "", "\1\155", "\1\156", "\1\157", "\1\160", "", "",
      "\1\161", "\1\162", "\1\163\2\uffff\1\164", "\32\43", "\1\166", "\1\167",
      "\1\170", "\1\171", "", "\1\172", "\1\173", "\1\174", "\1\175", "\1\176",
      "\32\43", "\1\u0080", "\1\u0081", "\32\43", "", "\1\u0083", "\1\u0084",
      "", "\32\43", "\1\u0086", "", "\32\43" };

  static final short[] DFA20_eot = DFA.unpackEncodedString(DFA20_eotS);

  static final short[] DFA20_eof = DFA.unpackEncodedString(DFA20_eofS);

  static final char[] DFA20_min = DFA
      .unpackEncodedStringToUnsignedChars(DFA20_minS);

  static final char[] DFA20_max = DFA
      .unpackEncodedStringToUnsignedChars(DFA20_maxS);

  static final short[] DFA20_accept = DFA.unpackEncodedString(DFA20_acceptS);

  static final short[] DFA20_special = DFA.unpackEncodedString(DFA20_specialS);

  static final short[][] DFA20_transition;

  static {
    int numStates = DFA20_transitionS.length;
    DFA20_transition = new short[numStates][];
    for (int i = 0; i < numStates; i++) {
      DFA20_transition[i] = DFA.unpackEncodedString(DFA20_transitionS[i]);
    }
  }

  class DFA20 extends DFA {

    public DFA20(BaseRecognizer recognizer) {
      this.recognizer = recognizer;
      this.decisionNumber = 20;
      this.eot = DFA20_eot;
      this.eof = DFA20_eof;
      this.min = DFA20_min;
      this.max = DFA20_max;
      this.accept = DFA20_accept;
      this.special = DFA20_special;
      this.transition = DFA20_transition;
    }

    @Override
    public String getDescription() {
      return "1:1: Tokens : ( STATE | RULE | WOLFRAMRULE | DIMENSIONS | NEIGHBORHOOD | MOORE | NEUMANN | FREE | ARROW | LEFT_PAREN | RIGHT_PAREN | LEFT_CURLEY | RIGHT_CURLEY | LEFT_BRACE | RIGHT_BRACE | COMMA | COLON | SEMICOLON | OR | AND | NOT | TRUE | FALSE | INTEGER | DOUBLE | CAVERSION | ID | WHITESPACE | LINECOMMENT | LANGUAGECOMMENT | GROUPCOMMENT | UNEXPECTED );";
    }

    @Override
    public int specialStateTransition(int s, IntStream _input)
        throws NoViableAltException {
      IntStream input = _input;
      int _s = s;
      switch (s) {
      case 0:
        int LA20_67 = input.LA(1);

        s = -1;
        if ((LA20_67 == '*')) {
          s = 80;
        }

        else if (((LA20_67 >= '\u0000' && LA20_67 <= ')') || (LA20_67 >= '+' && LA20_67 <= '\uFFFF'))) {
          s = 81;
        }

        if (s >= 0) {
          return s;
        }
        break;
      case 1:
        int LA20_0 = input.LA(1);

        s = -1;
        if ((LA20_0 == 's')) {
          s = 1;
        }

        else if ((LA20_0 == 'r')) {
          s = 2;
        }

        else if ((LA20_0 == 'w')) {
          s = 3;
        }

        else if ((LA20_0 == 'd')) {
          s = 4;
        }

        else if ((LA20_0 == 'n')) {
          s = 5;
        }

        else if ((LA20_0 == 'm')) {
          s = 6;
        }

        else if ((LA20_0 == 'f')) {
          s = 7;
        }

        else if ((LA20_0 == '-')) {
          s = 8;
        }

        else if ((LA20_0 == '(')) {
          s = 9;
        }

        else if ((LA20_0 == ')')) {
          s = 10;
        }

        else if ((LA20_0 == '{')) {
          s = 11;
        }

        else if ((LA20_0 == '}')) {
          s = 12;
        }

        else if ((LA20_0 == '[')) {
          s = 13;
        }

        else if ((LA20_0 == ']')) {
          s = 14;
        }

        else if ((LA20_0 == ',')) {
          s = 15;
        }

        else if ((LA20_0 == ':')) {
          s = 16;
        }

        else if ((LA20_0 == ';')) {
          s = 17;
        }

        else if ((LA20_0 == '|')) {
          s = 18;
        }

        else if ((LA20_0 == 'o')) {
          s = 19;
        }

        else if ((LA20_0 == '+')) {
          s = 20;
        }

        else if ((LA20_0 == 'v')) {
          s = 21;
        }

        else if ((LA20_0 == '&')) {
          s = 22;
        }

        else if ((LA20_0 == 'a')) {
          s = 23;
        }

        else if ((LA20_0 == '^')) {
          s = 24;
        }

        else if ((LA20_0 == '!')) {
          s = 25;
        }

        else if ((LA20_0 == '~')) {
          s = 26;
        }

        else if ((LA20_0 == 't')) {
          s = 27;
        }

        else if (((LA20_0 >= '0' && LA20_0 <= '9'))) {
          s = 28;
        }

        else if ((LA20_0 == '.')) {
          s = 29;
        }

        else if ((LA20_0 == '@')) {
          s = 31;
        }

        else if (((LA20_0 >= 'A' && LA20_0 <= 'Z'))) {
          s = 32;
        }

        else if (((LA20_0 >= '\t' && LA20_0 <= '\n')
            || (LA20_0 >= '\f' && LA20_0 <= '\r') || LA20_0 == ' ')) {
          s = 33;
        }

        else if ((LA20_0 == '/')) {
          s = 34;
        }

        else if (((LA20_0 >= '\u0000' && LA20_0 <= '\b') || LA20_0 == '\u000B'
            || (LA20_0 >= '\u000E' && LA20_0 <= '\u001F')
            || (LA20_0 >= '\"' && LA20_0 <= '%') || LA20_0 == '\''
            || LA20_0 == '*' || (LA20_0 >= '<' && LA20_0 <= '?')
            || LA20_0 == '\\' || (LA20_0 >= '_' && LA20_0 <= '`')
            || (LA20_0 >= 'b' && LA20_0 <= 'c') || LA20_0 == 'e'
            || (LA20_0 >= 'g' && LA20_0 <= 'l')
            || (LA20_0 >= 'p' && LA20_0 <= 'q') || LA20_0 == 'u'
            || (LA20_0 >= 'x' && LA20_0 <= 'z') || (LA20_0 >= '\u007F' && LA20_0 <= '\uFFFF'))) {
          s = 35;
        } else {
          s = 30;
        }

        if (s >= 0) {
          return s;
        }
        break;
      }
      NoViableAltException nvae =
          new NoViableAltException(getDescription(), 20, _s, input);
      error(nvae);
      throw nvae;
    }
  }

}