package com.bajins.deltaspackfx.controller;

import com.bajins.deltaspackfx.common.FxDialogs;
import com.bajins.deltaspackfx.model.BaseFormVO;
import com.bajins.deltaspackfx.utils.BuildUtils;
import com.bajins.deltaspackfx.utils.FxUtils;
import com.bajins.deltaspackfx.utils.SvnkitClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.decoration.Decorator;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class SvnController extends LocalController {

    @FXML
    private TextField svnUrl;
    @FXML
    private TextField svnUser;
    @FXML
    private PasswordField svnPwd;
    @FXML
    private Button validateSvnBtn;
    @FXML
    private CheckComboBox developer;
    @FXML
    private CheckComboBox svnVersion;

    // 校验全局管理，经测试：如果为父级，其他类需要继承则不能为 static，否则子类调用isInvalid()时永远为true
    protected final ValidationSupport SVN_VALIDATION_SUPPORT = new ValidationSupport();

    private SvnkitClient svnkitClient;

    /**
     * 会在Application.start(Stage primaryStage) 方法前运行
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources); // 先调用父级

        /*SVN_VALIDATION_SUPPORT.registerValidator(svnUrl, false, Validator.createRegexValidator("请输入正确的SVN地址",
                SvnkitClient.PROTOCOL_REGX.pattern(), Severity.ERROR)); // 输入框注册校验*/
        SVN_VALIDATION_SUPPORT.registerValidator(svnUrl, false, (Control c, String newValue) -> {
            boolean find = SvnkitClient.PROTOCOL_REGX.matcher(newValue).find();
            return ValidationResult.fromErrorIf(svnUrl, "请输入正确的SVN地址", StringUtils.isNotBlank(newValue) && !find);
        }); // 输入框注册校验
        SVN_VALIDATION_SUPPORT.registerValidator(svnUser, Validator.createEmptyValidator("必填")); // 输入框注册校验
        SVN_VALIDATION_SUPPORT.registerValidator(svnPwd, Validator.createEmptyValidator("必填")); // 输入框注册校验

        projectPathBtn.setOnAction(event -> {
            File file = FxUtils.borrowDirectoryPath("请选择一个SVN项目");

            if (file != null && file.exists()) {
                projectPath.setText(file.getPath());
            }
        });
        developer.prefWidthProperty().bind(projectPath.widthProperty()); // 绑定宽度
        svnVersion.prefWidthProperty().bind(projectPath.widthProperty()); // 绑定宽度

        svnUrl.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) { // 回车事件
                svnUser.requestFocus();
            }
        });
        svnUser.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) { // 回车事件
                svnPwd.requestFocus();
            }
        });
        validateSvnBtn.setOnAction(event -> getSvnAuthorAndVersion());
        developer.setDisable(true);
        developer.setOpacity(1);
        //developer.setTitle("请先输入SVN的地址、账户和密码");
        //developer.getItems().addAll(FXCollections.observableArrayList("请先输入SVN的url、账户和密码"));
        svnVersion.setDisable(true);
        svnVersion.setOpacity(1);
        //svnVersion.setTitle("请先输入SVN的地址、账户和密码");
        //svnVersion.getItems().addAll(FXCollections.observableArrayList("请先输入SVN的url、账户和密码"));
    }

    /**
     * 验证SVN信息
     *
     * @return
     */
    public boolean checkSvnInfo() {
        String svnUrlText = svnUrl.getText();
        String svnUserText = svnUser.getText();
        String svnPwdText = svnPwd.getText();

        if (VALIDATION_SUPPORT.isInvalid() || DATE_VALIDATION_SUPPORT.isInvalid() || SVN_VALIDATION_SUPPORT.isInvalid()) { // 校验值
            // 通过Platform刷新UI，以解决在独立线程中执行错误
            Platform.runLater(() -> {
                VALIDATION_SUPPORT.setValidationDecorator(COMPOUND_VALIDATION_DECORATION); // 设置装饰样式
                VALIDATION_SUPPORT.initInitialDecoration(); // 初始化装饰
                DATE_VALIDATION_SUPPORT.setValidationDecorator(COMPOUND_VALIDATION_DECORATION); // 设置装饰样式
                DATE_VALIDATION_SUPPORT.initInitialDecoration(); // 初始化装饰
                SVN_VALIDATION_SUPPORT.setValidationDecorator(COMPOUND_VALIDATION_DECORATION); // 设置装饰样式
                SVN_VALIDATION_SUPPORT.initInitialDecoration(); // 初始化装饰
            });
            return false;
        }
        boolean hasSvnUrlText = StringUtils.isBlank(svnUrlText);
        File svnDir = new File(projectPath.getText() + File.separator + ".svn");
        File parentFile = svnDir.getParentFile();
        if ((!svnDir.exists() || !SvnOperationFactory.isVersionedDirectory(parentFile)) && hasSvnUrlText) {
            // 通过Platform刷新UI，以解决在独立线程中执行错误
            Platform.runLater(() -> FxDialogs.showError(projectPath.getText()
                    + "选中的文件夹不是SVN项目，且未输入SVN地址", "温馨提示：要么选择SVN项目，要么输入SVN地址；"
                    + "如果SVN地址输入框有值，则以此为主"));
            return false;
        }
        if (hasSvnUrlText && null != svnkitClient) {
            svnUrlText = svnkitClient.getUrl();
        }
        try {
            if (null == svnkitClient) { // 为空则创建
                if (hasSvnUrlText) {
                    svnkitClient = new SvnkitClient(svnUserText, svnPwdText, parentFile);
                } else { // 如果URL输入框有值，则以此为主
                    svnkitClient = new SvnkitClient(svnUserText, svnPwdText, svnUrlText);
                }
            } else if (!svnkitClient.getUrl().equals(svnUrlText)
                    || !svnkitClient.getUserName().equals(svnUserText)
                    || !svnkitClient.getPassword().equals(svnPwdText)) { // 地址、账户、密码出现变化重新初始化
                svnkitClient.setUrl(svnUrlText);
                svnkitClient.setUserName(svnUserText);
                svnkitClient.setPassword(svnPwdText);
                svnkitClient.init();
            }
            /*if (!hasSvnUrlText) {
                SvnInfo svnInfo = svnkitClient.showInfo(parentFile);
                // svn地址与项目所属svn地址不一致
                if (svnInfo == null || svnInfo.getUrl() == null || !svnInfo.getUrl().toDecodedString().equals
                (svnUrlText)) {
                    Decorator.addDecoration(svnUrl, CLASS_DECORATION_ERROR); // 添加装饰样式
                    return false;
                }
            }*/
        } catch (SVNException e) {
            // 通过Platform刷新UI，以解决在独立线程中执行错误
            Platform.runLater(() -> {
                if (!hasSvnUrlText) {
                    Decorator.addDecoration(svnUrl, CLASS_DECORATION_ERROR); // 添加装饰样式
                }
                Decorator.addDecoration(svnUser, CLASS_DECORATION_ERROR); // 添加装饰样式
                Decorator.addDecoration(svnPwd, CLASS_DECORATION_ERROR); // 添加装饰样式
                FxDialogs.showError("请检查SVN地址、账户、密码", e.getMessage());
            });
            return false;
        }
        // 通过Platform刷新UI，以解决在独立线程中执行错误
        Platform.runLater(() -> {
            Decorator.removeDecoration(svnUrl, CLASS_DECORATION_ERROR); // 移除装饰样式
            Decorator.removeDecoration(svnUser, CLASS_DECORATION_ERROR); // 移除装饰样式
            Decorator.removeDecoration(svnPwd, CLASS_DECORATION_ERROR); // 移除装饰样式
        });
        return true;
    }

    /**
     * 获取SVN版本和作者
     */
    protected void getSvnAuthorAndVersion() {

        PROGRESS_FROM.activateProgressBar();

        new Thread(() -> { // 开启新线程操作
            if (!checkSvnInfo()) {
                PROGRESS_FROM.cancelProgressBar();
                return;
            }
            try {
                Date date = new Date();
                date.setTime(startDay.getValue().atTime(0, 0).toInstant(ZoneOffset.of("+8")).toEpochMilli());
                List<SVNLogEntry> logByVersion = svnkitClient.getLogByTime(date, null);
                Set<String> commitUsers = new HashSet<>();
                for (SVNLogEntry logEntry : logByVersion) {
                    commitUsers.add(logEntry.getAuthor());
                }
                long startVersion = svnkitClient.getRepository().getDatedRevision(date); // 根据时间查找版本
                long latestRevision = svnkitClient.getRepository().getLatestRevision(); // 最新一个版本

                // 通过Platform刷新UI，以解决在独立线程中执行错误
                Platform.runLater(() -> {
                    developer.setDisable(false);
                    developer.getItems().clear();
                    developer.getItems().addAll(FXCollections.observableArrayList(commitUsers));
                    List<Long> collect =
                            LongStream.range(startVersion, latestRevision).boxed().collect(Collectors.toList());
                    svnVersion.setDisable(false);
                    svnVersion.getItems().clear();
                    svnVersion.getItems().addAll(FXCollections.observableArrayList(collect));
                });
            } catch (SVNException e) {
                // 通过Platform刷新UI，以解决在独立线程中执行错误
                Platform.runLater(() -> FxDialogs.showError("请检查SVN地址、账户、密码", e.getMessage()));
            } finally {
                PROGRESS_FROM.cancelProgressBar();
            }
        }).start();
    }

    @FXML
    @Override
    public void compileAction(ActionEvent event) {

        PROGRESS_FROM.activateProgressBar();

        new Thread(() -> { // 开启新线程操作
            BaseFormVO formVO = checkForm();
            if (formVO == null) {
                PROGRESS_FROM.cancelProgressBar();
                return;
            }
            if (!checkSvnInfo()) {
                PROGRESS_FROM.cancelProgressBar();
                return;
            }
            ObservableList developers = developer.getCheckModel().getCheckedItems();
            Date startDate = new Date();
            startDate.setTime(formVO.getStartTime());
            Date endDate = null;
            if (formVO.getEndTime() != null) {
                endDate = new Date();
                endDate.setTime(formVO.getEndTime());
            }
            List<SVNLogEntry> svnLogEntries;
            List<String> msg = new ArrayList<>();

            try {
                if (developers != null && !developers.isEmpty()) {
                    svnLogEntries = svnkitClient.getLogByTimeUser(startDate, endDate, developers);
                } else {
                    svnLogEntries = svnkitClient.getLogByTime(startDate, endDate);
                }
                ObservableList svnVersions = svnVersion.getCheckModel().getCheckedItems();
                if (svnVersions != null && !svnVersions.isEmpty()) {
                    svnLogEntries.addAll(svnkitClient.getLogByVersion());
                }
                // 截取SVN最后一个节点
                String svnProjectName = svnkitClient.getUrl().substring(svnkitClient.getUrl().lastIndexOf(47) + 1);
                for (SVNLogEntry logEntry : svnLogEntries) {
                    Map<String, SVNLogEntryPath> changedPaths = logEntry.getChangedPaths();
                    for (SVNLogEntryPath logEntryPath : changedPaths.values()) { // 获取出现变化的路径
                        String path = logEntryPath.getPath();
                        if (logEntryPath.getType() != SVNLogEntryPath.TYPE_DELETED && !path.endsWith(".class")) {
                            String fullyPth = formVO.getProjectPath() + File.separator +
                                    path.substring(path.indexOf(svnProjectName) + svnProjectName.length() + 1);
                            File file = new File(fullyPth); // 转换路径为当前系统的正常路径
                            try {
                                BuildUtils.processFile(file.getAbsolutePath(), formVO.getProjectPath(), null,
                                        formVO.getSourcePath(), libSourceList.getItems(), formVO.getTargetPath(),
                                        configList.getItems(), formVO.getOutPath(), formVO.getClassesPath());
                            } catch (IOException e) {
                                msg.add(e.getMessage());
                            } catch (IllegalArgumentException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    }
                }
                // 通过Platform刷新UI，以解决在独立线程中执行错误
                Platform.runLater(() -> {
                    if (!msg.isEmpty()) {
                        FxDialogs.showError("编译错误", String.join(File.separator, msg));
                    } else {
                        FxDialogs.showInformation("编译完成");
                    }
                });
            } catch (SVNException | IllegalStateException e) {
                // 通过Platform刷新UI，以解决在独立线程中执行错误
                Platform.runLater(() -> FxDialogs.showException(e.getMessage(), e));
            } finally {
                PROGRESS_FROM.cancelProgressBar();
            }
        }).start();
    }
}
