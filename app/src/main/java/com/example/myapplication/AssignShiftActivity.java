package com.example.myapplication;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.Schedule;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AssignShiftActivity extends AppCompatActivity {

    private EditText editTextWorkDate, editTextShift;
    private Button saveButton;
    private String personnelId;

    private FirebaseDatabase database;
    private DatabaseReference scheduleRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_shift);

        // Инициализация элементов UI
        editTextWorkDate = findViewById(R.id.editTextWorkDate);
        editTextShift = findViewById(R.id.editTextShift);
        saveButton = findViewById(R.id.saveButton);

        // Получаем ID сотрудника, переданный из предыдущего экрана
        personnelId = getIntent().getStringExtra("personnelId");

        // Инициализация Firebase
        database = FirebaseDatabase.getInstance();
        scheduleRef = database.getReference("schedules");

        // Обработка нажатия на поле для выбора даты
        editTextWorkDate.setOnClickListener(v -> showDatePickerDialog());

        // Обработка нажатия на кнопку "Сохранить"
        saveButton.setOnClickListener(v -> saveShift());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);
                String dateFormat = "dd-MM-yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
                editTextWorkDate.setText(sdf.format(selectedDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void saveShift() {
        String workDate = editTextWorkDate.getText().toString().trim();
        String shift = editTextShift.getText().toString().trim();

        if (workDate.isEmpty() || shift.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = scheduleRef.push().getKey();
        Schedule schedule = new Schedule(id, personnelId, workDate, shift);

        scheduleRef.child(id).setValue(schedule).addOnSuccessListener(unused -> {
            Toast.makeText(this, "Смена назначена успешно", Toast.LENGTH_SHORT).show();
            finish(); // Закрываем активити и возвращаемся на предыдущий экран
        }).addOnFailureListener(e -> Toast.makeText(this, "Ошибка назначения смены", Toast.LENGTH_SHORT).show());
    }
}
