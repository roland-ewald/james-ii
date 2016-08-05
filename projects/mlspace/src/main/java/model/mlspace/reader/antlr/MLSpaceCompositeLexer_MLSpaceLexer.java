// $ANTLR 3.2 Sep 23, 2009 14:05:07 MLSpaceLexer.g

 package model.mlspace.reader.antlr;
 
 import java.util.logging.Level;
 import org.jamesii.core.util.logging.ApplicationLogger;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class MLSpaceCompositeLexer_MLSpaceLexer extends Lexer {
    public static final int BECOMES=29;
    public static final int MINUS=33;
    public static final int CUB=38;
    public static final int ELSE=43;
    public static final int ID=45;
    public static final int REPLACE=24;
    public static final int IF=41;
    public static final int Tokens=49;
    public static final int BIND=22;
    public static final int IN=25;
    public static final int SEMIC=9;
    public static final int DOT=18;
    public static final int FOR=19;
    public static final int RELEASE=23;
    public static final int EQ=14;
    public static final int HASH=13;
    public static final int AT=12;
    public static final int MIN=39;
    public static final int SQR=37;
    public static final int POW=36;
    public static final int THEN=42;
    public static final int PLUS=32;
    public static final int FLOAT=31;
    public static final int R_BRACE=7;
    public static final int MAX=40;
    public static final int L_BRACKET=4;
    public static final int L_PAREN=27;
    public static final int FREE=20;
    public static final int COMMENT=46;
    public static final int ARROW=11;
    public static final int R_PAREN=28;
    public static final int WS=47;
    public static final int EOF=-1;
    public static final int L_BRACE=6;
    public static final int COMMA=10;
    public static final int TIMES=34;
    public static final int COLON=8;
    public static final int DOTS=17;
    public static final int R_BRACKET=5;
    public static final int OCC=21;
    public static final int DIV=35;
    public static final int DEGREES=44;
    public static final int LESSTHAN=15;
    public static final int EXPONENT=30;
    public static final int STRING=48;
    public static final int GREATERTHAN=16;
    public static final int MODELNAMEKW=26;

    // delegates
    // delegators
    public MLSpaceCompositeLexer gMLSpaceCompositeLexer;
    public MLSpaceCompositeLexer gParent;

    public MLSpaceCompositeLexer_MLSpaceLexer() {;} 
    public MLSpaceCompositeLexer_MLSpaceLexer(CharStream input, MLSpaceCompositeLexer gMLSpaceCompositeLexer) {
        this(input, new RecognizerSharedState(), gMLSpaceCompositeLexer);
    }
    public MLSpaceCompositeLexer_MLSpaceLexer(CharStream input, RecognizerSharedState state, MLSpaceCompositeLexer gMLSpaceCompositeLexer) {
        super(input,state);

        this.gMLSpaceCompositeLexer = gMLSpaceCompositeLexer;
        gParent = gMLSpaceCompositeLexer;
    }
    public String getGrammarFileName() { return "MLSpaceLexer.g"; }

    // $ANTLR start "L_BRACKET"
    public final void mL_BRACKET() throws RecognitionException {
        try {
            int _type = L_BRACKET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // MLSpaceLexer.g:7:11: ( '[' )
            // MLSpaceLexer.g:7:13: '['
            {
            match('['); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "L_BRACKET"

    // $ANTLR start "R_BRACKET"
    public final void mR_BRACKET() throws RecognitionException {
        try {
            int _type = R_BRACKET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // MLSpaceLexer.g:8:11: ( ']' )
            // MLSpaceLexer.g:8:13: ']'
            {
            match(']'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "R_BRACKET"

    // $ANTLR start "L_BRACE"
    public final void mL_BRACE() throws RecognitionException {
        try {
            int _type = L_BRACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // MLSpaceLexer.g:10:9: ( '{' )
            // MLSpaceLexer.g:10:11: '{'
            {
            match('{'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "L_BRACE"

    // $ANTLR start "R_BRACE"
    public final void mR_BRACE() throws RecognitionException {
        try {
            int _type = R_BRACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // MLSpaceLexer.g:11:9: ( '}' )
            // MLSpaceLexer.g:11:11: '}'
            {
            match('}'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "R_BRACE"

    // $ANTLR start "COLON"
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // MLSpaceLexer.g:13:7: ( ':' )
            // MLSpaceLexer.g:13:9: ':'
            {
            match(':'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COLON"

    // $ANTLR start "SEMIC"
    public final void mSEMIC() throws RecognitionException {
        try {
            int _type = SEMIC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // MLSpaceLexer.g:14:7: ( ';' )
            // MLSpaceLexer.g:14:9: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SEMIC"

    // $ANTLR start "COMMA"
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // MLSpaceLexer.g:15:7: ( ',' )
            // MLSpaceLexer.g:15:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMA"

    // $ANTLR start "ARROW"
    public final void mARROW() throws RecognitionException {
        try {
            int _type = ARROW;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // MLSpaceLexer.g:16:7: ( '->' )
            // MLSpaceLexer.g:16:13: '->'
            {
            match("->"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ARROW"

    // $ANTLR start "AT"
    public final void mAT() throws RecognitionException {
        try {
            int _type = AT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // MLSpaceLexer.g:17:5: ( '@' )
            // MLSpaceLexer.g:17:10: '@'
            {
            match('@'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AT"

    // $ANTLR start "HASH"
    public final void mHASH() throws RecognitionException {
        try {
            int _type = HASH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // MLSpaceLexer.g:19:6: ( '#' )
            // MLSpaceLexer.g:19:8: '#'
            {
            match('#'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "HASH"

    // $ANTLR start "EQ"
    public final void mEQ() throws RecognitionException {
        try {
            int _type = EQ;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // MLSpaceLexer.g:21:4: ( '=' )
            // MLSpaceLexer.g:21:6: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EQ"

    // $ANTLR start "LESSTHAN"
    public final void mLESSTHAN() throws RecognitionException {
        try {
            int _type = LESSTHAN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // MLSpaceLexer.g:22:10: ( '<' )
            // MLSpaceLexer.g:22:12: '<'
            {
            match('<'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LESSTHAN"

    // $ANTLR start "GREATERTHAN"
    public final void mGREATERTHAN() throws RecognitionException {
        try {
            int _type = GREATERTHAN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // MLSpaceLexer.g:23:13: ( '>' )
            // MLSpaceLexer.g:23:15: '>'
            {
            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GREATERTHAN"

    // $ANTLR start "DOTS"
    public final void mDOTS() throws RecognitionException {
        try {
            int _type = DOTS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // MLSpaceLexer.g:25:6: ( '..' | '...' )
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0=='.') ) {
                int LA1_1 = input.LA(2);

                if ( (LA1_1=='.') ) {
                    int LA1_2 = input.LA(3);

                    if ( (LA1_2=='.') ) {
                        alt1=2;
                    }
                    else {
                        alt1=1;}
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 1, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }
            switch (alt1) {
                case 1 :
                    // MLSpaceLexer.g:25:8: '..'
                    {
                    match(".."); 


                    }
                    break;
                case 2 :
                    // MLSpaceLexer.g:25:16: '...'
                    {
                    match("..."); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOTS"

    // $ANTLR start "DOT"
    public final void mDOT() throws RecognitionException {
        try {
            int _type = DOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // MLSpaceLexer.g:27:5: ( '.' )
            // MLSpaceLexer.g:27:7: '.'
            {
            match('.'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOT"

    // $ANTLR start "FOR"
    public final void mFOR() throws RecognitionException {
        try {
            int _type = FOR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // MLSpaceLexer.g:29:4: ( 'FOR' | 'for' )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='F') ) {
                alt2=1;
            }
            else if ( (LA2_0=='f') ) {
                alt2=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // MLSpaceLexer.g:29:6: 'FOR'
                    {
                    match("FOR"); 


                    }
                    break;
                case 2 :
                    // MLSpaceLexer.g:29:12: 'for'
                    {
                    match("for"); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FOR"

    // $ANTLR start "FREE"
    public final void mFREE() throws RecognitionException {
        try {
            int _type = FREE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // MLSpaceLexer.g:31:5: ( 'free' )
            // MLSpaceLexer.g:31:7: 'free'
            {
            match("free"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FREE"

    // $ANTLR start "OCC"
    public final void mOCC() throws RecognitionException {
        try {
            int _type = OCC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // MLSpaceLexer.g:32:4: ( ( 'occupied' | 'occ' ) )
            // MLSpaceLexer.g:32:6: ( 'occupied' | 'occ' )
            {
            // MLSpaceLexer.g:32:6: ( 'occupied' | 'occ' )
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='o') ) {
                int LA3_1 = input.LA(2);

                if ( (LA3_1=='c') ) {
                    int LA3_2 = input.LA(3);

                    if ( (LA3_2=='c') ) {
                        int LA3_3 = input.LA(4);

                        if ( (LA3_3=='u') ) {
                            alt3=1;
                        }
                        else {
                            alt3=2;}
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 3, 2, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 3, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // MLSpaceLexer.g:32:7: 'occupied'
                    {
                    match("occupied"); 


                    }
                    break;
                case 2 :
                    // MLSpaceLexer.g:32:20: 'occ'
                    {
                    match("occ"); 


                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OCC"

    // $ANTLR start "BIND"
    public final void mBIND() throws RecognitionException {
        try {
            int _type = BIND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // MLSpaceLexer.g:33:5: ( 'bind' )
            // MLSpaceLexer.g:33:7: 'bind'
            {
            match("bind"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BIND"

    // $ANTLR start "RELEASE"
    public final void mRELEASE() throws RecognitionException {
        try {
            int _type = RELEASE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // MLSpaceLexer.g:34:8: ( 'release' )
            // MLSpaceLexer.g:34:10: 'release'
            {
            match("release"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RELEASE"

    // $ANTLR start "REPLACE"
    public final void mREPLACE() throws RecognitionException {
        try {
            int _type = REPLACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // MLSpaceLexer.g:35:8: ( 'replace' )
            // MLSpaceLexer.g:35:10: 'replace'
            {
            match("replace"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "REPLACE"

    // $ANTLR start "IN"
    public final void mIN() throws RecognitionException {
        try {
            int _type = IN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // MLSpaceLexer.g:37:3: ( 'IN' | 'in' )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0=='I') ) {
                alt4=1;
            }
            else if ( (LA4_0=='i') ) {
                alt4=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // MLSpaceLexer.g:37:5: 'IN'
                    {
                    match("IN"); 


                    }
                    break;
                case 2 :
                    // MLSpaceLexer.g:37:10: 'in'
                    {
                    match("in"); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IN"

    // $ANTLR start "MODELNAMEKW"
    public final void mMODELNAMEKW() throws RecognitionException {
        try {
            int _type = MODELNAMEKW;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // MLSpaceLexer.g:39:13: ( 'MLSpaceModel' )
            // MLSpaceLexer.g:39:15: 'MLSpaceModel'
            {
            match("MLSpaceModel"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MODELNAMEKW"

    public void mTokens() throws RecognitionException {
        // MLSpaceLexer.g:1:8: ( L_BRACKET | R_BRACKET | L_BRACE | R_BRACE | COLON | SEMIC | COMMA | ARROW | AT | HASH | EQ | LESSTHAN | GREATERTHAN | DOTS | DOT | FOR | FREE | OCC | BIND | RELEASE | REPLACE | IN | MODELNAMEKW )
        int alt5=23;
        alt5 = dfa5.predict(input);
        switch (alt5) {
            case 1 :
                // MLSpaceLexer.g:1:10: L_BRACKET
                {
                mL_BRACKET(); 

                }
                break;
            case 2 :
                // MLSpaceLexer.g:1:20: R_BRACKET
                {
                mR_BRACKET(); 

                }
                break;
            case 3 :
                // MLSpaceLexer.g:1:30: L_BRACE
                {
                mL_BRACE(); 

                }
                break;
            case 4 :
                // MLSpaceLexer.g:1:38: R_BRACE
                {
                mR_BRACE(); 

                }
                break;
            case 5 :
                // MLSpaceLexer.g:1:46: COLON
                {
                mCOLON(); 

                }
                break;
            case 6 :
                // MLSpaceLexer.g:1:52: SEMIC
                {
                mSEMIC(); 

                }
                break;
            case 7 :
                // MLSpaceLexer.g:1:58: COMMA
                {
                mCOMMA(); 

                }
                break;
            case 8 :
                // MLSpaceLexer.g:1:64: ARROW
                {
                mARROW(); 

                }
                break;
            case 9 :
                // MLSpaceLexer.g:1:70: AT
                {
                mAT(); 

                }
                break;
            case 10 :
                // MLSpaceLexer.g:1:73: HASH
                {
                mHASH(); 

                }
                break;
            case 11 :
                // MLSpaceLexer.g:1:78: EQ
                {
                mEQ(); 

                }
                break;
            case 12 :
                // MLSpaceLexer.g:1:81: LESSTHAN
                {
                mLESSTHAN(); 

                }
                break;
            case 13 :
                // MLSpaceLexer.g:1:90: GREATERTHAN
                {
                mGREATERTHAN(); 

                }
                break;
            case 14 :
                // MLSpaceLexer.g:1:102: DOTS
                {
                mDOTS(); 

                }
                break;
            case 15 :
                // MLSpaceLexer.g:1:107: DOT
                {
                mDOT(); 

                }
                break;
            case 16 :
                // MLSpaceLexer.g:1:111: FOR
                {
                mFOR(); 

                }
                break;
            case 17 :
                // MLSpaceLexer.g:1:115: FREE
                {
                mFREE(); 

                }
                break;
            case 18 :
                // MLSpaceLexer.g:1:120: OCC
                {
                mOCC(); 

                }
                break;
            case 19 :
                // MLSpaceLexer.g:1:124: BIND
                {
                mBIND(); 

                }
                break;
            case 20 :
                // MLSpaceLexer.g:1:129: RELEASE
                {
                mRELEASE(); 

                }
                break;
            case 21 :
                // MLSpaceLexer.g:1:137: REPLACE
                {
                mREPLACE(); 

                }
                break;
            case 22 :
                // MLSpaceLexer.g:1:145: IN
                {
                mIN(); 

                }
                break;
            case 23 :
                // MLSpaceLexer.g:1:148: MODELNAMEKW
                {
                mMODELNAMEKW(); 

                }
                break;

        }

    }


    protected DFA5 dfa5 = new DFA5(this);
    static final String DFA5_eotS =
        "\16\uffff\1\27\15\uffff";
    static final String DFA5_eofS =
        "\34\uffff";
    static final String DFA5_minS =
        "\1\43\15\uffff\1\56\1\uffff\1\157\2\uffff\1\145\5\uffff\1\154\2"+
        "\uffff";
    static final String DFA5_maxS =
        "\1\175\15\uffff\1\56\1\uffff\1\162\2\uffff\1\145\5\uffff\1\160"+
        "\2\uffff";
    static final String DFA5_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1"+
        "\15\1\uffff\1\20\1\uffff\1\22\1\23\1\uffff\1\26\1\27\1\16\1\17\1"+
        "\21\1\uffff\1\24\1\25";
    static final String DFA5_specialS =
        "\34\uffff}>";
    static final String[] DFA5_transitionS = {
            "\1\12\10\uffff\1\7\1\10\1\16\13\uffff\1\5\1\6\1\14\1\13\1\15"+
            "\1\uffff\1\11\5\uffff\1\17\2\uffff\1\24\3\uffff\1\25\15\uffff"+
            "\1\1\1\uffff\1\2\4\uffff\1\22\3\uffff\1\20\2\uffff\1\24\5\uffff"+
            "\1\21\2\uffff\1\23\10\uffff\1\3\1\uffff\1\4",
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
            "",
            "\1\26",
            "",
            "\1\17\2\uffff\1\30",
            "",
            "",
            "\1\31",
            "",
            "",
            "",
            "",
            "",
            "\1\32\3\uffff\1\33",
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
            return "1:1: Tokens : ( L_BRACKET | R_BRACKET | L_BRACE | R_BRACE | COLON | SEMIC | COMMA | ARROW | AT | HASH | EQ | LESSTHAN | GREATERTHAN | DOTS | DOT | FOR | FREE | OCC | BIND | RELEASE | REPLACE | IN | MODELNAMEKW );";
        }
    }
 

}
