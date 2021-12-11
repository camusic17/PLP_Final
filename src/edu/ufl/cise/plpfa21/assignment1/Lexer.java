package edu.ufl.cise.plpfa21.assignment1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import edu.ufl.cise.plpfa21.assignment1.PLPTokenKinds.Kind;


public class Lexer implements IPLPLexer {

	private ArrayList<IPLPToken> tokens;
	private char[] chars; //holds characters with 0 at the end
	private int nextTokenPos = 0;
	static final char EOFchar = 0;
	
	private HashMap<String, Kind> keywords = new HashMap<String, Kind>();
	
	//enums for dfa states
	private enum State{START, HAVE_EQUAL, HAVE_AND, HAVE_OR, HAVE_NOT, INTLITERAL, IDENT_PART, HAVE_SLASH, HAVE_MCOMMENT, HAVE_SCOMMENT, HAVE_STRINGLIT, ESCAPE_SEQ}
	
	
	public Lexer(String inputString) 
	{
		//creating char array from inputString
		int numChars = inputString.length();
		this.chars = Arrays.copyOf(inputString.toCharArray(), numChars+1);
		
		//indexing setup
		int pos = 0;
		int startPos = pos;
		int line = 1;
		int posInLine = 1;
		int startPosInLine = posInLine;
		
		State state = State.START;
		
		//input char array terminated with EOFchar for convenience
		chars[numChars] = EOFchar;
		tokens = new ArrayList<>();
		
		String digits = "";
		String inProgIdent = "";
		
		//add keywords to HashMap for ident check
		keywords.put("FUN",Kind.KW_FUN);
		keywords.put("DO",Kind.KW_DO);
		keywords.put("END",Kind.KW_END);
		keywords.put("LET",Kind.KW_LET);
		keywords.put("SWITCH",Kind.KW_SWITCH);
		keywords.put("CASE",Kind.KW_CASE);
		keywords.put("DEFAULT",Kind.KW_DEFAULT);
		keywords.put("IF",Kind.KW_IF);
		keywords.put("ELSE",Kind.KW_ELSE);
		keywords.put("WHILE",Kind.KW_WHILE);
		keywords.put("RETURN",Kind.KW_RETURN);
		keywords.put("LIST",Kind.KW_LIST);
		keywords.put("VAR",Kind.KW_VAR);
		keywords.put("VAL",Kind.KW_VAL);
		keywords.put("NIL",Kind.KW_NIL);
		keywords.put("TRUE",Kind.KW_TRUE);
		keywords.put("FALSE",Kind.KW_FALSE);
		keywords.put("INT",Kind.KW_INT);
		keywords.put("STRING",Kind.KW_STRING);
		keywords.put("FLOAT",Kind.KW_FLOAT);
		keywords.put("BOOLEAN",Kind.KW_BOOLEAN);
		
		//loop that scans through every character and builds the token array
		while(pos<chars.length)
		{
			//System.out.println(pos);
			//System.out.println("length: " + chars.length);
			char ch = chars[pos];
			//System.out.println("Char: " + ch);
			switch(state)
			{
				case START ->
				{
					startPos = pos;
					//startPosInLine = posInLine;
					switch(ch)
					{
						//need to handle other kinds of whitespace
						case ' ', '\t' ->
						{
							pos++;
							posInLine++;
						}
						case '\n','\r' ->
						{
							pos++;
							posInLine = 1;
							line++;
						}
						case ',' ->
						{
							//add token
							tokens.add(new Token(Kind.COMMA,startPos,1,line,startPosInLine, inputString));
							pos++;
							posInLine++;
						}
						case ';' ->
						{
							tokens.add(new Token(Kind.SEMI,startPos,1,line,startPosInLine, inputString));
							pos++;
							posInLine++;
						}
						case ':' ->
						{
							tokens.add(new Token(Kind.COLON,startPos,1,line,startPosInLine, inputString));
							pos++;
							posInLine++;
						}
						case '(' ->
						{
							tokens.add(new Token(Kind.LPAREN,startPos,1,line,posInLine, inputString));
							pos++;
							posInLine++;
						}
						case ')' ->
						{
							tokens.add(new Token(Kind.RPAREN,startPos,1,line,startPosInLine, inputString));
							pos++;
							posInLine++;
						}
						case '[' ->
						{
							tokens.add(new Token(Kind.LSQUARE,startPos,1,line,startPosInLine, inputString));
							pos++;
							posInLine++;
						}
						case ']' ->
						{
							tokens.add(new Token(Kind.RSQUARE,startPos,1,line,startPosInLine, inputString));
							pos++;
							posInLine++;
						}
						case '<' ->
						{
							tokens.add(new Token(Kind.LT,startPos,1,line,startPosInLine, inputString));
							pos++;
							posInLine++;
						}
						case '>' ->
						{
							tokens.add(new Token(Kind.GT,startPos,1,line,startPosInLine, inputString));
							pos++;
							posInLine++;
						}					
						case '+' ->
						{
							tokens.add(new Token(Kind.PLUS,startPos,1,line,posInLine, inputString));
							pos++;
							posInLine++;
						}
						case '-' ->
						{
							tokens.add(new Token(Kind.MINUS, startPos, 1, line, startPosInLine, inputString));
							pos++;
							posInLine++;
						}
						case '*' ->
						{
							tokens.add(new Token(Kind.TIMES,startPos,1,line,startPosInLine, inputString));
							pos++;
							posInLine++;
						}
						case '&' ->
						{
							//change state for double symbols
							state = State.HAVE_AND;
							pos++;
							posInLine++;
						}
						case '|' ->
						{
							state = State.HAVE_OR;
							pos++;
							posInLine++;
						}
						
						case '!' ->
						{
							state = State.HAVE_NOT;							
							pos++;
							posInLine++;
						}
						case '/' ->
						{
							state = State.HAVE_SLASH;
							pos++;
							posInLine++;
						}
						case '=' ->
						{							
							startPosInLine = posInLine;
							pos++;
							posInLine++;
							state = State.HAVE_EQUAL;
						}
						case '\"', '\'' ->
						{							
							startPosInLine = posInLine;
							pos++;
							posInLine++;
							state = State.HAVE_STRINGLIT;
						}
						case '0','1','2','3','4','5','6','7','8','9' ->
						{
							digits += ch;
							//System.out.println("got here 0");
							startPosInLine = posInLine;
							pos++;
							posInLine++;
							state = State.INTLITERAL;
						}
						default -> 
						{
							startPosInLine = posInLine;
							if(Character.isJavaIdentifierStart(ch))
							{
								inProgIdent += ch;
								pos++;
								posInLine++;
								state = State.IDENT_PART;							
							}
							else	
							{
								if(ch != EOFchar)
								{
									//handle error
									tokens.add(new Token(Kind.ERROR,startPos,pos - startPos,line,startPosInLine, inputString));
									state = State.START;
								}
								pos++;
								posInLine++;
							}
						}
					}
				}
				case HAVE_AND ->
				{
					if(ch == '&')
					{
						tokens.add(new Token(Kind.AND,startPos,pos - startPos,line,startPosInLine, inputString));
						pos++;
						posInLine++;
						state = State.START;
					}
					else
					{
						//throw error
						tokens.add(new Token(Kind.ERROR,startPos,pos - startPos,line,startPosInLine, inputString));
						pos++;
						posInLine++;
						state = State.START;
					}				
				}
				case HAVE_OR ->
				{
					if(ch == '|')
					{
						tokens.add(new Token(Kind.OR,startPos,pos - startPos,line,startPosInLine, inputString));
						pos++;
						posInLine++;
						state = State.START;
					}
					else
					{
						tokens.add(new Token(Kind.ERROR,startPos,pos - startPos,line,startPosInLine, inputString));
						pos++;
						posInLine++;
						state = State.START;
					}
				}
				case HAVE_NOT ->
				{
					if(ch == '=')
					{
						tokens.add(new Token(Kind.NOT_EQUALS,startPos,pos - startPos,line,startPosInLine, inputString));
						pos++;
						posInLine++;
						state = State.START;
					}
					else
					{
						tokens.add(new Token(Kind.BANG, startPos,pos - startPos,line,startPosInLine, inputString));
						state = State.START;
					}
				}
				case HAVE_SLASH ->
				{				
					if(ch == '/')	//single line comment
					{
						pos++;		
						posInLine++;
						state = State.HAVE_SCOMMENT;

					}
					else if(ch == '*')	//multiline comment
					{
						pos++;		
						posInLine++;
						state = State.HAVE_MCOMMENT;					
					}
					else	//division
					{
						tokens.add(new Token(Kind.DIV,startPos,1,line,startPosInLine, inputString));
						state = State.START;
					}
				}
				case HAVE_EQUAL ->
				{
					if(ch == '=')
					{
						tokens.add(new Token(Kind.EQUALS,startPos,2,line,startPosInLine, inputString));
						pos++;
						posInLine++;
						state = State.START;
					}
					else
					{
						tokens.add(new Token(Kind.ASSIGN,startPos,1,line,startPosInLine, inputString));					
						state = State.START;
					}
				}
				case HAVE_STRINGLIT -> {
					
					if(ch == '"') {
//						System.out.println("INPUT STRING: " + inputString);
//						Token t = new Token(Kind.STRING_LITERAL, startPos, 1 + pos - startPos, line, startPosInLine, inputString);
//						t.getText();
//						System.out.println("TOKEN STRING: " + t.getText());
						
						
						tokens.add(new Token(Kind.STRING_LITERAL, startPos, 1 + pos - startPos, line, startPosInLine, inputString));
						pos++;
						posInLine++;
						state = State.START;
					}
					else if(ch =='\\') {
						pos++;
						posInLine++;
						state = State.ESCAPE_SEQ;
					}
					else {
						pos++;
						posInLine++;
					}
					
				}	
				case INTLITERAL ->
				{					
					if(Character.isDigit(ch))
					{
						digits += ch;
						pos++;
						posInLine++;
					}
					else
					{						
						try
						{
							Integer.parseInt(digits);
							tokens.add(new Token(Kind.INT_LITERAL, startPos,pos - startPos,line,startPosInLine, inputString));
						}
						catch(NumberFormatException e)
						{
							tokens.add(new Token(Kind.ERROR,startPos,pos - startPos,line,startPosInLine, inputString));
						}
						
						state = State.START;
						digits = "";
					}
				}
				case IDENT_PART ->
				{
					if(Character.isJavaIdentifierPart(ch) && ch != EOFchar)
					{
						inProgIdent += ch;
						pos++;
						posInLine++;
					}
					else
					{
						if(keywords.containsKey(inProgIdent))
						{
							tokens.add(new Token(keywords.get(inProgIdent),startPos, pos- startPos, line, startPosInLine , inputString));
						}
						else
						{
							tokens.add(new Token(Kind.IDENTIFIER,startPos, pos- startPos, line, startPosInLine, inputString ));
						}
						state = State.START;
						inProgIdent = "";
					}
				}
				case ESCAPE_SEQ -> {
					switch (ch) {
						case 'b', 't', 'n', 'r', '"', '\'', '\\' -> {
							pos++;
							posInLine++;
							state = State.HAVE_STRINGLIT;
						}
						default -> {
							tokens.add(new Token(Kind.ERROR,startPos,pos - startPos,line,startPosInLine, inputString));
						}
					}
				}
				case HAVE_SCOMMENT ->
				{
					if(ch == '\n' || ch == '\r')
					{
						pos++;
						posInLine++;
						line++;
						state = State.START;
					}
					else if(ch == EOFchar)
					{
						state = State.START;
					}
					else
					{
						pos++;
						posInLine++;
						ch = chars[pos];
						state = State.HAVE_SCOMMENT;
					}
				}
				case HAVE_MCOMMENT ->
				{				
					if(ch == '*')
					{
						pos++;
						posInLine++;
						ch = chars[pos];
						if(ch == '/')	//if true, end of comment
						{
							pos++;
							posInLine++;
							state = State.START;	//go back to start after comment
						}
						else if (ch == EOFchar)
						{
							state = State.START;	
						}
						else	//else, keep looking for end of comment
						{
							state = State.HAVE_MCOMMENT;	
						}
					}
					else
					{
						pos++;
						posInLine++;
						state = State.HAVE_MCOMMENT;
					}
				}	
			}		
		}
		
		tokens.add(new Token(Kind.EOF,pos,0,line,startPosInLine, inputString));
	
	}
	
	@Override
	public IPLPToken nextToken() throws LexicalException {
		// TODO Auto-generated method stub
		nextTokenPos++;
		
		if(tokens.get(nextTokenPos-1).getKind() == Kind.ERROR)
		{
			throw new LexicalException("Lexical Error",tokens.get(nextTokenPos-1).getLine(),tokens.get(nextTokenPos-1).getCharPositionInLine() );
		}
		return tokens.get(nextTokenPos-1);
	}

}
