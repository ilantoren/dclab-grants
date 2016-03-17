package com.dclab;



public class GrantToken   {

   

  protected TC symbol;	// current token
  protected String lexeme;	// lexeme
  protected Integer lineNum;
  private Integer m_charBegin;
  private Integer m_charEnd;



  public GrantToken () { } 

 public GrantToken (TC symbol) {
    this (symbol, null, null, null, null);
  }

 public GrantToken (TC sym, String text, Integer line, Integer charBegin, Integer charEnd) {
        symbol = sym;
        lexeme = text;
        lineNum = line;
        m_charBegin = charBegin;
        m_charEnd = charEnd;
    }

  public TC symbol () { return symbol; }

  public String lexeme () { return lexeme; }
  
  public Integer lineNum() { return lineNum; }
  
  public String getFirstLexeme() {
      String fulLexeme = lexeme();
      return fulLexeme.trim();
      
  }

  public String toString () {
      
    switch (symbol) {
      case ORG :     return "<span class=\"org\">" + lexeme + "</span>";
      case DISCARD : return  lexeme;
      case SKIP :    return  lexeme ;
      case CONNECT :  return  lexeme;
      case GID :     return "<span class=\"gr\">" + lexeme + "</span>";
      case AUTH :    return "<span class=\"au\">" + lexeme  + "</span>";
      case GRANT :   return  lexeme;
      case EOF :     return "FINE";
      case PAREN:    return  lexeme;
      case FLUSH:    return  lexeme;
      case STOP :    return  lexeme;
      case AND :     return  lexeme;
      case START :   return "<span class = \"st\">" + lexeme + "</span>";  
      
      default :      
	ErrorMessage . print (0, "Unrecognized token" +lexeme);
        return null;
    }
  }

  
  
}
