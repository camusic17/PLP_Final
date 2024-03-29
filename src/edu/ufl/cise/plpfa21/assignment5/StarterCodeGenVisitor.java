package edu.ufl.cise.plpfa21.assignment5;


import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import edu.ufl.cise.plpfa21.assignment1.PLPTokenKinds.Kind;
import edu.ufl.cise.plpfa21.assignment3.ast.ASTVisitor;
import edu.ufl.cise.plpfa21.assignment3.ast.IAssignmentStatement;
import edu.ufl.cise.plpfa21.assignment3.ast.IBinaryExpression;
import edu.ufl.cise.plpfa21.assignment3.ast.IBlock;
import edu.ufl.cise.plpfa21.assignment3.ast.IBooleanLiteralExpression;
import edu.ufl.cise.plpfa21.assignment3.ast.IDeclaration;
import edu.ufl.cise.plpfa21.assignment3.ast.IExpression;
import edu.ufl.cise.plpfa21.assignment3.ast.IExpressionStatement;
import edu.ufl.cise.plpfa21.assignment3.ast.IFunctionCallExpression;
import edu.ufl.cise.plpfa21.assignment3.ast.IFunctionDeclaration;
import edu.ufl.cise.plpfa21.assignment3.ast.IIdentExpression;
import edu.ufl.cise.plpfa21.assignment3.ast.IIdentifier;
import edu.ufl.cise.plpfa21.assignment3.ast.IIfStatement;
import edu.ufl.cise.plpfa21.assignment3.ast.IImmutableGlobal;
import edu.ufl.cise.plpfa21.assignment3.ast.IIntLiteralExpression;
import edu.ufl.cise.plpfa21.assignment3.ast.ILetStatement;
import edu.ufl.cise.plpfa21.assignment3.ast.IListSelectorExpression;
import edu.ufl.cise.plpfa21.assignment3.ast.IListType;
import edu.ufl.cise.plpfa21.assignment3.ast.IMutableGlobal;
import edu.ufl.cise.plpfa21.assignment3.ast.INameDef;
import edu.ufl.cise.plpfa21.assignment3.ast.INilConstantExpression;
import edu.ufl.cise.plpfa21.assignment3.ast.IPrimitiveType;
import edu.ufl.cise.plpfa21.assignment3.ast.IProgram;
import edu.ufl.cise.plpfa21.assignment3.ast.IReturnStatement;
import edu.ufl.cise.plpfa21.assignment3.ast.IStatement;
import edu.ufl.cise.plpfa21.assignment3.ast.IStringLiteralExpression;
import edu.ufl.cise.plpfa21.assignment3.ast.ISwitchStatement;
import edu.ufl.cise.plpfa21.assignment3.ast.IType;
import edu.ufl.cise.plpfa21.assignment3.ast.IType.TypeKind;
import edu.ufl.cise.plpfa21.assignment3.ast.IUnaryExpression;
import edu.ufl.cise.plpfa21.assignment3.ast.IWhileStatement;
import edu.ufl.cise.plpfa21.assignment3.astimpl.Type__;
//import edu.ufl.cise.plpfa21.assignment5.StarterCodeGenVisitor.LocalVarInfo;
//import edu.ufl.cise.plpfa21.assignment5.StarterCodeGenVisitor.MethodVisitorLocalVarTable;
//import edu.ufl.cise.plpfa21.pLP.ListSelectorExpression;


public class StarterCodeGenVisitor implements ASTVisitor, Opcodes {
	
	public StarterCodeGenVisitor(String className, String packageName, String sourceFileName){
		this.className = className;
		this.packageName = packageName;	
		this.sourceFileName = sourceFileName;
	}
	

	ClassWriter cw;
	String className;
	String packageName;
	String classDesc;
	String sourceFileName; //



	public static final String stringClass = "java/lang/String";
	public static final String stringDesc = "Ljava/lang/String;";
	public static final String listClass = "java/util/ArrayList";
	public static final String listDesc = "Ljava/util/ArrayList;";
	public static final String runtimeClass = "edu/ufl/cise/plpfa21/assignment5/Runtime";
	
	
	
	/* Records for information passed to children, namely the methodVisitor and information about current methods Local Variables */
	@SuppressWarnings("preview")
	record LocalVarInfo(String name, String typeDesc, Label start, Label end) {}
	@SuppressWarnings("preview")
	record MethodVisitorLocalVarTable(MethodVisitor mv, List<LocalVarInfo> localVars) {};	

	/*  Adds local variables to a method
	 *  The information about local variables to add has been collected in the localVars table.  
	 *  This method should be invoked after all instructions for the method have been generated, immediately before invoking mv.visitMaxs.
	 */
	private void addLocals(MethodVisitorLocalVarTable arg, Label start, Label end) {
		System.out.println("METHOD: LOCALS");
		MethodVisitor mv = arg.mv;
		List<LocalVarInfo> localVars = arg.localVars();
		for (int slot = 0; slot < localVars.size(); slot++) {
			LocalVarInfo varInfo = localVars.get(slot);
			String varName = varInfo.name;
			String localVarDesc = varInfo.typeDesc;
			Label range0 = varInfo.start == null ? start : varInfo.start;
		    Label range1 = varInfo.end == null ? end : varInfo.end;
		    mv.visitLocalVariable(varName, localVarDesc, null, range0, range1, slot);
		}
		//System.out.println("GOT TO END OF addLocals - GIVEN");
	}
	
	@Override
	public Object visitIProgram(IProgram n, Object arg) throws Exception {
		System.out.println("METHOD: PROGRAM");
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		/*
		 * If the call to mv.visitMaxs(1, 1) crashes, it is sometime helpful to temporarily try it without COMPUTE_FRAMES. You won't get a runnable class file
		 * but you can at least see the bytecode that is being generated. 
		 */
//	    cw = new ClassWriter(0); 
		classDesc = "L" + className + ";";
		cw.visit(V16, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", null);
		if (sourceFileName != null) cw.visitSource(sourceFileName, null);
		
		// create MethodVisitor for <clinit>  
		//  This method is the static initializer for the class and contains code to initialize global variables.
		// get a MethodVisitor
		
		MethodVisitor clmv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "<clinit>", "()V", null, null);
		
		// visit the code first
		clmv.visitCode();
		
		//mark the beginning of the code
		Label clinitStart = new Label();
		clmv.visitLabel(clinitStart);
		//create a list to hold local var info.  This will remain empty for <clinit> but is shown for completeness.  Methods with local variable need this.
		List<LocalVarInfo> initializerLocalVars = new ArrayList<LocalVarInfo>();
		//pair the local var infor and method visitor to pass into visit routines
		MethodVisitorLocalVarTable clinitArg = new MethodVisitorLocalVarTable(clmv,initializerLocalVars);
		//visit all the declarations. 
		List<IDeclaration> decs = n.getDeclarations();
		for (IDeclaration dec : decs) {
			dec.visit(this, clinitArg);  //argument contains local variable info and the method visitor.  
		}
		//add a return method
		clmv.visitInsn(RETURN);
		//mark the end of the bytecode for <clinit>
		Label clinitEnd = new Label();
		clmv.visitLabel(clinitEnd);
		
		
		//System.out.println("FLAG");
		//add the locals to the class
		addLocals(clinitArg, clinitStart, clinitEnd);  //shown for completeness.  There shouldn't be any local variables in clinit.
		//required call of visitMaxs.  Since we created the ClassWriter with  COMPUTE_FRAMES, the parameter values don't matter. 
		clmv.visitMaxs(0, 0);
		//finish the method
		clmv.visitEnd();
	
		//finish the clas
		cw.visitEnd();

		//System.out.println("GOT TO END OF visitIProgram - GIVEN");
		//generate classfile as byte array and return
		return cw.toByteArray();
	}

	@Override
	public Object visitIBinaryExpression(IBinaryExpression n, Object arg) throws Exception {		//STILL WIP
		System.out.println("METHOD: BIN EXPR");
		//get the method visitor from the arg
		MethodVisitor mv = ((MethodVisitorLocalVarTable)arg).mv;
		
		//store ref to left and right exprs
		IExpression left = n.getLeft();
		IExpression right = n.getRight();
		
		IType leftType = left.getType();
		IType rightType = right.getType();
	
		//visit left and right expression args
		//puts values of left and right expressions on top of the stack
		left.visit(this, arg);
		right.visit(this, arg);
					
		//get the operator kind
		Kind op = n.getOp();
		
		Label start = new Label();
		Label stop;
		
		//change instructions based on op
		//do the action that the op specifies on the two items on top of the stack
		if(op == Kind.EQUALS)
		{
			if(left.getType().isKind(TypeKind.INT) || left.getType().isKind(TypeKind.BOOLEAN))		
			{			 
				mv.visitJumpInsn(IF_ICMPNE, start);
				mv.visitInsn(ICONST_1);
				stop = new Label();
				mv.visitJumpInsn(GOTO, stop);
				mv.visitLabel(start);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(stop);
			}
			else if(left.getType().isKind(TypeKind.STRING))		
			{
				mv.visitJumpInsn(IF_ACMPNE, start);
				mv.visitInsn(ICONST_1);
				stop = new Label();
				mv.visitJumpInsn(GOTO, stop);
				mv.visitLabel(start);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(stop);
			}
		}
		else if(op == Kind.NOT_EQUALS)
		{
			if(left.getType().isInt() || left.getType().isBoolean())	
			{
				mv.visitJumpInsn(IF_ICMPEQ, start);
				mv.visitInsn(ICONST_1);
				stop = new Label();
				mv.visitJumpInsn(GOTO, stop);
				mv.visitLabel(start);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(stop);
			}
			else if(left.getType().isKind(TypeKind.STRING))	
			{
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "startsWith", "(Ljava/lang/String;)Z", false);
				mv.visitInsn(ICONST_1);
				stop = new Label();
				mv.visitJumpInsn(GOTO, stop);
				mv.visitLabel(start);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(stop);
			}
		}
		else if(op == Kind.LT)
		{
			if(left.getType().isKind(TypeKind.INT) )		
			{
				mv.visitJumpInsn(IF_ICMPGE, start);
				mv.visitInsn(ICONST_1);
				stop = new Label();
				mv.visitJumpInsn(GOTO, stop);
				mv.visitLabel(start);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(stop);
			}
			else if(left.getType().isKind(TypeKind.STRING))		
			{				
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "startsWith", "(Ljava/lang/String;)Z", false);
				mv.visitInsn(ICONST_1);
				stop = new Label();
				mv.visitJumpInsn(GOTO, stop);
				mv.visitLabel(start);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(stop);		
			}
		}
		else if(op == Kind.GT)
		{
			//returns bool
			if(left.getType().isKind(TypeKind.INT))		
			{
				mv.visitJumpInsn(IF_ICMPLE, start);
				mv.visitInsn(ICONST_1);
				stop = new Label();
				mv.visitJumpInsn(GOTO, stop);
				mv.visitLabel(start);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(stop);
			}
			else if(left.getType().isKind(TypeKind.STRING))		
			{
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "startsWith", "(Ljava/lang/String;)Z", false);
				mv.visitInsn(ICONST_1);
				stop = new Label();
				mv.visitJumpInsn(GOTO, stop);
				mv.visitLabel(start);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(stop);
				
			}
		}
		else if(op == Kind.PLUS)
		{
			if(leftType.isInt() && rightType.isInt())		
			{
				mv.visitInsn(IADD);			
			}
			else if(leftType.isString() && rightType.isString())	
			{
				//for some reason this concatenates the first string to itself, not the first to the second
				mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "myConcat", "("+stringDesc+stringDesc+")"+stringDesc,false);			
			}
			else if(left.getType().isKind(TypeKind.LIST))		
			{
				throw new UnsupportedOperationException("LIST NOT IMPLEMENTED");
			}
		}
		else if(op == Kind.MINUS)
		{
			if(leftType.isInt() && rightType.isInt())		
			{
				mv.visitInsn(ISUB);				
			}
		}
		else if(op == Kind.TIMES)
		{
			if(leftType.isInt() && rightType.isInt())	
			{
				mv.visitInsn(IMUL);
			}
		}
		else if(op == Kind.DIV)
		{
			if(leftType.isInt() && rightType.isInt())		
			{
				mv.visitInsn(IDIV);
			}
		}
		else if(op == Kind.AND)
		{
			if(left.getType().isKind(TypeKind.BOOLEAN))		
			{
				mv.visitInsn(IAND);
			}
		}
		else if(op == Kind.OR)
		{
			if(left.getType().isKind(TypeKind.BOOLEAN))		
			{
				mv.visitInsn(IOR);
			}
		}

		return null;
		//throw new UnsupportedOperationException("TO IMPLEMENT");
	}


	@Override
	public Object visitIBlock(IBlock n, Object arg) throws Exception {
		System.out.println("METHOD: BLOCK");
		List<IStatement> statements = n.getStatements();
		for(IStatement statement: statements) {
			statement.visit(this, arg);
		}
		return null;
	}

	@Override
	public Object visitIBooleanLiteralExpression(IBooleanLiteralExpression n, Object arg) throws Exception {
		System.out.println("METHOD: BOOL LIT EXPR");
		MethodVisitor mv = ((MethodVisitorLocalVarTable) arg).mv();
		mv.visitLdcInsn(n.getValue());
		return null;
	}


	
	@Override
	public Object visitIFunctionDeclaration(IFunctionDeclaration n, Object arg) throws Exception {
		System.out.println("METHOD: FUNC DEC");
		String name = n.getName().getName();

		//Local var table
		List<LocalVarInfo> localVars = new ArrayList<LocalVarInfo>();
		//Add args to local var table while constructing type desc.
		List<INameDef> args = n.getArgs();

		//Iterate over the parameter list and build the function descriptor
		//Also assign and store slot numbers for parameters
		StringBuilder sb = new StringBuilder();	
		sb.append("(");
		for( INameDef def: args) {
			String desc = def.getType().getDesc();
			sb.append(desc);
			def.getIdent().setSlot(localVars.size());
			localVars.add(new LocalVarInfo(def.getIdent().getName(), desc, null, null));
		}
		sb.append(")");
		sb.append(n.getResultType().getDesc());
		String desc = sb.toString();
		
		// get method visitor
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, name, desc, null, null);
		// initialize
		mv.visitCode();
		// mark beginning of instructions for method
		Label funcStart = new Label();
		mv.visitLabel(funcStart);
		MethodVisitorLocalVarTable context = new MethodVisitorLocalVarTable(mv, localVars);
		//visit block to generate code for statements
		n.getBlock().visit(this, context);
		
		//add return instruction if Void return type
		if(n.getResultType().equals(Type__.voidType)) {
			mv.visitInsn(RETURN);
		}
		
		//add label after last instruction
		Label funcEnd = new Label();
		mv.visitLabel(funcEnd);
		

		mv.visitMaxs(0, 0);
		
		//terminate construction of method
		mv.visitEnd();
		
		//System.out.println("GOT TO END OF visitIFunctionDeclaration - GIVEN");
		return null;

	}



	//ASSIGNMENT 6
	@Override
	public Object visitIFunctionCallExpression(IFunctionCallExpression n, Object arg) throws Exception {
		System.out.println("METHOD: FUNC CALL EXPR");
		MethodVisitor mv = ((MethodVisitorLocalVarTable)arg).mv;
		IIdentifier ident = n.getName();
		String name = ident.getName();
		List<IExpression> args = n.getArgs();
		
		
		if (args.size() > 0)
		{			
			for (IExpression e : args)
			{
				e.visit(this, arg);				
			}		
		}
		mv.visitMethodInsn(INVOKESTATIC, className, name, null,false);
		
		

		return null;
		//throw new UnsupportedOperationException("TO IMPLEMENT");
	}

	@Override
	public Object visitIIdentExpression(IIdentExpression n, Object arg) throws Exception {
		System.out.println("METHOD: IDENT EXPR");
		MethodVisitor mv = ((MethodVisitorLocalVarTable)arg).mv;

		IIdentifier id = n.getName();
		String name = id.getName();	
		int slot = id.getSlot();
		
		
		
		if(n.getType().isInt()) {
			//CodeGenUtils.genDebugPrintTOS(mv, n.getType());
			
			mv.visitFieldInsn(GETSTATIC, className, name, "I");
			System.out.println("This static");
			
		}
		else if(n.getType().isBoolean()) {
			mv.visitFieldInsn(GETSTATIC, className, name, "Z");
		}
		else if(n.getType().isString()){
			//mv.visitVarInsn(ALOAD, id.getSlot());
			mv.visitFieldInsn(GETSTATIC, className, name, stringDesc);
		}

		//System.out.println("name: " + name);
		//System.out.println("slot: " + slot);
		//CodeGenUtils.genDebugPrintTOS(mv, n.getType());
		
		return null;
		//throw new UnsupportedOperationException("TO IMPLEMENT");
	}

	@Override
	public Object visitIIdentifier(IIdentifier n, Object arg) throws Exception {
		System.out.println("METHOD: IDENT");
		MethodVisitor mv = ((MethodVisitorLocalVarTable)arg).mv;
		
		String varName = n.getName();
		
		List<LocalVarInfo> locals = ((MethodVisitorLocalVarTable)arg).localVars;

		for (int i = 0; i < locals.size(); i++)
		{
			if (varName.equals(locals.get(i).name))
			{
				n.setSlot(i);
				n.setLocal(true);			
			}
		}

		return null;

		//throw new UnsupportedOperationException("TO IMPLEMENT");
	}

	@Override
	public Object visitIIfStatement(IIfStatement n, Object arg) throws Exception {	
		MethodVisitor mv = ((MethodVisitorLocalVarTable)arg).mv;
		
		IExpression gaurd = n.getGuardExpression();
		gaurd.visit(this, arg);	
		IBlock block = n.getBlock();
		block.visit(this, arg);
		
//		Label start = new Label();
//		mv.visitJumpInsn(IFEQ, start);
//		
//		
//		mv.visitLabel(start);

		return null;	
		//throw new UnsupportedOperationException("TO IMPLEMENT");
	}

	@Override
	public Object visitIImmutableGlobal(IImmutableGlobal n, Object arg) throws Exception {
		System.out.println("METHOD: IMMUTABLE GLOB");
		MethodVisitor mv = ((MethodVisitorLocalVarTable)arg).mv;				
		INameDef nameDef = n.getVarDef();
		String varName = nameDef.getIdent().getName();
		String typeDesc = nameDef.getType().getDesc();
		FieldVisitor fieldVisitor = cw.visitField(ACC_PUBLIC | ACC_STATIC | ACC_FINAL, varName, typeDesc, null, null);
		fieldVisitor.visitEnd();
		//generate code to initialize field.  
		IExpression e = n.getExpression();
		e.visit(this, arg);  //generate code to leave value of expression on top of stack
		mv.visitFieldInsn(PUTSTATIC, className, varName, typeDesc);	
		//System.out.println("GOT TO END OF visitIImmutableGlobal - GIVEN");
		return null;
	}
	
	@Override
	public Object visitIMutableGlobal(IMutableGlobal n, Object arg) throws Exception {
		System.out.println("METHOD: MUTABLE GLOB");
		MethodVisitor mv = ((MethodVisitorLocalVarTable)arg).mv;				
		INameDef nameDef = n.getVarDef();
		String varName = nameDef.getIdent().getName();
		String typeDesc = nameDef.getType().getDesc();	
		
		System.out.println("typeDesc: "+  typeDesc);
		
		FieldVisitor fieldVisitor = cw.visitField(ACC_PUBLIC | ACC_STATIC | ACC_FINAL, varName, typeDesc, null, null);
		fieldVisitor.visitEnd();
		//generate code to initialize field.  
		IExpression e = n.getExpression();
					
		if(e != null) {
			e.visit(this, arg);  //generate code to leave value of expression on top of stack		
			mv.visitFieldInsn(PUTSTATIC, className, varName, typeDesc);	
						
		}	
		
		
		return null;
		
	}

	@Override
	public Object visitIIntLiteralExpression(IIntLiteralExpression n, Object arg) throws Exception {
		System.out.println("METHOD: INT LIT EXP");
		MethodVisitor mv = ((MethodVisitorLocalVarTable)arg).mv;	
		mv.visitLdcInsn(n.getValue());
		return null;
	}

	@Override
	public Object visitILetStatement(ILetStatement n, Object arg) throws Exception {	
		System.out.println("METHOD: LET STATEMENT");
		//Local var table
		List<LocalVarInfo> localVars = new ArrayList<LocalVarInfo>();
		
		//get localDef
		INameDef localDef = n.getLocalDef();
		
		String desc = localDef.getType().getDesc();
		
		localDef.getIdent().setSlot(localVars.size());
		localVars.add(new LocalVarInfo(localDef.getIdent().getName(), desc, null, null));
		
		IExpression e = n.getExpression();
		
		
		// get method visitor
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, localDef.getIdent().getName(), desc, null, null);
		// initialize
		mv.visitCode();
		// mark beginning of instructions for method
		Label funcStart = new Label();
		mv.visitLabel(funcStart);
		MethodVisitorLocalVarTable context = new MethodVisitorLocalVarTable(mv, localVars);
		if(e != null) {
			e.visit(this, context);
		}
		//visit block to generate code for statements
		n.getBlock().visit(this, context);
			
		//add label after last instruction
		Label funcEnd = new Label();
		mv.visitLabel(funcEnd);
		

		mv.visitMaxs(0, 0);
		
		//terminate construction of method
		mv.visitEnd();
		
		//System.out.println("GOT TO END OF visitLetStatement");
		return null;
		//throw new UnsupportedOperationException("TO IMPLEMENT");
	}
		


	@Override
	public Object visitIListSelectorExpression(IListSelectorExpression n, Object arg) throws Exception {
		throw new UnsupportedOperationException("SKIP THIS");
	}

	@Override
	public Object visitIListType(IListType n, Object arg) throws Exception {
		throw new UnsupportedOperationException("SKIP THIS!!");
	}

	@Override
	public Object visitINameDef(INameDef n, Object arg) throws Exception {
		IIdentifier id = n.getIdent();
		String name = id.getName();
				
		IType t = n.getType();
		if (n.getIdent().getLocal())
		{
			List<LocalVarInfo> localVars = ((MethodVisitorLocalVarTable)arg).localVars;
			id.setSlot(localVars.size());
			localVars.add(new LocalVarInfo(name, t.getDesc(), null, null));

		}

		return null;
		
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitINilConstantExpression(INilConstantExpression n, Object arg) throws Exception {
		throw new UnsupportedOperationException("SKIP THIS");
	}

	

	@Override
	public Object visitIReturnStatement(IReturnStatement n, Object arg) throws Exception {
		System.out.println("METHOD: RETURN STATEMENT");
		//get the method visitor from the arg
		MethodVisitor mv = ((MethodVisitorLocalVarTable)arg).mv;
		IExpression e = n.getExpression();
		if (e != null) {  //the return statement has an expression
			e.visit(this, arg);  //generate code to leave value of expression on top of stack.
			//use type of expression to determine which return instruction to use
			IType type = e.getType();
			if (type.isInt() || type.isBoolean()) {mv.visitInsn(IRETURN);}
			else  {mv.visitInsn(ARETURN);}
		}
		else { //there is no argument, (and we have verified duirng type checking that function has void return type) so use this return statement.  
			mv.visitInsn(RETURN);
		}
		//System.out.println("GOT TO END OF visitIReturnStatement - GIVEN");
		return null;
	}

	@Override
	//DONE I THINK
	public Object visitIStringLiteralExpression(IStringLiteralExpression n, Object arg) throws Exception {
		System.out.println("METHOD: STRING LIT EXPR");
		MethodVisitor mv = ((MethodVisitorLocalVarTable)arg).mv;
		
		System.out.println("STRING: " + n.toString());
		mv.visitLdcInsn(n.getValue());
		return null;
		//throw new UnsupportedOperationException("TO IMPLEMENT");
	}

	@Override
	public Object visitISwitchStatement(ISwitchStatement n, Object arg) throws Exception {
		throw new UnsupportedOperationException("SKIP THIS");

	}

	@Override
	//DONE I THINK
	public Object visitIUnaryExpression(IUnaryExpression n, Object arg) throws Exception {
		System.out.println("METHOD: UNARY EXPR");
		//get method visitor from arg
		MethodVisitor mv = ((MethodVisitorLocalVarTable) arg).mv();
		//generate code to leave value of expression on top of stack
		n.getExpression().visit(this, arg);
		//get the operator and types of operand and result
		Kind op = n.getOp();
		IType resultType = n.getType();
		IType operandType = n.getExpression().getType();
		switch(op) {
		case MINUS -> {
			if (operandType.isInt()) {
				//mv.visitInsn(INEG);
				mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "minus", "(I)I",false);
			}
			//throw new UnsupportedOperationException("IMPLEMENT unary minus");
		}
		case BANG -> {
			if (operandType.isBoolean()) {
				//this is complicated.  Use a Java method instead
//				Label brLabel = new Label();
//				Label after = new Label();
//				mv.visitJumpInsn(IFEQ,brLabel);
//				mv.visitLdcInsn(0);
//				mv.visitJumpInsn(GOTO,after);
//				mv.visitLabel(brLabel);
//				mv.visitLdcInsn(1);
//				mv.visitLabel(after);
				mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "not", "(Z)Z",false);
			}
			else { //argument is List
				throw new UnsupportedOperationException("SKIP THIS");
		}
		}
		default -> throw new UnsupportedOperationException("compiler error");
		}
		return null;
	}

	@Override
	public Object visitIWhileStatement(IWhileStatement n, Object arg) throws Exception {
		System.out.println("METHOD: WHILE STATEMENT");
		MethodVisitor mv = ((MethodVisitorLocalVarTable) arg).mv();
		n.getGuardExpression().visit(this, arg);
		n.getBlock().visit(this, arg);
		
		
		return null;
		//throw new UnsupportedOperationException("TO IMPLEMENT");
	}


	

	@Override
	public Object visitIPrimitiveType(IPrimitiveType n, Object arg) throws Exception {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}


	@Override
	public Object visitIAssignmentStatement(IAssignmentStatement n, Object arg) throws Exception {
		System.out.println("METHOD: ASSIGN STATMENT");
		MethodVisitor mv = ((MethodVisitorLocalVarTable) arg).mv();

		IExpression left = n.getLeft();
		IExpression right = n.getRight();
		
		right.visit(this, arg);
		left.visit(this, arg);
		
		//CodeGenUtils.genDebugPrintTOS(mv, left.getType());
		
		if(left instanceof IIdentExpression) {
			IIdentifier id = ((IIdentExpression) left).getName();
			//mv.visitFieldInsn(PUTSTATIC, className, id.getName(), "I");
//			IDeclaration dec = id.getDec();
//			dec.visit(this, arg);
			
			//mv.visitVarInsn(ALOAD,id.getSlot());
		}

		
		
		return null;
		//throw new UnsupportedOperationException("TO IMPLEMENT");

	}

	@Override
	public Object visitIExpressionStatement(IExpressionStatement n, Object arg) throws Exception {
		throw new UnsupportedOperationException("TO IMPLEMENT");
	}
}
