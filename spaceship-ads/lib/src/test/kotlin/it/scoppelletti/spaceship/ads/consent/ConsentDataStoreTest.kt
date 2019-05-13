
@file:Suppress("JoinDeclarationAndAssignment")

package it.scoppelletti.spaceship.ads.consent

import it.scoppelletti.spaceship.ads.model.AdProvider
import it.scoppelletti.spaceship.ads.model.ConsentData
import it.scoppelletti.spaceship.io.FakeIOProvider
import it.scoppelletti.spaceship.types.FakeTimeProvider
import it.scoppelletti.spaceship.types.TimeProvider
import kotlinx.coroutines.runBlocking
import java.util.Calendar
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ConsentDataStoreTest {
    private var currentYear = 0
    private lateinit var timeProvider: TimeProvider
    private lateinit var consentDataStore: DefaultConsentDataStore

    @BeforeTest
    fun setUp() {
        timeProvider = FakeTimeProvider()
        currentYear = timeProvider.currentTime().get(Calendar.YEAR)
        consentDataStore = DefaultConsentDataStore(FakeIOProvider(),
                timeProvider)
    }

    @Test
    fun empty() = runBlocking {
        var data: ConsentData

        data = consentDataStore.load()
        assertEmpty(data, "Load from scratch.")

        consentDataStore.save(data)

        data = consentDataStore.load()
        assertEmpty(data, "Load from scratch.")
    }

    private fun assertEmpty(data: ConsentData, msg: String) {
        assertEquals(ConsentStatus.UNKNOWN, data.consentStatus,
                "$msg - consentStatus not unknown.")
        assertTrue(data.adProviders.isEmpty(), "$msg - adProviders not empty,")
        assertFalse(data.hasNonPersonalizedPublisherId,
                "$msg - hasNonPersonalizedPublisherId is true.")
        assertEquals(currentYear, data.year,
                "$msg - year is not $currentYear.")
    }

    @Test
    fun notEmpty() = runBlocking {
        val loaded: ConsentData
        val saving: ConsentData

        saving = ConsentData(
                consentStatus = ConsentStatus.PERSONALIZED,

                adProviders = listOf(
                        AdProvider(
                                companyId = "1765",
                                name = "Aarki",
                                policyUrl = "http://corp.aarki.com/privacy"
                        ),
                        AdProvider(
                                companyId = "236",
                                name = "Adacado",
                                policyUrl = "https://www.adacado.com/privacy-policy-april-25-2018/"
                        ),
                        AdProvider(
                                companyId = "15",
                                name = "Adara Media",
                                policyUrl = "https://adara.com/2018/04/10/adara-gdpr-faq"
                        )
                ),

                hasNonPersonalizedPublisherId = true,
                year = 2025)

        consentDataStore.save(saving)
        loaded = consentDataStore.load()

        assertNotNull(loaded, "Data not found.")
        assertEquals(saving.consentStatus, loaded.consentStatus,
                "Content status differs.")
        assertTrue(saving.adProviders.toTypedArray() contentEquals
                loaded.adProviders.toTypedArray(),
                "adProviders differs.")
        assertEquals(saving.hasNonPersonalizedPublisherId,
                loaded.hasNonPersonalizedPublisherId,
                "hasNonPersonalizedPublisherId differs.")
        assertEquals(saving.hasNonPersonalizedPublisherId,
                loaded.hasNonPersonalizedPublisherId,
                "hasNonPersonalizedPublisherId differs.")
        assertEquals(saving.year, loaded.year, "year differs.")
    }
}