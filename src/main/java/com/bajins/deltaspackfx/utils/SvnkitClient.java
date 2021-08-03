package com.bajins.deltaspackfx.utils;

import org.apache.commons.lang3.StringUtils;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.db.SVNSqlJetDb;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.internal.wc17.db.ISVNWCDb;
import org.tmatesoft.svn.core.internal.wc17.db.SVNWCDb;
import org.tmatesoft.svn.core.internal.wc17.db.SVNWCDbDir;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.*;
import org.tmatesoft.svn.core.wc2.*;

import java.io.File;
import java.security.Security;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * https://wiki.svnkit.com
 */
public class SvnkitClient {

    public static final Pattern PROTOCOL_REGX = Pattern.compile("((svn|http|https)://|file:///)[a-zA-Z0-9]+.*");

    private String userName;
    private String password;
    private String url;

    private ISVNAuthenticationManager authManager; // 认证管理：所有客户端类可用此认证初始化
    private SVNClientManager svnClientManager; // 客户端管理：包含所有客户端类
    private DefaultSVNOptions options; // svn的参数 ISVNOptions
    private SVNRepository repository; // 仓库

    // 更新状态 true:没有程序在执行更新，反之则反
    public static Boolean DoUpdateStatus = true;

    static {
        Security.setProperty("jdk.tls.disabledAlgorithms", ""); // ssl
        // 初始化库，必须先执行此操作。具体操作封装在setupLibrary方法中。
        // For using over http:// and https://
        DAVRepositoryFactory.setup();
        // For using over svn:// and svn+xxx://
        SVNRepositoryFactoryImpl.setup();
        // For using over file:///
        FSRepositoryFactory.setup();
    }

    private SvnkitClient() {
    }

    /**
     * 构造方法，先初始化认证管理器，然后在根据本地文件夹获取SVN信息初始化仓库
     *
     * @param user     用户名
     * @param password 密码
     * @param file     SVN本地仓库副本
     * @throws SVNException
     */
    public SvnkitClient(String user, String password, File file) throws SVNException {
        this.userName = user;
        this.password = password;

        initManager();

        SvnInfo svnInfo = showInfo(file);
        this.url = svnInfo.getUrl().toDecodedString();

        initRepository();
    }

    /**
     * 构造方法，会初始化认证管理器和仓库
     *
     * @param user     用户名
     * @param password 密码
     * @param url      SVN远程地址：svn://、http://、https://、file:/// 开头
     * @throws SVNException
     */
    public SvnkitClient(String user, String password, String url) throws SVNException {
        this.userName = user;
        this.password = password;
        if (!PROTOCOL_REGX.matcher(url).find()) {
            this.url = "file:///" + url;
        } else {
            this.url = url;
        }
        init();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ISVNAuthenticationManager getAuthManager() {
        return authManager;
    }

    public SVNClientManager getSvnClientManager() {
        return svnClientManager;
    }

    public DefaultSVNOptions getOptions() {
        return options;
    }

    public SVNRepository getRepository() {
        return repository;
    }

    /**
     * 初始化
     *
     * <pre> 可以拿到关键的几个对象
     * SVNWCClient wcClient = svnClientManager.getWCClient();
     * SvnOperationFactory operationFactory = svnClientManager.getOperationFactory();
     * if (operationFactory == null) {
     *     operationFactory = svnClientManager.getWCClient().getOperationsFactory();
     * }
     * SVNWCContext wcContext = operationFactory.getWcContext();
     * ISVNWCDb db = wcContext.getDb();
     * </pre>
     *
     * @throws SVNException
     */
    public void initManager() throws SVNException {
        authManager = SVNWCUtil.createDefaultAuthenticationManager(userName, password.toCharArray());
        options = SVNWCUtil.createDefaultOptions(true);
        options.setDiffCommand("-x -w");
        svnClientManager = SVNClientManager.newInstance(this.options, this.authManager);
        //svnClientManager = SVNClientManager.newInstance(this.options, this.userName, this.password);
    }

    public void initRepository() throws SVNException {
        repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
        //repository = SVNRepositoryFactoryImpl.create(SVNURL.parseURIEncoded(rootUrl));
        repository.setAuthenticationManager(authManager);
        if (!repository.getLocation().getProtocol().equals("file")) {
            repository.testConnection(); // 测试链接
        }

        /**
         * 检验某个URL（可以是文件、目录）是否在仓库历史的修订版本中存在，参数：被检验的URL，修订版本，这里我们想要打印出目录树，所以要求必须是目录
         * SVNNodeKind的枚举值有以下四种：
         *  SVNNodeKind.NONE    这个node已经丢失（可能是已被删除）
         *  SVNNodeKind.FILE    文件
         *  SVNNodeKind.DIR     目录
         *  SVNNodeKind.UNKNOW  未知，无法解析
         **/
        /*
         *  被检验的URL，本例有两种等价的写法。
         *  1.不是以"/"开头的是相对于仓库驱动目录的相对目录，即svnRepository的url，
         *      在本例中是：空字符串（url目录是：https://127.0.0.1:8443/svn/test/trunk）
         *  2.以"/"开头的是相对于svnRepository root目录的相对目录，即svnRepository的rootUrl，
         *      在本例中是：/trunk（root目录是https://127.0.0.1:8443/svn/test）
         */
        //SVNNodeKind svnNodeKind = repository.checkPath("",1);
    }

    public void init() throws SVNException {
        initManager();
        initRepository();
    }


    /**
     * https://wiki.svnkit.com/Printing_Out_Repository_History
     *
     * @param path
     * @param startVersion 起始版本为1
     * @param endVersion   -1为最新版本 SVNRepository.INVALID_REVISION
     * @return
     * @throws SVNException
     */
    public List<SVNLogEntry> getLogByVersion(String[] path, long startVersion, long endVersion) throws SVNException {
        return (List<SVNLogEntry>) repository.log(path, null, startVersion, endVersion, true, true);
    }

    public List<SVNLogEntry> getLogByVersion(long startVersion, long endVersion) throws SVNException {
        return getLogByVersion(new String[]{""}, startVersion, endVersion);
    }

    /**
     * 获取仓库所有版本的日志
     *
     * @return
     * @throws SVNException
     */
    public List<SVNLogEntry> getLogByVersion() throws SVNException {
        return getLogByVersion(1, SVNRepository.INVALID_REVISION);
    }

    /**
     * 获取一段时间内，所有的commit记录
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return
     * @throws SVNException
     */
    public List<SVNLogEntry> getLogByTime(Date start, Date end) throws SVNException {
        if (start == null) {
            throw new IllegalArgumentException("开始时间不能为空");
        }
        if (end == null) {
            end = new Date();
        }
        // 获取某一个时间点的版本号，如果用作开始时间，应该版本号+1，因为我们想要获取的是后一个版本
        long startRevision = repository.getDatedRevision(start) + 1;
        long endRevision = SVNRepository.INVALID_REVISION; // -1最新版本，同repository.getLatestRevision();
        if (null != end) {
            endRevision = repository.getDatedRevision(end);
        }
        return getLogByVersion(new String[]{""}, startRevision, endRevision);
    }

    /**
     * 获取某一用户时间范围提交的记录
     *
     * @param start  开始时间
     * @param end    结束时间
     * @param author 用户名
     * @return
     * @throws SVNException
     */
    public List<SVNLogEntry> getLogByTimeUser(Date start, Date end, String author) throws SVNException {
        if (StringUtils.isBlank(author)) {
            throw new IllegalArgumentException("用户不能为空");
        }
        List<SVNLogEntry> logEntries = getLogByTime(start, end);
        //return Arrays.stream(logEntries).filter(entry -> author.equals(entry.getAuthor())).parallel()
        // .collect(Collectors.toList());
        List<SVNLogEntry> authorLogEntryList = new ArrayList<>();
        for (SVNLogEntry entry : logEntries) {
            if (author.equals(entry.getAuthor())) {
                authorLogEntryList.add(entry);
            }
        }
        return authorLogEntryList;
    }

    /**
     * 获取多个用户时间范围内提交的记录
     *
     * @param start   开始时间
     * @param end     结束时间
     * @param authors 多个用户
     * @return
     * @throws SVNException
     */
    public List<SVNLogEntry> getLogByTimeUser(Date start, Date end, String[] authors) throws SVNException {
        if (authors == null || authors.length == 0) {
            throw new IllegalArgumentException("用户不能为空");
        }
        List<SVNLogEntry> logEntries = getLogByTime(start, end);
        Map<String, String> authorMap = Arrays.stream(authors).collect(Collectors.toMap(k -> k, Function.identity()));

        List<SVNLogEntry> authorLogEntryList = new ArrayList<>();
        for (SVNLogEntry entry : logEntries) {
            if (authorMap.containsKey(entry.getAuthor())) {
                authorLogEntryList.add(entry);
            }
        }
        return authorLogEntryList;
    }

    public List<SVNLogEntry> getLogByTimeUser(Date start, Date end, List<String> authors) throws SVNException {
        if (authors == null || authors.isEmpty()) {
            throw new IllegalArgumentException("用户不能为空");
        }
        List<SVNLogEntry> logEntries = getLogByTime(start, end);
        Map<String, String> authorMap = authors.stream().collect(Collectors.toMap(k -> k, Function.identity()));

        List<SVNLogEntry> authorLogEntryList = new ArrayList<>();
        for (SVNLogEntry entry : logEntries) {
            if (authorMap.containsKey(entry.getAuthor())) {
                authorLogEntryList.add(entry);
            }
        }
        return authorLogEntryList;
    }

    /**
     * 获取版本范围有变动的文件路径
     *
     * @param url          远程地址，可能报错：E160013、E170001、E175002
     * @param path         路径
     * @param pegVersion
     * @param startVersion 开始版本
     * @param endVersion   结束版本，-1为最新版本 SVNRepository.INVALID_REVISION
     * @param limit        分页
     * @return
     * @throws SVNException
     */
    public List<SVNLogEntry> getLogByLogClient(String url, String[] path, long pegVersion, long startVersion,
                                               long endVersion, Long limit) throws SVNException {
        if (path == null || path.length == 0) {
            path = new String[]{""};
        }
        if (limit == null) {
            limit = 9999L;
        }
        if (StringUtils.isBlank(url)) {
            url = this.url;
        }
        SVNLogClient logClient = svnClientManager.getLogClient();
        //SVNLogClient logClient = new SVNLogClient(this.authManager, this.options);
        SVNURL svnurl = SVNURL.parseURIEncoded(url);
        SVNRevision pegRevision = SVNRevision.create(pegVersion);
        SVNRevision startRevision = SVNRevision.create(startVersion);
        SVNRevision endRevision = SVNRevision.create(endVersion);

        final List<SVNLogEntry> svnLogEntrys = new ArrayList<>();

        final ISVNLogEntryHandler handler = new ISVNLogEntryHandler() {
            /**
             * doLog() 完成后，将处理此方法
             */
            @Override
            public void handleLogEntry(SVNLogEntry logEntry) {
                svnLogEntrys.add(logEntry);
            }
        };
        logClient.doLog(svnurl, path, pegRevision, startRevision, endRevision, false, true, limit, handler);
        return svnLogEntrys;
    }

    /**
     * SVN检出
     *
     * @param targetPath 本地目录路径
     * @return 执行check out 操作，返回工作副本的版本号
     * @throws SVNException
     */
    public long checkOutByUpdateClient(String targetPath) throws SVNException {
        // 相关变量赋值
        SVNURL repositoryURL = SVNURL.parseURIEncoded(this.url);
        // 要把版本库的内容check out到的目录
        File wcDir = new File(targetPath);

        // 通过客户端管理类获得updateClient类的实例。
        SVNUpdateClient updateClient = svnClientManager.getUpdateClient();
        updateClient.setIgnoreExternals(false);
        if (wcDir.exists()) {
            svnClientManager.getWCClient().doCleanup(wcDir);
        }
        // 执行check out 操作，返回工作副本的版本号。
        return updateClient.doCheckout(repositoryURL, wcDir, SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY,
                false);
    }

    /**
     * 更新svn
     *
     * @param targetPath 本地目录路径
     * @return 0有程序在占用更新
     * @throws SVNException
     */
    public long doUpdateByUpdateClient(String targetPath) throws SVNException {
        if (!DoUpdateStatus) {
            return 0;
        }
        DoUpdateStatus = false;

        // 要更新的文件
        File updateFile = new File(targetPath);
        // 获得updateClient的实例
        SVNUpdateClient updateClient = svnClientManager.getUpdateClient();
        updateClient.setIgnoreExternals(false);
        // 执行更新操作
        long versionNum = updateClient.doUpdate(updateFile, SVNRevision.HEAD, SVNDepth.INFINITY, false, false);
        DoUpdateStatus = true;
        return versionNum;
    }

    /**
     * 将文件导入并提交到svn 同路径文件要是已经存在将会报错
     *
     * @param dirPath 需要导入的目录路径
     * @return
     * @throws SVNException
     */
    public SVNCommitInfo doImportByCommitClient(String dirPath, String commitMessage) throws SVNException {
        if (StringUtils.isBlank(commitMessage)) {
            commitMessage = "import operation!";
        }
        // 相关变量赋值
        SVNURL repositoryURL = SVNURL.parseURIEncoded(this.url);

        // 要把此目录中的内容导入到版本库
        File impDir = new File(dirPath);
        if (!impDir.exists()) {
            return null;
        }
        SVNCommitClient commitClient = svnClientManager.getCommitClient();
        // 执行导入操作
        return commitClient.doImport(impDir, repositoryURL, commitMessage, null, false, false, SVNDepth.INFINITY);
    }

    /**
     * 解除svn Luck
     *
     * @param targetPath 本地目录路径
     * @throws SVNException
     */
    public void doCleanupByWCClient(String targetPath) throws SVNException {
        // 要把版本库的内容check out到的目录
        File wcDir = new File(targetPath);
        if (wcDir.exists()) {
            SVNWCClient wcClient = svnClientManager.getWCClient();
            wcClient.doCleanup(wcDir);
        }
    }

    /**
     * 获取本地文件夹SVN信息<br>
     * https://wiki.svnkit.com/Managing_A_Working_Copy#line-418
     *
     * @param wcPath
     * @param revision
     * @param isRecursive true：获取最近一次操作的信息，false：获取整个项目的信息
     * @throws SVNException
     */
    public SVNInfo showInfoByWcClient(File wcPath, SVNRevision revision, boolean isRecursive) throws SVNException {
        SVNWCClient wcClient = svnClientManager.getWCClient();
        AtomicReference<SVNInfo> svnInfos = new AtomicReference<>();
        wcClient.doInfo(wcPath, SVNRevision.WORKING, revision, SVNDepth.getInfinityOrEmptyDepth(isRecursive), null,
                svnInfos::set);
        return svnInfos.get();
    }

    /**
     * @param wcPath
     * @param isRecursive true：获取最近一次操作的信息，false：获取整个项目的信息
     * @return
     * @throws SVNException
     */
    public SVNInfo showInfoByWcClient(File wcPath, boolean isRecursive) throws SVNException {
        return showInfoByWcClient(wcPath, SVNRevision.WORKING, isRecursive);
    }

    /**
     * 列出有修改的文件
     *
     * @param path
     * @param revision
     * @return
     * @throws SVNException
     */
    public static List<File> listModifiedFiles(File path, SVNRevision revision) throws SVNException {
        SVNClientManager svnClientManager = SVNClientManager.newInstance();
        final List<File> fileList = new ArrayList<>();
        svnClientManager.getStatusClient().doStatus(path, revision, SVNDepth.INFINITY, false, false, false, false,
                status -> {
                    SVNStatusType statusType = status.getContentsStatus();
                    if (statusType != SVNStatusType.STATUS_NONE && statusType != SVNStatusType.STATUS_NORMAL && statusType != SVNStatusType.STATUS_IGNORED) {
                        fileList.add(status.getFile());
                    }
                }, null);
        return fileList;
    }

    /**
     * 通过SvnOperationFactory获取日志<br/>
     * 从 SVNKit 1.7 开始，SvnOperationFactory是首选方式。SVNKit 仍然包含SVNClientManager提供类似功能但它被认为是过时的<br/>
     * https://stackoverflow.com/questions/23611255/getting-all-revisions-with-svnkit
     *
     * @throws SVNException
     */
    public static List<SVNLogEntry> getLog(SvnOperationFactory operationFactory, File file) throws SVNException {
        if (operationFactory == null) {
            operationFactory = new SvnOperationFactory();
        }
        SvnLog logOperation = operationFactory.createLog();

        logOperation.setSingleTarget(SvnTarget.fromFile(file));
        SvnRevisionRange svnRevisionRange = SvnRevisionRange.create(SVNRevision.WORKING, SVNRevision.HEAD);
        logOperation.setRevisionRanges(Collections.singleton(svnRevisionRange));
        return (List<SVNLogEntry>) logOperation.run(null);
    }

    public static List<SVNLogEntry> getLog(File file) throws SVNException {
        return getLog(null, file);
    }

    public List<SVNLogEntry> getLogThis(File file) throws SVNException {
        SvnOperationFactory operationFactory = svnClientManager.getOperationFactory();
        if (operationFactory == null) {
            operationFactory = svnClientManager.getWCClient().getOperationsFactory();
        }
        return getLog(operationFactory, file);
    }

    /**
     * 获取当前仓库信息
     *
     * @param operationFactory
     * @param file
     * @param depth
     * @param revision
     * @return
     * @throws SVNException
     */
    public static SvnInfo showInfo(SvnOperationFactory operationFactory, File file, SVNDepth depth,
                                   SVNRevision revision) throws SVNException {
        if (operationFactory == null) {
            operationFactory = new SvnOperationFactory();
        }
        final SvnGetInfo infoOp = operationFactory.createGetInfo();
        infoOp.setSingleTarget(SvnTarget.fromFile(file));
        infoOp.setDepth(depth);
        infoOp.setRevision(revision);
        final SvnInfo svnInfo = infoOp.run();
        return svnInfo;
    }

    public static SvnInfo showInfo(SvnOperationFactory operationFactory, File file, SVNRevision revision) throws SVNException {
        return showInfo(operationFactory, file, SVNDepth.EMPTY, revision);
    }

    public static SvnInfo showInfo(SvnOperationFactory operationFactory, File file) throws SVNException {
        return showInfo(operationFactory, file, SVNRevision.WORKING);
    }

    public SvnInfo showInfo(File file) throws SVNException {
        SvnOperationFactory operationFactory = svnClientManager.getOperationFactory();
        if (operationFactory == null) {
            operationFactory = svnClientManager.getWCClient().getOperationsFactory();
        }
        return showInfo(operationFactory, file);
    }

    /**
     * 获取仓库根信息
     *
     * @param file
     * @return
     * @throws SVNException
     */
    public SVNWCDb.ReposInfo showRootInfo(File file) throws SVNException {
        SVNWCDb.ReposInfo reposInfo = null;

        SVNWCDb db = new SVNWCDb();
        db.open(ISVNWCDb.SVNWCDbOpenMode.ReadOnly, null, false, false);
        SVNWCDb.DirParsedInfo parsedInfo = db.parseDir(file.getAbsoluteFile(), SVNSqlJetDb.Mode.ReadOnly, true, false);
        if (parsedInfo != null && SVNWCDbDir.isUsable(parsedInfo.wcDbDir)) {
            ISVNWCDb.WCDbInfo nodeInfo = db.readInfo(file.getAbsoluteFile(), ISVNWCDb.WCDbInfo.InfoField.status,
                    ISVNWCDb.WCDbInfo.InfoField.kind);
            reposInfo = db.fetchReposInfo(parsedInfo.wcDbDir.getWCRoot().getSDb(), nodeInfo.reposId);
            /*if (nodeInfo != null && nodeInfo.kind != ISVNWCDb.SVNWCDbKind.Dir) {

            }*/
        }
        return reposInfo;
    }


    public static void main(String[] args) throws SVNException {
        // 提示svn: E160013 或 svn: E175002 错误时，请输入正确的仓库url
        //SvnkitClient svnkitEntity = new SvnkitClient("user", "pwd", "https://127.0.0.1:443/test");
        /*SvnkitClient svnkitClient = new SvnkitClient("dingdehang", "dingdh@101",
                "https://14.18.232.142:8433/svn/IMSV3/source/xm/dingtaik");*/

        String repoPth = "D:\\eclipse-workspace\\ims-erp";
        SvnkitClient svnkitClient = new SvnkitClient("dingdehang", "dingdh@101", repoPth);
        File file = new File(repoPth);
        //SvnkitClient svnkitClient = new SvnkitClient("dingdehang", "dingdh@101", file);

        System.out.println(svnkitClient.getRepository().getLocation().getProtocol());

        System.out.println(svnkitClient.repository.getLatestRevision()); // 最新版本，如果是file:///协议无法使用
        System.out.println(SVNWCUtil.isVersionedDirectory(file)); // 判断是否为SVN版本控制目录
        System.out.println(SvnOperationFactory.isVersionedDirectory(file)); // 判断是否为SVN版本控制目录

        SVNInfo svnInfo = svnkitClient.showInfoByWcClient(file, false);
        System.out.println(svnInfo == null ? null : svnInfo.getURL());

        SVNDirEntry info = svnkitClient.repository.info("", -1); // -1获取项目信息
        System.out.println(info == null ? null : info.getURL());

        SvnInfo svnInfo1 = svnkitClient.showInfo(file);
        System.out.println(svnInfo1 == null ? null : svnInfo1.getUrl());

        System.out.println(svnkitClient.showRootInfo(file).reposRootUrl);

        Date date = new Date();
        date.setTime(LocalDateTime.now().minusDays(30).toInstant(ZoneOffset.of("+8")).toEpochMilli());
        List<SVNLogEntry> logEntries = svnkitClient.getLogByTime(date, null);
        //Set<String> commitUsers = new HashSet<>();
        for (SVNLogEntry logEntry : logEntries) {
            Map<String, SVNLogEntryPath> changedPaths = logEntry.getChangedPaths();
            changedPaths.forEach((k, v) -> {
                System.out.println(v.getPath());
            });
            //commitUsers.add(logEntry.getAuthor());
        }
    }
}
