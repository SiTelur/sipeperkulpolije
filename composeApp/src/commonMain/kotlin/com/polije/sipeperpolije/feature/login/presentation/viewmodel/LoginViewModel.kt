package com.polije.sipeperpolije.feature.login.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.polije.sipeperpolije.feature.login.domain.usecase.LoginUseCase

class LoginViewModel(private val loginUseCase: LoginUseCase) : ViewModel(){

}

sealed class LoginAction{
    data class OnLoginPressed(val username: String, val password: String) : LoginAction()
}


