package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.CityPreset
import com.example.ui.components.IslamicCard
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldMedium
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.viewmodel.MainViewModel
import com.google.android.gms.location.LocationServices

@Composable
fun PrayerScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prayerData by viewModel.prayerData.collectAsState()
    val nextPrayer by viewModel.nextPrayer.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val isLoading by viewModel.isPrayerLoading.collectAsState()

    var showCityMenu by remember { mutableStateOf(false) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                    if (loc != null) {
                        viewModel.setLocationCoords(loc.latitude, loc.longitude)
                        Toast.makeText(context, "تم تحديد الموقع بنجاح ✓", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "تعذر تحديد الإحداثيات، تم اعتماد مدينة ${selectedCity.nameArabic}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: SecurityException) {
                // handled
            }
        } else {
            Toast.makeText(context, "تم رفض إذن الموقع، يمكنك اختيار مدينتك من القائمة", Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("prayer_screen")
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "مواقيت الصلاة",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "أوقات الصلاة الدقيقة حسب موقعك أو مدينتك",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // City Selector & GPS Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Dropdown card
            IslamicCard(
                modifier = Modifier.weight(1f),
                onClick = { showCityMenu = true }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "المدينة",
                            tint = GoldAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${selectedCity.nameArabic} (${selectedCity.countryArabic})",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "اختر مدينة",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                DropdownMenu(
                    expanded = showCityMenu,
                    onDismissRequest = { showCityMenu = false }
                ) {
                    CityPreset.PRESETS.forEach { city ->
                        DropdownMenuItem(
                            text = { Text("${city.nameArabic} - ${city.countryArabic}") },
                            onClick = {
                                viewModel.selectCity(city)
                                showCityMenu = false
                            }
                        )
                    }
                }
            }

            // GPS locate button
            Button(
                onClick = {
                    val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    if (hasFine || hasCoarse) {
                        try {
                            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                                if (loc != null) {
                                    viewModel.setLocationCoords(loc.latitude, loc.longitude)
                                    Toast.makeText(context, "تم تحديد الأوقات حسب موقعك الحالي ✓", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: SecurityException) {
                        }
                    } else {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = GoldAccent
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "موقعي الحالي",
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Next Prayer Hero Card
        nextPrayer?.let { info ->
            IslamicCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .border(1.5.dp, GoldAccent, RoundedCornerShape(18.dp))
                    .testTag("next_prayer_hero_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "الصلاة القادمة",
                            fontSize = 12.sp,
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = info.prayerNameArabic,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        val h = info.minutesRemaining / 60
                        val m = info.minutesRemaining % 60
                        val remText = if (h > 0) "المتبقي: ${h} ساعة و ${m} دقيقة" else "المتبقي: ${m} دقيقة"
                        Text(
                            text = remText,
                            fontSize = 12.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(GoldAccent)
                            .padding(horizontal = 18.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = info.prayerTime,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // All Prayer Times List
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GoldAccent)
            }
        } else {
            val timings = prayerData?.timings
            val prayers = listOf(
                PrayerDisplayItem("الفجر", timings?.fajr ?: "--:--", "🌅"),
                PrayerDisplayItem("الشروق", timings?.sunrise ?: "--:--", "☀️"),
                PrayerDisplayItem("الظهر", timings?.dhuhr ?: "--:--", "🕛"),
                PrayerDisplayItem("العصر", timings?.asr ?: "--:--", "🌤️"),
                PrayerDisplayItem("المغرب", timings?.maghrib ?: "--:--", "🌇"),
                PrayerDisplayItem("العشاء", timings?.isha ?: "--:--", "🌙")
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(prayers.size) { idx ->
                    val p = prayers[idx]
                    val isNext = nextPrayer?.prayerNameArabic == p.name

                    PrayerRowCard(
                        item = p,
                        isNext = isNext
                    )
                }
            }
        }
    }
}

data class PrayerDisplayItem(
    val name: String,
    val time: String,
    val icon: String
)

@Composable
fun PrayerRowCard(
    item: PrayerDisplayItem,
    isNext: Boolean
) {
    val cleanTime = item.time.split(" ").firstOrNull() ?: item.time

    IslamicCard(
        modifier = Modifier
            .then(
                if (isNext) Modifier.border(1.5.dp, GoldAccent, RoundedCornerShape(18.dp))
                else Modifier
            )
            .testTag("prayer_row_${item.name}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.icon,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = item.name,
                        fontSize = 16.sp,
                        fontWeight = if (isNext) FontWeight.Bold else FontWeight.Medium,
                        color = if (isNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                    if (isNext) {
                        Text(
                            text = "الصلاة القادمة",
                            fontSize = 11.sp,
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                text = cleanTime,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isNext) GoldAccent else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
