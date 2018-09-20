package it.scoppelletti.spaceship.ads.consent

import it.scoppelletti.spaceship.ads.model.AdProvider
import it.scoppelletti.spaceship.ads.model.ConsentData
import java.util.Calendar
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ConsentDataStoreTest {
    private var currentYear: Int = 0
    private lateinit var consentDataStore: DefaultConsentDataStore

    @BeforeTest
    fun setUp() {
        currentYear = Calendar.getInstance().get(Calendar.YEAR)
        consentDataStore = DefaultConsentDataStore(createTempDir())
    }

    @Test
    fun empty() {
        var data: ConsentData? = null
        var err: Throwable? = null

        consentDataStore.load()
                .subscribe({ obj ->
                    data = obj
                }, { ex ->
                    err = ex
                })

        if (err != null) {
            throw err!!
        }

        assertNotNull(data, "Data not found.")
        assertEmpty(data!!, "Load from scratch")

        err = null
        consentDataStore.save(data!!)
                .subscribe( {
                }, { ex ->
                    err = ex
                })

        if (err != null) {
            throw err!!
        }

        data = null
        err = null
        consentDataStore.load()
                .subscribe({ obj ->
                    data = obj
                }, { ex ->
                    err = ex
                })

        if (err != null) {
            throw err!!
        }

        assertNotNull(data, "Data not found.")
        assertEmpty(data!!, "Load from scratch")
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
    fun notEmpty() {
        val saving: ConsentData
        var loaded: ConsentData? = null
        var err: Throwable? = null

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
                .subscribe( {
                }, { ex ->
                    err = ex
                })

        if (err != null) {
            throw err!!
        }

        err = null
        consentDataStore.load()
                .subscribe({ obj ->
                    loaded = obj
                }, { ex ->
                    err = ex
                })

        if (err != null) {
            throw err!!
        }

        assertNotNull(loaded, "Data not found.")
        assertEquals(saving.consentStatus, loaded?.consentStatus,
                "Content status differs.")
        assertTrue(saving.adProviders.toTypedArray() contentEquals
                (loaded?.adProviders?.toTypedArray() ?: emptyArray()),
                "adProviders differs.")
        assertEquals(saving.hasNonPersonalizedPublisherId,
                loaded?.hasNonPersonalizedPublisherId,
                "hasNonPersonalizedPublisherId differs.")
        assertEquals(saving.hasNonPersonalizedPublisherId,
                loaded?.hasNonPersonalizedPublisherId,
                "hasNonPersonalizedPublisherId differs.")
        assertEquals(saving.year, loaded?.year, "year differs.")
    }
}