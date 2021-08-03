# deltas-pack-fx

JavaFX方式增量打包SVN/GIT

**必须依照Eclipse、IDEA、Maven、Gradle等所创建的项目结构**


参考
===

* https://docs.oracle.com/javase/8/javafx/api
* [JavaFx 的布局(全)](https://www.jianshu.com/p/a699e42a0573)
* [JavaFX 学习笔记——窗口与控件](https://segmentfault.com/a/1190000018109949)
* [Java: JavaFX桌面GUI开发](https://blog.csdn.net/mouday/article/details/100186537)
* [JavaFX教程](https://blog.csdn.net/tianjh1129/article/details/114266972)
* [JavaFX登录页面简单实现](https://www.sdk.cn/details/9pPQD6wqK0Jo8ozvNy)
* https://zetcode.com/gui/javafx
* [JavaFX 控件 - 结构 （Control - Structure）](https://zhuanlan.zhihu.com/p/174838575)
* [JAVAFX-5事件总结](https://www.cnblogs.com/dgwblog/p/7955930.html)
* https://docs.oracle.com/javase/8/javafx/api/javafx/scene/doc-files/cssref.html
* [axure日期选择器控件_JavaFX 控件 - 输入 （Control - Inputs）](https://blog.csdn.net/weixin_34138192/article/details/113046126)
* [JavaFX专栏](https://blog.csdn.net/cnds123321/category_9743824.html)
* [JavaFX专栏](https://blog.csdn.net/weixin_44105483/category_10430844.html)
* [JavaFX专栏](https://blog.csdn.net/loongshawn/category_6482308.html)
* [JavaFX-TableView详解](https://www.jianshu.com/p/dc99fba6933c)
* [JavaFx loading 数据加载中效果](https://blog.csdn.net/cdc_csdn/article/details/80712813)
* [JavaFx 实现加载等待页](https://blog.csdn.net/weixin_44105483/article/details/108827400)
* [JavaFX桌面应用-loading界面](https://www.cnblogs.com/itqn/p/13543681.html)
* [javafx 遮罩_JavaFX技巧31：遮罩/剪切/ Alpha通道](https://blog.csdn.net/dnc8371/article/details/107257974)




IDEA打包
===

- `File` -> `Project Structure` -> `+` -> `JavaFx Application` -> `From module '项目名'` -> `Java FX`
    - `Application class`
    - `Title`
    - `Natjve bundle` 打包的格式，`all`为所有
- `Build` -> `Rebuild Project` 然后看项目中的out文件夹


TODO
===

支持Maven，思路：如果没有选择Maven目录，先查找项目中是否有创建项目时生成的相关脚本，如果没有则使用全局变量，如果还没有就查找变量中是否有Maven目录，没有则报错Maven路径必须选择

Gradle思路暂时与Maven一致


Screenshot
===

![image](https://user-images.githubusercontent.com/30252550/127959614-c2e35dd9-45b7-4d37-9095-af1a8de39708.png)
