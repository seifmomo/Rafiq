package com.example.rafiq.presentation.awareness

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class Hotline(
    val name: String,
    val number: String,
    val description: String
)

data class RightInfo(
    val title: String,
    val description: String
)

data class ImportantContact(
    val name: String,
    val website: String,
    val description: String
)

@HiltViewModel
class AwarenessViewModel @Inject constructor() : ViewModel() {

    private val _hotlines = MutableStateFlow<List<Hotline>>(emptyList())
    val hotlines: StateFlow<List<Hotline>> = _hotlines.asStateFlow()

    private val _rights = MutableStateFlow<List<RightInfo>>(emptyList())
    val rights: StateFlow<List<RightInfo>> = _rights.asStateFlow()

    private val _contacts = MutableStateFlow<List<ImportantContact>>(emptyList())
    val contacts: StateFlow<List<ImportantContact>> = _contacts.asStateFlow()

    init {
        _hotlines.value = listOf(
            Hotline(
                name = "Emergency Services",
                number = "911",
                description = "For medical, fire, or police emergencies"
            ),
            Hotline(
                name = "Disability Rights Hotline",
                number = "1-800-949-4232",
                description = "ADA Information Line - Know your rights under the Americans with Disabilities Act"
            ),
            Hotline(
                name = "National Suicide Prevention",
                number = "988",
                description = "24/7 confidential support for people in distress"
            ),
            Hotline(
                name = "Crisis Text Line",
                number = "741741",
                description = "Text HOME to connect with a crisis counselor"
            )
        )

        _rights.value = listOf(
            RightInfo(
                title = "Right to Accessibility",
                description = "All public spaces, transportation, and digital services must be accessible. This includes ramps, elevators, braille signage, screen readers, and captioning."
            ),
            RightInfo(
                title = "Right to Employment",
                description = "Employers cannot discriminate based on disability. Reasonable accommodations must be provided to enable equal work opportunities."
            ),
            RightInfo(
                title = "Right to Education",
                description = "Inclusive education is a legal right. Schools must provide assistive technologies, sign language interpreters, and modified learning materials."
            ),
            RightInfo(
                title = "Right to Healthcare",
                description = "Medical facilities must be physically accessible and provide equal care. Communication aids like sign language interpreters must be available."
            ),
            RightInfo(
                title = "Right to Assistive Technology",
                description = "Governments must ensure access to assistive devices including wheelchairs, hearing aids, screen readers, and communication devices."
            ),
            RightInfo(
                title = "Right to Independent Living",
                description = "People with disabilities have the right to live independently and be included in the community, with access to personal assistance and support services."
            ),
            RightInfo(
                title = "Right to Vote & Participate",
                description = "Voting stations must be accessible. People with disabilities have equal rights to participate in political and public life."
            ),
            RightInfo(
                title = "Right to Legal Protection",
                description = "Discrimination based on disability is prohibited by law. You have the right to file complaints and seek legal recourse."
            )
        )

        _contacts.value = listOf(
            ImportantContact(
                name = "World Health Organization (WHO)",
                website = "https://www.who.int/health-topics/disability",
                description = "Global authority on disability inclusion, health equity, and accessibility standards."
            ),
            ImportantContact(
                name = "International Disability Alliance (IDA)",
                website = "https://www.internationaldisabilityalliance.org",
                description = "Network of global disability organizations advocating for rights and inclusion."
            ),
            ImportantContact(
                name = "Disabled Peoples' International (DPI)",
                website = "https://www.dpi.org",
                description = "Cross-disability rights organization representing people with disabilities worldwide."
            ),
            ImportantContact(
                name = "ADA National Network",
                website = "https://adata.org",
                description = "Free information and guidance on Americans with Disabilities Act requirements."
            ),
            ImportantContact(
                name = "World Federation of the Deaf",
                website = "https://wfdeaf.org",
                description = "International organization advocating for sign language rights and deaf culture."
            ),
            ImportantContact(
                name = "International Blind Union",
                website = "https://www.ibunions.org",
                description = "Global voice for blind and partially sighted persons."
            )
        )
    }
}
