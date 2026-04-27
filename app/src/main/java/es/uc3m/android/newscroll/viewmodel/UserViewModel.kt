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
import com.google.firebase.firestore.SetOptions

// 🔹 MODELOS

data class UserPreview(
    val id: String,
    val username: String,
    val photoUrl: String
)

data class FriendRequest(
    val id: String,
    val fromUserId: String
)

data class ChatPreview(
    val chatId: String,
    val otherUserId: String,
    val username: String,
    val photoUrl: String
)

data class Message(
    val senderId: String,
    val type: String,
    val text: String?,
    val articleId: String?
)

class UserViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    var isLoggedIn = mutableStateOf(auth.currentUser != null)
        private set

    var username = mutableStateOf(auth.currentUser?.email ?: "")
        private set

    var displayName = mutableStateOf("")
        private set

    var photoUrl = mutableStateOf<String?>(null)
        private set

    fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: ""
    }

    // 🔹 LOGIN
    fun login(email: String, password: String, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                username.value = auth.currentUser?.email ?: ""
                isLoggedIn.value = true
                loadUserProfile()
            } catch (e: Exception) {
                onError("Usuario o contraseña incorrectos")
            }
        }
    }

    // 🔹 REGISTER
    fun register(email: String, password: String, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val userId = result.user?.uid ?: return@launch

                db.collection("users")
                    .document(userId)
                    .set(
                        mapOf(
                            "username" to email.substringBefore("@"),
                            "photoUrl" to "",
                            "friends" to emptyList<String>()
                        )
                    )
                    .await()

                username.value = email
                isLoggedIn.value = true
                loadUserProfile()

            } catch (e: Exception) {
                onError("No se pudo crear la cuenta")
            }
        }
    }

    fun logout() {
        auth.signOut()
        isLoggedIn.value = false
        username.value = ""
    }

    // 🔹 SAVED ARTICLES
    fun saveArticle(articleId: String) {
        val userId = getCurrentUserId()
        db.collection("users")
            .document(userId)
            .collection("saved_articles")
            .document(articleId)
            .set(mapOf("id" to articleId))
    }

    fun getSavedArticles(onResult: (List<String>) -> Unit) {
        val userId = getCurrentUserId()

        db.collection("users")
            .document(userId)
            .collection("saved_articles")
            .get()
            .addOnSuccessListener {
                onResult(it.map { doc -> doc.id })
            }
    }

    fun removeArticle(articleId: String) {
        val userId = getCurrentUserId()
        db.collection("users")
            .document(userId)
            .collection("saved_articles")
            .document(articleId)
            .delete()
    }

    // 🔹 PROFILE
    fun loadUserProfile() {
        val userId = getCurrentUserId()

        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener {
                displayName.value = it.getString("username") ?: ""
                photoUrl.value = it.getString("photoUrl")
            }
    }

    fun updateUsername(newName: String) {
        val userId = getCurrentUserId()

        db.collection("users")
            .document(userId)
            .set(mapOf("username" to newName), SetOptions.merge())

        displayName.value = newName
    }

    fun uploadProfileImage(uri: Uri) {
        val userId = getCurrentUserId()
        val ref = storage.reference.child("profile_images/$userId.jpg")

        ref.putFile(uri).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { url ->
                db.collection("users")
                    .document(userId)
                    .set(mapOf("photoUrl" to url.toString()), SetOptions.merge())

                photoUrl.value = url.toString()
            }
        }
    }

    // 🔹 FRIEND REQUESTS
    fun sendFriendRequest(toUserId: String) {
        val fromUserId = getCurrentUserId()

        db.collection("friend_requests").add(
            mapOf(
                "fromUserId" to fromUserId,
                "toUserId" to toUserId,
                "status" to "pending",
                "timestamp" to System.currentTimeMillis()
            )
        )
    }

    fun getFriendRequests(onResult: (List<FriendRequest>) -> Unit) {
        val userId = getCurrentUserId()

        db.collection("friend_requests")
            .whereEqualTo("toUserId", userId)
            .whereEqualTo("status", "pending")
            .get()
            .addOnSuccessListener {
                val list = it.map { doc ->
                    FriendRequest(doc.id, doc.getString("fromUserId") ?: "")
                }
                onResult(list)
            }
    }

    fun acceptFriendRequest(requestId: String, fromUserId: String) {
        val currentUserId = getCurrentUserId()

        val batch = db.batch()

        val currentUserRef = db.collection("users").document(currentUserId)
        val otherUserRef = db.collection("users").document(fromUserId)
        val requestRef = db.collection("friend_requests").document(requestId)

        batch.set(currentUserRef,
            mapOf("friends" to com.google.firebase.firestore.FieldValue.arrayUnion(fromUserId)),
            SetOptions.merge()
        )

        batch.set(otherUserRef,
            mapOf("friends" to com.google.firebase.firestore.FieldValue.arrayUnion(currentUserId)),
            SetOptions.merge()
        )

        batch.update(requestRef, "status", "accepted")

        batch.commit().addOnSuccessListener {
            createChatIfNotExists(fromUserId)
        }
    }

    // 🔹 CHAT CREATION
    fun createChatIfNotExists(otherUserId: String) {
        val currentUserId = getCurrentUserId()

        db.collection("chats")
            .whereArrayContains("users", currentUserId)
            .get()
            .addOnSuccessListener { result ->

                val exists = result.any {
                    val users = it.get("users") as List<*>
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

    // 🔹 GET CHATS
    fun getChats(onResult: (List<ChatPreview>) -> Unit) {
        val currentUserId = getCurrentUserId()

        db.collection("chats")
            .whereArrayContains("users", currentUserId)
            .get()
            .addOnSuccessListener { result ->

                if (result.isEmpty) {
                    onResult(emptyList())
                    return@addOnSuccessListener
                }

                val chatList = mutableListOf<ChatPreview>()

                result.forEach { doc ->
                    val chatId = doc.id
                    val users = doc.get("users") as List<*>
                    val otherUserId = users.find { it != currentUserId } as String

                    db.collection("users")
                        .document(otherUserId)
                        .get()
                        .addOnSuccessListener { userDoc ->

                            chatList.add(
                                ChatPreview(
                                    chatId,
                                    otherUserId,
                                    userDoc.getString("username") ?: "Usuario",
                                    userDoc.getString("photoUrl") ?: ""
                                )
                            )

                            if (chatList.size == result.size()) {
                                onResult(chatList)
                            }
                        }
                }
            }
    }

    // 🔹 SEND MESSAGE
    fun sendTextMessage(chatId: String, text: String) {
        val userId = getCurrentUserId()

        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .add(
                mapOf(
                    "senderId" to userId,
                    "type" to "text",
                    "text" to text,
                    "timestamp" to System.currentTimeMillis()
                )
            )
    }

    fun sendArticleMessage(chatId: String, articleId: String) {
        val userId = getCurrentUserId()

        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .add(
                mapOf(
                    "senderId" to userId,
                    "type" to "article",
                    "articleId" to articleId,
                    "timestamp" to System.currentTimeMillis()
                )
            )
    }

    // 🔹 REALTIME LISTENER
    fun listenMessages(chatId: String, onUpdate: (List<Message>) -> Unit) {

        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, _ ->

                if (snapshot != null) {
                    val list = snapshot.map {
                        Message(
                            senderId = it.getString("senderId") ?: "",
                            type = it.getString("type") ?: "text",
                            text = it.getString("text"),
                            articleId = it.getString("articleId")
                        )
                    }
                    onUpdate(list)
                }
            }
    }
}