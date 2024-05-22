package com.pdm.proiect_android_kotlin.service

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.pdm.proiect_android_kotlin.models.Exercise
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Properties

class FirebaseService(private val context: Context) {

    private val auth: FirebaseAuth by lazy { initializeFirebaseAuth() }
    private val database: FirebaseDatabase by lazy { initializeFirebaseDatabase() }

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

    private fun initializeFirebaseAuth(): FirebaseAuth {
        if (FirebaseApp.getApps(context).isEmpty()) {
            val properties = loadPropertiesFromAssets("app.properties")
            val apiKey = properties.getProperty("FB_API_KEY")
            val appId = properties.getProperty("FB_APP_ID")
            val projectId = properties.getProperty("FB_PROJECT_ID")
            val databaseUrl = properties.getProperty("FB_DATABASE_URL")

            val options = FirebaseOptions.Builder()
                .setApplicationId(appId)
                .setProjectId(projectId)
                .setApiKey(apiKey)
                .setDatabaseUrl(databaseUrl)
                .build()
            FirebaseApp.initializeApp(context, options)
        }
        return FirebaseAuth.getInstance()
    }

    private fun initializeFirebaseDatabase(): FirebaseDatabase {
        if (FirebaseApp.getApps(context).isEmpty()) {
            initializeFirebaseAuth()
        }
        return FirebaseDatabase.getInstance()
    }

    suspend fun authenticateUser(email: String, password: String, signIn: Boolean): Boolean {
        return try {
            if (signIn) {
                auth.signInWithEmailAndPassword(email, password).await()
            } else {
                auth.createUserWithEmailAndPassword(email, password).await()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun addExercise(exercise: Exercise): Boolean {
        return try {
            val uid = auth.currentUser!!.uid
            val key = database.getReference("users").child(uid).child("exercises").push().key
            key?.let {
                database.getReference("users").child(uid).child("exercises").child(it)
                    .setValue(exercise).await()
                true
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getUserExercises(): List<Exercise> {
        return try {
            val uid = auth.currentUser!!.uid
            val snapshot =
                database.getReference("users").child(uid).child("exercises").get().await()
            val exercises = mutableListOf<Exercise>()
            snapshot.children.forEach { dataSnapshot ->
                dataSnapshot.getValue(Exercise::class.java)?.let {
                    exercises.add(it)
                }
            }
            exercises
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getTodayUserExercises(): List<Exercise> {
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return try {
            val uid = auth.currentUser!!.uid
            val snapshot =
                database.getReference("users").child(uid).child("exercises").orderByChild("date")
                    .equalTo(todayDate).get().await()
            val exercises = mutableListOf<Exercise>()
            snapshot.children.forEach { dataSnapshot ->
                dataSnapshot.getValue(Exercise::class.java)?.let {
                    exercises.add(it)
                }
            }
            exercises
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
