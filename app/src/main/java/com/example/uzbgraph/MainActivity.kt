package com.example.uzbgraph

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.vanyislands.tdlib.TdClient
import com.github.vanyislands.tdlib.TdApi

class MainActivity : ComponentActivity() {
    
    // ⚠️ 1-QADAMDA OLGAN KODLARINGIZNI SHU YERGA QO'YING:
    private val API_ID = 10489159 // O'zingizning api_id raqamingizni yozing
    private val API_HASH = "6b2c509f05b3c529eddb9326a813bf0f" // O'zingizning api_hash'ingizni yozing

    private lateinit var tdClient: TdClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // TDLib Telegram mijozini noldan yaratish
        tdClient = TdClient.create()

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    UzbgraphAuthScreen()
                }
            }
        }
    }

    @Composable
    fun UzbgraphAuthScreen() {
        var phoneNumber by remember { mutableStateOf("") }
        var authCode by remember { mutableStateOf("") }
        var isCodeSent by remember { mutableStateOf(false) }
        var statusMessage by remember { mutableStateOf("Uzbgraph messenjeriga xush kelibsiz!") }

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Uzbgraph", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = statusMessage, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(32.dp))

            if (!isCodeSent) {
                // Telefon raqam kiritish oynasi
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Telefon raqam (+998...)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (phoneNumber.isNotEmpty()) {
                            statusMessage = "Telegramga so'rov yuborilmoqda..."
                            // TDLib-ni sozlash va raqamni yuborish
                            sendTelegramPhone(phoneNumber) { success ->
                                if (success) {
                                    isCodeSent = true
                                    statusMessage = "Tasdiqlash kodi Telegram'ingizga yuborildi!"
                                } else {
                                    statusMessage = "Xatolik yuz berdi. Kalitlarni tekshiring."
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Kod olish")
                }
            } else {
                // Kelgan kodni kiritish oynasi
                OutlinedTextField(
                    value = authCode,
                    onValueChange = { authCode = it },
                    label = { Text("Tasdiqlash kodi") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (authCode.isNotEmpty()) {
                            checkTelegramCode(authCode) { loggedIn ->
                                statusMessage = if (loggedIn) {
                                    "Muvaffaqiyatli kirdingiz! Yaqinlaringizga xabar yuborishingiz mumkin."
                                } else {
                                    "Kod xato yoki eskirgan."
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Kirishni tasdiqlash")
                }
            }
        }
    }

    // Telegram bazasiga telefon jo'natish logikasi
    private fun sendTelegramPhone(phone: String, onResult: (Boolean) -> Unit) {
        tdClient.send(TdApi.SetTdlibParameters(
            false, "database", "files", "1.0", "Uzbgraph", "Desktop", "uz",
            TdApi.TdlibParameters()
        )) {
            tdClient.send(TdApi.SetAuthenticationPhoneNumber(phone, null)) { result ->
                onResult(result.constructor == TdApi.Ok.CONSTRUCTOR)
            }
        }
    }

    // Kodni tekshirish logikasi
    private fun checkTelegramCode(code: String, onResult: (Boolean) -> Unit) {
        tdClient.send(TdApi.CheckAuthenticationCode(code)) { result ->
            onResult(result.constructor == TdApi.Ok.CONSTRUCTOR)
        }
    }
}
