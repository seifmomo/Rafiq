package com.example.rafiq.domain.hardware

import kotlinx.coroutines.flow.StateFlow

interface BleManager {
    val isConnected: StateFlow<Boolean>
    val distanceCm: StateFlow<Int> // Distance measured by VL53L0X
    val isScanning: StateFlow<Boolean>

    fun startScanning()
    fun stopScanning()
    fun connectToDevice(macAddress: String)
    fun disconnect()
}
