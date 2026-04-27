package com.njit.medtrack.api

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder

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

    suspend fun getDrugName(rxcui: String): Pair<String?, String?> {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://rxnav.nlm.nih.gov/REST/rxcui/$rxcui/related.json?tty=IN"
                val response = URL(url).readText()

                val json = JSONObject(response)
                val groups = json
                    .getJSONObject("relatedGroup")
                    .getJSONArray("conceptGroup")

                for (i in 0 until groups.length()) {
                    val group = groups.getJSONObject(i)

                    if (group.getString("tty") == "IN") {
                        val props = group.getJSONArray("conceptProperties").getJSONObject(0)

                        val ingredientRxcui = props.getString("rxcui")
                        val name = props.getString("name")

                        Log.d("RXNORM", "Converted $rxcui → $name ($ingredientRxcui)")

                        return@withContext Pair(ingredientRxcui, name)
                    }
                }

                Pair(rxcui, null)

            } catch (e: Exception) {
                Log.e("RXNORM", "Error: ${e.message}", e)
                Pair(null, null)
            }
        }
    }
}