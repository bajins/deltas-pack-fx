package com.bajins.deltaspackfx;

import com.bajins.deltaspackfx.controller.LocalController;
import javafx.application.Application;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.controlsfx.control.decoration.Decorator;
import org.controlsfx.control.decoration.GraphicDecoration;
import org.controlsfx.control.decoration.StyleClassDecoration;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * https://controlsfx.github.io/javadoc/11.1.0/org.controlsfx.controls/module-summary.html
 * https://github.com/controlsfx/controlsfx/tree/jfx-13/controlsfx-samples/src/main/java/org/controlsfx/samples
 * https://github.com/controlsfx/controlsfx/tree/jfx-13/controlsfx-samples/src/main/resources/org/controlsfx/samples
 * <p>
 * AbstractPropertyEditor使用CheckComboBox控件实现
 * https://stackoverflow.com/questions/36243863/add-checkcombobox-to-propertysheet-javafx
 */
public class TestControlsFX extends Application implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        /*final PopOver datePopOver = new PopOver();
        datePopOver.setTitle("Enter new date");
        datePopOver.setCornerRadius(10);
        datePopOver.setHeaderAlwaysVisible(true);
        datePopOver.setCloseButtonEnabled(true);
        datePopOver.setAutoHide(true);
        final StackPane stackPane = new StackPane(selectDay);
        stackPane.setPadding(new Insets(10, 10, 10, 10));
        datePopOver.setContentNode(stackPane);
        datePopOver.show(projectPathBtn);*/
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.sceneProperty().addListener(o -> {
            if (primaryStage.getScene() != null) {
                primaryStage.getScene().getStylesheets().add(
                        LocalController.class.getResource("/css/validation.css").toExternalForm()
                );
            }
        });

        // https://controlsfx.github.io/javadoc/11.1.0/org.controlsfx.controls/org/controlsfx/control/decoration/Decorator.html
        TextField textField = new TextField();
        Node requiredDecoration = new ImageView("/images/require.jpg");
        Decorator.addDecoration(textField, new GraphicDecoration(requiredDecoration, Pos.CENTER_RIGHT));

        Rectangle d = new Rectangle(7, 7);
        d.setFill(Color.RED);
        //d.setTranslateX(10);
        d.setOpacity(0.8);
        Decorator.addDecoration(textField, new GraphicDecoration(d, Pos.CENTER_LEFT));


        Decorator.addDecoration(textField, new StyleClassDecoration("warning"));
        Decorator.addDecoration(textField, new StyleClassDecoration("error"));


        // https://controlsfx.github.io/javadoc/11.1.0/org.controlsfx.controls/org/controlsfx/validation/ValidationSupport.html
        // https://github.com/controlsfx/controlsfx/blob/jfx-13/controlsfx-samples/src/main/java/org/controlsfx/samples/HelloValidation.java
        ValidationSupport validationSupport = new ValidationSupport();
        validationSupport.registerValidator(textField, Validator.createEmptyValidator("Text is required"));
        ValidationSupport.setRequired(textField, true); // 必填样式，必须放在注册之后才会生效

        validationSupport.registerValidator(textField, true, (Control c, String newValue) ->
                ValidationResult.fromErrorIf(textField, "ComboBox Selection required",
                        newValue == null || newValue.isEmpty())
        );
        validationSupport.invalidProperty().addListener((obs, wasInvalid, isNowInvalid) -> {
            if (isNowInvalid) {
                System.out.println("Invalid");
            } else {
                System.out.println("Valid");
            }
        });
        //Button okButton = new Button("OK");
        //okButton.disableProperty().bind(support.invalidProperty());

        Validator<String> validator = (control, value) -> { // 创建一个验证器
            boolean condition = value != null ? !value.matches(
                    "[\\x00-\\x20]*[+-]?(((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)([eE][+-]?(\\p{Digit}+))?)" +
                            "|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)" +
                            "|(((0[xX](\\p{XDigit}+)(\\.)?)" +
                            "|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))[pP][+-]?(\\p{Digit}+)))[fFdD]?))" +
                            "[\\x00-\\x20]*")
                    : value == null;

            System.out.println(condition);
            return ValidationResult.fromMessageIf(control, "not a number", Severity.ERROR, condition);
        };
        validationSupport.registerValidator(textField, true, validator);

    }

    public static void main(String[] args) {
        launch(args);
    }
}
