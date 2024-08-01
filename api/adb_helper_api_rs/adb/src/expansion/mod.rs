use crate::entity::Device;

/// adb 命令格式化
pub trait AdbFormat {
    fn by_serial<T: AsRef<str>>(&self, serial_no: T) -> String;

    fn by_device(&self, device: &Device) -> String;
}

impl AdbFormat for str {
    /// 按序列格式化为 adb 命令前缀
    fn by_serial<T: AsRef<str>>(&self, serial_no: T) -> String {
        let cmd = format!("adb -s {}", serial_no.as_ref());
        self.replace("adb", cmd.as_str())
    }

    /// 按设备格式化为 adb 命令前缀
    fn by_device(&self, device: &Device) -> String {
        return self.by_serial(&device.serial_no);
    }
}
