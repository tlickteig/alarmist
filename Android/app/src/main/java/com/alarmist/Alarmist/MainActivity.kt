package com.alarmist.Alarmist

import com.alarmist.Alarmist.classes.CustomColors
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { // In here, we can call composables!
            MaterialTheme {
                Row {
                    Column {
                        Greeting(name = "World")
                        CurrentTime()
                        GetAlarmName()
                        GetAlarmTime()
                    }
                }
            }
        }
    }
}


@Composable
fun Greeting(name: String) {
    Column {
        Text(text = "Hello $name!", color = CustomColors.TextColor)
    }
}

@Preview
@Composable
fun CurrentTime() {
        Text(text = "When you opened the app it was "+ Date().toString(), color = CustomColors.TextColor)
}

@Composable
fun GetAlarmName() {
    Text(text = "Undefined Name.", color = CustomColors.TextColor)
}

@Composable
fun GetAlarmTime() {
    Text(text = Date().time.toString(), color = CustomColors.TextColor)
}