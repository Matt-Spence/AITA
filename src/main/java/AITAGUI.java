/**
 * Created by Matth_000 on 2/14/2017.
 */

import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.geometry.Insets;
import javafx.geometry.*;

import java.io.File;
import javafx.scene.input.KeyEvent;

import java.io.FileNotFoundException;
import java.util.*;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.scene.text.*;
import javafx.scene.image.*;


public class AITAGUI extends Application {
	// List of files to be read in
	private List<File> labs = new ArrayList<>(10);
	private int fileCount = 0;
	private File output;
	private static File inputFile;
	RadioButton whiteSpaceRB = new RadioButton("Ignore White Space");
	RadioButton symbolRB = new RadioButton("Ignore Symbols");
	RadioButton forLoopRB = new RadioButton("Check for each loops");
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
		Button expectedOutput = new Button("Expected Output");

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
		HBox button3Box = new HBox();

		button1Box.getChildren().addAll(whiteSpaceRB);
		button2Box.getChildren().addAll(symbolRB);
		button3Box.getChildren().addAll(forLoopRB);
		point3.setMaxWidth(50.0);
		button3Box.getChildren().add(point3);
		vb.getChildren().add(button1Box);
		vb.getChildren().add(button2Box);
		vb.getChildren().add(button3Box);

		outputField.getChildren().addAll(expectedOutput, outText);
		inputField.getChildren().addAll(inputData, inText);

		inputField.setSpacing(5);
		outputField.setSpacing(5);

		vb.getChildren().add(inputField);
		vb.getChildren().add(outputField);


		BorderPane bp = new BorderPane();
		bp.setCenter(hb);


		vb.setPadding(new Insets(25, 25, 25, 25));
		vb.setSpacing(5);

		Label TopBanner = new Label("Hello Ms. Campbell!");
		Label BottomBanner = new Label(getEasterEgg());

		HBox top = new HBox();
		top.setAlignment(Pos.CENTER);
		top.getChildren().add(TopBanner);

		HBox bottom = new HBox();
		bottom.setAlignment(Pos.CENTER);
		bottom.getChildren().add(BottomBanner);

		bp.setTop(top);
		bp.setBottom(bottom);

		primaryStage.setScene(new Scene(bp));
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
		ArrayList<String> options = optionList.getArrayList();
		HashMap<String, Integer> SearchStrings = new HashMap<>();
		for(String s:options){
			int lastSpaceIndex = 0;
			for(int i = s.length()-1; i > 0; i++){
				if(s.charAt(i) == ' '){
					lastSpaceIndex = i;
					break;
				}
			}
			SearchStrings.put(s.substring(0,lastSpaceIndex),Integer.parseInt(s.substring(lastSpaceIndex+1)));
		}
		AITA.setSearchStrings(SearchStrings);//hashmap of regex to search for; point value of that regex
		if (forLoopRB.isPressed()) {
			AITA.addRawSearchString("for\\s*\\(.*:.*\\)", Integer.parseInt(point3.getText()));
		}
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

	public OptionList(){
		title = new Label("Things to check:");
		getChildren().add(title);
		add();
	}

	public boolean remove(Option o){
		return getChildren().remove(o);
	}

	public boolean add(){
		return getChildren().add(new Option(this));
	}

	public ArrayList<String> getArrayList(){
		ArrayList<String> r = new ArrayList<>();
		for(Node n:getChildren()){
			if(n instanceof Label) continue;
			if(!((Option)n).text.getText().equals("")){
				r.add(((Option)n).text.getText());
			}

		}
		return r;
	}

}

class Option extends HBox{
	TextField text;
	Button remove;
	ComboBox<String> presets;
	ArrayList<String> presetRegex;
	boolean fresh;
	OptionList parent;

	public Option(OptionList p){
		parent = p;
		fresh = true;
		text = new TextField();
		text.setOnKeyTyped(new EventHandler<KeyEvent>(){
			@Override
			public void handle(KeyEvent event) {
				unfresh();
			}
		});
		remove = new Button();
		remove.setGraphic(new ImageView(new Image(this.getClass().getResource("close.png").toString(), 16, 16, true, false)));
		remove.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				p.remove(self());
			}
		});


		presets = new ComboBox();
		presetRegex = new ArrayList<>();

		//default nothing
		presets.getItems().add("");
		presetRegex.add("");

		//test 1
		presets.getItems().add("test1");
		presetRegex.add("regex for test 1");

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

		getChildren().addAll(text, presets);
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
}
