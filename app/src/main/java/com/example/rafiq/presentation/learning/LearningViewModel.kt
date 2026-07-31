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
                url = "https://www.youtube.com/watch?v=tk3i2YEFTqM",
                category = LearningResource.Category.SIGN_LANGUAGE
            ),
            LearningResource(
                id = "sl_2",
                title = "Basic ASL Greetings & Phrases",
                description = "Essential sign language phrases for everyday communication",
                url = "https://www.youtube.com/watch?v=UMR3FJvWDJk",
                category = LearningResource.Category.SIGN_LANGUAGE
            ),
            LearningResource(
                id = "sl_3",
                title = "ASL Emergency Signs",
                description = "Important signs for emergencies - help, doctor, hospital, and more",
                url = "https://www.youtube.com/watch?v=sTzL7F62iPc",
                category = LearningResource.Category.SIGN_LANGUAGE
            ),
            LearningResource(
                id = "sl_4",
                title = "ASL for Beginners - Full Course",
                description = "Complete beginner-friendly introduction to sign language",
                url = "https://www.youtube.com/watch?v=G0H4iRja53A",
                category = LearningResource.Category.SIGN_LANGUAGE
            ),

            // Physical Exercises - Accessible workouts
            LearningResource(
                id = "ex_1",
                title = "Chair Yoga for Seniors & Disabilities",
                description = "Gentle seated yoga stretches for flexibility and relaxation",
                url = "https://www.youtube.com/watch?v=lCZxtN2OBL4",
                category = LearningResource.Category.PHYSICAL_EXERCISES
            ),
            LearningResource(
                id = "ex_2",
                title = "Wheelchair Upper Body Workout",
                description = "Strengthen your arms, shoulders, and core while seated",
                url = "https://www.youtube.com/watch?v=KpMQGG8iQvY",
                category = LearningResource.Category.PHYSICAL_EXERCISES
            ),
            LearningResource(
                id = "ex_3",
                title = "Seated Breathing & Meditation",
                description = "Deep breathing exercises and guided relaxation in seated position",
                url = "https://www.youtube.com/watch?v=J4vWb4tPBQs",
                category = LearningResource.Category.PHYSICAL_EXERCISES
            ),
            LearningResource(
                id = "ex_4",
                title = "Gentle Stretching for Mobility",
                description = "Safe, low-impact stretches to improve flexibility and blood flow",
                url = "https://www.youtube.com/watch?v=SI6vS2QwP5E",
                category = LearningResource.Category.PHYSICAL_EXERCISES
            )
        )
    }

    fun getSignLanguageResources() = resources.value.filter { it.category == LearningResource.Category.SIGN_LANGUAGE }
    fun getExerciseResources() = resources.value.filter { it.category == LearningResource.Category.PHYSICAL_EXERCISES }
}
