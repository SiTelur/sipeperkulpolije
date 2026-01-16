package com.polije.sipeperpolije.feature.login.domain

interface LoginRepository {
    suspend fun login(username : String , password : String)
}