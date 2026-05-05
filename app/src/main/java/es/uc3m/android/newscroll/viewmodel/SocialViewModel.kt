package es.uc3m.android.newscroll.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SocialViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private fun getCurrentUserId(): String = auth.currentUser?.uid ?: ""
    
    fun sendFriendRequest(toUserId: String) {
        val fromUserId = getCurrentUserId()
        if (fromUserId.isBlank()) return
        db.collection("friend_requests").add(mapOf("fromUserId" to fromUserId, "toUserId" to toUserId, "status" to "pending", "timestamp" to System.currentTimeMillis()))
    }
    
    fun getChats(onResult: (List<ChatPreview>) -> Unit) {
        val currentUserId = getCurrentUserId()
        if (currentUserId.isBlank()) return
        db.collection("chats").whereArrayContains("users", currentUserId).get().addOnSuccessListener { result ->
            if (result.isEmpty) {
                onResult(emptyList())
                return@addOnSuccessListener
            }
            val chatList = mutableListOf<ChatPreview>()
            result.forEach { doc ->
                val chatId = doc.id
                val users = doc.get("users") as List<*>
                val otherUserId = users.find { it != currentUserId } as String
                db.collection("users").document(otherUserId).get().addOnSuccessListener { userDoc ->
                    chatList.add(ChatPreview(chatId, otherUserId, userDoc.getString("username") ?: "User", userDoc.getString("photoUrl") ?: ""))
                    if (chatList.size == result.size()) {
                        onResult(chatList)
                    }
                }
            }
        }
    }

    fun sendArticleMessage(chatId: String, articleId: String) {
        val userId = getCurrentUserId()
        if (userId.isBlank()) return
        db.collection("chats").document(chatId).collection("messages").add(mapOf("senderId" to userId, "type" to "article", "articleId" to articleId, "timestamp" to System.currentTimeMillis()))
    }
    
    fun sendTextMessage(chatId: String, text: String) {
        val userId = getCurrentUserId()
        if (userId.isBlank()) return
        db.collection("chats").document(chatId).collection("messages").add(mapOf("senderId" to userId, "type" to "text", "text" to text, "timestamp" to System.currentTimeMillis()))
    }

    fun listenMessages(chatId: String, onUpdate: (List<Message>) -> Unit) {
        db.collection("chats").document(chatId).collection("messages").orderBy("timestamp").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val list = snapshot.map { Message(it.getString("senderId") ?: "", it.getString("type") ?: "text", it.getString("text"), it.getString("articleId")) }
                onUpdate(list)
            }
        }
    }
}
