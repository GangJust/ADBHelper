use jni::{
    objects::{JObject, JValue},
    JNIEnv,
};

use crate::helper::get_result;

/// 获取adb版本
#[no_mangle]
pub unsafe extern "C" fn Java_adb_AdbServer_version<'local>(
    mut env: JNIEnv<'local>,
    _thiz: JObject<'local>,
) -> JObject<'local> {
    let adb_version = adb::version();

    if let Some(it) = adb_version {
        let version_name = env.new_string(it.version_name).unwrap();
        let version_code = env.new_string(it.version_code).unwrap();
        let install_path = env.new_string(it.install_path).unwrap();
        let running_os = env.new_string(it.running_os).unwrap();

        let args = [
            JValue::from(&version_name),
            JValue::from(&version_code),
            JValue::from(&install_path),
            JValue::from(&running_os),
        ];

        let result = env.new_object(
            "adb/entity/Version",
            "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
            &args,
        );

        return get_result(result);
    }

    let empty = env.get_static_field("adb/entity/Version", "EMPTY", "Ladb/entity/Version;");

    return match empty {
        Ok(it) => it.l().unwrap_or(JObject::null()),
        Err(_) => JObject::null(),
    };
}
