package com.polije.sipeperpolije.feature.login.presentation.viewmodel

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polije.sipeperpolije.feature.login.domain.usecase.LoginUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val loginUseCase: LoginUseCase) : ViewModel(){

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _eventChannel = Channel<LoginEvent>()
    val events = _eventChannel.receiveAsFlow()

    val usernameState = TextFieldState()
    val passwordState = TextFieldState()

    fun onAction(action: LoginAction){
        when(action){
            is LoginAction.OnLoginPressed -> {
                viewModelScope.launch {
                    runCatching {
                        _state.value = _state.value.copy(isLoading = true)
                        loginUseCase.invoke(usernameState.text.toString(), passwordState.text.toString())

                    }.onSuccess {
                        _state.value = _state.value.copy(isLoading = false)
                        _eventChannel.send(LoginEvent.LoginSuccess)
                    }.onFailure {
                        _state.value = _state.value.copy(isLoading = false)
                        _eventChannel.send(LoginEvent.LoginFailed(it.message ?: "Unknown error"))
                    }
                }
            }
        }
    }
}

sealed class LoginAction{
    object OnLoginPressed : LoginAction()
}

sealed class LoginEvent{
    object LoginSuccess : LoginEvent()
    data class  LoginFailed(val message: String) : LoginEvent()
}

data class LoginState(var isLoading: Boolean = false)


