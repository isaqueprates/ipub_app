package com.ipub.ipub_app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.time.LocalDate
import com.ipub.ipub_app.notification.BirthdayScheduler
import com.ipub.ipub_app.ui.theme.Ipub_appTheme
import com.ipub.ipub_app.ui.theme.AppScaffold
import com.ipub.ipub_app.viewmodel.MemberViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    private var navigateToState = mutableStateOf<String?>(null)
    private var targetDateState = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        BirthdayScheduler.scheduleDailyCheck(this)
        requestNotificationPermission()

        // Captura a Intent ao abrir do zero
        handleIntent(intent)

        setContent {
            Ipub_appTheme {
                IpubApp(
                    navigateToNotification = navigateToState.value,
                    targetDate = targetDateState.value
                )
            }
        }
    }

    // Trata o clique se o app já estiver aberto em segundo plano
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        navigateToState.value = intent?.getStringExtra("navigate_to")
        targetDateState.value = intent?.getStringExtra("target_date")
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }
}

@Composable
fun IpubApp(navigateToNotification: String? = null, targetDate: String? = null) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    val homeNavController = rememberNavController()

    // Efeito colateral acionado sempre que a notificação for recebida
    LaunchedEffect(navigateToNotification, targetDate) {
        if (navigateToNotification == "birthday_detail") {
            currentDestination = AppDestinations.HOME
            val dateToUse = targetDate ?: LocalDate.now().toString()

            // Pequeno delay para garantir que a UI do NavHost foi montada
            kotlinx.coroutines.delay(150)

            homeNavController.navigate("birthday_detail/$dateToUse") {
                popUpTo("home_calendar") {
                    saveState = true
                }
                launchSingleTop = true
            }
        }
    }

    val context = LocalContext.current
    val activity = context as Activity
    val auth = FirebaseAuth.getInstance()

    // Função auxiliar para reutilizar a ação de Logout na TopBar
    val logoutAction: @Composable RowScope.() -> Unit = {
        IconButton(onClick = {
            auth.signOut()
            Toast.makeText(context, "Você saiu da conta.", Toast.LENGTH_SHORT).show()
            context.startActivity(Intent(context, LoginActivity::class.java))
            activity.finish()
        }) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "Logout",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { dest ->
                item(
                    icon = { Icon(dest.icon, contentDescription = dest.label) },
                    label = { Text(dest.label) },
                    selected = dest == currentDestination,
                    onClick = {
                        if (dest == AppDestinations.HOME) {
                        // Se clicar na casinha, muda para a aba Home (se já não estiver nela)
                        currentDestination = AppDestinations.HOME
                        // E força o NavController interno a voltar para a raiz do calendário
                        try {
                            homeNavController.popBackStack("home_calendar", inclusive = false)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        currentDestination = dest
                    }
                    }
                )
            }
        }
    ) {
        when (currentDestination) {
            AppDestinations.HOME -> {
                NavHost(
                    navController = homeNavController,
                    startDestination = "home_calendar"
                ) {
                    composable("home_calendar") {
                        HomeScreen(
                            onNavigateToBirthdays = { selectedDate ->
                                homeNavController.navigate("birthday_detail/$selectedDate")
                            }
                        )
                    }
                    composable("birthday_detail/{selectedDate}") { backStackEntry ->
                        val dateString = backStackEntry.arguments?.getString("selectedDate")
                        val date = dateString?.let { LocalDate.parse(it) } ?: LocalDate.now()
                        val viewModel: MemberViewModel = viewModel()

                        BirthdayDetailScreen(
                            selectedDate = date,
                            viewModel = viewModel,
                            onBackClick = {
                                homeNavController.popBackStack()
                            }
                        )
                    }
                }
            }

            AppDestinations.FAVORITES -> {
                val viewModel: MemberViewModel = viewModel()
                val members by viewModel.members.collectAsState()

                AppScaffold(
                    title = "Membros",
                    actions = logoutAction
                ) { innerPadding ->
                    MembersScreen(
                        members = members,
                        onAddClick = {
                            activity.startActivity(Intent(activity, AddMemberActivity::class.java))
                        },
                        onEdit = { member ->
                            val intent = Intent(activity, EditMemberActivity::class.java)
                            intent.putExtra("memberId", member.id)
                            activity.startActivity(intent)
                        },
                        onSearchClick = {
                            activity.startActivity(Intent(activity, SearchActivity::class.java))
                        },
                        onDelete = { member -> viewModel.delete(member.id) },
                        innerPadding = innerPadding
                    )
                }
            }

            AppDestinations.PROFILE -> {
                val viewModel: MemberViewModel = viewModel()

                AppScaffold(
                    title = "Departamentos",
                    actions = logoutAction
                ) { innerPadding ->
                    DepartmentMembersScreen(
                        viewModel = viewModel,
                        innerPadding = innerPadding,
                        onBack = { /* não precisa voltar */ }
                    )
                }
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector
) {
    HOME("Home", Icons.Default.Home),
    FAVORITES("Membros", Icons.Default.Group),
    PROFILE("Departamentos", Icons.Default.AdminPanelSettings),
}