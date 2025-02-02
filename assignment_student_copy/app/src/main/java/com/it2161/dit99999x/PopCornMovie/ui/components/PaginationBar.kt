package com.it2161.dit99999x.PopCornMovie.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.it2161.dit99999x.PopCornMovie.R

@Composable
fun PaginationBar(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val maxPages = 500 // API limit
    val limitedTotalPages = minOf(totalPages, maxPages)
    val displayedPages = (currentPage - 2..currentPage + 2).filter { it in 1..limitedTotalPages }
    var showJumpToPageDialog by remember { mutableStateOf(false) }
    var pageInput by remember { mutableStateOf("") }

    // Dialog to jump to a specific page
    if (showJumpToPageDialog) {
        AlertDialog(
            onDismissRequest = { showJumpToPageDialog = false },
            title = { Text("Jump to Page") },
            text = {
                OutlinedTextField(
                    value = pageInput,
                    onValueChange = { pageInput = it },
                    label = { Text("Page Number") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val page = pageInput.toIntOrNull() ?: return@TextButton
                        if (page in 1..totalPages) {
                            onPageChange(page)
                            showJumpToPageDialog = false
                        }
                    }
                ) {
                    Text("Go")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showJumpToPageDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally), // Reduce spacing
        verticalAlignment = Alignment.CenterVertically
    ) {
        // First Page Button
        IconButton(
            onClick = { onPageChange(1) },
            enabled = currentPage > 1,
            modifier = Modifier.size(36.dp) // Reduce size if needed
        ) {
            Icon(
                painter = painterResource(id = R.drawable.round_keyboard_double_arrow_left_24),
                contentDescription = "First Page",
                modifier = Modifier.size(24.dp) // Reduce icon size slightly
            )
        }

        // Page Numbers (Ensure they remain the same size)
        val fixedWidth = 36.dp
        displayedPages.forEach { page ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .size(width = fixedWidth, height = 40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (page == currentPage) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent)
                    .clickable { onPageChange(page) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = page.toString(),
                    fontWeight = if (page == currentPage) FontWeight.Bold else FontWeight.Normal,
                    color = if (page == currentPage) MaterialTheme.colorScheme.primary else LocalContentColor.current,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Last Page Button
        IconButton(
            onClick = { onPageChange(limitedTotalPages) },
            enabled = currentPage < limitedTotalPages,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.round_keyboard_double_arrow_right_24),
                contentDescription = "Last Page",
                modifier = Modifier.size(24.dp)
            )
        }

        IconButton(
            onClick = { showJumpToPageDialog = true }
        ) {
            Icon(
                imageVector = Icons.Default.Search, // Material 3 search icon
                contentDescription = "Jump to Page"
            )
        }
    }
}