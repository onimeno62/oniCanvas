package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.ConnectionStatus
import com.example.service.ProtocolLog
import com.example.ui.components.GlassCard
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryPurple
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    ip: String,
    port: Int,
    haptics: Boolean,
    voice: Boolean,
    theme: String,
    status: ConnectionStatus,
    logs: List<ProtocolLog>,
    onSaveSettings: (String, Int, Boolean, Boolean, String) -> Unit,
    onToggleConnection: () -> Unit
) {
    var ipInput by remember { mutableStateOf(ip) }
    var portInput by remember { mutableStateOf(port.toString()) }
    var hapticsInput by remember { mutableStateOf(haptics) }
    var voiceInput by remember { mutableStateOf(voice) }
    var themeInput by remember { mutableStateOf(theme) }

    var showLogsConsole by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080710))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            Column {
                Text(
                    text = "Companion Settings",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Establish communication with Windows Companion",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp
                )
            }
        }

        // Connection Card
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Connection Status",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when (status) {
                                    ConnectionStatus.Connected -> Color(0xFF10B981).copy(alpha = 0.15f)
                                    ConnectionStatus.Connecting -> Color(0xFFF59E0B).copy(alpha = 0.15f)
                                    else -> Color(0xFFEF4444).copy(alpha = 0.15f)
                                }
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = status.name,
                            color = when (status) {
                                ConnectionStatus.Connected -> Color(0xFF10B981)
                                ConnectionStatus.Connecting -> Color(0xFFF59E0B)
                                else -> Color(0xFFEF4444)
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = ipInput,
                    onValueChange = { ipInput = it },
                    label = { Text(text = "Windows Companion IP") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1F1D36),
                        unfocusedContainerColor = Color(0xFF131124),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                TextField(
                    value = portInput,
                    onValueChange = { portInput = it },
                    label = { Text(text = "Communication Port") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1F1D36),
                        unfocusedContainerColor = Color(0xFF131124),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val parsedPort = portInput.toIntOrNull() ?: 8000
                            onSaveSettings(ipInput, parsedPort, hapticsInput, voiceInput, themeInput)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F1D36)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    ) {
                        Text(text = "Save Profile", color = Color.White, fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            val parsedPort = portInput.toIntOrNull() ?: 8000
                            onSaveSettings(ipInput, parsedPort, hapticsInput, voiceInput, themeInput)
                            onToggleConnection()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (status == ConnectionStatus.Connected) Color(0xFFEF4444) else PrimaryIndigo
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (status == ConnectionStatus.Connected) "Disconnect" else "Connect",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Toggles Card
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Feedback & Options",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Haptic Feedback Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.TapAndPlay, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Haptic Feedback", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "Tactile vibration when pressing keys", color = Color.White.copy(alpha = 0.45f), fontSize = 11.sp)
                        }
                    }
                    Switch(
                        checked = hapticsInput,
                        onCheckedChange = {
                            hapticsInput = it
                            val parsedPort = portInput.toIntOrNull() ?: 8000
                            onSaveSettings(ipInput, parsedPort, hapticsInput, voiceInput, themeInput)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = AccentCyan, checkedTrackColor = AccentCyan.copy(alpha = 0.3f))
                    )
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp))

                // Voice Commands Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = SecondaryPurple, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Voice Commands", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "Say actions like 'Undo' or 'Brush'", color = Color.White.copy(alpha = 0.45f), fontSize = 11.sp)
                        }
                    }
                    Switch(
                        checked = voiceInput,
                        onCheckedChange = {
                            voiceInput = it
                            val parsedPort = portInput.toIntOrNull() ?: 8000
                            onSaveSettings(ipInput, parsedPort, hapticsInput, voiceInput, themeInput)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = SecondaryPurple, checkedTrackColor = SecondaryPurple.copy(alpha = 0.3f))
                    )
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp))

                // Theme switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.BrightnessMedium, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Force Dark Mode First", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "Recommended for drawing screens", color = Color.White.copy(alpha = 0.45f), fontSize = 11.sp)
                        }
                    }
                    Switch(
                        checked = themeInput == "dark",
                        onCheckedChange = {
                            themeInput = if (it) "dark" else "light"
                            val parsedPort = portInput.toIntOrNull() ?: 8000
                            onSaveSettings(ipInput, parsedPort, hapticsInput, voiceInput, themeInput)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = AccentCyan, checkedTrackColor = AccentCyan.copy(alpha = 0.3f))
                    )
                }
            }
        }

        // Live Protocol stream terminal console
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showLogsConsole = !showLogsConsole }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Terminal, contentDescription = null, tint = Color.Green, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Protocol Monitor (Developer Mode)",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Icon(
                    imageVector = if (showLogsConsole) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.5f)
                )
            }
        }

        if (showLogsConsole) {
            if (logs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(Color.Black, RoundedCornerShape(10.dp))
                            .border(0.5.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No log traffic yet. Connect to PC to view JSON packets.",
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(logs.take(12)) { log ->
                    val isSent = log.direction == "SENT"
                    val isRcvd = log.direction == "RCVD"
                    val timestampStr = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date(log.timestamp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isSent) Color(0xFF0F172A) else if (isRcvd) Color(0xFF062F2F) else Color(0xFF131124),
                                RoundedCornerShape(6.dp)
                            )
                            .border(
                                0.5.dp,
                                if (isSent) Color(0xFF1E293B) else if (isRcvd) Color(0xFF0D9488) else Color.White.copy(alpha = 0.05f),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(8.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(if (isSent) Color(0xFF3B82F6) else if (isRcvd) Color(0xFF10B981) else Color(0xFFF59E0B))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = log.direction,
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = timestampStr,
                                        color = Color.White.copy(alpha = 0.35f),
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Text(
                                    text = "v1",
                                    color = Color.White.copy(alpha = 0.2f),
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = log.message,
                                color = if (isSent) Color(0xFF93C5FD) else if (isRcvd) Color(0xFF99F6E4) else Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Info block
        item {
            Text(
                text = "OniCanvas companion client v1.0.0\nMade with love for digital artists.",
                color = Color.White.copy(alpha = 0.25f),
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )
        }
    }
}
