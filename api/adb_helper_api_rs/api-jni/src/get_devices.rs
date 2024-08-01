use adb::entity::Device;
use jni::{
    objects::{JObject, JValue},
    JNIEnv,
};

use crate::helper::{get_result, JArrayList, JHashMap};

/// 获取附加设备列表
#[no_mangle]
pub unsafe extern "C" fn Java_adb_AdbServer_getDevices<'local>(
    mut env: JNIEnv<'local>,
    _thiz: JObject<'local>,
) -> JObject<'local> {
    let devices = adb::get_devices();

    let device_list = JArrayList::new(&mut env);

    if let Some(it) = devices {
        // 遍历设备列表, 构造ArrayList
        for ele in &it {
            let device = new_device(&mut env, &ele);
            JArrayList::add(&mut env, &device_list, &device);
        }
    }

    return device_list;
}

// 构建设备对象
fn new_device<'local>(env: &mut JNIEnv<'local>, device: &Device) -> JObject<'local> {
    let serial_no = env.new_string(&device.serial_no).unwrap();
    let state = env.new_string(&device.state).unwrap();
    let product = env.new_string(&device.product).unwrap();
    let model = env.new_string(&device.model).unwrap();
    let device_name = env.new_string(&device.device).unwrap();
    let transport_id = env.new_string(&device.transport_id).unwrap();

    // 遍历属性, 构造ArrayList
    let prop_map = JHashMap::new(env);
    for ele in &device.props {
        let key = env.new_string(ele.0).unwrap();
        let value = env.new_string(ele.1).unwrap();
        JHashMap::put(env, &prop_map, &key, &value);
    }

    let args = [
        JValue::from(&serial_no),
        JValue::from(&state),
        JValue::from(&product),
        JValue::from(&model),
        JValue::from(&device_name),
        JValue::from(&transport_id),
        JValue::from(&prop_map),
    ];

    let result = env.new_object(
        "adb/entity/Device",
        "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/Map;)V",
        &args,
    );

    get_result(result)
}
