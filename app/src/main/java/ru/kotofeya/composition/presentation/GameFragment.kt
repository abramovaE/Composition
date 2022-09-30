package ru.kotofeya.composition.presentation

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.kotofeya.composition.R
import ru.kotofeya.composition.databinding.FragmentGameBinding
import ru.kotofeya.composition.domain.entity.GameResult
import ru.kotofeya.composition.domain.entity.Level
import ru.kotofeya.composition.domain.entity.Question

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get()= _binding?: throw RuntimeException("FragmentGameBinding = null")

    private lateinit var level: Level
    private val viewModel: GameViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[GameViewModel::class.java]
    }

    private val viewModelFactory by lazy {
        GameViewModelFactory(level, requireActivity().application)
    }


    private val tvOptions by lazy {
        mutableListOf<TextView>().apply {
            add(binding.tvOption1)
            add(binding.tvOption2)
            add(binding.tvOption3)
            add(binding.tvOption4)
            add(binding.tvOption5)
            add(binding.tvOption6)
        }
    }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            parseArgs()
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            _binding = FragmentGameBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            observeViewModel()
            setClickListenersToOptions()

        }

    private fun setClickListenersToOptions() {
        for (tvOption in tvOptions) {
            tvOption.setOnClickListener {
                viewModel.chooseAnswer(tvOption.text.toString().toInt())
            }
        }
    }

        private fun observeViewModel(){
            viewModel.formattedTime.observe(viewLifecycleOwner, this::updateTime)
            viewModel.question.observe(viewLifecycleOwner, this::updateQuestion)
            viewModel.minPercent.observe(viewLifecycleOwner, {
                binding.progressBar.secondaryProgress = it
            })
            viewModel.percentOfRightAnswers.observe(viewLifecycleOwner,
                this::updateProgressBar)
            viewModel.enoughCountOfRightAnswers.observe(viewLifecycleOwner,
                this::updateEnoughCount)
            viewModel.enoughCountOfRightAnswers.observe(viewLifecycleOwner,
                this::updateEnoughPercent)
            viewModel.gameResult.observe(viewLifecycleOwner, {
                launchGameFinishedFragment(it)
            })
            viewModel.processAnswers.observe(viewLifecycleOwner, {
                binding.tvAnswersProgress.text = it
            })
        }

        private fun updateTime(time: String){
            binding.tvTimer.text = time
        }

        private fun updateQuestion(question: Question) {
            binding.tvLeftNumber.text = question.visibleNumber.toString()
            binding.tvSum.text = question.sum.toString()
            for (i in 0 until tvOptions.size) {
                tvOptions[i].text = question.options[i].toString()
            }
        }


        private fun updateProgressBar(progress: Int){
            binding.progressBar.setProgress(progress, true)
        }

        private fun updateEnoughCount(value: Boolean){
            val color = getColor(value)
            binding.tvAnswersProgress.setTextColor(color)
        }

        private fun updateEnoughPercent(value: Boolean){
            val color = getColor(value)
            binding.progressBar.progressTintList = ColorStateList.valueOf(color)
        }

    private fun getColor(value: Boolean): Int {
        val colorId = if (value) {
            android.R.color.holo_green_light
        } else {
            android.R.color.holo_red_light
        }
        return ContextCompat.getColor(requireContext(), colorId)
    }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }

        private fun parseArgs(){
            requireArguments().getParcelable<Level>(KEY_LEVEL)?.let {
                level = it
            }
        }

        private fun launchGameFinishedFragment(gameResult: GameResult){
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_container, GameFinishedFragment.newInstance(gameResult))
                .addToBackStack(null)
                .commit()
        }

        companion object {
        const val NAME = "GameFragment"
        private const val KEY_LEVEL = "level"
        fun newInstanse(level: Level): GameFragment{
            return GameFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_LEVEL, level)
                }
            }
        }
    }

}