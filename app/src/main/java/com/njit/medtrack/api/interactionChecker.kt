package com.njit.medtrack.api

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.njit.medtrack.Medication

class InteractionChecker(private val apiKey: String) {

    suspend fun checkInteractions(meds: List<Medication>): String {
        if (meds.isEmpty()) return "No medications to check."

        return try {
            val model = GenerativeModel(
                modelName = "gemini-3-flash-preview",
                apiKey = apiKey
            )

            if (meds.size < 2) {
                return "Add at least two medications to check for interactions."
            }

            val medList = meds.joinToString("\n") {
                val generic = it.normalizedName ?: it.name
                val brand = it.name
                val rxcui = it.rxcui ?: "unknown"

                if (generic != brand) {
                    "- $generic (brand: $brand, RxCUI: $rxcui) ${it.dosage}"
                } else {
                    "- $generic (RxCUI: $rxcui) ${it.dosage}"
                }
            }

            val prompt = """
            A user is taking the following medications (with standardized drug names and RxCUI identifiers):
            
            $medList
            
            Check for drug-to-drug interactions.
            
            Respond in this format:
            
            Interactions:
            - list interactions or say "None". List drugs in this format: drug name (brand). 
            If drug name and brand name are the same do not include both.
            
            Severity:
            - none/low / moderate / high
            
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