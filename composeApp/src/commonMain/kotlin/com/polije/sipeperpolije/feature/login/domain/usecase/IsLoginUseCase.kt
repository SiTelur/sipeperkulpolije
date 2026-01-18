package com.polije.sipeperpolije.feature.login.domain.usecase

import com.polije.sipeperpolije.feature.login.domain.repository.LoginRepository

class IsLoginUseCase(private val loginRepository: LoginRepository) {
    operator fun invoke() = loginRepository.isLogin()
}