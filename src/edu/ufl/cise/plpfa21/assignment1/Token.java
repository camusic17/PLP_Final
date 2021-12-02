package edu.ufl.cise.plpfa21.assignment1;

import java.util.Arrays;

public class Token implements IPLPToken{
	
	Kind kind;
	public int pos, length, line, posInLine;
	String input;
	String text;
	
	Token(Kind kind, int pos, int length, int line, int posInLine, String input)
	{
		this.kind = kind;
		this.pos = pos;
		this.length = length;
		this.line = line; 
		this.posInLine = posInLine;
		this.input = input;
		
		this.text = input.substring(posInLine-1,posInLine-1+length);
		
		//System.out.println("Token Created - Kind: " + kind + " Pos: " + pos + " Length: " + length + " PosInLine: " + posInLine + " Text: " + text);
		
	}

	@Override
	public Kind getKind() {
		// TODO Auto-generated method stub
		return this.kind;
	}

	@Override
	public String getText() {
		// TODO Auto-generated method stub
		int numChars = input.length();
		char[] chars;
		chars = Arrays.copyOf(input.toCharArray(), numChars + 1);
		
		if (this.kind == Kind.STRING_LITERAL) {
			int startPos = this.pos + 1;
			int n = this.length - 2;
			
			String s0 = String.copyValueOf(chars, startPos , n);
			// Dealing with escapes in the string literal.
			String s1 = s0.replace("\\r","\r");
			String s2 = s1.replace("\\n","\n");
			String s3 = s2.replace("\\b","\b");
			String s4 = s3.replace("\\t","\t");
			String s5 = s4.replace("\\'","\'");
			String s6 = s5.replace("\\\"","\"");
			String s7 = s6.replace("\\\\","\\");

			return s7;
		} else {
			return String.copyValueOf(chars, this.pos, this.length);
		}
		//return this.text;
		
		
	}

	@Override
	public int getLine() {
		// TODO Auto-generated method stub
		return this.line;
	}

	@Override
	public int getCharPositionInLine() {
		// TODO Auto-generated method stub
		return this.posInLine-1;
	}

	@Override
	public String getStringValue() {
		// TODO Auto-generated method stub
		//need to figure out EscapeSeq and StringLits
		return text.substring(1,text.length()-1);
	}

	@Override
	public int getIntValue() {
		// TODO Auto-generated method stub
		if(this.input.substring(pos,pos+length).length()>0)
		{			
			return Integer.valueOf(this.input.substring(pos,pos+length));
		}
		else
		{
			return 0;
		}
		
	}

}
