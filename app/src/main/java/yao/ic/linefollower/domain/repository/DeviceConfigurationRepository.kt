package yao.ic.linefollower.domain.repository

import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import yao.ic.linefollower.data.database.dao.DeviceConfigurationDao
import yao.ic.linefollower.data.database.entity.DeviceConfiguration
import javax.inject.Inject
import javax.inject.Singleton

interface DeviceConfigurationRepository {
    fun saveConfiguration(configuration: DeviceConfiguration)
    fun getConfiguration(deviceAddress: String): Flow<DeviceConfiguration>
}

@ViewModelScoped
class DeviceConfigurationRepositoryImpl @Inject constructor(
    private val deviceConfigurationDao: DeviceConfigurationDao
) : DeviceConfigurationRepository {
    override fun saveConfiguration(configuration: DeviceConfiguration) {
        deviceConfigurationDao.saveConfiguration(configuration)
    }

    override fun getConfiguration(deviceAddress: String): Flow<DeviceConfiguration> {
        return deviceConfigurationDao.getConfiguration(deviceAddress)
    }
}
