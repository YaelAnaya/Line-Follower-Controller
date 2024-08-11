package yao.ic.linefollower.di

import android.content.Context
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.companion.CompanionDeviceManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import yao.ic.linefollower.domain.ble.BLEController
import yao.ic.linefollower.domain.ble.BLEControllerImpl
import yao.ic.linefollower.domain.ble.BluetoothStateReceiver
import javax.inject.Singleton

@Module
@InstallIn(ActivityRetainedComponent::class)
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

    @Provides
    fun provideBluetoothStateReceiver(
        @Singleton adapter: BluetoothAdapter
    ): BluetoothStateReceiver = BluetoothStateReceiver(adapter = adapter)

}

@Module
@InstallIn(SingletonComponent::class)
internal abstract class BLEControllerModule {

    @Singleton
    @Binds
    abstract fun bindBLEController(controller: BLEControllerImpl): BLEController
}



