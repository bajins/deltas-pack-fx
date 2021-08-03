package com.bajins.deltaspackfx;

import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodHighlight;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TestFx extends Application implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void start(Stage primaryStage) {
        // 获取屏幕尺寸
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        Rectangle2D screenRectangle = Screen.getPrimary().getBounds();
        //double width = screenRectangle.getWidth();
        //double height = screenRectangle.getHeight();

        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        /*Timer timer = new Timer(delay, (ActionListener) e -> Platform.runLater(() -> {

        }));
        timer.start();*/

        /*new Thread(() -> {
            long timer = System.nanoTime();
            Semaphore semaphore = new Semaphore(1);
            try {
                //计算瞬间时间
                long now = System.nanoTime();
                double moment = (now - timer) * 0.000000001;
                timer = now;
                //处理事务
                semaphore.acquire();
                Platform.runLater(() -> {
                    semaphore.release();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();*/

        Button button = new Button("测试键盘");
        button.setFont(Font.font("Arial", FontPosture.ITALIC, 20)); // 设置字体

        /*
        KeyEvent.KEY_PRESSED    任意按键按下时响应
        KeyEvent.KEY_RELEASED   任意按键松开时响应
        KeyEvent.KEY_TYPED      文字输入键按下松开后响应，只会响应文字输入键，如字母、数字和标点符号等，不会响应CTRL/ENTER/F1等功能键

        KEY_PRESSED和KEY_RELEASED只能知道你按下了什么键，而不知道最后输入的结果
        从按下到松开过程三个事件响应顺序为：KEY_PRESSED -> KEY_TYPED -> KEY_RELEASED
        同一个类型下，添加事件方式的执行顺序为：addEventFilter -> addEventHandler -> setOnKey
         */
        // 键盘事件1
        button.setOnKeyTyped(e -> {
            System.out.println(e.getCharacter());
            //text.setText(String.join("", aList));
        });
        // 键盘事件2
        button.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {

            }
        });
        button.setOnKeyReleased(event -> {
            System.out.println(event.getCode().isFunctionKey());
            System.out.println(event.getCode().isNavigationKey());
            System.out.println(event.getCode().isArrowKey());
            System.out.println(event.getCode().isModifierKey());
            System.out.println(event.getCode().isLetterKey());
            System.out.println(event.getCode().isDigitKey());
            System.out.println(event.getCode().isKeypadKey());
            System.out.println(event.getCode().isWhitespaceKey());
            System.out.println(event.getCode().isMediaKey());
        });
        TextField textField = new TextField("测试");

        //textField.requestFocus(); // 光标定位

        // https://stackoverflow.com/questions/12956061/javafx-oninputmethodtextchanged-not-called-after-focus-is-lost
        if (Platform.isSupported(ConditionalFeature.INPUT_METHOD)) { // 支持input方法
            textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue || !textField.isFocused()) { // 光标离开事件
                    System.out.println("Focusing out from textfield");
                    System.out.println(InputMethodHighlight.SELECTED_RAW);
                }
            });
        }
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("----------");
        });
        textField.setOnInputMethodTextChanged(event -> {
            if (!event.getCommitted().isEmpty()) {
                System.out.println(event.getCommitted());
            }
        });

        /*datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            int year = newValue.getYear();
            int month = newValue.getMonthValue();
            int day = newValue.getDayOfMonth();
            System.out.println("year = " + year + ", month = " + month + ", day = " + day);
        });*/


        VBox vBox = new VBox();
        vBox.setMinWidth(200);
        vBox.setMinHeight(200);
        vBox.getChildren().add(button);
        vBox.getChildren().add(textField);
        // 创建场景，并将场景添加到窗口
        Scene scene = new Scene(vBox);
        primaryStage.setScene(scene);

        primaryStage.setTitle("JavaFX原生API测试");
        primaryStage.setMaximized(false); // 最大化
        primaryStage.setResizable(true);// 窗体缩放（默认为true）
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(800);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
