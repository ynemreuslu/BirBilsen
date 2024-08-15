package com.ynemreuslu.birbilsen.screen.entry


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.ynemreuslu.birbilsen.R
import com.ynemreuslu.birbilsen.databinding.FragmentEntryScreenBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class EntryScreen : Fragment() {
    private var _binding: FragmentEntryScreenBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EntryScreenViewModel by viewModels { EntryScreenViewModel.Factory }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEntryScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setButtonListeners()
        viewModel.fetchQuestionsFromFirebase()
        loadBannerAd()

    }

    private fun loadBannerAd() {
        MobileAds.initialize(requireContext())
        val adRequest = AdRequest.Builder().build()
        binding.entryScreenAdView.loadAd(adRequest)
    }

    private fun setButtonListeners() {
        binding.entryScreenPlayButton.setOnClickListener {
            navigateToPlayFragment()
        }
        binding.entryScreenSettingsButton.setOnClickListener {
            navigateToSettingsFragment()


        }
    }


    private fun navigateToPlayFragment() =
        findNavController().navigate(R.id.action_entryScreen_to_playScreen)

    private fun navigateToSettingsFragment() =
        findNavController().navigate(R.id.action_entryScreen_to_settingsScreen)


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
