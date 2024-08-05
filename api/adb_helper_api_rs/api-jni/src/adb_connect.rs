use jni::{
    objects::{JObject, JString},
    JNIEnv,
};

use crate::helper::get_string;

/// adb ip连接
#[no_mangle]
pub unsafe extern "C" fn Java_adb_AdbServer_connect<'local>(
    mut env: JNIEnv<'local>,
    _thiz: JObject<'local>,
    ip: JString<'local>,
) -> JString<'local> {
    let ip = get_string(&mut env, &ip);

    let result = adb::connect(&ip);

    match result {
        Some(it) => env.new_string(it).unwrap(),
        None => env.new_string("").unwrap(),
    }
}
