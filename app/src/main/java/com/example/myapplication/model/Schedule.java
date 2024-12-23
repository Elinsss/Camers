package com.example.myapplication.model;

public class Schedule {
    private String id; // Уникальный идентификатор
    private String personnelId; // ID сотрудника
    private String workDate; // Дата работы
    private String shift; // Смена

    public Schedule() {
        // Пустой конструктор для Firebase
    }

    public Schedule(String id, String personnelId, String workDate, String shift) {
        this.id = id;
        this.personnelId = personnelId;
        this.workDate = workDate;
        this.shift = shift;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPersonnelId() { return personnelId; }
    public void setPersonnelId(String personnelId) { this.personnelId = personnelId; }

    public String getWorkDate() { return workDate; }
    public void setWorkDate(String workDate) { this.workDate = workDate; }

    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }
}
