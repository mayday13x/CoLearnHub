package com.example.colearnhub.ui.screen.signup

import BackButton
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.colearnhub.R
import com.example.colearnhub.data.SignupData
import com.example.colearnhub.repository.AuthRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupStep1Screen(
    navController: NavController,
    signupData: SignupData
) {
    // Estados dos campos
    var name by remember { mutableStateOf(signupData.name) }
    var email by remember { mutableStateOf(signupData.email) }

    val birthParts = signupData.birthDate.split("-")
    var year by remember { mutableStateOf(birthParts.getOrNull(0)?.toIntOrNull() ?: 0) }
    var month by remember { mutableStateOf(birthParts.getOrNull(1)?.toIntOrNull() ?: 0) }
    var day by remember { mutableStateOf(birthParts.getOrNull(2)?.toIntOrNull() ?: 0) }

    var country by remember { mutableIntStateOf(signupData.country) }

    // Estados para dropdowns
    var expandedDay by remember { mutableStateOf(false) }
    var expandedMonth by remember { mutableStateOf(false) }
    var expandedYear by remember { mutableStateOf(false) }
    var expandedCountry by remember { mutableStateOf(false) }

    // Estados de validação e loading
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var dateError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val authRepository = remember { AuthRepository() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Dados para dropdowns
    val days = (1..31).toList()
    val months = listOf(
        stringResource(R.string.january),
        stringResource(R.string.february),
        stringResource(R.string.march),
        stringResource(R.string.april),
        stringResource(R.string.may),
        stringResource(R.string.june),
        stringResource(R.string.july),
        stringResource(R.string.august),
        stringResource(R.string.september),
        stringResource(R.string.october),
        stringResource(R.string.november),
        stringResource(R.string.december)
    )
    val years = (1950..2010).toList().reversed() // Ajustado para idades mais realistas

    val countries = listOf(
        "🇵🇹 ${stringResource(R.string.portugal)}" to 1,
        "🇺🇸 ${stringResource(R.string.unitedstates)}" to 2
    )

    fun validateInputs(): Boolean {
        var hasError = false

        // Validar nome
        when {
            name.isBlank() -> {
                nameError = "Nome é obrigatório"
                hasError = true
            }
            name.length < 2 -> {
                nameError = "Nome deve ter pelo menos 2 caracteres"
                hasError = true
            }
            else -> nameError = null
        }

        // Validar email
        when {
            email.isBlank() -> {
                emailError = "Email é obrigatório"
                hasError = true
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                emailError = "Email inválido"
                hasError = true
            }
            else -> emailError = null
        }

        // Validar data de nascimento
        when {
            day == 0 || month == 0 || year == 0 -> {
                dateError = "Data de nascimento é obrigatória"
                hasError = true
            }
            else -> {
                try {
                    val birthDate = LocalDate.of(year, month, day)
                    val now = LocalDate.now()
                    val age = now.year - birthDate.year -
                            if (now.dayOfYear < birthDate.dayOfYear) 1 else 0

                    when {
                        age < 13 -> {
                            dateError = "Deve ter pelo menos 13 anos"
                            hasError = true
                        }
                        age > 100 -> {
                            dateError = "Data inválida"
                            hasError = true
                        }
                        else -> dateError = null
                    }
                } catch (e: Exception) {
                    dateError = "Data inválida"
                    hasError = true
                }
            }
        }

        return !hasError
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header com botão voltar e título
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(
                    onClick = { navController.popBackStack() }
                )

                Text(
                    text = stringResource(R.string.sign_up),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 40.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Campo Nome
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it.trim()
                        nameError = null
                    },
                    label = { Text(stringResource(R.string.name), fontSize = 14.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    singleLine = true,
                    isError = nameError != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF395174),
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                        errorBorderColor = Color.Red
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
                )

                nameError?.let { error ->
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Campo Email
            Column {
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it.trim().lowercase()
                        emailError = null
                    },
                    label = { Text(stringResource(R.string.email), fontSize = 14.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    singleLine = true,
                    isError = emailError != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF395174),
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                        errorBorderColor = Color.Red
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
                )

                emailError?.let { error ->
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Data de Nascimento
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.birthdate),
                    modifier = Modifier.padding(bottom = 4.dp),
                    color = Color.Black,
                    fontSize = 14.sp
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Dia
                    Box(modifier = Modifier.weight(0.8f)) {
                        ExposedDropdownMenuBox(
                            expanded = expandedDay,
                            onExpandedChange = { expandedDay = !expandedDay }
                        ) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                readOnly = true,
                                value = if (day == 0) stringResource(R.string.day) else day.toString(),
                                onValueChange = {},
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDay)
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF395174),
                                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                                ),
                                textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                            )
                            ExposedDropdownMenu(
                                expanded = expandedDay,
                                onDismissRequest = { expandedDay = false }
                            ) {
                                days.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(text = item.toString(), fontSize = 12.sp) },
                                        onClick = {
                                            day = item
                                            expandedDay = false
                                            dateError = null
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Mês
                    Box(modifier = Modifier.weight(1.2f)) {
                        ExposedDropdownMenuBox(
                            expanded = expandedMonth,
                            onExpandedChange = { expandedMonth = !expandedMonth }
                        ) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                readOnly = true,
                                value = if (month == 0) stringResource(R.string.month) else months[month - 1],
                                onValueChange = {},
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMonth)
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF395174),
                                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                                ),
                                textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                            )
                            ExposedDropdownMenu(
                                expanded = expandedMonth,
                                onDismissRequest = { expandedMonth = false }
                            ) {
                                months.forEachIndexed { index, item ->
                                    DropdownMenuItem(
                                        text = { Text(text = item, fontSize = 12.sp) },
                                        onClick = {
                                            month = index + 1
                                            expandedMonth = false
                                            dateError = null
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Ano
                    Box(modifier = Modifier.weight(1f)) {
                        ExposedDropdownMenuBox(
                            expanded = expandedYear,
                            onExpandedChange = { expandedYear = !expandedYear }
                        ) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                readOnly = true,
                                value = if (year == 0) stringResource(R.string.year) else year.toString(),
                                onValueChange = {},
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedYear)
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF395174),
                                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                                ),
                                textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                            )
                            ExposedDropdownMenu(
                                expanded = expandedYear,
                                onDismissRequest = { expandedYear = false }
                            ) {
                                years.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(text = item.toString(), fontSize = 12.sp) },
                                        onClick = {
                                            year = item
                                            expandedYear = false
                                            dateError = null
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                dateError?.let { error ->
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Campo País
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.country),
                    color = Color.Black,
                    fontSize = 14.sp
                )

                ExposedDropdownMenuBox(
                    expanded = expandedCountry,
                    onExpandedChange = { expandedCountry = !expandedCountry },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        readOnly = true,
                        value = countries.find { it.second == country }?.first ?: countries[0].first,
                        onValueChange = {},
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCountry)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF395174),
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCountry,
                        onDismissRequest = { expandedCountry = false }
                    ) {
                        countries.forEach { (countryName, countryId) ->
                            DropdownMenuItem(
                                text = { Text(text = countryName, fontSize = 14.sp) },
                                onClick = {
                                    country = countryId
                                    expandedCountry = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botão Próximo
            Button(
                onClick = {
                    if (validateInputs()) {
                        isLoading = true
                        scope.launch {
                            try {
                                // Verificar se email já existe
                                val emailExists = authRepository.checkEmailExists(email)
                                if (emailExists) {
                                    emailError = "Este email já está registado"
                                } else {
                                    // Salvar dados no objeto compartilhado
                                    signupData.apply {
                                        this.name = name
                                        this.email = email
                                        this.birthDate = String.format("%04d-%02d-%02d", year, month, day)
                                        this.country = country
                                    }

                                    // Navegar para o próximo step
                                    navController.navigate("signup_step2")
                                }
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar(
                                    message = "Erro ao verificar email: ${e.message}",
                                    duration = SnackbarDuration.Long
                                )
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF395174),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.next),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}