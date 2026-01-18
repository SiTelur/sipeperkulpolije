package com.polije.sipeperpolije.feature.login.domain.repository

import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    suspend fun login(username : String , password : String)
    fun isLogin() : Flow<Boolean>

}