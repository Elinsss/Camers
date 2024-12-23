package com.example.myapplication;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.Maintenance;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Maintenances extends AppCompatActivity {

    private EditText etDate, etType, etNotes;
    private Button btnAddRequest;
    private ListView lvRequests;

    private DatabaseReference databaseReference;
    private ArrayAdapter<String> adapter;
    private List<String> maintenanceDisplayList;
    private List<Maintenance> maintenanceList; // Для хранения оригинальных объектов

    private String cameraId; // ID камеры, переданный через Intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);

        // Инициализация Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("maintenance_requests");

        // Получаем cameraId из Intent
        cameraId = getIntent().getStringExtra("cameraId");

        // Инициализация Views
        etDate = findViewById(R.id.et_date);
        etType = findViewById(R.id.et_type);
        etNotes = findViewById(R.id.et_notes);
        btnAddRequest = findViewById(R.id.btn_add_request);
        lvRequests = findViewById(R.id.lv_requests);

        // Инициализация данных для ListView
        maintenanceDisplayList = new ArrayList<>();
        maintenanceList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, maintenanceDisplayList);
        lvRequests.setAdapter(adapter);

        // Установка обработчика выбора даты
        etDate.setOnClickListener(v -> {
            // Добавляем вывод в лог для проверки, вызывается ли обработчик клика
            Toast.makeText(Maintenances.this, "Выбор даты", Toast.LENGTH_SHORT).show();
            showDatePickerDialog(etDate);
        });


        // Слушатель для добавления запроса
        btnAddRequest.setOnClickListener(v -> addMaintenanceRequest());

        // Загрузка запросов из Firebase
        loadMaintenanceRequests();
    }

    private void addMaintenanceRequest() {
        String date = etDate.getText().toString().trim();
        String type = etType.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        if (TextUtils.isEmpty(date) || TextUtils.isEmpty(type)) {
            Toast.makeText(this, "Заполните все обязательные поля", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = databaseReference.push().getKey(); // Генерация уникального ID
        Maintenance maintenance = new Maintenance(id, cameraId, date, type, notes);

        databaseReference.child(id).setValue(maintenance).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Запрос успешно добавлен", Toast.LENGTH_SHORT).show();
            etDate.setText("");
            etType.setText("");
            etNotes.setText("");
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Ошибка добавления запроса", Toast.LENGTH_SHORT).show()
        );
    }

    private void loadMaintenanceRequests() {
        databaseReference.orderByChild("cameraId").equalTo(cameraId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        maintenanceDisplayList.clear();
                        maintenanceList.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Maintenance maintenance = dataSnapshot.getValue(Maintenance.class);
                            if (maintenance != null) {
                                maintenanceList.add(maintenance);
                                String displayText = "Дата: " + maintenance.getMaintenanceDate()
                                        + "\nТип: " + maintenance.getMaintenanceType()
                                        + "\nПримечания: " + maintenance.getNotes();
                                maintenanceDisplayList.add(displayText);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(Maintenances.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDatePickerDialog(EditText targetEditText) {
        Log.d("DatePicker", "showDatePickerDialog called");
        // Получаем текущую дату
        Calendar calendar = Calendar.getInstance();

        // Создаём диалог выбора даты
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Log.d("DatePicker", "Date selected: " + year + "-" + (month+1) + "-" + dayOfMonth);
            // Устанавливаем выбранную дату
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);

            // Форматируем дату в формате dd-MM-yyyy
            String dateFormat = "dd-MM-yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());

            // Устанавливаем отформатированную дату в EditText
            targetEditText.setText(sdf.format(selectedDate.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // Показываем диалог
        datePickerDialog.show();
    }

}
