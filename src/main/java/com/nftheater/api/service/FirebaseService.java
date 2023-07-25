package com.nftheater.api.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseService {

    public FirebaseToken verifyToken(String firebaseToken) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().verifyIdToken(firebaseToken, true);
    }

    public UserRecord registerEmailAndPassword(String email, String password) throws FirebaseAuthException {
        UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest();
        createRequest.setEmail(email);
        createRequest.setPassword(password);
        return FirebaseAuth.getInstance().createUser(createRequest);
    }
}
