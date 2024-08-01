use jni::{
    errors::Error,
    objects::{JObject, JString},
    JNIEnv,
};

// 获取结果
pub fn get_result(result: Result<JObject, Error>) -> JObject {
    match result {
        Ok(it) => it,
        Err(_) => JObject::null(),
    }
}

/// 获取字符串
pub fn get_string(env: &mut JNIEnv, jstr: &JString) -> String {
    match env.get_string(jstr) {
        Ok(it) => it.into(),
        Err(_) => String::new(),
    }
}
