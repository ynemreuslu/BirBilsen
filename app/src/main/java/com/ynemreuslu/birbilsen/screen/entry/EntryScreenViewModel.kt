package com.ynemreuslu.birbilsen.screen.entry

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.gson.Gson
import com.ynemreuslu.birbilsen.data.Question
import com.ynemreuslu.birbilsen.util.AppConstants.FIREBASE_REFERENCE
import com.ynemreuslu.birbilsen.util.AppConstants.QUESTIONS_CHILD
import com.ynemreuslu.birbilsen.util.AppConstants.QUESTION_CACHE_KEY
import com.ynemreuslu.birbilsen.util.AppConstants.SHARED_PREFERENCES_NAME
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EntryScreenViewModel(
    private val application: Application,
) : ViewModel() {

    private val sharedPreferencesName = SHARED_PREFERENCES_NAME
    private val questionCacheKey = QUESTION_CACHE_KEY

    private val questionsDatabaseReference =
        Firebase.database.reference.child(FIREBASE_REFERENCE)
            .child(QUESTIONS_CHILD)

    private val _questions = MutableLiveData<List<Question>?>()
    val questions: LiveData<List<Question>?> get() = _questions


    init {
        fetchQuestionsFromFirebase()
    }


     fun fetchQuestionsFromFirebase() {
        questionsDatabaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val questionList = ArrayList<Question>()
                for (questionSnapshot in dataSnapshot.children) {
                    val question = questionSnapshot.getValue(Question::class.java)
                    question?.let { questionList.add(it) }
                }
                _questions.value = questionList
                cacheQuestions(questionList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("EntryScreenViewModel", error.message)
            }
        })
    }

    fun cacheQuestions(questionList: List<Question>) {
        val sharedPreferences =
            application.getSharedPreferences(
                sharedPreferencesName,
                Context.MODE_PRIVATE
            )
        with(sharedPreferences.edit()) {
            putString(questionCacheKey, Gson().toJson(questionList))
            apply()
        }
    }

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                return EntryScreenViewModel(application) as T
            }
        }


    }
}