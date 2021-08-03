package com.bajins.deltaspackfx.common;

import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class FxDialogs {


    public static String YES = "Yes";
    public static String NO = "No";
    public static String OK = "OK";
    public static String CANCEL = "Cancel";

    static {
        Locale locale = Locale.getDefault();
        if (locale.equals(Locale.CHINA)) {
            OK = "确定";
            CANCEL = "取消";
            YES = "是";
            NO = "否";
        }
    }

    public static void showInformation(String title, String headerText, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        //alert.titleProperty().set(title);
        //alert.headerTextProperty().set(headerText);
        alert.setContentText(message);
        alert.setHeight(600);
        alert.showAndWait();
    }

    public static void showInformation(String headerText, String message) {
        Locale locale = Locale.getDefault();
        if (locale.equals(Locale.CHINA)) {
            showInformation("信息", headerText, message);
        } else {
            showInformation("Information", headerText, message);
        }
    }

    public static void showInformation(String message) {
        showInformation(null, message);
    }

    public static void showWarning(String title, String headerText, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(message);
        alert.setHeight(600);
        alert.showAndWait();
    }

    public static void showWarning(String headerText, String message) {
        Locale locale = Locale.getDefault();
        if (locale.equals(Locale.CHINA)) {
            showWarning("警告", headerText, message);
        } else {
            showWarning("Warning", headerText, message);
        }
    }

    public static void showWarning(String message) {
        showWarning(null, message);
    }

    public static void showError(String title, String headerText, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(message);
        alert.setHeight(600);
        alert.showAndWait();
    }

    public static void showError(String headerText, String message) {
        Locale locale = Locale.getDefault();
        if (locale.equals(Locale.CHINA)) {
            showError("错误", headerText, message);
        } else {
            showError("Error", headerText, message);
        }
    }

    public static void showError(String message) {
        showError(null, message);
    }

    public static void showException(String title, String headerText, String message, Exception exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(message);
        alert.setHeight(600);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        String exceptionText = sw.toString();

        String text;
        Locale locale = Locale.getDefault();
        if (locale.equals(Locale.CHINA)) {
            text = "明细:";
        } else {
            text = "Details:";
        }
        Label label = new Label(text);

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    public static void showException(String headerText, String message, Exception exception) {
        Locale locale = Locale.getDefault();
        if (locale.equals(Locale.CHINA)) {
            showException("异常", headerText, message, exception);
        } else {
            showException("Exception", headerText, message, exception);
        }
    }

    public static void showException(String message, Exception exception) {
        showException(null, message, exception);
    }

    /**
     * @param title
     * @param headerText 传null则不显示
     * @param message
     * @param options
     * @return
     */
    public static String showConfirm(String title, String headerText, String message, String... options) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initStyle(StageStyle.UTILITY); // 不在任务栏显示独立窗口
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        //alert.titleProperty().set(title);
        //alert.headerTextProperty().set(headerText);
        alert.setContentText(message);
        alert.setHeight(600);

        //To make enter key press the actual focused button, not the first one. Just like pressing "space".
        alert.getDialogPane().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                event.consume();
                try {
                    Robot r = new Robot();
                    r.keyPress(java.awt.event.KeyEvent.VK_SPACE);
                    r.keyRelease(java.awt.event.KeyEvent.VK_SPACE);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        if (options == null || options.length == 0) {
            options = new String[]{OK, CANCEL};
        }
        List<ButtonType> buttons = new ArrayList<>();
        for (String option : options) {
            buttons.add(new ButtonType(option));
        }
        alert.getButtonTypes().setAll(buttons);

        Optional<ButtonType> result = alert.showAndWait();
        if (!result.isPresent()) {
            return CANCEL;
        } else {
            return result.get().getText();
        }
    }

    public static String showConfirmDefault(String headerText, String message, String... options) {
        Locale locale = Locale.getDefault();
        if (locale.equals(Locale.CHINA)) {
            return showConfirm("选择一个选项", headerText, message, options);
        }
        return showConfirm("Choose an option", headerText, message, options);
    }

    public static String showConfirmDefault(String headerText, String message) {
        return showConfirm(headerText, message, FxDialogs.YES, FxDialogs.NO);
    }

    public static boolean showConfirmDefaultIsYes(String headerText, String message) {
        return showConfirmDefault(headerText, message, FxDialogs.YES, FxDialogs.NO).equals(FxDialogs.YES);
    }

    public static boolean showConfirmDefaultIsYes(String message) {
        return showConfirmDefaultIsYes(null, message);
    }

    public static String showTextInput(String title, String headerText, String message, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        dialog.setContentText(message);

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    public static String showTextInput(String headerText, String message, String defaultValue) {
        Locale locale = Locale.getDefault();
        if (locale.equals(Locale.CHINA)) {
            return showTextInput("输入", headerText, message, defaultValue);
        }
        return showTextInput("Input", headerText, message, defaultValue);
    }

    public static String showTextInput(String message, String defaultValue) {
        return showTextInput(null, message, defaultValue);
    }

}
