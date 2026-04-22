package com.njit.medtrack

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.njit.medtrack.data.MedicalProfile
import com.njit.medtrack.data.MedicalProfileRepository
import com.njit.medtrack.data.MedicationRepository
import com.njit.medtrack.notifications.ReminderScheduler
import kotlinx.coroutines.launch

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val profileRepo by lazy { MedicalProfileRepository(requireContext().applicationContext) }
    private val medRepo by lazy { MedicationRepository(requireContext().applicationContext) }

    private var currentProfile = MedicalProfile()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        val ctx = requireContext()
        if (granted) {
            ReminderScheduler.setNotificationsEnabled(ctx, true)
            rescheduleAll()
            Toast.makeText(ctx, R.string.notifications_enabled_toast, Toast.LENGTH_SHORT).show()
        } else {
            view?.findViewById<SwitchMaterial>(R.id.switch_notifications)?.isChecked = false
            ReminderScheduler.setNotificationsEnabled(ctx, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnEdit = view.findViewById<MaterialButton>(R.id.btn_edit_profile)
        val btnShare = view.findViewById<MaterialButton>(R.id.btn_share_profile)
        val switchNotifications = view.findViewById<SwitchMaterial>(R.id.switch_notifications)
        val fieldsLayout = view.findViewById<View>(R.id.layout_profile_fields)
        val textEmpty = view.findViewById<TextView>(R.id.text_profile_empty)

        // field rows
        val rowName = view.findViewById<View>(R.id.row_name)
        val textName = view.findViewById<TextView>(R.id.text_profile_name)
        val rowDob = view.findViewById<View>(R.id.row_dob)
        val textDob = view.findViewById<TextView>(R.id.text_profile_dob)
        val rowBloodType = view.findViewById<View>(R.id.row_blood_type)
        val textBloodType = view.findViewById<TextView>(R.id.text_profile_blood_type)
        val labelMedical = view.findViewById<View>(R.id.label_section_medical)
        val rowAllergies = view.findViewById<View>(R.id.row_allergies)
        val textAllergies = view.findViewById<TextView>(R.id.text_profile_allergies)
        val rowConditions = view.findViewById<View>(R.id.row_conditions)
        val textConditions = view.findViewById<TextView>(R.id.text_profile_conditions)
        val dividerEmergency = view.findViewById<View>(R.id.divider_emergency)
        val labelEmergency = view.findViewById<View>(R.id.label_section_emergency)
        val rowEmergencyContact = view.findViewById<View>(R.id.row_emergency_contact)
        val textEmergencyContact = view.findViewById<TextView>(R.id.text_profile_emergency_contact)
        val rowEmergencyPhone = view.findViewById<View>(R.id.row_emergency_phone)
        val textEmergencyPhone = view.findViewById<TextView>(R.id.text_profile_emergency_phone)
        val dividerInsurance = view.findViewById<View>(R.id.divider_insurance)
        val labelInsurance = view.findViewById<View>(R.id.label_section_insurance)
        val rowInsuranceProvider = view.findViewById<View>(R.id.row_insurance_provider)
        val textInsuranceProvider = view.findViewById<TextView>(R.id.text_profile_insurance_provider)
        val rowInsuranceId = view.findViewById<View>(R.id.row_insurance_id)
        val textInsuranceId = view.findViewById<TextView>(R.id.text_profile_insurance_id)

        switchNotifications.isChecked = ReminderScheduler.areNotificationsEnabled(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            profileRepo.watchProfile().collect { profile ->
                currentProfile = profile

                val hasPersonal = profile.fullName.isNotEmpty() || profile.dateOfBirth.isNotEmpty() || profile.bloodType.isNotEmpty()
                val hasMedical = profile.allergies.isNotEmpty() || profile.conditions.isNotEmpty()
                val hasEmergency = profile.emergencyContact.isNotEmpty() || profile.emergencyPhone.isNotEmpty()
                val hasInsurance = profile.insuranceProvider.isNotEmpty() || profile.insuranceId.isNotEmpty()
                val hasAny = hasPersonal || hasMedical || hasEmergency || hasInsurance

                textEmpty.isVisible = !hasAny
                fieldsLayout.isVisible = hasAny

                // Personal
                rowName.isVisible = profile.fullName.isNotEmpty()
                textName.text = profile.fullName
                rowDob.isVisible = profile.dateOfBirth.isNotEmpty()
                textDob.text = profile.dateOfBirth
                rowBloodType.isVisible = profile.bloodType.isNotEmpty()
                textBloodType.text = profile.bloodType

                // Medical
                val showMedicalSection = hasMedical
                labelMedical.isVisible = showMedicalSection
                rowAllergies.isVisible = profile.allergies.isNotEmpty()
                textAllergies.text = profile.allergies
                rowConditions.isVisible = profile.conditions.isNotEmpty()
                textConditions.text = profile.conditions

                // Emergency
                val showEmergencySection = hasEmergency
                dividerEmergency.isVisible = showEmergencySection
                labelEmergency.isVisible = showEmergencySection
                rowEmergencyContact.isVisible = profile.emergencyContact.isNotEmpty()
                textEmergencyContact.text = profile.emergencyContact
                rowEmergencyPhone.isVisible = profile.emergencyPhone.isNotEmpty()
                textEmergencyPhone.text = profile.emergencyPhone

                // Insurance
                val showInsuranceSection = hasInsurance
                dividerInsurance.isVisible = showInsuranceSection
                labelInsurance.isVisible = showInsuranceSection
                rowInsuranceProvider.isVisible = profile.insuranceProvider.isNotEmpty()
                textInsuranceProvider.text = profile.insuranceProvider
                rowInsuranceId.isVisible = profile.insuranceId.isNotEmpty()
                textInsuranceId.text = profile.insuranceId
            }
        }

        btnEdit.setOnClickListener { showEditProfileDialog() }

        btnShare.setOnClickListener {
            val text = buildShareText(currentProfile)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_profile_subject))
                putExtra(Intent.EXTRA_TEXT, text)
            }
            startActivity(Intent.createChooser(intent, getString(R.string.share_profile_subject)))
        }

        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enableNotifications()
            } else {
                ReminderScheduler.setNotificationsEnabled(requireContext(), false)
                ReminderScheduler.cancelAll(requireContext())
                Toast.makeText(requireContext(), R.string.notifications_disabled_toast, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun enableNotifications() {
        val ctx = requireContext()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED -> {
                    ReminderScheduler.setNotificationsEnabled(ctx, true)
                    rescheduleAll()
                    Toast.makeText(ctx, R.string.notifications_enabled_toast, Toast.LENGTH_SHORT).show()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    MaterialAlertDialogBuilder(ctx)
                        .setMessage(R.string.notification_permission_rationale)
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        .setNegativeButton(android.R.string.cancel) { _, _ ->
                            view?.findViewById<SwitchMaterial>(R.id.switch_notifications)?.isChecked = false
                        }
                        .show()
                }
                else -> requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            ReminderScheduler.setNotificationsEnabled(ctx, true)
            rescheduleAll()
            Toast.makeText(ctx, R.string.notifications_enabled_toast, Toast.LENGTH_SHORT).show()
        }
    }

    private fun rescheduleAll() {
        viewLifecycleOwner.lifecycleScope.launch {
            val meds = medRepo.getAllMedications()
            ReminderScheduler.rescheduleAll(requireContext(), meds)
        }
    }

    private fun showEditProfileDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_medical_profile, null)

        val inputName = dialogView.findViewById<TextInputEditText>(R.id.input_profile_name)
        val inputDob = dialogView.findViewById<TextInputEditText>(R.id.input_profile_dob)
        val inputBloodType = dialogView.findViewById<android.widget.AutoCompleteTextView>(R.id.input_profile_blood_type)
        val inputAllergies = dialogView.findViewById<TextInputEditText>(R.id.input_profile_allergies)
        val inputConditions = dialogView.findViewById<TextInputEditText>(R.id.input_profile_conditions)
        val inputEmergencyContact = dialogView.findViewById<TextInputEditText>(R.id.input_profile_emergency_contact)
        val inputEmergencyPhone = dialogView.findViewById<TextInputEditText>(R.id.input_profile_emergency_phone)
        val inputInsuranceProvider = dialogView.findViewById<TextInputEditText>(R.id.input_profile_insurance_provider)
        val inputInsuranceId = dialogView.findViewById<TextInputEditText>(R.id.input_profile_insurance_id)

        val bloodTypes = arrayOf("A+", "A−", "B+", "B−", "AB+", "AB−", "O+", "O−", "Unknown")
        inputBloodType.setAdapter(
            android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, bloodTypes)
        )

        inputName.setText(currentProfile.fullName)
        inputDob.setText(currentProfile.dateOfBirth)
        inputBloodType.setText(currentProfile.bloodType, false)
        inputAllergies.setText(currentProfile.allergies)
        inputConditions.setText(currentProfile.conditions)
        inputEmergencyContact.setText(currentProfile.emergencyContact)
        inputEmergencyPhone.setText(currentProfile.emergencyPhone)
        inputInsuranceProvider.setText(currentProfile.insuranceProvider)
        inputInsuranceId.setText(currentProfile.insuranceId)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_title_edit_profile)
            .setView(dialogView)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(R.string.action_save) { _, _ ->
                val updated = MedicalProfile(
                    fullName = inputName.text?.toString()?.trim().orEmpty(),
                    dateOfBirth = inputDob.text?.toString()?.trim().orEmpty(),
                    bloodType = inputBloodType.text?.toString()?.trim().orEmpty(),
                    allergies = inputAllergies.text?.toString()?.trim().orEmpty(),
                    conditions = inputConditions.text?.toString()?.trim().orEmpty(),
                    emergencyContact = inputEmergencyContact.text?.toString()?.trim().orEmpty(),
                    emergencyPhone = inputEmergencyPhone.text?.toString()?.trim().orEmpty(),
                    insuranceProvider = inputInsuranceProvider.text?.toString()?.trim().orEmpty(),
                    insuranceId = inputInsuranceId.text?.toString()?.trim().orEmpty(),
                )
                viewLifecycleOwner.lifecycleScope.launch {
                    profileRepo.saveProfile(updated)
                }
            }
            .show()
    }

    private fun buildShareText(profile: MedicalProfile): String = buildString {
        appendLine("=== MedTrack Medical Profile ===")
        if (profile.fullName.isNotEmpty()) appendLine("Name: ${profile.fullName}")
        if (profile.dateOfBirth.isNotEmpty()) appendLine("Date of Birth: ${profile.dateOfBirth}")
        if (profile.bloodType.isNotEmpty()) appendLine("Blood Type: ${profile.bloodType}")
        if (profile.allergies.isNotEmpty()) appendLine("Allergies: ${profile.allergies}")
        if (profile.conditions.isNotEmpty()) appendLine("Conditions: ${profile.conditions}")
        if (profile.emergencyContact.isNotEmpty()) {
            appendLine("Emergency Contact: ${profile.emergencyContact}")
            if (profile.emergencyPhone.isNotEmpty()) appendLine("Emergency Phone: ${profile.emergencyPhone}")
        }
        if (profile.insuranceProvider.isNotEmpty()) appendLine("Insurance: ${profile.insuranceProvider}")
        if (profile.insuranceId.isNotEmpty()) appendLine("Insurance ID: ${profile.insuranceId}")
    }
}
