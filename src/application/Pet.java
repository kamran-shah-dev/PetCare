package application;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pet implements Serializable {
    private String petId;
    private String name;
    private String specieOrBreed;
    private int Age;
    private String ownerName;
    private LocalDate registrationDate;
    private String email;
    private String phoneNumber;
    private List<Appointments> appointmentsList;

    public Pet(String petId, String name, String specieOrBreed, int age, String ownerName, String email, String phoneNumber) {
        this.petId = petId;
        this.name = name;
        this.specieOrBreed = specieOrBreed;
        Age = age;
        this.ownerName = ownerName;
        this.registrationDate = LocalDate.now();
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.appointmentsList = new ArrayList<>();
    }

    public String getPetId() {
        return petId;
    }

    public String getName() {
        return name;
    }

    public String getSpecieOrBreed() {
        return specieOrBreed;
    }

    public int getAge() {
        return Age;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public List<Appointments> getAppointmentsList() {
        return appointmentsList;
    }

    public int totalAppointments() {
        return this.appointmentsList.size();
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSpecieOrBreed(String specieOrBreed) {
        this.specieOrBreed = specieOrBreed;
    }

    public void setAge(int age) {
        Age = age;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAppointmentsList(List<Appointments> appointmentsList) {
        this.appointmentsList = appointmentsList;
    }

    @Override
    public String toString() {
        return "Pet{" +
                "petId='" + petId + '\'' +
                ", name='" + name + '\'' +
                ", specieOrBreed='" + specieOrBreed + '\'' +
                ", Age=" + Age +
                ", ownerName='" + ownerName + '\'' +
                ", registrationDate=" + registrationDate +
                '}';
    }
}
