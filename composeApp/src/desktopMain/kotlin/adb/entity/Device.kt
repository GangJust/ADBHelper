package adb.entity

data class Device(
    // 设备序列
    val serialNo: String,
    // 设备状态
    val state: String,
    // 设备产品
    val product: String,
    // 设备型号
    val model: String,
    // 设备型号
    val device: String,
    // 传输ID
    val transportId: String,
    // 设备参数
    val props: Map<String, String>,
) {
    // Gson 需要保留一个空参构造方法
    // Gson Need to retain an empty parameter construction method
    constructor() : this(
        serialNo = "",
        state = "",
        product = "",
        model = "",
        device = "",
        transportId = "",
        props = emptyMap(),
    )

    override fun toString(): String {
        return "Device(serialNo=$displaySerialNo, props=${props.size})"
    }

    // 设备名称和型号
    // Device name and model
    val brandModel: String
        get() = "${props.getOrDefault("ro.product.brand", "")} $model".trim()

    // 设备名称、型号和序列号
    // Device name, model and serial number
    val brandModelSerialNo: String
        get() = "${props.getOrDefault("ro.product.brand", "")} $model ($displaySerialNo)".trim()

    // 设备序列号，尽量获取到真实的序列号
    // Device serial number, try to get the real serial number
    val displaySerialNo: String
        get() = props["ro.boot.serialno"] ?: props["ro.serialno"] ?: serialNo

    companion object {
        @JvmField
        val EMPTY = Device(
            serialNo = "",
            state = "",
            product = "",
            model = "",
            device = "",
            transportId = "",
            props = emptyMap(),
        )
    }
}