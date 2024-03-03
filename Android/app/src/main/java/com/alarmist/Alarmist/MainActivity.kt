package com.alarmist.Alarmist

import com.alarmist.Alarmist.classes.CustomColors
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ModalNavigationDrawer(
                drawerContent = {
                    ModalDrawerSheet {
                        Text("Alarmist", modifier = Modifier.padding(16.dp))
                        /*Divider()*/
                        NavigationDrawerItem(
                            label = { Text("Hello World!") },
                            selected = false,
                            onClick = { /*TODO*/ })
                    }
                }
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text("This is the top bar")
                            }
                        )
                    }
                ) { innerPadding ->
                    Text("This is the main page")
                }
            }
        }
    }
}