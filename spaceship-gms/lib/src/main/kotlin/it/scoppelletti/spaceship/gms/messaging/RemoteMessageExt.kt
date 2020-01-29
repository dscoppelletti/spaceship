/*
 * Copyright (C) 2020 Dario Scoppelletti, <http://www.scoppelletti.it/>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("JoinDeclarationAndAssignment", "RedundantVisibilityModifier",
        "unused")

package it.scoppelletti.spaceship.gms.messaging

import com.google.firebase.messaging.RemoteMessage

/**
 * Coverts a `RemoteMessage` to a string.
 *
 * @param  message Message.
 * @return         String.
 * @since          1.0.0
 */
public fun toString(message: RemoteMessage): String =
        buildString {
            val pos: Int

            append("RemoteMessage(messageId=")
            append(message.messageId)
            append(",messageType=")
            append(message.messageType)
            append(",from=")
            append(message.from)
            append(",to=")
            append(message.to)
            append(",sendType=")
            append(message.sentTime)
            append(",ttl=")
            append(message.ttl)
            append(",priority=")
            append(messagePriorityToString(message.priority))
            append(",originalPriority=")
            append(messagePriorityToString(message.originalPriority))
            append(",collapseKey=")
            append(message.collapseKey)
            append(",data={")

            pos = length
            message.data.forEach { entry ->
                if (length > pos) {
                    append(',')
                }

                append(entry.key)
                append(':')
                append(entry.value)
            }

            append('}')

            message.notification?.let { notification ->
                append(",notification=(title=")
                notification.title?.let { value ->
                    append(value)
                }
                append(",titleLocalizationKey=")
                notification.titleLocalizationKey?.let { value ->
                    append(value)
                }
                append(",titleLocalizationArgs=")
                notification.titleLocalizationArgs?.let { v ->
                    append(v.joinToString(separator = "=", prefix = "[",
                            postfix = "]"))
                }
                append(",body=")
                notification.body?.let { value ->
                    append(value)
                }
                append(",bodyLocalizationKey=")
                notification.bodyLocalizationKey?.let { value ->
                    append(value)
                }
                append(",bodyLocalizationArgs=")
                notification.bodyLocalizationArgs?.let { v ->
                    append(v.joinToString(separator = "=", prefix = "[",
                            postfix = "]"))
                }
                append(",eventTime=")
                notification.eventTime?.let { value ->
                    append(value)
                }
                append(",channelId=")
                notification.channelId?.let { value ->
                    append(value)
                }
                append(",tag=")
                notification.tag?.let { value ->
                    append(value)
                }
                append(",priority=")
                notification.notificationPriority?.let { value ->
                    append(notificationPriorityToString(value))
                }
                append(",clickAction=")
                notification.clickAction?.let { value ->
                    append(value)
                }
                append(",link=")
                notification.link?.let { value ->
                    append(value)
                }
            }

            append(')')
        }

private fun messagePriorityToString(value: Int): String =
        when (value) {
            RemoteMessage.PRIORITY_HIGH -> "PRIORITY_HIGH"
            RemoteMessage.PRIORITY_NORMAL -> "PRIORITY_NORMAL"
            RemoteMessage.PRIORITY_UNKNOWN -> "PRIORITY_UNKNOWN"
            else -> "N/D"
        }

private fun notificationPriorityToString(value: Int): String {
    val v = NotificationPriority.values()

    return if (value in v.indices) v[value].name else "N/D"
}

private enum class NotificationPriority {
    PRIORITY_UNSPECIFIED,
    PRIORITY_MIN,
    PRIORITY_LOW,
    PRIORITY_DEFAULT,
    PRIORITY_HIGH,
    PRIORITY_MAX,
}
