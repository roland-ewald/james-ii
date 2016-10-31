// $ANTLR 3.3 Nov 30, 2010 12:50:56 ExpressionLexer.g

 package model.mlspace.reader.antlr;
 
 import java.util.logging.Level;
 import org.jamesii.core.util.logging.ApplicationLogger;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class MLSpaceCompositeLexer_ExpressionLexer extends Lexer {
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
    public MLSpaceCompositeLexer gMLSpaceCompositeLexer;
    public MLSpaceCompositeLexer gParent;

    public MLSpaceCompositeLexer_ExpressionLexer() {;} 
    public MLSpaceCompositeLexer_ExpressionLexer(CharStream input, MLSpaceCompositeLexer gMLSpaceCompositeLexer) {
        this(input, new RecognizerSharedState(), gMLSpaceCompositeLexer);
    }
    public MLSpaceCompositeLexer_ExpressionLexer(CharStream input, RecognizerSharedState state, MLSpaceCompositeLexer gMLSpaceCompositeLexer) {
        super(input,state);

        this.gMLSpaceCompositeLexer = gMLSpaceCompositeLexer;
        gParent = gMLSpaceCompositeLexer;
    }
    public String getGrammarFileName() { return "ExpressionLexer.g"; }

    // $ANTLR start "L_PAREN"
    public final void mL_PAREN() throws RecognitionException {
        try {
            int _type = L_PAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ExpressionLexer.g:3:9: ( '(' )
            // ExpressionLexer.g:3:11: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "L_PAREN"

    // $ANTLR start "R_PAREN"
    public final void mR_PAREN() throws RecognitionException {
        try {
            int _type = R_PAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ExpressionLexer.g:4:9: ( ')' )
            // ExpressionLexer.g:4:11: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "R_PAREN"

    // $ANTLR start "BECOMES"
    public final void mBECOMES() throws RecognitionException {
        try {
            int _type = BECOMES;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ExpressionLexer.g:6:9: ( ':=' )
            // ExpressionLexer.g:6:11: ':='
            {
            match(":="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BECOMES"

    // $ANTLR start "FLOAT"
    public final void mFLOAT() throws RecognitionException {
        try {
            int _type = FLOAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ExpressionLexer.g:8:7: ( ( '0' .. '9' )+ | ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? | '.' ( '0' .. '9' )+ ( EXPONENT )? | ( '0' .. '9' )+ EXPONENT | 'Infinity' )
            int alt8=5;
            alt8 = dfa8.predict(input);
            switch (alt8) {
                case 1 :
                    // ExpressionLexer.g:9:5: ( '0' .. '9' )+
                    {
                    // ExpressionLexer.g:9:5: ( '0' .. '9' )+
                    int cnt1=0;
                    loop1:
                    do {
                        int alt1=2;
                        int LA1_0 = input.LA(1);

                        if ( ((LA1_0>='0' && LA1_0<='9')) ) {
                            alt1=1;
                        }


                        switch (alt1) {
                    	case 1 :
                    	    // ExpressionLexer.g:9:6: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt1 >= 1 ) break loop1;
                                EarlyExitException eee =
                                    new EarlyExitException(1, input);
                                throw eee;
                        }
                        cnt1++;
                    } while (true);


                    }
                    break;
                case 2 :
                    // ExpressionLexer.g:10:7: ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )?
                    {
                    // ExpressionLexer.g:10:7: ( '0' .. '9' )+
                    int cnt2=0;
                    loop2:
                    do {
                        int alt2=2;
                        int LA2_0 = input.LA(1);

                        if ( ((LA2_0>='0' && LA2_0<='9')) ) {
                            alt2=1;
                        }


                        switch (alt2) {
                    	case 1 :
                    	    // ExpressionLexer.g:10:8: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt2 >= 1 ) break loop2;
                                EarlyExitException eee =
                                    new EarlyExitException(2, input);
                                throw eee;
                        }
                        cnt2++;
                    } while (true);

                    match('.'); 
                    // ExpressionLexer.g:10:23: ( '0' .. '9' )*
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( ((LA3_0>='0' && LA3_0<='9')) ) {
                            alt3=1;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // ExpressionLexer.g:10:24: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop3;
                        }
                    } while (true);

                    // ExpressionLexer.g:10:35: ( EXPONENT )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0=='E'||LA4_0=='e') ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // ExpressionLexer.g:10:35: EXPONENT
                            {
                            mEXPONENT(); 

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // ExpressionLexer.g:11:7: '.' ( '0' .. '9' )+ ( EXPONENT )?
                    {
                    match('.'); 
                    // ExpressionLexer.g:11:11: ( '0' .. '9' )+
                    int cnt5=0;
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( ((LA5_0>='0' && LA5_0<='9')) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // ExpressionLexer.g:11:12: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt5 >= 1 ) break loop5;
                                EarlyExitException eee =
                                    new EarlyExitException(5, input);
                                throw eee;
                        }
                        cnt5++;
                    } while (true);

                    // ExpressionLexer.g:11:23: ( EXPONENT )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0=='E'||LA6_0=='e') ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // ExpressionLexer.g:11:23: EXPONENT
                            {
                            mEXPONENT(); 

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // ExpressionLexer.g:12:7: ( '0' .. '9' )+ EXPONENT
                    {
                    // ExpressionLexer.g:12:7: ( '0' .. '9' )+
                    int cnt7=0;
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( ((LA7_0>='0' && LA7_0<='9')) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // ExpressionLexer.g:12:8: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt7 >= 1 ) break loop7;
                                EarlyExitException eee =
                                    new EarlyExitException(7, input);
                                throw eee;
                        }
                        cnt7++;
                    } while (true);

                    mEXPONENT(); 

                    }
                    break;
                case 5 :
                    // ExpressionLexer.g:13:7: 'Infinity'
                    {
                    match("Infinity"); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FLOAT"

    // $ANTLR start "PLUS"
    public final void mPLUS() throws RecognitionException {
        try {
            int _type = PLUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ExpressionLexer.g:15:7: ( '+' )
            // ExpressionLexer.g:15:9: '+'
            {
            match('+'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PLUS"

    // $ANTLR start "MINUS"
    public final void mMINUS() throws RecognitionException {
        try {
            int _type = MINUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ExpressionLexer.g:16:7: ( '-' )
            // ExpressionLexer.g:16:9: '-'
            {
            match('-'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MINUS"

    // $ANTLR start "TIMES"
    public final void mTIMES() throws RecognitionException {
        try {
            int _type = TIMES;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ExpressionLexer.g:17:9: ( '*' )
            // ExpressionLexer.g:17:11: '*'
            {
            match('*'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TIMES"

    // $ANTLR start "DIV"
    public final void mDIV() throws RecognitionException {
        try {
            int _type = DIV;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ExpressionLexer.g:18:7: ( '/' )
            // ExpressionLexer.g:18:9: '/'
            {
            match('/'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DIV"

    // $ANTLR start "POW"
    public final void mPOW() throws RecognitionException {
        try {
            int _type = POW;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ExpressionLexer.g:19:7: ( '^' )
            // ExpressionLexer.g:19:9: '^'
            {
            match('^'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "POW"

    // $ANTLR start "SQR"
    public final void mSQR() throws RecognitionException {
        try {
            int _type = SQR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ExpressionLexer.g:20:7: ( '\\u00B2' )
            // ExpressionLexer.g:20:9: '\\u00B2'
            {
            match('\u00B2'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SQR"

    // $ANTLR start "CUB"
    public final void mCUB() throws RecognitionException {
        try {
            int _type = CUB;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ExpressionLexer.g:21:7: ( '\\u00B3' )
            // ExpressionLexer.g:21:9: '\\u00B3'
            {
            match('\u00B3'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CUB"

    // $ANTLR start "MIN"
    public final void mMIN() throws RecognitionException {
        try {
            int _type = MIN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ExpressionLexer.g:23:5: ( 'min' | 'Min' | 'MIN' )
            int alt9=3;
            int LA9_0 = input.LA(1);

            if ( (LA9_0=='m') ) {
                alt9=1;
            }
            else if ( (LA9_0=='M') ) {
                int LA9_2 = input.LA(2);

                if ( (LA9_2=='i') ) {
                    alt9=2;
                }
                else if ( (LA9_2=='I') ) {
                    alt9=3;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 9, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // ExpressionLexer.g:23:7: 'min'
                    {
                    match("min"); 


                    }
                    break;
                case 2 :
                    // ExpressionLexer.g:23:13: 'Min'
                    {
                    match("Min"); 


                    }
                    break;
                case 3 :
                    // ExpressionLexer.g:23:19: 'MIN'
                    {
                    match("MIN"); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MIN"

    // $ANTLR start "MAX"
    public final void mMAX() throws RecognitionException {
        try {
            int _type = MAX;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ExpressionLexer.g:24:5: ( 'max' | 'Max' | 'MAX' )
            int alt10=3;
            int LA10_0 = input.LA(1);

            if ( (LA10_0=='m') ) {
                alt10=1;
            }
            else if ( (LA10_0=='M') ) {
                int LA10_2 = input.LA(2);

                if ( (LA10_2=='a') ) {
                    alt10=2;
                }
                else if ( (LA10_2=='A') ) {
                    alt10=3;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 10, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // ExpressionLexer.g:24:7: 'max'
                    {
                    match("max"); 


                    }
                    break;
                case 2 :
                    // ExpressionLexer.g:24:13: 'Max'
                    {
                    match("Max"); 


                    }
                    break;
                case 3 :
                    // ExpressionLexer.g:24:19: 'MAX'
                    {
                    match("MAX"); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MAX"

    // $ANTLR start "IF"
    public final void mIF() throws RecognitionException {
        try {
            int _type = IF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ExpressionLexer.g:26:4: ( 'if' | 'If' | 'IF' )
            int alt11=3;
            int LA11_0 = input.LA(1);

            if ( (LA11_0=='i') ) {
                alt11=1;
            }
            else if ( (LA11_0=='I') ) {
                int LA11_2 = input.LA(2);

                if ( (LA11_2=='f') ) {
                    alt11=2;
                }
                else if ( (LA11_2=='F') ) {
                    alt11=3;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 11, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // ExpressionLexer.g:26:6: 'if'
                    {
                    match("if"); 


                    }
                    break;
                case 2 :
                    // ExpressionLexer.g:26:11: 'If'
                    {
                    match("If"); 


                    }
                    break;
                case 3 :
                    // ExpressionLexer.g:26:16: 'IF'
                    {
                    match("IF"); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IF"

    // $ANTLR start "THEN"
    public final void mTHEN() throws RecognitionException {
        try {
            int _type = THEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ExpressionLexer.g:27:6: ( 'then' | 'Then' | 'THEN' )
            int alt12=3;
            int LA12_0 = input.LA(1);

            if ( (LA12_0=='t') ) {
                alt12=1;
            }
            else if ( (LA12_0=='T') ) {
                int LA12_2 = input.LA(2);

                if ( (LA12_2=='h') ) {
                    alt12=2;
                }
                else if ( (LA12_2=='H') ) {
                    alt12=3;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // ExpressionLexer.g:27:8: 'then'
                    {
                    match("then"); 


                    }
                    break;
                case 2 :
                    // ExpressionLexer.g:27:15: 'Then'
                    {
                    match("Then"); 


                    }
                    break;
                case 3 :
                    // ExpressionLexer.g:27:22: 'THEN'
                    {
                    match("THEN"); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "THEN"

    // $ANTLR start "ELSE"
    public final void mELSE() throws RecognitionException {
        try {
            int _type = ELSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ExpressionLexer.g:28:6: ( 'else' | 'Else' | 'ELSE' )
            int alt13=3;
            int LA13_0 = input.LA(1);

            if ( (LA13_0=='e') ) {
                alt13=1;
            }
            else if ( (LA13_0=='E') ) {
                int LA13_2 = input.LA(2);

                if ( (LA13_2=='l') ) {
                    alt13=2;
                }
                else if ( (LA13_2=='L') ) {
                    alt13=3;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 13, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // ExpressionLexer.g:28:8: 'else'
                    {
                    match("else"); 


                    }
                    break;
                case 2 :
                    // ExpressionLexer.g:28:15: 'Else'
                    {
                    match("Else"); 


                    }
                    break;
                case 3 :
                    // ExpressionLexer.g:28:22: 'ELSE'
                    {
                    match("ELSE"); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ELSE"

    // $ANTLR start "DEGREES"
    public final void mDEGREES() throws RecognitionException {
        try {
            int _type = DEGREES;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ExpressionLexer.g:30:9: ( '\\u00B0' )
            // ExpressionLexer.g:30:11: '\\u00B0'
            {
            match('\u00B0'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DEGREES"

    // $ANTLR start "EXPONENT"
    public final void mEXPONENT() throws RecognitionException {
        try {
            // ExpressionLexer.g:33:19: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            // ExpressionLexer.g:33:22: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // ExpressionLexer.g:33:35: ( '+' | '-' )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0=='+'||LA14_0=='-') ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // ExpressionLexer.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;

            }

            // ExpressionLexer.g:33:47: ( '0' .. '9' )+
            int cnt15=0;
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( ((LA15_0>='0' && LA15_0<='9')) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // ExpressionLexer.g:33:48: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt15 >= 1 ) break loop15;
                        EarlyExitException eee =
                            new EarlyExitException(15, input);
                        throw eee;
                }
                cnt15++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "EXPONENT"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ExpressionLexer.g:35:4: ( ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )* )
            // ExpressionLexer.g:35:6: ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // ExpressionLexer.g:36:6: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( ((LA16_0>='0' && LA16_0<='9')||(LA16_0>='A' && LA16_0<='Z')||LA16_0=='_'||(LA16_0>='a' && LA16_0<='z')) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // ExpressionLexer.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop16;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ID"

    public void mTokens() throws RecognitionException {
        // ExpressionLexer.g:1:8: ( L_PAREN | R_PAREN | BECOMES | FLOAT | PLUS | MINUS | TIMES | DIV | POW | SQR | CUB | MIN | MAX | IF | THEN | ELSE | DEGREES | ID )
        int alt17=18;
        alt17 = dfa17.predict(input);
        switch (alt17) {
            case 1 :
                // ExpressionLexer.g:1:10: L_PAREN
                {
                mL_PAREN(); 

                }
                break;
            case 2 :
                // ExpressionLexer.g:1:18: R_PAREN
                {
                mR_PAREN(); 

                }
                break;
            case 3 :
                // ExpressionLexer.g:1:26: BECOMES
                {
                mBECOMES(); 

                }
                break;
            case 4 :
                // ExpressionLexer.g:1:34: FLOAT
                {
                mFLOAT(); 

                }
                break;
            case 5 :
                // ExpressionLexer.g:1:40: PLUS
                {
                mPLUS(); 

                }
                break;
            case 6 :
                // ExpressionLexer.g:1:45: MINUS
                {
                mMINUS(); 

                }
                break;
            case 7 :
                // ExpressionLexer.g:1:51: TIMES
                {
                mTIMES(); 

                }
                break;
            case 8 :
                // ExpressionLexer.g:1:57: DIV
                {
                mDIV(); 

                }
                break;
            case 9 :
                // ExpressionLexer.g:1:61: POW
                {
                mPOW(); 

                }
                break;
            case 10 :
                // ExpressionLexer.g:1:65: SQR
                {
                mSQR(); 

                }
                break;
            case 11 :
                // ExpressionLexer.g:1:69: CUB
                {
                mCUB(); 

                }
                break;
            case 12 :
                // ExpressionLexer.g:1:73: MIN
                {
                mMIN(); 

                }
                break;
            case 13 :
                // ExpressionLexer.g:1:77: MAX
                {
                mMAX(); 

                }
                break;
            case 14 :
                // ExpressionLexer.g:1:81: IF
                {
                mIF(); 

                }
                break;
            case 15 :
                // ExpressionLexer.g:1:84: THEN
                {
                mTHEN(); 

                }
                break;
            case 16 :
                // ExpressionLexer.g:1:89: ELSE
                {
                mELSE(); 

                }
                break;
            case 17 :
                // ExpressionLexer.g:1:94: DEGREES
                {
                mDEGREES(); 

                }
                break;
            case 18 :
                // ExpressionLexer.g:1:102: ID
                {
                mID(); 

                }
                break;

        }

    }


    protected DFA8 dfa8 = new DFA8(this);
    protected DFA17 dfa17 = new DFA17(this);
    static final String DFA8_eotS =
        "\1\uffff\1\4\5\uffff";
    static final String DFA8_eofS =
        "\7\uffff";
    static final String DFA8_minS =
        "\2\56\5\uffff";
    static final String DFA8_maxS =
        "\1\111\1\145\5\uffff";
    static final String DFA8_acceptS =
        "\2\uffff\1\3\1\5\1\1\1\2\1\4";
    static final String DFA8_specialS =
        "\7\uffff}>";
    static final String[] DFA8_transitionS = {
            "\1\2\1\uffff\12\1\17\uffff\1\3",
            "\1\5\1\uffff\12\1\13\uffff\1\6\37\uffff\1\6",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA8_eot = DFA.unpackEncodedString(DFA8_eotS);
    static final short[] DFA8_eof = DFA.unpackEncodedString(DFA8_eofS);
    static final char[] DFA8_min = DFA.unpackEncodedStringToUnsignedChars(DFA8_minS);
    static final char[] DFA8_max = DFA.unpackEncodedStringToUnsignedChars(DFA8_maxS);
    static final short[] DFA8_accept = DFA.unpackEncodedString(DFA8_acceptS);
    static final short[] DFA8_special = DFA.unpackEncodedString(DFA8_specialS);
    static final short[][] DFA8_transition;

    static {
        int numStates = DFA8_transitionS.length;
        DFA8_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA8_transition[i] = DFA.unpackEncodedString(DFA8_transitionS[i]);
        }
    }

    class DFA8 extends DFA {

        public DFA8(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 8;
            this.eot = DFA8_eot;
            this.eof = DFA8_eof;
            this.min = DFA8_min;
            this.max = DFA8_max;
            this.accept = DFA8_accept;
            this.special = DFA8_special;
            this.transition = DFA8_transition;
        }
        public String getDescription() {
            return "8:1: FLOAT : ( ( '0' .. '9' )+ | ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? | '.' ( '0' .. '9' )+ ( EXPONENT )? | ( '0' .. '9' )+ EXPONENT | 'Infinity' );";
        }
    }
    static final String DFA17_eotS =
        "\5\uffff\1\25\7\uffff\7\25\2\uffff\1\25\2\47\6\25\1\47\7\25\1\uffff"+
        "\1\65\1\66\2\65\2\66\7\25\2\uffff\3\76\3\77\1\25\2\uffff\2\25\1"+
        "\4";
    static final String DFA17_eofS =
        "\103\uffff";
    static final String DFA17_minS =
        "\1\50\4\uffff\1\106\7\uffff\1\141\1\101\1\146\1\150\1\110\1\154"+
        "\1\114\2\uffff\1\146\2\60\1\156\1\170\1\156\1\116\1\170\1\130\1"+
        "\60\2\145\1\105\2\163\1\123\1\151\1\uffff\6\60\2\156\1\116\2\145"+
        "\1\105\1\156\2\uffff\6\60\1\151\2\uffff\1\164\1\171\1\60";
    static final String DFA17_maxS =
        "\1\u00b3\4\uffff\1\156\7\uffff\2\151\1\146\2\150\2\154\2\uffff"+
        "\1\146\2\172\1\156\1\170\1\156\1\116\1\170\1\130\1\172\2\145\1\105"+
        "\2\163\1\123\1\151\1\uffff\6\172\2\156\1\116\2\145\1\105\1\156\2"+
        "\uffff\6\172\1\151\2\uffff\1\164\1\171\1\172";
    static final String DFA17_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\uffff\1\5\1\6\1\7\1\10\1\11\1\12\1\13"+
        "\7\uffff\1\21\1\22\21\uffff\1\16\15\uffff\1\14\1\15\7\uffff\1\17"+
        "\1\20\3\uffff";
    static final String DFA17_specialS =
        "\103\uffff}>";
    static final String[] DFA17_transitionS = {
            "\1\1\1\2\1\10\1\6\1\uffff\1\7\1\4\1\11\12\4\1\3\6\uffff\4\25"+
            "\1\23\3\25\1\5\3\25\1\16\6\25\1\21\6\25\3\uffff\1\12\2\uffff"+
            "\4\25\1\22\3\25\1\17\3\25\1\15\6\25\1\20\6\25\65\uffff\1\24"+
            "\1\uffff\1\13\1\14",
            "",
            "",
            "",
            "",
            "\1\30\37\uffff\1\27\7\uffff\1\26",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\32\7\uffff\1\31",
            "\1\36\7\uffff\1\34\27\uffff\1\35\7\uffff\1\33",
            "\1\37",
            "\1\40",
            "\1\42\37\uffff\1\41",
            "\1\43",
            "\1\45\37\uffff\1\44",
            "",
            "",
            "\1\46",
            "\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
            "\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
            "\1\50",
            "\1\51",
            "\1\52",
            "\1\53",
            "\1\54",
            "\1\55",
            "\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
            "\1\56",
            "\1\57",
            "\1\60",
            "\1\61",
            "\1\62",
            "\1\63",
            "\1\64",
            "",
            "\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
            "\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
            "\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
            "\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
            "\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
            "\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
            "\1\67",
            "\1\70",
            "\1\71",
            "\1\72",
            "\1\73",
            "\1\74",
            "\1\75",
            "",
            "",
            "\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
            "\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
            "\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
            "\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
            "\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
            "\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
            "\1\100",
            "",
            "",
            "\1\101",
            "\1\102",
            "\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25"
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
            return "1:1: Tokens : ( L_PAREN | R_PAREN | BECOMES | FLOAT | PLUS | MINUS | TIMES | DIV | POW | SQR | CUB | MIN | MAX | IF | THEN | ELSE | DEGREES | ID );";
        }
    }
 

}
