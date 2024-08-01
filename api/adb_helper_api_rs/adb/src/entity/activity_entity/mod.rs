use serde::{Deserialize, Serialize};

use crate::utils::RegexUtils;

#[derive(Debug, Serialize, Deserialize)]
pub struct Activity {
    //包名
    pub package_name: String,
    //进程名
    pub process_name: String,
    //启动活动
    pub launch_activity: String,
    //当前活动
    pub resumed_activity: String,
    //上次活动
    pub last_activity: String,
    //活动堆栈
    pub stack_activities: Vec<String>,
}

impl Activity {
    /// 解析活动信息
    pub fn parse(content: String) -> Self {
        let package_name_regex = r"packageName=(.*?)\s";
        let package_name = RegexUtils::regex_capture_value(&content, package_name_regex, 1);

        let process_name_regex = r"processName=(.*?)\s";
        let process_name = RegexUtils::regex_capture_value(&content, process_name_regex, 1);

        let launch_activity_regex = r"android.intent.category.LAUNCHER.*?cmp=(.*?)[\}\s]";
        let launch_activity = RegexUtils::regex_capture_value(&content, launch_activity_regex, 1);

        let resumed_activity_regex = r"ResumedActivity: ActivityRecord\{.*?\s\w{2}\s(.*?)[\}\s]";
        let resumed_activity = RegexUtils::regex_capture_value(&content, resumed_activity_regex, 1);

        let last_activity_regex = r"LastPausedActivity: ActivityRecord\{.*?\s\w{2}\s(.*?)[\}\s]";
        let last_activity = RegexUtils::regex_capture_value(&content, last_activity_regex, 1);

        let mut stack_activities: Vec<String> = Vec::new();
        let stack_activities_regex = r"\* Hist\s.*:\sActivityRecord\{.*?\s\w{2}\s(.*?)[\}\s]";
        let hists: Vec<regex::Captures<'_>> =
            RegexUtils::captures_iter(&content, stack_activities_regex);
        for ele in hists {
            let his_activity = RegexUtils::captures_value(&ele, 1);
            stack_activities.push(his_activity);
        }

        Self {
            package_name,
            process_name,
            launch_activity,
            resumed_activity,
            last_activity,
            stack_activities,
        }
    }
}
