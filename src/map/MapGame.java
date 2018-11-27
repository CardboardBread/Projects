package map;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MapGame extends Application {
	
	public static void main(String[] args) {
		launch(args);
	}

	private MapPanel map;

	@Override
	public void start(Stage primaryStage) throws Exception {
		map = new MapPanel(300,300);
		Scene scene = new Scene(map);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Map Game");
		primaryStage.show();

	}

	

}
