package com.guanyc.stock.discipline.domain.model

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation

data class StockNoteWithTargetLists(

    @Embedded
    val note: StockNote,

    @Relation(
        parentColumn = "stockNoteId",
        entityColumn = "stockNoteId"
    )

    var stockTargets: List<StockTarget>

)
