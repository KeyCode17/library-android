package com.library.android.data.remote

import com.library.android.data.remote.dto.NotificationDto
import com.library.android.data.remote.dto.ReminderKindDto
import com.library.android.domain.model.Reminder
import com.library.android.domain.model.ReminderKind

/** Maps notification wire DTOs to domain models. */
fun NotificationDto.toDomain(): Reminder = Reminder(
    id = id,
    userId = userId,
    loanId = loanId,
    kind = kind.toDomain(),
    message = message,
    createdAt = createdAt,
)

fun ReminderKindDto.toDomain(): ReminderKind = when (this) {
    ReminderKindDto.DUE_SOON -> ReminderKind.DUE_SOON
    ReminderKindDto.OVERDUE -> ReminderKind.OVERDUE
}
