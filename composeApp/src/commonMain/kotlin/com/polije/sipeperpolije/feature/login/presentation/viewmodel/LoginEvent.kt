package com.polije.sipeperpolije.feature.login.presentation.viewmodel

sealed class LoginEvent{
    object LoginSuccess : LoginEvent()
    data class  LoginFailed(val message: String) : LoginEvent()
}