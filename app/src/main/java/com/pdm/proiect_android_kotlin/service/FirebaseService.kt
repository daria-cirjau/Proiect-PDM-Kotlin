import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import java.util.Properties

class FirebaseService(private val context: Context) {

    private lateinit var auth: FirebaseAuth

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

    private fun initializeFirebase(): Boolean {
        val properties = loadPropertiesFromAssets("app.properties")
        val apiKey = properties.getProperty("FB_API_KEY")
        val appId = properties.getProperty("FB_APP_ID")
        val projectId = properties.getProperty("FB_PROJECT_ID")
        val url = properties.getProperty("FB_BASE_URL")

        return try {
            val options = FirebaseOptions.Builder()
                .setApplicationId(appId)
                .setProjectId(projectId)
                .setApiKey(apiKey)
                .build()
            val firebaseApp = FirebaseApp.initializeApp(context, options)
            auth = FirebaseAuth.getInstance(firebaseApp)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun authenticateUser(email: String, password: String, signIn: Boolean): Boolean {
        if (!::auth.isInitialized && !initializeFirebase()) {
            return false
        }

        return try {
            if (signIn) {
                auth.signInWithEmailAndPassword(email, password).await()
                true
            } else {
                auth.createUserWithEmailAndPassword(email, password).await()
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
