package com.it2161.dit99999x.PopCornMovie.ui.components

import android.content.Context
import android.content.SharedPreferences
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.util.*

@Composable
fun RegistrationScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var yearOfBirth by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var receiveUpdates by remember { mutableStateOf(false) }

    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val maxYear = currentYear - 90

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Username field
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Enter username") }
        )

        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Enter password") },
            visualTransformation = PasswordVisualTransformation()
        )

        // Confirm Password field
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm password") },
            visualTransformation = PasswordVisualTransformation()
        )

        // Email field with validation
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Enter email") },
            isError = !Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.isNotEmpty()
        )

        // Gender selection
        Text("Gender")
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                listOf("Male", "Female").forEach { genderOption ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = gender == genderOption,
                            onClick = { gender = genderOption }
                        )
                        Text(genderOption)
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                listOf("Non-Binary", "Prefer not to say").forEach { genderOption ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = gender == genderOption,
                            onClick = { gender = genderOption }
                        )
                        Text(genderOption)
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                }
            }
        }

        // Mobile Number field with validation
        OutlinedTextField(
            value = mobileNumber,
            onValueChange = { mobileNumber = it.filter { it.isDigit() } },
            label = { Text("Enter mobile number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = mobileNumber.length != 10 && mobileNumber.isNotEmpty()  // Assuming a 10-digit format
        )

        // Receive updates checkbox
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = receiveUpdates,
                onCheckedChange = { receiveUpdates = it }
            )
            Text("Receive updates via email")
        }

        // Year of Birth input with validation
        OutlinedTextField(
            value = yearOfBirth,
            onValueChange = { input ->
                if (input.all { it.isDigit() }) {
                    yearOfBirth = input
                }
            },
            label = { Text("Enter Year of Birth") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = yearOfBirth.toIntOrNull()?.let { it < maxYear || it > currentYear } == true && yearOfBirth.isNotEmpty()
        )

        // Register and Cancel Buttons
        Button(
            onClick = {
                // Validation logic
                if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
                    email.isEmpty() || gender.isEmpty() || mobileNumber.isEmpty() || yearOfBirth.isEmpty()
                ) {
                    Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(context, "Invalid email format", Toast.LENGTH_SHORT).show()
                } else if (password != confirmPassword) {
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                } else if (yearOfBirth.toInt() < maxYear || yearOfBirth.toInt() > currentYear) {
                    Toast.makeText(context, "Year of birth must be within the valid range", Toast.LENGTH_SHORT).show()
                } else {
                    // Save user data in SharedPreferences
                    sharedPreferences.edit().apply {
                        putString("username", username)
                        putString("password", password)
                        putString("email", email)
                        putString("gender", gender)
                        putString("mobileNumber", mobileNumber)
                        putString("yearOfBirth", yearOfBirth)
                        putBoolean("receiveUpdates", receiveUpdates)
                        apply()
                    }
                    Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
                    navController.navigate("LoginScreen")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                // Navigate back to login screen
                navController.navigate("LoginScreen")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegistrationScreenPreview() {
    RegistrationScreen(navController = rememberNavController())
}
