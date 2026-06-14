package com.library.android.data.remote

import com.library.android.data.remote.dto.BookDto
import com.library.android.domain.model.Book

/** Maps the wire DTO to the domain model. Keeps serialization concerns out of the domain. */
fun BookDto.toDomain(): Book = Book(
    id = id,
    title = title,
    author = author,
    isbn = isbn,
    shelf = shelf,
    row = row,
    available = available,
)
