package com.example.stockmarket.data.repository

import com.example.stockmarket.data.csv.CSVParser
import com.example.stockmarket.data.csv.CompanyListingsParser
import com.example.stockmarket.data.local.StockDatabase
import com.example.stockmarket.data.mapper.toCompanyListing
import com.example.stockmarket.data.mapper.toCompanyListingEntity
import com.example.stockmarket.data.remote.dto.StockApi
import com.example.stockmarket.domain.model.CompanyListing
import com.example.stockmarket.domain.repository.StockRepository
import com.example.stockmarket.util.Resource
import com.opencsv.CSVReader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOError
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    val api: StockApi,
    val db: StockDatabase,
    val companyListingsParser: CSVParser<CompanyListing>
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
}