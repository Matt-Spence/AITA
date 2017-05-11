/**
 * Created by Matth_000 on 3/10/2017.
 */
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class FileBrowser extends HBox{

	File root;
	static ArrayList<File> fileList;
	static VBox selected;
	public FileBrowser(AITAGUI ag){
		if (System.getProperty("os.name").contains("Windows"))
		{
			root = new File("C:/");
		}
		else
		{
			root = new File("/");
		}
		fileList = new ArrayList<File>();

		VBox vbRoot = new VBox();
		vbRoot.setMinWidth(150);
		vbRoot.setMaxWidth(150);

		VBox vb1 = new VBox();
		ScrollPane sp = new ScrollPane();
		sp.setMinWidth(200);
		sp.setMaxWidth(200);
		sp.setContent(vb1);
		sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		sp.setBackground(Background.EMPTY);

		MyFile fol1 = root.isDirectory()?new Folder(root):new AFile(root);
		if (System.getProperty("os.name").contains("Windows"))
		{
			fol1.name = "C:\\";
			fol1.text.setText("C:\\");
		}
		else
		{
			fol1.name = "/";
			fol1.text.setText("/");
		}
		fol1.realbtn.setMinWidth(fol1.text.getLayoutBounds().getWidth()+64+10);
		fol1.realbtn.setMaxWidth(fol1.text.getLayoutBounds().getWidth()+64+10);
		vb1.getChildren().add(fol1);

		Button btn = new Button("Submit");
		btn.setDefaultButton(true);
		btn.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				ag.submit(fileList);
			}
		});
		selected = new VBox();
		this.getChildren().addAll(vbRoot,sp);
		vbRoot.getChildren().addAll(btn,selected);

	}

}

class MyFile extends VBox{
	HBox stuff;
	File file;
	String name;
	Button btn;
	Text text;
	boolean bool;
	ImageView pic;
	Button realbtn;
	StackPane secret;
	HBox secret2;

	public MyFile(){

	}

	public MyFile(File f){
		stuff = new HBox();
		stuff.setSpacing(5);
		file = f;
		name = f.getName();
		bool = false;

		text = new Text(name);
		text.setFont(new Font("monospace", 10));
		btn = new Button();
		btn.setBackground(Background.EMPTY);
		btn.setMaxSize(16, 16);
		btn.setMinSize(16, 16);
		pic = new ImageView();

		secret = new StackPane();
		secret.getChildren().add(stuff);
		secret2 = new HBox();
		realbtn = new Button();
		realbtn.setMinHeight(16);
		realbtn.setMaxHeight(16);

		realbtn.setMinWidth(text.getLayoutBounds().getWidth()+64+10);
		realbtn.setMaxWidth(text.getLayoutBounds().getWidth()+64+10);
		realbtn.setBackground(Background.EMPTY);

		secret2.getChildren().add(realbtn);
		secret.getChildren().add(secret2);
		stuff.getChildren().addAll(btn,pic,text);


		getChildren().add(secret);
	}
}

class Folder extends MyFile{
	InFolder in;
	Image closed;
	Image open;
	ImageView arrowOpen;
	ImageView arrowClosed;
	boolean empty;

	public Folder(File f){
		super(f);

		in = new InFolder(f);
		in.parent = this;
		getChildren().add(in);

		arrowOpen = new ImageView(new Image(this.getClass().getResource("arrow2.png").toString(), 16, 16, true, false));
		arrowClosed = new ImageView(new Image(this.getClass().getResource("arrow1.png").toString(), 16, 16, true, false));

		empty = !(f.listFiles(new emptyCheck()) != null && f.listFiles(new emptyCheck()).length>0);
		if(!empty){
			closed = new Image(this.getClass().getResource("folderClosed2.png").toString(), 16, 16, true, false);
			open = new Image(this.getClass().getResource("folderOpen2.png").toString(), 16, 16, true, false);
		}
		else{
			closed = new Image(this.getClass().getResource("folderClosed.png").toString(), 16, 16, true, false);
			open = new Image(this.getClass().getResource("folderOpen.png").toString(), 16, 16, true, false);
		}

		//btn.setText(">");
		btn.setGraphic(arrowClosed);
		//if(!empty)
		realbtn.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				in.action();
			}
		});
		pic.setImage(closed);
		//stuff.getChildren().addAll(btn,pic,text);

	}

}

class InFolder extends VBox{
	MyFile[] files;
	File file;
	boolean filled;
	boolean visible;
	Folder parent;

	public InFolder(File f){
		filled = false;
		visible = false;
		setPadding(new Insets(0,0,0,11));
		file = f;

	}
	public void fill(){
		//filled = true;
		getChildren().clear();
		for(File t:file.listFiles()){
			if(t.isDirectory() && !t.isHidden())
				getChildren().add(new Folder(t));
		}
		for(File t:file.listFiles()){
			if(!t.isDirectory() && !t.isHidden() && t.getName().matches(".+\\.java$"))
				getChildren().add(new AFile(t));
		}
	}

	public void action(){
		visible = !visible;
		if(visible) fill();
		setVisible(visible);
		setManaged(visible);
		parent.btn.setGraphic(visible?parent.arrowOpen:parent.arrowClosed);
		parent.pic.setImage(visible?parent.open:parent.closed);
	}
}

class AFile extends MyFile{
	Image imgFile;
	Image imgJava;
	ImageView uncheck;
	ImageView check;
	AFileClone clone;

	public AFile(File f){
		super(f);

		imgFile = new Image(this.getClass().getResource("file.png").toString(), 16, 16, true, false);
		imgJava = new Image(this.getClass().getResource("java.png").toString(), 16, 16, true, false);
		check = new ImageView(new Image(this.getClass().getResource("check.png").toString(), 16, 16, true, false));
		uncheck = new ImageView(new Image(this.getClass().getResource("uncheck.png").toString(), 16, 16, true, false));

		//btn.setText("_");
		btn.setGraphic(uncheck);
		realbtn.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				action();
			}
		});
		pic.setImage((name.length()>=4 && name.substring(name.length()-4).equalsIgnoreCase("java"))?imgJava:imgFile);
		//stuff.getChildren().addAll(btn,pic,text);
	}
	public void action(){
		bool = !bool;
		if(bool){
			btn.setGraphic(check);
			FileBrowser.fileList.add(file);
			clone = new AFileClone(this);
			FileBrowser.selected.getChildren().add(clone);
		}
		else{
			btn.setGraphic(uncheck);
			FileBrowser.fileList.remove(file);
			FileBrowser.selected.getChildren().remove(clone);
			clone = null;
		}


	}
}

class AFileClone extends StackPane{
	ImageView img;
	Button fakebtn;
	Text text;
	Button realbtn;
	AFile original;
	HBox hb;

	public AFileClone(AFile o){
		original = o;
		hb = new HBox();
		hb.setSpacing(5);
		img = new ImageView(o.pic.getImage());
		fakebtn = new Button();
		fakebtn.setBackground(Background.EMPTY);
		fakebtn.setMaxSize(16, 16);
		fakebtn.setMinSize(16, 16);
		fakebtn.setGraphic(new ImageView(new Image(this.getClass().getResource("check.png").toString(), 16, 16, true, false)));
		if(o.name.length() > 20){
			text = new Text(o.name.substring(0,21)+ "...");
		}
		else{
			text = new Text(o.name);
		}
		text.setFont(new Font("monospace", 10));

		realbtn = new Button();
		realbtn.setMinHeight(16);
		realbtn.setMaxHeight(16);
		realbtn.setMinWidth(o.text.getLayoutBounds().getWidth()+64+10);
		realbtn.setMaxWidth(o.text.getLayoutBounds().getWidth()+64+10);
		realbtn.setBackground(Background.EMPTY);
		realbtn.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				original.action();
			}
		});

		hb.getChildren().addAll(img,fakebtn,text);
		getChildren().addAll(hb,realbtn);
	}
}

class emptyCheck implements FileFilter{

	public boolean accept(File f){
		return !f.isHidden();
	}
}

