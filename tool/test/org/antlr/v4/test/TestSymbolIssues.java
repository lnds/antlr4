package org.antlr.v4.test;

import org.antlr.v4.tool.LexerGrammar;
import org.junit.Test;

/** */
public class TestSymbolIssues extends BaseTest {
    static String[] A = {
        // INPUT
        "grammar A;\n" +
        "options { opt='sss'; k=3; }\n" +
        "\n" +
        "@members {foo}\n" +
        "@members {bar}\n" +
        "@lexer::header {package jj;}\n" +
        "@lexer::header {package kk;}\n" +
        "\n" +
        "a[int i] returns [foo f] : X ID a[3] b[34] q ;\n" +
        "b returns [int g] : Y 'y' 'if' a ;\n" +
        "a : FJKD ;\n" +
        "\n" +
        "ID : 'a'..'z'+ ID ;",
        // YIELDS
			"error(94): A.g4:5:1: redefinition of members action\n" +
			"error(94): A.g4:7:1: redefinition of header action\n" +
			"warning(83): A.g4:2:10: illegal option opt\n" +
			"warning(83): A.g4:2:21: illegal option k\n" +
			"error(94): A.g4:5:1: redefinition of members action\n" +
			"warning(125): A.g4:9:27: implicit definition of token X in parser\n" +
			"warning(125): A.g4:10:20: implicit definition of token Y in parser\n" +
			"warning(125): A.g4:11:4: implicit definition of token FJKD in parser\n" +
			"error(80): A.g4:9:32: rule a has no defined parameters\n" +
			"error(80): A.g4:9:37: rule b has no defined parameters\n" +
			"error(56): A.g4:9:43: reference to undefined rule: q\n"
    };

    static String[] B = {
        // INPUT
        "parser grammar B;\n" +
        "tokens { ID, FOO, X, Y }\n" +
        "\n" +
        "a : s=ID b+=ID X=ID '.' ;\n" +
        "\n" +
        "b : x=ID x+=ID ;\n" +
        "\n" +
        "s : FOO ;",
        // YIELDS
		"error(69): B.g4:4:4: label s conflicts with rule with same name\n" +
		"error(69): B.g4:4:9: label b conflicts with rule with same name\n" +
		"error(70): B.g4:4:15: label X conflicts with token with same name\n" +
		"error(75): B.g4:6:9: label x type mismatch with previous definition: TOKEN_LIST_LABEL!=TOKEN_LABEL\n" +
		"error(126): B.g4:4:20: cannot create implicit token for string literal '.' in non-combined grammar\n"
    };

    static String[] D = {
        // INPUT
        "parser grammar D;\n" +
		"tokens{ID}\n" +
        "a[int j] \n" +
        "        :       i=ID j=ID ;\n" +
        "\n" +
        "b[int i] returns [int i] : ID ;\n" +
        "\n" +
        "c[int i] returns [String k]\n" +
        "        :       ID ;",

        // YIELDS
        "error(72): D.g4:4:21: label j conflicts with rule a's return value or parameter with same name\n" +
		"error(76): D.g4:6:0: rule b's argument i conflicts a return value with same name\n"
    };

	static String[] E = {
		// INPUT
		"grammar E;\n" +
		"tokens {\n" +
		"	A, A,\n" +
		"	B,\n" +
		"	C\n" +
		"}\n" +
		"a : A ;\n",

		// YIELDS
		"warning(108): E.g4:3:4: token name A is already defined\n"
	};

    @Test public void testA() { super.testErrors(A, false); }
    @Test public void testB() { super.testErrors(B, false); }
	@Test public void testD() { super.testErrors(D, false); }
	@Test public void testE() { super.testErrors(E, false); }

	@Test public void testStringLiteralRedefs() throws Exception {
		String grammar =
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"mode X;\n"+
			"B : 'a' ;\n"+
			"mode Y;\n"+
			"C : 'a' ;\n";

		LexerGrammar g = new LexerGrammar(grammar);

		String expectedTokenIDToTypeMap = "{EOF=-1, A=1, B=2, C=3}";
		String expectedStringLiteralToTypeMap = "{}";
		String expectedTypeToTokenList = "[A, B, C]";

		assertEquals(expectedTokenIDToTypeMap, g.tokenNameToTypeMap.toString());
		assertEquals(expectedStringLiteralToTypeMap, g.stringLiteralToTypeMap.toString());
		assertEquals(expectedTypeToTokenList, realElements(g.typeToTokenList).toString());
	}
}
