package ru.kotofeya.composition.presentation


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import ru.kotofeya.composition.R
import ru.kotofeya.composition.databinding.FragmentGameFinishedBinding
import ru.kotofeya.composition.domain.entity.GameResult


class GameFinishedFragment : Fragment() {

    private var _binding: FragmentGameFinishedBinding? = null
    private val binding: FragmentGameFinishedBinding
        get()= _binding?: throw RuntimeException("FragmentGameFinishedBinding = null")

    private lateinit var gameResult: GameResult


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArguments()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners(){
        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                retryGame()
            }
        }
        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, callback)
        binding.buttonRetry.setOnClickListener {
            retryGame()
        }
    }

    private fun observeViewModel(){
        with(binding){
            emojiResult.setImageResource(getSmileResId())
            tvRequiredAnswers.text = String.format(getString(R.string.required_score),
                gameResult.gameSettings.minCountOfRightAnswers)
            tvScoreAnswers.text = String.format(getString(R.string.score_answers),
                gameResult.countOfRightAnswers)
            tvRequiredPercentage.text = String.format(getString(R.string.required_percentage),
                gameResult.gameSettings.minPercentOfRightAnswers)
            tvScorePercentage.text = String.format(getString(R.string.score_percentage),
                getPercentOfRightAnswers())
        }
    }

    private fun getSmileResId(): Int{
        return if(gameResult.winner){
            R.drawable.ic_smile
        } else {
            R.drawable.ic_sad
        }
    }

    private fun getPercentOfRightAnswers() = with(gameResult){
        if(countOfQuestions == 0){
            0
        } else {
            ((countOfRightAnswers / countOfQuestions.toDouble()) * 100).toInt()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun parseArguments(){
        requireArguments().getParcelable<GameResult>(KEY_GAME_RESULT)?.let{
            gameResult = it
        }
    }

    private fun retryGame(){
        requireActivity().supportFragmentManager
            .popBackStack(GameFragment.NAME, FragmentManager.POP_BACK_STACK_INCLUSIVE)

    }

    companion object {
        private const val KEY_GAME_RESULT = "game_result"
        fun newInstance(gameResult: GameResult): GameFinishedFragment{
            return GameFinishedFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_GAME_RESULT, gameResult)
                }
            }
        }
    }
}