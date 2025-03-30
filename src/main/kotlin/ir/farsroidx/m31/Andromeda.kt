package ir.farsroidx.m31

import com.intellij.openapi.util.IconLoader

object Andromeda {

    const val NOTIFICATION_INFO_CHANNEL   = "FARSROIDX_INFO_NOTIFICATIONS"
    const val NOTIFICATION_WANING_CHANNEL = "FARSROIDX_WARNING_NOTIFICATIONS"
    const val NOTIFICATION_ERROR_CHANNEL  = "FARSROIDX_ERROR_NOTIFICATIONS"

    object Icons {

        @JvmField
        val Compose = IconLoader.getIcon("/icons/compose.png", javaClass)

        @JvmField
        val Icon_16 = IconLoader.getIcon("/icons/icon_16.png", javaClass)

        @JvmField
        val Icon_32 = IconLoader.getIcon("/icons/icon_32.png", javaClass)

        @JvmField
        val Icon_64 = IconLoader.getIcon("/icons/icon_64.png", javaClass)

        @JvmField
        val Icon_128 = IconLoader.getIcon("/icons/icon_128.png", javaClass)

        @JvmField
        val Icon_256 = IconLoader.getIcon("/icons/icon_256.png", javaClass)

        @JvmField
        val Icon_512 = IconLoader.getIcon("/icons/icon_512.png", javaClass)

    }
}