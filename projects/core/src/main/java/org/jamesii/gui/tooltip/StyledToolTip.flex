/* JFlex specification file for the StyledToolTip Syntax */

package james.gui2.swing.tooltip;

import james.gui2.swing.tooltip.StyledToolTipSyntaxToken.Type;
import james.gui2.swing.syntaxeditor.ILexer;
import james.gui2.swing.syntaxeditor.ILexerToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.io.Reader;
import java.util.List;

/**
* This class is a lexer to tokenize the tooltip string into formating tokens and text tokens
*
* @author Stefan Rybacki (using JFlex)
*/
@SuppressWarnings("unused")
%%
%class StyledToolTipLexer
%implements ILexer

%public

%unicode

%char

%type StyledToolTipSyntaxToken


/**
* Interface implementation methods
*/
%{
  private List<ILexerToken> problemTokens=new ArrayList<ILexerToken>();
  private List<ILexerToken> syntaxTokens=new ArrayList<ILexerToken>();
  private StringBuffer value=new StringBuffer();
  
  /**
  * Creates the lexer
  */
  public StyledToolTipLexer() {
  }
  
  @Override
  public List<ILexerToken> getProblemTokens() {
    return problemTokens;
  }

  @Override
  public List<ILexerToken> getSyntaxTokens() {
    return syntaxTokens;
  }

  @Override
  public void parse(Reader input) {
	stop=false;
    yyreset(input);
    try {
      generateTokens();
    } catch (IOException e) {
      e.printStackTrace();
    }   
  }

  @Override
  public void stopParsing() {
    stop=true;
  }

  /**
  * helper field to get the original text or color value starting positions in case of state changes
  */
  private int textstart;
  /**
   * field that indicates whether the parsing should be stop at the next possible point
   */
  protected boolean stop;

  /**
  * Helper method that generates syntax and problem tokens that can be used for further parsing as well
  * as syntax highlighting
  */
  private void generateTokens() throws IOException {
    syntaxTokens.clear();
    problemTokens.clear();
    
    StyledToolTipSyntaxToken t;
    while ((t=yylex())!=null && !stop) {
      syntaxTokens.add(t);
    }
    Collections.sort(syntaxTokens);
    Collections.sort(problemTokens);    
  }	
  
  /**
  * Helper function that returns a StyledTooltipSyntaxToken with the specified type
  * @param type
  *             the token type
  * @return token of specified type with the calculated start position and length value
  */
  private StyledToolTipSyntaxToken symbol(Type type) {
    return new StyledToolTipSyntaxToken(type, yychar, yylength());
  }
%}



/* tags */
LineBreak = \r\n|\n|\r|\n\r|"<br/>"(\n|\n\r|\r\n)?
BoldOn = "<b>"
BoldOff = "</b>"
ItalicOn = "<i>"
ItalicOff = "</i>"
FontSizeStart = "<size"[ \t\f]*
FontSizeEnd ="/>"(\n|\n\r|\r\n)?
FontSize = {FontSizeStart} [^/]* {FontSizeEnd}
FontStart = "<font"[ \t\f]*
FontEnd ="/>"(\n|\n\r|\r\n)?
Font = {FontStart} [^/]* {FontEnd}
ColorStart = "<color"[ \t\f]*
ColorEnd = "/>"(\n|\n\r|\r\n)?
Color = {ColorStart} [^/]* {ColorEnd}
Line = "<hr/>"(\n|\n\r|\r\n)?
BgColorStart = "<bgcolor"[ \t\f]*
BgColorEnd = "/>"(\n|\n\r|\r\n)?
BgColor ={BgColorStart} [^/]* {BgColorEnd}
WidthStart = "<width"[ \t\f]*
WidthEnd = "/>"(\n|\n\r|\r\n)?
Width = {WidthStart} [^/]* {WidthEnd}
HeightStart = "<height"[ \t\f]*
HeightEnd = "/>"(\n|\n\r|\r\n)?
Height = {HeightStart} [^/]* {HeightEnd}
UnderLineOn = "<u>"
UnderLineOff ="</u>"
CrossOutOn = "<c>"
CrossOutOff = "</c>"
LessThan = "<</>"
PreOn = "<pre>"
PreOff= "</pre>"
PushState = "<push/>"
PopState = "<pop/>"

/* add complete tags here */
Tags = {LineBreak} | {BoldOn} | {BoldOff} | {ItalicOn} | {ItalicOff} | {Color} | {Line} | {BgColor} | 
       {UnderLineOn} | {UnderLineOff} | {Font} | {FontSize} | {CrossOutOn} | {CrossOutOff} | {LessThan} |
       {PreOn} | {PreOff} | {PushState} | {PopState}

/* states we can be in */
%state TEXT
%state COLOR
%state BGCOLOR
%state WIDTH
%state HEIGHT
%state FONT
%state FONTSIZE
%state PRETEXT

%%

/* COLOR state is used to determine the color value 
	- but the color value is only returned if there is an ending color tag ColorEnd otherwise a text token is returned
*/
<COLOR> {
{ColorStart} { textstart=yychar+yylength(); return symbol(Type.COLORSTART); }
{ColorEnd} {yybegin(YYINITIAL); return new StyledToolTipSyntaxToken(Type.COLORVALUE, textstart, yychar-textstart, value.substring(0));}
<<EOF>> {yybegin(YYINITIAL); return new StyledToolTipSyntaxToken(Type.TEXT, textstart, yychar-textstart);}

.|[\n\r] { value.append(yytext()); }
}

/* FONT state is used to determine the font value 
	- but the font value is only returned if there is an ending font tag FontEnd otherwise a text token is returned
*/
<FONT> {
{FontStart} { textstart=yychar+yylength(); return symbol(Type.FONTSTART); }
{FontEnd} {yybegin(YYINITIAL); return new StyledToolTipSyntaxToken(Type.FONTVALUE, textstart, yychar-textstart, value.substring(0));}
<<EOF>> {yybegin(YYINITIAL); return new StyledToolTipSyntaxToken(Type.TEXT, textstart, yychar-textstart);}

.|[\n\r] { value.append(yytext()); }
}

/* FONTSIZE state is used to determine the font size  
	- but the font size is only returned if there is an ending font size tag FontSizeEnd otherwise a text token is returned
*/
<FONTSIZE> {
{FontSizeStart} { textstart=yychar+yylength(); return symbol(Type.FONTSIZESTART); }
{FontSizeEnd} {yybegin(YYINITIAL); return new StyledToolTipSyntaxToken(Type.FONTSIZEVALUE, textstart, yychar-textstart, value.substring(0));}
<<EOF>> {yybegin(YYINITIAL); return new StyledToolTipSyntaxToken(Type.TEXT, textstart, yychar-textstart);}

.|[\n\r] { value.append(yytext()); }
}


/* BGCOLOR state is used to determine the color value 
	- but the color value is only returned if there is an ending color tag BgColorEnd otherwise a text token is returned
*/
<BGCOLOR> {
{BgColorStart} { textstart=yychar+yylength(); return symbol(Type.BGCOLORSTART); }
{BgColorEnd} {yybegin(YYINITIAL); return new StyledToolTipSyntaxToken(Type.BGCOLORVALUE, textstart, yychar-textstart, value.substring(0));}
<<EOF>> {yybegin(YYINITIAL); return new StyledToolTipSyntaxToken(Type.TEXT, textstart, yychar-textstart);}

.|[\n\r] { value.append(yytext()); }
}

/* WIDTH state is used to determine the width for tooltip */
<WIDTH> {
{WidthStart} { textstart=yychar+yylength(); return symbol(Type.WIDTHSTART); }
{WidthEnd} {yybegin(YYINITIAL); return new StyledToolTipSyntaxToken(Type.WIDTHVALUE, textstart, yychar-textstart, value.substring(0));}
<<EOF>> {yybegin(YYINITIAL); return new StyledToolTipSyntaxToken(Type.TEXT, textstart, yychar-textstart);}

.|[\n\r] { value.append(yytext()); }
}

/* WIDTH state is used to determine the width for tooltip */
<HEIGHT> {
{HeightStart} { textstart=yychar+yylength(); return symbol(Type.HEIGHTSTART); }
{HeightEnd} {yybegin(YYINITIAL); return new StyledToolTipSyntaxToken(Type.HEIGHTVALUE, textstart, yychar-textstart, value.substring(0));}
<<EOF>> {yybegin(YYINITIAL); return new StyledToolTipSyntaxToken(Type.TEXT, textstart, yychar-textstart);}

.|[\n\r] { value.append(yytext()); }
}



/* 
	Text state is to collect all text characters that are not part of any tag and to build one token out of them
*/
<TEXT> {
{Tags} {yypushback(yylength()); yybegin(YYINITIAL); return new StyledToolTipSyntaxToken(Type.TEXT, textstart, yychar-textstart);}
<<EOF>> {yybegin(YYINITIAL); return new StyledToolTipSyntaxToken(Type.TEXT, textstart, yychar-textstart);}

.|[\n\r] {/* ignore */}
}

/* 
	Text state is to collect all text characters that are not part of any tag and to build one token out of them
*/
<PRETEXT> {
{PreOff} {yypushback(yylength()); yybegin(YYINITIAL); return new StyledToolTipSyntaxToken(Type.TEXT, textstart, yychar-textstart);}
<<EOF>> {yybegin(YYINITIAL); return new StyledToolTipSyntaxToken(Type.TEXT, textstart, yychar-textstart);}

.|[\n\r] {/* ignore */}
}


<YYINITIAL> {
/* identify tags */
{LessThan} { return symbol(Type.LESSTHAN); }
{LineBreak} { return symbol(Type.LINEBREAK); }
{BoldOn} { return symbol(Type.BOLDON); }
{BoldOff} { return symbol(Type.BOLDOFF); }
{CrossOutOn} { return symbol(Type.CROSSOUTON); }
{CrossOutOff} { return symbol(Type.CROSSOUTOFF); }
{ItalicOn} { return symbol(Type.ITALICON); }
{ItalicOff} { return symbol(Type.ITALICOFF); }
{BgColor} { yypushback(yylength()); yybegin(BGCOLOR); value.setLength(0); }
{Width} { yypushback(yylength()); yybegin(WIDTH); value.setLength(0); }
{Height} { yypushback(yylength()); yybegin(HEIGHT); value.setLength(0); }
{Color} { yypushback(yylength()); yybegin(COLOR); value.setLength(0); }
{FontSize} { yypushback(yylength()); yybegin(FONTSIZE); value.setLength(0); }
{Line} { return symbol(Type.LINE); }
{UnderLineOn} { return symbol(Type.UNDERLINEON); }
{UnderLineOff} { return symbol(Type.UNDERLINEOFF); }
{Font} { yypushback(yylength()); yybegin(FONT); value.setLength(0); }
{PreOn} {textstart=yychar+yylength();  yybegin(PRETEXT); return symbol(Type.PREON); }
{PreOff} { return symbol(Type.PREOFF); }
{PushState} { return symbol(Type.PUSHSTATE); }
{PopState} { return symbol(Type.POPSTATE); }

/* if nothing else was matched it must be text */
. { yybegin(TEXT); textstart=yychar;}


}

