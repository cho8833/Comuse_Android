package com.example.comuse.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.comuse.DataManager.MemberDataManager;
import com.example.comuse.DataModel.Member;
import com.example.comuse.DataManager.FirebaseVar;
import com.example.comuse.MembersViewAdapter;
import com.example.comuse.NotifyAdapter;
import com.example.comuse.R;
import com.example.comuse.UpdateUI;

import static com.example.comuse.Activity.MainActivity.memberDataManager;

public class MemberFragment extends Fragment {
    Context context;
    UpdateUI updateUI;
    Switch inoutSwitch;
    CompoundButton.OnCheckedChangeListener onCheckedChangeListener;
    MembersViewAdapter adapter;
    NotifyAdapter notify;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = getActivity();
    }
    public MemberFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // RecyclerView Adapter Settings
        adapter = new MembersViewAdapter();
        adapter.setContext(context);
        adapter.setMembers(MemberDataManager.members);

        // get Members
        notify = new NotifyAdapter() {
            @Override
            public void notifyInsertToAdapter(int position) { adapter.notifyItemInserted(position); }
            @Override
            public void notifyChangeToAdapter(int position, Member modified) { adapter.notifyItemChanged(position,modified); }
            @Override
            public void notifyRemoveToAdapter(int position) { adapter.notifyItemRemoved(position); }
        };

        updateUI = new UpdateUI() {
            @Override
            public void updateUI() {
                MemberFragment.this.updateUI();
            }
        };


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View mView = inflater.inflate(R.layout.fragment_member, container, false);
        inoutSwitch = mView.findViewById(R.id.inout_switch);
        onCheckedChangeListener = initOnCheckedListener();
        configInOutSwitch(inoutSwitch,onCheckedChangeListener);
        RecyclerView recycler = mView.findViewById(R.id.recycler_members);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(manager);
        if (FirebaseVar.membersListener == null) {
            MemberDataManager.getMembers(notify);
        }

        memberDataManager.getMemberData(context,updateUI);
        return mView;
    }
    private void updateUI() {
        try {
            //inout_switch의 checked state를 myDataControl 객체의 me(자신의 데이터)객체의 inout state와 동기화
            inoutSwitch.setClickable(true);
            if(memberDataManager.getMe().getInoutStatus())    {
                inoutSwitch.setOnCheckedChangeListener(null);
                inoutSwitch.setChecked(true);
                inoutSwitch.setText("in");
                inoutSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
            }
            else    {
                inoutSwitch.setOnCheckedChangeListener(null);
                inoutSwitch.setChecked(false);
                inoutSwitch.setText("out");
                inoutSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
            }
            //myDataControl의 me 객체가 null일 경우, inoutswitch 비활성화
        } catch (NullPointerException e)    {
            inoutSwitch.setOnCheckedChangeListener(null);
            inoutSwitch.setClickable(false);
            inoutSwitch.setText("");
            inoutSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        }
    }
    //MARK:- initialize onClickListener of InoutSwitch
    private CompoundButton.OnCheckedChangeListener initOnCheckedListener() {
        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final Boolean inout;
                //user가 null이 아닐 때, myDataControl 객체를 통해 자신의 Inout state 업데이
                if (FirebaseVar.user != null) {
                    if (isChecked == true) inout = true;
                    else inout = false;
                    memberDataManager.updateInOut(context,inout,updateUI);
                } else {
                    inoutSwitch.setChecked(false);
                    inoutSwitch.setClickable(false);
                    inoutSwitch.setText("");
                }
            }
        };
        return onCheckedChangeListener;
    }
    //MARK:- inoutswitch에 onclicklistener 연결
    private void configInOutSwitch(final Switch inout_switch, CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        inout_switch.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        MemberDataManager.saveMemberData(context);
    }
}
