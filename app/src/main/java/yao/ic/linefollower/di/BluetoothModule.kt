package yao.ic.linefollower.di

import android.content.Context
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.companion.CompanionDeviceManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import yao.ic.linefollower.domain.ble.BLEManager
import yao.ic.linefollower.domain.ble.BLEManagerImpl
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object BluetoothModule {
    @Provides
    fun provideBluetoothAdapter(@ApplicationContext context: Context): BluetoothAdapter {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        return bluetoothManager.adapter
    }

    @Provides
    fun provideBluetoothManager(@ApplicationContext context: Context): BluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    @Provides
    fun provideCompanionDeviceManager(@ApplicationContext context: Context): CompanionDeviceManager =
        context.getSystemService(Context.COMPANION_DEVICE_SERVICE) as CompanionDeviceManager

}

@Module
@InstallIn(SingletonComponent::class)
internal abstract class BLEManagerModule {

    @Singleton
    @Binds
    abstract fun bindBLEManager(controller: BLEManagerImpl): BLEManager

    companion object {
        @Provides
        fun provideCoroutineScope(): CoroutineScope = CoroutineScope(Dispatchers.IO)
    }
}



