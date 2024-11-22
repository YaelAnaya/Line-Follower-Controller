package yao.ic.linefollower.domain.repository

import dagger.hilt.android.scopes.ViewModelScoped
import yao.ic.linefollower.data.database.dao.DeviceConfigurationDao
import yao.ic.linefollower.data.database.entity.DeviceConfiguration
import javax.inject.Inject

interface DeviceConfigurationRepository {
    fun saveConfiguration(configuration: DeviceConfiguration)
}

@ViewModelScoped
class DeviceConfigurationRepositoryImpl @Inject constructor(
    private val deviceConfigurationDao: DeviceConfigurationDao,
) : DeviceConfigurationRepository {
    override fun saveConfiguration(configuration: DeviceConfiguration) {

    }
}