package com.sjsu.HealthConnect.dao;

import java.util.List;

import com.sjsu.HealthConnect.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AppointmentDao extends JpaRepository<Appointment, Integer>  {

    List<Appointment> findByPatientId(int patientId);
    List<Appointment> findByDoctorId(int doctorId);


}
