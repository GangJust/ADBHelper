use jni::{
    objects::{JObject, JString, JValue},
    JNIEnv,
};

use crate::helper::{get_result, get_string, JArrayList};

/// 获取活动信息
#[no_mangle]
pub unsafe extern "C" fn Java_adb_AdbServer_getActivity<'local>(
    mut env: JNIEnv<'local>,
    _thiz: JObject<'local>,
    serial_no: JString<'local>,
) -> JObject<'local> {
    let serial_no = get_string(&mut env, &serial_no);

    let activity = adb::get_activity(&serial_no);

    if let Some(it) = activity {
        let package_name = env.new_string(&it.package_name).unwrap();
        let process_name = env.new_string(&it.process_name).unwrap();
        let launch_activity = env.new_string(&it.launch_activity).unwrap();
        let resumed_activity = env.new_string(&it.resumed_activity).unwrap();
        let last_activity = env.new_string(&it.last_activity).unwrap();

        let stack_activities = JArrayList::new(&mut env);
        for ele in &it.stack_activities {
            let stack_activity = env.new_string(ele).unwrap();
            JArrayList::add(&mut env, &stack_activities, &stack_activity);
        }

        let args = [
            JValue::from(&package_name),
            JValue::from(&process_name),
            JValue::from(&launch_activity),
            JValue::from(&resumed_activity),
            JValue::from(&last_activity),
            JValue::from(&stack_activities),
        ];

        let result = env.new_object(
            "adb/entity/Activity",
            "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;)V",
            &args,
        );

        return get_result(result);
    }

    let empty = env.get_static_field("adb/entity/Activity", "EMPTY", "Ladb/entity/Activity;");
    return match empty {
        Ok(it) => it.l().unwrap_or(JObject::null()),
        Err(_) => JObject::null(),
    };
}
