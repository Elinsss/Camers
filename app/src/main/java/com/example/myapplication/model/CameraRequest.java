package com.example.myapplication.model;

public class CameraRequest {
    private String id; // Уникальный идентификатор
    private String location; // Местоположение
    private String cameraType; // Тип камеры
    private String requestDate; // Дата запроса
    private boolean isInstalled; // Установлена ли камера

    public CameraRequest() {
        // Пустой конструктор для Firebase
    }

    public CameraRequest(String id, String location, String cameraType, String requestDate, boolean isInstalled) {
        this.id = id;
        this.location = location;
        this.cameraType = cameraType;
        this.requestDate = requestDate;
        this.isInstalled = isInstalled;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCameraType() { return cameraType; }
    public void setCameraType(String cameraType) { this.cameraType = cameraType; }

    public String getRequestDate() { return requestDate; }
    public void setRequestDate(String requestDate) { this.requestDate = requestDate; }

    public boolean isInstalled() { return isInstalled; }
    public void setInstalled(boolean installed) { isInstalled = installed; }
}
