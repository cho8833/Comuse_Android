package com.example.comuse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comuse.databinding.MemberItemLayoutBinding;

import java.util.ArrayList;

public class MembersViewAdapter extends RecyclerView.Adapter<MembersViewAdapter.ViewHolder> {
    ArrayList<Member> members;
    Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setMembers(ArrayList<Member> members) {
        this.members = members;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        MemberItemLayoutBinding binding = DataBindingUtil.inflate(inflater,R.layout.member_item_layout,parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Member member = members.get(position);
        holder.bind(member);
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    class ViewHolder  extends RecyclerView.ViewHolder  {
        MemberItemLayoutBinding itemBinding;

        public ViewHolder(@NonNull MemberItemLayoutBinding binding) {
            super(binding.getRoot());
            this.itemBinding = binding;
        }
        public void bind(Member member) {
            itemBinding.setMember(member);
            if(member.getInoutStatus()) {
                itemBinding.inoutImage.setImageResource(R.drawable.online_status);
            } else
                itemBinding.inoutImage.setImageResource(R.drawable.offline_status);
        }

    }

}
