package com.guanyc.stock.discipline.domain.model

import com.guanyc.stock.discipline.presentation.main.components.TabEntity
import java.util.UUID

open class Metadata(
    val id: UUID = UUID.randomUUID(),
    val title: String = "",
    val description: String = "",
    val note: String = "",
    val enabled: Boolean = true,
    val dispalyInWatchList: Boolean = true,
) {
    override fun hashCode(): Int {
        return title.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Metadata) return false

        if (id != other.id) return false
        if (title != other.title) return false
        if (description != other.description) return false
        if (note != other.note) return false

        return true
    }

}




