package com.example.colearnhub.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.colearnhub.modelLayer.TagData
import androidx.compose.foundation.layout.FlowRow

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagFilterSection(
    availableTags: List<TagData>,
    selectedTags: List<TagData>,
    onTagToggle: (TagData) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // All Tags
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            availableTags.forEach { tag ->
                if (selectedTags.contains(tag)) {
                    SelectedTagChip(
                        tag = tag,
                        onRemove = { onTagToggle(tag) }
                    )
                } else {
                    AvailableTagChip(
                        tag = tag,
                        onSelect = { onTagToggle(tag) }
                    )
                }
            }
        }
    }
}

@Composable
fun SelectedTagChip(
    tag: TagData,
    onRemove: () -> Unit
) {
    // Lista de cores para as tags
    val tagColors = listOf(
        Color(0xFF4A90E2), // Azul
        Color(0xFF50C878), // Verde
        Color(0xFFFFA500), // Laranja
        Color(0xFFE91E63), // Rosa
        Color(0xFF9C27B0), // Roxo
        Color(0xFF00BCD4), // Ciano
        Color(0xFFFF6B6B), // Vermelho
        Color(0xFF795548)  // Marrom
    )

    val tagColor = tagColors[tag.id.toInt() % tagColors.size]

    Box(
        modifier = Modifier
            .background(
                color = tagColor,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onRemove() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = tag.description,
                fontSize = 12.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )

            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove ${tag.description}",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun AvailableTagChip(
    tag: TagData,
    onSelect: () -> Unit
) {
    // Lista de cores para as tags
    val tagColors = listOf(
        Color(0xFF4A90E2), // Azul
        Color(0xFF50C878), // Verde
        Color(0xFFFFA500), // Laranja
        Color(0xFFE91E63), // Rosa
        Color(0xFF9C27B0), // Roxo
        Color(0xFF00BCD4), // Ciano
        Color(0xFFFF6B6B), // Vermelho
        Color(0xFF795548)  // Marrom
    )

    val tagColor = tagColors[tag.id.toInt() % tagColors.size]

    Box(
        modifier = Modifier
            .border(
                width = 1.5.dp,
                color = tagColor,
                shape = RoundedCornerShape(20.dp)
            )
            .background(
                color = Color.Transparent,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onSelect() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = tag.description,
                fontSize = 12.sp,
                color = tagColor,
                fontWeight = FontWeight.Medium
            )
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add ${tag.description}",
                tint = tagColor,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}