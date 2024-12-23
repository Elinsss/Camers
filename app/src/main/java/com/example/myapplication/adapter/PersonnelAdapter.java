package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.AssignShiftActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.Personnel;
import com.example.myapplication.model.Schedule;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PersonnelAdapter extends RecyclerView.Adapter<PersonnelAdapter.PersonnelViewHolder> {

    private final List<Personnel> personnelList;
    private final OnDeleteClickListener onDeleteClickListener;
    private final OnScheduleClickListener onScheduleClickListener;
    private final Context context; // Контекст, переданный в адаптер

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public interface OnScheduleClickListener {
        void onScheduleClick(int position);
    }

    // Конструктор адаптера с контекстом
    public PersonnelAdapter(Context context, List<Personnel> personnelList, OnDeleteClickListener onDeleteClickListener, OnScheduleClickListener onScheduleClickListener) {
        this.context = context;
        this.personnelList = personnelList;
        this.onDeleteClickListener = onDeleteClickListener;
        this.onScheduleClickListener = onScheduleClickListener;
    }

    @NonNull
    @Override
    public PersonnelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.employee_item, parent, false);
        return new PersonnelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonnelViewHolder holder, int position) {
        Personnel personnel = personnelList.get(position);
        holder.name.setText(personnel.getName());
        holder.position.setText(personnel.getPosition());
        holder.contact.setText(personnel.getContact());

        // Загрузка последнего графика для сотрудника
        getLastSchedule(personnel.getId(), holder.schedule);

        holder.deleteButton.setOnClickListener(v -> onDeleteClickListener.onDeleteClick(position));

        // Открытие экрана для назначения смены
        holder.scheduleButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, AssignShiftActivity.class);
            intent.putExtra("personnelId", personnel.getId()); // Передаем ID сотрудника
            context.startActivity(intent);
        });
    }

    private void getLastSchedule(String personnelId, TextView scheduleTextView) {
        // Постоянный слушатель изменений
        FirebaseDatabase.getInstance().getReference("schedules")
                .orderByChild("personnelId")
                .equalTo(personnelId)
                .limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Schedule schedule = dataSnapshot.getValue(Schedule.class);
                                if (schedule != null) {
                                    scheduleTextView.setText("Последняя смена: " + schedule.getWorkDate() + ", " + schedule.getShift());
                                }
                            }
                        } else {
                            scheduleTextView.setText("Смены не назначены");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        scheduleTextView.setText("Ошибка загрузки смены");
                    }
                });
    }


    @Override
    public int getItemCount() {
        return personnelList.size();
    }

    static class PersonnelViewHolder extends RecyclerView.ViewHolder {
        TextView name, position, contact, schedule;
        Button deleteButton, scheduleButton;

        public PersonnelViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textViewName);
            position = itemView.findViewById(R.id.textViewPosition);
            contact = itemView.findViewById(R.id.textViewContact);
            schedule = itemView.findViewById(R.id.textViewSchedule);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            scheduleButton = itemView.findViewById(R.id.scheduleButton);
        }
    }
}
