package com.bajins.deltaspackfx;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class TestListView extends Application implements Initializable {

    @FXML
    private ListView listView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 设置按住Ctrl可多选
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        //listView.getSelectionModel().select(0); // 设置默认选中
        // 单选事件 item的监听
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(newValue);
        });
        // 单选事件 索引的监听
        listView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(newValue.intValue());
        });
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton().compareTo(MouseButton.PRIMARY) == 0) { // 实现双击事件
                System.out.println("你用左键点击了两次");
                if (listView.isEditable()) { // 实现双击新增一行
                    listView.getItems().add("");
                    new AnimationTimer() {
                        int frameCount = 0;

                        @Override
                        public void handle(long now) {
                            frameCount++;
                            if (frameCount > 1) {
                                listView.edit(listView.getItems().size() - 1);
                                stop();
                            }
                        }
                    }.start();
                }
            }
        });
        listView.setOnKeyPressed(event -> { // 实现循环浏览，KeyPressEvent(Item还未被选中)->selectItemEvent(Item被选中)->selectIndexEvent
            int size = listView.getItems().size();
            int index = listView.getSelectionModel().getSelectedIndex();
            if ((index == size - 1 && event.getCode() == KeyCode.RIGHT)) {
                listView.getSelectionModel().selectFirst();
                listView.scrollTo(0); //操作滚动条
                event.consume();
            } else if (index == 0 && event.getCode() == KeyCode.LEFT) {
                listView.getSelectionModel().selectLast();
                listView.scrollTo(size - 1); // 操作滚动条
                event.consume();
            }
        });
        listView.setFixedCellSize(50); // 设置单元格尺寸
        // 直接对数据源进行修改
        //listView.setCellFactory(TextFieldListCell.forListView());

        /*listView.setCellFactory(TextFieldListCell.forListView(new StringConverter() { // 自定义编辑单元格，使用转换器转换数据类型进行显示
            @Override
            public String toString(Object object) { // 页面显示
                return (String) object;
            }

            @Override
            public Object fromString(String string) { // 后台取数据
                return string;
            }
        }));*/
        /*listView.setCellFactory(cell -> new ListCell() {
            @Override
            public void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                //this.setGraphic();
                this.setText("test");
            }
        });*/
        listView.setCellFactory(lv -> {
            ListCell<Object> cell = new ListCell<Object>() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(item.toString());
                    }
                }
            };
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (event.getButton().compareTo(MouseButton.SECONDARY) == 0 && !cell.isEmpty()) { // 在一行上单击鼠标右键
                    Object item = cell.getItem();
                    System.out.println("Right clicked " + item);
                }
            });
            return cell;
        });
        /*listView.setCellFactory(ChoiceBoxListCell.forListView(new StringConverter() { // 自定义编辑单元格下拉选择项，使用转换器转换数据类型进行显示
            @Override
            public String toString(Object object) { // 页面显示
                return (String) object;
            }

            @Override
            public Object fromString(String string) { // 后台取数据
                return string;
            }
        }));*/
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        // 创建布局控件: 这里的root从FXML文件中加载进行初始化，这里FXMLLoader类用于加载FXML文件
        AnchorPane load = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/listview.fxml")));

        // 创建场景，并将场景添加到窗口
        Scene scene = new Scene(load);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX自定义列表视图测试");
        primaryStage.setMaximized(false); // 最大化
        primaryStage.setResizable(true);// 窗体缩放（默认为true）
        primaryStage.setMinWidth(200);
        primaryStage.setMinHeight(200);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
