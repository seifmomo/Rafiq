package com.example.rafiq.data.hardware

import com.example.rafiq.domain.hardware.BleManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class MockBleManagerImpl @Inject constructor() : BleManager {

    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _distanceCm = MutableStateFlow(300)
    override val distanceCm: StateFlow<Int> = _distanceCm.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    override val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var simulationJob: Job? = null
    private var scanJob: Job? = null
    private var obstacleActive = false
    private var obstacleDistance = 300

    override fun startScanning() {
        // Guard against duplicate scans
        if (_isScanning.value) return

        _isScanning.value = true
        scanJob?.cancel()
        scanJob = scope.launch {
            delay(2000)
            _isScanning.value = false
        }
    }

    override fun stopScanning() {
        scanJob?.cancel()
        scanJob = null
        _isScanning.value = false
    }

    override fun connectToDevice(macAddress: String) {
        scope.launch {
            delay(1000)
            _isConnected.value = true
            obstacleDistance = Random.nextInt(200, 400)
            startSimulation()
        }
    }

    override fun disconnect() {
        simulationJob?.cancel()
        simulationJob = null
        _isConnected.value = false
        obstacleActive = false
        _distanceCm.value = 300
    }

    private fun startSimulation() {
        simulationJob?.cancel()
        simulationJob = scope.launch {
            while (isActive && _isConnected.value) {
                if (!obstacleActive) {
                    if (Random.nextFloat() < 0.3f) {
                        obstacleActive = true
                        obstacleDistance = Random.nextInt(150, 300)
                    }
                }

                if (obstacleActive) {
                    val noise = Random.nextInt(-3, 4)
                    obstacleDistance = (obstacleDistance - Random.nextInt(15, 45) + noise).coerceAtLeast(10)

                    if (obstacleDistance <= 20) {
                        _distanceCm.value = obstacleDistance
                        delay(2000)
                        obstacleActive = false
                        obstacleDistance = Random.nextInt(250, 450)
                    }
                } else {
                    if (Random.nextFloat() < 0.2f) {
                        obstacleDistance = (obstacleDistance + Random.nextInt(10, 30)).coerceAtMost(500)
                    }
                }

                _distanceCm.value = obstacleDistance.coerceIn(10, 500)
                delay(1000)
            }
        }
    }
}
