// $ANTLR 3.2 Sep 23, 2009 14:05:07 CommentAndWhitespaceLexer.g

 package model.mlspace.reader.antlr;
 
 import java.util.logging.Level;
 import org.jamesii.core.util.logging.ApplicationLogger;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class MLSpaceCompositeLexer_CommentAndWhitespaceLexer extends Lexer {
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

    public MLSpaceCompositeLexer_CommentAndWhitespaceLexer() {;} 
    public MLSpaceCompositeLexer_CommentAndWhitespaceLexer(CharStream input, MLSpaceCompositeLexer gMLSpaceCompositeLexer) {
        this(input, new RecognizerSharedState(), gMLSpaceCompositeLexer);
    }
    public MLSpaceCompositeLexer_CommentAndWhitespaceLexer(CharStream input, RecognizerSharedState state, MLSpaceCompositeLexer gMLSpaceCompositeLexer) {
        super(input,state);

        this.gMLSpaceCompositeLexer = gMLSpaceCompositeLexer;
        gParent = gMLSpaceCompositeLexer;
    }
    public String getGrammarFileName() { return "CommentAndWhitespaceLexer.g"; }

    // $ANTLR start "COMMENT"
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // CommentAndWhitespaceLexer.g:3:9: ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' | '/*' ( options {greedy=false; } : . )* '*/' )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0=='/') ) {
                int LA4_1 = input.LA(2);

                if ( (LA4_1=='/') ) {
                    alt4=1;
                }
                else if ( (LA4_1=='*') ) {
                    alt4=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // CommentAndWhitespaceLexer.g:4:5: '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n'
                    {
                    match("//"); 

                    // CommentAndWhitespaceLexer.g:5:5: (~ ( '\\n' | '\\r' ) )*
                    loop1:
                    do {
                        int alt1=2;
                        int LA1_0 = input.LA(1);

                        if ( ((LA1_0>='\u0000' && LA1_0<='\t')||(LA1_0>='\u000B' && LA1_0<='\f')||(LA1_0>='\u000E' && LA1_0<='\uFFFF')) ) {
                            alt1=1;
                        }


                        switch (alt1) {
                    	case 1 :
                    	    // CommentAndWhitespaceLexer.g:5:5: ~ ( '\\n' | '\\r' )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFF') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop1;
                        }
                    } while (true);

                    // CommentAndWhitespaceLexer.g:9:5: ( '\\r' )?
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( (LA2_0=='\r') ) {
                        alt2=1;
                    }
                    switch (alt2) {
                        case 1 :
                            // CommentAndWhitespaceLexer.g:9:5: '\\r'
                            {
                            match('\r'); 

                            }
                            break;

                    }

                    match('\n'); 

                                    _channel = HIDDEN;
                                   

                    }
                    break;
                case 2 :
                    // CommentAndWhitespaceLexer.g:13:7: '/*' ( options {greedy=false; } : . )* '*/'
                    {
                    match("/*"); 

                    // CommentAndWhitespaceLexer.g:13:12: ( options {greedy=false; } : . )*
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( (LA3_0=='*') ) {
                            int LA3_1 = input.LA(2);

                            if ( (LA3_1=='/') ) {
                                alt3=2;
                            }
                            else if ( ((LA3_1>='\u0000' && LA3_1<='.')||(LA3_1>='0' && LA3_1<='\uFFFF')) ) {
                                alt3=1;
                            }


                        }
                        else if ( ((LA3_0>='\u0000' && LA3_0<=')')||(LA3_0>='+' && LA3_0<='\uFFFF')) ) {
                            alt3=1;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // CommentAndWhitespaceLexer.g:13:38: .
                    	    {
                    	    matchAny(); 

                    	    }
                    	    break;

                    	default :
                    	    break loop3;
                        }
                    } while (true);

                    match("*/"); 


                                                                   _channel = HIDDEN;
                                                                  

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMENT"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // CommentAndWhitespaceLexer.g:19:4: ( ( ' ' | '\\t' | '\\r' | '\\n' ) )
            // CommentAndWhitespaceLexer.g:20:5: ( ' ' | '\\t' | '\\r' | '\\n' )
            {
            if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


                  _channel = HIDDEN;
                 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WS"

    public void mTokens() throws RecognitionException {
        // CommentAndWhitespaceLexer.g:1:8: ( COMMENT | WS )
        int alt5=2;
        int LA5_0 = input.LA(1);

        if ( (LA5_0=='/') ) {
            alt5=1;
        }
        else if ( ((LA5_0>='\t' && LA5_0<='\n')||LA5_0=='\r'||LA5_0==' ') ) {
            alt5=2;
        }
        else {
            NoViableAltException nvae =
                new NoViableAltException("", 5, 0, input);

            throw nvae;
        }
        switch (alt5) {
            case 1 :
                // CommentAndWhitespaceLexer.g:1:10: COMMENT
                {
                mCOMMENT(); 

                }
                break;
            case 2 :
                // CommentAndWhitespaceLexer.g:1:18: WS
                {
                mWS(); 

                }
                break;

        }

    }


 

}
