package com.example.myapplication.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.CameraRequest;
import com.example.myapplication.model.CameraComplex;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CameraRequestAdapter extends RecyclerView.Adapter<CameraRequestAdapter.ViewHolder> {

    private List<CameraRequest> cameraRequests;
    private DatabaseReference cameraRequestsRef;
    private DatabaseReference cameraComplexRef;

    public CameraRequestAdapter(List<CameraRequest> cameraRequests) {
        this.cameraRequests = cameraRequests;
        this.cameraRequestsRef = FirebaseDatabase.getInstance().getReference("camera_requests");
        this.cameraComplexRef = FirebaseDatabase.getInstance().getReference("cameras");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_camera_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CameraRequest cameraRequest = cameraRequests.get(position);

        holder.locationText.setText(cameraRequest.getLocation());
        holder.typeText.setText(cameraRequest.getCameraType());
        holder.requestDateText.setText(cameraRequest.getRequestDate());

        holder.installButton.setOnClickListener(v -> {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String id = cameraRequest.getId();

            // Создаем новую запись для установленной камеры
            CameraComplex installedCamera = new CameraComplex(
                    id,
                    cameraRequest.getLocation(),
                    cameraRequest.getCameraType(),
                    currentDate,
                    true
            );

            // Добавляем в "camera_complex" и удаляем из "camera_requests"
            cameraComplexRef.child(id).setValue(installedCamera)
                    .addOnSuccessListener(aVoid -> {
                        cameraRequestsRef.child(id).removeValue()
                                .addOnFailureListener(e -> Log.e("CameraRequestAdapter", "Ошибка удаления заявки: " + e.getMessage()));
                    })
                    .addOnFailureListener(e -> Log.e("CameraRequestAdapter", "Ошибка добавления в camera_complex: " + e.getMessage()));
        });
    }



    @Override
    public int getItemCount() {
        return cameraRequests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView locationText, typeText, requestDateText;
        Button installButton;

        public ViewHolder(View itemView) {
            super(itemView);
            locationText = itemView.findViewById(R.id.tv_location);
            typeText = itemView.findViewById(R.id.tv_type);
            requestDateText = itemView.findViewById(R.id.tv_request_date);
            installButton = itemView.findViewById(R.id.btn_install);
        }
    }
}
