package singhsarae.badshah.jetpackcomposepractice

import android.R
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import singhsarae.badshah.jetpackcomposepractice.ui.theme.JetpackComposePracticeTheme
import singhsarae.badshah.jetpackcomposepractice.customColorsForDayNightTheme.LocalCustomColorsPalette
import singhsarae.badshah.stripem2.creepySteam.StripeManager
import singhsarae.badshah.stripem2.customModels.stripe.reader.Reader
import singhsarae.badshah.stripem2.interfaces.StripeCallbacks

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainUI()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainUI(){
    JetpackComposePracticeTheme {
        Surface(color = colorResource(id = R.color.black)) {
                LoadUI()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadUI() {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current
    val activity = context as? Activity
    val contentPadding = WindowInsets
        .systemBars
        .add(WindowInsets(left = 16.dp, top = 0.dp, right = 16.dp, bottom = 16.dp))
        .asPaddingValues()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        item {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                OutlinedInput(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email"
                )
            }
        }
        item {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                OutlinedInput(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    isPassword = true
                )
            }
        }
        item {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
                horizontalAlignment = Alignment.End) {
                CommonButton(
                    text = "Login",
                    modifier = Modifier.width(120.dp),
                    cornerRadius = 8.dp,
                    onClick = {
                        // handle click
                        Log.i("BADSHAH","Email:$email")
                        Log.i("BADSHAH","Password:$password")
                        val job = SupervisorJob()
                        val mainScope = CoroutineScope(Dispatchers.Main + job)
                        val callbacks = object: StripeCallbacks {
                            override fun onSuccess(reader: Reader) {
                                mainScope.launch {
                                    Toast.makeText(context,"Stripe onSuccess: ${reader.readerName}",Toast.LENGTH_LONG).show()
                                }
                                Log.i("BADSHAH","Stripe onSuccess Called reader.readerName:${reader.readerName}")
                            }

                            override fun onError(error: String) {
                                mainScope.launch {
                                    Toast.makeText(
                                        context,
                                        "Stripe Error: $error",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                Log.i("BADSHAH","Stripe Error: $error")
                            }

                            override fun onFinishInstallingUpdate(
                                finished: Boolean?,
                                error: String?
                            ) {

                            }

                            override fun onStartInstallingUpdate(started: Boolean) {

                            }

                            override fun onReportReaderSoftwareUpdateProgress(progress: Float) {

                            }

                            override fun onReaderReconnectFailed(reader: Reader) {

                            }

                            override fun onReaderReconnectSucceeded(reader: Reader) {

                            }

                        }
                    }
                )
            }
        }
    }
}

@Composable
fun OutlinedInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val visualTransformation = if (isPassword) {
        if (passwordVisible) VisualTransformation.None
        else PasswordVisualTransformation()
    } else {
        VisualTransformation.None
    }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Email
        ),
        trailingIcon = {
            if (isPassword) {
                val icon = if (passwordVisible)
                    Icons.Default.Visibility
                else
                    Icons.Default.VisibilityOff

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = icon, contentDescription = "Toggle password visibility")
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colorResource(singhsarae.badshah.jetpackcomposepractice.R.color.colorGolden),
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = colorResource(singhsarae.badshah.jetpackcomposepractice.R.color.colorGolden),
            cursorColor = colorResource(singhsarae.badshah.jetpackcomposepractice.R.color.colorGolden),
            focusedTextColor = colorResource(R.color.white),
            unfocusedTextColor = colorResource(R.color.white),
        )
    )
}

@Composable
fun CommonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    backgroundColor: Color = Color(0xFFFFC107),
    textColor: Color = Color.Black
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = textColor
        )
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}



