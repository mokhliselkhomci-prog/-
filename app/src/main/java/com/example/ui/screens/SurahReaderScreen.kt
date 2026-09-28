package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AyahItem
import com.example.data.model.Reciter
import com.example.ui.components.IslamicCard
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldMedium
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun SurahReaderScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val surahDetail by viewModel.selectedSurah.collectAsState()
    val isLoading by viewModel.isSurahDetailLoading.collectAsState()
    val tafsirMap by viewModel.tafsirMap.collectAsState()
    val tafsirLoadingMap by viewModel.tafsirLoadingMap.collectAsState()
    val ayahFontSize by viewModel.ayahFontSize.collectAsState()
    val favorites by viewModel.favorites.collectAsState()

    val isPlaying by viewModel.audioPlayer.isPlaying.collectAsState()
    val currentSurahNum by viewModel.audioPlayer.currentSurahNumber.collectAsState()
    val currentAyahNum by viewModel.audioPlayer.currentAyahNumber.collectAsState()
    val currentReciter by viewModel.audioPlayer.currentReciter.collectAsState()
    val isAudioLoading by viewModel.audioPlayer.isLoading.collectAsState()

    var showReciterMenu by remember { mutableStateOf(false) }

    // Intercept back button to return to Quran list
    BackHandler {
        viewModel.audioPlayer.stop()
        viewModel.navigateTo(Screen.QURAN)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("surah_reader_screen")
    ) {
        // Reader Header Bar
        Surface(
            tonalElevation = 4.dp,
            shadowElevation = 3.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                viewModel.audioPlayer.stop()
                                viewModel.navigateTo(Screen.QURAN)
                            },
                            modifier = Modifier.testTag("back_to_surahs_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع لقائمة السور",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        surahDetail?.let { surah ->
                            Column {
                                Text(
                                    text = surah.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${surah.englishName} • ${surah.numberOfAyahs} آية",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Font Size adjustments
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GoldAccent.copy(alpha = 0.15f))
                                    .clickable { showReciterMenu = true }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = currentReciter.nameArabic.split(" ").takeLast(2).joinToString(" "),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GoldAccent
                                )
                            }

                            DropdownMenu(
                                expanded = showReciterMenu,
                                onDismissRequest = { showReciterMenu = false }
                            ) {
                                Reciter.DEFAULT_RECITERS.forEach { reciter ->
                                    DropdownMenuItem(
                                        text = { Text(reciter.nameArabic) },
                                        onClick = {
                                            viewModel.setReciter(reciter)
                                            showReciterMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        IconButton(onClick = { viewModel.setFontSize(ayahFontSize - 2f) }) {
                            Text("A-", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        IconButton(onClick = { viewModel.setFontSize(ayahFontSize + 2f) }) {
                            Text("A+", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }

                // Audio Player Control Strip
                surahDetail?.let { surah ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isPlaying) "التلاوة جارية: آية $currentAyahNum" else "استمع للتلاوة الصوتية",
                                fontSize = 12.sp,
                                color = if (isPlaying) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Normal
                            )
                            if (isAudioLoading) {
                                Spacer(modifier = Modifier.width(8.dp))
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = GoldAccent
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { viewModel.audioPlayer.playPrevious() },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SkipPrevious,
                                    contentDescription = "الآية السابقة",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            IconButton(
                                onClick = {
                                    val currentAyah = currentAyahNum ?: 1
                                    viewModel.playAyahAudio(surah.number, currentAyah, surah.numberOfAyahs)
                                },
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(GoldAccent)
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "إيقاف مؤقت" else "تشغيل",
                                    tint = EmeraldDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = { viewModel.audioPlayer.playNext() },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SkipNext,
                                    contentDescription = "الآية التالية",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        if (isLoading || surahDetail == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GoldAccent)
            }
        } else {
            val surah = surahDetail!!
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Bismillah Banner (All surahs except At-Tawbah 9)
                if (surah.number != 9) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                color = GoldAccent,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                items(surah.ayahs, key = { it.number }) { ayah ->
                    val isCurrentPlaying = (currentSurahNum == surah.number && currentAyahNum == ayah.numberInSurah && isPlaying)
                    val isFav = favorites.any { it.surahNumber == surah.number && it.ayahNumberInSurah == ayah.numberInSurah }
                    val tafsirText = tafsirMap[ayah.number]
                    val isTafsirLoading = tafsirLoadingMap[ayah.number] == true

                    AyahCardItem(
                        ayah = ayah,
                        surahName = surah.name,
                        surahNumber = surah.number,
                        totalAyahs = surah.numberOfAyahs,
                        fontSize = ayahFontSize,
                        isPlaying = isCurrentPlaying,
                        isFavorite = isFav,
                        tafsirText = tafsirText,
                        isTafsirLoading = isTafsirLoading,
                        onPlayAudio = {
                            viewModel.playAyahAudio(surah.number, ayah.numberInSurah, surah.numberOfAyahs)
                        },
                        onToggleTafsir = {
                            viewModel.toggleTafsir(ayah.number)
                        },
                        onToggleFavorite = {
                            viewModel.toggleFavorite(surah.number, surah.name, ayah.numberInSurah, ayah.text)
                        },
                        onCopy = {
                            val copyContent = "${ayah.text}\n[سورة ${surah.name}: ${ayah.numberInSurah}]"
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("القرآن الكريم", copyContent))
                            Toast.makeText(context, "تم نسخ الآية ✓", Toast.LENGTH_SHORT).show()
                        },
                        onShare = {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "${ayah.text}\n[سورة ${surah.name}: ${ayah.numberInSurah}]")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "مشاركة الآية"))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AyahCardItem(
    ayah: AyahItem,
    surahName: String,
    surahNumber: Int,
    totalAyahs: Int,
    fontSize: Float,
    isPlaying: Boolean,
    isFavorite: Boolean,
    tafsirText: String?,
    isTafsirLoading: Boolean,
    onPlayAudio: () -> Unit,
    onToggleTafsir: () -> Unit,
    onToggleFavorite: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    val cardBorder = if (isPlaying) GoldAccent else MaterialTheme.colorScheme.outline
    val cardBackground = if (isPlaying) GoldAccent.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface

    IslamicCard(
        modifier = Modifier
            .border(if (isPlaying) 2.dp else 1.dp, cardBorder, RoundedCornerShape(18.dp))
            .testTag("ayah_card_${ayah.numberInSurah}")
    ) {
        // Top Ayah Number Badge and Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(if (isPlaying) GoldAccent else MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${ayah.numberInSurah}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPlaying) EmeraldDark else GoldAccent
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onPlayAudio,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "استماع",
                        tint = if (isPlaying) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onToggleTafsir,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "تفسير",
                        tint = if (tafsirText != null) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "حفظ في المفضلة",
                        tint = if (isFavorite) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "نسخ",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onShare,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "مشاركة",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Ayah Arabic Text
        Text(
            text = ayah.text,
            fontSize = fontSize.sp,
            lineHeight = (fontSize * 1.8f).sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Justify,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        // Tafsir Accordion
        AnimatedVisibility(
            visible = isTafsirLoading || tafsirText != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, GoldAccent.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                if (isTafsirLoading) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = GoldAccent,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "جاري تحميل التفسير الميسر...",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else if (tafsirText != null) {
                    Column {
                        Text(
                            text = "التفسير الميسر:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = tafsirText,
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
