package com.njit.medtrack.gemini

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import org.json.JSONObject

data class ParsedMedicineLabel(
    val name: String,
    val dosage: String,
    val scheduleTime: String,
    val frequency: String,
    val tags: String,
    val notes: String,
)

class MedicineLabelVisionAnalyzer(private val apiKey: String) {

    suspend fun analyze(bitmap: Bitmap, allowedFrequencies: Array<String>): Result<ParsedMedicineLabel> {
        if (apiKey.isBlank()) {
            return Result.failure(IllegalStateException("Missing API key"))
        }
        return runCatching {
            val model = GenerativeModel(
                modelName = "gemini-3-flash-preview",
                apiKey = apiKey,
            )
            val prompt = """
                You are reading a prescription or OTC medication bottle or label from a photo.
                Return ONLY a single JSON object with these keys (use "" if unknown):
                {"name":"","dosage":"","scheduleTime":"","frequency":"","tags":"","notes":""}

                - name: drug name as printed (brand or generic).
                - dosage: strength per dose, e.g. "500 mg", "10 mg / 5 mL".
                - scheduleTime: one suggested reminder time in US 12-hour format like "8:00 AM" if you can infer it; otherwise "".
                - frequency: exactly one of: Daily, Twice Daily, Weekly, As Needed — match the label directions; if unclear use Daily.
                - tags: short comma-separated hints like "tablet,oral" or "".
                - notes: brief patient-facing instructions from the label or "".

                No markdown fences, no commentary — only the JSON object.
            """.trimIndent()

            val response = model.generateContent(
                content {
                    image(bitmap)
                    text(prompt)
                },
            )
            val text = response.text?.trim().orEmpty().ifEmpty { error("Empty model response") }
            parseJson(text, allowedFrequencies)
        }
    }

    private fun parseJson(raw: String, allowed: Array<String>): ParsedMedicineLabel {
        val jsonStr = extractJsonObject(raw)
        val o = JSONObject(jsonStr)
        val parsed = ParsedMedicineLabel(
            name = o.optString("name", "").trim(),
            dosage = o.optString("dosage", "").trim(),
            scheduleTime = o.optString("scheduleTime", "").trim(),
            frequency = mapFrequency(o.optString("frequency", ""), allowed),
            tags = o.optString("tags", "").trim(),
            notes = o.optString("notes", "").trim(),
        )
        if (parsed.name.isEmpty()) {
            error("Could not read a medicine name from the image")
        }
        return parsed
    }

    private fun extractJsonObject(raw: String): String {
        var s = raw.trim()
        if (s.startsWith("```")) {
            s = s.removePrefix("```json").removePrefix("```").trim()
            val fence = s.lastIndexOf("```")
            if (fence >= 0) s = s.substring(0, fence).trim()
        }
        val start = s.indexOf('{')
        val end = s.lastIndexOf('}')
        require(start >= 0 && end > start) { "Response was not JSON" }
        return s.substring(start, end + 1)
    }

    private fun mapFrequency(raw: String, allowed: Array<String>): String {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return allowed[0]
        allowed.firstOrNull { it.equals(trimmed, ignoreCase = true) }?.let { return it }
        val n = trimmed.lowercase()
        return when {
            "twice" in n || "bid" in n || "2 times" in n || "two times" in n ->
                allowed.firstOrNull { it.contains("Twice", ignoreCase = true) } ?: allowed[0]
            "weekly" in n || "once a week" in n || "every week" in n ->
                allowed.firstOrNull { it.contains("Weekly", ignoreCase = true) } ?: allowed[0]
            "as needed" in n || "prn" in n || "when needed" in n ->
                allowed.firstOrNull { it.contains("As Needed", ignoreCase = true) } ?: allowed[0]
            else -> allowed[0]
        }
    }
}
