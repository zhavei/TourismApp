package com.dicoding.tourismapp.core.data.source.remote

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicoding.tourismapp.core.data.source.remote.network.ApiResponse
import com.dicoding.tourismapp.core.data.source.remote.network.ApiService
import com.dicoding.tourismapp.core.data.source.remote.response.ListTourismResponse
import com.dicoding.tourismapp.core.data.source.remote.response.TourismResponse
import com.dicoding.tourismapp.core.utils.JsonHelper
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RemoteDataSource private constructor(private val apiService: ApiService) {
    companion object {
        @Volatile
        private var instance: RemoteDataSource? = null

        fun getInstance(service: ApiService): RemoteDataSource =
            instance ?: synchronized(this) {
                instance ?: RemoteDataSource(service)
            }
    }

    fun getAllTourism(): LiveData<ApiResponse<List<TourismResponse>>> {
        val resultData = MutableLiveData<ApiResponse<List<TourismResponse>>>()

        //region get data from local json
        /*  val handler = Handler(Looper.getMainLooper())
          handler.postDelayed({
              try {
                  val dataArray = jsonHelper.loadData()
                  if (dataArray.isNotEmpty()) {
                      resultData.value = ApiResponse.Success(dataArray)
                  } else {
                      resultData.value = ApiResponse.Empty
                  }
              } catch (e: JSONException){
                  resultData.value = ApiResponse.Error(e.toString())
                  Log.e("RemoteDataSource", e.toString())
              }
          }, 2000)*/
        //endregion

        //CALL NETWORK
        val client = apiService.getList()

        client.enqueue(object : Callback<ListTourismResponse> {
            override fun onResponse(
                call: Call<ListTourismResponse>,
                response: Response<ListTourismResponse>
            ) {


                val dataArray = response.body()?.places
                resultData.value = if (dataArray != null)
                    ApiResponse.Success(dataArray) else ApiResponse.Empty

            }

            override fun onFailure(call: Call<ListTourismResponse>, t: Throwable) {
                resultData.value = ApiResponse.Error(t.message.toString())
                Log.e("remote data source", "onFailure: ${t.message.toString()}")
            }

        })

        return resultData
    }
}

