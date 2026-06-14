package com.library.android.data.remote

import com.library.android.data.remote.dto.PrincipalDto
import com.library.android.data.remote.dto.RoleDto
import com.library.android.domain.model.Principal
import com.library.android.domain.model.Role

/** Maps IAM wire DTOs to domain models. */
fun PrincipalDto.toDomain(): Principal = Principal(id = id, email = email, role = role.toDomain())

fun RoleDto.toDomain(): Role = when (this) {
    RoleDto.ADMIN -> Role.ADMIN
    RoleDto.LIBRARIAN -> Role.LIBRARIAN
    RoleDto.MEMBER -> Role.MEMBER
}
