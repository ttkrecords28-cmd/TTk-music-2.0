package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Radio
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TTKGlassModifiers.glassPill

enum class AppNavTab(val title: String, val iconFilled: ImageVector, val iconOutlined: ImageVector) {
    HOME("Home 🏠", Icons.Filled.Home, Icons.Outlined.Home),
    TUNER("Tuner 📻", Icons.Filled.Radio, Icons.Outlined.Radio),
    SEARCH("Search 🔍", Icons.Filled.Search, Icons.Outlined.Search),
    LIBRARY("Library 📚", Icons.Filled.LibraryMusic, Icons.Outlined.LibraryMusic)
}

@Composable
fun AppBottomGlassTabs(
    selectedTab: AppNavTab,
    onTabSelected: (AppNavTab) -> Unit,
    primaryAccent: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        // Floating Frosted Glass Dock Container (Apple Music #tabs style)
        Row(
            modifier = Modifier
                .widthIn(max = 350.dp)
                .fillMaxWidth()
                .glassPill(
                    borderColor = Color.White.copy(alpha = 0.22f),
                    surfaceColor = Color(0xD80A0E1A),
                    accentGlow = primaryAccent
                )
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppNavTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab

                // The Lit Frosted Glass Tab Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .then(
                            if (isSelected) {
                                Modifier
                                    .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = primaryAccent)
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                Color.White.copy(alpha = 0.22f),
                                                Color.White.copy(alpha = 0.08f),
                                                primaryAccent.copy(alpha = 0.15f)
                                            )
                                        )
                                    )
                                    .border(
                                        width = 1.dp,
                                        brush = Brush.verticalGradient(
                                            listOf(
                                                Color.White.copy(alpha = 0.50f),
                                                primaryAccent.copy(alpha = 0.40f),
                                                Color.White.copy(alpha = 0.10f)
                                            )
                                        ),
                                        shape = RoundedCornerShape(24.dp)
                                    )
                                    .drawBehind {
                                        // Specular top edge catchlight on lit tab
                                        drawLine(
                                            color = Color.White.copy(alpha = 0.55f),
                                            start = Offset(12f, 1f),
                                            end = Offset(size.width - 12f, 1f),
                                            strokeWidth = 2f
                                        )
                                    }
                            } else {
                                Modifier
                            }
                        )
                        .clickable { onTabSelected(tab) }
                        .testTag("nav_tab_${tab.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) tab.iconFilled else tab.iconOutlined,
                            contentDescription = tab.title,
                            tint = if (isSelected) primaryAccent else Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tab.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) primaryAccent else Color.White.copy(alpha = 0.6f)
                            )
                        )
                    }
                }
            }
        }
    }
}
