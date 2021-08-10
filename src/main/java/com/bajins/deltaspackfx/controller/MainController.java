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

    public Parent localPane = null;
    public Parent svnPane = null;
    public LicenseController licenseController = null;
    public AboutController aboutController = null;

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
        if (localPane == null) {
            try {
                // 创建布局控件: 这里的root从FXML文件中加载进行初始化，这里FXMLLoader类用于加载FXML文件
                localPane = FXMLLoader.load(Objects.requireNonNull(MainController.class.getResource("/fxml/local" +
                        ".fxml")));
            } catch (IOException e) {
                FxDialogs.showException(e.getMessage(), e);
                return;
            }
        }
        Main.root.setCenter(localPane); // 切换BorderPane内容部分
        //Main.topStage.setScene(new Scene(pane)); // 给当前窗口设置新的场景
        /*BorderPane root = (BorderPane) Main.topStage.getScene().getRoot(); // 有无法强转的风险
        root.setCenter(pane);*/
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
        if (svnPane == null) {
            try {
                // 创建布局控件: 这里的root从FXML文件中加载进行初始化，这里FXMLLoader类用于加载FXML文件
                svnPane = FXMLLoader.load(Objects.requireNonNull(MainController.class.getResource("/fxml/svn.fxml")));
            } catch (IOException e) {
                FxDialogs.showException(e.getMessage(), e);
                return;
            }
        }
        Main.root.setCenter(svnPane); // 切换BorderPane内容部分
    }

    @FXML
    public void clickLicensePane(ActionEvent event) { // 对应FXML文件中声明的onAction属性，以#开头
        if (licenseController == null) {
            licenseController = new LicenseController();
        }
        licenseController.show();
    }

    @FXML
    public void clickAboutPane(ActionEvent event) { // 对应FXML文件中声明的onAction属性，以#开头
        if (aboutController == null) {
            aboutController = new AboutController();
        }
        aboutController.show();
    }

}
