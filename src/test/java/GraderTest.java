import org.junit.Test;

import java.io.*;
import java.util.HashMap;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;


public class GraderTest
{

	@Test
	public void testGrade() throws IOException
	{
		File testSrc = new File(System.getProperty("java.io.tmpdir") + "/TestSrc.java");
		BufferedWriter testSrcOut = new BufferedWriter(new FileWriter(testSrc));
		testSrcOut.write("import java.util.Scanner;\n" +
				"\n" +
				"public class TestSrc\n" +
				"{\n" +
				"\tpublic static void main(String[] args)\n" +
				"\t{\n" +
				"\t\tScanner in = new Scanner(System.in);\n" +
				"\t\tfor (int i = 0; i < 4; i++)\n" +
				"\t\t{\n" +
				"\t\t\tSystem.out.println(in.next());\n" +
				"\t\t}\n" +
				"\t}\n" +
				"}\n");
		testSrcOut.close();
		testSrc.deleteOnExit();

		File testIn = File.createTempFile("TestInput", "txt");
		BufferedWriter testInOut = new BufferedWriter(new FileWriter(testIn));
		testInOut.write("1\n2\n3\n4");
		testInOut.close();
		testIn.deleteOnExit();


		File testOut = File.createTempFile("TestOutput", "txt");
		testSrc.deleteOnExit();
		BufferedWriter testOutOut = new BufferedWriter(new FileWriter(testOut));
		testOutOut.write("1\n2\n3\n4");
		testOutOut.close();
		testOut.deleteOnExit();

		HashMap<String, Integer> testSearchStrings = new HashMap<>();
		testSearchStrings.put("for *\\(.*\\)", 1);
		testSearchStrings.put("while *\\(.*\\)", 2);
		Either<Integer, String> result = Grader.grade(testSrc, testIn, testOut, false, false, testSearchStrings);
		//assertEquals(98, result.getLeft().intValue());
		//assertEquals(null, result.getRight());
	}

	@Test
	public void testSetRawSearchString()
	{
		GradeBot test = GradeBot.getInstance();
		test.setSearchStrings(new HashMap<>());
		test.addRawSearchString("X", 0);
	}

	@Test
	public void testCompErrGrade() throws IOException
	{
		File testSrc = new File("/home/dominik/TestSrc.java");
		BufferedWriter testSrcOut = new BufferedWriter(new FileWriter(testSrc));
		testSrcOut.write("import java.utilcanner;\n" +
				"\n" +
				"puic cls TestSrc\n" +
				"{\n" +
				"\tpuic stic void mn(String[] args)\n" +
				"\t{\n" +
				"\t\tSnner in = new Scanner(System.in);\n" +
				"\t\tfor (int i = 0; i < 4; i++)\n" +
				"\t\t{\n" +
				"\t\t\tStem.out.println(in.next());\n" +
				"\t\t}\n" +
				"\t}\n" +
				"}\n");
		testSrcOut.close();
		testSrc.deleteOnExit();

		File testIn = File.createTempFile("TestInput", "txt");
		BufferedWriter testInOut = new BufferedWriter(new FileWriter(testIn));
		testInOut.write("1\n2\n3\n4");
		testInOut.close();
		testIn.deleteOnExit();


		File testOut = File.createTempFile("TestOutput", "txt");
		testSrc.deleteOnExit();
		BufferedWriter testOutOut = new BufferedWriter(new FileWriter(testOut));
		testOutOut.write("1\n2\n3\n4");
		testOutOut.close();
		testOut.deleteOnExit();

		HashMap<String, Integer> testSearchStrings = new HashMap<>();
		testSearchStrings.put("for *\\(.*\\)", 1);
		testSearchStrings.put("while *\\(.*\\)", 2);

		//turns off stderr for duration of test so that we don't get any unwanted stack traces when testing
		PrintStream err = System.err;
		System.setErr(new PrintStream(new OutputStream() {@Override	public void write(int i) throws IOException	{}}));

		Either<Integer, String> result = Grader.grade(testSrc, testIn, testOut, false, false, testSearchStrings);
		System.setErr(err);
		assertTrue(result.getRight().contains("java.lang.IllegalArgumentException"));
	}

		@Test
	public void testGradeBot() throws Exception
	{
		File testSrc = new File("/home/dominik/test.java");
		BufferedWriter testSrcOut = new BufferedWriter(new FileWriter(testSrc));
		testSrcOut.write("public class test\n" +
				"{\n" +
				"\tpublic static void main(String... args)\n" +
				"\t{\n" +
				"\t\tfor (int i = 1; i < 11; i++)\n" +
				"\t\t{\n" +
				"\t\t\tSystem.out.println(i);\n" +
				"\t\t}\n" +
				"\t}\n" +
				"}");
		testSrcOut.close();

		File testIn = File.createTempFile("TestInput", "txt");


		File testOut = File.createTempFile("TestOutput", "txt");
		testSrc.deleteOnExit();
		BufferedWriter testOutOut = new BufferedWriter(new FileWriter(testOut));
		testOutOut.write("1\n" + "2\n" + "3\n" + "4\n" + "5\n" + "6\n" + "7\n" + "8\n" + "9\n" + "10\n");
		testOutOut.close();

		HashMap<String, Integer> testSearchStrings = new HashMap<>();
		testSearchStrings.put("for_(int VAR1 = 1; VAR1 < 11; VAR1++)", 1);
		testSearchStrings.put("while_(*VAR*)", 2);

		GradeBot AITA = GradeBot.getInstance();
		AITA.setSourceCode(new File[]{testSrc});
		AITA.setInputFile(testIn);
		AITA.setCorrectOutputFile(testOut);
		AITA.setIgnoreWhiteSpace(false);
		AITA.setIgnoreSymbolCharacters(false);
		AITA.setSearchStrings(testSearchStrings);

//		AITA.grade().forEach((String x, String y) -> assertEquals("98", y));
	}

	@Test
	public void testGradeWithNullCheck() throws Exception
	{
		File testSrc = new File("/home/dominik/TestSrc.java");
		BufferedWriter testSrcOut = new BufferedWriter(new FileWriter(testSrc));
		testSrcOut.write("public class TestSrc\n" +
				"{\n" +
				"\tpublic static void main(String... args)\n" +
				"\t{\n" +
				"\t\tfor (int i = 1; i < 11; i++)\n" +
				"\t\t{\n" +
				"\t\t\tSystem.out.println(i);\n" +
				"\t\t}\n" +
				"\t}\n" +
				"}");
		testSrcOut.close();

		File testOut = File.createTempFile("TestOutput", "txt");
		testSrc.deleteOnExit();
		BufferedWriter testOutOut = new BufferedWriter(new FileWriter(testOut));
		testOutOut.write("1\n" + "2\n" + "3\n" + "4\n" + "5\n" + "6\n" + "7\n" + "8\n" + "9\n" + "10\n");
		testOutOut.close();

		HashMap<String, Integer> testSearchStrings = new HashMap<>();
		testSearchStrings.put("for *\\(.*\\)", 1);
		testSearchStrings.put("while *\\(.*\\)", 2);

		Either<Integer, String> result = Grader.grade(testSrc, null, testOut, false, false, testSearchStrings);
//		assertEquals(98, result.getLeft().intValue());
//		assertEquals(null, result.getRight());
	}

	@Test
	public void testGradeBotWithNullCheck() throws Exception
	{
		File testSrc = new File("/home/dominik/TestSrc.java");
		BufferedWriter testSrcOut = new BufferedWriter(new FileWriter(testSrc));
		testSrcOut.write("public class TestSrc\n" +
				"{\n" +
				"\tpublic static void main(String... args)\n" +
				"\t{\n" +
				"\t\tfor (int i = 1; i < 11; i++)\n" +
				"\t\t{\n" +
				"\t\t\tSystem.out.println(i);\n" +
				"\t\t}\n" +
				"\t}\n" +
				"}");
		testSrcOut.close();

		File testOut = File.createTempFile("TestOutput", "txt");
		testSrc.deleteOnExit();
		BufferedWriter testOutOut = new BufferedWriter(new FileWriter(testOut));
		testOutOut.write("1\n" + "2\n" + "3\n" + "4\n" + "5\n" + "6\n" + "7\n" + "8\n" + "9\n" + "10\n");
		testOutOut.close();

		HashMap<String, Integer> testSearchStrings = new HashMap<>();
		testSearchStrings.put("for_(int VAR1 = 1; VAR1 < 11; VAR1++)", 1);
		testSearchStrings.put("while_(*VAR*)", 2);

		GradeBot AITA = GradeBot.getInstance();
		AITA.setSourceCode(new File[]{testSrc});
		AITA.setCorrectOutputFile(testOut);
		AITA.setIgnoreWhiteSpace(false);
		AITA.setIgnoreSymbolCharacters(false);
		AITA.setSearchStrings(testSearchStrings);

	//	AITA.grade().forEach((String x, String y) -> assertEquals("98", y));
	}


}
