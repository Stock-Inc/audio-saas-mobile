package dev.alnoer.audiosaas.local.keyvalue

import androidx.datastore.core.Serializer
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesSerializer
import okio.buffer
import okio.sink
import okio.source
import java.io.InputStream
import java.io.OutputStream

object PreferencesStreamSerializer : Serializer<Preferences> {
    override val defaultValue: Preferences = PreferencesSerializer.defaultValue

    override suspend fun readFrom(input: InputStream): Preferences =
        PreferencesSerializer.readFrom(input.source().buffer())

    override suspend fun writeTo(
        t: Preferences,
        output: OutputStream,
    ) {
        val sink = output.sink().buffer()
        PreferencesSerializer.writeTo(t, sink)
        sink.flush()
    }
}
