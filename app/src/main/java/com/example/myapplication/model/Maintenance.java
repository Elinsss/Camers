package com.example.myapplication.model;

public class Maintenance {
    private String id; // Уникальный идентификатор
    private String cameraId; // ID камеры
    private String maintenanceDate; // Дата обслуживания
    private String maintenanceType; // Тип обслуживания
    private String notes; // Заметки

    // Пустой конструктор (обязателен для Firebase)
    public Maintenance() {
    }

    // Конструктор с параметрами
    public Maintenance(String id, String cameraId, String maintenanceDate, String maintenanceType, String notes) {
        this.id = id;
        this.cameraId = cameraId;
        this.maintenanceDate = maintenanceDate;
        this.maintenanceType = maintenanceType;
        this.notes = notes;
    }

    // Геттеры и сеттеры
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCameraId() {
        return cameraId;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    public String getMaintenanceDate() {
        return maintenanceDate;
    }

    public void setMaintenanceDate(String maintenanceDate) {
        this.maintenanceDate = maintenanceDate;
    }

    public String getMaintenanceType() {
        return maintenanceType;
    }

    public void setMaintenanceType(String maintenanceType) {
        this.maintenanceType = maintenanceType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
