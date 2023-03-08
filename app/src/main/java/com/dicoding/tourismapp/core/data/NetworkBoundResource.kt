package com.dicoding.tourismapp.core.data

import com.dicoding.tourismapp.core.data.source.remote.network.ApiResponse
import kotlinx.coroutines.flow.*

abstract class NetworkBoundResource<ResultType, RequestType> {

    //non flow
    /*private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.Loading(null)

        @Suppress("LeakingThis")
        val dbSource = loadFromDB()

        result.addSource(dbSource) { data ->
            result.removeSource(dbSource)
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource)
            } else {
                result.addSource(dbSource) { newData ->
                    result.value = Resource.Success(newData)
                }
            }
        }
    }*/

    private var result: Flow<Resource<ResultType>> = flow {

        emit(Resource.Loading())

        val dbSource = loadFromDB().first()
        if (shouldFetch(dbSource)) {
            emit(Resource.Loading())
            when (val apiResponse = createCall().first()) {
                is ApiResponse.Success -> {
                    saveCallResult(apiResponse.data)
                    emitAll(loadFromDB().map { Resource.Success(it) })
                }
                is ApiResponse.Empty -> {
                    emitAll(loadFromDB().map { Resource.Success(it) })
                }
                is ApiResponse.Error -> {
                    onFetchFailed()
                    emit(Resource.Error<ResultType>(apiResponse.errorMessage))
                }
            }
        } else {
            emitAll(loadFromDB().map { Resource.Success(it) })
        }
    }

    protected open fun onFetchFailed() {}

    protected abstract fun loadFromDB(): Flow<ResultType>

    protected abstract fun shouldFetch(data: ResultType?): Boolean

    protected abstract suspend fun createCall(): Flow<ApiResponse<RequestType>>

    protected abstract suspend fun saveCallResult(data: RequestType)

    //region use live data
    /*private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {

        val apiResponse = createCall()

        result.addSource(dbSource) { newData ->
            result.value = Resource.Loading(newData)
        }
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)
            when (response) {
                is ApiResponse.Success ->
                    mExecutors.diskIO().execute {
                        saveCallResult(response.data)
                        mExecutors.mainThread().execute {
                            result.addSource(loadFromDB()) { newData ->
                                result.value = Resource.Success(newData)
                            }
                        }
                    }
                is ApiResponse.Empty -> mExecutors.mainThread().execute {
                    result.addSource(loadFromDB()) { newData ->
                        result.value = Resource.Success(newData)
                    }
                }
                is ApiResponse.Error -> {
                    onFetchFailed()
                    result.addSource(dbSource) { newData ->
                        result.value = Resource.Error(response.errorMessage, newData)
                    }
                }
            }
        }
    }*/
    //endregion

    fun asFlow(): Flow<Resource<ResultType>> = result
    /*fun asLiveData(): LiveData<Resource<ResultType>> = result*/
}