package com.example.comuse.DataManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.comuse.Member;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

import static androidx.constraintlayout.widget.Constraints.TAG;
/*
    class Member
        private String name;            // 멤버의 이름, FirebaseUser.name 저장

        private String email;             // Member class 의 고유데이터
                                        // FireStore Database 의 User 의 데이터를 저장할 때 document name 을 email 로 저장한다.
                                        // Schedule 을 생성할 때 작성자 구분을 위해 professorName property 에 email 를 저장한다.

        private Boolean inoutStatus;    // 멤버의 inout 상터태
        private String position;        // 멤버의 포지션, Setting 에서 edit 가능

    Member Data 는 FireStore/Members Collection 에 문서 이름은 Member.email 로 저장된다.
    자신의 Member Data 는 MutableLiveData 타입의 me 라는 객체에 저장이 되고, SharedPreference 기능을 통해 local 에 save 하고 load 하여
    FireStore 데이터 통신의 지연시간을 없앤다.
    따라서 me의 데이터가 수정되려면 DataBase 의 데이터 update, Local 의 데이터 update, me 객체의 데이터의 update 가 필요하다.
    기본적으로 데이터베이스의 데이터를 수정하고 Success 콜백함수가 실행되면 Local 의 데이터를 수정한다.
*/
/*
    Member Data 가 수정/추가/제거 될 때, 만약 User 의 데이터라면 View 중 자신의 데이터를 표시하는 View 에 notify 할 필요가 있다
    하지만 User 의 데이터가 아닌 RecyclerView 에 표시될 다른 사람의 데이터라면 recycler view 에 notify 할 필요가 없다 ( snapshot listener, LiveData 에서 처리 )
 */
public class MemberDataViewModel extends ViewModel {

    //MARK: -My Member Data Control
    public final MutableLiveData<Member> me = new MutableLiveData<>();
    public LiveData<Member> getMe() { return me; }
    public void getMemberData(final Context context) {
        /*
            먼저 Local 에 데이터가 저장되어 있는지 검사한다.
            데이터가 저장되어 있지 않으면 loadMemberData 함수는 false 를 반환하고 me 객체는 null 값이다. 그리고 자신의 객체를 가져오기 위한 데이터 통신을 시작한다.
            데이터가 저장되어 load 되면 loadMemberData 함수는 true 를 반환하고 me 객체에 데이터가 저장된다. 그리고 함수가 종료된다.
         */
        if (loadMemberData(context) == true) {
            return;
        } else {
            if (FirebaseVar.user != null) {
                if (FirebaseVar.db != null) {
                    FirebaseVar.db.collection("Members").document(FirebaseVar.user.getEmail())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    /*
                                        DataBase 에도 데이터가 없으면 null 데이터가 전달된다.
                                        그럴 떄엔, addMemberData 함수를 통해 데이터를 데이터 베이스에 저장하고 me 객체를 초기화한다.
                                     */
                                    Member data = documentSnapshot.toObject(Member.class);
                                    if (data == null) {
                                        //set
                                        addMemberData(context, false, null);
                                    } else {
                                        Member myData =documentSnapshot.toObject(Member.class);
                                        me.setValue(myData);
                                        saveMemberData(context);
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("TAG",e.getMessage());
                                }
                            });
                }
            }
        }
    }
    // FireStore/Members Collection 에 Member.email 의 이름으로 문서 생
    public void addMemberData(final Context context, Boolean inoutStatus, String position) {
        if (FirebaseVar.user != null) {
            if (FirebaseVar.db != null) {
                final Member newData = new Member(FirebaseVar.user.getDisplayName(),FirebaseVar.user.getEmail(),inoutStatus,position);
                FirebaseVar.db.collection("Members").document(FirebaseVar.user.getEmail())
                        .set(newData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                me.setValue(newData);       // me 객체의 정보 수정
                                saveMemberData(context);    // Local 에 데이터 저장
                            }
                        });
            }
        }
    }
    // FireStore/Members Collection 의 Member.uid 의 문서 삭제
    public void removeMemberData(final Context context) {
        if (FirebaseVar.user != null) {
            if (FirebaseVar.db != null) {
                FirebaseVar.db.collection("Members").document(FirebaseVar.user.getEmail())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                me.setValue(null);          // me 객체 데이터 삭제
                                removeSavedData(context);   // Local 데이터 삭제
                            }
                        });
            }
        }
    }
    // FireStore/Members Collection 의 Member 데이터 중 inoutStatus 를 update 한다.
    public void updateInOut(final Context context, final Boolean inoutStatus) {
        if (FirebaseVar.user != null) {
            if (FirebaseVar.db != null) {
                FirebaseVar.db.collection("Members").document(FirebaseVar.user.getEmail())
                        .update("inoutStatus",inoutStatus)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                me.setValue(new Member(me.getValue().getName(),me.getValue().getEmail(),inoutStatus,me.getValue().getPosition())); // me 객체 데이터 update
                                updateSavedData(context,inoutStatus,null);  // Local 데이터 update

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // error occurred updating inoutStatus
                                // case1 : no Document
                                if (((FirebaseFirestoreException)e).getCode() == FirebaseFirestoreException.Code.NOT_FOUND) {
                                    addMemberData(context,false,null);
                                }
                            }
                        });
            }
        }
    }
    // FireStore/Members Collection 의 Member 데이터 중 position 을 update 한다.
    public void updatePosition(final Context context, final String position) {
        if (FirebaseVar.user != null) {
            if (FirebaseVar.db != null) {
                FirebaseVar.db.collection("Members").document(FirebaseVar.user.getEmail())
                        .update("position",position)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                me.setValue(new Member(me.getValue().getName(),me.getValue().getEmail(),me.getValue().getInoutStatus(),position));    // me 객체 데이터 update
                                updateSavedData(context,null,position); // Local 데이터 update

                            }
                        });
            }
        }
    }

    //MARK: Manage local Data Methods (SharedPreferences)
    public void saveMemberData(Context context) {
        if(me.getValue() != null) {
            SharedPreferences sp = context.getSharedPreferences("me",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("name",me.getValue().getName());
            editor.putString("position",me.getValue().getPosition());
            editor.putBoolean("inoutStatus",me.getValue().getInoutStatus());
            editor.putString("email",me.getValue().getEmail());
            editor.commit();

        }
    }
    public Boolean loadMemberData(Context context) {
        if(me == null) {
            SharedPreferences sp = context.getSharedPreferences("me",Context.MODE_PRIVATE);
            String name = sp.getString("name",null);
            String email = sp.getString("email",null);
            Boolean inoutStatus = sp.getBoolean("inoutStatus",false);
            String position = sp.getString("position",null);
            if (name != null && email != null) {
                Member savedData = new Member(name, email, inoutStatus, position);
                me.setValue(savedData);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    public void updateSavedData(Context context, Boolean inoutStatus, String position) {
        SharedPreferences sp = context.getSharedPreferences("me",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (inoutStatus != null) {
            editor.putBoolean("inoutStatus",inoutStatus);
        } else {
            editor.putString("position",position);
        }
        editor.commit();
    }
    public void removeSavedData(Context context) {
        SharedPreferences sp = context.getSharedPreferences("me",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();

    }
    //MARK: -All Members Control Method
    /*
        모든 Member 은 SnapShot 리스너를 통해 실시간으로 View(RecyclerView) 에 파급된다.
        MemberFragment 내의 observer 가 membersLiveData 를 구독하여 DocumentChange 가 발생하면 members 데이터를 수정하고
        membersLiveData.setValue 를 통해 notify 한다.
     */
    public MutableLiveData<ArrayList<Member>> membersLiveData = new MutableLiveData<>();
    public ArrayList<Member> members = new ArrayList<>();
    public void getMembers() {
        if (FirebaseVar.user != null) {
            if (FirebaseVar.db != null) {
                FirebaseVar.membersListener = FirebaseVar.db.collection("Members")                  // Snapshot 리스너 분리
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    Log.w(TAG,"listen error", e);
                                    return;
                                }
                                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                                    Member data = dc.getDocument().toObject(Member.class);
                                    switch (dc.getType()) {
                                        case ADDED:
                                            members.add(0,data);
                                            break;
                                        case MODIFIED:
                                            for (Member compare : members) {
                                                if (compare.getEmail().equals(data.getEmail())) {
                                                    int index = members.indexOf(compare);
                                                    members.remove(index);
                                                    members.add(index,data);
                                                    break;
                                                }
                                            }

                                            break;
                                        case REMOVED:
                                            for (Member compare : members) {
                                                if (compare.getEmail().equals(dc.getDocument().toObject(Member.class).getEmail())) {
                                                    int position = members.indexOf(compare);
                                                    members.remove(position);
                                                    break;
                                                }
                                            }
                                            break;
                                    }
                                }
                                membersLiveData.setValue(members);
                            }
                        });
            }
        }
    }
}
