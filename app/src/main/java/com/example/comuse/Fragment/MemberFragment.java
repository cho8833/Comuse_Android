package com.example.comuse.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
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
import com.example.comuse.databinding.FragmentMemberBinding;

import java.util.ArrayList;

public class MemberFragment extends Fragment {
    Context context;
    CompoundButton.OnCheckedChangeListener onCheckedChangeListener;
    MembersViewAdapter adapter;
    MemberDataViewModel memberViewModel;
    FragmentMemberBinding binding;
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
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_member,container,false);

        // RecyclerView Setting
        LinearLayoutManager manager = new LinearLayoutManager(context);
        binding.recyclerMembers.setAdapter(adapter);
        binding.recyclerMembers.setLayoutManager(manager);
        bindItem(binding.recyclerMembers,memberViewModel.members);

        // ViewModel Setting
        memberViewModel.membersLiveData.observe((LifecycleOwner) context, new Observer<ArrayList<Member>>() {
            @Override
            public void onChanged(ArrayList<Member> members) {
                bindItem(binding.recyclerMembers,members);
            }
        });
        memberViewModel.getMe().observe((LifecycleOwner) context, new Observer<Member>() {
            @Override
            public void onChanged(Member member) {
                MemberFragment.this.updateUI();
            }
        });
        setOnClickListenerOnInoutButton(binding.buttonInOutMemberFragment);
        return binding.getRoot();
    }
    private void updateUI() {
        Member me = memberViewModel.getMe().getValue();
        if (me != null) {
            binding.setMyMemberData(me);
            binding.buttonInOutMemberFragment.setOnClickListener(null);
            if (me.getInoutStatus()) { binding.buttonInOutMemberFragment.setText("in"); }
            else { binding.buttonInOutMemberFragment.setText("out"); }
            setOnClickListenerOnInoutButton(binding.buttonInOutMemberFragment);
        }
    }
    //MARK:- initialize onClickListener of inoutButton
    private void setOnClickListenerOnInoutButton(TextView textView) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((TextView)v).getText() == "in") {
                    memberViewModel.updateInOut(context,false);
                } else {
                    memberViewModel.updateInOut(context,true);
                }
            }
        });
    }

    // RecyclerView 에 데이터 바인딩
    @BindingAdapter("members")
    public void bindItem(RecyclerView recyclerView, ArrayList<Member> members) {
        MembersViewAdapter adapter = (MembersViewAdapter)recyclerView.getAdapter();
        if (adapter != null) {
            adapter.setMembers(members);
            adapter.notifyDataSetChanged();
        }
    }
}
