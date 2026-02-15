import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
<<<<<<< HEAD
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
=======
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.InputStream;
>>>>>>> 27166d7 (Fixes Done)

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Component
public class FirebaseConfig {

    @PostConstruct
<<<<<<< HEAD
    public void init() {
        try {
            String firebaseJson = System.getenv("FIREBASE_SERVICE_ACCOUNT_JSON");

            if (firebaseJson == null || firebaseJson.isEmpty()) {
                throw new RuntimeException("FIREBASE_SERVICE_ACCOUNT_JSON env variable not set");
            }

            ByteArrayInputStream serviceAccount =
                    new ByteArrayInputStream(firebaseJson.getBytes(StandardCharsets.UTF_8));

=======
    public void init() throws Exception {
        String serviceAccountPath = System.getenv("FIREBASE_SERVICE_ACCOUNT_PATH");

        InputStream serviceAccount = null;
        try {
            if (serviceAccountPath != null && !serviceAccountPath.isBlank()) {
                serviceAccount = new FileInputStream(serviceAccountPath);
            } else {
                serviceAccount = new ClassPathResource("firebase-service-account.json").getInputStream();
            }

>>>>>>> 27166d7 (Fixes Done)
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
<<<<<<< HEAD

            System.out.println("Firebase initialized successfully");

        } catch (Exception e) {
            throw new RuntimeException("Firebase initialization failed", e);
=======
        } finally {
            if (serviceAccount != null) {
                serviceAccount.close();
            }
>>>>>>> 27166d7 (Fixes Done)
        }
    }
}
