package com.example.stockmarket.data.repository

import com.example.stockmarket.data.csv.CSVParser
import com.example.stockmarket.data.local.StockDatabase
import com.example.stockmarket.data.mapper.toCompanyInfo
import com.example.stockmarket.data.mapper.toCompanyListing
import com.example.stockmarket.data.mapper.toCompanyListingEntity
import com.example.stockmarket.data.remote.StockApi
import com.example.stockmarket.domain.model.CompanyInfo
import com.example.stockmarket.domain.model.CompanyListing
import com.example.stockmarket.domain.model.IntradayInfo
import com.example.stockmarket.domain.repository.StockRepository
import com.example.stockmarket.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val api: StockApi,
    private val db: StockDatabase,
    private val companyListingsParser: CSVParser<CompanyListing>,
    private val intradayInfoParse: CSVParser<IntradayInfo>
): StockRepository{

    private val dao = db.dao

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String,
    ): Flow<Resource<List<CompanyListing>>> {
        return flow{
            emit(Resource.Loading(true))
            val localListings = dao.searchCompanyListing(query)
            emit(Resource.Success(
                data = localListings.map{
                    it.toCompanyListing()
                }
            ))

            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote

            if(shouldJustLoadFromCache){
                emit(Resource.Loading(false))
                return@flow
            }
            val remoteListings = try{
                val response = api.getListings()
                companyListingsParser.parse(response.byteStream()) //csv 파일 받아오기
            }catch(e: IOException){
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data "))
                null
            }catch (e: HttpException){
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data "))
                null
            }

            remoteListings?.let{ listings ->
                dao.clearCompanyListings() //clear cache
                dao.insertCompanyListing(
                    listings.map {
                        it.toCompanyListingEntity()
                    }
                )
                emit(Resource.Success( //Single Source -> remote list가 아닌 cache list 가져오기
                    data = dao.searchCompanyListing("")
                        .map{
                            it.toCompanyListing()
                        }
                ))
                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun getIntradayInfo(symbol: String): Resource<List<IntradayInfo>> {
        return try{
            val response = api.getIntradayInfo(symbol = symbol)
            val results = intradayInfoParse.parse(response.byteStream())
            Resource.Success(results)
        }catch(e: IOException){
            e.printStackTrace()
            Resource.Error("Couldn't load intraday info")
        }catch(e: HttpException){
            e.printStackTrace()
            Resource.Error("Couldn't load intraday info")
        }
    }

    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> {
        return try{
            val result = api.getCompanyInfo(symbol = symbol)
            Resource.Success(result.toCompanyInfo())
        }catch(e: IOException){
            e.printStackTrace()
            Resource.Error("Couldn't load company info")
        }catch(e: HttpException){
            e.printStackTrace()
            Resource.Error("Couldn't load company info")
        }
    }
}