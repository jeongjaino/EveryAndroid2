package com.jaino.cachingpaging.data.mappers

import com.jaino.cachingpaging.data.local.BeerEntity
import com.jaino.cachingpaging.data.remote.BeerDto
import com.jaino.cachingpaging.domain.Beer

fun BeerDto.toBeerEntity(): BeerEntity{
    return BeerEntity(
        id = id,
        name = name,
        tagline = tagline,
        description = description,
        firstBrewed = first_brewed,
        imageUrl = image_url
    )
}

fun BeerEntity.toBeer(): Beer {
    return Beer(
        id = id,
        name = name,
        tagline = tagline,
        description = description,
        firstBrewed = firstBrewed,
        imageUrl = imageUrl
    )
}