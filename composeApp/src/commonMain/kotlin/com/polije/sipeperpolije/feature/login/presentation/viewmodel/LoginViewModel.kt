package com.polije.sipeperpolije.feature.login.presentation.viewmodel

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polije.sipeperpolije.feature.login.domain.usecase.IsLoginUseCase
import com.polije.sipeperpolije.feature.login.domain.usecase.LoginUseCase
import io.github.jan.supabase.auth.exception.AuthRestException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val loginUseCase: LoginUseCase, isLoginUseCase: IsLoginUseCase) :
    ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _eventChannel = Channel<LoginEvent>()
    val events = _eventChannel.receiveAsFlow()

    val usernameState = TextFieldState()
    val passwordState = TextFieldState()

    init {
        viewModelScope.launch {
            isLoginUseCase().collectLatest {
                if (it) {
                    _eventChannel.send(LoginEvent.LoginSuccess)
                }
            }
        }
    }


    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.OnLoginPressed -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(isLoading = true)
                    runCatching {
                        loginUseCase(usernameState.text.toString(), passwordState.text.toString())
                    }.onSuccess {
                        _state.value = _state.value.copy(isLoading = false)
                        _eventChannel.send(LoginEvent.LoginSuccess)
                    }.onFailure { throwable ->
                        _state.value = _state.value.copy(isLoading = false)
                        val message = when (throwable) {
                            is AuthRestException -> "Username atau password anda salah"
                            else -> "Error tidak diketahui"
                        }
                        _eventChannel.send(LoginEvent.LoginFailed(message))
                    }

                }
            }
        }
    }
}


data class LoginState(var isLoading: Boolean = false)


