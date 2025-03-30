package ir.farsroidx.m31.extensions

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import ir.farsroidx.m31.Andromeda

fun Project?.infoNotification(content: String) {
    NotificationGroupManager.getInstance()
        .getNotificationGroup(Andromeda.NOTIFICATION_INFO_CHANNEL)
        .createNotification("Andromeda", content, NotificationType.INFORMATION)
        .notify(this)
}

fun Project?.warningNotification(content: String) {
    NotificationGroupManager.getInstance()
        .getNotificationGroup(Andromeda.NOTIFICATION_WANING_CHANNEL)
        .createNotification("Andromeda", content, NotificationType.WARNING)
        .notify(this)
}

fun Project?.errorNotification(content: String) {
    NotificationGroupManager.getInstance()
        .getNotificationGroup(Andromeda.NOTIFICATION_ERROR_CHANNEL)
        .createNotification("Andromeda", content, NotificationType.ERROR)
        .notify(this)
}