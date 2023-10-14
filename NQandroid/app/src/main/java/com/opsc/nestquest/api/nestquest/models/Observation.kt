package com.opsc.nestquest.api.nestquest.models

import java.time.LocalDate

data class Observation(
    var birdSightingId: String?,
    var userId: String?,
    var birdId: String?,
    var dateSeen: LocalDate,
    var coordinates: String?,
    var pictures: List<Picture>?
)

