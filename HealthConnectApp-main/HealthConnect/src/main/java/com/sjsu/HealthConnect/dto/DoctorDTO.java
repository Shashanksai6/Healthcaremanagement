package com.sjsu.HealthConnect.dto;

public class DoctorDTO {
    private int id;
    private String firstName;

    private String lastName;
    private String specialist;
    private int experience;

    public int getId() {
        return id;
    }

    public DoctorDTO(int id, String firstName, String lastName, String specialist, int experience) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialist = specialist;
        this.experience = experience;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSpecialist() {
        return specialist;
    }

    public void setSpecialist(String specialist) {
        this.specialist = specialist;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }
}
