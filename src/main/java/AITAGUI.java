/**
 * Created by Matth_000 on 2/14/2017.
 */

import javafx.event.*;
import javafx.geometry.Insets;
import javafx.geometry.*;
import javafx.collections.*;
import javafx.scene.control.cell.*;
import java.io.File;
import javafx.scene.input.KeyEvent;
import java.util.*;
import javafx.beans.property.*;
import javafx.application.*;
import javafx.collections.FXCollections;
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
    private File inputFile;
    RadioButton rb = new RadioButton("Check White Space");
    RadioButton rb2 = new RadioButton("Check Symbols");
    RadioButton rb3 = new RadioButton("Check for each loops");
    ScrollPane searchStringScrollPane = new ScrollPane();
    OptionList ol = new OptionList();
    TextField point1 = new TextField();
    TextField point2 = new TextField();
    TextField point3 = new TextField();

    Text outText = new Text();
    Text inText  = new Text();
    HBox inputField = new HBox();
    HBox outputField = new HBox();

    public void start(Stage primaryStage) {
        primaryStage.setTitle("AITA");

        searchStringScrollPane.setContent(ol);
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
        hb.getChildren().add(ol);
        hb.getChildren().add(vb);

        // add buttons
        HBox button1Box = new HBox();
        HBox button2Box = new HBox();
        HBox button3Box = new HBox();

        button1Box.getChildren().addAll(rb, point1);
        button2Box.getChildren().addAll(rb2, point2);
        button3Box.getChildren().addAll(rb3, point3);
        vb.getChildren().add(button1Box);
        vb.getChildren().add(button2Box);
        vb.getChildren().add(button3Box);

        outputField.getChildren().addAll(expectedOutput, outText);
        inputField.getChildren().addAll(inputData, inText);

        inputField.setSpacing(5);
        outputField.setSpacing(5);

        vb.getChildren().add(outputField);
        vb.getChildren().add(inputField);

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
        System.out.println(l);
        AITA.setSourceCode(toFileArray(l));
        AITA.setInputFile(inputFile);
        AITA.setCorrectOutputFile(output);
        AITA.setIgnoreWhiteSpace(rb.isPressed());
        AITA.setIgnoreSymbolCharacters(rb2.isPressed());
        ArrayList<String> options = ol.getArrayList();
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
        if (rb3.isPressed()) {
            AITA.addRawSearchString("for\\s*\\(.*:.*\\)", Integer.parseInt(point3.getText()));
        }
        HashMap<String, String> hm = AITA.grade();
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

    public static void displayResults(HashMap<String, String> results) {
        Scene scene = new Scene(new Group());
        Stage resultStage = new Stage();
        resultStage.setTitle("Results");


        ObservableList<result> data = FXCollections.observableArrayList();
        results.forEach((String p, String r) -> {
            data.add(new result(p, r));
        });

        TableView<result> table = new TableView<result>();

        TableColumn<result, String> result = new TableColumn("Result");
        result.setMinWidth(150);
        result.setCellValueFactory((p) -> {
            result x = p.getValue();
            return new SimpleStringProperty(
                    x.getScore() != null && x.getScore().length() > 0 ? x.getScore() : "<error>");
        });

        TableColumn<result, String> path = new TableColumn("Path");
        path.setMinWidth(100);
        path.setCellValueFactory((p) -> {
            result x = p.getValue();
            return new SimpleStringProperty(
                    x.getPath() != null && x.getPath().length() > 0 ? x.getPath() : "<error>");
        });
        TableColumn view = new TableColumn("    ");
        view.setMinWidth(50);
        view.setCellValueFactory(new PropertyValueFactory<>("viewButton"));

        table.setItems(data);
        table.getColumns().addAll(path, result, view);

        VBox box = new VBox();
        box.getChildren().add(table);
        box.setAlignment(Pos.CENTER);

        ((Group) scene.getRoot()).getChildren().addAll(box);

        resultStage.setScene(scene);
        resultStage.show();
    }

    public static void displayDetails(result r){
        Scene scene = new Scene(new Group());
        Stage detailStage = new Stage();
        detailStage.setTitle("Details");
        detailStage.setWidth(480);
        detailStage.setHeight(480);

    }

    public static void main(String[] args) {
        launch(args);
    }

    public class viewButton extends Button {
        public viewButton(result r){
            super("view");
            setOnAction((event) -> {
                displayDetails(r);
            });
        }

    }

    public class resultRow {
        private SimpleStringProperty path;
        private final SimpleObjectProperty<viewButton> viewButton;

        public resultRow(result r){
            this.path = new SimpleStringProperty(r.getPath());
            viewButton = new SimpleObjectProperty(new viewButton(r));
        }

        public String getPath(){
            return path.get();
        }

        public viewButton getViewButton(){
            return viewButton.get();
        }

        public ObjectProperty<viewButton> viewButtonProperty() {
            return viewButton;
        }
    }

    public static String getEasterEgg() {
        return "Let's grade some labs!";
    }

    private void storeFile(File file) {
        labs.add( file);
        fileCount++;
    }

    public static class result {

        public SimpleStringProperty path;
        public SimpleStringProperty score;

        public result(String s, String n) {
            path = new SimpleStringProperty(s);
            score = new SimpleStringProperty(n);
        }

        public String getScore() {
            return score.getValue();
        }

        public String getPath() {
            return path.getValue();
        }
    }

}

class OptionList extends VBox{
    public OptionList(){
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
    boolean fresh;
    Parent parent;

    public Option(OptionList p){
        parent = p;
        fresh = true;
        text = new TextField();
        text.setOnKeyTyped(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event) {
                if(fresh){
                    fresh = false;
                    getChildren().add(remove);
                    p.add();
                }
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

        getChildren().add(text);
    }

    private Option self(){
        return this;
    }

}
