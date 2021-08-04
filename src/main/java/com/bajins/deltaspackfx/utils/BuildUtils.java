package com.bajins.deltaspackfx.utils;


import org.apache.commons.lang3.StringUtils;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BuildUtils {

    /**
     * 获取JavaCompiler
     *
     * <a href="https://stackoverflow.com/questions/15513330/toolprovider-getsystemjavacompiler-returns-null-usable-with-only-jre-install"></a>
     *
     * @return
     */
    public static JavaCompiler getJavaCompiler() {
        String javaHome = System.getProperty("java.home");
        String jrePath = File.separator + "jre";
        if (javaHome.contains(jrePath)) {
            System.setProperty("java.home", javaHome.replace(jrePath, File.separator + "jdk"));
        }
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        if (javaCompiler == null) {
            throw new IllegalStateException("请检查JDK环境变量设置，当前环境变量：“" + javaHome + "”，需要“jdk/lib/tools.jar”");
        }
        return javaCompiler;
    }

    /**
     * 编译文件 <br/>
     * https://pfmiles.github.io/blog/dynamic-java<br/>
     * https://docs.oracle.com/javase/8/docs/api/javax/tools/package-summary.html<br/>
     * https://docs.oracle.com/javase/8/docs/api/javax/tools/JavaCompiler.html
     *
     * @param errors
     * @param javaFiles   要编译的Java源码文件，如果为空，则编译整个源码文件目录
     * @param libs        jar包文件，不能为文件夹，linux/mac 上cp分隔符使用: windows使用 ; 对应 File.pathSeparator
     * @param classesPath class输出目录
     * @param sourceDir   源码文件目录
     * @return
     */
    public static boolean compiler(DiagnosticCollector<JavaFileObject> errors, List<File> javaFiles, String libs,
                                   String classesPath, String sourceDir) throws IOException {
        JavaCompiler javaCompiler = getJavaCompiler();
        // 第一个参数：输入，默认System.in；第二个参数：输出，默认System.out；第三个参数：错误输出，默认System.err
        // 返回 0 表示成功， 其他表示出现了错误
        //int i = javaCompiler.run(null, null, null, "-encoding", "UTF-8", "-d", ".", "test/Hello.java");
        // 编译文件
        try (StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(errors, null, null)) {
            // 获取编译单元
            Iterable<? extends JavaFileObject> compilationUnits =
                    fileManager.getJavaFileObjectsFromFiles(javaFiles); // 编译单元
            // 编译执行的参数
            List<String> options = new ArrayList<>();
            options.add("-encoding");
            options.add("UTF-8");
            if (StringUtils.isNotBlank(libs)) { // 当fileManager不为null时，使用此方式无效
                options.add("-classpath"); // 或 -cp
                options.add(libs); // linux/mac 上cp分隔符使用: windows使用 ;
            }
            options.add("-d");
            options.add(classesPath);
            options.add("-sourcepath");
            options.add(sourceDir);

            JavaCompiler.CompilationTask task = javaCompiler.getTask(null, fileManager, errors, options, null,
                    compilationUnits);
            return task.call();
        }
    }

    /**
     * 编译文件<br/>
     * https://pfmiles.github.io/blog/dynamic-java<br/>
     * https://docs.oracle.com/javase/8/docs/api/javax/tools/package-summary.html<br/>
     * https://docs.oracle.com/javase/8/docs/api/javax/tools/JavaCompiler.html
     *
     * @param errors
     * @param javaFiles   要编译的Java源码文件，如果为空，则编译整个源码文件目录
     * @param libList     jar包文件，不能为文件夹
     * @param classesPath class输出目录
     * @param sourceDir   源码文件目录
     * @return
     */
    public static boolean compiler(DiagnosticCollector<JavaFileObject> errors, List<File> javaFiles,
                                   List<File> libList, String classesPath, String sourceDir) throws IOException {
        JavaCompiler javaCompiler = getJavaCompiler();
        // 编译文件
        try (StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(errors, null, null)) {
            /*JavaFileManager fm = new ForwardingJavaFileManager(fileManager) {
                public void flush() throws IOException {
                    super.flush();
                }
            };*/
            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singletonList(new File(classesPath)));
            // https://stackoverflow.com/questions/52601788/javacompiler-works-when-running-the-main-method-directly-but-not-when-running-sp
            fileManager.setLocation(StandardLocation.CLASS_PATH, libList); // 使用此方式替代在参数中添加-classpath参数
            fileManager.setLocation(StandardLocation.SOURCE_PATH, Collections.singletonList(new File(sourceDir)));
            // 获取编译单元
            Iterable<? extends JavaFileObject> compilationUnits =
                    fileManager.getJavaFileObjectsFromFiles(javaFiles); // 编译单元
            // 编译执行的参数
            List<String> options = new ArrayList<>();
            options.add("-encoding");
            options.add("UTF-8");

            JavaCompiler.CompilationTask task = javaCompiler.getTask(null, fileManager, errors, options, null,
                    compilationUnits);
            return task.call();
        }
    }

    /**
     * 普通项目（不含maven、gradlew、ant）处理单个文件，java编译，静态文件和jar包直接复制
     *
     * @param filePath         要处理的文件绝对路径
     * @param projectPath      项目绝对路径
     * @param projectName      项目名，可以包含在项目绝对路径中，为空默认取项目绝对路径最后一个目录名称
     * @param sourceDir        源码目录，相对于项目路径，如：src
     * @param resourcesDirList 资源目录，相对于项目路径，如：src/main/resources、config，子文件夹和文件会复制到classesPath中
     * @param targetDir        静态文件目录，相对于项目路径，如：WebContent；注意：普通项目路径为 项目名/WebContent
     * @param outPath          存储路径，默认: 当前目录
     * @param classesPath      编译输出，相对于项目路径，如: WebContent/WEB-INF/classes
     * @throws IOException
     */
    public static void processFile(String filePath, String projectPath, String projectName, String sourceDir,
                                   String targetDir, List<String> resourcesDirList, String outPath,
                                   String classesPath) throws IOException {
        // 忽略大小写
        Pattern pattern = Pattern.compile("\\.(git|svn|maven|gradlew|idea|settings)|class", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(filePath);
        if (matcher.find()) { // 如果配匹到第一个
            throw new IllegalArgumentException("非常规资源文件");
        }
        Path filePath_ = Paths.get(filePath); // 源文件
        File file = filePath_.toFile();
        if (!file.exists()) {
            throw new IOException("文件不存在");
        }
        if (!file.isFile()) {
            throw new IOException("不是文件");
        }
        // 源文件夹
        Path oldProjectPath = Paths.get(projectPath);
        Path oldTargetPath = oldProjectPath.resolve(targetDir); // 静态资源文件夹

        if (StringUtils.isBlank(projectName)) { // 如果项目名为空
            projectName = oldProjectPath.getName(oldProjectPath.getNameCount() - 1).toString();
        }
        File oldTargetFile = oldTargetPath.toFile();
        if (!oldTargetFile.exists() || !oldTargetFile.isDirectory()) {
            throw new IOException("源静态资源文件目录不存在");
        }
        // 目的地文件夹
        Path destProjectPath = Paths.get(outPath, projectName);
        Path destTargetPath = destProjectPath.resolve(targetDir);

        String oldTargetPathStr = oldTargetPath + File.separator;
        int index;
        if ((index = filePath.indexOf(oldTargetPathStr)) != -1) { // 如果是静态文件直接复制
            // 目标文件
            Path destPath = destTargetPath.resolve(filePath.substring(index + oldTargetPathStr.length()));
            File destFile = destPath.toFile();
            File parentFile = destFile.getParentFile();
            if (!parentFile.exists()) { // 如果目标文件所在文件夹不存在，则创建
                if (!parentFile.mkdirs()) { // 创建目录
                    throw new IOException("创建文件夹失败");
                }
                if (!destFile.createNewFile()) {// 创建文件
                    throw new IOException("创建文件失败");
                }
            }
            // 选项REPLACE_EXISTING表示想覆盖原有目标路径, COPY_ATTRIBUTES表示复制所有文件属性
            Files.copy(filePath_, destPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);

            return;
        }
        Path destClassesPath = hasClasses(destProjectPath, classesPath, destTargetPath);
        Path oldSourcePath = oldProjectPath.resolve(sourceDir); // 源码文件夹

        if (filePath.endsWith(JavaFileObject.Kind.SOURCE.extension)) { // java源码文件

            hasClasses(oldProjectPath, classesPath, oldTargetPath);
            // 待编译java文件
            List<File> javaFiles = new ArrayList<>();
            javaFiles.add(file);

            Path libPath = oldTargetPath.resolve("WEB-INF").resolve("lib"); // jar包的文件夹路径
            File libFile = libPath.toFile();
            List<File> libList = new ArrayList<>();
            //StringJoiner libs = new StringJoiner(File.pathSeparator);
            if (libFile.exists() && libFile.isDirectory()) {
                try (Stream<Path> entries = Files.walk(libPath)) { // 会进入子目录
                    entries.forEach(f -> {
                        File libPathFile = f.toFile();
                        if (libPathFile.isFile()) { // 获取jar包
                            libList.add(f.toFile());
                            //libs.add(f.toAbsolutePath().toString());
                        }
                    });
                }
            }
            String oldSourcePathStr = oldSourcePath.toString();
            DiagnosticCollector<JavaFileObject> errors = new DiagnosticCollector<>();

            //boolean res = compiler(errors, javaFiles, libs.toString(), destClassesPath.toString(), oldSourcePathStr);
            boolean res = compiler(errors, javaFiles, libList, destClassesPath.toString(), oldSourcePathStr);
            if (!res) {
                StringJoiner errorStringJoiner = new StringJoiner(File.separator);
                for (Diagnostic diagnostic : errors.getDiagnostics()) {
                    if (diagnostic.getKind() == Diagnostic.Kind.ERROR) {
                        //errorStringJoiner.add(diagnostic.toString());
                        errorStringJoiner.add(diagnostic.getMessage(null));
                    }
                }
                throw new IOException(errorStringJoiner.toString());
            }
            return;
        }
        String oldSourcePathStr = oldSourcePath.toString();

        if ((index = filePath.indexOf(oldSourcePathStr)) != -1) { // SRC目录下，非java源码文件，如mybatis的xml
            // 文件，复制到相应的包文件夹
            Path destPath = destClassesPath.resolve(filePath.substring(index + oldSourcePathStr.length() + 1));
            File destFile = destPath.toFile(); // 目标文件

            File parentFile = destFile.getParentFile();
            if (!parentFile.exists() && !parentFile.mkdirs()) { // 根据包路径创建文件夹
                throw new IOException("创建包路径文件夹失败");
            }
            if (!destFile.createNewFile()) {
                throw new IOException("创建文件失败");
            }
            // 选项REPLACE_EXISTING表示想覆盖原有目标路径, COPY_ATTRIBUTES表示复制所有文件属性
            Files.copy(filePath_, destPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);

            return;
        }
        for (String resourcesDir : resourcesDirList) {
            Path oldResourcesPath = oldProjectPath.resolve(resourcesDir); // 资源文件夹
            String oldResourcesPathStr = oldResourcesPath + File.separator;

            if (filePath.contains(oldResourcesPathStr)) { // 如果是资源文件，如：properties、yaml、xml等后缀文件，直接复制到classes文件夹
                Path destPath = destClassesPath.resolve(filePath.substring(oldResourcesPathStr.length()));
                File destFile = destPath.toFile(); // 目标文件

                File parentFile = destFile.getParentFile();
                if (!parentFile.exists() && !parentFile.mkdirs()) { // 根据包路径创建文件夹
                    throw new IOException("创建包路径文件夹失败");
                }
                // 选项REPLACE_EXISTING表示想覆盖原有目标路径, COPY_ATTRIBUTES表示复制所有文件属性
                Files.copy(filePath_, destPath, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.COPY_ATTRIBUTES);
            }
        }
    }

    /**
     * 如果编译后的class文件夹不存在则创建
     *
     * @param classesPath
     * @param targetPath
     * @return
     * @throws IOException
     */
    private static Path hasClasses(Path projectPath, String classesPath, Path targetPath) throws IOException {
        Path path;
        if (StringUtils.isBlank(classesPath)) {
            path = targetPath.resolve("WEB-INF").resolve("classes");
        } else {
            path = projectPath.resolve(classesPath);
        }
        File pathFile = path.toFile();
        if (!pathFile.exists() && !pathFile.mkdirs()) {
            throw new IOException("创建classes文件夹失败");
        }
        if (!pathFile.isDirectory()) {
            throw new IOException("不是文件夹");
        }
        return path;
    }

    public static void main(String[] args) {
        long l = LocalDateTime.now().minusDays(10).toInstant(ZoneOffset.of("+8")).toEpochMilli();

        Path path = Paths.get("D:\\eclipse-workspace\\ims-erp");
        try (Stream<Path> entries = Files.walk(path)) { // 会进入子目录
            entries.forEach(f -> {
                File file = f.toFile();
                if (file.isFile() && file.lastModified() > l) { // 如果文件修改时间大于指定时间
                    System.out.print(file.lastModified());
                    System.out.print("\t");
                    System.out.print(l);
                    System.out.print("\t");
                    System.out.print(file.lastModified() > l);
                    System.out.print("\t");
                    System.out.println(f);
                    try {
                        processFile(f.toAbsolutePath().toString(), path.toAbsolutePath().toString(), null, "src",
                                "webapp", Arrays.asList("config", "configdev"), "D:\\", null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


/**
 * 自定义字符串源文件对象
 */
class JavaSourceFromString extends SimpleJavaFileObject {
    /**
     * The source code of this "file".
     */
    final String code;

    /**
     * Constructs a new JavaSourceFromString.
     *
     * @param name the name of the compilation unit represented by this file object
     * @param code the source code for the compilation unit represented by this file object
     */
    JavaSourceFromString(String name, String code) {
        super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}
