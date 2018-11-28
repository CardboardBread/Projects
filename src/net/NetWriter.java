package net;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class NetWriter extends Application {

	private VBox main;
	private MainController mainController;
	private Stage mainStage;

	public static void main(String[] args) {
		Application.launch(NetWriter.class, args);
	}

	@Override
	public void init() throws Exception {
		super.init();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("netwriter_jfx.fxml"));
		main = loader.<VBox>load();
		mainController = loader.<MainController>getController();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene = new Scene(main);
		mainStage = primaryStage;
		mainStage.setScene(scene);
		mainStage.setTitle("NetWriter");
		mainStage.show();
	}

	@Override
	public void stop() throws Exception {
		super.stop();
	}

}
