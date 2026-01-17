package com.polije.sipeperpolije.feature.login.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polije.sipeperpolije.feature.login.domain.usecase.LoginUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val loginUseCase: LoginUseCase) : ViewModel(){


    private val _eventChannel = Channel<LoginEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onAction(action: LoginAction){
        when(action){
            is LoginAction.OnLoginPressed -> {
                viewModelScope.launch {
                    delay(500)
                    _eventChannel.send(LoginEvent.LoginSuccess)
                }
            }
        }
    }
}

sealed class LoginAction{
    data class OnLoginPressed(val username: String, val password: String) : LoginAction()
}

sealed class LoginEvent{
    object LoginSuccess : LoginEvent()
    data class  LoginFailed(val message: String) : LoginEvent()
}


