package com.example.comuse.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.comuse.Activity.MainActivity;
import com.example.comuse.DataManager.FirebaseVar;
import com.example.comuse.DataManager.MemberDataViewModel;
import com.example.comuse.Member;
import com.example.comuse.R;
import com.example.comuse.Activity.SignInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.example.comuse.Activity.MainActivity.generateSimpleAlertDialog;


public class SettingsFragment extends Fragment {
    TextView text_name;
    TextView text_email;
    TextView sign_inout_button;
    TextView button_position_edit;
    TextView text_position;
    Context context;
    MemberDataViewModel memberViewModel;
    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider.Factory factory = ViewModelProvider.AndroidViewModelFactory.getInstance(((AppCompatActivity)context).getApplication());
        memberViewModel = new ViewModelProvider((ViewModelStoreOwner) context,factory).get(MemberDataViewModel.class);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_settings, container, false);
        text_name = mView.findViewById(R.id.mypage_name);
        text_email = mView.findViewById(R.id.mypage_email);
        sign_inout_button = mView.findViewById(R.id.button_sign_inout);
        button_position_edit = mView.findViewById(R.id.button_position_edit);
        text_position = mView.findViewById(R.id.position_text);

        sign_inout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sign_inout_button.getText().equals("Sign Out"))  {
                    SettingsFragment.this.removeAccount();
                }
                else    {
                    // 로그인이 되어 있지 않으면 로그인 액티비티 생성
                    Intent intent = new Intent(context, SignInActivity.class);
                    startActivity(intent);
                }
            }
        });

        //MARK:- Edit Button for Change Position
        button_position_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FirebaseVar.user != null)  {
                    AlertDialog.Builder builder = initPositionEditTextDialog();
                    builder.create().show();
                }
            }
        });
        memberViewModel.getMe().observe((LifecycleOwner) context, new Observer<Member>() {
            @Override
            public void onChanged(Member member) {
                SettingsFragment.this.updateUI();
            }
        });
        return mView;
    }
    private void updateUI()    {
        if(FirebaseVar.user != null)    {
            // Log in이 되었을 때 Login Info, button setting
            text_email.setText(FirebaseVar.user.getEmail());
            text_name.setText(FirebaseVar.user.getDisplayName());
            sign_inout_button.setText("Sign Out");
        }
        else    {
            //Login이 되지 않았을 때 Login Info, button setting
            text_email.setText("null");
            text_name.setText("null");
            sign_inout_button.setText("Sign In");
        }
        try {
            //myDataControl의 me 객체의 position을 받아와 textView에 띄움
            String position = memberViewModel.getMe().getValue().getPosition();
            text_position.setText(position);
        } catch (NullPointerException e)    {
            //me가 null일 때(login이 되지 않았을 때) textView setting
            text_position.setText("null");
        }


    }
    private AlertDialog.Builder initPositionEditTextDialog()   {
        final EditText editText = new EditText(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("포지션 변경");
        builder.setMessage("변경할 포지션을 입력하세요");

        //editText Layout Params Setting
        FrameLayout container = new FrameLayout(context);
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.alert_dialog_internal_margin);
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.alert_dialog_internal_margin);
        editText.setLayoutParams(params);
        container.addView(editText);
        builder.setView(container);

        //editText EditButton Setting
        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String position = editText.getText().toString();
                memberViewModel.updatePosition(context,position);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder;

    }

    @Override
    public void onResume() {
        super.onResume();
        if (memberViewModel.getMe().getValue() == null) {
            memberViewModel.getMemberData(context);
        }
        updateUI();
    }
    private void removeAccount() {
        FirebaseVar.user.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // user delete complete without reauthenticate
                        FirebaseAuth.getInstance().signOut();
                        FirebaseVar.user = null;
                        FirebaseVar.membersListener = null;
                        FirebaseVar.db = null;
                        FirebaseVar.schedulesListener = null;

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e.getClass() == FirebaseAuthRecentLoginRequiredException.class) {
                            // need re-login with password
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("계정 삭제");
                            builder.setMessage("비밀번호를 입력하세요");
                            final EditText editText = new EditText(context);
                            FrameLayout container = new FrameLayout(context);
                            FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            params.leftMargin = context.getResources().getDimensionPixelSize(R.dimen.alert_dialog_internal_margin);
                            params.rightMargin = context.getResources().getDimensionPixelSize(R.dimen.alert_dialog_internal_margin);
                            editText.setLayoutParams(params);
                            container.addView(editText);
                            builder.setView(container);

                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            AuthCredential credential = EmailAuthProvider
                                                    .getCredential(FirebaseVar.user.getEmail(), editText.getText().toString());
                                            FirebaseVar.user.reauthenticate(credential)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Log.d(TAG, "User re-authenticated.");


                                                            //Delete TimeTable Data


                                                            //Delete Local myMemberData
                                                            memberViewModel.removeSavedData(context);

                                                            FirebaseVar.user.delete();

                                                            FirebaseAuth.getInstance().signOut();
                                                            FirebaseVar.user = null;
                                                            FirebaseVar.membersListener.remove();
                                                            FirebaseVar.db = null;
                                                            FirebaseVar.schedulesListener.remove();
                                                            memberViewModel.removeSavedData(context);
                                                            Intent intent = new Intent(context, MainActivity.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            context.startActivity(intent);
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // reauthenticate failed
                                                            generateSimpleAlertDialog(e.getMessage(), context);
                                                        }
                                                    });
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            builder.show();
                        }
                    }
                });
    }
}
