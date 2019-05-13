package it.scoppelletti.spaceship.io

import java.io.File
import java.io.InputStream
import java.io.OutputStream

class FakeIOProvider : IOProvider {
    private val _dataDir: File = createTempDir()

    override val noBackupFilesDir: File = _dataDir

    override fun base64InputStream(inputStream: InputStream): InputStream {
        throw NotImplementedError()
    }

    override fun base64OutputStream(outputStream: OutputStream): OutputStream {
        throw NotImplementedError()
    }
}