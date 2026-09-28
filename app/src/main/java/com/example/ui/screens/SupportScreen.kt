package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.IslamicCard
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldMedium
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun SupportScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    BackHandler {
        viewModel.navigateTo(Screen.HOME)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("support_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo(Screen.HOME) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع"
                    )
                }
                Text(
                    text = "عن التطبيق والدعم",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        item {
            // Main Support Card (Matches website's exact support banner)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(EmeraldDark, EmeraldMedium)
                        )
                    )
                    .border(1.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ادعمنا بدعوة، أو ساهم في استمرار التطبيق 🤲",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "هاد التطبيق صاوبناه خالص لوجه الله، بلا إعلانات وبلا مقابل. إلا حبيت تساهم فمصاريف الاستضافة أو تطوير التطبيق، تقدر تتواصل معايا مباشرة. وإلا ما قدرتيش، دعوة صالحة كافية وزيادة 🤍",
                        fontSize = 14.5.sp,
                        lineHeight = 24.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    // Contact Action Buttons
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // WhatsApp
                        Button(
                            onClick = {
                                openUrl(context, "https://wa.me/212716473082")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF25D366),
                                contentColor = Color(0xFF06381F)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("whatsapp_button")
                        ) {
                            Text(
                                text = "💬 تواصل عبر واتساب (+212716473082)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        // Email
                        Button(
                            onClick = {
                                openUrl(context, "mailto:mokhliselkhomci@gmail.com")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.15f),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .border(1.dp, GoldAccent, RoundedCornerShape(12.dp))
                                .testTag("email_button")
                        ) {
                            Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = GoldAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "✉️ مراسلة بالإيميل (mokhliselkhomci@gmail.com)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        // Instagram
                        Button(
                            onClick = {
                                openUrl(context, "https://instagram.com/hadee_alquloob")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.15f),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .border(1.dp, GoldAccent, RoundedCornerShape(12.dp))
                                .testTag("instagram_button")
                        ) {
                            Text(
                                text = "📷 إنستغرام (@hadee_alquloob)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            // App Information & Vision Card
            IslamicCard {
                Text(
                    text = "مميزات تطبيق نور القرآن",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(10.dp))

                val features = listOf(
                    "📖 القرآن الكريم كاملاً بالرسم العثماني الموثوق مع تلاوة نخبة من مشاهير القراء.",
                    "📘 التفسير الميسر لكل آية بضغطة زر.",
                    "📿 أحاديث نبوية صحيحة من صحيح البخاري وصحيح مسلم والأربعين النووية.",
                    "🕌 أذكار الصباح والمساء وبعد الصلاة مع عداد ذكي واهتزاز تفاعلي.",
                    "🕋 مواقيت الصلاة الدقيقة حسب الإحداثيات ومدن المغرب والعالم الإسلامي.",
                    "⭐ حفظ الآيات المفضلة في الجهاز دون الحاجة لإنترنت أو تسجيل دخول.",
                    "🚫 تطبيق مجاني بالكامل وبدون أي إعلانات لوجه الله تعالى."
                )

                features.forEach { feat ->
                    Text(
                        text = feat,
                        fontSize = 13.5.sp,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "تطبيق وموقع نور القرآن — القرآن الكريم، الأحاديث النبوية، الأذكار ومواقيت الصلاة:\nhttps://mokhlis-alquran.netlify.app/"
                            )
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "مشاركة تطبيق نور القرآن"))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("انشر التطبيق ولك الأجر والدال على الخير كفاعله 🤍")
                }
            }
        }
    }
}

private fun openUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (_: Exception) {
    }
}
