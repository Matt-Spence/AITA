import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;


public class GradeBot
{

	private static final GradeBot instance = new GradeBot();

	private File[] sourceCode;
	private File inputFile;
	private File correctOutputFile;
	private boolean ignoreWhiteSpace;
	private boolean ignoreSymbolCharacters;
	private HashMap<String, Integer> searchStrings;
	private PrintStream logOut;
	private GradeBot()
	{
		sourceCode = null;
		inputFile = null;
		ignoreWhiteSpace = false;
		ignoreSymbolCharacters = false;
		searchStrings = null;
		try
		{
			logOut = new PrintStream(new FileOutputStream("GradeBotLog"), true);
		} catch (FileNotFoundException FNF)
		{
			try
			{
				Files.write(Paths.get("GradeBotLog"), new byte[]{0});
				logOut = new PrintStream(new FileOutputStream("GradeBotLog"), true);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return The GradeBotInstance
	 */
	public static GradeBot getInstance()
	{
		return instance;
	}

	/**
	 * @param sourceCode The array of File objects with source code to be graded
	 */
	public void setSourceCode(File[] sourceCode)
	{
		this.sourceCode = sourceCode;
	}

	/**
	 * @param inputFile The File with the input for the code to be graded
	 */
	public void setInputFile(File inputFile)
	{
		this.inputFile = inputFile;
	}

	/**
	 * @param correctOutputFile The File with the correct output
	 */
	public void setCorrectOutputFile(File correctOutputFile)
	{
		this.correctOutputFile = correctOutputFile;
	}

	/**
	 * @param ignoreWhiteSpace True means whitespace will be ignored for grading purposes
	 */
	public void setIgnoreWhiteSpace(boolean ignoreWhiteSpace)
	{
		this.ignoreWhiteSpace = ignoreWhiteSpace;
	}

	/**
	 * @param ignoreSymbolCharacters True means non-alphanumeric characters will be ignored for grading purposes
	 */
	public void setIgnoreSymbolCharacters(boolean ignoreSymbolCharacters)
	{
		this.ignoreSymbolCharacters = ignoreSymbolCharacters;
	}

	/**
	 * @param searchStrings The HashMap of regex strings to match the source code against and their point value
	 */
	public void setSearchStrings(HashMap<String, Integer> searchStrings)
	{
		HashMap<String, Integer> realOnes = new HashMap<>();
		searchStrings.forEach((String simpleIn, Integer y) ->
		{
			String regexed = simpleIn.replace("+", "\\+").replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)").replaceAll("\\*", ".*").replaceAll("#", "\\d+").replaceAll("\\|\\|", "|").replaceAll("_", "[\\\\s\\\\n\\\\r]*");
			for (int i = 0; i < 10; i++)
			{
				regexed = regexed.replaceFirst("VAR" + i, "(VAR)");
				regexed = regexed.replaceAll("VAR" + i, "\\\\" + i);
			}
			regexed = regexed.replaceAll("VAR", "[a-zA-Z][a-z0-9]*");
			realOnes.put(regexed, y);
		});

		this.searchStrings = realOnes;
	}

	/**
	 * @param regex The regex string to match the source code against point value
	 */
	public void addRawSearchString(String regex, int value)
	{
		this.searchStrings.put(regex, value);
	}


	/**
	 * Does the actual work of grading the files
	 * @return A HashMap mapping each file's path to either their score or the error
	 */
	public LinkedList<Result> grade()
	{
		LinkedList<Result> grades = new LinkedList<>();
		for (File currentFile : sourceCode)
		{
			grades.add(Grader.grade(currentFile, inputFile, correctOutputFile, ignoreWhiteSpace, ignoreSymbolCharacters, searchStrings));
		}
		return grades;
	}

}
