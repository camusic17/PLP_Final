package edu.ufl.cise.plpfa21.assignment2;

import java.util.ArrayList;
import java.util.List;

import edu.ufl.cise.plpfa21.assignment1.*;
import edu.ufl.cise.plpfa21.assignment3.ast.*;
import edu.ufl.cise.plpfa21.assignment3.astimpl.*;

public class Parser implements IPLPParser {

	Lexer theLexer;
	IPLPToken theToken;

	// checks if theToken has same kind as given token
	public IPLPToken match(IPLPToken.Kind k) throws SyntaxException {
		if (theToken.getKind() == k) {
			return consumeToken();
		} else {
			throw new SyntaxException("Token matching error", theToken.getLine(), theToken.getCharPositionInLine());
		}

	}

	public IPLPToken matchEOF(IPLPToken t) throws SyntaxException {
		if (t.getKind() == IPLPToken.Kind.EOF) {
			return t;
		} else {
			throw new SyntaxException("Token matching error", t.getLine(), t.getCharPositionInLine());
		}
	}

	public IPLPToken consumeToken() {
		try {
			theToken = theLexer.nextToken();
		} catch (LexicalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return theToken;
	}

	public boolean isKind(IPLPToken.Kind k1, IPLPToken.Kind k2) {
		if (k1 == k2) {
			return true;
		} else {
			return false;
		}
	}

	// DONE I THINK
	@Override
	public IASTNode parse() throws SyntaxException {
		// TODO Auto-generated method stub
		return program();
	}

	// DONE I THINK
	public Parser(Lexer l) {
		theLexer = l;

		try {
			theToken = l.nextToken();
		} catch (LexicalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// DONE I THINK
	public IProgram program() throws SyntaxException {
		ArrayList<IDeclaration> decs = new ArrayList<>();

		IDeclaration curDec = null;
		curDec = declaration();
		if (curDec != null) {
			decs.add(curDec);
		}
		
		while(curDec != null)			
		{
			curDec = declaration();
			if (curDec != null) {
				decs.add(curDec);
			}
		}

		IProgram theProgram = new Program__(theToken.getLine(), theToken.getCharPositionInLine(), theToken.getText(), decs);

		return theProgram;
	}

	// DONE I THINK
	public IBlock block() throws SyntaxException 
	{
		IPLPToken temp = this.theToken;
		ArrayList<IStatement> statements = new ArrayList<>();

		while (!isKind(theToken.getKind(), IPLPToken.Kind.KW_END)) {
			statements.add(statement());
		}

		return new Block__(temp.getLine(), temp.getCharPositionInLine(), temp.getText(), statements);
	}

	
	public IStatement statement() throws SyntaxException {
		//WIP
		if (isKind(theToken.getKind(), IPLPToken.Kind.KW_LET)) {
			IPLPToken temp = theToken;
			match(IPLPToken.Kind.KW_LET);
			INameDef def = nameDef();
			IExpression e = null;
			if (!isKind(theToken.getKind(), IPLPToken.Kind.SEMI)) {
				match(IPLPToken.Kind.ASSIGN);

				e = expression();
			}
			match(IPLPToken.Kind.SEMI);
			
			return new LetStatement__(temp.getLine(), temp.getCharPositionInLine(), temp.getText(), null, e, def);

		}
		else if (isKind(theToken.getKind(), IPLPToken.Kind.KW_SWITCH)) {
			IPLPToken temp = theToken;
			match(IPLPToken.Kind.KW_SWITCH);
			IExpression e = expression();

			ArrayList<IExpression> branchExprs = new ArrayList<>();
			ArrayList<IBlock> blocks = new ArrayList<>();

			while (isKind(theToken.getKind(), IPLPToken.Kind.KW_CASE)) {
				match(IPLPToken.Kind.KW_CASE);
				branchExprs.add(expression());
				match(IPLPToken.Kind.COLON);
				blocks.add(block());

			}

			match(IPLPToken.Kind.KW_DEFAULT);
			IBlock bl = block();
			match(IPLPToken.Kind.KW_END);

			return new SwitchStatement__(temp.getLine(), temp.getCharPositionInLine(), temp.getText(), e, branchExprs, blocks, bl);

		}
		else if (isKind(theToken.getKind(), IPLPToken.Kind.KW_IF)) {
			IPLPToken temp = theToken;
			match(IPLPToken.Kind.KW_IF);
			IExpression e = expression();
			match(IPLPToken.Kind.KW_DO);
			IBlock bl = block();
			match(IPLPToken.Kind.KW_END);

			return new IfStatement__(temp.getLine(), temp.getCharPositionInLine(), temp.getText(), e, bl);

		}
		else if (isKind(theToken.getKind(), IPLPToken.Kind.KW_WHILE)) {
			IPLPToken temp = theToken;
			match(IPLPToken.Kind.KW_WHILE);
			IExpression e = expression();
			match(IPLPToken.Kind.KW_DO);
			IBlock bl = block();
			match(IPLPToken.Kind.KW_END);

			return new WhileStatement__(temp.getLine(), temp.getCharPositionInLine(), temp.getText(), e, bl);

		}
		else if (isKind(theToken.getKind(), IPLPToken.Kind.KW_RETURN)) {
			IPLPToken temp = theToken;
			match(IPLPToken.Kind.KW_RETURN);
			IExpression e = expression();
			match(IPLPToken.Kind.SEMI);

			return new ReturnStatement__(temp.getLine(), temp.getCharPositionInLine(), temp.getText(), e);
		}
		else {
			IPLPToken temp = theToken;

			IExpression left = expression();
			IExpression right = null;
			if (!isKind(theToken.getKind(), IPLPToken.Kind.SEMI)) {
				match(IPLPToken.Kind.ASSIGN);
				right = expression();
			}
			match(IPLPToken.Kind.SEMI);
			
			return new AssignmentStatement__(temp.getLine(), temp.getCharPositionInLine(), temp.getText(), left, right);
		}
	}

	public IDeclaration declaration() throws SyntaxException {

		if (isKind(theToken.getKind(), IPLPToken.Kind.KW_VAR)) {
			IPLPToken temp = theToken;
			match(IPLPToken.Kind.KW_VAR);
			INameDef def = nameDef();
			IExpression e = null;
			if (isKind(theToken.getKind(), IPLPToken.Kind.ASSIGN)) {
				match(IPLPToken.Kind.ASSIGN);
				e = expression();
			}
			match(IPLPToken.Kind.SEMI);

			return new MutableGlobal__(temp.getLine(), temp.getCharPositionInLine(), temp.getText(), def, e);
			
		} 
		else if (isKind(theToken.getKind(), IPLPToken.Kind.KW_VAL)) {
			IPLPToken temp = theToken;
			match(IPLPToken.Kind.KW_VAL);
			INameDef def = nameDef();
			match(IPLPToken.Kind.ASSIGN);
			IExpression e = expression();
			match(IPLPToken.Kind.SEMI);

			return new ImmutableGlobal__(temp.getLine(), temp.getCharPositionInLine(), temp.getText(), def, e);
		} 
		else if (isKind(theToken.getKind(), IPLPToken.Kind.KW_FUN)) 
		{		
			IPLPToken temp = theToken;
			match(IPLPToken.Kind.KW_FUN);
			IPLPToken idTemp = theToken;
			match(IPLPToken.Kind.IDENTIFIER);
			IIdentifier identifier = new Identifier__(idTemp.getLine(), idTemp.getCharPositionInLine(), idTemp.getText(), idTemp.getText());
			
			match(IPLPToken.Kind.LPAREN);
			ArrayList<INameDef> args = new ArrayList<>();

			if (isKind(theToken.getKind(), IPLPToken.Kind.IDENTIFIER)) {
				args.add(nameDef());
				while (isKind(theToken.getKind(), IPLPToken.Kind.COMMA)) {
					match(IPLPToken.Kind.COMMA);
					args.add(nameDef());
				}
			}
			match(IPLPToken.Kind.RPAREN);

			IType type = null;
			if (isKind(theToken.getKind(), IPLPToken.Kind.COLON)) {
				match(IPLPToken.Kind.COLON);
				type = type();
			}

			match(IPLPToken.Kind.KW_DO);
			IBlock bl = block();
			match(IPLPToken.Kind.KW_END);

			return new FunctionDeclaration___(temp.getLine(), temp.getCharPositionInLine(), temp.getText(), identifier, args, type, bl);

		}

		return null;
	}

	public INameDef nameDef() throws SyntaxException {
		IPLPToken temp = theToken;
		match(IPLPToken.Kind.IDENTIFIER);
		IIdentifier ident = new Identifier__(temp.getLine(), temp.getCharPositionInLine(), temp.getText(),	temp.getText());
		IType type = null;
		if (isKind(theToken.getKind(), IPLPToken.Kind.COLON)) {
			match(IPLPToken.Kind.COLON);
			type = type();

		}

		return new NameDef__(temp.getLine(), temp.getCharPositionInLine(), temp.getText(), ident, type);

	}

	public IType type() throws SyntaxException {
		IPLPToken temp = null;

		if (isKind(theToken.getKind(), IPLPToken.Kind.KW_INT)) {
			temp = theToken;
			match(IPLPToken.Kind.KW_INT);
			return new PrimitiveType__(temp.getLine(), temp.getCharPositionInLine(), temp.getText(), IType.TypeKind.INT);

		} else if (isKind(theToken.getKind(), IPLPToken.Kind.KW_STRING)) {
			temp = theToken;
			match(IPLPToken.Kind.KW_STRING);
			//System.out.println("STRING VALUE: "+temp.getText());
			return new PrimitiveType__(temp.getLine(), temp.getCharPositionInLine(), temp.getText(), IType.TypeKind.STRING);

		} else if (isKind(theToken.getKind(), IPLPToken.Kind.KW_BOOLEAN)) {
			temp = theToken;
			match(IPLPToken.Kind.KW_BOOLEAN);
			return new PrimitiveType__(temp.getLine(), temp.getCharPositionInLine(), temp.getText(), IType.TypeKind.BOOLEAN);

		} else if (isKind(theToken.getKind(), IPLPToken.Kind.KW_LIST)) {
			temp = theToken;
			match(IPLPToken.Kind.KW_LIST);
			match(IPLPToken.Kind.LSQUARE);

			IType type = null;
			if (!isKind(theToken.getKind(), IPLPToken.Kind.RSQUARE)) {
				type = type();
			}

			match(IPLPToken.Kind.RSQUARE);

			return new ListType__(temp.getLine(), temp.getCharPositionInLine(), temp.getText(), type);

		}

		return null;
	}

	// expression = compExpression ((&& | ||) compExpression)*
	public IExpression expression() throws SyntaxException {
		IExpression left;
		IExpression right;

		left = compExpr();
		while (isKind(theToken.getKind(), IPLPToken.Kind.AND) || isKind(theToken.getKind(), IPLPToken.Kind.OR)) {
			IPLPToken temp = this.theToken;
			PLPTokenKinds.Kind tempKind = null;
			if (isKind(theToken.getKind(), IPLPToken.Kind.AND)) {
				match(IPLPToken.Kind.AND);
				tempKind = PLPTokenKinds.Kind.AND;
			} else if (isKind(theToken.getKind(), IPLPToken.Kind.OR)) {
				match(IPLPToken.Kind.OR);
				tempKind = PLPTokenKinds.Kind.OR;
			}
			right = compExpr();
			left = new BinaryExpression__(temp.getLine(), temp.getCharPositionInLine(), temp.getText(), left, right, tempKind);
		}

		return left;
	}

	// compExpr = addExpr ((<|>|==|!=) addExpr)*
	public IExpression compExpr() throws SyntaxException {
		IExpression left;
		IExpression right;

		left = addExpr();
		while (isKind(theToken.getKind(), IPLPToken.Kind.LT) || isKind(theToken.getKind(), IPLPToken.Kind.GT) ||
				isKind(theToken.getKind(), IPLPToken.Kind.EQUALS) || isKind(theToken.getKind(), IPLPToken.Kind.NOT_EQUALS)) {
			IPLPToken temp = this.theToken;

			PLPTokenKinds.Kind tempKind = null;

			if (isKind(theToken.getKind(), IPLPToken.Kind.LT)) {
				match(IPLPToken.Kind.LT);
				tempKind = PLPTokenKinds.Kind.LT;
			} else if (isKind(theToken.getKind(), IPLPToken.Kind.GT)) {
				match(IPLPToken.Kind.GT);
				tempKind = PLPTokenKinds.Kind.GT;
			} else if (isKind(theToken.getKind(), IPLPToken.Kind.EQUALS)) {
				match(IPLPToken.Kind.EQUALS);
				tempKind = PLPTokenKinds.Kind.EQUALS;
			} else if (isKind(theToken.getKind(), IPLPToken.Kind.NOT_EQUALS)) {
				match(IPLPToken.Kind.NOT_EQUALS);
				tempKind = PLPTokenKinds.Kind.NOT_EQUALS;
			}
			right = addExpr();
			left = new BinaryExpression__(temp.getLine(), temp.getCharPositionInLine(), temp.getText(), left, right, tempKind);
		}

		return left;
	}

	// addExpr = multExpr ((+|-) multExpr)*
	public IExpression addExpr() throws SyntaxException {
		IExpression left;
		IExpression right;

		left = multExpr();
		while (isKind(theToken.getKind(), IPLPToken.Kind.PLUS) || isKind(theToken.getKind(), IPLPToken.Kind.MINUS)) {
			IPLPToken temp = this.theToken;
			PLPTokenKinds.Kind tempKind = null;
			if (isKind(theToken.getKind(), IPLPToken.Kind.PLUS)) {
				match(IPLPToken.Kind.PLUS);
				tempKind = PLPTokenKinds.Kind.PLUS;
			} else if (isKind(theToken.getKind(), IPLPToken.Kind.MINUS)) {
				match(IPLPToken.Kind.MINUS);
				tempKind = PLPTokenKinds.Kind.MINUS;
			}
			right = multExpr();
			left = new BinaryExpression__(temp.getLine(), temp.getCharPositionInLine(), temp.getText(), left, right, tempKind);
		}

		return left;
	}

	// multExpr = unExpr ((*|/) unExpr)*
	public IExpression multExpr() throws SyntaxException {
		IExpression left;
		IExpression right;

		left = unExpr();
		while (isKind(theToken.getKind(), IPLPToken.Kind.TIMES) || isKind(theToken.getKind(), IPLPToken.Kind.DIV)) {
			IPLPToken temp = this.theToken;
			PLPTokenKinds.Kind tempKind = null;
			if (isKind(theToken.getKind(), IPLPToken.Kind.TIMES)) {
				match(IPLPToken.Kind.TIMES);
				tempKind = PLPTokenKinds.Kind.TIMES;
			} else if (isKind(theToken.getKind(), IPLPToken.Kind.DIV)) {
				match(IPLPToken.Kind.DIV);
				tempKind = PLPTokenKinds.Kind.DIV;
			}
			right = unExpr();
			left = new BinaryExpression__(temp.getLine(), temp.getCharPositionInLine(), temp.getText(), left, right, tempKind);
		}

		return left;
	}

	// unExpr = (!|-)? primeExpr
	public IExpression unExpr() throws SyntaxException {
		IPLPToken temp = null;
		PLPTokenKinds.Kind tempKind = null;
		if (isKind(theToken.getKind(), IPLPToken.Kind.BANG)) {
			temp = theToken;
			match(IPLPToken.Kind.BANG);
			tempKind = PLPTokenKinds.Kind.BANG;
			IExpression e = primeExpr();
			return new UnaryExpression__(temp.getLine(), temp.getCharPositionInLine(), temp.getText(), e,	tempKind);
		} 
		else if (isKind(theToken.getKind(), IPLPToken.Kind.MINUS)) {
			temp = theToken;
			match(IPLPToken.Kind.MINUS);
			tempKind = PLPTokenKinds.Kind.MINUS;
			IExpression e = primeExpr();
			return new UnaryExpression__(temp.getLine(), temp.getCharPositionInLine(), temp.getText(), e,	tempKind);

		}

		return primeExpr();

	}

	public IExpression primeExpr() throws SyntaxException {
		IPLPToken temp = null;

		if (isKind(theToken.getKind(), IPLPToken.Kind.KW_NIL)) {
			temp = theToken;
			match(IPLPToken.Kind.KW_NIL);

			return new NilConstantExpression__(temp.getLine(), temp.getCharPositionInLine(), temp.getText());
		} 
		else if (isKind(theToken.getKind(), IPLPToken.Kind.KW_TRUE)) {
			temp = theToken;
			match(IPLPToken.Kind.KW_TRUE);

			return new BooleanLiteralExpression__(temp.getLine(), temp.getCharPositionInLine(), temp.getText(), true);
		} 
		else if (isKind(theToken.getKind(), IPLPToken.Kind.KW_FALSE)) {
			temp = theToken;
			match(IPLPToken.Kind.KW_FALSE);

			return new BooleanLiteralExpression__(temp.getLine(), temp.getCharPositionInLine(), temp.getText(), false);
		} 
		else if (isKind(theToken.getKind(), IPLPToken.Kind.INT_LITERAL)) {
			temp = theToken;
			match(IPLPToken.Kind.INT_LITERAL);

			return new IntLiteralExpression__(temp.getLine(), temp.getCharPositionInLine(), temp.getText(),	Integer.parseInt(temp.getText()));
		} 
		
		
		
		//something is wrong here, errors thrown in hw6 test cases for string literal values
		else if (isKind(theToken.getKind(), IPLPToken.Kind.STRING_LITERAL)) {
			temp = theToken;
			match(IPLPToken.Kind.STRING_LITERAL);

			return new StringLiteralExpression__(temp.getLine(), temp.getCharPositionInLine(), temp.getText(),
					temp.getStringValue());
		} 
		else if (isKind(theToken.getKind(), IPLPToken.Kind.LPAREN)) {
			match(IPLPToken.Kind.LPAREN);
			IExpression e = expression();
			match(IPLPToken.Kind.RPAREN);
			return e;
		}
		// Identifier | Identifier ( (Expression ( , Expression)* )? ) | Identifier [
		// Expression ]
		else if (isKind(theToken.getKind(), IPLPToken.Kind.IDENTIFIER)) {
			temp = theToken;

			// first case
			match(IPLPToken.Kind.IDENTIFIER);

			IIdentifier ident = new Identifier__(temp.getLine(), temp.getCharPositionInLine(), temp.getText(),	temp.getText());

			// second case
			if (isKind(theToken.getKind(), IPLPToken.Kind.LPAREN)) {
				// temp = theToken;

				match(IPLPToken.Kind.LPAREN);
				// (Expression ( , Expression)* )?

				ArrayList<IExpression> args = new ArrayList<>();

				try {
					args.add(expression());
					while (isKind(theToken.getKind(), IPLPToken.Kind.COMMA)) {
						match(IPLPToken.Kind.COMMA);
						args.add(expression());
					}
				} catch (SyntaxException e) {
					e.printStackTrace();
				}

				match(IPLPToken.Kind.RPAREN);

				return new FunctionCallExpression__(theToken.getLine(), theToken.getCharPositionInLine(),theToken.getText(), ident, args);

			}
			// third case
			else if (isKind(theToken.getKind(), IPLPToken.Kind.LSQUARE)) {
				match(IPLPToken.Kind.LSQUARE);
				IExpression e = expression();
				match(IPLPToken.Kind.RSQUARE);
				return e;
			}

			return new IdentExpression__(temp.getLine(), temp.getCharPositionInLine(), temp.getText(), ident);

		}

		return null;

	}

}
