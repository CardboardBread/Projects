package map;

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;

public class MapPanel extends StackPane {
	
	private Canvas map;

	public MapPanel(int initialWidth, int initialHeight) {
		super();
		
		map = new Canvas(initialWidth, initialHeight);
		this.getChildren().add(map);
	}

	public MapPanel(Node... children) {
		super(children);
		// TODO Auto-generated constructor stub
	}

}
