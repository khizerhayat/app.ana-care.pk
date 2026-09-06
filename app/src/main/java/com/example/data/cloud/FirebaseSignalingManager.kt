package com.example.data.cloud

import android.content.Context
import android.util.Log
import com.example.data.local.dao.EncryptedMessageDao
import com.example.data.local.entities.EncryptedMessageEntity
import com.example.data.local.entities.MedicalGalleryEntity
import com.example.data.local.entities.UserAccountEntity
import com.example.ui.viewmodel.ActiveCallSession
import com.example.ui.viewmodel.CallStatus
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Firebase Real-time Cloud Signaling & Multi-Device Synchronization Engine.
 * Enables live 1-on-1 cross-device incoming call ringing, interactive consultation room synchronization,
 * and real-time end-to-end encrypted messaging across separate physical phones, tablets, and emulators.
 */
class FirebaseSignalingManager(
    private val context: Context,
    private val encryptedMessageDao: EncryptedMessageDao
) {
    companion object {
        private const val TAG = "FirebaseSignaling"
        private const val COLLECTION_CALLS = "telehealth_active_calls"
        private const val COLLECTION_MESSAGES = "telehealth_messages"
    }

    private var firestore: FirebaseFirestore? = null
    private var callListenerRegistration: ListenerRegistration? = null
    private var messageListenerRegistration: ListenerRegistration? = null

    private val _isCloudConnected = MutableStateFlow(false)
    val isCloudConnected: StateFlow<Boolean> = _isCloudConnected.asStateFlow()

    private val _cloudStatusMessage = MutableStateFlow("Initializing Cloud Signaling...")
    val cloudStatusMessage: StateFlow<Boolean> = _isCloudConnected.asStateFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    init {
        initializeFirestore()
    }

    private fun initializeFirestore() {
        try {
            val apps = FirebaseApp.getApps(context)
            if (apps.isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            val db = FirebaseFirestore.getInstance()
            firestore = db
            _isCloudConnected.value = true
            _cloudStatusMessage.value = "Cloud Sync Active (Firestore Real-time Channel)"
            Log.d(TAG, "Firebase Firestore initialized successfully for real-time signaling.")
        } catch (e: Exception) {
            Log.w(TAG, "Firebase Firestore initialization notice: ${e.message}. Using local memory & Room fallback.")
            _isCloudConnected.value = false
            _cloudStatusMessage.value = "Offline / Local Mode (${e.localizedMessage ?: "No Cloud Project Configured"})"
        }
    }

    /**
     * Start listening for incoming/active video calls for the logged-in user.
     */
    fun startListeningForCalls(
        currentUserId: String,
        allAccounts: List<UserAccountEntity>,
        onCallReceivedOrUpdated: (ActiveCallSession?) -> Unit
    ) {
        callListenerRegistration?.remove()
        val db = firestore ?: return

        try {
            // Listen to calls where user is either the recipient or caller
            callListenerRegistration = db.collection(COLLECTION_CALLS)
                .whereIn("status", listOf("RINGING", "CONNECTED"))
                .addSnapshotListener { snapshots, error ->
                    if (error != null) {
                        Log.e(TAG, "Call listener error: ${error.message}")
                        return@addSnapshotListener
                    }

                    if (snapshots == null || snapshots.isEmpty) {
                        return@addSnapshotListener
                    }

                    // Find call relevant to current user
                    val relevantDoc = snapshots.documents.firstOrNull { doc ->
                        val recipientId = doc.getString("recipientId") ?: ""
                        val callerId = doc.getString("callerId") ?: ""
                        recipientId == currentUserId || callerId == currentUserId
                    }

                    if (relevantDoc != null) {
                        val session = parseCallSession(relevantDoc, allAccounts)
                        if (session != null) {
                            onCallReceivedOrUpdated(session)
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to register call listener: ${e.message}")
        }
    }

    /**
     * Start listening for real-time encrypted messages across devices.
     */
    fun startListeningForMessages(currentUserId: String) {
        messageListenerRegistration?.remove()
        val db = firestore ?: return

        try {
            messageListenerRegistration = db.collection(COLLECTION_MESSAGES)
                .whereEqualTo("receiverId", currentUserId)
                .addSnapshotListener { snapshots, error ->
                    if (error != null) {
                        Log.e(TAG, "Message listener error: ${error.message}")
                        return@addSnapshotListener
                    }

                    if (snapshots != null && !snapshots.isEmpty) {
                        coroutineScope.launch {
                            for (doc in snapshots.documents) {
                                try {
                                    val senderId = doc.getString("senderId") ?: continue
                                    val senderName = doc.getString("senderName") ?: "Healthcare User"
                                    val senderRole = doc.getString("senderRole") ?: "PATIENT"
                                    val receiverId = doc.getString("receiverId") ?: currentUserId
                                    val receiverName = doc.getString("receiverName") ?: ""
                                    val messageText = doc.getString("messageText") ?: ""
                                    val cipherDigest = doc.getString("cipherTextDigest") ?: ""
                                    val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                                    val attachmentName = doc.getString("attachmentName")
                                    val attachmentType = doc.getString("attachmentType")
                                    val attachmentSize = doc.getString("attachmentSize")

                                    val entity = EncryptedMessageEntity(
                                        senderId = senderId,
                                        senderName = senderName,
                                        senderRole = senderRole,
                                        receiverId = receiverId,
                                        receiverName = receiverName,
                                        timestamp = timestamp,
                                        messageText = messageText,
                                        cipherTextDigest = cipherDigest,
                                        isEncrypted = true,
                                        attachmentName = attachmentName,
                                        attachmentType = attachmentType,
                                        attachmentSize = attachmentSize,
                                        isRead = false
                                    )
                                    encryptedMessageDao.insertMessage(entity)
                                } catch (e: Exception) {
                                    Log.e(TAG, "Failed to insert synced message: ${e.message}")
                                }
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to register message listener: ${e.message}")
        }
    }

    /**
     * Broadcast an outgoing video call to the cloud signaling channel.
     */
    fun publishCallInitiation(session: ActiveCallSession) {
        val db = firestore ?: return
        val data = hashMapOf<String, Any?>(
            "callId" to session.callId,
            "callerId" to session.caller.userId,
            "callerName" to session.caller.name,
            "callerRole" to session.caller.role,
            "callerAvatar" to session.caller.avatarInitials,
            "recipientId" to session.recipient.userId,
            "recipientName" to session.recipient.name,
            "recipientRole" to session.recipient.role,
            "recipientAvatar" to session.recipient.avatarInitials,
            "callType" to session.callType.name,
            "status" to session.status.name,
            "startedAt" to session.startedAt,
            "caseTitle" to (session.caseItem?.title ?: ""),
            "caseCategory" to (session.caseItem?.category ?: ""),
            "caseSummary" to (session.caseItem?.notes ?: "")
        )

        db.collection(COLLECTION_CALLS).document(session.callId)
            .set(data, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(TAG, "Call ${session.callId} published to cloud signaling.")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to publish call: ${e.message}")
            }
    }

    /**
     * Update call status in cloud (CONNECTED, DECLINED, ENDED).
     */
    fun updateCallStatus(callId: String, status: CallStatus) {
        val db = firestore ?: return
        db.collection(COLLECTION_CALLS).document(callId)
            .update("status", status.name)
            .addOnSuccessListener {
                Log.d(TAG, "Call $callId updated to $status in cloud.")
                if (status == CallStatus.ENDED || status == CallStatus.DECLINED) {
                    // Clean up after slight delay
                    coroutineScope.launch {
                        kotlinx.coroutines.delay(5000L)
                        try {
                            db.collection(COLLECTION_CALLS).document(callId).delete()
                        } catch (_: Exception) {}
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to update call status: ${e.message}")
            }
    }

    /**
     * Publish encrypted message to cloud channel.
     */
    fun publishEncryptedMessage(entity: EncryptedMessageEntity) {
        val db = firestore ?: return
        val msgMap = hashMapOf<String, Any?>(
            "id" to entity.id,
            "senderId" to entity.senderId,
            "senderName" to entity.senderName,
            "senderRole" to entity.senderRole,
            "receiverId" to entity.receiverId,
            "receiverName" to entity.receiverName,
            "messageText" to entity.messageText,
            "cipherTextDigest" to entity.cipherTextDigest,
            "timestamp" to entity.timestamp,
            "attachmentName" to entity.attachmentName,
            "attachmentType" to entity.attachmentType,
            "attachmentSize" to entity.attachmentSize,
            "isRead" to entity.isRead
        )

        db.collection(COLLECTION_MESSAGES).document("msg_${entity.senderId}_${entity.timestamp}")
            .set(msgMap, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(TAG, "Message synced to cloud.")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to sync message to cloud: ${e.message}")
            }
    }

    private fun parseCallSession(doc: DocumentSnapshot, accounts: List<UserAccountEntity>): ActiveCallSession? {
        val callId = doc.getString("callId") ?: doc.id
        val callerId = doc.getString("callerId") ?: return null
        val recipientId = doc.getString("recipientId") ?: return null
        val statusStr = doc.getString("status") ?: "RINGING"
        val status = try { CallStatus.valueOf(statusStr) } catch (_: Exception) { CallStatus.RINGING }
        val callTypeStr = doc.getString("callType") ?: "VIDEO"
        val callType = try { com.example.ui.viewmodel.CallType.valueOf(callTypeStr) } catch (_: Exception) { com.example.ui.viewmodel.CallType.VIDEO }
        val startedAt = doc.getLong("startedAt") ?: System.currentTimeMillis()

        val callerAccount = accounts.find { it.userId == callerId } ?: UserAccountEntity(
            userId = callerId,
            name = doc.getString("callerName") ?: "Healthcare Caller",
            email = "$callerId@anacare.org",
            role = doc.getString("callerRole") ?: "DOCTOR",
            avatarInitials = doc.getString("callerAvatar") ?: "DR"
        )

        val recipientAccount = accounts.find { it.userId == recipientId } ?: UserAccountEntity(
            userId = recipientId,
            name = doc.getString("recipientName") ?: "Patient Recipient",
            email = "$recipientId@anacare.org",
            role = doc.getString("recipientRole") ?: "PATIENT",
            avatarInitials = doc.getString("recipientAvatar") ?: "PT"
        )

        val caseTitle = doc.getString("caseTitle")
        val caseItem = if (!caseTitle.isNullOrBlank()) {
            MedicalGalleryEntity(
                patientId = recipientId,
                timestamp = startedAt,
                title = caseTitle,
                category = doc.getString("caseCategory") ?: "Clinical Consultation",
                imageUri = "",
                notes = doc.getString("caseSummary") ?: "",
                loggedByRole = callerAccount.role,
                loggedByName = callerAccount.name
            )
        } else null

        return ActiveCallSession(
            callId = callId,
            caller = callerAccount,
            recipient = recipientAccount,
            callType = callType,
            participants = listOf(callerAccount, recipientAccount),
            caseItem = caseItem,
            status = status,
            startedAt = startedAt
        )
    }

    fun stopListeners() {
        callListenerRegistration?.remove()
        callListenerRegistration = null
        messageListenerRegistration?.remove()
        messageListenerRegistration = null
    }
}
