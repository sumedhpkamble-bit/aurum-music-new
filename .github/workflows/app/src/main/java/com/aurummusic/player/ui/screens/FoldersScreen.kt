package com.aurummusic.player.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aurummusic.player.data.model.AudioFolder
import com.aurummusic.player.data.model.Song
import com.aurummusic.player.ui.components.FolderCardItem
import com.aurummusic.player.ui.theme.BlackPrimary

@Composable
fun FoldersScreen(
    folders: List<AudioFolder>,
    onFolderClick: (AudioFolder) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BlackPrimary),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(folders, key = { it.path }) { folder ->
            FolderCardItem(
                folder = folder,
                onClick = { onFolderClick(folder) }
            )
        }
    }
}
