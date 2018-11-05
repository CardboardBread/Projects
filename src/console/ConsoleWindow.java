package console;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class ConsoleWindow extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {

		BorderPane root = new BorderPane();
		root.setPadding(new Insets(5));

		TextFlow window = new TextFlow();
		window.setTextAlignment(TextAlignment.LEFT);
		window.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(5.0), Insets.EMPTY)));
		window.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, new CornerRadii(5.0), BorderWidths.DEFAULT)));
		root.setCenter(window);
		
		BorderPane entry = new BorderPane();
		entry.setPadding(new Insets(5, 0, 0, 0));
		entry.setMinWidth(200);

		TextField entryField = new TextField();
		entry.setCenter(entryField);

		Button sendButton = new Button("Send");
		entry.setRight(sendButton);

		root.setBottom(entry);

		Scene scene = new Scene(root);

		stage.setWidth(450);
		stage.setHeight(300);
		stage.setMinHeight(150);
		stage.setMinWidth(225);
		stage.setTitle("Console");
		stage.setScene(scene);
		stage.show();

		window.getChildren().add(new Text("bones\n"));
		window.getChildren().add(new Text("bones\n"));
		window.getChildren().add(new Text("bones\n"));
		window.getChildren().add(new Text("bones\n"));
		window.getChildren().add(new Text("bones\n"));
	}

}
