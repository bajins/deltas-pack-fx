package com.bajins.deltaspackfx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class TestLoading extends Application {

    @Override
    public void start(Stage stage) {
        StackPane loadingRoot = new StackPane();

        loadingRoot.getChildren().setAll(new ProgressIndicator());
        final Scene scene = new Scene(loadingRoot);

        //stage.initOwner();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initStyle(StageStyle.TRANSPARENT);
        //stage.initModality(Modality.APPLICATION_MODAL);

        stage.setWidth(400);
        stage.setHeight(400);
        stage.setScene(scene);
        stage.show();

        new Thread(() -> {
            // TODO: 这里执行自己的逻辑

            // 通过Platform刷新UI，以解决在独立线程中执行错误
            Platform.runLater(() -> {
                /*try {
                    Parent root = FXMLLoader.load(Main.class.getResource("FXMLDocument.fxml"));
                    scene.setRoot(root);
                } catch (IOException ex) {
                }*/
            });
        }).start();
    }
}
