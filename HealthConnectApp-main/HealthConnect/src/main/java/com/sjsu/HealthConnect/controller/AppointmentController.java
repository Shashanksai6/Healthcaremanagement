package com.sjsu.HealthConnect.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.sjsu.HealthConnect.dto.CommanApiResponse;
import com.sjsu.HealthConnect.dto.Prescription;
import com.sjsu.HealthConnect.dto.UpdateAppointmentRequest;
import com.sjsu.HealthConnect.entity.Appointment;
import com.sjsu.HealthConnect.entity.User;
import com.sjsu.HealthConnect.exception.AppointmentNotFoundException;
import com.sjsu.HealthConnect.service.AppointmentService;
import com.sjsu.HealthConnect.dto.AppointmentResponseDto;
import com.sjsu.HealthConnect.service.UserService;
import com.sjsu.HealthConnect.util.Constants.*;
import com.sjsu.HealthConnect.util.EmailNotificationHelper;
import com.sjsu.HealthConnect.util.JwtUtil;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/appointment")
@CrossOrigin(origins = "http://localhost:3000")
public class AppointmentController {

    Logger LOG = LoggerFactory.getLogger(AppointmentController.class);

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailNotificationHelper notificationHelper;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("")
    @ApiOperation(value = "Api to add patient appointment")
    public ResponseEntity<?> addAppointment(@RequestBody Appointment appointment, @RequestHeader("Authorization") String token) {
        LOG.info("Recieved request to add patient appointment");

        if(!(userService.isAuthorized(token, UserRole.PATIENT.value()) && userService.isAuthorized(token, appointment.getPatientId()))){
            return new ResponseEntity("User cannot perform this action", HttpStatus.FORBIDDEN);
        }

        CommanApiResponse response = new CommanApiResponse();

        if (appointment == null) {
            response.setResponseCode(ResponseCode.FAILED.value());
            response.setResponseMessage("Failed to add patient appointment");
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }

        if (appointment.getPatientId() == 0) {
            response.setResponseCode(ResponseCode.FAILED.value());
            response.setResponseMessage("Failed to add patient appointment");
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }

        appointment.setDate(LocalDate.now().toString());
        appointment.setStatus(AppointmentStatus.SCHEDULED.value());

        Appointment addedAppointment = appointmentService.addAppointment(appointment);

        if (addedAppointment != null) {
            response.setResponseCode(ResponseCode.SUCCESS.value());
            response.setResponseMessage("Appointment Added");
            notificationHelper.sendApptCreateEmail(addedAppointment);
            return new ResponseEntity(response, HttpStatus.OK);
        }

        else {
            response.setResponseCode(ResponseCode.FAILED.value());
            response.setResponseMessage("Failed to add Appointment");
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("")
    public ResponseEntity<?> getAllAppointments(@RequestHeader("Authorization") String token) {
        LOG.info("recieved request for getting ALL Appointments !!!");
        if(!userService.isAuthorized(token, UserRole.ADMIN.value())){
            return new ResponseEntity("User cannot perform this action", HttpStatus.FORBIDDEN);
        }

        List<Appointment> appointments = this.appointmentService.getAllAppointment();

        List<AppointmentResponseDto> response = new ArrayList();

        for (Appointment appointment : appointments) {

            AppointmentResponseDto a = new AppointmentResponseDto();

            User patient = this.userService.getUserById(appointment.getPatientId());

            a.setPatientContact(patient.getContact());
            a.setPatientId(patient.getId());
            a.setPatientName(patient.getFirstName() + " " + patient.getLastName());

            if (appointment.getDoctorId() != 0) {
                User doctor = this.userService.getUserById(appointment.getDoctorId());
                a.setDoctorContact(doctor.getContact());
                a.setDoctorName(doctor.getFirstName() + " " + doctor.getLastName());
                a.setDoctorId(doctor.getId());
                a.setPrescription(appointment.getPrescription());

              //  if (appointment.getStatus().equals(AppointmentStatus.TREATMENT_DONE.value())) {
                    a.setPrice(appointment.getPrice());
             /*   }

                else {
                    a.setPrice(200);
                }*/
            }

            else {
                //a.setDoctorContact(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
                //a.setDoctorName(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
                a.setDoctorId(0);
                //a.setPrice(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
                //a.setPrescription(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
            }

            a.setStatus(appointment.getStatus());
            a.setProblem(appointment.getProblem());
            a.setDate(appointment.getDate());
            a.setAppointmentDate(appointment.getAppointmentDate());
            a.setId(appointment.getId());

            response.add(a);
        }

        LOG.info("response sent!!!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/id")
    public ResponseEntity<?> getAllAppointments(@RequestParam("id") int appointmentId) {
        LOG.info("recieved request for getting  Appointment by id !!!");

        Appointment appointment = this.appointmentService.getAppointmentById(appointmentId);

        if (appointment == null) {
            throw new AppointmentNotFoundException();
        }

        AppointmentResponseDto a = new AppointmentResponseDto();

        User patient = this.userService.getUserById(appointment.getPatientId());

        a.setPatientContact(patient.getContact());
        a.setPatientId(patient.getId());
        a.setPatientName(patient.getFirstName() + " " + patient.getLastName());

        if (appointment.getDoctorId() != 0) {
            User doctor = this.userService.getUserById(appointment.getDoctorId());
            a.setDoctorContact(doctor.getContact());
            a.setDoctorName(doctor.getFirstName() + " " + doctor.getLastName());
            a.setDoctorId(doctor.getId());
            a.setPrescription(appointment.getPrescription());

           // if (appointment.getStatus().equals(AppointmentStatus.TREATMENT_DONE.value())) {
                a.setPrice(appointment.getPrice());
          /*  }

            else {
                a.setPrice(200);
            }*/

        }

        else {
          /*  a.setDoctorContact(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
            a.setDoctorName(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());*/
            a.setDoctorId(0);
            a.setPrice(200);
            //a.setPrescription(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());

        }

        a.setStatus(appointment.getStatus());
        a.setProblem(appointment.getProblem());
        a.setDate(appointment.getDate());
        a.setAppointmentDate(appointment.getAppointmentDate());
        a.setBloodGroup(patient.getBloodGroup());
        a.setId(appointment.getId());

        LOG.info("response sent!!!");
        return ResponseEntity.ok(a);
    }

    @GetMapping("patient")
    public ResponseEntity<?> getAllAppointmentsByPatientId(@RequestParam("id") int patientId, @RequestHeader("Authorization") String token) {
        LOG.info("recieved request for getting ALL Appointments by patient Id !!!");
        if(!userService.isAuthorized(token, patientId)){
            return new ResponseEntity("User cannot perform this action", HttpStatus.FORBIDDEN);
        }

        List<Appointment> appointments = this.appointmentService.getAppointmentByPatientId(patientId);

        List<AppointmentResponseDto> response = new ArrayList();

        for (Appointment appointment : appointments) {

            AppointmentResponseDto a = new AppointmentResponseDto();

            User patient = this.userService.getUserById(appointment.getPatientId());

            a.setPatientContact(patient.getContact());
            a.setPatientId(patient.getId());
            a.setPatientName(patient.getFirstName() + " " + patient.getLastName());

            if (appointment.getDoctorId() != 0) {
                User doctor = this.userService.getUserById(appointment.getDoctorId());
                a.setDoctorContact(doctor.getContact());
                a.setDoctorName(doctor.getFirstName() + " " + doctor.getLastName());
                a.setDoctorId(doctor.getId());
                a.setPrescription(appointment.getPrescription());

              //  if (appointment.getStatus().equals(AppointmentStatus.TREATMENT_DONE.value())) {
                    a.setPrice(appointment.getPrice());
               /* }

                else {
                    a.setPrice(200);
                }*/

            }

            else {
                a.setDoctorContact(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
                a.setDoctorName(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
                a.setDoctorId(0);
                a.setPrice(200);
               // a.setPrescription(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());

            }

            a.setStatus(appointment.getStatus());
            a.setProblem(appointment.getProblem());
            a.setDate(appointment.getDate());
            a.setAppointmentDate(appointment.getAppointmentDate());
            a.setBloodGroup(patient.getBloodGroup());
            a.setId(appointment.getId());

            response.add(a);

        }

        LOG.info("response sent!!!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("doctor")
    public ResponseEntity<?> getAllAppointmentsByDoctorId(@RequestParam("id") int doctorId, @RequestHeader("Authorization") String token) {
        LOG.info("recieved request for getting ALL Appointments by doctor Id !!!");

        if(!userService.isAuthorized(token, doctorId)){
            return new ResponseEntity("User cannot perform this action", HttpStatus.FORBIDDEN);
        }

        List<Appointment> appointments = this.appointmentService.getAppointmentByDoctorId(doctorId);

        List<AppointmentResponseDto> response = new ArrayList();

        for (Appointment appointment : appointments) {

            AppointmentResponseDto a = new AppointmentResponseDto();

            User patient = this.userService.getUserById(appointment.getPatientId());

            a.setPatientContact(patient.getContact());
            a.setPatientId(patient.getId());
            a.setPatientName(patient.getFirstName() + " " + patient.getLastName());

            if (appointment.getDoctorId() != 0) {
                User doctor = this.userService.getUserById(appointment.getDoctorId());
                a.setDoctorContact(doctor.getContact());
                a.setDoctorName(doctor.getFirstName() + " " + doctor.getLastName());
                a.setDoctorId(doctor.getId());
                a.setPrescription(appointment.getPrescription());

                //if (appointment.getStatus().equals(AppointmentStatus.TREATMENT_DONE.value())) {
                    a.setPrice(appointment.getPrice());
                /*}

                else {
                    a.setPrice(200);
                }*/
                a.setPrescription(appointment.getPrescription());

            }

            else {
                a.setDoctorContact(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
                a.setDoctorName(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
                a.setDoctorId(0);
                a.setPrice(200);
               // a.setPrescription(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
            }

            a.setStatus(appointment.getStatus());
            a.setProblem(appointment.getProblem());
            a.setDate(appointment.getDate());
            a.setAppointmentDate(appointment.getAppointmentDate());
            a.setBloodGroup(patient.getBloodGroup());
            a.setId(appointment.getId());

            response.add(a);

        }

        LOG.info("response sent!!!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/doctor")
    @ApiOperation(value = "Api to assign appointment to doctor")
    public ResponseEntity<?> updateAppointmentStatus(UpdateAppointmentRequest request) {
        LOG.info("Recieved request to assign appointment to doctor");

        CommanApiResponse response = new CommanApiResponse();

        if (request == null) {
            response.setResponseCode(ResponseCode.FAILED.value());
            response.setResponseMessage("Failed to assign appointment");
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }

        if (request.getDoctorId() == 0) {
            response.setResponseCode(ResponseCode.FAILED.value());
            response.setResponseMessage("Doctor not found");
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }

        User doctor = this.userService.getUserById(request.getDoctorId());

        if (doctor == null) {
            response.setResponseCode(ResponseCode.FAILED.value());
            response.setResponseMessage("Doctor not found");
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }

        if (request.getAppointmentId() == 0) {
            response.setResponseCode(ResponseCode.FAILED.value());
            response.setResponseMessage("Appointment not found");
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }

        Appointment appointment = appointmentService.getAppointmentById(request.getAppointmentId());

        if (appointment == null) {
            throw new AppointmentNotFoundException();
        }

        if (appointment.getStatus().equals(AppointmentStatus.CANCEL.value())) {
            response.setResponseCode(ResponseCode.FAILED.value());
            response.setResponseMessage("Appointment is cancel by patient");
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }

        appointment.setDoctorId(request.getDoctorId());
        appointment.setStatus(AppointmentStatus.ASSIGNED_TO_DOCTOR.value());

        Appointment updatedAppointment = this.appointmentService.addAppointment(appointment);

        if (updatedAppointment != null) {
            response.setResponseCode(ResponseCode.SUCCESS.value());
            response.setResponseMessage("Successfully Assigned Appointment to doctor");
            return new ResponseEntity(response, HttpStatus.OK);
        }

        else {
            response.setResponseCode(ResponseCode.FAILED.value());
            response.setResponseMessage("Failed to assign");
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("doctor/update")
    @ApiOperation(value = "Api to assign appointment to doctor")
    public ResponseEntity<?> assignAppointmentToDoctor(UpdateAppointmentRequest request) {
        LOG.info("Recieved request to update appointment");

        CommanApiResponse response = new CommanApiResponse();

        if (request == null) {
            response.setResponseCode(ResponseCode.FAILED.value());
            response.setResponseMessage("Failed to assign appointment");
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }

        if (request.getAppointmentId() == 0) {
            response.setResponseCode(ResponseCode.FAILED.value());
            response.setResponseMessage("Appointment not found");
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }

        Appointment appointment = appointmentService.getAppointmentById(request.getAppointmentId());

        if (appointment == null) {
            throw new AppointmentNotFoundException();
        }

        //appointment.setPrescription(request.getPrescription());
        appointment.setStatus(request.getStatus());

        //if (request.getStatus().equals(AppointmentStatus.TREATMENT_DONE.value())) {
            appointment.setPrice(request.getPrice());
        //}

        Appointment updatedAppointment = this.appointmentService.addAppointment(appointment);

        if (updatedAppointment != null) {
            response.setResponseCode(ResponseCode.SUCCESS.value());
            response.setResponseMessage("Updated Treatment Status");
            return new ResponseEntity(response, HttpStatus.OK);
        }

        else {
            response.setResponseCode(ResponseCode.FAILED.value());
            response.setResponseMessage("Failed to update");
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("")
    @ApiOperation(value = "Api to update appointment patient")
    public ResponseEntity<?> udpateAppointmentStatus(@RequestBody UpdateAppointmentRequest request) {
        LOG.info("Recieved request to update appointment");

        CommanApiResponse response = new CommanApiResponse();

        if (request == null) {
            response.setResponseCode(ResponseCode.FAILED.value());
            response.setResponseMessage("Failed to assign appointment");
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }

        if (request.getAppointmentId() == 0) {
            response.setResponseCode(ResponseCode.FAILED.value());
            response.setResponseMessage("Appointment not found");
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }

        Appointment appointment = appointmentService.getAppointmentById(request.getAppointmentId());

        if (appointment == null) {
            throw new AppointmentNotFoundException();
        }

        appointment.setStatus(request.getStatus());
        appointment.setPrice(request.getPrice());
        Appointment updatedAppointment = this.appointmentService.addAppointment(appointment);

        if (updatedAppointment != null) {
            response.setResponseCode(ResponseCode.SUCCESS.value());
            notificationHelper.sendApptUpdateEmail(appointment);
            response.setResponseMessage("Updated Treatment Status");
            return new ResponseEntity(response, HttpStatus.OK);
        }

        else {
            response.setResponseCode(ResponseCode.FAILED.value());
            response.setResponseMessage("Failed to update");
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Api to add patient appointment")
    public ResponseEntity<?> deleteAppointment(@PathVariable int id, @RequestHeader("Authorization") String token) {
        LOG.info("Recieved request to cancel appointment");
        System.out.println("Token: " + token);
        String username = jwtUtil.extractUsername(token.substring(7));
        System.out.println("username:  " + username );
        Appointment appointment = appointmentService.getAppointmentById(id);

        if(!(userService.isAuthorized(token, appointment.getDoctorId()) || userService.isAuthorized(token, appointment.getPatientId()))){
            return new ResponseEntity("User cannot perform this action", HttpStatus.FORBIDDEN);
        }

        if (appointment == null) {
            throw new AppointmentNotFoundException();
        }

        appointment.setStatus(AppointmentStatus.CANCEL.value());
        appointmentService.updateAppointment(appointment);
        notificationHelper.sendApptCancelEmail(appointment);
        return new ResponseEntity("Successfully cancelled appointment!", HttpStatus.OK);
    }


    @PostMapping("/prescribe")
    @ApiOperation(value = "Api to add patient appointment")
    public ResponseEntity<?> sendpresciption(@RequestBody Prescription prescription, @RequestHeader("Authorization") String token) {
        LOG.info("Recieved request to prescribe medicine");
        if(!userService.isAuthorized(token, UserRole.DOCTOR.value())){
            return new ResponseEntity("User cannot perform this action", HttpStatus.FORBIDDEN);
        }
        notificationHelper.sendPrescription(prescription);
        return new ResponseEntity(null, HttpStatus.OK);
    }

}
