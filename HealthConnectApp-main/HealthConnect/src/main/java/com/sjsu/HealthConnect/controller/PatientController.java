package com.sjsu.HealthConnect.controller;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sjsu.HealthConnect.entity.User;
import com.sjsu.HealthConnect.service.UserService;
import com.sjsu.HealthConnect.util.Constants.BloodGroup;
import com.sjsu.HealthConnect.util.Constants.UserRole;

@RestController
@RequestMapping("api/patient")
@CrossOrigin(origins = "http://localhost:3000")
public class PatientController {

    Logger LOG = LoggerFactory.getLogger(PatientController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/bloodgroup")
    public ResponseEntity<?> getAllBloodGroups() {

        LOG.info("Received the request for getting all the Blood Groups");

        List<String> bloodGroups = new ArrayList<>();

        for (BloodGroup bg : BloodGroup.values()) {
            bloodGroups.add(bg.value());
        }

        LOG.info("Response Sent!!!");

        return new ResponseEntity(bloodGroups, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<?> getAllPatient(@RequestHeader("Authorization") String token) {
        LOG.info("recieved request for getting ALL Customer!!!");
        if(!userService.isAuthorized(token, UserRole.ADMIN.value())){
            return new ResponseEntity("User cannot perform this action", HttpStatus.FORBIDDEN);
        }
        List<User> patients = this.userService.getAllUserByRole(UserRole.PATIENT.value());

        LOG.info("response sent!!!");
        return ResponseEntity.ok(patients);
    }


}
