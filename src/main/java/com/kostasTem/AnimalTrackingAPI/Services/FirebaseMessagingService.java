package com.kostasTem.AnimalTrackingAPI.Services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FirebaseMessagingService {


    private final FirebaseMessaging fcm;

    public void sendNotificationToMissingNearby(String postID, Double latitude, Double longitude, String notificationText, String username) throws FirebaseMessagingException {
        Message msg = Message.builder()
                .setTopic("missing_nearby")
                .putData("notification_type","Post")
                .putData("type", "Missing")
                .putData("postID", postID)
                .putData("latitude",latitude.toString())
                .putData("longitude",longitude.toString())
                .putData("username", username)
                .putData("body", notificationText)
                .build();
        fcm.send(msg);
    }

    public void sendNotificationToEncounteredNearby(String postID, Double latitude, Double longitude, String notificationText, String username) throws FirebaseMessagingException {
        Message msg = Message.builder()
                .setTopic("encountered_nearby")
                .putData("notification_type","Post")
                .putData("type", "Encountered")
                .putData("postID", postID)
                .putData("latitude",latitude.toString())
                .putData("longitude",longitude.toString())
                .putData("username", username)
                .putData("body", notificationText)
                .build();
        fcm.send(msg);
    }

    public void sendNotificationToDeviceID(String token,String postID, String postType, String notificationText, String username) throws FirebaseMessagingException {
        Message msg = Message.builder()
                .setToken(token)
                .putData("notification_type","User")
                .putData("type", postType)
                .putData("postID", postID)
                .putData("body", notificationText)
                .putData("username", username)
                .build();
        fcm.send(msg);
    }
}
