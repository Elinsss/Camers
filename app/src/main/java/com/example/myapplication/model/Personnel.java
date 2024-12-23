package com.example.myapplication.model;

public class Personnel {

    private String id; // Уникальный идентификатор, который будет выдан Firebase
    private String name; // Имя сотрудника
    private String position; // Должность
    private String contact; // Контактная информация

    public Personnel() {
        // Пустой конструктор для Firebase
    }

    public Personnel(String id, String name, String position, String contact) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.contact = contact;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
}
