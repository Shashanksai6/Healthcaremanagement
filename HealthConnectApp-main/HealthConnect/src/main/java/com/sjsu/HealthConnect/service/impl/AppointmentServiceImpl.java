package com.sjsu.HealthConnect.service.impl;

import java.util.List;

import com.sjsu.HealthConnect.dao.AppointmentDao;
import com.sjsu.HealthConnect.entity.Appointment;
import com.sjsu.HealthConnect.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;



@Repository
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentDao appointmentDao;

    @Override
    public Appointment addAppointment(Appointment appointment) {
        return appointmentDao.save(appointment);
    }

    @Override
    public Appointment updateAppointment(Appointment appointment) {
        return appointmentDao.save(appointment);
    }

    @Override
    public Appointment getAppointmentById(int id) {
        return appointmentDao.findById(id).get();
    }

    @Override
    public List<Appointment> getAllAppointment() {
        return appointmentDao.findAll();
    }

    @Override
    public List<Appointment> getAppointmentByPatientId(int patiendId) {
        return appointmentDao.findByPatientId(patiendId);
    }

    @Override
    public List<Appointment> getAppointmentByDoctorId(int doctorId) {
        return appointmentDao.findByDoctorId(doctorId);
    }


}

