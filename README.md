# CooFly-i18n (酷飞国际化项目)

### 名词解释： 
* RCP：eclipse RCP & RAP (Knime, Kettle)
* Swt：eclipse swt gtk, win32, macos
* widgets组件：`org.eclipse.swt.widgets.Label`, `org.eclipse.swt.widgets.MenuItem`等

### 项目概要
可以实现 RCP 应用程序无倾入形式实现多语言能力。
目前主要支持 CooFly & Knime & Kettle 项目的多语言支持。

### 为什么需要这个项目
* **问**：Eclipse 的 `NLS (Native Language Support)` 支持 RCP 程序的i18n能力，为什么还要使用CooFly-i18n呢？
* **答**：当你的RCP应用程序很好的支持`NLS`的情况下，只需要添加`NLS`语言包，即可支持对应的语言。但是当RCP应用程序没有使用`NLS`的时候，你想在不修改源代码（或者你没有源代码）的情况下，你就可以用CooFly-i18n对RCP程序实现多语言支持。

### 项目特点
1. 对 RCP 平台版本无依赖
2. 无需倾入 RCP 应用程序代码，即可实现所有可见文字（菜单，按钮，弹窗等）的多语言支持

### 使用步骤
1. 克隆代码
    ```shell
    git clone https://github.com/cooflydata/coofly-i18n.git
    ```
2. 依赖安装，代码编译
    ```shell
    cd coofly-i18n && mvn install && mvn package
    ```
3. 覆盖RCP对应的swt jar包，**请确保构建的swt包文件名称与将要覆盖的swt文件名称完全一致。**
    ```shell
    # 备份
    cp /your/eclipse-rcp-install-dir/plugins/org.eclipse.swt_*.jar /your/eclipse-rcp-install-dir/plugins/org.eclipse.swt_*.jar_back
    # 替换
    cp eclipse-swt/target/org.eclipse.swt_*.jar /your/eclipse-rcp/plugins
    ```
4. 给RCP应用追加对应的`javagent`参数
    ```shell
    echo -javaagent:coofly-i18n\i18n-agent\target\i18n-agent-1.0-SNAPSHOT-jar-with-dependencies.jar >> /your/eclipse-rcp-install-dir/eclipse.ini
    ```

### 如何添加新的语言内容
1. 语言文件 [zh_CN.yaml](eclipse-swt%2Fsrc%2Fmain%2Fresources%2Fzh_CN.yaml) 简体中文配置文件
2. [理解如何填写配置文件](docs%2FHowConfigLang.md)

### 技术方案
* 动态机制(已经实现)
1. 本方案使用 `Java Instrumentation` 技术，在 `Class Load` 阶段，对`Swt`底层 `widgets组件` Class 进行改写，通过改写 `setText(String)` 等方法实现对显示文字的控制。
2. 该方式需要依赖 `javaagent` 机制

* 静态机制(暂未实现)
1. 本方案使用 `Java javassist` 动态重写 `Swt` 底层 `widgets组件`
       等`Class`代码，并对重写后的`Class`重写打`jar`包
2. 将重写后的`Swt`包替换掉现有`RCP`程序的`jar`包
3. 该方式不需要依赖 `javaagent` 机制
