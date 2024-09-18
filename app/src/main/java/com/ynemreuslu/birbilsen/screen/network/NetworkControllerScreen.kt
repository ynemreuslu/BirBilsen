package com.ynemreuslu.birbilsen.screen.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ynemreuslu.birbilsen.R
import com.ynemreuslu.birbilsen.databinding.FragmentNetworkControllerScreenBinding
import com.ynemreuslu.birbilsen.screen.entry.EntryScreen
import com.ynemreuslu.birbilsen.screen.entry.EntryScreenViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class NetworkControllerScreen : Fragment() {

    private var _binding: FragmentNetworkControllerScreenBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NetworkControllerViewModel by viewModels { NetworkControllerViewModel.Factory }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNetworkControllerScreenBinding.inflate(inflater, container, false)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity()
            }
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isNetworkAvailable.observe(viewLifecycleOwner) { isConnection ->
                if (isConnection)
                    findNavController().navigate(R.id.action_networkControllerScreen_to_entryScreen)

            }
        }



    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}