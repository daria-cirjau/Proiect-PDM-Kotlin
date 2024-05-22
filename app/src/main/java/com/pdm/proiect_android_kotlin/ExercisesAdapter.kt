package com.pdm.proiect_android_kotlin

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pdm.proiect_android_kotlin.models.Exercise

class ExercisesAdapter(private val onItemClick: (Exercise) -> Unit) : RecyclerView.Adapter<ExercisesAdapter.ExerciseViewHolder>() {

    private var exercises: List<Exercise> = listOf()
    private var selectedPosition = RecyclerView.NO_POSITION

    fun submitList(newExercises: List<Exercise>) {
        exercises = newExercises
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.exercise_item, parent, false)
        return ExerciseViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.bind(exercise, position == selectedPosition)
    }

    override fun getItemCount(): Int = exercises.size

    inner class ExerciseViewHolder(itemView: View, private val onItemClick: (Exercise) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val exerciseName: TextView = itemView.findViewById(R.id.exerciseName)
        private val exerciseEquipment: TextView = itemView.findViewById(R.id.exerciseEquipment)
        private val exerciseMuscle: TextView = itemView.findViewById(R.id.exerciseMuscle)

        fun bind(exercise: Exercise, isSelected: Boolean) {
            exerciseName.text = exercise.name
            exerciseEquipment.text = "Equipment: ${exercise.equipment}"
            exerciseMuscle.text = "Muscle: ${exercise.muscle}"

            itemView.setBackgroundColor(
                if (isSelected) ContextCompat.getColor(itemView.context, R.color.selected_item_background)
                else Color.TRANSPARENT
            )

            itemView.setOnClickListener {
                onItemClick(exercise)
                notifyItemChanged(selectedPosition)
                selectedPosition = adapterPosition
                notifyItemChanged(selectedPosition)
            }
        }
    }
}
