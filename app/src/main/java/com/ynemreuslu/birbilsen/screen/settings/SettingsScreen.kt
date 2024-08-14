package com.ynemreuslu.birbilsen.screen.settings

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.ynemreuslu.birbilsen.databinding.FragmentSettingsScreenBinding



class SettingsScreen : Fragment() {


    private var _binding: FragmentSettingsScreenBinding? = null
    private val binding get() = _binding!!
    private val settingsViewModel: SettingsScreenViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsScreenBinding.inflate(inflater, container, false)
        MobileAds.initialize(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsViewModel.init(
            requireContext(),
            requireActivity(),
            requireActivity().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        )
        sharedPreferences =
            requireActivity().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        initAdView()
        switchOnVoice()
        backButton()
    }

    private fun initAdView() {
        val adRequest = AdRequest.Builder().build()
        binding.settingsFragmentAd.loadAd(adRequest)
    }

    private fun switchOnVoice() {
        binding.settingsScreenVoice.setOnCheckedChangeListener { _, isChecked ->
            updateVoiceSetting(isChecked)

            val sharedPref = requireActivity().getSharedPreferences("app_settings", MODE_PRIVATE)
            with(sharedPref.edit()) {
                putBoolean(
                    "voice_setting", isChecked
                )
                commit()
            }
        }
        val sharedPref = requireActivity().getSharedPreferences("app_settings", MODE_PRIVATE)
        val savedVoiceSetting = sharedPref.getBoolean("voice_setting", false)
        binding.settingsScreenVoice.isChecked = savedVoiceSetting

    }


    private fun switchShared() {
        val sharedPref = requireActivity().getSharedPreferences("app_settings", MODE_PRIVATE)
        val savedVoiceSetting = sharedPref.getBoolean("voice_setting", false)
        binding.settingsScreenVoice.isChecked = savedVoiceSetting
    }


    private fun updateVoiceSetting(isVoiceEnabled: Boolean) {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putBoolean("voice", isVoiceEnabled)
            commit()
        }

    }


    private fun backButton() {
        binding.settingsBackButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun sharedFriendsLinkApp() {
        binding.settingsScreenShare.setOnClickListener {
            TODO()
        }
    }

//    private fun appLinksButtons() {
//        binding.settingsPoint.setOnClickListener {
//            val manager = ReviewManagerFactory.create(requireContext())
//            binding.settingsPoint.setOnClickListener {
//                val request = manager.requestReviewFlow()
//                request.addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        // We got the ReviewInfo object
//                        val reviewInfo = task.result
//                    } else {
//                        // There was some problem, log or handle the error code.
//                        @ReviewErrorCode val reviewErrorCode = (task.getException() as ReviewException).errorCode
//                    }
//                }
//            }
//
//
//
//        }
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
