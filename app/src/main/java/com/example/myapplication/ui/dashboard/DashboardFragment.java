package com.example.myapplication.ui.dashboard;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.CameraRequestAdapter;
import com.example.myapplication.model.CameraRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {
    private ChildEventListener childEventListener;

    private EditText etLocation, etType, etRequestDate;
    private RecyclerView recyclerView;
    private CameraRequestAdapter cameraRequestAdapter;
    private List<CameraRequest> cameraRequestList;

    private DatabaseReference databaseReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference("camera_requests");

        etLocation = root.findViewById(R.id.et_location);
        etType = root.findViewById(R.id.et_type);
        etRequestDate = root.findViewById(R.id.et_request_date);
        recyclerView = root.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cameraRequestList = new ArrayList<>();
        cameraRequestAdapter = new CameraRequestAdapter(cameraRequestList);
        recyclerView.setAdapter(cameraRequestAdapter);

        loadCameraRequestsFromFirebase();

        // Добавление TextWatcher для форматирования даты в EditText
        etRequestDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // Не нужно ничего делать перед изменением текста
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
                String currentText = charSequence.toString();
                if (currentText.length() == 2 || currentText.length() == 5) {
                    // Добавляем точку в нужное место
                    etRequestDate.setText(currentText + ".");
                    etRequestDate.setSelection(etRequestDate.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Можно добавить дополнительные проверки или действия после изменения текста
            }
        });

        // Обработчик нажатия на кнопку для добавления записи в Firebase
        root.findViewById(R.id.btn_add_camera).setOnClickListener(v -> addCameraRequestToFirebase());

        return root;
    }

    private void addCameraRequestToFirebase() {
        String location = etLocation.getText().toString();
        String type = etType.getText().toString();
        String requestDate = etRequestDate.getText().toString();

        if (!location.isEmpty() && !type.isEmpty() && !requestDate.isEmpty()) {
            String id = databaseReference.push().getKey();
            CameraRequest newRequest = new CameraRequest(id, location, type, requestDate, false);

            if (id != null) {
                databaseReference.child(id).setValue(newRequest).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // После добавления записи вызываем метод для обновления данных из Firebase
                        loadCameraRequestsFromFirebase();

                        etLocation.setText("");
                        etType.setText("");
                        etRequestDate.setText("");
                    } else {
                        Log.e("DashboardFragment", "Ошибка при добавлении записи в Firebase");
                    }
                });
            }
        }
    }

    private void loadCameraRequestsFromFirebase() {
        if (childEventListener != null) {
            databaseReference.removeEventListener(childEventListener);
        }

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                CameraRequest request = snapshot.getValue(CameraRequest.class);
                if (request != null && !isRequestAlreadyInList(request)) {
                    cameraRequestList.add(request);
                    cameraRequestAdapter.notifyItemInserted(cameraRequestList.size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                CameraRequest updatedRequest = snapshot.getValue(CameraRequest.class);
                if (updatedRequest != null) {
                    for (int i = 0; i < cameraRequestList.size(); i++) {
                        if (cameraRequestList.get(i).getId().equals(updatedRequest.getId())) {
                            cameraRequestList.set(i, updatedRequest);
                            cameraRequestAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                CameraRequest removedRequest = snapshot.getValue(CameraRequest.class);
                if (removedRequest != null) {
                    for (int i = 0; i < cameraRequestList.size(); i++) {
                        if (cameraRequestList.get(i).getId().equals(removedRequest.getId())) {
                            cameraRequestList.remove(i);
                            cameraRequestAdapter.notifyItemRemoved(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Этот метод не используется
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DashboardFragment", "Ошибка при чтении данных: " + error.getMessage());
            }
        };

        databaseReference.addChildEventListener(childEventListener);
    }

    // Утилита для проверки уникальности записей
    private boolean isRequestAlreadyInList(CameraRequest request) {
        for (CameraRequest existingRequest : cameraRequestList) {
            if (existingRequest.getId().equals(request.getId())) {
                return true;
            }
        }
        return false;
    }
}
