package io.silksmith.js.closure.task;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class TestBrowser extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		StackPane root = new StackPane();

		WebView view = new WebView();
		WebEngine engine = view.getEngine();
		// engine.load("http://192.168.192.28:10101/TEST/MOCHA");
		//

		engine.getLoadWorker().exceptionProperty()
				.addListener(new ChangeListener<Throwable>() {
					@Override
					public void changed(
							ObservableValue<? extends Throwable> ov,
							Throwable t, Throwable t1) {
						System.out.println("Received exception: "
								+ t1.getMessage());
					}
				});
		// engine.load("http://localhost:5555/");
		engine.load("http://192.168.192.28:10101/TEST/MOCHA");
		root.getChildren().add(view);

		Scene scene = new Scene(root, 800, 600);
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		TestBrowser.launch();
	}
}
