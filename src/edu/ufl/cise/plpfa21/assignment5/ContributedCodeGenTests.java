package edu.ufl.cise.plpfa21.assignment5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import edu.ufl.cise.plpfa21.assignment1.CompilerComponentFactory;
import edu.ufl.cise.plpfa21.assignment3.ast.IASTNode;
import edu.ufl.cise.plpfa21.assignment5.CodeGenUtils.DynamicClassLoader;

public class ContributedCodeGenTests extends CodeGenTests{
	
	@DisplayName("contributedTest0")
	@Test
	public void contributedTest0(TestInfo testInfo) throws Exception {
		String input = """
				VAR x = TRUE;
				VAR y = (TRUE && !x);
				VAR z = (!FALSE && x);
				VAR b = !(x && FALSE);
				VAR c = (FALSE && !b);
				""";
		byte[] bytecode = compile(input, className, packageName);
		show(CodeGenUtils.bytecodeToString(bytecode));
		Class<?> testClass = getClass(bytecode, className);
		assertEquals(false, getBoolean(testClass, "y"));
		assertEquals(true, getBoolean(testClass, "z"));
		assertEquals(true, getBoolean(testClass, "b"));
		assertEquals(false, getBoolean(testClass, "c"));
	}
	
	@DisplayName("contributedTest1")
	@Test
	public void contributedTest1(TestInfo testInfo) throws Exception {
		String input = """
				VAL x = 33;
				VAL y = 67;
				VAR z = x + y;
				FUN f():INT
				DO
				   RETURN z - 50;
				END
				""";
		byte[] bytecode = compile(input, className, packageName);
		show(CodeGenUtils.bytecodeToString(bytecode));
		int z = (int) loadClassAndRunMethod(bytecode, className, "f", null);
		assertEquals(50, z);
	}
	
	@DisplayName("contributedTest2")
	@Test
	public void contributedTest2(TestInfo testInfo) throws Exception {
		String input = """
				VAL x = 33;
				VAL y = 67;
				VAR z = x + y;
				FUN f():INT
				DO
				   RETURN -z * 2;
				END
				""";
		byte[] bytecode = compile(input, className, packageName);
		show(CodeGenUtils.bytecodeToString(bytecode));
		int z = (int) loadClassAndRunMethod(bytecode, className, "f", null);
		assertEquals(-200, z);
	}
	
	@DisplayName("contributedTest3")
	@Test
	public void contributedTest3(TestInfo testInfo) throws Exception {
		String input = """
				VAL x = "guten Tag"; VAR y:STRING = " adios";
				FUN f():STRING
				DO
				   RETURN x + y;
				END
				""";
		byte[] bytecode = compile(input, className, packageName);
		show(CodeGenUtils.bytecodeToString(bytecode));
		String z = (String) loadClassAndRunMethod(bytecode, className, "f", null);
		assertEquals("guten Tag adios", z);
	}

}
