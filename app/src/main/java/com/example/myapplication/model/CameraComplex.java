package com.example.myapplication.model;

public class CameraComplex {
    private String id; // Уникальный идентификатор
    private String location; // Местоположение
    private String type; // Тип камеры
    private String installationDate; // Дата установки
    private boolean isActive; // Активна ли камера

    public CameraComplex() {
        // Пустой конструктор для Firebase
    }

    public CameraComplex(String id, String location, String type, String installationDate, boolean isActive) {
        this.id = id;
        this.location = location;
        this.type = type;
        this.installationDate = installationDate;
        this.isActive = isActive;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getInstallationDate() { return installationDate; }
    public void setInstallationDate(String installationDate) { this.installationDate = installationDate; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
