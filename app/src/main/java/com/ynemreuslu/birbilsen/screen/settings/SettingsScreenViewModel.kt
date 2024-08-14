package com.ynemreuslu.birbilsen.screen.settings

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.ynemreuslu.birbilsen.data.Voice

class SettingsScreenViewModel : ViewModel() {


    private lateinit var voice: Voice
    private lateinit var sharedPreferences: SharedPreferences


    fun init(
        context: Context,
        activity: Activity,
        sharedPreferences: SharedPreferences
    ) {

        this.sharedPreferences = sharedPreferences
        initVoice(context, activity)
    }

    private fun initVoice(context: Context, activity: Activity) {
        voice = Voice(context, activity)
        switchShared()
    }

    private fun switchShared() {
        val savedVoiceSetting = sharedPreferences.getBoolean("voice_setting", false)
    }



}