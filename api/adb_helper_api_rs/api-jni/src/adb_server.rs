use jni::{
    objects::{JObject, JString},
    sys::jboolean,
    JNIEnv,
};

use crate::helper::get_string;

/// 设置adb工作目录
#[no_mangle]
pub unsafe extern "C" fn Java_adb_AdbServer_setWorkDir<'local>(
    mut env: JNIEnv<'local>,
    _thiz: JObject<'local>,
    path: JString<'local>,
) -> JString<'local> {
    let path = get_string(&mut env, &path);
    let adb_path = adb::set_work_path(&path);
    env.new_string(&adb_path).unwrap()
}

/// 启动adb服务
#[no_mangle]
pub unsafe extern "C" fn Java_adb_AdbServer_startServer<'local>(
    env: JNIEnv<'local>,
    _thiz: JObject<'local>,
) -> JString<'local> {
    let result = adb::start_server();
    env.new_string(&result).unwrap()
}

/// 停止adb服务
#[no_mangle]
pub unsafe extern "C" fn Java_adb_AdbServer_killServer<'local>(
    env: JNIEnv<'local>,
    _thiz: JObject<'local>,
) -> JString<'local> {
    let result = adb::kill_server();
    env.new_string(&result).unwrap()
}

/// adb服务是否已被杀死
#[no_mangle]
pub unsafe extern "C" fn Java_adb_AdbServer_isKilled<'local>(
    _env: JNIEnv<'local>,
    _thiz: JObject<'local>,
) -> jboolean {
    let is_killed = adb::is_killed();
    if is_killed {
        1
    } else {
        0
    }
}
