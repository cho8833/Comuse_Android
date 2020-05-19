package com.example.comuse.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingAdapter;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comuse.DataManager.FirebaseVar;
import com.example.comuse.DataManager.MemberDataViewModel;
import com.example.comuse.Member;
import com.example.comuse.MembersViewAdapter;
import com.example.comuse.R;

import java.util.ArrayList;

public class MemberFragment extends Fragment {
    Context context;
    Switch inoutSwitch;
    CompoundButton.OnCheckedChangeListener onCheckedChangeListener;
    MembersViewAdapter adapter;
    MemberDataViewModel memberViewModel;
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

        // RecyclerView Adapter initialize
        adapter = new MembersViewAdapter();
        adapter.setContext(context);

        // get ViewModel
        ViewModelProvider.Factory factory = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication());
        memberViewModel = new ViewModelProvider((ViewModelStoreOwner) context,factory).get(MemberDataViewModel.class);
    }

    /*
        Fragment 가 resume 될 때마다 모든 사용자를 불러오는 snapshot listener 를 검사한다.
        로그아웃 -> 로그인 상태로 바뀌면 FirebaseVar.user 에 사용자 데이터가 저장된다. memberViewModel.getMembers 는 user 의 null 여부에 따라 snapshot listener 를 활성화한다.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (FirebaseVar.membersListener == null) {
            memberViewModel.getMembers();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_member, container, false);

        // inOutSwitch Setting
        inoutSwitch = mView.findViewById(R.id.inout_switch);
        onCheckedChangeListener = initOnCheckedListener();
        configInOutSwitch(inoutSwitch,onCheckedChangeListener);

        // RecyclerView Setting
        final RecyclerView recycler = mView.findViewById(R.id.recycler_members);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(manager);
        bindItem(recycler,memberViewModel.members);

        // ViewModel Setting
        memberViewModel.membersLiveData.observe((LifecycleOwner) context, new Observer<ArrayList<Member>>() {
            @Override
            public void onChanged(ArrayList<Member> members) {
                bindItem(recycler,members);
            }
        });
        memberViewModel.getMe().observe((LifecycleOwner) context, new Observer<Member>() {
            @Override
            public void onChanged(Member member) {
                MemberFragment.this.updateUI();
            }
        });

        return mView;
    }
    private void updateUI() {
        try {
            //inout_switch의 checked state를 myDataControl 객체의 me(자신의 데이터)객체의 inout state와 동기화
            inoutSwitch.setClickable(true);
            if(memberViewModel.getMe().getValue().getInoutStatus())    {
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
                    memberViewModel.updateInOut(context,inout);
                } else {
                    inoutSwitch.setChecked(false);
                    inoutSwitch.setClickable(false);
                    inoutSwitch.setText("");
                }
            }
        };
        return onCheckedChangeListener;
    }
    // inOutSwitch 의 OnCheckedListener 연결
    private void configInOutSwitch(final Switch inout_switch, CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        inout_switch.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    // RecyclerView 에 데이터 바인딩
    @BindingAdapter("tools:item")
    public void bindItem(RecyclerView recyclerView, ArrayList<Member> members) {
        MembersViewAdapter adapter = (MembersViewAdapter)recyclerView.getAdapter();
        if (adapter != null) {
            adapter.setMembers(members);
            adapter.notifyDataSetChanged();
        }
    }
}
