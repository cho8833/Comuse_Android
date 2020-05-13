package com.example.comuse.DataManager;


import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class FirebaseVar {
    public static FirebaseFirestore db;
    public static FirebaseUser user;
    public static ListenerRegistration membersListener;
    public static ListenerRegistration schedulesListener;
}
