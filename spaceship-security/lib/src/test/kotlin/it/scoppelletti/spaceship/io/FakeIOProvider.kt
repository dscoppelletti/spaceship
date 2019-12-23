package it.scoppelletti.spaceship.io

import java.io.File

class FakeIOProvider : IOProvider {
    private val _dataDir: File = createTempDir()

    override val noBackupFilesDir: File = _dataDir
}
