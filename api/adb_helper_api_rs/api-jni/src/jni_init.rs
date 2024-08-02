use std::ffi::c_void;

use jni::{
    sys::{jint, JNI_VERSION_1_8},
    JavaVM,
};

/// JNI_OnLoad
#[no_mangle]
pub extern "C" fn JNI_OnLoad(vm: JavaVM, _: *mut c_void) -> jint {
    let _env = vm.get_env().expect("Cannot get reference to the JNIEnv");
    if cfg!(debug_assertions) {
        println!("JNI_OnLoad");
    }
    JNI_VERSION_1_8
}

/// JNI_OnUnload
#[no_mangle]
pub extern "C" fn JNI_OnUnload(_: JavaVM, _: *mut c_void) {
    if cfg!(debug_assertions) {
        println!("JNI_OnUnload");
    }
}
