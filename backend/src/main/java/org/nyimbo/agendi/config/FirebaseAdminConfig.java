package org.nyimbo.agendi.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "nyimbo.fcm.enabled", havingValue = "true")
public class FirebaseAdminConfig {
    @Bean FirebaseApp firebaseApp(@Value("${nyimbo.fcm.project-id:}") String projectId) throws Exception {
        FirebaseOptions.Builder options = FirebaseOptions.builder().setCredentials(GoogleCredentials.getApplicationDefault());
        if (projectId != null && !projectId.isBlank()) options.setProjectId(projectId);
        return FirebaseApp.initializeApp(options.build(), "nyimbo-admin");
    }
    @Bean FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) { return FirebaseMessaging.getInstance(firebaseApp); }
}
