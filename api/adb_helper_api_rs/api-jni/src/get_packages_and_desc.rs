use jni::{
    objects::{JObject, JString, JValue},
    sys::jboolean,
    JNIEnv,
};

use crate::helper::{get_result, get_string, JArrayList};

/// 获取包名列表
#[no_mangle]
pub unsafe extern "C" fn Java_adb_AdbServer_getPackages<'local>(
    mut env: JNIEnv<'local>,
    _thiz: JObject<'local>,
    serial_no: JString<'local>,
    is_system: jboolean,
) -> JObject<'local> {
    let serial_no = get_string(&mut env, &serial_no);

    let packages = adb::get_packages(&serial_no, is_system == 1);

    let array_list = JArrayList::new(&mut env);

    if let Some(it) = packages {
        for ele in &it {
            let package = env.new_string(ele).unwrap();
            JArrayList::add(&mut env, &array_list, &package);
        }
    }

    array_list
}

/// 通过包名获取App信息
#[no_mangle]
pub unsafe extern "C" fn Java_adb_AdbServer_getAppDesc<'local>(
    mut env: JNIEnv<'local>,
    _thiz: JObject<'local>,
    serial_no: JString<'local>,
    package_name: JString<'local>,
) -> JObject<'local> {
    let serial_no = get_string(&mut env, &serial_no);
    let package_name = get_string(&mut env, &package_name);

    let app_desc = adb::get_app_desc(&serial_no, &package_name);

    if let Some(it) = app_desc {
        let package_name = env.new_string(&it.package_name).unwrap();
        let launcher_activity = env.new_string(&it.launcher_activity).unwrap();
        let primary_cpu_abi = env.new_string(&it.primary_cpu_abi).unwrap();
        let version_name = env.new_string(&it.version_name).unwrap();
        let version_code = env.new_string(&it.version_code).unwrap();
        let min_sdk = env.new_string(&it.min_sdk).unwrap();
        let target_sdk = env.new_string(&it.target_sdk).unwrap();
        let time_stamp = env.new_string(&it.time_stamp).unwrap();
        let first_install_time = env.new_string(&it.first_install_time).unwrap();
        let last_update_time = env.new_string(&it.last_update_time).unwrap();
        let sign_version = env.new_string(&it.sign_version).unwrap();
        let data_dir = env.new_string(&it.data_dir).unwrap();
        let external_data_dir = env.new_string(&it.external_data_dir).unwrap();
        let install_path = env.new_string(&it.install_path).unwrap();
        let size = env.new_string(&it.size).unwrap();
        let is_system = it.is_system;

        let args = [
            JValue::from(&package_name),
            JValue::from(&launcher_activity),
            JValue::from(&primary_cpu_abi),
            JValue::from(&version_name),
            JValue::from(&version_code),
            JValue::from(&min_sdk),
            JValue::from(&target_sdk),
            JValue::from(&time_stamp),
            JValue::from(&first_install_time),
            JValue::from(&last_update_time),
            JValue::from(&sign_version),
            JValue::from(&data_dir),
            JValue::from(&external_data_dir),
            JValue::from(&install_path),
            JValue::from(&size),
            JValue::from(is_system),
        ];

        let result = env.new_object(
            "adb/entity/AppDesc",
            "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V",
            &args,
        );

        return get_result(result);
    }

    let empty = env.get_static_field("adb/entity/AppDesc", "EMPTY", "Ladb/entity/AppDesc;");
    return match empty {
        Ok(it) => it.l().unwrap_or(JObject::null()),
        Err(_) => JObject::null(),
    };
}
