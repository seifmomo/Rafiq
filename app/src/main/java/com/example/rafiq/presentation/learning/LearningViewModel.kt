package com.example.rafiq.presentation.learning

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class LearningResource(
    val id: String,
    val title: String,
    val description: String,
    val url: String,
    val category: Category
) {
    enum class Category {
        SIGN_LANGUAGE,
        PHYSICAL_EXERCISES
    }
}

@HiltViewModel
class LearningViewModel @Inject constructor() : ViewModel() {

    private val _resources = MutableStateFlow<List<LearningResource>>(emptyList())
    val resources: StateFlow<List<LearningResource>> = _resources.asStateFlow()

    init {
        _resources.value = listOf(
            // Sign Language - Real ASL tutorials
            LearningResource(
                id = "sl_1",
                title = "ASL Alphabet & Numbers",
                description = "Learn the American Sign Language alphabet and numbers from A-Z",
                url = "https://www.youtube.com/watch?v=DBQINq0SsAw",
                category = LearningResource.Category.SIGN_LANGUAGE
            ),
            LearningResource(
                id = "sl_2",
                title = "Basic ASL Greetings & Phrases",
                description = "Essential sign language phrases for everyday communication",
                url = "https://www.youtube.com/watch?v=0FcwzMq4iWg",
                category = LearningResource.Category.SIGN_LANGUAGE
            ),
            LearningResource(
                id = "sl_3",
                title = "ASL Emergency Signs",
                description = "Important signs for emergencies - help, doctor, hospital, and more",
                url = "https://www.youtube.com/watch?v=zht0ia5Vq1U",
                category = LearningResource.Category.SIGN_LANGUAGE
            ),
            LearningResource(
                id = "sl_4",
                title = "ASL for Beginners - Full Course",
                description = "Complete beginner-friendly introduction to sign language",
                url = "https://www.youtube.com/watch?v=6w1ZDaE-whc",
                category = LearningResource.Category.SIGN_LANGUAGE
            ),

            // Physical Exercises - Accessible workouts
            LearningResource(
                id = "ex_1",
                title = "Chair Yoga for Seniors & Mobility",
                description = "Gentle seated yoga stretches for flexibility and relaxation",
                url = "https://www.youtube.com/watch?v=8hp74BdYrNM",
                category = LearningResource.Category.PHYSICAL_EXERCISES
            ),
            LearningResource(
                id = "ex_2",
                title = "Wheelchair Upper Body Workout",
                description = "Strengthen your arms, shoulders, and core while seated",
                url = "https://www.youtube.com/watch?v=qDndfnLMB5Q",
                category = LearningResource.Category.PHYSICAL_EXERCISES
            ),
            LearningResource(
                id = "ex_3",
                title = "Seated Breathing & Meditation",
                description = "Deep breathing exercises and guided relaxation in seated position",
                url = "https://www.youtube.com/watch?v=IumIKwyx8pg",
                category = LearningResource.Category.PHYSICAL_EXERCISES
            ),
            LearningResource(
                id = "ex_4",
                title = "Gentle Low-Impact Mobility Workout",
                description = "Safe, low-impact stretches to improve flexibility and blood flow",
                url = "https://www.youtube.com/watch?v=gC_L9qAHVJ8",
                category = LearningResource.Category.PHYSICAL_EXERCISES
            )
        )
    }

    fun getSignLanguageResources() = resources.value.filter { it.category == LearningResource.Category.SIGN_LANGUAGE }
    fun getExerciseResources() = resources.value.filter { it.category == LearningResource.Category.PHYSICAL_EXERCISES }
}
