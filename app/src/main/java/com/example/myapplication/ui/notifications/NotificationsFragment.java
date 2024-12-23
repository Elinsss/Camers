package com.example.myapplication.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Personnel;
import com.example.myapplication.adapter.PersonnelAdapter;
import com.example.myapplication.model.Schedule;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private EditText editTextName, editTextPosition, editTextContact;
    private Button buttonAdd;
    private RecyclerView recyclerView;
    private PersonnelAdapter adapter;
    private List<Personnel> personnelList;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        // Инициализация UI
        editTextName = root.findViewById(R.id.editTextName);
        editTextPosition = root.findViewById(R.id.editTextPosition);
        editTextContact = root.findViewById(R.id.editTextContact);
        buttonAdd = root.findViewById(R.id.addButton);
        recyclerView = root.findViewById(R.id.recyclerViewEmployees);

        // Настройка RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        personnelList = new ArrayList<>();
        adapter = new PersonnelAdapter(getContext(), personnelList, this::deletePersonnel, this::assignSchedule);
        recyclerView.setAdapter(adapter);

        // Ссылка на базу данных Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("personnel");

        // Загрузка данных из Firebase
        loadPersonnelData();

        // Добавление нового сотрудника
        buttonAdd.setOnClickListener(v -> addPersonnel());

        return root;
    }

    private void loadPersonnelData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                personnelList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Personnel personnel = dataSnapshot.getValue(Personnel.class);
                    personnelList.add(personnel);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addPersonnel() {
        String name = editTextName.getText().toString().trim();
        String position = editTextPosition.getText().toString().trim();
        String contact = editTextContact.getText().toString().trim();

        if (name.isEmpty() || position.isEmpty() || contact.isEmpty()) {
            Toast.makeText(getContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        // Генерация уникального ID для нового сотрудника
        String id = databaseReference.push().getKey(); // Генерация уникального ID
        Personnel personnel = new Personnel(id, name, position, contact);

        databaseReference.child(id).setValue(personnel).addOnSuccessListener(unused -> {
            Toast.makeText(getContext(), "Сотрудник добавлен", Toast.LENGTH_SHORT).show();
            editTextName.setText("");
            editTextPosition.setText("");
            editTextContact.setText("");
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Ошибка добавления", Toast.LENGTH_SHORT).show());
    }


    private void deletePersonnel(int position) {
        Personnel personnel = personnelList.get(position);
        databaseReference.child(personnel.getId()).removeValue();
        Toast.makeText(getContext(), "Сотрудник удалён", Toast.LENGTH_SHORT).show();
    }


    private void assignSchedule(int position) {
        Personnel personnel = personnelList.get(position);

        // Показать диалог или новое окно для назначения смены
        // Предположим, что здесь будет код для выбора даты и смены

        String workDate = "2024-12-10"; // Пример
        String shift = "Дневная"; // Пример

        String scheduleId = FirebaseDatabase.getInstance().getReference("schedules").push().getKey();
        Schedule schedule = new Schedule(scheduleId, personnel.getId(), workDate, shift);

        FirebaseDatabase.getInstance().getReference("schedules").child(scheduleId).setValue(schedule)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Смена назначена", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Ошибка назначения смены", Toast.LENGTH_SHORT).show();
                });
    }
}
