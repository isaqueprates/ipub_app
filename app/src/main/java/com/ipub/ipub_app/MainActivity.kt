package com.ipub.ipub_app

import MembersScreen
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
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
import com.ipub.ipub_app.ui.theme.Ipub_appTheme
import com.ipub.ipub_app.viewmodel.MemberViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Verifica login
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContent {
            Ipub_appTheme {
                IpubApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IpubApp() {

    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    val context = LocalContext.current
    val activity = context as Activity
    val auth = FirebaseAuth.getInstance()

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { dest ->
                item(
                    icon = { Icon(dest.icon, contentDescription = dest.label) },
                    label = { Text(dest.label) },
                    selected = dest == currentDestination,
                    onClick = { currentDestination = dest }
                )
            }
        }
    ) {

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text(currentDestination.label) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    actions = {
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
                )
            }
        ) { innerPadding ->

            when (currentDestination) {

                AppDestinations.HOME -> HomeScreen()

                AppDestinations.FAVORITES -> {
                    val viewModel: MemberViewModel = viewModel()
                    val members by viewModel.members.collectAsState()

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

                AppDestinations.PROFILE -> {
                    val viewModel: MemberViewModel = viewModel()

                    DepartmentMembersScreen(
                        viewModel = viewModel,
                        onBack = { /* não precisa voltar; está dentro das abas */ }
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
