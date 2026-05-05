package es.uc3m.android.newscroll.viewmodel

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    var displayName = mutableStateOf("")
        private set
    var photoUrl = mutableStateOf<String?>(null)
        private set

    private fun getCurrentUserId(): String = auth.currentUser?.uid ?: ""

    fun loadUserProfile() {
        val userId = getCurrentUserId()
        if (userId.isBlank()) return
        db.collection("users").document(userId).get().addOnSuccessListener {
            displayName.value = it.getString("username") ?: ""
            photoUrl.value = it.getString("photoUrl")
        }
    }

    fun updateUsername(newName: String) {
        val userId = getCurrentUserId()
        if (userId.isBlank()) return
        db.collection("users").document(userId).set(mapOf("username" to newName), SetOptions.merge())
        displayName.value = newName
    }

    fun uploadProfileImage(uri: Uri) {
        val userId = getCurrentUserId()
        if (userId.isBlank()) return
        val ref = storage.reference.child("profile_images/$userId.jpg")
        ref.putFile(uri).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { url ->
                db.collection("users").document(userId).set(mapOf("photoUrl" to url.toString()), SetOptions.merge())
                photoUrl.value = url.toString()
            }
        }
    }
}
