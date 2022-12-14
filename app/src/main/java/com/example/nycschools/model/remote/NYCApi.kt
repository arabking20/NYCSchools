package com.example.nycschools.model.remote

import retrofit2.Response
import retrofit2.http.GET

interface NYCApi {

    @GET(END_POINT_SCHOOL)
    suspend fun getRemotSchoolList(): Response<List<SchoolItem>>

    @GET(END_POINT_SAT)
   suspend fun getRemoteSat(): Response<List<NYCSatScore>>

}