package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppCurrency
import com.example.ui.theme.VegasBorder
import com.example.ui.theme.VegasCrimson
import com.example.ui.theme.VegasEmerald
import com.example.ui.theme.VegasEmeraldBright
import com.example.ui.theme.VegasGoldLight
import com.example.ui.theme.VegasGoldMetallic
import com.example.ui.theme.VegasGoldPrimary
import com.example.ui.theme.VegasSurfaceCard
import com.example.ui.theme.VegasTextMuted
import com.example.ui.theme.VegasTextSecondary
import com.example.ui.viewmodel.ChartDataPoint
import java.util.Locale

@Composable
fun BankrollChart(
    points: List<ChartDataPoint>,
    startingBankroll: Double,
    currency: AppCurrency,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val activePoint = selectedIndex?.let { points.getOrNull(it) } ?: points.lastOrNull()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(VegasSurfaceCard)
            .border(1.dp, VegasBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
            .testTag("bankroll_evolution_chart")
    ) {
        Column {
            // Chart Title & Active Point Readout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "EVOLUCIÓN DEL BANKROLL (P&L)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = VegasTextSecondary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Trayectoria en ${currency.code} (${currency.symbol})",
                        fontSize = 10.sp,
                        color = VegasTextMuted
                    )
                }

                if (activePoint != null) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = currency.format(activePoint.bankrollValue),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = if (activePoint.bankrollValue >= startingBankroll) VegasEmeraldBright else VegasCrimson
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val diff = activePoint.bankrollValue - startingBankroll
                            val sign = if (diff >= 0) "+" else ""
                            Text(
                                text = "$sign${currency.format(diff)} • ${activePoint.label}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (diff >= 0) VegasEmerald else VegasCrimson
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Canvas Line Chart
            val values = points.map { it.bankrollValue }
            val minVal = (values.minOrNull() ?: startingBankroll).coerceAtMost(startingBankroll * 0.95)
            val maxVal = (values.maxOrNull() ?: startingBankroll).coerceAtLeast(startingBankroll * 1.05)
            val range = if (maxVal - minVal > 0) maxVal - minVal else 1.0

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .matchParentSize()
                        .pointerInput(points) {
                            detectTapGestures(
                                onPress = { offset ->
                                    val count = points.size
                                    if (count > 1) {
                                        val stepX = size.width / (count - 1)
                                        val idx = (offset.x / stepX).toInt().coerceIn(0, count - 1)
                                        selectedIndex = idx
                                    }
                                }
                            )
                        }
                ) {
                    val width = size.width
                    val height = size.height
                    val paddingBottom = 24.dp.toPx()
                    val chartHeight = height - paddingBottom
                    val count = points.size

                    if (count < 2) return@Canvas

                    // Draw baseline dashed line for startingBankroll
                    val baselineY = (chartHeight - ((startingBankroll - minVal) / range * chartHeight)).toFloat()
                    drawLine(
                        color = Color(0xFF334155),
                        start = Offset(0f, baselineY),
                        end = Offset(width, baselineY),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )

                    // Compute Coordinates
                    val stepX = width / (count - 1)
                    val coords = points.mapIndexed { i, pt ->
                        val x = i * stepX
                        val y = (chartHeight - ((pt.bankrollValue - minVal) / range * chartHeight)).toFloat()
                        Offset(x, y)
                    }

                    // Build Smooth Cubic Curve
                    val linePath = Path().apply {
                        moveTo(coords.first().x, coords.first().y)
                        for (i in 0 until coords.size - 1) {
                            val p0 = coords[i]
                            val p1 = coords[i + 1]
                            val cx = (p0.x + p1.x) / 2
                            cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                        }
                    }

                    // Build Fill Path for Area Under Curve
                    val fillPath = Path().apply {
                        addPath(linePath)
                        lineTo(coords.last().x, chartHeight)
                        lineTo(coords.first().x, chartHeight)
                        close()
                    }

                    // Draw Area Gradient
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                VegasGoldPrimary.copy(alpha = 0.28f),
                                VegasEmerald.copy(alpha = 0.15f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = chartHeight
                        )
                    )

                    // Draw Curve Stroke
                    drawPath(
                        path = linePath,
                        brush = Brush.horizontalGradient(
                            colors = listOf(VegasGoldPrimary, VegasGoldMetallic, VegasEmeraldBright)
                        ),
                        style = Stroke(
                            width = 3.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )

                    // Draw Point Nodes
                    coords.forEachIndexed { idx, offset ->
                        val isSelected = (idx == selectedIndex)
                        if (isSelected) {
                            // Selected Halo
                            drawCircle(
                                color = VegasGoldPrimary.copy(alpha = 0.4f),
                                radius = 8.dp.toPx(),
                                center = offset
                            )
                            drawCircle(
                                color = VegasGoldMetallic,
                                radius = 5.dp.toPx(),
                                center = offset
                            )
                        } else if (idx == 0 || idx == coords.lastIndex) {
                            drawCircle(
                                color = VegasGoldMetallic,
                                radius = 4.dp.toPx(),
                                center = offset
                            )
                        }
                    }
                }
            }

            // Bottom X-Axis Labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = points.firstOrNull()?.label ?: "Inicio",
                    fontSize = 10.sp,
                    color = VegasTextMuted
                )
                if (points.size > 2) {
                    val mid = points[points.size / 2]
                    Text(
                        text = mid.label,
                        fontSize = 10.sp,
                        color = VegasTextMuted
                    )
                }
                Text(
                    text = points.lastOrNull()?.label ?: "Hoy",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = VegasGoldLight
                )
            }
        }
    }
}
