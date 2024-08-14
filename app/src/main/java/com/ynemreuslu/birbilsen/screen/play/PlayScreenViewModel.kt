package com.ynemreuslu.birbilsen.screen.play

import android.app.Application
import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ynemreuslu.birbilsen.data.Question
import com.ynemreuslu.birbilsen.util.AppConstants.FIREBASE_REFERENCE
import com.ynemreuslu.birbilsen.util.AppConstants.QUESTIONS_CHILD
import com.ynemreuslu.birbilsen.util.AppConstants.QUESTION_CACHE_KEY
import com.ynemreuslu.birbilsen.util.AppConstants.SHARED_PREFERENCES_NAME

class PlayViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val sharedPreferencesName = SHARED_PREFERENCES_NAME
    private val questionCacheKey = QUESTION_CACHE_KEY


    private val _questions = MutableLiveData<List<Question>?>()
    val questions: LiveData<List<Question>?> get() = _questions


    private val questionsDatabaseReference =
        Firebase.database.reference.child(FIREBASE_REFERENCE)
            .child(QUESTIONS_CHILD)


    private val _progress = MutableLiveData<Int>()
    val progress: LiveData<Int> get() = _progress

    private var countDownTimer: CountDownTimer? = null
    private var remainingTimeInMillis: Long = 0


    init {
        loadQuestionsFromCacheOrFirebase()
        _progress.value = 20

    }

    fun loadQuestionsFromCacheOrFirebase() {
        val cachedQuestions = getCachedQuestions()
        if (cachedQuestions != null) {
            _questions.value = cachedQuestions
        } else {
            fetchQuestionsFromFirebase()
        }
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
                Log.e("PlayViewModel", error.message)
            }
        })
    }

    fun cacheQuestions(questionList: List<Question>) {
        val sharedPreferences =
            getApplication<Application>().getSharedPreferences(
                sharedPreferencesName,
                Context.MODE_PRIVATE
            )
        with(sharedPreferences.edit()) {
            putString(questionCacheKey, Gson().toJson(questionList))
            apply()
        }
    }

    fun getCachedQuestions(): List<Question>? {
        val sharedPreferences =
            getApplication<Application>().getSharedPreferences(
                sharedPreferencesName,
                Context.MODE_PRIVATE
            )
        val jsonString = sharedPreferences.getString(questionCacheKey, null)
        return jsonString?.let {
            Gson().fromJson(it, object : TypeToken<List<Question>>() {}.type)
        }
    }

    fun startCountdownTimer() {
        countDownTimer = object : CountDownTimer(21000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTimeInMillis = millisUntilFinished
                _progress.value = (millisUntilFinished / 1000).toInt()
            }

            override fun onFinish() {
                _progress.value = 0
            }
        }.start()
    }

    fun startCounterTimer() {
        countDownTimer?.start()

    }

    fun cancelCountdownTimer() {
        countDownTimer?.cancel()
    }


    override fun onCleared() {
        super.onCleared()
        cancelCountdownTimer()
    }
}