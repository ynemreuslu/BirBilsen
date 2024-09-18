package com.ynemreuslu.birbilsen.screen.settings

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import com.google.android.play.core.review.testing.FakeReviewManager
import com.ynemreuslu.birbilsen.databinding.FragmentSettingsScreenBinding


class SettingsScreen : Fragment() {

    private var _binding: FragmentSettingsScreenBinding? = null
    private val binding get() = _binding!!
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



        sharedPreferences =
            requireActivity().getSharedPreferences("app_settings", Context.MODE_PRIVATE)

        initAdView()
        switchOnVoice()

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
                putBoolean("voice_setting", isChecked)
                commit()
            }
        }
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


    private fun setupShareButton() {
        binding.settingsScreenShare.setOnClickListener {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.ynemreuslu.birbilsen")
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }

    private fun setupReviewButton() {
        binding.settingsScreenPoint.setOnClickListener {
            val manager = ReviewManagerFactory.create(requireContext())
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo = task.result
                    val flow = manager.launchReviewFlow(requireActivity(), reviewInfo)
                    flow.addOnCompleteListener { /* Handle completion */ }
                } else {
                    // Handle error if necessary
                    val reviewErrorCode = (task.exception as? ReviewException)?.errorCode
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}