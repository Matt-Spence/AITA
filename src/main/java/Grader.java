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
	/**
	 * Does the actual act of grading.
	 * @param currentFile the file that will be compiled and graded
	 * @param inputFile the file that will be used as input
	 * @param correctOutputFile the file that the program's
	 * @param ignoreWhiteSpace if true it will ignore all whitespace, if false it won't
	 * @param ignoreSymbolCharacters if true it will ignore all non-alphanumeric, if false it won't
	 * @param searchStrings every regex string to compare against as a key, and it's score value
	 */
	static Result grade(File currentFile, File inputFile, File correctOutputFile, boolean ignoreWhiteSpace, boolean ignoreSymbolCharacters, List<searchString> searchStrings)
	{
		
		List<String> missedRegex = new LinkedList<>();
		
		StringBuilder sourceCodeBuild = new StringBuilder();
		String sourceCode;
		try
		{
			sourceCode = Normalizer.normalize(currentFile);
			Scanner sourceReader = new Scanner(currentFile);
			while (sourceReader.hasNextLine())
			{
				sourceCodeBuild.append(sourceReader.nextLine());
				sourceCodeBuild.append("\n");
			}
		}
		catch (FileNotFoundException e)
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


			String className = currentFile.getName().replaceFirst("[.][^.]+$", "");
			Class<?> currentCodeToBeGraded;
			Method mainMethod;
			try
			{

				JavaCompiler comp = ToolProvider.getSystemJavaCompiler();
				DiagnosticCollector<JavaFileObject> diag = new DiagnosticCollector<>();
				StandardJavaFileManager fm = comp.getStandardFileManager(diag, null, null);
				Iterable<? extends JavaFileObject> compU = fm.getJavaFileObjects(currentFile);
				JavaCompiler.CompilationTask task = comp.getTask(new OutputStreamWriter(System.out), fm, diag, null, null, compU);
				task.call();
				fm.close();
				currentCodeToBeGraded = new FileClassLoader(currentFile.getParentFile()).loadClass(className);
				mainMethod = currentCodeToBeGraded.getMethod("main", (new String[0]).getClass());
			}
			catch (Error error)
			{
				log.error("{}", error.toString());
				return new Result(currentFile.getAbsolutePath(), "Compilation error", sourceCodeBuild.toString(), error.toString(),"No Output" );
			}


			InputStream in;
			if (inputFile != null)
			{
				in = new FileInputStream(inputFile);
			}
			else
			{
				in = sin;
			}


			System.setIn(in);

			//problem somewhere between here
			System.setOut(out);

			mainMethod.invoke(currentCodeToBeGraded, (Object) new String[]{});
			String actualResult = stream.toString(Charset.defaultCharset().toString());
			System.setOut(sout);
			System.setIn(sin);
			String modifiedResult = new String(actualResult);
			String expectedResult;
			{
				Scanner tmpOut = new Scanner(correctOutputFile);
				tmpOut.useDelimiter("\\z");
				expectedResult = tmpOut.next();
			}
			//and here

			if (ignoreWhiteSpace)
			{
				System.err.println("Ignoring White Space");
				StringBuilder whitespaceStripper = new StringBuilder();
				for (char x : modifiedResult.toCharArray())
				{
					if (!Character.isWhitespace(x))
					{ whitespaceStripper.append(x); }
				}
				modifiedResult = whitespaceStripper.toString();

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
				System.err.println("Ignoring Symbols");
				StringBuilder symbolStripper = new StringBuilder();
				for (char x : modifiedResult.toCharArray())
				{
					if (Character.isWhitespace(x) || Character.isLetterOrDigit(x))
					{ symbolStripper.append(x); }
				}
				modifiedResult = symbolStripper.toString();

				StringBuilder expectedSymbolStripper = new StringBuilder();
				for (char x : expectedResult.toCharArray())
				{
					if (Character.isWhitespace(x) || Character.isLetterOrDigit(x))
					{ expectedSymbolStripper.append(x); }
				}
				expectedResult = expectedSymbolStripper.toString();
			}

			for (searchString x : searchStrings) {
				String pattern = "^[\\W\\w]*" + x.getRegex() + "[\\W\\w]*$";
				Integer value = x.getValue();
				if (!sourceCode.matches(pattern)) {
					missedRegex.add(x.getOrig());
					score -= value;
				}
			}
			if (missedRegex.size() > 0) {
				StringBuilder missed = new StringBuilder();
				missedRegex.forEach((x) -> {
					missed.append(x);
					missed.append('\n');
				});
				if (!modifiedResult.equals(expectedResult)) {
					System.err.printf("expected:%n%s%n%nactual:%n%s", expectedResult, actualResult);
					return new Result(currentFile.getAbsolutePath(), "Incorrect Output", sourceCodeBuild.toString(), missed.toString(), actualResult);

				} else {
					return new Result(currentFile.getAbsolutePath(), new Integer(score).toString(), sourceCodeBuild.toString(), missed.toString(), actualResult);
				}
			}
			else {
				if (!modifiedResult.equals(expectedResult)) {
					System.err.printf("expected:%n%s%n%nactual:%n%s", expectedResult, actualResult);
					return new Result(currentFile.getAbsolutePath(), "Incorrect Output", sourceCodeBuild.toString(), "No error, no missed", actualResult);

				} else {
					return new Result(currentFile.getAbsolutePath(), new Integer(score).toString(), sourceCodeBuild.toString(), "No error, no missed", actualResult);
				}
			}

		} catch (Exception e)
		{
			log.error("{}", e.toString());
			e.printStackTrace();
			return new Result(currentFile.getAbsolutePath(), "Runtime error" , sourceCodeBuild.toString(), e.toString(), "No output" );
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

	private static class FileClassLoader extends ClassLoader
	{
		File root;

		FileClassLoader(File root)
		{
			this.root = root;
		}

		public Class findClass(String name)
		{
			byte[] b = loadClassData(name);
			return defineClass(name, b, 0, b.length);
		}

		private byte[] loadClassData(String name)
		{
			ArrayList<Byte> bytes = new ArrayList<>();
			File target = null;
			for (File cur : root.listFiles())
			{
				if (cur.getName().equals(name + ".class"))
				{
					target = cur;
					break;
				}
			}

			if(target == null) throw new ClassFileNotFoundError(name);
			try
			{
				FileInputStream in = new FileInputStream(target);
				int x = 0;
				while ((x = in.read()) != -1)
				{
					bytes.add((byte)x);
				}
				in.close();//cannot delete file if input stream is still open
				target.delete();

			} catch (IOException e)
			{
				e.printStackTrace();
			}
			Byte[] Bytes = bytes.toArray(new Byte[]{});
			byte[] b = new byte[Bytes.length];
			for (int i = 0; i < Bytes.length; i++)
			{
				b[i] = Bytes[i].byteValue();
			}
			return b;
		}
	}

	private static class ClassFileNotFoundError extends Error{
		//More specific error than NullPointerException

		ClassFileNotFoundError(){
			super(".class file not found");
		}

		ClassFileNotFoundError(String str){
			super(str+".class file not found");
		}
	}

}




