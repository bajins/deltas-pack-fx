package com.bajins.deltaspackfx.utils;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class FxUtils {

    /**
     * 给指定元素添加样式
     *
     * @param node
     * @param styleClassName
     */
    public static void addStyleClass(Node node, String styleClassName) {
        if (node == null) {
            return;
        }
        if (styleClassName == null || styleClassName.trim().equals("") || Pattern.matches(",", styleClassName)) {
            return;
        }
        ObservableList<String> styleClass = node.getStyleClass();
        for (String aClass : styleClass) {
            if (styleClassName.equals(aClass)) {
                return;
            }
        }
        styleClass.add(styleClassName);
    }

    /**
     * 给指定元素添加样式
     *
     * @param node
     * @param styleClassName
     */
    public static void addStyleClass(MenuItem node, String styleClassName) {
        if (node == null) {
            return;
        }
        if (styleClassName == null || styleClassName.trim().equals("") || Pattern.matches(",", styleClassName)) {
            return;
        }
        ObservableList<String> styleClass = node.getStyleClass();
        for (String aClass : styleClass) {
            if (styleClassName.equals(aClass)) {
                return;
            }
        }
        styleClass.add(styleClassName);
    }

    /**
     * 解决Spinner中手动输入文本后必须按ENTER键才会更新值
     *
     * <p>
     * 复制自Spinner.commitEditorText() 此方法为私有
     * </p>
     *
     * @param spinner
     * @param <T>
     */
    public static <T> void commitEditorText(Spinner<T> spinner) {
        if (!spinner.isEditable()) {
            return;
        }
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                (SpinnerValueFactory.IntegerSpinnerValueFactory) spinner.getValueFactory();
        String text = spinner.getEditor().getText();
        if (StringUtils.isBlank(text)) {
            text = "-1";
        }
        Integer value = Integer.valueOf(text);
        if (value >= valueFactory.getMin() && value <= valueFactory.getMax()) {
            return;
        }
        if (value < valueFactory.getMin()) {
            value = Integer.valueOf(text);
            spinner.getEditor().setText(text);
        } else if (value > valueFactory.getMax()) {
            text = String.valueOf(valueFactory.getMax());
            value = Integer.valueOf(text);
            spinner.getEditor().setText(text);
        }
        valueFactory.setValue(value);
    }


    public static File borrowDirectoryPath(String title, File initPath) {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle(title);
        if (null != initPath) {
            dc.setInitialDirectory(initPath); // 设置打开初始地址
        }
        File file = dc.showDialog(new Stage());// 打开新窗口以选择文件夹
        return file;
    }

    public static File borrowDirectoryPath(String title, String initPath) {
        File file = null;
        if (StringUtils.isNotBlank(initPath)) {
            file = new File(initPath);
        }
        return borrowDirectoryPath(title, file);
    }

    public static File borrowDirectoryPath(String title) {
        return borrowDirectoryPath(title, "");
    }

    public static void main(String[] args) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择文件列表");
        // 过滤选择文件类型
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("图片类型", "*.jpg", "*.png"),
                new FileChooser.ExtensionFilter("文本类型", "*.txt")
        );
        //File file = fileChooser.showOpenDialog(new Stage()); // 打开新窗口以选择并获取文件
        List<File> list = fileChooser.showOpenMultipleDialog(new Stage()); // 选中了多个文件
        if (list == null) {
            return;
        }
        // 集合的forEach方法 传入一个Consumer接口 重写Consumer接口的accept（T t）方法
        // forEach方法使用增强for循环使集合的每个元素执行accept方法
        list.forEach(new Consumer<File>() {
            @Override
            public void accept(File t) {
                System.out.println(t.getAbsolutePath());
            }
        });
    }
}
