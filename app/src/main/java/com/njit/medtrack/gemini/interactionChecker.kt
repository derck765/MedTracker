package com.njit.medtrack.gemini

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.njit.medtrack.Medication

class InteractionChecker(private val apiKey: String) {

    suspend fun checkInteractions(meds: List<Medication>): String {
        if (meds.isEmpty()) return "No medications to check."

        return try {
            val model = GenerativeModel(
                modelName = "gemini-2.0-flash",
                apiKey = apiKey
            )

            if (meds.size < 2) {
                return "Add at least two medications to check for interactions."
            }

            val medList = meds.joinToString("\n") {
                "- ${it.name} ${it.dosage}"
            }

            val prompt = """
                A user is taking the following medications:
                $medList

                Are there any harmful drug interactions?

                Respond in this format:

                Interactions:
                - list interactions or say "None"

                Severity:
                - low / moderate / high

                Advice:
                - short explanation for a patient
            """.trimIndent()

            Log.d("GEMINI_CALL", "Sending meds:\n$medList")

            val response = model.generateContent(prompt)

            response.text ?: "No response from AI."

        } catch (e: Exception) {
            Log.e("GEMINI_CALL", "Error: ${e.message}", e)
            "Error checking interactions: ${e.message}"
        }
    }
}