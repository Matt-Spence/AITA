/**
 * Created by Matth_000 on 2/14/2017.
 */

import javafx.event.*;
import javafx.geometry.Insets;
import javafx.geometry.*;

import java.io.File;

import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;

import java.io.FileNotFoundException;
import java.util.*;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.*;
import javafx.scene.text.*;
import javafx.scene.image.*;


public class AITAGUI extends Application {
	private String helpText = "";
	// List of files to be read in
	private List<File> labs = new ArrayList<>(10);
	private int fileCount = 0;
	private File output;
	private static File inputFile;
	RadioButton whiteSpaceRB = new RadioButton("Ignore White Space");
	RadioButton symbolRB = new RadioButton("Ignore Symbols");


	TextField point3 = new TextField();
	ScrollPane searchStringScrollPane = new ScrollPane();
	OptionList optionList = new OptionList();
	Text outText = new Text();
	Text inText  = new Text();
	HBox inputField = new HBox();
	HBox outputField = new HBox();

	public void start(Stage primaryStage) {
		primaryStage.setTitle("AITA");

		searchStringScrollPane.setContent(optionList);
		searchStringScrollPane.setMaxHeight(160);
		searchStringScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		searchStringScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

		// create buttons
		Button inputData = new Button("Get Data File");
		inputData.setFont(Font.font("monospace", 16));
		inputData.setStyle("-fx-text-fill:#00ff00;-fx-background-color:#008800;-fx-background-radius:0px;");

		Button expectedOutput = new Button("Expected Output");
		expectedOutput.setFont(Font.font("monospace", 16));
		expectedOutput.setStyle("-fx-text-fill:#00ff00;-fx-background-color:#008800;-fx-background-radius:0px;");
		//create text field

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose Labs");

		//select data input file
		inputData.setOnAction((final ActionEvent e) -> {
			File file = fileChooser.showOpenDialog(primaryStage);
			if(file != null) {
				inputFile = file;
				inText.setText(file.getName());
			}
		});

		// select file representing expected output
		expectedOutput.setOnAction((final ActionEvent e) -> {
			File file = fileChooser.showOpenDialog(primaryStage);
			if(file != null){
				output = file;
				outText.setText(file.getName());
			}
		});

		// Create and build GUI
		HBox hb = new HBox();
		VBox vb = new VBox();

		//create file selector
		FileBrowser fb = new FileBrowser(this);
		hb.getChildren().add(fb);
		hb.getChildren().add(optionList);
		hb.getChildren().add(vb);

		// add buttons
		HBox button1Box = new HBox();
		HBox button2Box = new HBox();


		whiteSpaceRB.setStyle("-fx-text-fill:#ffffff;");
		whiteSpaceRB.setFont(Font.font("monospace", 16));
		button1Box.getChildren().addAll(whiteSpaceRB);

		symbolRB.setStyle("-fx-text-fill:#ffffff;");
		symbolRB.setFont(Font.font("monospace", 16));
		button2Box.getChildren().addAll(symbolRB);

		point3.setMaxWidth(50.0);
		vb.getChildren().add(button1Box);
		vb.getChildren().add(button2Box);

		outputField.getChildren().addAll(expectedOutput, outText);
		inputField.getChildren().addAll(inputData, inText);

		inputField.setSpacing(5);
		outputField.setSpacing(5);

		vb.getChildren().add(inputField);
		vb.getChildren().add(outputField);


		BorderPane bp = new BorderPane();
		bp.setCenter(hb);
		bp.setStyle("-fx-background-color:#000000;");

		hb.setSpacing(25);
		hb.setAlignment(Pos.CENTER);

		vb.setPadding(new Insets(25, 25, 25, 25));
		vb.setSpacing(5);

		Label topBanner = new Label("Hello Ms. Campbell!");
		topBanner.setFont(Font.font("monospace", 32));
		topBanner.setStyle("-fx-text-fill:#00ff00;");
		HBox toph = new HBox();
		toph.setAlignment(Pos.CENTER);
		toph.getChildren().add(topBanner);
		toph.setMinHeight(50);
		toph.setStyle("-fx-border-width:2px;-fx-border-style: solid;-fx-border-color:#00ff00;-fx-background-color:#008800;");

		VBox top = new VBox();
		top.getChildren().add(toph);
		top.setPadding(new Insets(0,0,20,0));

		Label bottomBanner = new Label(getEasterEgg());
		bottomBanner.setFont(Font.font("monospace", 32));
		bottomBanner.setStyle("-fx-text-fill:#00ff00;");
		HBox bottomh = new HBox();
		bottomh.setAlignment(Pos.CENTER);
		bottomh.getChildren().add(bottomBanner);
		bottomh.setMinHeight(50);
		bottomh.setStyle("-fx-border-width:2px;-fx-border-style: solid;-fx-border-color:#00ff00;-fx-background-color:#008800;");

		VBox bottom = new VBox();
		bottom.getChildren().add(bottomh);
		bottom.setPadding(new Insets(20,0,0,0));


		bp.setTop(top);
		bp.setBottom(bottom);


		Scene scene = new Scene(bp);
		scene.getStylesheets().add("style.css");
		primaryStage.setScene(scene);
		primaryStage.setMaximized(true);
		primaryStage.show();

	}

	public void submit(List<File> l){
		GradeBot AITA = GradeBot.getInstance();
		AITA.setSourceCode(toFileArray(l));
		AITA.setInputFile(inputFile);
		AITA.setCorrectOutputFile(output);
		AITA.setIgnoreWhiteSpace(whiteSpaceRB.isSelected());
		AITA.setIgnoreSymbolCharacters(symbolRB.isSelected());
		ArrayList<SearchString> options = optionList.getArrayList();
		System.out.println(options);
		HashMap<String, Integer> SearchStrings = new HashMap<>();
		for(SearchString s:options){
			SearchStrings.put(s.getRegex(),s.getValue());
		}
		System.out.println(SearchStrings);
		AITA.setSearchStrings(SearchStrings);//hashmap of regex to search for; point value of that regex

		LinkedList<Result> hm = AITA.grade();
		displayResults(hm);
	}

	public static File[] toFileArray(List<File> l){
		File[] toReturn = new File[l.size()];
		for(int i = 0; i < l.size(); i++){
			if(l.get(i) instanceof File){
				toReturn[i] = l.get(i);
			}
		}
		return toReturn;
	}

	public static void displayResults(List<Result> l) {
		Stage resultStage = new Stage();
		resultStage.setTitle("Results");

		Iterator it = l.iterator();

		VBox vb = new VBox();
		vb.setSpacing(10);


		HBox titles = new HBox();
		titles.setSpacing(10);
		Label pathTitle = new Label("Files");
		pathTitle.setMinWidth(480);
		pathTitle.setMaxWidth(480);
		Label resultTitle = new Label("Results");
		resultTitle.setMinWidth(240);
		resultTitle.setMaxWidth(240);
		titles.getChildren().addAll(pathTitle, resultTitle);

		vb.getChildren().add(titles);

		HBox[] hbox = new HBox[l.size()];
		for(HBox h:hbox){
			h = new HBox();
			h.setSpacing(10);

			Result e = (Result)it.next();
			Label path = new Label((String)e.getPath());
			path.setMinWidth(480);
			path.setMaxWidth(480);
			Label result = new Label((String)e.getScore());
			result.setMinWidth(240);
			result.setMaxWidth(240);
			Button view = new Button("view more");
			view.setOnAction((final ActionEvent ae) -> {
				//view more
				try {
					displayDetails(e);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			});
			h.getChildren().addAll(path,result,view);
			vb.getChildren().add(h);
		}


		resultStage.setScene(new Scene(vb));
		resultStage.show();
	}

	public static void displayDetails(Result r) throws FileNotFoundException {
		Stage detailStage = new Stage();
		detailStage.setTitle(r.getPath());

		HBox main = new HBox();
		VBox view = new VBox();
		view.setMinWidth(480);
		view.setMaxWidth(480);

		VBox mainV = new VBox();


		HBox titles = new HBox();
		Label codeTitle = new Label("Source Code:");
		codeTitle.setMinWidth(480);
		codeTitle.setMaxWidth(480);
		Label expectedOutputTitle = new Label("Expected Output:");
		expectedOutputTitle.setMinWidth(240);
		expectedOutputTitle.setMaxWidth(240);
		Label outputTitle = new Label("Output:");
		outputTitle.setMinWidth(240);
		outputTitle.setMaxWidth(240);
		titles.getChildren().addAll(codeTitle, outputTitle, expectedOutputTitle);

		ScrollPane codePane = new ScrollPane();
		codePane.setMinHeight(480);
		codePane.setMaxHeight(480);
		codePane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		codePane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		codePane.setContent(new Label(r.getCode()));

		ScrollPane stackTracePane = new ScrollPane();
		stackTracePane.setMinHeight(240);
		stackTracePane.setMaxHeight(240);
		stackTracePane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		stackTracePane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		stackTracePane.setContent(new Label(r.getErr()));


		ScrollPane expectedOutputPane = new ScrollPane();
		expectedOutputPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		expectedOutputPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		//scan input into expected output pane
		Scanner in = new Scanner(inputFile);
		StringBuilder sb = new StringBuilder();
		while(in.hasNextLine()){
			sb.append(in.nextLine());
			sb.append("\n");
		}
		expectedOutputPane.setContent(new Label(sb.toString()));
		expectedOutputPane.setMinWidth(240);
		expectedOutputPane.setMaxWidth(240);


		ScrollPane outputPane = new ScrollPane(new Label(r.getOutput()));
		outputPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		outputPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		outputPane.setMinWidth(240);
		outputPane.setMaxWidth(240);


		detailStage.setMinWidth(960);
		detailStage.setMinHeight(720);

		view.getChildren().addAll(codePane, stackTracePane);
		main.getChildren().addAll(view, outputPane, expectedOutputPane);
		mainV.getChildren().addAll(titles, main);
		detailStage.setScene(new Scene(mainV));
		detailStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}



	public static String getEasterEgg() {
		return "Let's grade some labs!";
	}

	private void storeFile(File file) {
		labs.add( file);
		fileCount++;
	}



class OptionList extends VBox{

	Label title;
	HBox labels;
	VBox options;
	ScrollPane optionsSP;

	public OptionList(){
		setMinWidth(390);

		title = new Label("Things to check:");
		title.setFont(Font.font("monospace", 20));
		title.setStyle("-fx-text-fill:#ffffff;");

		labels = new HBox();
		labels.setSpacing(10);
		Label valueLabel = new Label("Value");
		valueLabel.setFont(Font.font("monospace"));
		valueLabel.setMaxWidth(50);
		valueLabel.setStyle("-fx-text-fill:#ffffff;");
		Label regexLabel = new Label("Regex");
		regexLabel.setFont(Font.font("monospace"));
		regexLabel.setStyle("-fx-text-fill:#ffffff;");

		labels.getChildren().addAll(valueLabel, regexLabel);

		options = new VBox();
		options.setSpacing(2);
		optionsSP = new ScrollPane();
		optionsSP.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		optionsSP.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		//optionsSP.setBackground(Background.EMPTY);
		optionsSP.setStyle("-fx-background:#000000;-fx-border-color:#000000;");
		optionsSP.setContent(options);
		getChildren().addAll(title, labels, optionsSP);
		setSpacing(5);

		add();
	}

	public boolean remove(Option o){
		return options.getChildren().remove(o);
	}

	public boolean add(){
		return options.getChildren().add(new Option(this));
	}

	public ArrayList<SearchString> getArrayList(){
		ArrayList<SearchString> r = new ArrayList<>();
		for(Node n:options.getChildren()){
			System.out.println(n);
			if(!(n instanceof Option)) continue;
			if(!((Option)n).text.getText().equals("")){
				r.add(new SearchString( ((Option)n).text.getText(),((Option)n).value.getText()));
			}

		}
		return r;
	}

}

class Option extends HBox{
	TextField value;
	TextField text;
	ComboBox<String> presets;
	ArrayList<String> presetRegex;
	Button remove;
	boolean fresh;
	OptionList parent;
	String lastValue;
	boolean replaceValue;

	public Option(OptionList p){
		parent = p;
		fresh = true;
		text = new TextField();
		text.setFont(Font.font("monospace"));
		text.setMinWidth(200);
		text.setStyle("-fx-background-color:#000000;-fx-text-fill:#ffffff;-fx-border-color:#ffffff;-fx-border-style: hidden hidden solid hidden;");
		text.setOnKeyTyped(new EventHandler<KeyEvent>(){
			@Override
			public void handle(KeyEvent event) {
				unfresh();
			}
		});

		replaceValue = false;
		lastValue = "0";
		value = new TextField("0");
		value.setFont(Font.font("monospace"));
		value.setMaxWidth(40);
		value.setStyle("-fx-background-color:#000000;-fx-text-fill:#ffffff;-fx-border-color:#ffffff;-fx-border-style: hidden hidden solid hidden;");
		value.setOnKeyTyped(new EventHandler<KeyEvent>(){
			@Override
			public void handle(KeyEvent event) {
				unfresh();
				//System.out.println(value.getText()+"::"+event.getCharacter());
				if(event.getCharacter().matches("\\D")){
					lastValue = value.getText();
					replaceValue = true;
				}

			}
		});
		value.setOnKeyReleased(new EventHandler<KeyEvent>(){
			@Override
			public void handle(KeyEvent event) {
				unfresh();
				if(replaceValue || value.getText().length() > 2){
					replaceValue = false;
					value.setText(lastValue);
				}
				else{
					lastValue = value.getText();
				}
			}
		});

		remove = new Button();
		remove.setStyle("-fx-text-fill:#00ff00;-fx-background-color:#008800;-fx-background-radius:0px;");
		remove.setGraphic(new ImageView(new Image(this.getClass().getResource("close.png").toString(), 16, 16, true, false)));
		remove.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				p.remove(self());
			}
		});


		presets = new ComboBox();

		//presets.setStyle("-fx-text-fill:#00ff00;-fx-background-color:#008800;-fx-background-radius:0px;");
		presetRegex = new ArrayList<>();

		//default nothing
		presets.getItems().add("");
		presetRegex.add("");

		//test 1
		presets.getItems().add("for each");
		presetRegex.add("for\\s*\\(.*:.*\\)");

		//test 2
		presets.getItems().add("test2");
		presetRegex.add("regex for test 2");

		//test 3
		presets.getItems().add("test3");
		presetRegex.add("regex for test 3");

		presets.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				unfresh();
				text.setText( presetRegex.get(presets.getItems().indexOf(presets.getValue())) );
			}
		});

		setSpacing(5);
		getChildren().addAll(value, text, presets);
	}

	public void unfresh(){
		if(fresh){
			fresh = false;

			getChildren().add(remove);
			parent.add();
		}
	}

	private Option self(){
		return this;
	}

}

class SearchString{
	String regex;
	int value;

	public SearchString(String regex, String value){
		this.regex = regex;
		try
		{
			this.value = value.isEmpty() ? 0 : Integer.parseInt(value);
		}
		catch(NumberFormatException e){
			this.value = 0;
		}
	}

	public SearchString(String regex, int value){
		this.regex = regex;
		this.value = value;
	}

	public String getRegex(){
		return regex;
	}

	public int getValue(){
		return value;
	}

	public void setRegex(String regex){
		this.regex = regex;
	}

	public void setValue(int value){
		this.value = value;
	}

	public void setValue(String value){
		try
		{
			this.value = value.isEmpty() ? 0 : Integer.parseInt(value);
		}
		catch(NumberFormatException e){
			this.value = 0;
		}
	}

}

}
