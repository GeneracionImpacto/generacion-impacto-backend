package com.generacionimpacto.controller;

import com.generacionimpacto.config.SecurityUtil;
import com.generacionimpacto.dto.ScheduleDTO;
import com.generacionimpacto.dto.TutorshipRequestDTO;
import com.generacionimpacto.model.*;
import com.generacionimpacto.repository.UserRepository;
import com.generacionimpacto.service.TutorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/tutor")
@CrossOrigin(origins = "*")
public class TutorController {
    
    @Autowired
    private TutorService tutorService;
    
    @Autowired
    private UserRepository userRepository;
    
    private Long getCurrentUserId() {
        String email = SecurityUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getId();
    }
    
    @PostMapping("/tutorship-requests")
    public ResponseEntity<?> createTutorshipRequest(@Valid @RequestBody TutorshipRequestDTO dto) {
        try {
            TutorshipRequest request = tutorService.createTutorshipRequest(getCurrentUserId(), dto);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/announcements")
    public ResponseEntity<List<TutorshipAnnouncement>> getMyAnnouncements() {
        return ResponseEntity.ok(tutorService.getMyAnnouncements(getCurrentUserId()));
    }
    
    @GetMapping("/notifications")
    public ResponseEntity<List<Notification>> getNotifications() {
        return ResponseEntity.ok(tutorService.getNotifications(getCurrentUserId()));
    }
    
    @GetMapping("/finance/total")
    public ResponseEntity<BigDecimal> getTotalEarnings() {
        return ResponseEntity.ok(tutorService.getTotalEarnings(getCurrentUserId()));
    }
    
    @GetMapping("/finance/payments")
    public ResponseEntity<List<Payment>> getPayments() {
        return ResponseEntity.ok(tutorService.getPayments(getCurrentUserId()));
    }
    
    @GetMapping("/students")
    public ResponseEntity<List<User>> getMyStudents() {
        return ResponseEntity.ok(tutorService.getMyStudents(getCurrentUserId()));
    }
    
    @GetMapping("/schedule/reservations")
    public ResponseEntity<List<Reservation>> getReservations() {
        return ResponseEntity.ok(tutorService.getReservations(getCurrentUserId()));
    }
    
    @GetMapping("/schedule/availability")
    public ResponseEntity<List<Schedule>> getSchedule() {
        return ResponseEntity.ok(tutorService.getSchedule(getCurrentUserId()));
    }
    
    @PostMapping("/schedule/availability")
    public ResponseEntity<?> addAvailableHours(@Valid @RequestBody Schedule schedule) {
        try {
            Schedule savedSchedule = tutorService.addAvailableHours(getCurrentUserId(), schedule);
            return ResponseEntity.ok(savedSchedule);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}




