package com.bajins.deltaspackfx;

import com.bajins.deltaspackfx.common.FxDialogs;
import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import javax.swing.*;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class Main extends Application implements Initializable {

    public static BorderPane root;
    public static Stage topStage;

    /**
     * 会在Application.start(Stage primaryStage) 方法前运行
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 创建布局控件: 这里的root从FXML文件中加载进行初始化，这里FXMLLoader类用于加载FXML文件
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/main.fxml")));
        /*GridPane center = (GridPane) root.getCenter();
        center.getChildrenUnmodifiable().forEach(N -> { // 获取子节点
            System.out.println(N.getStyleClass());
        });*/
        primaryStage.setTitle("增量打包");
        primaryStage.setMaximized(false); // 最大化
        primaryStage.setResizable(true);// 窗体缩放（默认为true）
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(750);
        //primaryStage.setMaxHeight(600);
        //primaryStage.setMaxWidth(1300);
        // 设置窗口的图标
        //InputStream logo = Objects.requireNonNull(getClass().getResourceAsStream("/img/top_left_logo.png"));
        //primaryStage.getIcons().add(new Image("/img/top_left_logo.png"));
        // 创建场景，并将场景添加到窗口
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        // 上下文菜单
        /*MenuItem saveItem = new MenuItem("_Save");
        saveItem.setMnemonicParsing(true);
        saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN)); // 绑定键盘按键
        ContextMenu contextFileMenu = new ContextMenu(exitItem);
        primaryStage.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
            if (me.getButton() == MouseButton.SECONDARY || me.isControlDown()) {
                contextFileMenu.show(root, me.getScreenX(), me.getScreenY());
            } else {
                contextFileMenu.hide();
            }
        });*/
        // 窗口点击叉号关闭询问
        primaryStage.setOnCloseRequest(event -> {
            event.consume();  // 消除默认事件
            handleClose();
        });
        // 显示窗口
        primaryStage.show();

        topStage = primaryStage;

        // lookup查找元素需要在Stage.show()窗口打开后才有作用
        /*Node localMenu = root.lookup("#local_menu");
        if (localMenu != null) {
            FxUtils.addStyleClass(localMenu, "clicked-menu");
        }*/
        Label localMenuLabel = (Label) root.lookup("#local_menu_label");
        //Label localMenuLabel = (Label) scene.lookup("#local_menu_label");
        if (localMenuLabel != null) {
            // 构造事件
            MouseEvent mouseEvent = new MouseEvent(MouseEvent.MOUSE_CLICKED, 100, 100, 10, 10, MouseButton.PRIMARY, 1,
                    true, true, true, true, true, true, true, true, true, true, null);
            // 编程方式触发事件
            localMenuLabel.fireEvent(mouseEvent);
            //FxUtils.addStyleClass(localMenuLabel, "clicked-menu-label");
        }
    }

    /**
     * 确认结束程序进程
     */
    private void handleClose() {
        if (FxDialogs.showConfirm("关闭窗口", null, "是否关闭窗口？", FxDialogs.YES, FxDialogs.NO).equals(FxDialogs.YES)) {
            topStage.close();
        }
    }

    /**
     * final SwingNode swingNode = new SwingNode(); // 实现Swing转换成JavaFx的关键类
     *
     * @param swingNode
     */
    private void createAndSetSwingContent(final SwingNode swingNode) {
        SwingUtilities.invokeLater(() -> {
            // 只能嵌入JComponent 类别的组件，像JFrame、JDialog等不适JComponent类别的都不能够嵌入
            swingNode.setContent(new JButton("Click me!"));
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
