package com.maikeruwu.substats.service

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.core.content.edit
import com.maikeruwu.substats.SubStatsApplication
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object SecureStorage {

    enum class Key(val value: String) {
        API_KEY("api_key"),
        BASE_URL("base_url")
    }

    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "SubStatsKey"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val SECURE_PREFERENCES = "SubStatsSecurePrefs"
    private const val IV_SUFFIX = "_iv"

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        return keyStore.getKey(KEY_ALIAS, null) as? SecretKey ?: createSecretKey()
    }

    private fun createSecretKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance("AES", ANDROID_KEYSTORE)
        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        return keyGenerator.generateKey()
    }

    private fun encrypt(data: String): Pair<String, String> {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        var iv = cipher.iv
        val encrypted = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encrypted, Base64.DEFAULT) to Base64.encodeToString(
            iv,
            Base64.DEFAULT
        )
    }

    private fun decrypt(encryptedData: String, key: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val iv = SubStatsApplication.appContext.getSharedPreferences(
            SECURE_PREFERENCES,
            Context.MODE_PRIVATE
        ).getString(key, null)?.let { Base64.decode(it, Base64.DEFAULT) }
            ?: throw IllegalStateException("IV not found for key: $key")
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)
        val decoded = Base64.decode(encryptedData, Base64.DEFAULT)
        return String(cipher.doFinal(decoded), Charsets.UTF_8)
    }

    fun get(key: Key): String? {
        val sharedPreferences =
            SubStatsApplication.appContext.getSharedPreferences(
                SECURE_PREFERENCES,
                Context.MODE_PRIVATE
            )
        val encryptedData = sharedPreferences.getString(key.value, null)
        return if (encryptedData == null) null else try {
            decrypt(encryptedData, key.value + IV_SUFFIX)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            null
        }
    }

    fun set(key: Key, value: String) {
        SubStatsApplication.appContext.getSharedPreferences(
            SECURE_PREFERENCES,
            Context.MODE_PRIVATE
        ).edit {
            val (encryptedData, iv) = encrypt(value)
            putString(key.value, encryptedData)
            putString(key.value + IV_SUFFIX, iv)
        }
    }
}