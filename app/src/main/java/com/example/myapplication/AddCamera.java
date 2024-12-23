package com.example.myapplication;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.CameraComplex;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddCamera extends AppCompatActivity {

    private EditText editLocation, editType;
    private DatePicker datePicker;
    private CheckBox checkboxActive;
    private Button buttonAddCamera;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_camera);

        // Инициализация полей
        editLocation = findViewById(R.id.edit_location);
        editType = findViewById(R.id.edit_type);
        datePicker = findViewById(R.id.date_picker);
        checkboxActive = findViewById(R.id.checkbox_active);
        buttonAddCamera = findViewById(R.id.button_add_camera);

        // Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("cameras");

        // Обработчик кнопки
        buttonAddCamera.setOnClickListener(v -> {
            String location = editLocation.getText().toString();
            String type = editType.getText().toString();

            // Получение даты из DatePicker
            String installationDate = datePicker.getDayOfMonth() + "-" +
                    (datePicker.getMonth() + 1) + "-" + datePicker.getYear();

            boolean isActive = checkboxActive.isChecked();

            // Проверка на заполненность полей
            if (location.isEmpty() || type.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            // Генерация ID и создание объекта
            String id = databaseReference.push().getKey();
            CameraComplex camera = new CameraComplex(id, location, type, installationDate, isActive);

            // Добавление в Firebase
            if (id != null) {
                databaseReference.child(id).setValue(camera)
                        .addOnSuccessListener(aVoid -> Toast.makeText(this, "Камера добавлена", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            clearFields();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        });
    }
    private void clearFields() {
        // Очищаем поле "Местоположение"
        editLocation.setText("");

        // Очищаем поле "Тип"
        editType.setText("");

        // Сбрасываем дату в DatePicker на текущую
        datePicker.updateDate(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());

        // Сбрасываем чекбокс
        checkboxActive.setChecked(false);
    }

}
