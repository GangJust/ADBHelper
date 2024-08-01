use serde::{Deserialize, Serialize};

#[derive(Debug, Serialize, Deserialize)]
pub struct Version {
    //版本名
    pub version_name: String,
    //版本号
    pub version_code: String,
    //安装路径
    pub install_path: String,
    //运行系统
    pub running_os: String,
}

impl Version {
    /// 解析adb版本
    pub fn parse(content: String) -> Self {
        let lines = content
            .split_whitespace() //按空白字符分割
            .into_iter()
            .collect::<Vec<&str>>();

        // dbg!("{:?}", &lines);

        Self {
            version_name: lines[4].to_string(),
            version_code: lines[6].to_string(),
            install_path: lines[9].to_string(),
            running_os: (&lines[12..].join(" ")).to_string(),
        }
    }

    /// 是否为空 (未找到/安装adb命令行程序)
    pub fn is_empty(&self) -> bool {
        self.version_name.is_empty()
    }
}
