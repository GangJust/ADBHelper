use jni::objects::*;
use jni::JNIEnv;

pub struct JHashMap;

impl JHashMap {
    /// 创建HashMap对象
    pub fn new<'local>(env: &mut JNIEnv<'local>) -> JObject<'local> {
        let list_clazz = env.find_class("java/util/HashMap").unwrap();
        env.new_object(list_clazz, "()V", &[]).unwrap()
    }

    // 添加元素
    pub fn put<'local>(
        env: &mut JNIEnv<'local>,
        map: &JObject<'local>,
        key: &JObject<'local>,
        value: &JObject<'local>,
    ) -> bool {
        let result = env.call_method(
            map,
            "put",
            "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
            &[JValue::from(key), JValue::from(value)],
        );

        match result {
            Ok(_) => true,
            Err(_) => false,
        }
    }
}
