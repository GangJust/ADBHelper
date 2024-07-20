package i18n

import java.util.*

object StringRes {

    val language: String
        get() = Locale.getDefault().language

    val locale: Language
        get() = when (Locale.getDefault()) {
            Locale.ENGLISH -> EN
            else -> CN
        }

    ///
    interface Language {
        val title: String

        val title1: String

        val simpleTitle: String

        val initDescription: String

        val initPlaceholder: String

        val initNext: String

        val initSuccess: String

        val noDeviceConnection: String

        val notConnected: String

        val connect: String

        val noDeviceSelected: String

        val deviceList: String

        val loadDeviceWaiting: String

        val ipAndPortPlaceholder: String

        val githubUrlCopied: String

        val activityInfo: String

        val appManage: String

        val fileManage: String

        val layoutAnalyse: String

        val scheduledTask: String

        val simpleTerminal: String

        val currPackageName: String

        val currProcessName: String

        val launchActivityName: String

        val currActivityName: String

        val lastActivityName: String

        val stackActivities: String

        val nothing: String

        val yes: String

        val no: String

        val startScrcpyWaiting: String

        val scrcpyRunning: String

        val screenshotWaiting: String

        val screenshotSuccess: String

        val screenshotRefresh: String

        val screenshotCleared: String

        val screenshotSaved: String

        val refresh: String

        val save: String

        val noFoundDownloadPath: String

        val allApp: String

        val systemApp: String

        val userApp: String

        val searchApp: String

        val searchPlaceholder: String

        val loadAppsWaiting: String

        val tipsTitle: String

        val cancel: String

        val confirm: String

        val reloadAppList: String

        val installTitle: String

        val dropApk: String

        val dropApkEnter: String

        val dropApkFair: String

        val installing: String

        val exportApk: String

        val exportApkMessage: String

        val launchApp: String

        val launchAppMessage: String

        val killApp: String

        val killAppMessage: String

        val clearApp: String

        val clearAppMessage: String

        val uninstallApp: String

        val uninstallMessage: String

        val operSuccess: String

        val operFailure: String

        val appPackageName: String

        val appVersionName: String

        val appVersionCode: String

        val appLaunchActivity: String

        val appPrimaryAbi: String

        val appTargetSdk: String

        val appMinSdk: String

        val appSignVersion: String

        val appIsSystem: String

        val appSize: String

        val appInstallDate: String

        val appUpdateDate: String

        val appDataDir: String

        val appExtDataDir: String

        val appInstallPath: String

        val pathPlaceholder: String

        val multiFileMessage: String

        val filePushMessage: String

        val detail: String

        val pathCopied: String

        val delete: String

        val permissions: String

        val export: String

        val future: String

        val fileName: String

        val fileDir: String

        val fileKind: String

        val fileSize: String

        val filePermission: String

        val fileOwner: String

        val fileGroup: String

        val fileUpdate: String

        val fileDeleteMessage: String

        val bookmark: String

        val bookmarkEmpty: String

        val bookmarkEditTitle: String

        val bookmarkEditName: String

        val bookmarkEditPath: String

        val bookmarkEditItem: String

        val bookmarkDeleteItem: String

        val bookmarkDeletedSuccess: String

        val bookmarkDeletedFailure: String

        val bookmarkNotFound: String

        val bookmarkSavedSuccess: String

        val bookmarkSavedFailure: String

        val bookmarkAddedSuccess: String

        val bookmarkAddedFailure: String

        val pullRequest: String
    }

    data object CN : Language {
        override val title
            get() = "AdbHelper"
        override val title1: String
            get() = "ADB Helper"
        override val simpleTitle: String
            get() = "AH"
        override val initDescription: String
            get() = "首次使用时需要设置adb可执行文件路径"
        override val initPlaceholder: String
            get() = "请输入adb可执行文件路径"
        override val initNext: String
            get() = "下一步"
        override val initSuccess: String
            get() = "设置成功，请稍后手动重启应用."
        override val noDeviceConnection: String
            get() = "没有设备连接"
        override val notConnected: String
            get() = "未连接"
        override val connect: String
            get() = "连接"
        override val noDeviceSelected: String
            get() = "未选择设备"
        override val deviceList: String
            get() = "设备列表"
        override val loadDeviceWaiting: String
            get() = "正在加载设备列表.."
        override val ipAndPortPlaceholder: String
            get() = "请输入ip[:port]"
        override val githubUrlCopied: String
            get() = "Github复制成功"
        override val activityInfo: String
            get() = "活动信息"
        override val appManage: String
            get() = "应用管理"
        override val fileManage: String
            get() = "文件列表"
        override val layoutAnalyse: String
            get() = "布局分析"
        override val scheduledTask: String
            get() = "定时任务"
        override val simpleTerminal: String
            get() = "终端"
        override val currPackageName: String
            get() = "当前包名:"
        override val currProcessName: String
            get() = "当前进程:"
        override val launchActivityName: String
            get() = "启动活动:"
        override val currActivityName: String
            get() = "当前活动:"
        override val lastActivityName: String
            get() = "历史活动:"
        override val stackActivities: String
            get() = "活动堆栈:"
        override val nothing: String
            get() = "无"
        override val yes: String
            get() = "是"
        override val no: String
            get() = "否"
        override val startScrcpyWaiting: String
            get() = "正在启动Scrcpy.."
        override val scrcpyRunning: String
            get() = "Scrcpy已开启"
        override val screenshotWaiting: String
            get() = "屏幕截取中，请稍候.."
        override val screenshotSuccess: String
            get() = "屏幕截取成功"
        override val screenshotRefresh: String
            get() = "屏幕刷新中, 请稍后.."
        override val screenshotCleared: String
            get() = "截图数据丢失，请刷新重试"
        override val screenshotSaved: String
            get() = "截图保存成功"
        override val refresh: String
            get() = "刷新"
        override val save: String
            get() = "保存"
        override val noFoundDownloadPath: String
            get() = "Download目录获取失败或不存在"
        override val allApp: String
            get() = "全部(%s)"
        override val systemApp: String
            get() = "系统(%s)"
        override val userApp: String
            get() = "用户(%s)"
        override val searchApp: String
            get() = "搜索(%s)"
        override val searchPlaceholder: String
            get() = "请输入关键字.."
        override val loadAppsWaiting: String
            get() = "应用列表正在加载中.."
        override val tipsTitle: String
            get() = "提示"
        override val cancel: String
            get() = "取消"
        override val confirm: String
            get() = "确定"
        override val installTitle: String
            get() = "安装应用"
        override val reloadAppList: String
            get() = "是否重新加载应用列表?"
        override val dropApk: String
            get() = "请将Apk文件拖拽至此区域"
        override val dropApkEnter: String
            get() = "松开安装即可安装"
        override val dropApkFair: String
            get() = "不支持该文件类型"
        override val installing: String
            get() = "正在安装: %s"
        override val exportApk: String
            get() = "提取Apk"
        override val exportApkMessage: String
            get() = "将保存至Download目录下, 若文件较大则可能耗时较久。"
        override val launchApp: String
            get() = "启动应用"
        override val launchAppMessage: String
            get() = "若应用无Launch活动, 则无法启动"
        override val killApp: String
            get() = "杀死进程"
        override val killAppMessage: String
            get() = "是否杀死该应用下的所有进程?"
        override val clearApp: String
            get() = "清除数据"
        override val clearAppMessage: String
            get() = "是否清除该应用数据?"
        override val uninstallApp: String
            get() = "卸载应用"
        override val uninstallMessage: String
            get() = "是否卸载该应用？"
        override val operSuccess: String
            get() = "操作成功"
        override val operFailure: String
            get() = "操作失败"
        override val appPackageName: String
            get() = "包    名:"
        override val appVersionName: String
            get() = "版 本 名:"
        override val appVersionCode: String
            get() = "版 本 号:"
        override val appLaunchActivity: String
            get() = "启动活动:"
        override val appPrimaryAbi: String
            get() = "主要ABI:"
        override val appTargetSdk: String
            get() = "目标SDK:"
        override val appMinSdk: String
            get() = "最小SDK:"
        override val appSignVersion: String
            get() = "签名版本:"
        override val appIsSystem: String
            get() = "系统应用:"
        override val appSize: String
            get() = "文件大小:"
        override val appInstallDate: String
            get() = "安装日期:"
        override val appUpdateDate: String
            get() = "更新日期:"
        override val appDataDir: String
            get() = "数据目录:"
        override val appExtDataDir: String
            get() = "外部目录:"
        override val appInstallPath: String
            get() = "安装路径:"
        override val pathPlaceholder: String
            get() = "请输入路径"
        override val multiFileMessage: String
            get() = "暂不支持多文件推送"
        override val filePushMessage: String
            get() = "松开将文件推送至此"
        override val detail: String
            get() = "详细"
        override val pathCopied: String
            get() = "路径复制成功"
        override val delete: String
            get() = "删除"
        override val permissions: String
            get() = "权限"
        override val export: String
            get() = "提取"
        override val future: String
            get() = "待实现"
        override val fileName: String
            get() = "名     称"
        override val fileDir: String
            get() = "目     录"
        override val fileKind: String
            get() = "类     型"
        override val fileSize: String
            get() = "大     小"
        override val filePermission: String
            get() = "权     限"
        override val fileOwner: String
            get() = "所 有 者"
        override val fileGroup: String
            get() = "用 户 组"
        override val fileUpdate: String
            get() = "修改时间"
        override val fileDeleteMessage: String
            get() = "删除后不可恢复，你确定要删除么？"
        override val bookmark: String
            get() = "书签"
        override val bookmarkEmpty: String
            get() = "暂无书签"
        override val bookmarkEditTitle: String
            get() = "编辑书签"
        override val bookmarkEditName: String
            get() = "名称"
        override val bookmarkEditPath: String
            get() = "路径"
        override val bookmarkEditItem: String
            get() = "编辑"
        override val bookmarkDeleteItem: String
            get() = "删除"
        override val bookmarkDeletedSuccess: String
            get() = "书签删除成功"
        override val bookmarkDeletedFailure: String
            get() = "书签删除失败: %s"
        override val bookmarkNotFound: String
            get() = "书签不存在"
        override val bookmarkSavedSuccess: String
            get() = "书签保存成功"
        override val bookmarkSavedFailure: String
            get() = "书签保存失败: %s"
        override val bookmarkAddedSuccess: String
            get() = "书签添加成功"
        override val bookmarkAddedFailure: String
            get() = "书签添加失败: %s"
        override val pullRequest: String
            get() = "待实现，欢迎Pr"
    }

    data object EN : Language {
        override val title
            get() = "AdbHelper"
        override val title1: String
            get() = "ADB Helper"
        override val simpleTitle: String
            get() = "AH"
        override val initDescription: String
            get() = "The first time you use it, you need to set the path to the adb executable."
        override val initPlaceholder: String
            get() = "Please enter the adb executable file path"
        override val initNext: String
            get() = "Next"
        override val initSuccess: String
            get() = "Ok, please restart the app manually later."
        override val noDeviceConnection: String
            get() = "No device connected"
        override val notConnected: String
            get() = "Not connected"
        override val connect: String
            get() = "Connect"
        override val noDeviceSelected: String
            get() = "No device selected"
        override val deviceList: String
            get() = "Device List"
        override val loadDeviceWaiting: String
            get() = "Loading device list.."
        override val ipAndPortPlaceholder: String
            get() = "Please enter ip[:port]"
        override val githubUrlCopied: String
            get() = "Github url copied successfully"
        override val activityInfo: String
            get() = "Activity"
        override val appManage: String
            get() = "AppManage"
        override val fileManage: String
            get() = "FileManage"
        override val layoutAnalyse: String
            get() = "Layout"
        override val scheduledTask: String
            get() = "Schedule"
        override val simpleTerminal: String
            get() = "Terminal"
        override val currPackageName: String
            get() = "CurrPackage:"
        override val currProcessName: String
            get() = "CurrProcess:"
        override val launchActivityName: String
            get() = "LaunchActivity:"
        override val currActivityName: String
            get() = "CurrActivity:"
        override val lastActivityName: String
            get() = "LastActivity:"
        override val stackActivities: String
            get() = "StackActivities:"
        override val nothing: String
            get() = "Nothing"
        override val yes: String
            get() = "Yes"
        override val no: String
            get() = "No"
        override val startScrcpyWaiting: String
            get() = "Starting Scrcpy.."
        override val scrcpyRunning: String
            get() = "Scrcpy is started."
        override val screenshotWaiting: String
            get() = "Screenshot, please wait.."
        override val screenshotSuccess: String
            get() = "Screenshot success."
        override val screenshotRefresh: String
            get() = "Refreshing, please wait.."
        override val screenshotCleared: String
            get() = "If the screenshot data is lost, please refresh and try again"
        override val screenshotSaved: String
            get() = "Screenshot saved successfully"
        override val refresh: String
            get() = "Refresh"
        override val save: String
            get() = "Save"
        override val noFoundDownloadPath: String
            get() = "The Download directory fails to be fetched or does not exist"
        override val allApp: String
            get() = "All(%s)"
        override val systemApp: String
            get() = "System(%s)"
        override val userApp: String
            get() = "User(%s)"
        override val searchApp: String
            get() = "Search(%s)"
        override val searchPlaceholder: String
            get() = "Keywords.."
        override val loadAppsWaiting: String
            get() = "App list is loading.."
        override val tipsTitle: String
            get() = "Tips"
        override val cancel: String
            get() = "Cancel"
        override val confirm: String
            get() = "Confirm"
        override val reloadAppList: String
            get() = "Do you want to reload the app list?"
        override val installTitle: String
            get() = "Install Apk"
        override val dropApk: String
            get() = "Please drag the Apk file to this area"
        override val dropApkEnter: String
            get() = "Let go of the mouse and continue install"
        override val dropApkFair: String
            get() = "This file type is not supported"
        override val installing: String
            get() = "installing: %s"
        override val exportApk: String
            get() = "ExportApk"
        override val exportApkMessage: String
            get() = "It will be saved to the Download directory, if the file is large, it may take a long time."
        override val launchApp: String
            get() = "LaunchApp"
        override val launchAppMessage: String
            get() = "If the app does not have a Launch activity, it cannot be launched"
        override val killApp: String
            get() = "KillProcess"
        override val killAppMessage: String
            get() = "Do you want to kill all processes under this app?"
        override val clearApp: String
            get() = "ClearData"
        override val clearAppMessage: String
            get() = "Do you want to clear the app data?"
        override val uninstallApp: String
            get() = "Uninstall"
        override val uninstallMessage: String
            get() = "Do you want to uninstall the app?"
        override val operSuccess: String
            get() = "Success"
        override val operFailure: String
            get() = "Failure"
        override val appPackageName: String
            get() = "PackageName:"
        override val appVersionName: String
            get() = "VersionName:"
        override val appVersionCode: String
            get() = "VersionCode:"
        override val appLaunchActivity: String
            get() = "LaunchActivity:"
        override val appPrimaryAbi: String
            get() = "PrimaryAbi:"
        override val appTargetSdk: String
            get() = "TargetSdk:"
        override val appMinSdk: String
            get() = "MinSdk:"
        override val appSignVersion: String
            get() = "SignVersion:"
        override val appIsSystem: String
            get() = "System:"
        override val appSize: String
            get() = "Size:"
        override val appInstallDate: String
            get() = "InstallDate:"
        override val appUpdateDate: String
            get() = "UpdateDate:"
        override val appDataDir: String
            get() = "DataDir:"
        override val appExtDataDir: String
            get() = "ExtDataDir:"
        override val appInstallPath: String
            get() = "InstallPath:"
        override val pathPlaceholder: String
            get() = "Please enter a path"
        override val multiFileMessage: String
            get() = "Multi-file push is not supported yet"
        override val filePushMessage: String
            get() = "Let go of the mouse and continue pushing"
        override val detail: String
            get() = "Detail"
        override val pathCopied: String
            get() = "Path copied successfully"
        override val delete: String
            get() = "Delete"
        override val permissions: String
            get() = "Permissions"
        override val export: String
            get() = "Export"
        override val future: String
            get() = "In future."
        override val fileName: String
            get() = "Name"
        override val fileDir: String
            get() = "Dir"
        override val fileKind: String
            get() = "Kind"
        override val fileSize: String
            get() = "Size"
        override val filePermission: String
            get() = "Permission"
        override val fileOwner: String
            get() = "Owner"
        override val fileGroup: String
            get() = "Group"
        override val fileUpdate: String
            get() = "Update"
        override val fileDeleteMessage: String
            get() = "It cannot be restored after deletion, do you want to delete it?"
        override val bookmark: String
            get() = "Bookmark"
        override val bookmarkEmpty: String
            get() = "No bookmarks"
        override val bookmarkEditTitle: String
            get() = "Edit Bookmark"
        override val bookmarkEditName: String
            get() = "Name"
        override val bookmarkEditPath: String
            get() = "Path"
        override val bookmarkEditItem: String
            get() = "Edit"
        override val bookmarkDeleteItem: String
            get() = "Delete"
        override val bookmarkDeletedSuccess: String
            get() = "Bookmark deleted successfully"
        override val bookmarkDeletedFailure: String
            get() = "Bookmark deletion failed: %s"
        override val bookmarkNotFound: String
            get() = "Bookmark not found"
        override val bookmarkSavedSuccess: String
            get() = "Bookmark saved successfully"
        override val bookmarkSavedFailure: String
            get() = "Bookmark save failed: %s"
        override val bookmarkAddedSuccess: String
            get() = "Bookmark added successfully"
        override val bookmarkAddedFailure: String
            get() = "Bookmark add failed: %s"
        override val pullRequest: String
            get() = "In future, welcome pull request."
    }
}