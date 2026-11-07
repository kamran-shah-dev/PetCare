package application;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Appointments implements Serializable {
    private String appointmentType;
    private LocalDateTime appointmentDateAndTime;
    private String notes;

    public Appointments(String appointmentType, LocalDateTime appointmentDateAndTime, String notes) {
        this.appointmentType = appointmentType;
        this.appointmentDateAndTime = appointmentDateAndTime;
        this.notes = notes;
    }

    public LocalDateTime getAppointmentDateAndTime() {
        return appointmentDateAndTime;
    }

    public String getAppointmentType() {
        return appointmentType;
    }

    public String getNotes() {
        return notes;
    }

    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }

    public void setAppointmentDateAndTime(LocalDateTime appointmentDateAndTime) {
        this.appointmentDateAndTime = appointmentDateAndTime;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Appointments{" +
                "appointmentType='" + appointmentType + '\'' +
                ", appointmentDateAndTime=" + appointmentDateAndTime +
                ", notes='" + notes + '\'' +
                '}';
    }
}
