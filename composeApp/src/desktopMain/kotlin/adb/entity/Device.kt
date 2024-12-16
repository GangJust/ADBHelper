package adb.entity

import kotlinx.serialization.Serializable

@Serializable
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

    // 设备品牌
    // Device brand
    val brand: String
        get() = props.getOrDefault("ro.product.brand", "")

    // 设备序列号，尽量获取到真实的序列号
    // Device serial number, try to get the real serial number
    val displaySerialNo: String
        get() = props["ro.boot.serialno"] ?: props["ro.serialno"] ?: serialNo

    // 设备品牌和型号
    // Device brand and model
    val brandModel: String
        get() = "$brand $model".trim()

    // 设备品牌、型号和序列号
    // Device brand, model and serial number
    val brandModelSerialNo: String
        get() = "$brand $model ($displaySerialNo)".trim()

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