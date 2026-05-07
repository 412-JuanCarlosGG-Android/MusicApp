package com.example.musicapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.musicapp.data.Album
import com.example.musicapp.data.RetrofitClient
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import com.example.musicapp.ui.components.AlbumImage
import com.example.musicapp.ui.components.MiniPlayer
import com.example.musicapp.ui.components.clickableNoRipple
import com.example.musicapp.ui.theme.CardWhite
import com.example.musicapp.ui.theme.PurpleBg
import com.example.musicapp.ui.theme.PurpleDeep
import com.example.musicapp.ui.theme.PurplePrimary
import com.example.musicapp.ui.theme.PurpleScrim
import com.example.musicapp.ui.theme.TextDark
import com.example.musicapp.ui.theme.TextMuted

@Composable
fun HomeScreen(onAlbumClick: (Album) -> Unit) {
    var albums by remember { mutableStateOf<List<Album>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        try {
            val fetched = RetrofitClient.api.getAlbums()
            val loader = context.imageLoader
            albums = coroutineScope {
                fetched.map { album ->
                    async {
                        val result = loader.execute(
                            ImageRequest.Builder(context)
                                .data(album.image)
                                .build()
                        )
                        if (result is SuccessResult) album else null
                    }
                }.awaitAll().filterNotNull()
            }
        } catch (e: Exception) {
            error = e.message ?: "Error"
        } finally {
            loading = false
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(PurpleBg)) {
        when {
            loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PurplePrimary)
            }
            error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: $error", color = TextDark)
            }
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 24.dp, bottom = 110.dp)
            ) {
                item { Header() }
                item { SectionHeader("Albums") }
                item { AlbumsRow(albums, onAlbumClick) }
                item { SectionHeader("Recently Played") }
                items(albums) { album ->
                    RecentlyPlayedItem(album, onClick = { onAlbumClick(album) })
                }
            }
        }
        if (albums.isNotEmpty()) {
            MiniPlayer(
                title = albums.first().title,
                subtitle = albums.first().artist,
                image = albums.first().image,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun Header() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.verticalGradient(listOf(PurplePrimary, PurpleDeep))
            )
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Menu, contentDescription = null, tint = Color.White)
                Icon(Icons.Filled.Search, contentDescription = null, tint = Color.White)
            }
            Box(modifier = Modifier.height(48.dp))
            Text(
                "Good Morning!",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 14.sp
            )
            Text(
                "Carlos Gallegos",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = TextDark, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(text = "See more", color = PurplePrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun AlbumsRow(albums: List<Album>, onAlbumClick: (Album) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(albums) { album ->
            AlbumCard(album, onClick = { onAlbumClick(album) })
        }
    }
}

@Composable
private fun AlbumCard(album: Album, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 240.dp, height = 260.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(CardWhite)
            .padding(8.dp)
            .clickableNoRipple(onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
        ) {
            AlbumImage(
                url = album.image,
                contentDescription = album.title,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.55f to Color.Transparent,
                            1f to PurpleScrim
                        )
                    )
            )
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        album.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1
                    )
                    Text(
                        album.artist,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = PurpleDeep
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentlyPlayedItem(album: Album, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(CardWhite)
            .clickableNoRipple(onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AlbumImage(
            url = album.image,
            contentDescription = album.title,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(14.dp))
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                album.title,
                color = TextDark,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 1
            )
            Text(
                "${album.artist} • Popular Song",
                color = TextMuted,
                fontSize = 12.sp,
                maxLines = 1
            )
        }
        Icon(
            Icons.Filled.MoreVert,
            contentDescription = null,
            tint = TextMuted
        )
    }
}
