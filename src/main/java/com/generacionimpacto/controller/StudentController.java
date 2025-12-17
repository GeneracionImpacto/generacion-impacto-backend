package com.generacionimpacto.controller;

import com.generacionimpacto.config.SecurityUtil;
import com.generacionimpacto.dto.ReservationDTO;
import com.generacionimpacto.dto.ScholarshipRequestDTO;
import com.generacionimpacto.model.*;
import com.generacionimpacto.repository.UserRepository;
import com.generacionimpacto.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "*")
public class StudentController {
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private UserRepository userRepository;
    
    private Long getCurrentUserId() {
        String email = SecurityUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getId();
    }
    
    @GetMapping("/announcements")
    public ResponseEntity<List<TutorshipAnnouncement>> getAllAnnouncements() {
        return ResponseEntity.ok(studentService.getAllAnnouncements());
    }

    @GetMapping("/announcements/{announcementId}/available-slots")
    public ResponseEntity<?> getAvailableSlots(@PathVariable Long announcementId, @RequestParam String date) {
        try {
            LocalDate parsed = LocalDate.parse(date);
            return ResponseEntity.ok(studentService.getAvailableSlots(announcementId, getCurrentUserId(), parsed));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/reservations")
    public ResponseEntity<?> bookTutorship(@Valid @RequestBody ReservationDTO dto) {
        try {
            Reservation reservation = studentService.bookTutorship(getCurrentUserId(), dto);
            return ResponseEntity.ok(reservation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/scholarships")
    public ResponseEntity<?> requestScholarship(@Valid @RequestBody ScholarshipRequestDTO dto) {
        try {
            Scholarship scholarship = studentService.requestScholarship(getCurrentUserId(), dto);
            return ResponseEntity.ok(scholarship);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/notifications")
    public ResponseEntity<List<Notification>> getNotifications() {
        return ResponseEntity.ok(studentService.getNotifications(getCurrentUserId()));
    }
    
    @GetMapping("/reservations")
    public ResponseEntity<List<Reservation>> getMyReservations() {
        return ResponseEntity.ok(studentService.getMyReservations(getCurrentUserId()));
    }
    
    @GetMapping("/tutors")
    public ResponseEntity<List<User>> getMyTutors() {
        return ResponseEntity.ok(studentService.getMyTutors(getCurrentUserId()));
    }
}

