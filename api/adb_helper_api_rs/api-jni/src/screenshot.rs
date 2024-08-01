use jni::{
    objects::{JObject, JString, JValue},
    JNIEnv,
};

use crate::helper::{get_result, get_string};

/// 屏幕截图
#[no_mangle]
pub unsafe extern "C" fn Java_adb_AdbServer_screenshot<'local>(
    mut env: JNIEnv<'local>,
    _thiz: JObject<'local>,
    serial_no: JString<'local>,
) -> JObject<'local> {
    let serial_no = get_string(&mut env, &serial_no);

    let result = adb::screenshot(&serial_no);

    if let Some(it) = result {
        let mimetype = env.new_string(&it.mimetype).unwrap();
        let width = it.width;
        let height = it.height;
        let data = env.byte_array_from_slice(&it.data).unwrap();

        let args = [
            JValue::from(&mimetype),
            JValue::Long(width),
            JValue::Long(height),
            JValue::from(&data),
        ];

        let result = env.new_object("adb/entity/Screenshot", "(Ljava/lang/String;JJ[B)V", &args);

        return get_result(result);
    }

    let empty = env.get_static_field("adb/entity/Screenshot", "EMPTY", "Ladb/entity/Screenshot;");
    return match empty {
        Ok(it) => it.l().unwrap_or(JObject::null()),
        Err(_) => JObject::null(),
    };
}
