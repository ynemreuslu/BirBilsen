package com.ynemreuslu.birbilsen.screen.play

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.ynemreuslu.birbilsen.R
import com.ynemreuslu.birbilsen.data.Question
import com.ynemreuslu.birbilsen.data.Voice
import com.ynemreuslu.birbilsen.databinding.FragmentPlayScreenBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random


class PlayScreen : Fragment() {

    private var _binding: FragmentPlayScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var playViewModel: PlayViewModel
    private lateinit var correctAnswer: String
    private lateinit var answerButtons: Array<Button>
    private lateinit var voice: Voice
    private var currentQuestionIndex: Int = Random.nextInt(0, 598)
    private val askedQuestions = HashSet<Int>()
    private lateinit var sharedPref: SharedPreferences
    private var mInterstitialAd: InterstitialAd? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        playViewModel.startCounterTimer()
    }

    override fun onStop() {
        super.onStop()
        playViewModel.cancelCountdownTimer()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        voice = Voice(requireContext(), requireActivity())
        playViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[PlayViewModel::class.java]

        playViewModel.questions.observe(viewLifecycleOwner) {
            initializeGame(it!!)
        }
        playViewModel.progress.observe(viewLifecycleOwner) {
            binding.playScreenProgressBar.progress = it
            binding.playScreenProgressBarTextView.text = it.toString()
            backToEntry(it)
        }
        sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)

        initAnswerButtons()
        setBackButtonClickListener()
        playViewModel.startCountdownTimer()
        MobileAds.initialize(requireContext())


    }


    private fun updateUIWithCurrentQuestion(index: Int, questions: List<Question>) {
        binding.playScreenQuestionsText.text = questions[index].question
        binding.playScreenCategoryText.text = questions[index].category
        correctAnswer = questions[index].correctAnswer.toString()
        val answers = questions[index].anwers?.split(",")?.shuffled()
        answers?.forEachIndexed { i, answer -> answerButtons[i].text = answer }
    }

    private fun handleButtonCorrectAnswer() {
        do {
            currentQuestionIndex = Random.nextInt(0, 598)
        } while (askedQuestions.contains(currentQuestionIndex))

        askedQuestions.add(currentQuestionIndex)

        val questions = playViewModel.questions.value
        if (currentQuestionIndex < questions!!.size) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(500)
                updateUIWithCurrentQuestion(currentQuestionIndex, questions)
                resetAnswerButtonsColors()
            }
        }
    }

    private fun loadAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            requireContext(),
            "ca-app-pub-5990820577037460/8549360627",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    mInterstitialAd = null
                }

                override fun onAdLoaded(p0: InterstitialAd) {
                    super.onAdLoaded(p0)
                    mInterstitialAd = p0

                }
            })
    }

    private fun showInterstitial() {
        playViewModel.cancelCountdownTimer()
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        mInterstitialAd = null
                        loadAd()
                        playViewModel.startCounterTimer()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        mInterstitialAd = null
                        playViewModel.startCounterTimer()
                    }

                    override fun onAdShowedFullScreenContent() {
                        // Called when ad is dismissed.
                    }
                }
            mInterstitialAd?.show(requireActivity())
        }
    }




    private fun resetAnswerButtonsColors() {
        for (button in answerButtons) {
            button.setBackgroundColor(resetButtonBackgroundColor())
            button.isClickable = true
        }
    }

    private fun setAnswerButtonsClickable(clickable: Boolean) {
        for (button in answerButtons) {
            button.isClickable = clickable
        }
    }

    private fun processCurrentAnswer(index: Int) {
        if (answerButtons[index].text == correctAnswer) {
            answerButtons[index].setBackgroundColor(correctAnswerTrueColor())
            handleButtonCorrectAnswer()
            playViewModel.cancelCountdownTimer()
            playViewModel.startCountdownTimer()
            trueVoice()
            setAnswerButtonsClickable(false)
        } else {
            answerButtons[index].setBackgroundColor(correctAnswerFalseColor())
            setBackButtonClickListener()
            continueWithTimer()
            currentAnswer()
            playViewModel.cancelCountdownTimer()
            playViewModel.startCountdownTimer()
            falseVoice()
            handleButtonCorrectAnswer()
        }


    }

    private fun continueWithTimer() {
        playViewModel.cancelCountdownTimer()
        playViewModel.startCountdownTimer()
    }

    private fun currentAnswer() {
        for (button in answerButtons) {
            if (button.text == correctAnswer) {
                button.setBackgroundColor(correctAnswerTrueAwaitColor())
            }
        }
    }

    private fun setAnswerButtonsListeners() {
        for ((index, button) in answerButtons.withIndex()) {
            button.setOnClickListener {
                onAnswerButtonClick(index)
                setAnswerButtonsClickable(false)
                CoroutineScope(Dispatchers.Main).launch { delay(1000); processCurrentAnswer(index) }
            }
        }
    }

    private fun onAnswerButtonClick(index: Int) {
        answerButtons[index].isClickable = false
        CoroutineScope(Dispatchers.Main).launch {
            answerButtons[index].setBackgroundColor(buttonClickAwaitColor())
            delay(100000)
        }
    }

    private fun initializeGame(questions: List<Question>) {
        updateUIWithCurrentQuestion(currentQuestionIndex, questions)
        setAnswerButtonsListeners()
    }

    private fun backToNavigate() {
        findNavController().navigateUp()
    }

    private fun backToEntry(index: Int) {
        if (index == 0) {
            timeFinish()
            playViewModel.cancelCountdownTimer()
            timeFinishCount()
            timerFinishVoice()
        }
    }

    private fun timeFinish() {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(250)
            handleButtonCorrectAnswer()
            timeFinishCorrectAnswer()
        }
    }

    private fun timeFinishCount() {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(1000)
            playViewModel.startCountdownTimer()
            playViewModel.getCachedQuestions()

        }
    }

    private fun timeFinishCorrectAnswer() {
        for (button in answerButtons) {
            if (button.text == correctAnswer) {
                button.setBackgroundColor(correctAnswerTrueColor())
            }
        }
    }

    private fun setBackButtonClickListener() {
        binding.playScreenBackButton.setOnClickListener {
            backToNavigate()
        }
    }

    private fun initAnswerButtons() {
        answerButtons = arrayOf(
            binding.playScreenQuestionsFirstButton,
            binding.playScreenQuestionsSecondButton,
            binding.playScreenQuestionsThirdButton,
            binding.playQuestionsFourthButton,
            binding.playScreenQuestionsFifthButton
        )
    }

    private fun correctAnswerTrueColor(): Int {
        return ContextCompat.getColor(requireContext(), R.color.green)
    }

    private fun correctAnswerTrueAwaitColor(): Int {
        return ContextCompat.getColor(requireContext(), R.color.blue)
    }

    private fun correctAnswerFalseColor(): Int {
        return ContextCompat.getColor(requireContext(), R.color.md_theme_light_error)
    }

    private fun resetButtonBackgroundColor(): Int {
        return ContextCompat.getColor(requireContext(), R.color.md_theme_dark_onPrimaryContainer)
    }

    private fun buttonClickAwaitColor(): Int {
        return ContextCompat.getColor(requireContext(), R.color.ic_launcher_background)
    }

    private fun trueVoice() {
        val trueId = R.raw.current_true
        val trueMediaPlayer = MediaPlayer.create(requireContext(), trueId)

        val isVoiceEnabled = sharedPref.getBoolean("voice", false)

        if (isVoiceEnabled) trueMediaPlayer.start()
        else {
            trueMediaPlayer.pause()
            trueMediaPlayer.release()
        }
    }

    private fun falseVoice() {
        val falseId = R.raw.current_false
        val falseMediaPlayer = MediaPlayer.create(requireContext(), falseId)

        val isVoiceEnabled = sharedPref.getBoolean("voice", false)

        if (isVoiceEnabled) falseMediaPlayer.start()
        else {
            falseMediaPlayer.pause()
            falseMediaPlayer.release()
        }
    }

    private fun timerFinishVoice() {
        val timerId = R.raw.timer_finish
        val timerFinish = MediaPlayer.create(requireContext(), timerId)

        val isVoiceEnabled = sharedPref.getBoolean("voice", false)

        if (isVoiceEnabled) timerFinish.start()
        else {
            timerFinish.stop()
            timerFinish.release()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}