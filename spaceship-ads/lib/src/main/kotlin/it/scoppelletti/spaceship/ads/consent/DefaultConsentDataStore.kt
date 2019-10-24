/*
 * Copyright (C) 2018 Dario Scoppelletti, <http://www.scoppelletti.it/>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("RedundantVisibilityModifier", "RemoveRedundantQualifierName")

package it.scoppelletti.spaceship.ads.consent

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import it.scoppelletti.spaceship.StdlibExt
import it.scoppelletti.spaceship.ads.model.ConsentData
import it.scoppelletti.spaceship.io.IOProvider
import it.scoppelletti.spaceship.io.closeQuietly
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import okio.BufferedSink
import okio.BufferedSource
import okio.Okio
import okio.Sink
import okio.Source
import org.threeten.bp.Clock
import org.threeten.bp.LocalDateTime
import java.io.File
import javax.inject.Inject
import javax.inject.Named

/**
 * Default implementation of the `ConsentDataStore` interface.
 *
 * @since 1.0.0
 */
public class DefaultConsentDataStore @Inject constructor(
        ioProvider: IOProvider,

        @Named(StdlibExt.DEP_UTCCLOCK)
        private val clock: Clock
) : ConsentDataStore {
    private val file: File
    private val adapter: JsonAdapter<ConsentData>

    init {
        file = File(ioProvider.noBackupFilesDir, DefaultConsentDataStore.DATA)
        adapter = Moshi.Builder().build().adapter(ConsentData::class.java)
    }

    public override suspend fun load(): ConsentData =
            withContext(Dispatchers.IO) {
                var data: ConsentData? = null
                var stream: Source? = null
                var reader: BufferedSource? = null

                try {
                    stream = Okio.source(file)
                    reader = Okio.buffer(stream!!)
                    data = adapter.fromJson(reader!!)
                    logger.debug { "Data loaded from $file." }
                } catch (ex: Exception) { // IOException | JsonException
                    logger.error(ex) { "Failed to load file $file." }
                } finally {
                    reader?.closeQuietly()
                    stream?.closeQuietly()
                }

                data ?: ConsentData(year = LocalDateTime.now(clock).year)
            }

    public override suspend fun save(data: ConsentData) =
            withContext(Dispatchers.IO) {
                var stream: Sink? = null
                var writer: BufferedSink? = null

                try {
                    stream = Okio.sink(file)
                    writer = Okio.buffer(stream!!)
                    adapter.toJson(writer!!, data)
                    logger.debug { "Data saved to $file." }
                } catch (ex: Exception) { // IOException | JsonException
                    logger.error(ex) { "Failed to save file $file." }
                } finally {
                    writer?.closeQuietly()
                    stream?.closeQuietly()
                }
            }

    private companion object {
        const val DATA = "it-scoppelletti-ads.dat"
        val logger = KotlinLogging.logger {}
    }
}
