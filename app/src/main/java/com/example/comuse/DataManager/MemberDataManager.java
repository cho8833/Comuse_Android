package com.example.comuse.DataManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.comuse.DataModel.Member;
import com.example.comuse.NotifyAdapter;
import com.example.comuse.UpdateUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Optional;

import javax.annotation.Nullable;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MemberDataManager {
    public static Member me;
    public static Member getMe() {
        return me;
    }
    //MARK: Manage Server Data Methods
    public static void getMemberData(final Context context, final UpdateUI updateUI) {
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
                                        addMemberData(context, false, null, updateUI);
                                    } else {
                                        me = documentSnapshot.toObject(Member.class);
                                        saveMemberData(context);
                                        updateUI.updateUI();
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
    public static void addMemberData(final Context context, Boolean inoutStatus, String position, @Nullable final UpdateUI updateUI) {
        if (FirebaseVar.user != null) {
            if (FirebaseVar.db != null) {
                final Member newData = new Member(FirebaseVar.user.getDisplayName(),FirebaseVar.user.getUid(),inoutStatus,position);
                FirebaseVar.db.collection("Members").document(FirebaseVar.user.getUid())
                        .set(newData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                me = newData;
                                saveMemberData(context);

                            }
                        });
            }
        }
    }
    public static void removeMemberData(final Context context) {
        if (FirebaseVar.user != null) {
            if (FirebaseVar.db != null) {
                FirebaseVar.db.collection("Members").document(FirebaseVar.user.getUid())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                me = null;
                                removeSavedData(context);
                            }
                        });
            }
        }
    }
    public static void updateInOut(final Context context, final Boolean inoutStatus, @Nullable final UpdateUI updateUI) {
        if (FirebaseVar.user != null) {
            if (FirebaseVar.db != null) {
                FirebaseVar.db.collection("Members").document(FirebaseVar.user.getUid())
                        .update("inoutStatus",inoutStatus)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                me.setInoutStatus(inoutStatus);
                                updateSavedData(context,inoutStatus,null);
                                if(updateUI != null) {
                                    updateUI.updateUI();
                                }
                            }
                        });
            }
        }
    }
    public static void updatePosition(final Context context, final String position,@Nullable final UpdateUI updateUI) {
        if (FirebaseVar.user != null) {
            if (FirebaseVar.db != null) {
                FirebaseVar.db.collection("Members").document(FirebaseVar.user.getUid())
                        .update("position",position)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                me.setPosition(position);
                                updateSavedData(context,null,position);
                                if (updateUI != null) {
                                    updateUI.updateUI();
                                }
                            }
                        });
            }
        }
    }

    //MARK: Manage local Data Methods
    public static void saveMemberData(Context context) {
        if(me != null) {
            SharedPreferences sp = context.getSharedPreferences("me",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("name",me.getName());
            editor.putString("position",me.getPosition());
            editor.putBoolean("inoutStatus",me.getInoutStatus());
            editor.putString("uid",me.getUid());
            editor.commit();

        }
    }
    public static Boolean loadMemberData(Context context) {
        if(me == null) {
            SharedPreferences sp = context.getSharedPreferences("me",Context.MODE_PRIVATE);
            String me_fromJson = sp.getString("me","");
            Gson gson = new GsonBuilder().create();
            me = gson.fromJson(me_fromJson,Member.class);
            if(me == null) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
    public static void updateSavedData(Context context, Boolean inoutStatus, String position) {
        SharedPreferences sp = context.getSharedPreferences("me",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (inoutStatus != null) {
            editor.putBoolean("inoutStatus",inoutStatus);
        } else {
            editor.putString("position",position);
        }
        editor.commit();
    }
    public static void removeSavedData(Context context) {
        SharedPreferences sp = context.getSharedPreferences("me",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();

    }
    //MARK: -Members Control
    public static ArrayList<Member> members = new ArrayList<>();
    public static void getMembers(final NotifyAdapter notify) {
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
                                            // notify
                                            notify.notifyInsertToAdapter(0);
                                            break;
                                        case MODIFIED:
                                            for (Member compare : members) {
                                                if (compare.getUid() == data.getUid()) {
                                                    int index = members.indexOf(compare);
                                                    members.remove(index);
                                                    members.add(index,data);
                                                    notify.notifyChangeToAdapter(index,data);
                                                    break;
                                                }
                                            }

                                            break;
                                        case REMOVED:
                                            for (Member compare : members) {
                                                if (compare.getUid().equals(dc.getDocument().toObject(Member.class).getUid())) {
                                                    int position = members.indexOf(compare);
                                                    members.remove(position);
                                                    notify.notifyRemoveToAdapter(position);
                                                    break;
                                                }
                                            }
                                            break;
                                    }
                                }
                            }
                        });
            }
        }
    }
}
