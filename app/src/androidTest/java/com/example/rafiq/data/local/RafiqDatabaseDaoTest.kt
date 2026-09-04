package com.example.rafiq.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RafiqDatabaseDaoTest {

    private lateinit var db: RafiqDatabase
    private lateinit var contactDao: ContactDao
    private lateinit var medicationDao: MedicationDao
    private lateinit var placeDao: PlaceDao
    private lateinit var chatMessageDao: ChatMessageDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, RafiqDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        contactDao = db.contactDao()
        medicationDao = db.medicationDao()
        placeDao = db.placeDao()
        chatMessageDao = db.chatMessageDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun contactDao_insertAndFindAndDelete() = runBlocking {
        val contact = Contact(id = "c1", name = "Ahmed", phoneNumber = "01012345678")
        contactDao.insertContact(contact)

        val found = contactDao.findContactByName("Ahm")
        assertEquals("Ahmed", found?.name)

        val all = contactDao.getAllContacts().first()
        assertEquals(1, all.size)
        assertEquals("01012345678", all[0].phoneNumber)

        contactDao.deleteContact(contact)
        assertNull(contactDao.findContactByName("Ahmed"))
    }

    @Test
    fun contactDao_insertReplacesOnSameId() = runBlocking {
        contactDao.insertContact(Contact(id = "c1", name = "Ali", phoneNumber = "111"))
        contactDao.insertContact(Contact(id = "c1", name = "Omar", phoneNumber = "222"))

        val all = contactDao.getAllContacts().first()
        assertEquals(1, all.size)
        assertEquals("Omar", all[0].name)
    }

    @Test
    fun medicationDao_insertAndDelete() = runBlocking {
        val med = Medication(id = "m1", name = "Aspirin", dosage = "500mg", time = "08:00")
        medicationDao.insertMedication(med)

        val all = medicationDao.getAllMedications().first()
        assertEquals(1, all.size)
        assertEquals("Aspirin", all[0].name)

        medicationDao.deleteMedication(med)
        assertTrue(medicationDao.getAllMedications().first().isEmpty())
    }

    @Test
    fun placeDao_insertAndGetById() = runBlocking {
        val place = EquippedPlaceEntity(
            id = "p1",
            name = "Cairo Hospital",
            description = "Accessible",
            latitude = 30.0444,
            longitude = 31.2357,
            isWheelchairAccessible = true,
            hasSignLanguageSupport = false,
            hasBrailleSignage = true
        )
        placeDao.insertPlace(place)

        val byId = placeDao.getPlaceById("p1")
        assertEquals("Cairo Hospital", byId?.name)
        assertTrue(byId!!.isWheelchairAccessible)

        val all = placeDao.getAllPlaces().first()
        assertEquals(1, all.size)
        assertEquals(31.2357, all[0].longitude, 0.0)

        placeDao.deletePlace(place)
        assertNull(placeDao.getPlaceById("p1"))
    }

    @Test
    fun chatMessageDao_insertOrderedAndClear() = runBlocking {
        chatMessageDao.insertMessage(ChatMessage(id = "x1", message = "hello", timestamp = 10, sender = "user"))
        chatMessageDao.insertMessage(ChatMessage(id = "x2", message = "hi rafiq", timestamp = 5, sender = "user"))

        val ascending = chatMessageDao.getAllMessagesOnce()
        assertEquals("hi rafiq", ascending[0].message)
        assertEquals("hello", ascending[1].message)

        val flow = chatMessageDao.getMessagesBySender("user").first()
        assertEquals(2, flow.size)

        chatMessageDao.clearAll()
        assertTrue(chatMessageDao.getAllMessagesOnce().isEmpty())
    }
}
