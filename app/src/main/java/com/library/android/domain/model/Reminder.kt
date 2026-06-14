package com.library.android.domain.model

/** Why a reminder fired (contract: ReminderKind). */
enum class ReminderKind {
    DUE_SOON,
    OVERDUE,
}

/** A logged due-date reminder (contract: Notification). `createdAt` is an ISO-8601 string. */
data class Reminder(
    val id: String,
    val userId: String,
    val loanId: String,
    val kind: ReminderKind,
    val message: String,
    val createdAt: String,
)
