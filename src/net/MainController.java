package net;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class MainController {

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="entryField"
	private TextField entryField; // Value injected by FXMLLoader

	@FXML // fx:id="sendButton"
	private Button sendButton; // Value injected by FXMLLoader

	@FXML // fx:id="textView"
	private TextArea textView; // Value injected by FXMLLoader
	
	@FXML
    void onEntryTyped(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			onSendPressed(null);
		}
    }

	@FXML
	void onSendPressed(ActionEvent event) {
		if (!entryField.getText().isEmpty()) {
			textView.appendText(entryField.getText() + "\n");
			entryField.clear();
		}
	}

	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize() {
		assert entryField != null : "fx:id=\"entryField\" was not injected: check your FXML file 'netwriter_jfx.fxml'.";
		assert sendButton != null : "fx:id=\"sendButton\" was not injected: check your FXML file 'netwriter_jfx.fxml'.";
		assert textView != null : "fx:id=\"textView\" was not injected: check your FXML file 'netwriter_jfx.fxml'.";

	}
}
