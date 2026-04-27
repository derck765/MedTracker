package com.njit.medtrack.api

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class RxNormService {

    suspend fun getRxCui(drugName: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://rxnav.nlm.nih.gov/REST/rxcui.json?name=${drugName}"
                val response = URL(url).readText()

                val json = JSONObject(response)
                val ids = json
                    .getJSONObject("idGroup")
                    .optJSONArray("rxnormId")

                if (ids != null && ids.length() > 0) {
                    ids.getString(0)
                } else {
                    null
                }

            } catch (e: Exception) {
                Log.e("RXNORM", "Error: ${e.message}", e)
                null
            }
        }
    }
}