package com.dicoding.tourismapp.core.di

//not using this class since using depedency injection its been handled by koin
/*object Injection {
    fun provideRepository(context: Context): ITourismRepository {
        val database = TourismDatabase.getInstance(context)

        val remoteDataSource = RemoteDataSource.getInstance(ApiConfig.provideApiService())
        val localDataSource = LocalDataSource.getInstance(database.tourismDao())
        val appExecutors = AppExecutors()

        return TourismRepository.getInstance(remoteDataSource, localDataSource, appExecutors)
    }

    fun provideTourismUseCase(context: Context) : TourismUseCase {
        val repository = provideRepository(context)
        return TourismInteractor(repository)
    }
}*/
