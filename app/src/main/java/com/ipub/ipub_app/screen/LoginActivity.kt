package com.ipub.ipub_app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ipub.ipub_app.screen.AdminActivity
import com.ipub.ipub_app.ui.theme.Ipub_appTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.BuildConfig
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.text.style.TextDecoration

class LoginActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        // 🔹 Se já estiver logado, pula o login
        auth.currentUser?.let {
            checkUserRoleAndNavigate(it.uid)
            return
        }

        setContent {
            Ipub_appTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    LoginScreen(
                        onLogin = { email, password ->
                            if (email.isBlank() || password.isBlank()) {
                                Toast.makeText(this, "Preencha e-mail e senha", Toast.LENGTH_SHORT).show()
                                return@LoginScreen
                            }

                            auth.signInWithEmailAndPassword(email.trim(), password.trim())
                                .addOnSuccessListener { result ->
                                    val uid = result.user?.uid
                                    if (uid != null) checkUserRoleAndNavigate(uid)
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Erro no login: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                        }
                    )
                }
            }
        }
    }

    private fun checkUserRoleAndNavigate(uid: String) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val role = doc.getString("role") ?: "member"
                if (role == "admin") {
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                finish()
            }
            .addOnFailureListener {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
    }
}

@Composable
fun LoginScreen(onLogin: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showLogo by remember { mutableStateOf(false) }
    var tapCount by remember { mutableStateOf(0) }
    var lastTapTime by remember { mutableStateOf(0L) }
    var showPinDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }

    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current // 🔹 Obtém o handler para abrir URLs
    val privacyPolicyUrl = "https://drive.google.com/file/d/1sVszYXIi2hWtuVbI0fQlf4mYN4JYRqKb/view?usp=sharing" // 🔹 SEU LINK AQUI

    LaunchedEffect(Unit) { showLogo = true }

    // 🔐 PIN secreto do admin (você pode mudar aqui)
    val adminPin = "4321"

    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = { Text("Modo administrador") },
            text = {
                OutlinedTextField(
                    value = pinInput,
                    onValueChange = { pinInput = it },
                    label = { Text("Digite o PIN secreto") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (pinInput == adminPin) {
                        showPinDialog = false
                        Toast.makeText(context, "🔓 Modo administrador desbloqueado!", Toast.LENGTH_SHORT).show()
                        context.startActivity(Intent(context, AdminActivity::class.java))
                    } else {
                        Toast.makeText(context, "PIN incorreto!", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            AnimatedVisibility(
                visible = showLogo,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_ipub), // Use o nome do seu arquivo aqui
                        contentDescription = "Logo iPub",
                        contentScale = ContentScale.Fit, // Ajusta a imagem dentro do tamanho especificado
                        modifier = Modifier
                            .size(120.dp) // Aumentei o tamanho para 120.dp para uma imagem real
                            .padding(8.dp)
                            .clickable {
                                // ... (lógica de clique quíntuplo para o admin)
                                val now = System.currentTimeMillis()
                                if (now - lastTapTime < 1500) {
                                    tapCount++
                                } else {
                                    tapCount = 1
                                }
                                lastTapTime = now

                                if (tapCount >= 5) {
                                    tapCount = 0
                                    showPinDialog = true // 🔒 pede o PIN
                                }
                            }
                    )

                    Text(
                        text = "iPub App",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (BuildConfig.DEBUG) {
                        Text(
                            text = "Versão 1.0.0 — toque 5x no logo",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(20.dp))
                }
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-mail") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Senha") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { onLogin(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Entrar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = "Política de Privacidade",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    // Abre o link no navegador do dispositivo
                    uriHandler.openUri(privacyPolicyUrl)
                }
            )
        }
    }
}
