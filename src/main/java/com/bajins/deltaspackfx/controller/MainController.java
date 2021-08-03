package com.bajins.deltaspackfx.controller;

import com.bajins.deltaspackfx.Main;
import com.bajins.deltaspackfx.common.FxDialogs;
import com.bajins.deltaspackfx.utils.FxUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private Menu localMenu;
    @FXML
    private Label localMenuLabel;
    @FXML
    private Menu svnMenu;
    @FXML
    private Label svnMenuLabel;
    @FXML
    private Button compileButton;

    /**
     * 会在Application.start(Stage primaryStage) 方法前运行
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //menuBar.prefWidthProperty().bind(pane.widthProperty()); // 宽度绑定为Pane宽度
    }

    @FXML
    public void checkLocalPane(MouseEvent event) { // 对应FXML文件中声明的onAction属性，以#开头
        /*System.out.println(event.getSource());
        System.out.println(event.getTarget());*/
        FxUtils.addStyleClass(localMenu, "clicked-menu"); // 给指定元素添加样式
        //svn_menu.styleProperty().setValue("clicked-menu");
        Label label = (Label) event.getSource(); // 获取当前元素
        FxUtils.addStyleClass(label, "clicked-menu-label");
        svnMenu.getStyleClass().remove("clicked-menu"); // 移除样式
        svnMenuLabel.getStyleClass().remove("clicked-menu-label"); // 移除样式
        try {
            // 创建布局控件: 这里的root从FXML文件中加载进行初始化，这里FXMLLoader类用于加载FXML文件
            Parent pane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/local.fxml")));
            Main.root.setCenter(pane); // 切换BorderPane内容部分
            //Main.topStage.setScene(new Scene(pane)); // 给当前窗口设置新的场景
            /*BorderPane root = (BorderPane) Main.topStage.getScene().getRoot(); // 有无法强转的风险
            root.setCenter(pane);*/
        } catch (IOException e) {
            FxDialogs.showException(e.getMessage(), e);
        }
    }

    @FXML
    public void checkSvnPane(MouseEvent event) { // 对应FXML文件中声明的onAction属性，以#开头
        /*System.out.println(event.getSource());
        System.out.println(event.getTarget());*/
        FxUtils.addStyleClass(svnMenu, "clicked-menu");
        //svn_menu.styleProperty().setValue("clicked-menu");
        Label label = (Label) event.getSource(); // 获取当前元素
        FxUtils.addStyleClass(label, "clicked-menu-label");
        localMenu.getStyleClass().remove("clicked-menu"); // 移除样式
        localMenuLabel.getStyleClass().remove("clicked-menu-label"); // 移除样式
        try {
            // 创建布局控件: 这里的root从FXML文件中加载进行初始化，这里FXMLLoader类用于加载FXML文件
            Parent pane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/svn.fxml")));
            Main.root.setCenter(pane); // 切换BorderPane内容部分
        } catch (IOException e) {
            FxDialogs.showException(e.getMessage(), e);
        }
    }

    @FXML
    public void clickLicensePane(ActionEvent event) { // 对应FXML文件中声明的onAction属性，以#开头
        new LicenseController();
    }

    @FXML
    public void clickAboutPane(ActionEvent event) { // 对应FXML文件中声明的onAction属性，以#开头
        new AboutController();
    }

}
