package Patient;
import java.time.LocalDateTime;
import Hospital.Doctor;

public class Appointment {
    private LocalDateTime appointmentDate;
    private Doctor doctorAssigned;
    private Patient patient;
    private String status;

    public Appointment(Patient patient, Doctor doctorAssigned, LocalDateTime appointmentDate) {
        this.patient = patient;
        this.doctorAssigned = doctorAssigned;
        this.appointmentDate = appointmentDate;
        this.status = "upcoming";
    }

    public void scheduleAppointment(LocalDateTime newDate) {
        if (newDate != null) this.appointmentDate = newDate;
        this.status = "upcoming";
    }

    public void cancelAppointment() {
        this.status = "cancelled";
    }

    public void rescheduleAppointment(LocalDateTime newDate) {
        if (newDate != null) this.appointmentDate = newDate;
        this.status = "upcoming";
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public Doctor getDoctorAssigned() {
        return doctorAssigned;
    }

    public Patient getPatient() {
        return patient;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

