package com.jaino.cachingpaging.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface BeerDao {

    // insert or update 존재하는 항목만 업데이트, 존재하지 않는 항목은 삽입
    @Upsert
    suspend fun upsertAll(beers: List<BeerEntity>)

    @Query("SELECT * FROM beerentity")
    fun pagingSource(): PagingSource<Int, BeerEntity> // page, page content

    @Query("DELETE FROM beerentity")
    suspend fun clearAll()
}