package com.example.myapplication.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.AddCamera;
import com.example.myapplication.R;
import com.example.myapplication.adapter.CameraAdapter;
import com.example.myapplication.databinding.FragmentHomeBinding;
import com.example.myapplication.model.CameraComplex;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private CameraAdapter cameraAdapter;
    private List<CameraComplex> cameraList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Инициализация RecyclerView
        cameraList = new ArrayList<>();
        cameraAdapter = new CameraAdapter(cameraList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(cameraAdapter);

        // Загрузка данных из Firebase
        loadCamerasFromFirebase();

        // Обработчик нажатия кнопки
        binding.AddCamera.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddCamera.class);
            startActivity(intent);
        });

        return root;
    }

    private void loadCamerasFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("cameras");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cameraList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CameraComplex camera = dataSnapshot.getValue(CameraComplex.class);
                    if (camera != null) {
                        cameraList.add(camera);
                    }
                }
                cameraAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Обработка ошибок
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
