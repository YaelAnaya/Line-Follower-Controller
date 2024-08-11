package yao.ic.linefollower.utils

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi

object Permissions {

    @RequiresApi(Build.VERSION_CODES.S)
    private val android12Permissions =
        listOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN)

    @RequiresApi(Build.VERSION_CODES.Q)
    private val android10Permissions = listOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)

    private val legacyPermissions =
        listOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN)
    private val locationPermissions = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

    val permissions by lazy {
        mutableListOf<String>().apply {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    addAll(android12Permissions)
                }
                Build.VERSION.SDK_INT == Build.VERSION_CODES.O -> {
                    addAll(locationPermissions)
                }
                else -> {
                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) addAll(android10Permissions)
                    addAll(legacyPermissions)
                    addAll(locationPermissions)
                }
            }
        }.toList()
    }

}