use jni::objects::*;
use jni::JNIEnv;

pub struct JArrayList;

impl JArrayList {
    /// 创建ArrayList对象
    pub fn new<'local>(env: &mut JNIEnv<'local>) -> JObject<'local> {
        let list_clazz = env.find_class("java/util/ArrayList").unwrap();
        env.new_object(list_clazz, "()V", &[]).unwrap()
    }

    // 添加元素
    pub fn add<'local>(env: &mut JNIEnv<'local>, list: &JObject, obj: &JObject) -> bool {
        let result = env.call_method(list, "add", "(Ljava/lang/Object;)Z", &[JValue::from(obj)]);
        match result {
            Ok(_) => true,
            Err(_) => false,
        }
    }
}
