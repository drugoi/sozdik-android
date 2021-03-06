package kz.sozdik.logout

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kz.sozdik.core.AuthUtils
import kz.sozdik.history.domain.HistoryInteractor
import kz.sozdik.profile.domain.ProfileLocalGateway
import javax.inject.Inject

private const val FCM_SENDER_ID = "654978430104"

class LogoutInteractor @Inject constructor(
    private val profileLocalGateway: ProfileLocalGateway,
    private val historyInteractor: HistoryInteractor
) {
    suspend fun logout() {
        profileLocalGateway.deleteProfile()
        historyInteractor.clearAllWordsLocally()
        GlobalScope.launch {
            FirebaseInstanceId.getInstance()
                .deleteToken(FCM_SENDER_ID, FirebaseMessaging.INSTANCE_ID_SCOPE)
        }
        AuthUtils.clear()
    }
}