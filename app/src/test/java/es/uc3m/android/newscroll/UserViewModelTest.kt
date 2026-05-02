package es.uc3m.android.newscroll

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import io.mockk.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import es.uc3m.android.newscroll.viewmodel.UserViewModel

class UserViewModelAdvancedTest {

    @Before
    fun setup() {
        mockkStatic(FirebaseAuth::class)
        mockkStatic(FirebaseFirestore::class)
        mockkStatic(FirebaseStorage::class)

        val mockAuth = mockk<FirebaseAuth>(relaxed = true)
        val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)
        val mockStorage = mockk<FirebaseStorage>(relaxed = true)

        every { FirebaseAuth.getInstance() } returns mockAuth
        every { FirebaseFirestore.getInstance() } returns mockFirestore
        every { FirebaseStorage.getInstance() } returns mockStorage
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `getCurrentUserId returns empty string when nobody is logged in`() {
        val mockAuth = FirebaseAuth.getInstance()
        every { mockAuth.currentUser } returns null

        val viewModel = UserViewModel()
        val result = viewModel.getCurrentUserId()

        assertEquals("", result)
    }

    @Test
    fun `getCurrentUserId returns correct uid when a user is logged in`() {
        val mockAuth = FirebaseAuth.getInstance()
        val mockUser = mockk<FirebaseUser>()

        every { mockUser.uid } returns "uc3m_student_999"

        every { mockUser.email } returns "student@uc3m.es"

        every { mockAuth.currentUser } returns mockUser

        val viewModel = UserViewModel()
        val result = viewModel.getCurrentUserId()

        assertEquals("uc3m_student_999", result)
    }

    @Test
    fun `logout correctly clears UI state AND tells Firebase to sign out`() {
        val mockAuth = FirebaseAuth.getInstance()
        val mockUser = mockk<FirebaseUser>()
        every { mockUser.email } returns "student@uc3m.es"
        every { mockAuth.currentUser } returns mockUser

        val viewModel = UserViewModel()

        assertEquals(true, viewModel.isLoggedIn.value)
        assertEquals("student@uc3m.es", viewModel.username.value)

        viewModel.logout()

        assertEquals(false, viewModel.isLoggedIn.value)
        assertEquals("", viewModel.username.value)

        verify(exactly = 1) { mockAuth.signOut() }
    }
}