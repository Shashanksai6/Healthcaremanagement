package com.sjsu.HealthConnect.dto;

import com.sjsu.HealthConnect.entity.Medication;

import java.util.List;

public class Prescription {
    private int appointmentId;
    private List<Medication> prescription;

    public Prescription(int appointmentId, List<Medication> prescription) {
        this.appointmentId = appointmentId;
        this.prescription = prescription;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public List<Medication> getPrescription() {
        return prescription;
    }

    public void setPrescription(List<Medication> prescription) {
        this.prescription = prescription;
    }
}
