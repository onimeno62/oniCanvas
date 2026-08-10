package com.onimeno.onicanvas.feature.connection.data

import com.onimeno.onicanvas.feature.connection.state.ConnectionHost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress

/** UDP broadcast discovery for Windows companion instances on the local network. */
class ConnectionDiscovery(
    private val discoveryPort: Int = DEFAULT_DISCOVERY_PORT,
    private val listenTimeoutMs: Int = DEFAULT_LISTEN_TIMEOUT_MS
) {
    suspend fun scan(): List<ConnectionHost> = withContext(Dispatchers.IO) {
        val request = OniCanvasProtocol.encode(OniCanvasMessage.discoveryRequest()).toByteArray(Charsets.UTF_8)
        val discovered = linkedMapOf<String, ConnectionHost>()

        DatagramSocket().use { socket ->
            socket.broadcast = true
            socket.soTimeout = listenTimeoutMs
            val target = DatagramPacket(
                request,
                request.size,
                InetAddress.getByName("255.255.255.255"),
                discoveryPort
            )
            runCatching { socket.send(target) }

            val buffer = ByteArray(MAX_PACKET_SIZE)
            while (true) {
                val packet = DatagramPacket(buffer, buffer.size)
                try {
                    socket.receive(packet)
                } catch (_: Exception) {
                    break
                }

                val message = OniCanvasProtocol.decode(
                    packet.data.decodeToString(0, packet.length)
                ) ?: continue
                if (message.type != OniCanvasMessage.Type.DISCOVERY_RESPONSE) continue

                val host = message.toHost(packet.address.hostAddress ?: continue)
                discovered[host.id] = host
            }
        }

        discovered.values.toList()
    }

    private fun OniCanvasMessage.toHost(ipAddress: String): ConnectionHost {
        val name = payload["device"]?.toString()?.trim('"')?.takeIf { it.isNotBlank() } ?: "Companion"
        val host = payload["host"]?.toString()?.trim('"')?.takeIf { it.isNotBlank() }
        val port = payload["port"]?.toString()?.toIntOrNull() ?: ConnectionProbe.DEFAULT_PORT
        return ConnectionHost(
            id = "${ipAddress}:$port",
            name = host ?: name,
            ipAddress = ipAddress,
            lastConnected = "Available",
            isAvailable = true,
            port = port
        )
    }

    private companion object {
        const val DEFAULT_DISCOVERY_PORT = 8086
        const val DEFAULT_LISTEN_TIMEOUT_MS = 750
        const val MAX_PACKET_SIZE = 4096
    }
}
