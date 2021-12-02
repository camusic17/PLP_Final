package edu.ufl.cise.plpfa21.assignment1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ExampleLexerTests implements PLPTokenKinds {

	IPLPLexer getLexer(String input) {
		return CompilerComponentFactory.getLexer(input);
	}
	


	@Test
	public void test0() throws LexicalException {
		String input = """

				""";
		IPLPLexer lexer = getLexer(input);
		{
			IPLPToken token = lexer.nextToken();
			Kind kind = token.getKind();
			assertEquals(kind, Kind.EOF);
		}
	}

	@Test
	public void test1() throws LexicalException {
		String input = """
				abc
				  def
				     ghi

				""";
		IPLPLexer lexer = getLexer(input);
		{
			IPLPToken token = lexer.nextToken();
			Kind kind = token.getKind();
			assertEquals(kind, Kind.IDENTIFIER);
			int line = token.getLine();
			assertEquals(line, 1);
			int charPositionInLine = token.getCharPositionInLine();
			assertEquals(0,charPositionInLine);
			String text = token.getText();
			assertEquals("abc",text);
		}
		{
			IPLPToken token = lexer.nextToken();
			Kind kind = token.getKind();
			assertEquals(kind, Kind.IDENTIFIER);
			int line = token.getLine();
			assertEquals(line, 2);
			int charPositionInLine = token.getCharPositionInLine();
			assertEquals(charPositionInLine, 2);
			String text = token.getText();
			assertEquals("def",text);
		}
		{
			IPLPToken token = lexer.nextToken();
			Kind kind = token.getKind();
			assertEquals(kind, Kind.IDENTIFIER);
			int line = token.getLine();
			assertEquals(line, 3);
			int charPositionInLine = token.getCharPositionInLine();
			assertEquals(charPositionInLine, 5);
			String text = token.getText();
			assertEquals("ghi", text);
		}
		{
			IPLPToken token = lexer.nextToken();
			Kind kind = token.getKind();
			assertEquals(kind, Kind.EOF);
		}
	}

	@Test
	public void test2() throws LexicalException {
		String input = """
				a123 123a
				""";
		IPLPLexer lexer = getLexer(input);
		{
			IPLPToken token = lexer.nextToken();
			Kind kind = token.getKind();
			assertEquals(kind, Kind.IDENTIFIER);
			int line = token.getLine();
			assertEquals(line, 1);
			int charPositionInLine = token.getCharPositionInLine();
			assertEquals(charPositionInLine, 0);
			String text = token.getText();
			assertEquals(text, "a123");
		}
		{
			IPLPToken token = lexer.nextToken();
			Kind kind = token.getKind();
			assertEquals(kind, Kind.INT_LITERAL);
			int line = token.getLine();
			assertEquals(line, 1);
			int charPositionInLine = token.getCharPositionInLine();
			assertEquals(5, charPositionInLine);
			String text = token.getText();
			assertEquals(text, "123");
			int val = token.getIntValue();
			assertEquals(val, 123);
		}
		{
			IPLPToken token = lexer.nextToken();
			Kind kind = token.getKind();
			assertEquals(kind, Kind.IDENTIFIER);
			int line = token.getLine();
			assertEquals(line, 1);
			int charPositionInLine = token.getCharPositionInLine();
			assertEquals(charPositionInLine, 8);
			String text = token.getText();
			assertEquals(text, "a");
		}
		{
			IPLPToken token = lexer.nextToken();
			Kind kind = token.getKind();
			assertEquals(kind, Kind.EOF);
		}
	}

	@Test
	public void test3() throws LexicalException {
		String input = """
				= == ===
				""";
		IPLPLexer lexer = getLexer(input);
		{
			IPLPToken token = lexer.nextToken();
			Kind kind = token.getKind();
			assertEquals(kind, Kind.ASSIGN);
			int line = token.getLine();
			assertEquals(line, 1);
			int charPositionInLine = token.getCharPositionInLine();
			assertEquals(charPositionInLine, 0);
			String text = token.getText();
			assertEquals(text, "=");
		}
		{
			IPLPToken token = lexer.nextToken();
			Kind kind = token.getKind();
			assertEquals(kind, Kind.EQUALS);
			int line = token.getLine();
			assertEquals(line, 1);
			int charPositionInLine = token.getCharPositionInLine();
			//assertEquals(charPositionInLine, 2);
			String text = token.getText();
			assertEquals(text, "==");
		}
		{
			IPLPToken token = lexer.nextToken();
			Kind kind = token.getKind();
			assertEquals(kind, Kind.EQUALS);
			int line = token.getLine();
			assertEquals(line, 1);
			int charPositionInLine = token.getCharPositionInLine();
			assertEquals(charPositionInLine, 5);
			String text = token.getText();
			assertEquals(text, "==");
		}
		{
			IPLPToken token = lexer.nextToken();
			Kind kind = token.getKind();
			assertEquals(kind, Kind.ASSIGN);
			int line = token.getLine();
			assertEquals(line, 1);
			int charPositionInLine = token.getCharPositionInLine();
			assertEquals(charPositionInLine, 7);
			String text = token.getText();
			assertEquals(text, "=");
		}
		{
			IPLPToken token = lexer.nextToken();
			Kind kind = token.getKind();
			assertEquals(kind, Kind.EOF);
		}
	}

	@Test
	public void test4() throws LexicalException {
		String input = """
				a %
				""";
		IPLPLexer lexer = getLexer(input);
		{
			IPLPToken token = lexer.nextToken();
			Kind kind = token.getKind();
			assertEquals(kind, Kind.IDENTIFIER);
			int line = token.getLine();
			assertEquals(line, 1);
			int charPositionInLine = token.getCharPositionInLine();
			assertEquals(charPositionInLine, 0);
			String text = token.getText();
			assertEquals(text, "a");
		}
		assertThrows(LexicalException.class, () -> {
			@SuppressWarnings("unused")
			IPLPToken token = lexer.nextToken();
		});
	}

	@SuppressWarnings("unused")
	@Test
	public void test5() throws LexicalException {
		String input = """
				99999999999999999999999999999999999999999999999999999999999999999999999
				""";
		IPLPLexer lexer = getLexer(input);
		assertThrows(LexicalException.class, () -> {
			IPLPToken token = lexer.nextToken();
		});
	}
	
	@Test
	public void test6() throws LexicalException {
		String input = "&&";
		IPLPLexer lexer = getLexer(input);
		{
			IPLPToken token = lexer.nextToken();
			Kind kind = token.getKind();
			assertEquals(kind, Kind.AND);
		}
	}
	
	@Test
	public void test7() throws LexicalException {
		String input = "( < )";
		IPLPLexer lexer = getLexer(input);
		{
			IPLPToken token = lexer.nextToken();
			Kind kind = token.getKind();
			assertEquals(kind, Kind.LPAREN);
			
			token = lexer.nextToken();
			kind = token.getKind();
			assertEquals(kind, Kind.LT);
			
			token = lexer.nextToken();
			kind = token.getKind();
			assertEquals(kind, Kind.RPAREN);
		}
	}
	
	@Test
	public void test8() throws LexicalException {
		String input = "+";
		IPLPLexer lexer = getLexer(input);
		{
			IPLPToken token = lexer.nextToken();
			Kind kind = token.getKind();
			int pos = token.getCharPositionInLine();
			int line = token.getLine();
			assertEquals(kind, Kind.PLUS);
			assertEquals(0,pos);
			assertEquals(1,line);
			
		}
	}
	
	@Test
	public void test9() throws LexicalException {
		String input = "+	-";
		IPLPLexer lexer = getLexer(input);
		{
			IPLPToken token = lexer.nextToken();
			Kind kind = token.getKind();
			int pos = token.getCharPositionInLine();
			int line = token.getLine();
			assertEquals(kind, Kind.PLUS);
			assertEquals(0, pos);
			assertEquals(1,line);
			
			token = lexer.nextToken();
			kind = token.getKind();
			pos = token.getCharPositionInLine();
			line = token.getLine();
			assertEquals(2,pos);
			assertEquals(1,line);
			
		}
	}
	
	@Test
	public void test10() throws LexicalException {
		String input = "1";
		IPLPLexer lexer = getLexer(input);
		{
			IPLPToken token = lexer.nextToken();
			Kind kind = token.getKind();
			int pos = token.getCharPositionInLine();
			@SuppressWarnings("unused")
			int line = token.getLine();
			assertEquals(kind, Kind.INT_LITERAL);
			assertEquals(0,pos);
			
		}
	}
	
	@Test
	public void test11() throws LexicalException {
		String input = "12";
		IPLPLexer lexer = getLexer(input);
		{
			IPLPToken token = lexer.nextToken();
			Kind kind = token.getKind();
			int pos = token.getCharPositionInLine();
			@SuppressWarnings("unused")
			int line = token.getLine();
			assertEquals(kind, Kind.INT_LITERAL);
			assertEquals(0, pos);
			
			
		}
	}
	
	@Test
	public void test12() throws LexicalException {
		String input = "99999999999999999999999999999999999999999999999999999999999999999999999";
		IPLPLexer lexer = getLexer(input);
		assertThrows(LexicalException.class, () -> {
			@SuppressWarnings("unused")
			IPLPToken token = lexer.nextToken();
		});
	}
	
	@Test
	public void test13() throws LexicalException {
		String input = "INT";
		IPLPLexer lexer = getLexer(input);
		IPLPToken token = lexer.nextToken();
		Kind kind = token.getKind();
		int pos = token.getCharPositionInLine();
		
		assertEquals(Kind.KW_INT,kind);
		assertEquals(0, pos);
	}
	
	@Test
	public void test14() throws LexicalException {
		String input = "$abc";
		IPLPLexer lexer = getLexer(input);
		IPLPToken token = lexer.nextToken();
		Kind kind = token.getKind();
		int pos = token.getCharPositionInLine();
		
		assertEquals(Kind.IDENTIFIER, kind);
		assertEquals(0, pos);
	}
	
	@Test
	public void test15() throws LexicalException {
		String input = "$abc BOOLEAN";
		IPLPLexer lexer = getLexer(input);
		IPLPToken token = lexer.nextToken();
		Kind kind = token.getKind();
		int pos = token.getCharPositionInLine();
		
		assertEquals(Kind.IDENTIFIER, kind);
		assertEquals(0, pos);
		
		token = lexer.nextToken();
		kind = token.getKind();
		pos = token.getCharPositionInLine();
		assertEquals(Kind.KW_BOOLEAN, kind);
		assertEquals(5, pos);
	}
	
	@Test
	public void test16() throws LexicalException {
		String input = "\n";
		IPLPLexer lexer = getLexer(input);
		IPLPToken token = lexer.nextToken();
		Kind kind = token.getKind();
		int pos = token.getCharPositionInLine();
		int line = token.getLine();
		
		
		assertEquals(Kind.EOF, kind);
		assertEquals(0, pos);
		assertEquals(2, line);
		
		
	
	}
	@Test
	public void test17() throws LexicalException {
		String input = "abc\n123";
		IPLPLexer lexer = getLexer(input);
		IPLPToken token = lexer.nextToken();
		int pos = token.getCharPositionInLine();
		int line = token.getLine();		
		String text = token.getText();
				
		assertEquals("abc",text);
		assertEquals(0, pos);
		assertEquals(1, line);
		
		token = lexer.nextToken();
		pos = token.getCharPositionInLine();
		line = token.getLine();		
		text = token.getText();
		
		assertEquals("123",text);
		assertEquals(0, pos);
		assertEquals(2, line);
	
	}
	
	@Test
	public void test18() throws LexicalException {
		String input = "IF(x < 7){};";
		IPLPLexer lexer = getLexer(input);
		IPLPToken token = lexer.nextToken();
		int pos = token.getCharPositionInLine();
		int line = token.getLine();		
		String text = token.getText();
				
		assertEquals("IF",text);
		assertEquals(0, pos);
		assertEquals(1, line);
		
		token = lexer.nextToken();
		pos = token.getCharPositionInLine();
		line = token.getLine();		
		text = token.getText();
		
		assertEquals("(",text);
		assertEquals(2, pos);
		assertEquals(1, line);
		
		token = lexer.nextToken();
		pos = token.getCharPositionInLine();
		line = token.getLine();		
		text = token.getText();
		
		assertEquals("x",text);
		assertEquals(3, pos);
		assertEquals(1, line);
	
	}
	
	@SuppressWarnings("unused")
	@Test
	public void test19() throws LexicalException {
		String input = "&1";
		IPLPLexer lexer = getLexer(input);
		{
			assertThrows(LexicalException.class, () -> {
				IPLPToken token = lexer.nextToken();
			});
		}
	}
	
	@Test
	public void test20() throws LexicalException {
		String input = "abc/*...*/def";
		IPLPLexer lexer = getLexer(input);
		{
			IPLPToken token = lexer.nextToken();
			Kind kind = token.getKind();
			int pos = token.getCharPositionInLine();
			
			assertEquals(kind, Kind.IDENTIFIER);
			assertEquals(0, pos);
			
			
			token = lexer.nextToken();
			kind = token.getKind();
			pos = token.getCharPositionInLine();
			
			assertEquals(kind, Kind.IDENTIFIER);
			assertEquals(10,pos);
			
			
		}
	}
	
	@Test
	public void test21() throws LexicalException {
		String input = "abc //def";
		IPLPLexer lexer = getLexer(input);
		{
			IPLPToken token = lexer.nextToken();
			Kind kind = token.getKind();
			int pos = token.getCharPositionInLine();
			
			assertEquals(kind, Kind.IDENTIFIER);
			assertEquals(0, pos);
			
			
			token = lexer.nextToken();
			kind = token.getKind();
			pos = token.getCharPositionInLine();
			
			assertEquals(kind, Kind.EOF);
			assertEquals(9,pos);
			
			
		}
	}
	
//	@Test
//	public void test22() throws LexicalException {
//		String input = "'abc'";
//		IPLPLexer lexer = getLexer(input);
//		{
//			IPLPToken token = lexer.nextToken();
//			Kind kind = token.getKind();
//			int pos = token.getCharPositionInLine();
//			
//			assertEquals(kind, Kind.STRING_LITERAL);
//			assertEquals(0, pos);
//			
//			
//			token = lexer.nextToken();
//			kind = token.getKind();
//			pos = token.getCharPositionInLine();
//			
//			assertEquals(kind, Kind.EOF);
//			assertEquals(5,pos);
//			
//			
//		}
//	}
	@Test
	public void test23() throws LexicalException {
		String input = "\"def\"";
		IPLPLexer lexer = getLexer(input);
		{
			IPLPToken token = lexer.nextToken();
			Kind kind = token.getKind();
			int pos = token.getCharPositionInLine();
			
			assertEquals(kind, Kind.STRING_LITERAL);
			assertEquals(0, pos);
			
			
			token = lexer.nextToken();
			kind = token.getKind();
			pos = token.getCharPositionInLine();
			
			assertEquals(kind, Kind.EOF);
			assertEquals(5,pos);
			
			
		}
	}

}