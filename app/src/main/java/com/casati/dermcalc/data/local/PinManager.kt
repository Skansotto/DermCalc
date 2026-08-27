package com.casati.dermcalc.data.local

import android.content.Context
import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom

// Il PIN non viene mai salvato in chiaro: si conserva solo un hash (SHA-256) con salt casuale.
class PinManager(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NOME, Context.MODE_PRIVATE)

    fun isPinConfigurato(): Boolean = prefs.contains(CHIAVE_HASH)

    fun impostaPin(pin: String) {
        val salt = generaSalt()
        val hash = calcolaHash(pin, salt)
        prefs.edit()
            .putString(CHIAVE_SALT, salt)
            .putString(CHIAVE_HASH, hash)
            .apply()
    }

    fun verificaPin(pin: String): Boolean {
        val salt = prefs.getString(CHIAVE_SALT, null) ?: return false
        val hashSalvato = prefs.getString(CHIAVE_HASH, null) ?: return false
        return calcolaHash(pin, salt) == hashSalvato
    }

    private fun generaSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    private fun calcolaHash(pin: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(Base64.decode(salt, Base64.NO_WRAP))
        val hashBytes = digest.digest(pin.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(hashBytes, Base64.NO_WRAP)
    }

    private companion object {
        const val PREFS_NOME = "dermcalc_auth"
        const val CHIAVE_SALT = "pin_salt"
        const val CHIAVE_HASH = "pin_hash"
    }
}
