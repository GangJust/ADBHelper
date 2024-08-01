fn main() {
    // let result = adb::set_work_path(&"C:\\Users\\HMS\\Desktop");
    // println!("path: {}", &result);

    // let result = adb::version();
    // dbg!("{:?}", &result);

    let devices = adb::get_devices().expect("Not found any device");
    // dbg!("{:?}", &devices);

    let first_device = devices.first().expect("No device attached");
    // let activity = adb::get_activity(&first_device.serial_no);
    // dbg!("{:?}", &activity);

    // let packages = adb::get_packages(&first_device.serial_no, false);
    // dbg!("{:?}", &packages);

    // let desc = adb::get_app_desc(&first_device.serial_no, "com.coolapk.market");
    // dbg!("{:?}", &desc);

    let files = adb::list_files(&first_device.serial_no, "/");
    // let files = adb::list_files(&first_device.serial_no, "/storage/emulated/0/新建_文件 夹/");
    // let files = adb::list_files(&first_device.serial_no, "/storage/emulated/0/Download/");
    // let files = adb::list_files(&first_device.serial_no, "/metadata");
    // let files = adb::list_files(&first_device.serial_no, "/storage/emulated/0/.Application/");
    // let files = adb::list_files(&first_device.serial_no, "/storage/emulated/0/Android/.system_android_l2");
    println!("{}", serde_json::to_string_pretty(&files).unwrap());

    // let local_path = "C:\\Users\\HMS\\Desktop\\测试 空格\\com.coolapk.market.apk";
    // let remote_path = "/storage/emulated/0/新建_文件 夹/com.coolapk.market.apk";
    // let result = adb::push_file(&first_device.serial_no, local_path, remote_path);
    // dbg!("{:?}", &result);

    // let remote_path = "/storage/emulated/0/新建_文件 夹/com.coolapk.market.apk";
    // let local_path = "C:\\Users\\HMS\\Desktop\\测试 空格\\com.coolapk.market.apk";
    // let result = adb::pull_file(&first_device.serial_no, remote_path, local_path);
    // dbg!("{:?}", &result);

    // let apk_path = "C:/Users/HMS/Desktop/测试 空格/com.coolapk.market.apk";
    // let result = adb::install_apk(&first_device.serial_no, apk_path);
    // dbg!("{:?}", &result);

    // let result = adb::shell_syn(
    //     &first_device.serial_no,
    //     // r"ls -lai '/storage/emulated/0/新建_文件\ 夹/'",
    //     r"chmod 777 /data/local/tmp/test",
    //     true,
    // );
    // println!("{}", &result);

    // let result = adb::uninstall_apk(&first_device.serial_no, "com.coolapk.market");
    // dbg!("{:?}", &result);

    // let data = adb::screenshot(&first_device.serial_no);
    // println!("{:?}", data);

    // let layout = adb::get_layout(&first_device.serial_no);
    // println!("{}", layout);

    // adb::logcat(&first_device.serial_no, 5656); //and then run `cargo run -p logcat-client`
}
