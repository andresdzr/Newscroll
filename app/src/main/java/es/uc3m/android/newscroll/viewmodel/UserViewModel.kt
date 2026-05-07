package es.uc3m.android.newscroll.viewmodel

import android.util.Log
import es.uc3m.android.newscroll.model.NewsArticle
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

// =========================
// DATA MODELS
// =========================

// Basic user preview for lists
data class UserPreview(
    val id: String,
    val username: String,
    val photoUrl: String
)

// Friend request model
data class FriendRequest(
    val id: String,
    val fromUserId: String
)

// Chat preview model (used in chat list)
data class ChatPreview(
    val chatId: String,
    val otherUserId: String,
    val username: String,
    val photoUrl: String
)

// Message model (text or article)
data class Message(
    val senderId: String,
    val type: String,
    val text: String?,
    val articleId: String?
)

class UserViewModel : ViewModel() {

    // Firebase services
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // Auth state
    var isLoggedIn = mutableStateOf(auth.currentUser != null)
        private set

    // User email (fallback identifier)
    var username = mutableStateOf(auth.currentUser?.email ?: "")
        private set

    // Profile data
    var displayName = mutableStateOf("")
        private set

    var photoUrl = mutableStateOf<String?>(null)
        private set

    // Returns current user ID or empty if not logged in
    fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: ""
    }

    // =========================
    // AUTHENTICATION
    // =========================

    // Login user with email/password
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

    // Register new user and create Firestore profile
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

    // Logout current user
    fun logout() {
        auth.signOut()
        isLoggedIn.value = false
        username.value = ""
    }

    // =========================
    // SAVED ARTICLES
    // =========================

    // Save article to user collection
    fun saveArticle(articleId: String) {
        val userId = getCurrentUserId()
        db.collection("users")
            .document(userId)
            .collection("saved_articles")
            .document(articleId)
            .set(mapOf("id" to articleId))
    }

    // Retrieve saved article IDs
    fun getSavedArticles(onResult: (List<String>) -> Unit) {
        val userId = getCurrentUserId()

        if (userId.isBlank()) return

        db.collection("users")
            .document(userId)
            .collection("saved_articles")
            .get()
            .addOnSuccessListener {
                onResult(it.map { doc -> doc.id })
            }
    }

    // Fetch saved IDs then resolve each to a full NewsArticle from Firestore
    fun getSavedArticlesFull(onResult: (List<NewsArticle>) -> Unit) {
        val userId = getCurrentUserId()
        if (userId.isBlank()) {
            Log.d("SavedArticles", "User not logged in — returning empty")
            onResult(emptyList())
            return
        }
        viewModelScope.launch {
            try {
                val savedDocs = db.collection("users")
                    .document(userId)
                    .collection("saved_articles")
                    .get()
                    .await()
                Log.d("SavedArticles", "Found ${savedDocs.size()} saved IDs")
                val articles = savedDocs.mapNotNull { savedDoc ->
                    try {
                        val doc = db.collection("articles")
                            .document(savedDoc.id)
                            .get()
                            .await()
                        if (doc.exists()) {
                            Log.d("SavedArticles", "Resolved: ${savedDoc.id} → ${doc.getString("title")}")
                            NewsArticle(
                                id = doc.getString("id") ?: savedDoc.id,
                                title = doc.getString("title") ?: "",
                                description = doc.getString("description") ?: "",
                                imageUrl = doc.getString("imageUrl") ?: "",
                                articleUrl = doc.getString("articleUrl") ?: "",
                                category = doc.getString("category"),
                                source = doc.getString("source"),
                                publishedDate = doc.getString("publishedDate")
                            )
                        } else {
                            Log.w("SavedArticles", "No Firestore doc for ID: ${savedDoc.id}")
                            null
                        }
                    } catch (e: Exception) {
                        Log.e("SavedArticles", "Error fetching ${savedDoc.id}: ${e.message}")
                        null
                    }
                }
                Log.d("SavedArticles", "Returning ${articles.size} resolved articles")
                onResult(articles)
            } catch (e: Exception) {
                Log.e("SavedArticles", "Failed to load saved articles: ${e.message}")
                onResult(emptyList())
            }
        }
    }

    // Remove article from saved list
    fun removeArticle(articleId: String) {
        val userId = getCurrentUserId()
        db.collection("users")
            .document(userId)
            .collection("saved_articles")
            .document(articleId)
            .delete()
    }

    // =========================
    // PROFILE
    // =========================

    // Load user profile from Firestore
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

    // Update username in Firestore
    fun updateUsername(newName: String) {
        val userId = getCurrentUserId()

        db.collection("users")
            .document(userId)
            .set(mapOf("username" to newName), SetOptions.merge())

        displayName.value = newName
    }

    // Upload profile image to Firebase Storage
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

    // Get user profile for a given user ID
    fun getUserProfile(userId: String, onResult: (UserPreview) -> Unit) {
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener {
                val user = UserPreview(
                    id = userId,
                    username = it.getString("username") ?: "Unknown",
                    photoUrl = it.getString("photoUrl") ?: ""
                )
                onResult(user)
            }
    }

    // =========================
    // FRIEND REQUESTS
    // =========================

    // Send friend request to another user
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

    // Get pending friend requests
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

    // Accept friend request and create chat
    fun acceptFriendRequest(requestId: String, fromUserId: String) {
        val currentUserId = getCurrentUserId()

        val batch = db.batch()

        val currentUserRef = db.collection("users").document(currentUserId)
        val otherUserRef = db.collection("users").document(fromUserId)
        val requestRef = db.collection("friend_requests").document(requestId)

        // Add each user to the other's friend list
        batch.set(
            currentUserRef,
            mapOf("friends" to com.google.firebase.firestore.FieldValue.arrayUnion(fromUserId)),
            SetOptions.merge()
        )

        batch.set(
            otherUserRef,
            mapOf("friends" to com.google.firebase.firestore.FieldValue.arrayUnion(currentUserId)),
            SetOptions.merge()
        )

        // Mark request as accepted
        batch.update(requestRef, "status", "accepted")

        batch.commit().addOnSuccessListener {
            createChatIfNotExists(fromUserId)
        }
    }

    // =========================
    // CHAT MANAGEMENT
    // =========================

    // Create chat only if it does not already exist
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

    // Retrieve chat previews with user info
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

    // =========================
    // MESSAGING
    // =========================

    // Send text message
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

    // Send article message
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

    // =========================
    // REAL-TIME LISTENER
    // =========================

    // Listen to messages in real time
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