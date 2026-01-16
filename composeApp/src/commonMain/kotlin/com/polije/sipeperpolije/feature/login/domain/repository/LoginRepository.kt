package com.polije.sipeperpolije.feature.login.domain.repository

interface LoginRepository {
    suspend fun login(username : String , password : String)
}