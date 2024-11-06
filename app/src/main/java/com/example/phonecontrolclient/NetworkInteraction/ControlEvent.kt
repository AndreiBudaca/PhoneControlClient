package com.example.phonecontrolclient.NetworkInteraction

class ControlEvent(
    private val eventType: ControlEventType,
    private val data1: String,
    private val data2: String?,
) {
    override fun toString(): String {
        val data2String = if (data2 == null) "null" else "\"$data2\""
        return "{\"eventType\":${eventType.value}, \"data1\":\"$data1\", \"data2\":$data2String}"
    }
}


enum class ControlEventType(val value: Int) {
    Keyboard(1),
    Mouse(2),
    MouseWheel(3),
    MouseButton(4),
}