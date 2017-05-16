import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.*;
import java.io.*;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.*;


class Grader
{
	static Logger log = LoggerFactory.getLogger(Grader.class);
	static Result grade(File currentFile, File inputFile, File correctOutputFile, boolean ignoreWhiteSpace, boolean ignoreSymbolCharacters, HashMap<String, Integer> searchStrings)
	{
		StringBuilder sourceCodeBuild = new StringBuilder();
		try
		{
			Scanner sourceReader = new Scanner(currentFile);
			while (sourceReader.hasNextLine())
			{
				sourceCodeBuild.append(sourceReader.nextLine());
				sourceCodeBuild.append("\n");
			}
		} catch (FileNotFoundException e)
		{
			return new Result(currentFile.getAbsolutePath(), "<- That file wasn't found" , "No code", e.toString(), "No output" );
		}
		try
		{
			InputStream sin = System.in;
			PrintStream sout = System.out;

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			PrintStream out = new PrintStream(stream);

			Integer score = 100;

			String sourceCode = sourceCodeBuild.toString();
			String className = currentFile.getName().replaceFirst("[.][^.]+$", "");
			Class<?> currentCodeToBeGraded;
			Method mainMethod;
			try
			{
				JavaCompiler comp = ToolProvider.getSystemJavaCompiler();
				DiagnosticCollector<JavaFileObject> diag = new DiagnosticCollector<>();
				StandardJavaFileManager fm = comp.getStandardFileManager(diag, null, null);
				Iterable<? extends JavaFileObject> compU = fm.getJavaFileObjects(sourceCode);
				JavaCompiler.CompilationTask task = comp.getTask(null, fm, diag, null, null, compU);
				task.call();
				fm.close();
				currentCodeToBeGraded = ToolProvider.getSystemToolClassLoader().loadClass(className);
				mainMethod = currentCodeToBeGraded.getMethod("main", (new String[0]).getClass());
			} catch (Error error)
			{
				log.error("{}", error.toString());
				return new Result(currentFile.getAbsolutePath(), "Compilation error", sourceCode, error.toString(),"No Output" );
			}

			System.setOut(out);
			mainMethod.invoke(currentCodeToBeGraded, (Object) new String[]{});
			String actualResult = stream.toString(Charset.defaultCharset().toString());
			System.setOut(sout);
			System.setIn(sin);
			String expectedResult;
			{
				Scanner tmpOut = new Scanner(correctOutputFile);
				tmpOut.useDelimiter("\\z");
				expectedResult = tmpOut.next();
			}

			if (ignoreWhiteSpace)
			{
				StringBuilder whitespaceStripper = new StringBuilder();
				for (char x : actualResult.toCharArray())
				{
					if (!Character.isWhitespace(x))
					{ whitespaceStripper.append(x); }
				}
				actualResult = whitespaceStripper.toString();
				StringBuilder expectedWhitespaceStripper = new StringBuilder();
				for (char x : expectedResult.toCharArray())
				{
					if (!Character.isWhitespace(x))
					{ expectedWhitespaceStripper.append(x); }
				}
				expectedResult = expectedWhitespaceStripper.toString();
			}
			if (ignoreSymbolCharacters)
			{
				StringBuilder symbolStripper = new StringBuilder();
				for (char x : actualResult.toCharArray())
				{
					if (Character.isWhitespace(x) || Character.isLetterOrDigit(x))
					{ symbolStripper.append(x); }
				}
				actualResult = symbolStripper.toString();

				StringBuilder expectedSymbolStripper = new StringBuilder();
				for (char x : expectedResult.toCharArray())
				{
					if (Character.isWhitespace(x) || Character.isLetterOrDigit(x))
					{ expectedSymbolStripper.append(x); }
				}
				expectedResult = expectedSymbolStripper.toString();
			}

			for (Map.Entry<String, Integer> x : searchStrings.entrySet())
			{
				String pattern = "^[\\W\\w]*" + x.getKey() + "[\\W\\w]*$";
				Integer value = x.getValue();
				if (!sourceCode.matches(pattern))
				{

					score -= value;
				}
			}
			if (!actualResult.equals(expectedResult))
			{
				log.debug("expected:\n{}\n\n actual:\n{}", new Object[]{expectedResult, actualResult});
				return new Result(currentFile.getAbsolutePath(), new Integer(score).toString() , sourceCode, "No error", actualResult );

			} else
			{
				return new Result(currentFile.getAbsolutePath(), new Integer(score).toString() , sourceCode, "No error", actualResult );
			}

		} catch (Exception e)
		{
			log.error("{}", e.toString());
			return new Result(currentFile.getAbsolutePath(), "Runtime error" , sourceCodeBuild.toString(), "No error", "No output" );

		}

	}

	private static class StringsInputStream extends InputStream
	{
		Iterator<Byte> iterator;

		StringsInputStream(String s)
		{
			super();
			ArrayList<Byte> bytes = new ArrayList<>();
			ArrayList<Character> characters = new ArrayList<Character>();
			for (char c : s.toCharArray())
			{
				characters.add(c);
			}
			characters.iterator().forEachRemaining((character ->
			{
				bytes.add((byte)((character.charValue() & 0xFF00)>>8));
				bytes.add((byte)((character.charValue() & 0x00FF)));
			}));
			iterator = bytes.iterator();
		}

		@Override
		public int read() throws IOException
		{
			if (iterator.hasNext())
			return iterator.next();
			return 0;
		}
	}
}




