package com.bajins.deltaspackfx.controller;

import com.bajins.deltaspackfx.Main;
import com.bajins.deltaspackfx.common.FxDialogs;
import com.bajins.deltaspackfx.common.ProgressFrom;
import com.bajins.deltaspackfx.model.BaseFormVO;
import com.bajins.deltaspackfx.utils.BuildUtils;
import com.bajins.deltaspackfx.utils.FxUtils;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.decoration.Decorator;
import org.controlsfx.control.decoration.StyleClassDecoration;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.decoration.CompoundValidationDecoration;
import org.controlsfx.validation.decoration.GraphicValidationDecoration;
import org.controlsfx.validation.decoration.StyleClassValidationDecoration;
import org.controlsfx.validation.decoration.ValidationDecoration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class LocalController implements Initializable {

    @FXML
    protected GridPane gridPane; // 对应FXML文件中声明的fx:id属性
    @FXML
    protected TextField projectPath; // 项目路径
    @FXML
    protected Button projectPathBtn;
    @FXML
    protected TextField sourcePath; // 源码路径，默认: src
    @FXML
    protected Button sourcePathBtn;
    @FXML
    protected ComboBox targetPath; // 资源目录，默认: WebContent
    @FXML
    protected Button targetPathBtn;
    @FXML
    protected TextField classesPath; // 编译输出，默认: 资源目录/WEB-INF/classes
    @FXML
    protected Button classesPathBtn;
    @FXML
    protected ListView configList;
    @FXML
    protected Button configAddBtn;
    @FXML
    protected TextField outPath; // 存储路径，默认: 当前目录
    @FXML
    protected Button outPathBtn;
    @FXML
    protected DatePicker startDay;
    @FXML
    protected DatePicker endDay;
    @FXML
    protected StackPane bottomStackPane;

    // 校验样式
    protected static final StyleClassDecoration CLASS_DECORATION_ERROR = new StyleClassDecoration("error");

    protected static final ValidationDecoration ICON_DECORATOR = new GraphicValidationDecoration();
    protected static final ValidationDecoration VALIDATION_DECORATION = new StyleClassValidationDecoration();
    protected static final ValidationDecoration COMPOUND_VALIDATION_DECORATION =
            new CompoundValidationDecoration(VALIDATION_DECORATION, ICON_DECORATOR);

    // 校验全局管理，经测试：如果为父级，其他类需要继承则不能为 static，否则子类调用isInvalid()时永远为true
    protected final ValidationSupport VALIDATION_SUPPORT = new ValidationSupport();
    protected final ValidationSupport DATE_VALIDATION_SUPPORT = new ValidationSupport();

    protected static final ProgressFrom PROGRESS_FROM = new ProgressFrom(Main.topStage);

    /**
     * 会在Application.start(Stage primaryStage) 方法前运行
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initValidate();

        projectPathBtn.setOnAction(event -> {
            File file = FxUtils.borrowDirectoryPath("请选择一个项目");
            if (file != null && file.exists()) {
                projectPath.setText(file.getPath());
                Decorator.removeAllDecorations(projectPath); // 移除装饰样式
            }
        });
        sourcePath.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton().compareTo(MouseButton.PRIMARY) == 0) { // 实现双击事件
                clickDir("请选择一个源码文件夹", sourcePath);
            }
        });
        sourcePathBtn.setOnAction(event -> clickDir("请选择一个源码文件夹", sourcePath));
        targetPath.prefWidthProperty().bind(projectPath.widthProperty()); // 绑定宽度
        targetPath.getSelectionModel().selectedItemProperty().addListener(e -> {
            classesPath.setPromptText("默认: " + targetPath.getSelectionModel().getSelectedItem() + "/WEB-INF/classes");
        });
        targetPathBtn.setOnAction(event -> {
            clickDir("请选择存放静态资源的目录", targetPath);
            classesPath.setPromptText("默认: " + targetPath.getSelectionModel().getSelectedItem() + "/WEB-INF/classes");
        });

        initConfigListView();

        classesPath.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton().compareTo(MouseButton.PRIMARY) == 0) { // 实现双击事件
                clickDir("请选择一个编译输出文件夹", classesPath);
            }
        });
        classesPathBtn.setOnAction(event -> clickDir("请选择一个编译输出文件夹", classesPath));

        outPath.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton().compareTo(MouseButton.PRIMARY) == 0) { // 实现双击事件
                clickDir("请选择一个存储文件夹", outPath);
            }
        });
        outPathBtn.setOnAction(event -> clickDir("请选择一个存储文件夹", outPath));

        startDay.prefWidthProperty().bind(projectPath.widthProperty()); // 绑定宽度

        startDay.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                //setDisable(empty || date.getDayOfWeek() == DayOfWeek.MONDAY); // 禁止周一可选
                // 禁止一年之前或今天之后的日期可选
                LocalDate now = LocalDate.now();
                setDisable(empty || date.isBefore(now.minusYears(1)) || date.isAfter(now));
            }
        });
        endDay.prefWidthProperty().bind(projectPath.widthProperty()); // 绑定宽度
        endDay.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                //setDisable(empty || date.getDayOfWeek() == DayOfWeek.MONDAY); // 禁止周一可选
                // 禁止一年之前或今天之后的日期可选
                LocalDate now = LocalDate.now();
                LocalDate start = startDay.getValue();
                setDisable(empty || start == null || date.isBefore(start) || date.isAfter(now));
            }
        });
    }

    /**
     * 初始化必填校验
     */
    private void initValidate() {
        /*validationSupport.registerValidator(projectPath, (Control c, String newValue) ->
                ValidationResult.fromErrorIf(projectPath, "ComboBox Selection required",
                        newValue == null || newValue.isEmpty())
        );*/
        VALIDATION_SUPPORT.registerValidator(projectPath, Validator.createEmptyValidator("必填")); // 输入框注册校验

        DATE_VALIDATION_SUPPORT.registerValidator(startDay, true, (Control c, LocalDate newValue) -> {
            LocalDate endDayValue = endDay.getValue();
            return ValidationResult.fromErrorIf(startDay, "开始日期不能晚于结束日期",
                    newValue == null || (endDayValue != null && newValue.compareTo(endDayValue) > 0));
        });
        /*DATE_VALIDATION_SUPPORT.registerValidator(endDay, true, (Control c, LocalDate newValue) -> {
            LocalDate startDayValue = startDay.getValue();
            //System.out.println(startDayValue.compareTo(newValue));
            return ValidationResult.fromErrorIf(endDay, "结束日期不能早于开始日期",
                    startDayValue != null && startDayValue.compareTo(newValue) > 0);
        });*/
    }

    /**
     * 检查项目路径是否选择
     *
     * @return
     */
    protected boolean checkProjectPath() {
        if (StringUtils.isBlank(projectPath.getText())) {
            Decorator.addDecoration(projectPath, CLASS_DECORATION_ERROR); // 添加装饰样式
            return false;
        }
        Decorator.removeAllDecorations(projectPath); // 移除装饰样式
        return true;
    }

    /**
     * 初始化配置列表元素
     */
    private void initConfigListView() {
        configList.prefWidthProperty().bind(projectPath.widthProperty()); // 绑定宽度
        configList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); // 设置按住Ctrl可多选
        configList.setCellFactory(lv -> {
            ListCell<Object> cell = new ListCell<Object>() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(item.toString().replace(projectPath.getText() + File.separator, ""));
                    }
                }
            };
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (event.getButton().compareTo(MouseButton.SECONDARY) == 0 && !cell.isEmpty()) { // 在一行上单击鼠标右键
                    Object item = cell.getItem();
                    if (FxDialogs.showConfirmDefaultIsYes("确认要删除吗？")) {
                        configList.getItems().remove(item);
                    }
                }
            });
            return cell;
        });
        configList.setOnKeyReleased(event -> {
            System.out.println(event.getCode());
            if (event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE) {
                if (configList.getSelectionModel().getSelectedItem() == null) {
                    FxDialogs.showWarning("请先选中一行");
                    return;
                }
                if (FxDialogs.showConfirmDefaultIsYes("确认要删除吗？")) {
                    configList.getItems().remove(configList.getSelectionModel().getSelectedIndex());
                }
            }
        });
        configList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton().compareTo(MouseButton.PRIMARY) == 0) { // 实现双击事件
                clickDir("请选择一个配置文件夹", configList);
            }
        });
        configAddBtn.setOnAction(event -> {
            clickDir("请选择一个配置文件夹", configList);
        });
    }

    /**
     * 根据项目文件夹选择相对子文件夹
     *
     * @param title
     * @param node
     */
    protected void clickDir(String title, Node node) {
        if (!checkProjectPath()) {
            return;
        }
        String projectPathText = projectPath.getText();
        File file = FxUtils.borrowDirectoryPath(title, projectPathText);
        if (file != null && file.exists()) {
            String absolutePath = file.getAbsolutePath();
            if (!absolutePath.startsWith(projectPathText)) {
                FxDialogs.showError("请选择项目路径中的文件夹");
                return;
            }
            String dir = absolutePath.replace(projectPathText + File.separator, "");
            if (node instanceof TextField) {
                TextField textField = (TextField) node;
                textField.setText(dir);
            }
            if (node instanceof ListView) {
                ListView listView = (ListView) node;
                String filePath = file.getPath();
                ObservableList items = listView.getItems();
                if (items.contains(filePath)) {
                    FxDialogs.showError("选择的文件夹与列表中路径重合");
                    return;
                }
                listView.getItems().add(filePath);
            }
            if (node instanceof ComboBox) {
                ComboBox comboBox = (ComboBox) node;
                comboBox.getEditor().setText(dir);
                comboBox.setValue(dir);
            }
        }
    }

    /**
     * 检查并组装表单实体
     *
     * @return
     */
    public BaseFormVO checkForm() {
        if (VALIDATION_SUPPORT.isInvalid() || DATE_VALIDATION_SUPPORT.isInvalid()) { // 校验值
            // 通过Platform刷新UI
            Platform.runLater(() -> {
                VALIDATION_SUPPORT.setValidationDecorator(COMPOUND_VALIDATION_DECORATION); // 设置装饰样式
                VALIDATION_SUPPORT.initInitialDecoration(); // 初始化装饰
                DATE_VALIDATION_SUPPORT.setValidationDecorator(COMPOUND_VALIDATION_DECORATION); // 设置装饰样式
                DATE_VALIDATION_SUPPORT.initInitialDecoration(); // 初始化装饰
            });
            return null;
        }
        BaseFormVO formVO = new BaseFormVO();

        formVO.setProjectPath(projectPath.getText());

        ZoneOffset zoneOffset = ZoneOffset.of("+8");
        formVO.setStartTime(startDay.getValue().atTime(0, 0).toInstant(zoneOffset).toEpochMilli());
        LocalDate endDayValue = endDay.getValue();
        if (endDayValue != null) {
            formVO.setEndTime(endDayValue.atTime(23, 59, 59).toInstant(zoneOffset).toEpochMilli());
        }
        String sourcePathText = sourcePath.getText();
        if (StringUtils.isBlank(sourcePathText)) {
            formVO.setSourcePath("src");
        } else {
            formVO.setSourcePath(sourcePathText);
        }
        String targetPathValue = (String) targetPath.getValue();
        if (StringUtils.isBlank(targetPathValue)) {
            formVO.setTargetPath("webapp");
        } else {
            formVO.setTargetPath(targetPathValue);
        }
        String classesPathText = classesPath.getText();
        if (StringUtils.isBlank(classesPathText)) {
            formVO.setClassesPath(Paths.get(formVO.getTargetPath(), "WEB-INF", "classes").toString());
        } else {
            formVO.setClassesPath(classesPathText);
        }
        String outPathText = outPath.getText();
        if (StringUtils.isBlank(outPathText)) {
            formVO.setOutPath(System.getProperty("user.dir")); // 默认取当前路径
        } else {
            formVO.setOutPath(outPathText);
        }
        return formVO;
    }

    @FXML
    public void compileAction(ActionEvent event) { // 对应FXML文件中声明的onAction属性，以#开头
        PROGRESS_FROM.activateProgressBar();

        new Thread(() -> { // 开启新线程操作

            BaseFormVO formVO = checkForm();
            if (formVO == null) {
                return;
            }
            List<String> msg = new ArrayList<>();
            try (Stream<Path> entries = Files.walk(Paths.get(formVO.getProjectPath()))) { // 会进入子目录
                entries.forEach(f -> {
                    File file = f.toFile();
                    if (file.isFile() && file.lastModified() > formVO.getStartTime()) { // 如果文件修改时间大于开始时间
                        if (formVO.getEndTime() != null && file.lastModified() > formVO.getEndTime()) { // 文件修改时间大于结束时间
                            return;
                        }
                        try {
                            BuildUtils.processFile(f.toAbsolutePath().toString(), formVO.getProjectPath(), null,
                                    formVO.getSourcePath(), formVO.getTargetPath(), configList.getItems(),
                                    formVO.getOutPath(), formVO.getClassesPath());
                        } catch (IOException e) {
                            msg.add(e.getMessage());
                        } catch (IllegalArgumentException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                });
                // 通过Platform刷新UI
                Platform.runLater(() -> {
                    if (!msg.isEmpty()) {
                        FxDialogs.showError("编译错误", String.join(File.separator, msg));
                    } else {
                        FxDialogs.showInformation("编译完成");
                    }
                });
            } catch (IOException e) {
                // 通过Platform刷新UI
                Platform.runLater(() -> FxDialogs.showException(e.getMessage(), e));
            } finally {
                // 通过Platform刷新UI
                Platform.runLater(PROGRESS_FROM::cancelProgressBar);
            }
        }).start();
    }
}
