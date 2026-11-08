package com.lyyyyrics.player

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.audiofx.Visualizer
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.hypot

enum class SongMood { HAPPY, SAD, ENERGETIC, ROMANTIC, CALM }

private fun detectSongMood(song: Song?, lyrics: List<LrcLine>): SongMood {
    // Название учитывается сильнее текста, чтобы короткие/неполные тексты не сбивали настроение.
    val title = song?.title.orEmpty().lowercase()
    val text = lyrics.joinToString(" ") { it.text }.lowercase()
    val groups: List<Pair<SongMood, List<String>>> = listOf(
        SongMood.HAPPY to listOf("счаст", "радост", "улыб", "сме", "весел", "солнц", "праздник", "happy", "joy", "smile", "dance"),
        SongMood.SAD to listOf("груст", "плач", "слез", "один", "пуст", "боль", "потер", "скуч", "дожд", "уход", "фальшив", "обман", "лож", "предател", "разочар", "маск", "sad", "lonely", "cry", "miss you"),
        SongMood.ENERGETIC to listOf("огонь", "взрыв", "быстр", "беж", "танц", "движ", "летим", "разгон", "энерг", "energy", "fire", "run", "party"),
        SongMood.ROMANTIC to listOf("любов", "люблю", "сердц", "поцел", "нежн", "вместе", "родн", "kiss", "love", "baby"),
        SongMood.CALM to listOf("тишин", "спокой", "море", "ветер", "сон", "звезд", "звёзд", "покой", "calm", "quiet")
    )
    val scores: List<Pair<SongMood, Int>> = groups.map { entry ->
        val mood: SongMood = entry.first
        val words: List<String> = entry.second
        val titleScore = words.fold(0) { total: Int, word: String ->
            total + if (title.contains(word)) 3 else 0
        }
        val lyricScore = words.fold(0) { total: Int, word: String ->
            total + Regex(Regex.escape(word)).findAll(text).count()
        }
        Pair(mood, titleScore + lyricScore)
    }
    val best = scores.maxByOrNull { score -> score.second }
    return if (best == null || best.second == 0) SongMood.CALM else best.first
}

private fun moodColor(mood: SongMood): Color = when (mood) {
    SongMood.HAPPY -> Color(0xFFB96822)
    SongMood.SAD -> Color(0xFF3E4A59)
    SongMood.ENERGETIC -> Color(0xFF702B56)
    SongMood.ROMANTIC -> Color(0xFF743B58)
    SongMood.CALM -> Color(0xFF263D59)
}

@Composable
private fun MoodAtmosphere(mood: SongMood, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        when (mood) {
            SongMood.HAPPY -> {
                val center = Offset(w * 0.82f, h * 0.18f)
                drawCircle(Color(0xFFFFD66B).copy(alpha = 0.22f), w * 0.12f, center)
                drawCircle(Color(0xFFFFE7A0).copy(alpha = 0.5f), w * 0.055f, center)
                for (i in 0 until 12) {
                    val a = i * (Math.PI / 6.0)
                    val start = Offset(center.x + kotlin.math.cos(a).toFloat() * w * 0.075f, center.y + kotlin.math.sin(a).toFloat() * w * 0.075f)
                    val end = Offset(center.x + kotlin.math.cos(a).toFloat() * w * 0.105f, center.y + kotlin.math.sin(a).toFloat() * w * 0.105f)
                    drawLine(Color(0xFFFFD66B).copy(alpha = 0.45f), start, end, 3.dp.toPx())
                }
            }
            SongMood.SAD -> {
                // Мягкий дождь и серые облака для грустных треков.
                drawCircle(Color(0xFFB9C4D0).copy(alpha = 0.08f), w * 0.18f, Offset(w * 0.78f, h * 0.10f))
                for (i in 0 until 42) {
                    val x = (i * 97f % w)
                    val y = (i * 137f % h)
                    drawLine(Color(0xFFB7C7D8).copy(alpha = 0.22f), Offset(x, y), Offset(x - 9.dp.toPx(), y + 24.dp.toPx()), 1.5.dp.toPx())
                }
            }
            SongMood.ENERGETIC -> {
                for (i in 0 until 8) {
                    val x = w * (0.12f + i * 0.11f)
                    drawLine(Color(0xFFFF8B62).copy(alpha = 0.16f), Offset(x, h * 0.05f), Offset(x + 45.dp.toPx(), h * 0.95f), 8.dp.toPx())
                }
            }
            SongMood.ROMANTIC -> {
                drawCircle(Color(0xFFFFA6C1).copy(alpha = 0.12f), w * 0.16f, Offset(w * 0.82f, h * 0.2f))
            }
            SongMood.CALM -> {
                drawCircle(Color(0xFF9CCBFF).copy(alpha = 0.12f), w * 0.10f, Offset(w * 0.82f, h * 0.16f))
                for (i in 0 until 18) {
                    val x = (i * 131f % w)
                    val y = (i * 79f % (h * 0.55f))
                    drawCircle(Color.White.copy(alpha = 0.22f), 1.5.dp.toPx(), Offset(x, y))
                }
            }
        }
    }
}

data class Song(
    val uri: Uri,
    val title: String,
    val artist: String,
    val cover: Bitmap?,
    val durationSec: Int
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Полноэкранный режим: интерфейс занимает весь экран, как на ПК.
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            )

        setContent { PlayerApp() }
    }
}

fun fileName(context: Context, uri: Uri): String {
    return context.contentResolver.query(uri, null, null, null, null)?.use { c ->
        val i = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (c.moveToFirst() && i >= 0) c.getString(i) else null
    } ?: "Трек"
}

// "Исполнитель - Название.mp3" -> (исполнитель, название)
fun guessFromName(name: String): Pair<String?, String> {
    val base = name.substringBeforeLast('.').replace('_', ' ').trim()
    val parts = base.split(" - ", limit = 2)
    return if (parts.size == 2) Pair(parts[0].trim(), parts[1].trim()) else Pair(null, base)
}

fun loadSong(context: Context, uri: Uri): Song {
    val r = MediaMetadataRetriever()
    val guess = guessFromName(fileName(context, uri))
    return try {
        r.setDataSource(context, uri)
        val title = r.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        val artist = r.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        val durMs = r.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            ?.toLongOrNull() ?: 0L
        val pic = r.embeddedPicture
        val cover = pic?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
        Song(
            uri,
            if (title.isNullOrBlank()) guess.second else title,
            if (artist.isNullOrBlank()) (guess.first ?: "Неизвестный исполнитель") else artist,
            cover,
            (durMs / 1000).toInt()
        )
    } catch (e: Exception) {
        Song(uri, guess.second, guess.first ?: "Неизвестный исполнитель", null, 0)
    } finally {
        r.release()
    }
}

fun fmt(ms: Long): String {
    val s = ms / 1000
    return "%d:%02d".format(s / 60, s % 60)
}

fun coverColor(song: Song?): Color {
    if (song == null) return Color(0xFF3B1F6B)
    val bmp = song.cover
    if (bmp != null) {
        val p = Palette.from(bmp).generate()
        val c = p.getDarkVibrantColor(
            p.getDarkMutedColor(p.getDominantColor(0xFF3B1F6B.toInt()))
        )
        return Color(c)
    }
    val hue = ((song.title.hashCode() % 360) + 360) % 360
    return Color.hsv(hue.toFloat(), 0.6f, 0.45f)
}

fun shiftHue(c: Color, deg: Float): Color {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(c.toArgb(), hsv)
    return Color.hsv(
        (hsv[0] + deg) % 360f,
        hsv[1].coerceIn(0.4f, 1f),
        hsv[2].coerceIn(0.35f, 0.7f)
    )
}

// 0 - назад, 1 - играть, 2 - пауза, 3 - вперёд
@Composable
fun TransportIcon(kind: Int) {
    Canvas(Modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        val white = Color.White
        when (kind) {
            0 -> {
                drawRect(white, Offset(w * 0.15f, h * 0.2f), Size(w * 0.12f, h * 0.6f))
                val p = Path().apply {
                    moveTo(w * 0.85f, h * 0.2f)
                    lineTo(w * 0.35f, h * 0.5f)
                    lineTo(w * 0.85f, h * 0.8f)
                    close()
                }
                drawPath(p, white)
            }
            1 -> {
                val p = Path().apply {
                    moveTo(w * 0.25f, h * 0.12f)
                    lineTo(w * 0.9f, h * 0.5f)
                    lineTo(w * 0.25f, h * 0.88f)
                    close()
                }
                drawPath(p, white)
            }
            2 -> {
                drawRoundRect(
                    white, Offset(w * 0.2f, h * 0.15f),
                    Size(w * 0.2f, h * 0.7f), CornerRadius(3f, 3f)
                )
                drawRoundRect(
                    white, Offset(w * 0.6f, h * 0.15f),
                    Size(w * 0.2f, h * 0.7f), CornerRadius(3f, 3f)
                )
            }
            else -> {
                drawRect(white, Offset(w * 0.73f, h * 0.2f), Size(w * 0.12f, h * 0.6f))
                val p = Path().apply {
                    moveTo(w * 0.15f, h * 0.2f)
                    lineTo(w * 0.65f, h * 0.5f)
                    lineTo(w * 0.15f, h * 0.8f)
                    close()
                }
                drawPath(p, white)
            }
        }
    }
}

@Composable
fun CoverCard(song: Song?, mood: SongMood, modifier: Modifier) {
    Box(
        modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.08f))
    ) {
        val cover = song?.cover
        if (cover != null) {
            Image(
                bitmap = cover.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            val accent = when (mood) {
                SongMood.HAPPY -> Color(0xFFFFC857)
                SongMood.SAD -> Color(0xFF9DB4CC)
                SongMood.ENERGETIC -> Color(0xFFFF765E)
                SongMood.ROMANTIC -> Color(0xFFFF9DBB)
                SongMood.CALM -> Color(0xFF80C7E8)
            }
            val symbol = when (mood) {
                SongMood.HAPPY -> "☀"
                SongMood.SAD -> "☂"
                SongMood.ENERGETIC -> "ϟ"
                SongMood.ROMANTIC -> "♡"
                SongMood.CALM -> "✦"
            }
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Brush.linearGradient(listOf(moodColor(mood), shiftHue(moodColor(mood), 42f), Color(0xFF090B14))))
            ) {
                MoodAtmosphere(mood, Modifier.fillMaxSize())
                Box(
                    Modifier
                        .align(Alignment.Center)
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.12f))
                        .border(1.dp, accent.copy(alpha = 0.3f), CircleShape)
                )
                Column(
                    Modifier
                        .align(Alignment.Center)
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(symbol, color = accent, fontSize = 76.sp, fontWeight = FontWeight.Light)
                    Text(
                        song?.title ?: "БЕЗ НАЗВАНИЯ",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        song?.artist ?: "НЕИЗВЕСТНЫЙ ИСПОЛНИТЕЛЬ",
                        color = Color.White.copy(alpha = 0.72f),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        when (mood) {
                            SongMood.HAPPY -> "GOOD VIBES"
                            SongMood.SAD -> "AFTER THE RAIN"
                            SongMood.ENERGETIC -> "FULL POWER"
                            SongMood.ROMANTIC -> "IN LOVE"
                            SongMood.CALM -> "SLOW MOMENTS"
                        },
                        color = accent.copy(alpha = 0.9f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }
            }
        }
        Text(
            if (cover != null) "Сейчас в эфире" else "ОБЛОЖКА ПО НАСТРОЕНИЮ",
            color = Color.White,
            fontSize = 9.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black.copy(alpha = 0.45f))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun PlayerAction(label: String, glyph: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(13.dp))
            .background(Color.White.copy(alpha = 0.09f))
            .border(1.dp, Color.White.copy(alpha = 0.16f), RoundedCornerShape(13.dp))
            .clickable { onClick() }
            .padding(horizontal = 9.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(glyph, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Text(
            label,
            color = Color.White.copy(alpha = 0.82f),
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.7.sp
        )
    }
}

@Composable
fun PlayerCard(
    song: Song?,
    isPlaying: Boolean,
    position: Long,
    duration: Long,
    onPrev: () -> Unit,
    onToggle: () -> Unit,
    onNext: () -> Unit,
    onSeek: (Float) -> Unit,
    onAdd: () -> Unit,
    onSearch: () -> Unit,
    onCopy: () -> Unit,
    onScene: () -> Unit,
    modifier: Modifier
) {
    val frac = if (duration > 0) (position.toFloat() / duration).coerceIn(0f, 1f) else 0f
    Column(
        modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    song?.title ?: "Добавь песню",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    song?.artist ?: "",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            PlayerAction("КЛИП", "▣", onScene)
            PlayerAction("ПОИСК", "⌕", onSearch)
            PlayerAction("ТРЕК", "≡", onCopy)
            Box(
                Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.16f))
                    .border(1.dp, Color.White.copy(alpha = 0.22f), CircleShape)
                    .clickable { onAdd() },
                contentAlignment = Alignment.Center
            ) {
                Text("+", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Light)
            }
        }

        Box(
            Modifier
                .fillMaxWidth()
                .height(14.dp)
                .pointerInput(duration) {
                    detectTapGestures { o -> onSeek(o.x / size.width) }
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
            )
            Box(
                Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(frac)
                    .height(3.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(fmt(position), color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.clickable { onPrev() }.padding(horizontal = 12.dp, vertical = 2.dp)) {
                    TransportIcon(0)
                }
                Box(Modifier.clickable { onToggle() }.padding(horizontal = 12.dp, vertical = 2.dp)) {
                    TransportIcon(if (isPlaying) 2 else 1)
                }
                Box(Modifier.clickable { onNext() }.padding(horizontal = 12.dp, vertical = 2.dp)) {
                    TransportIcon(3)
                }
            }
            Text(fmt(duration), color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
        }
    }
}

@Composable
fun PlayerApp() {
    val context = LocalContext.current
    val player = remember { ExoPlayer.Builder(context).build() }
    val prefs = remember { context.getSharedPreferences("scenes", Context.MODE_PRIVATE) }

    var songs by remember { mutableStateOf(listOf<Song>()) }
    var index by remember { mutableStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var position by remember { mutableStateOf(0L) }
    var duration by remember { mutableStateOf(0L) }
    var audioSession by remember { mutableStateOf(player.audioSessionId) }
    var bars by remember { mutableStateOf(FloatArray(24)) }
    var micGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var lyrics by remember { mutableStateOf(listOf<LrcLine>()) }
    var status by remember { mutableStateOf("Добавь песню, и здесь появится текст") }
    var matched by remember { mutableStateOf(listOf<WKind>()) }
    var scene by remember { mutableStateOf(listOf<SLine>()) }
    var bannerApp by remember { mutableStateOf("") }
    var bannerText by remember { mutableStateOf("") }
    var bannerVisible by remember { mutableStateOf(false) }
    var bannerTick by remember { mutableStateOf(0) }
    var showSearch by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var manualQuery by remember { mutableStateOf<String?>(null) }
    var showScene by remember { mutableStateOf(false) }
    var sceneReference by remember { mutableStateOf("") }
    var sceneText by remember { mutableStateOf("") }
    var showInfo by remember { mutableStateOf(false) }

    val micLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { micGranted = it }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                index = player.currentMediaItemIndex
            }

            override fun onAudioSessionIdChanged(audioSessionId: Int) {
                audioSession = audioSessionId
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    LaunchedEffect(player) {
        while (true) {
            position = player.currentPosition
            duration = maxOf(player.duration, 0L)
            delay(120)
        }
    }

    DisposableEffect(audioSession, micGranted) {
        var vis: Visualizer? = null
        if (micGranted && audioSession != 0) {
            try {
                val v = Visualizer(audioSession)
                v.setCaptureSize(Visualizer.getCaptureSizeRange()[1])
                v.setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
                    override fun onWaveFormDataCapture(
                        visualizer: Visualizer?, waveform: ByteArray?, samplingRate: Int
                    ) {
                    }

                    override fun onFftDataCapture(
                        visualizer: Visualizer?, fft: ByteArray?, samplingRate: Int
                    ) {
                        if (fft == null) return
                        val n = 24
                        val usable = fft.size / 2
                        val out = FloatArray(n)
                        for (i in 0 until n) {
                            val t = i.toFloat() / n
                            val idx = 1 + (t * t * (usable - 2)).toInt()
                            val re = fft[2 * idx].toInt().toFloat()
                            val im = fft[2 * idx + 1].toInt().toFloat()
                            out[i] = (hypot(re, im) / 90f).coerceIn(0f, 1f)
                        }
                        bars = out
                    }
                }, Visualizer.getMaxCaptureRate() / 2, false, true)
                v.setEnabled(true)
                vis = v
            } catch (e: Exception) {
                vis = null
            }
        }
        onDispose { vis?.release() }
    }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
            val wasEmpty = songs.isEmpty()
            val loaded = uris.map { loadSong(context, it) }
            songs = songs + loaded
            loaded.forEach { player.addMediaItem(MediaItem.fromUri(it.uri)) }
            if (wasEmpty) {
                player.prepare()
                player.play()
            }
        }
    }

    val song = songs.getOrNull(index)

    LaunchedEffect(song?.uri) {
        lyrics = emptyList()
        matched = emptyList()
        manualQuery = null
        scene = if (song != null) {
            prefs.getString(song.uri.toString(), null)?.let { parseScene(it) } ?: emptyList()
        } else emptyList()
        if (song != null) {
            status = "Ищу текст…"
            var r = withContext(Dispatchers.IO) {
                fetchLyrics(song.title, song.artist, song.durationSec)
            }
            if (song.cover == null || r.isEmpty()) {
                val q = if (song.artist != "Неизвестный исполнитель")
                    "${song.artist} ${song.title}" else song.title
                val hit = withContext(Dispatchers.IO) { searchItunes(q) }
                if (hit != null && looksSame(song.title, hit.title)) {
                    if (hit.cover != null && song.cover == null) {
                        songs = songs.map {
                            if (it.uri == song.uri) it.copy(cover = hit.cover) else it
                        }
                    }
                    if (r.isEmpty()) {
                        r = withContext(Dispatchers.IO) { fetchLyrics(hit.title, hit.artist, 0) }
                    }
                }
            }
            lyrics = r
            status = if (r.isEmpty()) "Текст не найден. Нажми 🔍 и введи название" else ""
        }
    }

    LaunchedEffect(manualQuery) {
        val q = manualQuery
        if (q != null && q.isNotBlank()) {
            status = "Ищу текст…"
            val hit = withContext(Dispatchers.IO) { searchItunes(q) }
            var r = withContext(Dispatchers.IO) {
                fetchLyrics(hit?.title ?: q, hit?.artist ?: "", 0)
            }
            if (r.isEmpty()) r = withContext(Dispatchers.IO) { fetchLyrics(q, "", 0) }
            if (hit != null && song != null) {
                songs = songs.map {
                    if (it.uri == song.uri) {
                        it.copy(
                            title = hit.title.ifBlank { it.title },
                            artist = hit.artist.ifBlank { it.artist },
                            cover = hit.cover ?: it.cover
                        )
                    } else it
                }
            }
            lyrics = r
            status = if (r.isEmpty()) "Текст не найден" else ""
        }
    }

    val current = if (lyrics.isEmpty()) -1
    else lyrics.indexOfLast { it.timeMs <= position + 150 }
    val progress = if (current >= 0) {
        val start = lyrics[current].timeMs
        val end = lyrics.getOrNull(current + 1)?.timeMs ?: (start + 4000)
        ((position - start).toFloat() / (end - start).coerceAtLeast(1)).coerceIn(0f, 1f)
    } else 0f

    val sl: SLine? = if (scene.isNotEmpty() && current >= 0) {
        scene.lastOrNull { it.i <= current }
    } else null

    LaunchedEffect(current, lyrics) {
        if (current >= 0 && scene.isEmpty()) {
            val found = detectWidgets(lyrics[current].text)
            matched = found
            if (found.isNotEmpty()) {
                bannerApp = found.first().bannerApp
                bannerText = found.first().bannerText
                bannerTick += 1
            }
        }
    }

    LaunchedEffect(sl?.i, scene) {
        val b = sl?.banner
        if (b != null) {
            bannerApp = b.app
            bannerText = b.text
            bannerTick += 1
        }
    }

    LaunchedEffect(bannerTick) {
        if (bannerTick > 0) {
            bannerVisible = true
            delay(3200)
            bannerVisible = false
        }
    }

    // Настроение трека влияет на фон и набор нижних виджетов.
    val mood = remember(song?.uri, lyrics) { detectSongMood(song, lyrics) }
    val neutral = when (mood) {
        SongMood.HAPPY -> listOf(WKind.HEART, WKind.SPEED, WKind.HEAVEN, WKind.MEETING, WKind.VIS)
        SongMood.SAD -> listOf(WKind.WEATHER, WKind.MOON, WKind.LONELY, WKind.DISTANCE, WKind.CANDLE)
        SongMood.ENERGETIC -> listOf(WKind.SPEED, WKind.VIS, WKind.BATTERY, WKind.HEART, WKind.STOPWATCH)
        SongMood.ROMANTIC -> listOf(WKind.HEART, WKind.MEETING, WKind.CANDLE, WKind.MOON, WKind.HEAVEN)
        SongMood.CALM -> listOf(WKind.MOON, WKind.CLOCK, WKind.COMPASS, WKind.CANDLE, WKind.VIS)
    }
    val rot = (position / 12000).toInt()
    val slot0 = matched.getOrNull(0) ?: neutral[(rot * 2) % neutral.size]
    val slot1Raw = matched.getOrNull(1)
        ?: if (matched.isNotEmpty()) WKind.VIS else neutral[(rot * 2 + 1) % neutral.size]
    val slot1 = if (slot1Raw == slot0) neutral[(rot * 2 + 2) % neutral.size] else slot1Raw

    val w0: SWidget
    val w1: SWidget
    if (sl != null && sl.widgets.isNotEmpty()) {
        w0 = sl.widgets[0]
        w1 = sl.widgets.getOrNull(1) ?: kindWidget(WKind.VIS)
    } else {
        w0 = kindWidget(slot0)
        w1 = kindWidget(slot1)
    }

    val sceneHue = sl?.hue
    val base = remember(song?.uri, sceneHue, mood) {
        if (sceneHue != null) Color.hsv(sceneHue, 0.65f, 0.45f)
        else moodColor(mood)
    }
    val hueShift = if (current >= 0) (current % 8) * 12f else (rot % 8) * 12f
    val c1 by animateColorAsState(base, tween(900), label = "c1")
    val c2target = remember(base, hueShift, sceneHue) {
        if (sceneHue != null) Color.hsv((sceneHue + 50f) % 360f, 0.7f, 0.5f)
        else shiftHue(base, 40f + hueShift)
    }
    val c2 by animateColorAsState(c2target, tween(1500), label = "c2")

    if (showSearch) {
        AlertDialog(
            onDismissRequest = { showSearch = false },
            title = { Text("Найти текст") },
            text = {
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Исполнитель и название") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    manualQuery = searchText
                    showSearch = false
                }) { Text("Искать") }
            },
            dismissButton = {
                TextButton(onClick = { showSearch = false }) { Text("Отмена") }
            }
        )
    }

    if (showScene) {
        AlertDialog(
            onDismissRequest = { showScene = false },
            title = { Text("Клип по таймингам") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "1. Вставь референс: опиши стиль, кадры или эффект, который хочешь повторить.",
                        fontSize = 12.sp
                    )
                    TextField(
                        value = sceneReference,
                        onValueChange = { sceneReference = it },
                        placeholder = { Text("Референс клипа / идея / стиль") },
                        minLines = 2,
                        maxLines = 4
                    )
                    Text(
                        "2. После генерации вставь сюда JSON-код сцен. Каждая сцена привязана к номеру строки и времени.",
                        fontSize = 12.sp
                    )
                    TextField(
                        value = sceneText,
                        onValueChange = { sceneText = it },
                        placeholder = { Text("Вставь готовый код сцен сюда") },
                        minLines = 3,
                        maxLines = 6
                    )
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TextButton(onClick = {
                        val prompt = buildString {
                            append(SCENE_PROMPT)
                            append("\n\nРЕФЕРЕНС ПОЛЬЗОВАТЕЛЯ:\n")
                            append(sceneReference.ifBlank { "Создай стиль сам по настроению песни." })
                            append("\n\nПЕСНЯ: ")
                            append(song?.artist ?: "Неизвестный исполнитель")
                            append(" — ")
                            append(song?.title ?: "Без названия")
                            append("\nТАЙМИНГИ СТРОК (номер|время):\n")
                            lyrics.forEachIndexed { i, line ->
                                append(i).append("|").append(fmt(line.timeMs)).append("\n")
                            }
                            append("\nВерни код сцен для всех подходящих таймингов. Не вставляй текст песни.")
                        }
                        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        cm.setPrimaryClip(ClipData.newPlainText("clip_prompt", prompt))
                        Toast.makeText(context, "Задание с референсом и таймингами скопировано", Toast.LENGTH_LONG).show()
                    }) { Text("Скопировать задание") }
                    TextButton(onClick = {
                        val p = parseScene(sceneText)
                        if (p == null || p.isEmpty()) {
                            Toast.makeText(context, "Не получилось прочитать JSON-код", Toast.LENGTH_LONG).show()
                        } else {
                            scene = p
                            if (song != null) prefs.edit().putString(song.uri.toString(), sceneText).apply()
                            showScene = false
                        }
                    }) { Text("Применить код") }
                    TextButton(onClick = {
                        scene = emptyList()
                        if (song != null) prefs.edit().remove(song.uri.toString()).apply()
                        sceneText = ""
                        showScene = false
                        Toast.makeText(context, "Своя сцена удалена — включены виджеты по настроению", Toast.LENGTH_LONG).show()
                    }) { Text("Сбросить") }
                }
            },
            dismissButton = {
                TextButton(onClick = { showScene = false }) { Text("Закрыть") }
            }
        )
    }

    if (showInfo) {
        AlertDialog(
            onDismissRequest = { showInfo = false },
            title = { Text("Информация о треке") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Text(song?.title ?: "Название неизвестно", fontWeight = FontWeight.Bold)
                    Text("Исполнитель: ${song?.artist ?: "Неизвестен"}")
                    Text("Длительность: ${if (duration > 0) fmt(duration) else "Не определена"}")
                    Text("Текст: ${if (lyrics.isNotEmpty()) "найден" else "пока не найден"}")
                    Text("Строк с таймингами: ${lyrics.size}")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val details = "Трек: ${song?.title ?: "Без названия"}\nИсполнитель: ${song?.artist ?: "Неизвестен"}\nДлительность: ${if (duration > 0) fmt(duration) else "—"}\nСтрок с таймингами: ${lyrics.size}"
                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    cm.setPrimaryClip(ClipData.newPlainText("track_info", details))
                    Toast.makeText(context, "Информация о треке скопирована", Toast.LENGTH_SHORT).show()
                    showInfo = false
                }) { Text("Скопировать данные") }
            },
            dismissButton = {
                TextButton(onClick = { showInfo = false }) { Text("Готово") }
            }
        )
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(c1, c2, Color(0xFF0B0B0B))))
    ) {
        MoodAtmosphere(mood, Modifier.fillMaxSize())

        val bg = song?.cover
        if (bg != null) {
            Image(
                bitmap = bg.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(50.dp)
                    .alpha(0.12f)
            )
        }

        Row(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 38.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            Column(
                Modifier
                    .weight(0.43f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CoverCard(song, mood, Modifier.weight(1.35f).fillMaxWidth())
                PlayerCard(
                    song = song,
                    isPlaying = isPlaying,
                    position = position,
                    duration = duration,
                    onPrev = { player.seekToPreviousMediaItem() },
                    onToggle = { if (isPlaying) player.pause() else player.play() },
                    onNext = { player.seekToNextMediaItem() },
                    onSeek = { f -> if (duration > 0) player.seekTo((f * duration).toLong()) },
                    onAdd = { picker.launch(arrayOf("audio/*")) },
                    onSearch = {
                        searchText = if (song != null && song.artist != "Неизвестный исполнитель")
                            "${song.artist} ${song.title}" else (song?.title ?: "")
                        showSearch = true
                    },
                    onCopy = { showInfo = true },
                    onScene = {
                        sceneText = ""
                        sceneReference = ""
                        showScene = true
                    },
                    modifier = Modifier.weight(0.82f).fillMaxWidth()
                )
                Row(
                    Modifier.weight(0.88f).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(Modifier.weight(1f).fillMaxHeight()) {
                        Crossfade(w0, animationSpec = tween(500), label = "s0") { k ->
                            SceneWidget(k, bars, micGranted) {
                                micLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                    }
                    Box(Modifier.weight(1f).fillMaxHeight()) {
                        Crossfade(w1, animationSpec = tween(500), label = "s1") { k ->
                            SceneWidget(k, bars, micGranted) {
                                micLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                    }
                }
            }

            LyricsView(
                lines = lyrics,
                current = current,
                progress = progress,
                status = status,
                modifier = Modifier
                    .weight(0.57f)
                    .fillMaxHeight()
            )
        }

        AnimatedVisibility(
            visible = bannerVisible,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 12.dp, end = 20.dp)
        ) {
            Banner(bannerApp, bannerText)
        }
    }
}
