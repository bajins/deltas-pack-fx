package com.bajins.deltaspackfx;

import com.bajins.deltaspackfx.common.FxDialogs;
import com.bajins.deltaspackfx.common.WindowAlert;
import javafx.application.Application;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class TestDialogs extends Application implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void start(Stage primaryStage) {

        boolean ret = WindowAlert.display("关闭窗口", "是否关闭窗口？");

        FxDialogs.showInformation("Hi", null, "Good Morning y'all!");
        if (FxDialogs.showConfirm("Choose one baby!", null, "Can i ask you a question?", FxDialogs.YES, FxDialogs.NO).equals(FxDialogs.YES)) {
            FxDialogs.showWarning(null, null, "Pay attention to my next question!");
            String answer = FxDialogs.showTextInput("Are you a pink elephant disguised as a flying pig?", "Tell me!", "No");
            FxDialogs.showError(null, "You should not have said " + answer + "!");
            FxDialogs.showException("Now i'm angry", "I'm going home...", new RuntimeException("Exception caused by angry dinossaurs"));
        }
        /*
        VBox vBox=new VBox();
        vBox.setMinWidth(200);
        vBox.setMinHeight(200);
        vBox.getChildren().add(comboBox);
        // 创建场景，并将场景添加到窗口
        Scene scene = new Scene(vBox);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX自定义弹窗测试");
        primaryStage.setMaximized(false); // 最大化
        primaryStage.setResizable(true);// 窗体缩放（默认为true）
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(800);
        primaryStage.show();*/
    }

    public static void main(String[] args) {
        launch(args);
    }
}
