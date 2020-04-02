package eu.nk2.springcraft.utils

import java.security.Key
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

fun ByteArray.getPrivateKey(algorithm: String): Key {
    val spec = PKCS8EncodedKeySpec(this)
    val keyFactory = KeyFactory.getInstance(algorithm)
    return keyFactory.generatePrivate(spec)
}

fun ByteArray.getPublicKey(algorithm: String): Key {
    val spec = X509EncodedKeySpec(this)
    val keyFactory = KeyFactory.getInstance(algorithm)
    return keyFactory.generatePublic(spec)
}
