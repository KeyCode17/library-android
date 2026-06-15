package com.library.android.data.remote

import com.library.android.data.remote.dto.PrincipalDto
import com.library.android.data.remote.dto.RoleDto
import com.library.android.data.remote.dto.UserSummaryDto
import com.library.android.domain.model.Principal
import com.library.android.domain.model.Role
import com.library.android.domain.model.UserSummary

/** Maps IAM wire DTOs to domain models. */
fun PrincipalDto.toDomain(): Principal =
    Principal(id = id, email = email, role = role.toDomain(), verified = verified, active = active)

fun UserSummaryDto.toDomain(): UserSummary = UserSummary(
    id = id,
    email = email,
    role = role.toDomain(),
    verified = verified,
    active = active,
    createdAt = createdAt,
)

fun RoleDto.toDomain(): Role = when (this) {
    RoleDto.ADMIN -> Role.ADMIN
    RoleDto.LIBRARIAN -> Role.LIBRARIAN
    RoleDto.MEMBER -> Role.MEMBER
}

fun Role.toDto(): RoleDto = when (this) {
    Role.ADMIN -> RoleDto.ADMIN
    Role.LIBRARIAN -> RoleDto.LIBRARIAN
    Role.MEMBER -> RoleDto.MEMBER
}
