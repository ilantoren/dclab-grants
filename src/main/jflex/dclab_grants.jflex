package  com.dclab;

import com.dclab.GrantToken;
import com.dclab.TC;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.apache.commons.lang3.StringUtils;
%%
%{

  private  StringBuilder sb = new StringBuilder();
  private  Pattern  patx  = Pattern.compile("\\s\\S{1,3}$");
 
  
%}


%class    GrantsLexer
%function nextToken
%type	  GrantToken
%unicode 
%line  

%column
%eofval{
  { return getToken(TC.EOF); }
%eofval}

%{
    
   // user code
    StringBuilder  textBuf = new StringBuilder();
    GrantToken getToken( TC sym)              {  return new GrantToken( sym , yytext(), yyline, yycolumn, yycolumn + yylength() -1); }
    GrantToken getToken( TC sym, String text) {  return new GrantToken( sym , text, yyline, yycolumn, yycolumn + yylength() -1);     }
    GrantToken handleGID() { 
                         if ( StringUtils.containsAny( yytext(), "01234567890")  && isGreedyGid( yytext()) ){
                            int x = yytext().indexOf(' ', yytext().length() - 3);
                            int len = yytext().length() - x;
                            if ( len > yytext().length()) len = 0;
                              yypushback(len);
                              return getToken( TC.GID );  
   
                         }
                         else if( yylength()  > 5 && StringUtils.containsAny( yytext(), "01234567890") ){
                           yybegin( GRANT );
                           return getToken( TC.GID );
                         }                       
                             return getToken( TC.DISCARD );
                         }        
                         
    String     reset()                        {  String s = sb.toString();  sb = new StringBuilder(); return s; }
    boolean     isGreedyGid( String text)     {  return patx.matcher( text ).find();    } 
    GrantToken  flushBuf()  { if ( textBuf.length() > 0  ) 
                                { String s = textBuf.toString(); textBuf = new StringBuilder();  return getToken( TC.SKIP, s); }
                            else { return getToken( TC.SKIP ); }
                         }
%}

WhiteSpace = [ \t]
LineTerminator = \r|\n|\r\n|\u000A
SPACE        = ' '|\u00A0|" "
GRANT_START  = {GRANT_ST_WD}{FORMS}
FORMS        = \s|ed|s|ing|s
GRANT_ST_WD     = [R|r]eference|support|(F|f)und|Support|sponsor|project|[A|a]ward|"acknowledge the"|[C|c]ontract|agreement|scholarship|grateful(ly)?|(g|G)rant|also|[R|r]ef|"funded by"|"supported by"
GRANT        = grant(s){0,1}|"grant no."|Grant(s){0,1}|"Training Grant"|Contract|"Grant no."|No\.|no\.|([N|n]umber[\.]{0,1})
AND          = ", and"|:|;|-|" and "|\/|", "
LPAREN       = \p{Ps}
RPAREN       = \p{Pe}
PAREN        = {LPAREN}|{RPAREN}
CAP          = [A-Z]
CONNECT      = a|is|in|part|of|the|provided|acknowledge|by|from|a|an|to|for|no\.|No(s)?\.|number|research|through|work|partially|contract(s)?|is|was|{SPACE}|under|received|framework|:|;|numbers|under|(F|f)ellowship(s?)
GRANT_ID     = ({CAP}|[0-9]|#)({CAP}|[0-9]|\/|\+|\\|-|_|{SPACE}|\.|#)+ [:jletterdigit:]{0,1}
INITIAL      = ({CAP}\.(-)?){2,6}
STOP         = \.|\.\n
COMMA        = ,
ORG          = ([:jletterdigit:]|:|\"|-|\/|\&|\*|{AB})+
US           = "U.S."
AB           = \p{Upper}\.\s
ORG_S        = \u201D|\"|[A-Z]|\'|\u201C|\p{Pi}|\p{Pf}   
PAUSE        = " to " | "and the" | "under the" | " by " | " is " | ", and the" | program

%state DISCARD, GS, GRANT, ORG 
 
%%

                                                                                                                                                        
<YYINITIAL,DISCARD> {

   {GRANT_START}      { yybegin( GS ); return getToken( TC.START);}
    \p{Ps}            { yybegin( GRANT ); yypushback(1); return getToken(TC.PAREN); }
    \w                { textBuf.append( yytext()) ; return getToken( TC.DISCARD ); }
    {WhiteSpace}      { return getToken( TC.DISCARD ); }
    {LineTerminator}  { return getToken( TC.FLUSH ); }
    {PAREN}           { yybegin(GRANT); return getToken ( TC.PAREN ); }
    {INITIAL}         { yybegin(GS); return getToken( TC.AUTH ); }
    {GRANT}           { yybegin(GS);   return getToken( TC.GRANT );  }
    "NIH"             { yybegin(ORG); yypushback(3); }
    .                 { return getToken( TC.DISCARD ); }
}

<GS> {                                                                                                                                                                  
 {CAP}                { yybegin( ORG );  yypushback(1); }
 {ORG_S}              { yybegin( ORG );  yypushback(1);  }
 {WhiteSpace}         { return getToken( TC.SKIP  ); }
  {AND}                 { return getToken( TC.AND   ); }
 {GRANT_ID}           { if ( StringUtils.containsNone( yytext(), "0123456789") ) { yypushback( yylength()); yybegin( ORG ); } else {return handleGID();} }
 {CONNECT}            { return getToken( TC.SKIP  ); }
 {GRANT }             { yybegin( GRANT);  return getToken( TC.GRANT ); }
 {COMMA}              { return getToken( TC.AND ); }
 {STOP}               {  yybegin( DISCARD );  return getToken( TC.STOP ); }
 {LineTerminator}     {  yybegin( DISCARD );  return getToken( TC.DISCARD); }
  .                   {  yybegin( DISCARD  ); yypushback(1);    }
}


<ORG> {
 \p{Pe}              { yybegin(GRANT); yypushback(1); String s = reset(); return getToken(TC.ORG, s);  }
 \p{Ps}               { yybegin(GRANT); yypushback(1);  String s = reset(); return getToken(TC.ORG, s); }
  {PAUSE}             { yybegin( GS   ); yypushback( yytext().length() );  String s = reset(); return getToken( TC.ORG, s); }
  {GRANT}             { yybegin( GS   ); yypushback( yytext().length() );  String s = reset(); return getToken( TC.ORG, s); }
  {ORG}               { sb.append( yytext() ); }
  {ORG_S}             { sb.append(yytext()); }
  {SPACE}             { sb.append( yytext());  }
  {AND}               { sb.append( yytext() ); }
  \w+                  { sb.append( yytext() ); }
  {US}                { sb.append( yytext() ); }
   {LineTerminator}    { yybegin( DISCARD );  yypushback(1);    String s = reset(); return getToken( TC.ORG, s);  }
  {STOP}              { yybegin( DISCARD   ); yypushback(1);  String s = reset(); return getToken( TC.ORG, s); }
     .                { yybegin( GRANT);  yypushback(1); }
}


<GRANT> {
  {CONNECT}           { return getToken( TC.SKIP ); }
  {INITIAL}           { yybegin( GRANT );  return getToken(  TC.AUTH );   }
  {GRANT_ID}          {  if ( StringUtils.containsNone( yytext(), "0123456789") ) { yypushback( yylength()); yybegin( ORG ); } else {return handleGID();} }
  {CAP}|\"|“          { yybegin( ORG ); yypushback(1);                  }
  {GRANT_START}       { yybegin( GS);      return getToken(TC.START);      }
  {PAREN}             {                    return getToken( TC.PAREN);    } 
  {GRANT}             {                   return getToken( TC.GRANT );  }
   and                { yybegin( GRANT );  return getToken( TC.AND );    }                                                                                             
   ,                  {                    return getToken( TC.AND); } 
  {AND}               { yybegin( GRANT );  return getToken(  TC.AND );   }
  {WhiteSpace}        { yybegin( GRANT );       return getToken( TC.SPACE);  }
  {LineTerminator}    { yybegin( DISCARD );  return getToken( TC.DISCARD); }
   [a-z]\w+               { return getToken( TC.SKIP );  }
    BW                { yybegin( DISCARD );  return getToken( TC.DISCARD); }
  {STOP}              { yybegin( DISCARD );yypushback(1); return getToken( TC.STOP );  }
    .                 { yybegin(DISCARD);  yypushback(1); }
}

