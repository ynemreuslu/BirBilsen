package com.ynemreuslu.birbilsen.screen.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow


class ConnectivityRepository(context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val _isConnected = MutableStateFlow(false)
    val isConnected: Flow<Boolean> = _isConnected


    init {
        connectivityManager.registerDefaultNetworkCallback(object :
            ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: android.net.Network) {
                _isConnected.value = true

            }

            override fun onLost(network: android.net.Network) {
                _isConnected.value = false
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {


                val isWifi = networkCapabilities.hasTransport(TRANSPORT_WIFI)

                val hasInternet = networkCapabilities.hasCapability(NET_CAPABILITY_INTERNET) &&
                        networkCapabilities.hasCapability(NET_CAPABILITY_VALIDATED)
//                val isConnected = isWifi && hasInternet

                _isConnected.value = hasInternet
            }
        })
    }
}