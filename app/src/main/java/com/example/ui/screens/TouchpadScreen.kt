package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.PrimaryIndigo

@Composable
fun TouchpadScreen(
    onTriggerAction: (String, String) -> Unit
) {
    var isGestureMode by remember { mutableStateOf(true) }
    var currentGestureState by remember { mutableStateOf("Ready. Tap, drag, or pinch to control canvas.") }
    var dragOffsetsX by remember { mutableStateOf(0f) }
    var dragOffsetsY by remember { mutableStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080710))
            .padding(16.dp)
    ) {
        // Toolbar
        Column {
            Text(
                text = "Gesture Pad",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Wireless touch & gesture control surface",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Toggle pill buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF131124))
                .border(0.5.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                .padding(4.dp)
        ) {
            Button(
                onClick = { isGestureMode = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isGestureMode) PrimaryIndigo else Color.Transparent
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Gesture, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Gesture Pad", fontSize = 12.sp)
                }
            }
            Button(
                onClick = { isGestureMode = false },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!isGestureMode) PrimaryIndigo else Color.Transparent
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Mouse, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Mouse Touchpad", fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Info card showing active state
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.RadioButtonChecked,
                    contentDescription = null,
                    tint = AccentCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = currentGestureState,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Large interaction touchpad area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                .background(Color(0xFF131124).copy(alpha = 0.3f))
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            currentGestureState = "Double Tapped! Triggering Zoom Fit."
                            onTriggerAction("Fit Screen", "Ctrl+0")
                        },
                        onTap = {
                            currentGestureState = if (isGestureMode) {
                                "Single Tapped! Left Click Simulation."
                            } else {
                                "Tap Left Click."
                            }
                            onTriggerAction("Click", "LeftClick")
                        },
                        onLongPress = {
                            currentGestureState = "Long Pressed! Toggle Right-Click Menu."
                            onTriggerAction("Menu", "RightClick")
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            dragOffsetsX = 0f
                            dragOffsetsY = 0f
                        },
                        onDragEnd = {
                            currentGestureState = "Gesture ended."
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        dragOffsetsX += dragAmount.x
                        dragOffsetsY += dragAmount.y

                        if (isGestureMode) {
                            if (Math.abs(dragOffsetsY) > 120 && Math.abs(dragOffsetsX) < 40) {
                                if (dragOffsetsY > 0) {
                                    currentGestureState = "Swiped Down: Triggering Redo"
                                    onTriggerAction("Redo", "Ctrl+Y")
                                    dragOffsetsY = 0f
                                } else {
                                    currentGestureState = "Swiped Up: Triggering Undo"
                                    onTriggerAction("Undo", "Ctrl+Z")
                                    dragOffsetsY = 0f
                                }
                            } else {
                                currentGestureState = "Dragging: Simulating Canvas Pan (Spacebar active)"
                                onTriggerAction("Pan", "Space")
                            }
                        } else {
                            currentGestureState = "Moving cursor: DeltaX=${dragAmount.x.toInt()}, DeltaY=${dragAmount.y.toInt()}"
                            onTriggerAction("Mouse Move", "MoveCursor")
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.White.copy(alpha = 0.03f), CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PanTool,
                        contentDescription = "Hand",
                        tint = AccentCyan,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (isGestureMode) "Touchpad Gesture Zone" else "Mouse Trackpad Zone",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isGestureMode) {
                        "• Single Tap to click\n• Double Tap to Reset Canvas Zoom\n• Swipe Up/Down to Undo/Redo\n• Drag to Pan Canvas"
                    } else {
                        "• Slide single finger to move cursor\n• Tap once to Left-Click\n• Long-press to Right-Click"
                    },
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Simulated quick mouse buttons for Touchpad
        if (!isGestureMode) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        currentGestureState = "Simulating Left Mouse Click."
                        onTriggerAction("Click", "LeftClick")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF131124)),
                    modifier = Modifier
                        .weight(1f)
                        .border(0.5.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Left Click", color = Color.White)
                }
                Button(
                    onClick = {
                        currentGestureState = "Simulating Right Mouse Click."
                        onTriggerAction("Click", "RightClick")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF131124)),
                    modifier = Modifier
                        .weight(1f)
                        .border(0.5.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Right Click", color = Color.White)
                }
            }
        }
    }
}
