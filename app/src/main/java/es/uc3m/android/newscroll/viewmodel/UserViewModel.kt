package es.uc3m.android.newscroll.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import android.net.Uri

data class UserPreview(
    val id: String,
    val username: String,
    val photoUrl: String
)

class UserViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    var isLoggedIn = mutableStateOf(auth.currentUser != null)
        private set

    var username = mutableStateOf(auth.currentUser?.email ?: "")
        private set

    var displayName = mutableStateOf("")
        private set

    var photoUrl = mutableStateOf<String?>(null)
        private set

    private val storage = FirebaseStorage.getInstance()

    fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: ""
    }
    //  LOGIN
    fun login(email: String, password: String, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                username.value = auth.currentUser?.email ?: ""
                isLoggedIn.value = true
            } catch (e: Exception) {
                onError("Usuario o contraseña incorrectos")
            }
        }
    }

    //  REGISTER
    fun register(email: String, password: String, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()

                val userId = result.user?.uid
                if (userId == null) {
                    onError("Error interno (UID null)")
                    return@launch
                }

                // FORZAMOS creación del documento
                db.collection("users")
                    .document(userId)
                    .set(
                        mapOf(
                            "username" to email.substringBefore("@"),
                            "photoUrl" to ""
                        )
                    )
                    .await()

                username.value = email
                isLoggedIn.value = true

                println("DEBUG: Usuario creado en Firestore $userId")

            } catch (e: Exception) {
                e.printStackTrace()
                onError("No se pudo crear la cuenta")
            }
        }
    }

    //  LOGOUT
    fun logout() {
        auth.signOut()
        isLoggedIn.value = false
        username.value = ""
    }

    //  GUARDAR ARTÍCULO
    fun saveArticle(articleId: String) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection("saved_articles")
            .document(articleId)
            .set(mapOf("id" to articleId))
    }

    //  OBTENER ARTÍCULOS
    fun getSavedArticles(onResult: (List<String>) -> Unit) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection("saved_articles")
            .get()
            .addOnSuccessListener { result ->
                val list = result.map { it.id }
                onResult(list)
            }
    }

    //  ELIMINAR ARTÍCULO
    fun removeArticle(articleId: String) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection("saved_articles")
            .document(articleId)
            .delete()
    }

    fun loadUserProfile() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->

                displayName.value = doc.getString("username") ?: ""
                photoUrl.value = doc.getString("photoUrl")

            }
    }

    fun updateUsername(newName: String) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .set(
                mapOf("username" to newName),
                com.google.firebase.firestore.SetOptions.merge()
            )

        displayName.value = newName
    }

    fun uploadProfileImage(uri: Uri) {
        val userId = auth.currentUser?.uid ?: return

        val ref = storage.reference.child("profile_images/$userId.jpg")

        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl
                    .addOnSuccessListener { downloadUrl ->

                        db.collection("users")
                            .document(userId)
                            .set(
                                mapOf("photoUrl" to downloadUrl.toString()),
                                com.google.firebase.firestore.SetOptions.merge()
                            )
                            .addOnSuccessListener {
                                println("FOTO GUARDADA")
                                photoUrl.value = downloadUrl.toString()
                            }
                            .addOnFailureListener {
                                println("ERROR FIRESTORE: ${it.message}")
                            }
                    }
                    .addOnFailureListener {
                        println("ERROR URL: ${it.message}")
                    }
            }
            .addOnFailureListener {
                println("ERROR STORAGE: ${it.message}")
            }
    }

    fun sendFriendRequest(toUserId: String) {
        val fromUserId = auth.currentUser?.uid ?: return

        db.collection("friend_requests")
            .add(
                mapOf(
                    "fromUserId" to fromUserId,
                    "toUserId" to toUserId,
                    "status" to "pending",
                    "timestamp" to System.currentTimeMillis()
                )
            )
    }

    fun getFriendRequests(onResult: (List<String>) -> Unit) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("friend_requests")
            .whereEqualTo("toUserId", userId)
            .whereEqualTo("status", "pending")
            .get()
            .addOnSuccessListener { result ->
                val list = result.map {
                    it.getString("fromUserId") ?: ""
                }
                onResult(list)
            }
    }

    fun acceptFriendRequest(fromUserId: String) {
        val currentUserId = auth.currentUser?.uid ?: return

        val batch = db.batch()

        val currentUserRef = db.collection("users").document(currentUserId)
        val otherUserRef = db.collection("users").document(fromUserId)

        // añadir en ambos
        batch.update(
            currentUserRef,
            "friends",
            com.google.firebase.firestore.FieldValue.arrayUnion(fromUserId)
        )

        batch.update(
            otherUserRef,
            "friends",
            com.google.firebase.firestore.FieldValue.arrayUnion(currentUserId)
        )

        batch.commit().addOnSuccessListener {

            // marcar solicitud como aceptada
            db.collection("friend_requests")
                .whereEqualTo("fromUserId", fromUserId)
                .whereEqualTo("toUserId", currentUserId)
                .get()
                .addOnSuccessListener { result ->
                    for (doc in result) {
                        doc.reference.update("status", "accepted")
                    }
                }

            // crear chat
            createChatIfNotExists(fromUserId)
        }
    }

    fun createChatIfNotExists(otherUserId: String) {
        val currentUserId = auth.currentUser?.uid ?: return

        db.collection("chats")
            .whereArrayContains("users", currentUserId)
            .get()
            .addOnSuccessListener { result ->

                val exists = result.any { doc ->
                    val users = doc.get("users") as List<*>
                    users.contains(otherUserId)
                }

                if (!exists) {
                    db.collection("chats").add(
                        mapOf(
                            "users" to listOf(currentUserId, otherUserId),
                            "lastMessage" to "",
                            "timestamp" to System.currentTimeMillis()
                        )
                    )
                }
            }
    }


    fun getChats(onResult: (List<UserPreview>) -> Unit) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("chats")
            .whereArrayContains("users", userId)
            .get()
            .addOnSuccessListener { result ->

                val otherUsers = mutableListOf<String>()

                result.forEach { doc ->
                    val users = doc.get("users") as List<*>
                    val other = users.find { it != userId } as String
                    otherUsers.add(other)
                }

                // cargar datos de esos users
                val finalList = mutableListOf<UserPreview>()

                otherUsers.forEach { uid ->
                    db.collection("users")
                        .document(uid)
                        .get()
                        .addOnSuccessListener { userDoc ->

                            val username = userDoc.getString("username") ?: "Usuario"
                            val photoUrl = userDoc.getString("photoUrl") ?: ""

                            finalList.add(
                                UserPreview(uid, username, photoUrl)
                            )

                            if (finalList.size == otherUsers.size) {
                                onResult(finalList)
                            }
                        }
                }
            }
    }

}

