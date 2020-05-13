package com.example.comuse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comuse.DataModel.Member;

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
        View view = inflater.inflate(R.layout.member_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Member member = members.get(position);
        holder.name.setText(member.getName());
        // Inout에 따라 status circle을 다르게 불러옴
        // true: 초록색 원(online_status)
        // false: 회색 원(offline_status)
        if(member.getInoutStatus() == true)
            holder.inout.setImageResource(R.drawable.online_status);
        else
            holder.inout.setImageResource(R.drawable.offline_status);
        holder.position.setText(member.getPosition());
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    class ViewHolder  extends RecyclerView.ViewHolder  {

        TextView name;
        ImageView inout;
        TextView position;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.member_name);
            inout = itemView.findViewById(R.id.inout_image);
            position = itemView.findViewById(R.id.member_position);
        }
    }

}
