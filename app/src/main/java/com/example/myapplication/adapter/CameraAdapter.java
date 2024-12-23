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

import com.example.myapplication.Maintenances;
import com.example.myapplication.R;
import com.example.myapplication.model.CameraComplex;

import java.util.List;

public class CameraAdapter extends RecyclerView.Adapter<CameraAdapter.CameraViewHolder> {

    private List<CameraComplex> cameraList;

    public CameraAdapter(List<CameraComplex> cameraList) {
        this.cameraList = cameraList;
    }

    @NonNull
    @Override
    public CameraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_camera, parent, false);
        return new CameraViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CameraViewHolder holder, int position) {
        CameraComplex camera = cameraList.get(position);
        holder.tvLocation.setText("Местоположение: " + camera.getLocation());
        holder.tvType.setText("Тип: " + camera.getType());
        holder.tvDate.setText("Дата установки: " + camera.getInstallationDate());
        holder.tvStatus.setText("Активна: " + (camera.isActive() ? "Да" : "Нет"));

        holder.btnMaintenance.setOnClickListener(v -> {
            Context context = holder.itemView.getContext(); // Получаем контекст
            Intent intent = new Intent(context, Maintenances.class); // Создаём Intent
            intent.putExtra("cameraId", camera.getId()); // Передаём данные о камере
            context.startActivity(intent); // Запускаем Activity
        });

    }

    @Override
    public int getItemCount() {
        return cameraList.size();
    }

    public static class CameraViewHolder extends RecyclerView.ViewHolder {
        TextView tvLocation, tvType, tvDate, tvStatus;
        Button btnMaintenance;

        public CameraViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvType = itemView.findViewById(R.id.tv_type);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnMaintenance = itemView.findViewById(R.id.btnMaintenance); // Кнопка "Обслуживание"
        }
    }
}
