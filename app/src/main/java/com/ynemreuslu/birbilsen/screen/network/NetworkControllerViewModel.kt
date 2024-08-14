package com.ynemreuslu.birbilsen.screen.network

import android.net.ConnectivityManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewmodel.CreationExtras
import com.ynemreuslu.birbilsen.screen.entry.EntryScreenViewModel


class NetworkControllerViewModel(private val connectivityRepository: ConnectivityRepository) :
    ViewModel() {
    val isNetworkAvailable = connectivityRepository.isConnected.asLiveData()

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                val connectivityRepository =
                    ConnectivityRepository(application.baseContext)
                return NetworkControllerViewModel(connectivityRepository) as T
            }
        }


    }
}