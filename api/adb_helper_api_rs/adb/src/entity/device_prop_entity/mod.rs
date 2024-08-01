use regex::{Match, Regex};
use serde::{Deserialize, Serialize};

//设备属性实体
#[derive(Serialize, Deserialize, Debug)]
pub struct DeviceProp {
    //属性名
    pub name: String,
    //属性值
    pub value: String,
}

impl DeviceProp {
    /// 解析prop
    pub fn parse(content: String) -> Vec<Self> {
        let mut props: Vec<DeviceProp> = Vec::new();

        let splits = content.split("\n");
        let regex_str = r"\[(.*?)]:\s+\[(.*?)]";
        let regex = Regex::new(&regex_str).unwrap();

        for it in splits {
            let caps = regex.captures(&it);
            match caps {
                Some(cap) => {
                    let fn_to_string = |m: Match<'_>| m.as_str().to_string();

                    let name = cap.get(1).map_or(String::new(), fn_to_string);
                    let value = cap.get(2).map_or(String::new(), fn_to_string);
                    props.push(Self { name, value });
                }
                None => {
                    props.push(Self {
                        name: String::new(),
                        value: String::new(),
                    });
                }
            };
        }

        return props;
    }
}
