package dev.alnoer.audiosaas.feature.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import dev.alnoer.audiosaas.core.designsystem.component.AudioAuthCard
import dev.alnoer.audiosaas.core.designsystem.component.AudioAuthSwitch
import dev.alnoer.audiosaas.core.designsystem.component.AudioButton
import dev.alnoer.audiosaas.core.designsystem.component.AudioCheckbox
import dev.alnoer.audiosaas.core.designsystem.component.AudioTextField
import dev.alnoer.audiosaas.core.designsystem.component.AudioTopBar
import dev.alnoer.audiosaas.core.designsystem.theme.AudioSaaSTheme
import dev.alnoer.audiosaas.core.designsystem.theme.audioColors

@Composable
fun SignInScreen(
    onSignIn: () -> Unit,
    onCreateAccount: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        AudioTopBar()
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            AudioAuthCard(
                title = "Вход",
                description = "Войдите, чтобы продолжить работу.",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(384.dp),
            ) {
                AudioTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "name@company.com",
                    label = "EMAIL",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                )
                AudioTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Введите пароль",
                    label = "ПАРОЛЬ",
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Забыли пароль?",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
                AudioButton(
                    text = "Войти",
                    onClick = onSignIn,
                    modifier = Modifier.fillMaxWidth(),
                )
                AudioAuthSwitch(
                    prompt = "Нет аккаунта?",
                    action = "Создать аккаунт",
                    onAction = onCreateAccount,
                )
            }
        }
    }
}

@Composable
fun SignUpScreen(
    onCreateAccount: () -> Unit,
    onSignIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var acceptedTerms by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        AudioTopBar()
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            AudioAuthCard(
                title = "Создать аккаунт",
                contentSpacing = 14.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(464.dp),
            ) {
                AudioTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Ваше имя",
                    label = "ИМЯ",
                    modifier = Modifier.fillMaxWidth(),
                )
                AudioTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "name@company.com",
                    label = "EMAIL",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                )
                AudioTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Не менее 8 символов",
                    label = "ПАРОЛЬ",
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    AudioCheckbox(
                        checked = acceptedTerms,
                        onCheckedChange = { acceptedTerms = it },
                    )
                    Text(
                        text = "Согласен с условиями использования и политикой данных",
                        color = MaterialTheme.audioColors.mutedForeground,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.clickable(
                            role = Role.Checkbox,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { acceptedTerms = !acceptedTerms },
                    )
                }
                AudioButton(
                    text = "Создать аккаунт",
                    onClick = onCreateAccount,
                    modifier = Modifier.fillMaxWidth(),
                )
                AudioAuthSwitch(
                    prompt = "Уже есть аккаунт?",
                    action = "Войти",
                    onAction = onSignIn,
                )
            }
        }
    }
}

@PreviewScreenSizes
@Composable
private fun SignInScreenPreview() {
    AudioSaaSTheme {
        Surface {
            SignInScreen(
                onSignIn = {},
                onCreateAccount = {},
            )
        }
    }
}

@PreviewScreenSizes
@Composable
private fun SignUpScreenPreview() {
    AudioSaaSTheme {
        Surface {
            SignUpScreen(
                onCreateAccount = {},
                onSignIn = {},
            )
        }
    }
}

