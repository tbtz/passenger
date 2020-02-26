package com.example.project

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.widget.Toast
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

private var secretKey: SecretKeySpec? = null
private lateinit var key: ByteArray

fun setKey(myKey: String) {
    key = myKey.toByteArray(charset("UTF-8"))
    key = MessageDigest.getInstance("SHA-1").digest(key)
    secretKey = SecretKeySpec(key.copyOf(16), "AES")
}

fun String.encrypt(secret: String): String {
    setKey(secret)
    val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    return Base64.getEncoder()
        .encodeToString(cipher.doFinal(this.toByteArray(charset("UTF-8"))))
}

fun String.decrypt(secret: String): String {
    setKey(secret)
    val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
    cipher.init(Cipher.DECRYPT_MODE, secretKey)
    return String(cipher.doFinal(Base64.getDecoder().decode(this)))
}

fun String.hash(): String? {
    val digest = MessageDigest.getInstance("MD5")
    digest.update(this.toByteArray())
    val messageDigest = digest.digest()
    val hexString = StringBuffer()
    for (i in messageDigest.indices) hexString.append(
        Integer.toHexString(
            0xFF and messageDigest[i].toInt()
        )
    )
    return hexString.toString()
}

fun checkOrCreateMasterPassword(masterPassword: String, activity: Activity): Boolean {
    return if (masterPasswordExists(activity)) {
        checkMasterPassword(masterPassword, activity)
    } else {
        setMasterPassword(masterPassword, activity)
        Toast.makeText(activity, "Set new master password", Toast.LENGTH_SHORT).show()
        true
    }
}

fun masterPasswordExists(activity: Activity): Boolean {
    val sharedPref: SharedPreferences = activity.getSharedPreferences("hash", MODE_PRIVATE)
    val hashedMasterPassword = sharedPref.getString("MASTER_PASSWORD", null)
    return hashedMasterPassword !== null
}

fun checkMasterPassword(masterPassword: String, activity: Activity): Boolean {
    val sharedPref: SharedPreferences = activity.getSharedPreferences("hash", MODE_PRIVATE)
    val hashedMasterPassword = sharedPref.getString("MASTER_PASSWORD", null)!!
    return masterPassword.hash().equals(hashedMasterPassword)
}

fun setMasterPassword(masterPassword: String, activity: Activity) {
    val sharedPref: SharedPreferences = activity.getSharedPreferences("hash", MODE_PRIVATE)

    with(sharedPref.edit()) {
        putString("MASTER_PASSWORD", masterPassword.hash())
        commit()
    }
}

fun dropEncryptedData(db: PassengerDatabase, activity: Activity) {
    val sharedPref: SharedPreferences = activity.getSharedPreferences("hash", MODE_PRIVATE)
    with(sharedPref.edit()) {
        clear()
        commit()
    }
    db.clearAllTables()
}