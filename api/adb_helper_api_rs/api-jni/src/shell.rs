use jni::{
    objects::{JObject, JString},
    sys::jboolean,
    JNIEnv,
};

use crate::helper::get_string;

/// 同步执行shell命令
#[no_mangle]
pub unsafe extern "C" fn Java_adb_AdbServer_shellSyn<'local>(
    mut env: JNIEnv<'local>,
    _thiz: JObject<'local>,
    serial_no: JString<'local>,
    cmd: JString<'local>,
    su: jboolean,
) -> JString<'local> {
    let serial_no = get_string(&mut env, &serial_no);
    let cmd = get_string(&mut env, &cmd);

    let result = adb::shell_syn(&serial_no, &cmd, su == 1);

    match result {
        Some(it) => env.new_string(it).unwrap(),
        None => env.new_string("").unwrap(),
    }
}
