package com.generacionimpacto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Reservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnoreProperties({"announcements", "reservations", "scholarships", "notifications", "payments", "schedules", "password"})
    private User student;
    
    @ManyToOne
    @JoinColumn(name = "announcement_id", nullable = false)
    @JsonIgnoreProperties({"reservations", "tutor"})
    private TutorshipAnnouncement announcement;
    
    private LocalDateTime dateTime;
    
    private boolean paid = false;
    
    private boolean scholarshipUsed = false;
    
    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"reservation"})
    private Payment payment;
    
    public Reservation() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getStudent() {
        return student;
    }
    
    public void setStudent(User student) {
        this.student = student;
    }
    
    public TutorshipAnnouncement getAnnouncement() {
        return announcement;
    }
    
    public void setAnnouncement(TutorshipAnnouncement announcement) {
        this.announcement = announcement;
    }
    
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
    
    public boolean isPaid() {
        return paid;
    }
    
    public void setPaid(boolean paid) {
        this.paid = paid;
    }
    
    public boolean isScholarshipUsed() {
        return scholarshipUsed;
    }
    
    public void setScholarshipUsed(boolean scholarshipUsed) {
        this.scholarshipUsed = scholarshipUsed;
    }
    
    public Payment getPayment() {
        return payment;
    }
    
    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}




