package dev.alnoer.audiosaas.local.encryption

import com.google.crypto.tink.Aead
import com.google.crypto.tink.integration.android.AndroidKeystore

object EncryptionProvider {
    fun getOrCreateAead(keyAlias: String): Aead {
        if (!AndroidKeystore.hasKey(keyAlias)) {
            AndroidKeystore.generateNewAes256GcmKey(keyAlias)
        }
        return AndroidKeystore.getAead(keyAlias)
    }
}
