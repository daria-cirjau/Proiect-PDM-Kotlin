package com.pdm.proiect_android_kotlin.service

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pdm.proiect_android_kotlin.models.Exercise
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties

class ExerciseService(private val context: Context) {

    private val httpClient = OkHttpClient()

    private fun loadPropertiesFromAssets(fileName: String): Properties {
        val properties = Properties()
        try {
            context.assets.open(fileName).use { inputStream ->
                properties.load(inputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return properties
    }

    suspend fun getExercisesByName(exerciseName: String): List<Exercise> {
        val properties = loadPropertiesFromAssets("app.properties")
        val apiKey = properties.getProperty("EXERCISE_API_KEY")
        val baseUrl = properties.getProperty("EXERCISE_URL")
        val url = "$baseUrl/exercises?name=${Uri.encode(exerciseName)}"
        val request = Request.Builder()
            .url(url)
            .addHeader("X-Api-Key", apiKey)
            .build()

        return withContext(Dispatchers.IO) {
            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val jsonResponse = response.body().string()
                val type = object : TypeToken<List<Exercise>>() {}.type
                Gson().fromJson<List<Exercise>>(jsonResponse, type) ?: emptyList()
            } else {
                emptyList()
            }
        }
    }
}
