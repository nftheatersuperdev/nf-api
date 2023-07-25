package com.nftheater.api.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfiguration {
    @Bean
    @Primary
    public void firebaseInitialization() throws IOException {
//        final byte[] decodedBytes = Base64.getDecoder().decode(encodedKey);
//        final InputStream in = new ByteArrayInputStream(decodedBytes);
//        final FirebaseOptions options = FirebaseOptions.builder()
//                .setCredentials(GoogleCredentials.fromStream(in))
//                .build();
//        if (FirebaseApp.getApps().isEmpty()) {
//            FirebaseApp.initializeApp(options);
//        }
//        FileInputStream serviceAccount =
//                new FileInputStream("path/to/serviceAccountKey.json");
         InputStream serviceAccount = new ClassPathResource(
                "/nft-admin-firebase-adminsdk.json").getInputStream();
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);
    }
}
