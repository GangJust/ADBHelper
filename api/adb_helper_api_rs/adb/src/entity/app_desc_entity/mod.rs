use serde::{Deserialize, Serialize};

use crate::utils::RegexUtils;

#[derive(Debug, Serialize, Deserialize)]
pub struct AppDesc {
    //包名
    pub package_name: String,
    //启动活动
    pub launcher_activity: String,
    //主CPU架构
    pub primary_cpu_abi: String,
    //版本名
    pub version_name: String,
    //版本号
    pub version_code: String,
    //最小SDK版本
    pub min_sdk: String,
    //目标SDK版本
    pub target_sdk: String,
    //时间戳
    pub time_stamp: String,
    //安装时间
    pub first_install_time: String,
    //更新时间
    pub last_update_time: String,
    //签名版本
    pub sign_version: String,
    //数据目录
    pub data_dir: String,
    //外部数据目录
    pub external_data_dir: String,
    //安装路径
    pub install_path: String,
    //应用大小
    pub size: String,
    //是否系统应用
    pub is_system: bool,
}

impl AppDesc {
    /// 解析App信息
    pub fn parse(
        content: String,
        package_name: String,
        install_path: String,
        size: String,
    ) -> Self {
        let launcher_activity = AppDesc::get_launcher_activity(&content);

        let version_name_regex = r"versionName=(.*)\s";
        let version_name = RegexUtils::regex_capture_value(&content, version_name_regex, 1);

        let primary_cpu_abi_regex = r"primaryCpuAbi=(.*)\s";
        let primary_cpu_abi = RegexUtils::regex_capture_value(&content, primary_cpu_abi_regex, 1);

        let time_stamp_regex = r"timeStamp=(.*)\s";
        let time_stamp = RegexUtils::regex_capture_value(&content, time_stamp_regex, 1);

        let first_install_time_regex = r"firstInstallTime=(.*)\s";
        let first_install_time =
            RegexUtils::regex_capture_value(&content, first_install_time_regex, 1);

        let last_update_time_regex = r"lastUpdateTime=(.*)\s";
        let last_update_time = RegexUtils::regex_capture_value(&content, last_update_time_regex, 1);

        let sign_version_regex = r"apkSigningVersion=(.*)\s";
        let sign_version = RegexUtils::regex_capture_value(&content, sign_version_regex, 1);

        let data_dir_regex = r"dataDir=(.*)\s";
        let data_dir = RegexUtils::regex_capture_value(&content, data_dir_regex, 1);

        let external_data_dir = format!("/storage/emulated/0/Android/data/{}", &package_name);

        let is_system = install_path.starts_with("/system");

        //保守匹配
        let version_code_regex = r"versionCode=(.*?)\s";
        let version_code = RegexUtils::regex_capture_value(&content, version_code_regex, 1);

        let min_sdk_regex = r"minSdk=(.*?)\s";
        let min_sdk = RegexUtils::regex_capture_value(&content, min_sdk_regex, 1);

        let taget_sdk_regex = r"targetSdk=(.*?)\s";
        let target_sdk = RegexUtils::regex_capture_value(&content, taget_sdk_regex, 1);

        Self {
            package_name,
            launcher_activity,
            primary_cpu_abi,
            version_name,
            version_code,
            target_sdk,
            min_sdk,
            time_stamp,
            first_install_time,
            last_update_time,
            sign_version,
            data_dir,
            external_data_dir,
            install_path,
            size,
            is_system,
        }
    }

    /// 获取启动活动
    fn get_launcher_activity(content: &String) -> String {
        // 正则规则 "前置文本([\s\S]*?)后置文本" 可匹配任意中间内容(包含换行)
        let launcher_activity_regex =
            r"android.intent.action.MAIN:([\s\S]*?)android.intent.category.LAUNCHER";
        let result = RegexUtils::regex_capture_value(&content, launcher_activity_regex, 1);

        let splits = result
            .split("\n")
            .map(|line| line.trim())
            .filter(|line| !line.is_empty())
            .collect::<Vec<&str>>();

        match splits.first() {
            Some(first) => {
                let launcher_activity = first.split_whitespace().collect::<Vec<&str>>();
                match launcher_activity.get(1) {
                    Some(it) => it.to_string(),
                    None => String::new(),
                }
            }
            None => String::new(),
        }
    }
}
