// $ANTLR 3.3 Nov 30, 2010 12:50:56 model\\mlspace\\reader\\antlr\\MLSpaceCompositeLexer.g

 package model.mlspace.reader.antlr;
 
 import java.util.logging.Level;
 import org.jamesii.core.util.logging.ApplicationLogger;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class MLSpaceCompositeLexer extends Lexer {
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

      @Override
      public void emitErrorMessage(String msg) {
        ApplicationLogger.log(Level.SEVERE, msg, 3);
      }


    // delegates
    public MLSpaceCompositeLexer_MLSpaceLexer gMLSpaceLexer;
    public MLSpaceCompositeLexer_ExpressionLexer gExpressionLexer;
    public MLSpaceCompositeLexer_CommentAndWhitespaceLexer gCommentAndWhitespaceLexer;
    // delegators

    public MLSpaceCompositeLexer() {;} 
    public MLSpaceCompositeLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public MLSpaceCompositeLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
        gMLSpaceLexer = new MLSpaceCompositeLexer_MLSpaceLexer(input, state, this);
        gExpressionLexer = new MLSpaceCompositeLexer_ExpressionLexer(input, state, this);
        gCommentAndWhitespaceLexer = new MLSpaceCompositeLexer_CommentAndWhitespaceLexer(input, state, this);
    }
    public String getGrammarFileName() { return "model\\mlspace\\reader\\antlr\\MLSpaceCompositeLexer.g"; }

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // model\\mlspace\\reader\\antlr\\MLSpaceCompositeLexer.g:18:8: ( '\"' ID '\"' | '\\'' ID '\\'' )
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0=='\"') ) {
                alt1=1;
            }
            else if ( (LA1_0=='\'') ) {
                alt1=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }
            switch (alt1) {
                case 1 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceCompositeLexer.g:18:10: '\"' ID '\"'
                    {
                    match('\"'); 
                    gExpressionLexer.mID(); 
                    match('\"'); 

                    }
                    break;
                case 2 :
                    // model\\mlspace\\reader\\antlr\\MLSpaceCompositeLexer.g:18:23: '\\'' ID '\\''
                    {
                    match('\''); 
                    gExpressionLexer.mID(); 
                    match('\''); 

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING"

    public void mTokens() throws RecognitionException {
        // model\\mlspace\\reader\\antlr\\MLSpaceCompositeLexer.g:1:8: ( STRING | MLSpaceLexer. Tokens | ExpressionLexer. Tokens | CommentAndWhitespaceLexer. Tokens )
        int alt2=4;
        alt2 = dfa2.predict(input);
        switch (alt2) {
            case 1 :
                // model\\mlspace\\reader\\antlr\\MLSpaceCompositeLexer.g:1:10: STRING
                {
                mSTRING(); 

                }
                break;
            case 2 :
                // model\\mlspace\\reader\\antlr\\MLSpaceCompositeLexer.g:1:17: MLSpaceLexer. Tokens
                {
                gMLSpaceLexer.mTokens(); 

                }
                break;
            case 3 :
                // model\\mlspace\\reader\\antlr\\MLSpaceCompositeLexer.g:1:37: ExpressionLexer. Tokens
                {
                gExpressionLexer.mTokens(); 

                }
                break;
            case 4 :
                // model\\mlspace\\reader\\antlr\\MLSpaceCompositeLexer.g:1:60: CommentAndWhitespaceLexer. Tokens
                {
                gCommentAndWhitespaceLexer.mTokens(); 

                }
                break;

        }

    }


    protected DFA2 dfa2 = new DFA2(this);
    static final String DFA2_eotS =
        "\3\uffff\1\2\1\16\1\2\10\16\1\uffff\1\16\1\uffff\6\16\2\2\1\16"+
        "\2\2\1\16\1\2\4\16\1\2\1\16\1\2\14\16\2\2\1\16\1\2\4\16\1\2";
    static final String DFA2_eofS =
        "\72\uffff";
    static final String DFA2_minS =
        "\1\11\2\uffff\1\75\1\76\1\60\1\117\1\157\1\143\1\151\1\145\1\116"+
        "\1\156\1\114\1\uffff\1\52\1\uffff\1\122\1\162\1\145\1\143\1\156"+
        "\1\154\2\60\1\123\2\60\1\145\1\60\1\144\1\145\1\154\1\160\1\60\1"+
        "\160\1\60\3\141\1\151\1\163\2\143\4\145\1\144\2\60\1\115\1\60\1"+
        "\157\1\144\1\145\1\154\1\60";
    static final String DFA2_maxS =
        "\1\u00b3\2\uffff\1\75\1\76\1\71\1\117\1\162\1\143\1\151\1\145\1"+
        "\116\1\156\1\114\1\uffff\1\57\1\uffff\1\122\1\162\1\145\1\143\1"+
        "\156\1\160\2\172\1\123\2\172\1\145\1\172\1\144\1\145\1\154\1\160"+
        "\1\172\1\160\1\172\3\141\1\151\1\163\2\143\4\145\1\144\2\172\1\115"+
        "\1\172\1\157\1\144\1\145\1\154\1\172";
    static final String DFA2_acceptS =
        "\1\uffff\1\1\1\2\13\uffff\1\3\1\uffff\1\4\51\uffff";
    static final String DFA2_specialS =
        "\72\uffff}>";
    static final String[] DFA2_transitionS = {
            "\2\20\2\uffff\1\20\22\uffff\1\20\1\uffff\1\1\1\2\3\uffff\1"+
            "\1\4\16\1\2\1\4\1\5\1\17\12\16\1\3\4\2\1\uffff\1\2\5\16\1\6"+
            "\2\16\1\13\3\16\1\15\15\16\1\2\1\uffff\1\2\1\16\2\uffff\1\16"+
            "\1\11\3\16\1\7\2\16\1\14\5\16\1\10\2\16\1\12\10\16\1\2\1\uffff"+
            "\1\2\62\uffff\1\16\1\uffff\2\16",
            "",
            "",
            "\1\16",
            "\1\2",
            "\12\16",
            "\1\21",
            "\1\22\2\uffff\1\23",
            "\1\24",
            "\1\25",
            "\1\26",
            "\1\27",
            "\1\30",
            "\1\31",
            "",
            "\1\20\4\uffff\1\20",
            "",
            "\1\32",
            "\1\33",
            "\1\34",
            "\1\35",
            "\1\36",
            "\1\37\3\uffff\1\40",
            "\12\16\7\uffff\32\16\4\uffff\1\16\1\uffff\32\16",
            "\12\16\7\uffff\32\16\4\uffff\1\16\1\uffff\32\16",
            "\1\41",
            "\12\16\7\uffff\32\16\4\uffff\1\16\1\uffff\32\16",
            "\12\16\7\uffff\32\16\4\uffff\1\16\1\uffff\32\16",
            "\1\42",
            "\12\16\7\uffff\32\16\4\uffff\1\16\1\uffff\24\16\1\43\5\16",
            "\1\44",
            "\1\45",
            "\1\46",
            "\1\47",
            "\12\16\7\uffff\32\16\4\uffff\1\16\1\uffff\32\16",
            "\1\50",
            "\12\16\7\uffff\32\16\4\uffff\1\16\1\uffff\32\16",
            "\1\51",
            "\1\52",
            "\1\53",
            "\1\54",
            "\1\55",
            "\1\56",
            "\1\57",
            "\1\60",
            "\1\61",
            "\1\62",
            "\1\63",
            "\1\64",
            "\12\16\7\uffff\32\16\4\uffff\1\16\1\uffff\32\16",
            "\12\16\7\uffff\32\16\4\uffff\1\16\1\uffff\32\16",
            "\1\65",
            "\12\16\7\uffff\32\16\4\uffff\1\16\1\uffff\32\16",
            "\1\66",
            "\1\67",
            "\1\70",
            "\1\71",
            "\12\16\7\uffff\32\16\4\uffff\1\16\1\uffff\32\16"
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
            return "1:1: Tokens : ( STRING | MLSpaceLexer. Tokens | ExpressionLexer. Tokens | CommentAndWhitespaceLexer. Tokens );";
        }
    }
 

}
