package com.example.melon

import com.example.melon.service.MusicEntity

//entitiy를 모델로 mapping하는 extension

fun MusicEntity.mapper(id: Long): MusicModel =
    MusicModel(
        id = id,
        streamUrl = streamUrl,
        coverUrl = coverUrl,
        track = track,
        artist = artist
    )