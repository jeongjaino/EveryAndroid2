package com.jaino.cachingpaging.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.jaino.cachingpaging.data.local.BeerDatabase
import com.jaino.cachingpaging.data.local.BeerEntity
import com.jaino.cachingpaging.data.mappers.toBeerEntity
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class BeerRemoteMediator(
    private val beerDb: BeerDatabase,
    private val beerApi: BeerApi
): RemoteMediator<Int, BeerEntity>(){

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, BeerEntity>
    ): MediatorResult {
        return try{
            val loadKey = when(loadType){
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if(lastItem == null){ // 남은 항목이 없는 경우
                        1
                    }else{ // 남은 항목이 있는 경우 : 현재 id 나누기 페이지당 들어갈 항목의 갯수 + 1
                        (lastItem.id / state.config.pageSize) + 1
                    }
                }
                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )
                LoadType.REFRESH -> 1 //
            }

            delay(2000L)
            // call api
            val beers = beerApi.getBeers(
                page = loadKey,
                pageCount = state.config.pageSize
            )

            beerDb.withTransaction {
                if(loadType == LoadType.REFRESH) { // Refresh 일 경우,
                    beerDb.dao.clearAll() // clear cache
                }
                val beerEntities = beers.map{ it.toBeerEntity() }
                beerDb.dao.upsertAll(beerEntities)
            }

            // page load finished
            MediatorResult.Success(
                endOfPaginationReached = beers.isEmpty()
            )
        } catch(e: IOException){
            MediatorResult.Error(e)
        } catch (e : HttpException){
            MediatorResult.Error(e)
        }
    }
}