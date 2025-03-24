package com.sjsu.HealthConnect.service;


import com.sjsu.HealthConnect.entity.Appointment;

import java.util.List;

public interface AppointmentService {
    public Appointment addAppointment(Appointment appointment) ;

    public Appointment updateAppointment(Appointment appointment) ;

    public Appointment getAppointmentById(int id);

    public List<Appointment> getAllAppointment();

    public List<Appointment> getAppointmentByPatientId(int patiendId);

    public List<Appointment> getAppointmentByDoctorId(int doctorId);

}
