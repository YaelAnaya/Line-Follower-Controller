package yao.ic.linefollower.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import yao.ic.linefollower.data.database.AppDatabase
import yao.ic.linefollower.domain.repository.DeviceConfigurationRepository
import yao.ic.linefollower.domain.repository.DeviceConfigurationRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object  DataModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ) : AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            AppDatabase.DB_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideDeviceConfigurationDao(appDatabase: AppDatabase) =
        appDatabase.bleConfigurationDao()

}

@Module
@InstallIn(ViewModelComponent::class)
internal abstract class RepositoryModule {
    @Binds
    abstract fun bindDeviceConfigurationRepository(
        deviceConfigurationRepositoryImpl: DeviceConfigurationRepositoryImpl
    ): DeviceConfigurationRepository
}