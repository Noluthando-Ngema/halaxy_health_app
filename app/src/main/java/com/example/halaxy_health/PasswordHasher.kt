package com.example.halaxy_health

import java.security.MessageDigest
import java.security.SecureRandom
import android.util.Base64

object PasswordHasher {
    // generate salt + hash with SHA-256
    fun hashPassword(password: String): String {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        val saltString = Base64.encodeToString(salt, Base64.NO_WRAP)
        val hash = sha256(saltString + password)
        return "$saltString:$hash" // save as salt:hash
    }

    fun verifyPassword(password: String, stored: String): Boolean {
        val parts = stored.split(":")
        if (parts.size!= 2) return false
        val salt = parts[0]
        val hash = parts[1]
        val newHash = sha256(salt + password)
        return newHash == hash
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}