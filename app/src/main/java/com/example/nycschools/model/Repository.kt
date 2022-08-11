package com.example.nycschools.model

import com.example.nycschools.model.local.NYCdao
import com.example.nycschools.model.local.SatEntity
import com.example.nycschools.model.local.SchoolEntity
import com.example.nycschools.model.local.SchoolSatEntity
import com.example.nycschools.model.remote.NYCSatScore
import com.example.nycschools.model.remote.Network
import com.example.nycschools.model.remote.SchoolItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class Repository(
    private val nyc: NYCdao,
    private val network: Network,
    private val scope: CoroutineScope
) {

    // fetch school list
    // fetch school sat
    // merge both list
    // insert school entity
    // inset school sat
    // insert the merge

    private val merge = mutableListOf<SchoolSatEntity>()

    /**
     * Coroutine builders
     * CoroutineScope.launch.- will return a job, the task that
     * will be executed. similar to "runnable".
     *
     * Example: Network call (independent), DB transactions,
     * upload data to the server
     * CoroutineScope.async.- Return Deferred<T>, to unwrapped
     * the value, use await ().
     * Example: Network call (sequential/parallel)
     */

    init {

        scope.launch {

            val remoteSchoolList = network.service
                .getRemotSchoolList()

            val remoteSat = network.service
                .getRemoteSat()
            //MERGE


            if (remoteSchoolList.isSuccessful && remoteSat.isSuccessful) {
                remoteSchoolList.body()?.let { schoolList ->
                    remoteSat.body()?.let { satScore ->
                        for (i in 0 until schoolList.size) {
                            for (j in 0 until satScore.size) {
                                if (schoolList[i].dbn == satScore[j].dbn) {
                                    val schoolSat = SchoolSatEntity(
                                        schoolList[i].dbn,
                                        schoolList[i].school_name,
                                        satScore[j].satTakers,
                                        satScore[j].reading,
                                        satScore[j].math,
                                        satScore[j].writing,
                                        schoolList[i].location,
                                        schoolList[i].phone_number,
                                        schoolList[i].fax_number,
                                        schoolList[i].city,
                                        schoolList[i].latitude,
                                        schoolList[i].longitude,
                                    )
                                    merge.add(schoolSat)
                                }
                            }

                        }
                        insertToLocal(schoolList, satScore)
                    }
                }
            }

        }


    }

    private fun insertToLocal(schoolList: List<SchoolItem>, satScore: List<NYCSatScore>) {
        scope.launch {
            satScore.forEach {
                nyc.fetchSatList(it.toSatEntity())
            }
            schoolList.map {
                it.toSchoolEntity()
            }.forEach {
                nyc.fetchSchoolList(it)
            }
        }
    }


    private fun NYCSatScore.toSatEntity(): SatEntity {
        return SatEntity(
            this.dbn,
            this.school_name,
            this.satTakers,
            this.reading,
            this.math,
            this.writing
        )

    }

    private fun SchoolItem.toSchoolEntity(): SchoolEntity {
        return SchoolEntity(
            1, this.dbn, this.school_name, this.location, this.phone_number, this.fax_number,
            this.city, this.latitude, this.longitude
        )


    }
}

