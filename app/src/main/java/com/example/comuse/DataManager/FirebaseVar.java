package com.example.comuse.DataManager;


import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
/*
    Firebase 관련된 변수를 저장하는 클래스
 */
public class FirebaseVar {
    public static FirebaseFirestore db;         // Firestore의 데이터베이스에 접근하기 위한 변수

    /*
        Google Login 을 성공하였을 경우 사용자의 데이터가 저장되는 변수
        Google Login 이 되어있지 않으면 null 이다. 그러므로 user data 에 접근하거나 데이터베이스에 접근해야 하는 경우 null 검사가 필요하다.
        user.getDisplayName()으로 사용자의 이름을 받아오고, user.getEmail()으로 사용자의 이메일을, user.getUid()로 사용자의 uid 를 가져온다.
     */
    public static FirebaseUser user;

    /*
        FireStore 의 데이터베이스에서 Members Collection 의 데이터들을 실시간으로 모두 받아오는 Snapshot Listener 를 저장하는 변수
        사용자가 로그아웃하였을 경우, 사용자가 로그인 상태가 아닌 경우 null 이다.
     */
    public static ListenerRegistration membersListener;

    /*
        FireStore 의 데이터베이스에서 Schedules Collection 의 데이터들을 실시간으로 모두 받아오는 Snapshot Listener 를 저장하는 변수
        사용자가 로그아웃하였을 경우, 사용자가 로그인 상태가 아닌 경우 null 이다.
     */
    public static ListenerRegistration schedulesListener;
}
