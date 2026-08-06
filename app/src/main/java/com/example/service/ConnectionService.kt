package com.example.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

enum class ConnectionStatus {
    Disconnected,
    Connecting,
    Connected,
    Error
}

data class ProtocolLog(
    val id: String = UUID.randomUUID().toString().take(6),
    val timestamp: Long = System.currentTimeMillis(),
    val direction: String, // "SENT" or "RCVD"
    val message: String
)

class ConnectionService(private val scope: CoroutineScope) {

    private val _status = MutableStateFlow(ConnectionStatus.Disconnected)
    val status = _status.asStateFlow()

    private val _latency = MutableStateFlow(0)
    val latency = _latency.asStateFlow()

    private val _cpuUsage = MutableStateFlow(12)
    val cpuUsage = _cpuUsage.asStateFlow()

    private val _ramUsage = MutableStateFlow(34)
    val ramUsage = _ramUsage.asStateFlow()

    private val _batteryLevel = MutableStateFlow(85)
    val batteryLevel = _batteryLevel.asStateFlow()

    private val _logs = MutableStateFlow<List<ProtocolLog>>(emptyList())
    val logs = _logs.asStateFlow()

    private var heartbeatJob: Job? = null
    private var telemetryJob: Job? = null

    init {
        // Run light telemetry update simulation
        startTelemetrySimulation()
    }

    fun connect(ip: String, port: Int) {
        scope.launch(Dispatchers.Default) {
            _status.value = ConnectionStatus.Connecting
            addLog("SENT", """{"version":1,"type":"discovery_request"}""")
            delay(1000)

            _status.value = ConnectionStatus.Connected
            _latency.value = 8 // Default starting latency
            addLog("RCVD", """{"version":1,"type":"discovery_response","payload":{"deviceName":"Studio-PC","version":"1.0.0","supportedConnections":["wifi","usb"]}}""")
            addLog("RCVD", """{"type":"event","event":"connected"}""")

            startHeartbeat()
        }
    }

    fun disconnect() {
        scope.launch(Dispatchers.Default) {
            stopHeartbeat()
            addLog("SENT", """{"version":1,"type":"command","action":"disconnect"}""")
            _status.value = ConnectionStatus.Disconnected
            _latency.value = 0
            addLog("RCVD", """{"type":"event","event":"disconnected"}""")
        }
    }

    fun sendCommand(action: String, payload: Map<String, Any>? = null) {
        if (_status.value != ConnectionStatus.Connected) {
            addLog("WARN", "Attempted to send command '$action' while disconnected")
            return
        }

        scope.launch(Dispatchers.Default) {
            val payloadStr = payload?.entries?.joinToString(",") { """"${it.key}":${it.value}""" } ?: ""
            val fullPayload = if (payloadStr.isNotEmpty()) ""","payload":{$payloadStr}""" else ""
            val msgId = UUID.randomUUID().toString().take(6)
            val jsonMsg = """{"version":1,"id":"$msgId","timestamp":${System.currentTimeMillis() / 1000},"type":"command","action":"$action"$fullPayload}"""
            
            addLog("SENT", jsonMsg)
            
            // Simulate brief response from server
            delay(50)
            addLog("RCVD", """{"version":1,"id":"$msgId","type":"response","status":"success"}""")
        }
    }

    private fun startHeartbeat() {
        stopHeartbeat()
        heartbeatJob = scope.launch(Dispatchers.Default) {
            while (true) {
                delay(5000)
                addLog("SENT", """{"type":"heartbeat"}""")
                delay(100)
                addLog("RCVD", """{"type":"heartbeat_ack"}""")
                
                // Randomly vary latency slightly
                _latency.value = (6..14).random()
            }
        }
    }

    private fun stopHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = null
    }

    private fun startTelemetrySimulation() {
        telemetryJob = scope.launch(Dispatchers.Default) {
            while (true) {
                delay(10000)
                if (_status.value == ConnectionStatus.Connected) {
                    _cpuUsage.value = (15..45).random()
                    _ramUsage.value = (40..75).random()
                    _batteryLevel.value = (_batteryLevel.value - 1).coerceAtLeast(1)
                } else {
                    _cpuUsage.value = (5..15).random()
                    _ramUsage.value = (25..35).random()
                }
            }
        }
    }

    private fun addLog(direction: String, message: String) {
        val newLog = ProtocolLog(direction = direction, message = message)
        val currentList = _logs.value.toMutableList()
        currentList.add(0, newLog)
        if (currentList.size > 100) {
            currentList.removeAt(currentList.lastIndex)
        }
        _logs.value = currentList
    }
}
