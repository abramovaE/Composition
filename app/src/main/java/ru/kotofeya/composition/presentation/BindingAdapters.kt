package ru.kotofeya.composition.presentation

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import ru.kotofeya.composition.R
import ru.kotofeya.composition.domain.entity.GameResult

@BindingAdapter("requiredAnswers")
fun bindRequiredAnswers(textView: TextView, count: Int){
    textView.text = String.format(textView.context.getString(R.string.required_score), count)
}

@BindingAdapter("scoreAnswers")
fun bindScoreAnswers(textView: TextView, count: Int){
    textView.text = String.format(textView.context.getString(R.string.score_answers), count)
}

@BindingAdapter("requiredPercentage")
fun bindRequiredPercentage(textView: TextView, count: Int){
    textView.text = String.format(textView.context.getString(R.string.required_percentage), count)
}

@BindingAdapter("scorePercentage")
fun bindScorePercentage(textView: TextView, gameResult: GameResult){
    val count = getPercentOfRightAnswers(gameResult)
    textView.text = String.format(textView.context.getString(R.string.score_percentage), count)
}

private fun getPercentOfRightAnswers(gameResult: GameResult) = with(gameResult){
    if(countOfQuestions == 0){
        0
    } else {
        ((countOfRightAnswers / countOfQuestions.toDouble()) * 100).toInt()
    }
}


@BindingAdapter("resultImage")
fun bindImage(imageView: ImageView, winner: Boolean){
    val imageId = getSmileResId(winner)
    imageView.setImageResource(imageId)
}

private fun getSmileResId(winner: Boolean): Int{
    return if(winner){
        R.drawable.ic_smile
    } else {
        R.drawable.ic_sad
    }
}


