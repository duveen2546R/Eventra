package com.evantra.evantra.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class FirebaseConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirebaseConfig.class);

    @PostConstruct
    public void init() {
        if (!FirebaseApp.getApps().isEmpty()) {
            return;
        }

        for (CredentialSource source : getCredentialSources()) {
            try (InputStream serviceAccount = source.open()) {
                if (serviceAccount == null) {
                    continue;
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                LOGGER.info("Firebase initialized successfully using {}", source.name);
                return;
            } catch (Exception e) {
                LOGGER.warn("Firebase initialization failed using {}: {}", source.name, e.getMessage());
            }
        }

        LOGGER.error("Firebase credentials not configured or invalid. Google login will fail until Firebase admin credentials are fixed.");
    }

    private List<CredentialSource> getCredentialSources() {
        List<CredentialSource> sources = new ArrayList<>();

        sources.add(new CredentialSource("FIREBASE_SERVICE_ACCOUNT_JSON", () -> {
            String firebaseJson = System.getenv("FIREBASE_SERVICE_ACCOUNT_JSON");
            if (firebaseJson == null || firebaseJson.isBlank()) {
                return null;
            }
            return new ByteArrayInputStream(firebaseJson.getBytes(StandardCharsets.UTF_8));
        }));

        sources.add(new CredentialSource("FIREBASE_SERVICE_ACCOUNT_PATH", () -> {
            String serviceAccountPath = System.getenv("FIREBASE_SERVICE_ACCOUNT_PATH");
            if (serviceAccountPath == null || serviceAccountPath.isBlank()) {
                return null;
            }
            return new FileInputStream(serviceAccountPath);
        }));

        sources.add(new CredentialSource("classpath:firebase-service-account.json", () -> {
            ClassPathResource resource = new ClassPathResource("firebase-service-account.json");
            if (!resource.exists()) {
                return null;
            }
            return resource.getInputStream();
        }));

        return sources;
    }

    private interface CredentialProvider {
        InputStream open() throws Exception;
    }

    private static class CredentialSource {
        private final String name;
        private final CredentialProvider provider;

        private CredentialSource(String name, CredentialProvider provider) {
            this.name = name;
            this.provider = provider;
        }

        private InputStream open() throws Exception {
            return provider.open();
        }
    }
}
