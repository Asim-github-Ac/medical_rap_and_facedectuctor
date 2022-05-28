package com.example.medical_rap_tracker.Model;

public class EmpModel {
    String fullnamre,type,email,face;


    public EmpModel(String fullnamre, String type, String email, String face) {
        this.fullnamre = fullnamre;
        this.type = type;
        this.email = email;
        this.face = face;
    }

    public EmpModel() {
    }

    public String getFullnamre() {
        return fullnamre;
    }

    public void setFullnamre(String fullnamre) {
        this.fullnamre = fullnamre;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }
}
