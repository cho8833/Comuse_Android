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
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.comuse.Activity.Edit_AddTimeActivity;
import com.example.comuse.DataManager.ScheduleDataViewModel;
import com.example.comuse.R;
import com.github.tlaabs.timetableview.Schedule;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/*/
    TimeTable 의 Schedule 을 클릭했을 때 발생하는 Dialog
    Schedule.professorName 과 user.uid 가 일치해야 Dialog 가 발생한다.
 */
public class HandleTimeDialogFragment extends BottomSheetDialogFragment {

    Context context;
    Schedule selected;
    ScheduleDataViewModel scheduleDataViewModel;

    public HandleTimeDialogFragment(Context context, Schedule selected) {
        this.context = context;
        this.selected = selected;
        //get ViewModel
        ViewModelProvider.Factory factory = ViewModelProvider.AndroidViewModelFactory.getInstance(((AppCompatActivity)context).getApplication());
        scheduleDataViewModel = new ViewModelProvider((ViewModelStoreOwner) context,factory).get(ScheduleDataViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_handle_edit_time,container,false);

        // EditButton
        view.findViewById(R.id.btn_edit_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Edit_AddTimeActivity.class);
                intent.putExtra("get",selected);
                context.startActivity(intent);
                dismiss();
            }
        });

        // RemoveButton
        view.findViewById(R.id.btn_remove_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Remove");
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        scheduleDataViewModel.removeScheduleData(selected);
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

        // CancelButton
        view.findViewById(R.id.btn_cancel_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }
}
