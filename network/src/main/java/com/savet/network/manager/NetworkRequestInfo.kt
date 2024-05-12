package com.savet.network.manager

class NetworkRequestInfo(
    val headerMap: HashMap<String, String> = HashMap()
) {
    companion object {
        const val TAG = "NetworkRequestInfo"
    }

    init {
        headerMap["Content-Type"] = "application/json;charset=UTF-8"
    }

}
