use regex::{Captures, Match, Regex};

pub struct RegexUtils;

impl RegexUtils {
    /// 正则匹配
    /// content: 内容
    /// regex: 正则表达式
    /// 返回: 匹配结果集
    pub fn captures_iter<'a, T>(content: &'a T, regex: &str) -> Vec<Captures<'a>>
    where
        T: AsRef<str>,
    {
        let regex = Regex::new(&regex).unwrap();
        return regex
            .captures_iter(content.as_ref())
            .collect::<Vec<Captures>>();
    }

    /// 正则匹配
    /// content: 内容
    /// regex: 正则表达式
    /// 返回: 匹配结果
    pub fn captures<'a, T>(content: &'a T, regex: &'a str) -> Option<regex::Captures<'a>>
    where
        T: AsRef<str>,
    {
        let regex = Regex::new(&regex).unwrap();
        return regex.captures(content.as_ref());
    }

    /// 获取正则匹配的值
    /// content: 内容
    /// regex: 正则表达式
    /// index: 匹配的索引
    /// 返回: 匹配的值
    pub fn regex_capture_value<T: AsRef<str>>(content: &T, regex: &str, index: usize) -> String {
        let cps = RegexUtils::captures(content, regex);
        match cps {
            Some(it) => {
                let fn_to_string = |m: Match<'_>| m.as_str().trim().to_string();
                it.get(index).map_or(String::new(), fn_to_string)
            }
            None => String::new(),
        }
    }

    /// 获取匹配的值
    /// captures: 匹配结果
    /// index: 匹配的索引
    /// 返回: 匹配的值
    pub fn captures_value(captures: &regex::Captures, index: usize) -> String {
        let fn_to_string = |m: Match<'_>| m.as_str().to_string();
        captures.get(index).map_or(String::new(), fn_to_string)
    }
}
