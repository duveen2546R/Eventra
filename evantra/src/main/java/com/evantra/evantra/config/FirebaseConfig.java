package com.evantra.evantra.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class FirebaseConfig {

    @Autowired
    private Environment env;

    @PostConstruct
    public void init() {
        try {
            String firebaseJson = env.getProperty("FIREBASE_SERVICE_ACCOUNT_JSON");

            if (firebaseJson == null || firebaseJson.isEmpty()) {
                throw new RuntimeException("FIREBASE_SERVICE_ACCOUNT_JSON not found in .env or environment variables!");
            }

            // Convert \\n into actual newline
            firebaseJson = firebaseJson.replace("\\n", "\n");

            InputStream serviceAccount = new ByteArrayInputStream(firebaseJson.getBytes(StandardCharsets.UTF_8));

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase initialized successfully");
            } else {
                System.out.println("Firebase already initialized");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Firebase initialization failed", e);
        }
    }
}