package com.polije.sipeperpolije.feature.login.domain.usecase

import com.polije.sipeperpolije.feature.login.domain.repository.LoginRepository

class LoginUseCase(private val loginRepository: LoginRepository) {
    suspend operator fun invoke(username: String, password: String) = loginRepository.login(username, password)
}


