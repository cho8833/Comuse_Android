package com.example.comuse.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.comuse.Activity.Edit_AddTimeActivity;
import com.example.comuse.DataManager.ScheduleDataManager;
import com.example.comuse.R;
import com.github.tlaabs.timetableview.Schedule;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class HandleTimeDialogFragment extends BottomSheetDialogFragment {
    Context context;
    Schedule selected;

    public HandleTimeDialogFragment(Context context, Schedule selected) {
        this.context = context;
        this.selected = selected;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_handle_edit_time,container,false);
        view.findViewById(R.id.btn_edit_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Edit_AddTimeActivity.class);
                intent.putExtra("get",selected);
                context.startActivity(intent);
                dismiss();
            }
        });
        view.findViewById(R.id.btn_remove_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Remove");
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ScheduleDataManager.removeScheduleData(selected);
                        dismiss();
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        dismiss();
                    }
                });
                builder.create().show();
            }
        });
        view.findViewById(R.id.btn_cancel_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }
}
