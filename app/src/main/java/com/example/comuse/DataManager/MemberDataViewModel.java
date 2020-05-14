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

public class MemberDataViewModel extends ViewModel {
    public final MutableLiveData<Member> me = new MutableLiveData<>();
    public LiveData<Member> getMe() {
        return me;
    }
    //MARK: Manage Server Data Methods
    public void getMemberData(final Context context) {
        if (loadMemberData(context) == true) {
            return;
        } else {
            if (FirebaseVar.user != null) {
                if (FirebaseVar.db != null) {
                    FirebaseVar.db.collection("Members").document(FirebaseVar.user.getUid())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
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
    public void addMemberData(final Context context, Boolean inoutStatus, String position) {
        if (FirebaseVar.user != null) {
            if (FirebaseVar.db != null) {
                final Member newData = new Member(FirebaseVar.user.getDisplayName(),FirebaseVar.user.getUid(),inoutStatus,position);
                FirebaseVar.db.collection("Members").document(FirebaseVar.user.getUid())
                        .set(newData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                me.setValue(newData);
                                saveMemberData(context);
                            }
                        });
            }
        }
    }
    public void removeMemberData(final Context context) {
        if (FirebaseVar.user != null) {
            if (FirebaseVar.db != null) {
                FirebaseVar.db.collection("Members").document(FirebaseVar.user.getUid())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                me.setValue(null);
                                removeSavedData(context);
                            }
                        });
            }
        }
    }
    public void updateInOut(final Context context, final Boolean inoutStatus) {
        if (FirebaseVar.user != null) {
            if (FirebaseVar.db != null) {
                FirebaseVar.db.collection("Members").document(FirebaseVar.user.getUid())
                        .update("inoutStatus",inoutStatus)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                me.getValue().setInoutStatus(inoutStatus);
                                updateSavedData(context,inoutStatus,null);

                            }
                        });
            }
        }
    }
    public void updatePosition(final Context context, final String position) {
        if (FirebaseVar.user != null) {
            if (FirebaseVar.db != null) {
                FirebaseVar.db.collection("Members").document(FirebaseVar.user.getUid())
                        .update("position",position)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                me.getValue().setPosition(position);
                                updateSavedData(context,null,position);

                            }
                        });
            }
        }
    }

    //MARK: Manage local Data Methods
    public void saveMemberData(Context context) {
        if(me != null) {
            SharedPreferences sp = context.getSharedPreferences("me",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("name",me.getValue().getName());
            editor.putString("position",me.getValue().getPosition());
            editor.putBoolean("inoutStatus",me.getValue().getInoutStatus());
            editor.putString("uid",me.getValue().getUid());
            editor.commit();

        }
    }
    public Boolean loadMemberData(Context context) {
        if(me == null) {
            SharedPreferences sp = context.getSharedPreferences("me",Context.MODE_PRIVATE);
            String name = sp.getString("name",null);
            String uid = sp.getString("uid",null);
            Boolean inoutStatus = sp.getBoolean("inoutStatus",false);
            String position = sp.getString("position",null);
            if (name != null && uid != null) {
                Member savedData = new Member(name, uid, inoutStatus, position);
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
    //MARK: -Members Control
    public MutableLiveData<ArrayList<Member>> membersLiveData = new MutableLiveData<>();
    public ArrayList<Member> members = new ArrayList<>();
    public void getMembers() {
        if (FirebaseVar.user != null) {
            if (FirebaseVar.db != null) {
                FirebaseVar.membersListener = FirebaseVar.db.collection("Members")
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
                                                if (compare.getUid() == data.getUid()) {
                                                    int index = members.indexOf(compare);
                                                    members.remove(index);
                                                    members.add(index,data);
                                                    break;
                                                }
                                            }

                                            break;
                                        case REMOVED:
                                            for (Member compare : members) {
                                                if (compare.getUid().equals(dc.getDocument().toObject(Member.class).getUid())) {
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
