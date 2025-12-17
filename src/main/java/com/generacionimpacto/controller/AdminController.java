package com.generacionimpacto.controller;

import com.generacionimpacto.model.*;
import com.generacionimpacto.repository.TutorshipAnnouncementRepository;
import com.generacionimpacto.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private TutorshipAnnouncementRepository announcementRepository;
    
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Long>> getStatistics() {
        return ResponseEntity.ok(adminService.getStatistics());
    }
    
    @GetMapping("/tutors")
    public ResponseEntity<List<User>> getAllTutors() {
        return ResponseEntity.ok(adminService.getAllTutors());
    }
    
    @GetMapping("/tutors/{tutorId}/announcements")
    public ResponseEntity<List<TutorshipAnnouncement>> getTutorAnnouncements(@PathVariable Long tutorId) {
        User tutor = new User();
        tutor.setId(tutorId);
        return ResponseEntity.ok(announcementRepository.findByTutor(tutor));
    }
    
    @GetMapping("/students")
    public ResponseEntity<List<User>> getAllStudents() {
        return ResponseEntity.ok(adminService.getAllStudents());
    }
    
    @GetMapping("/notifications/tutorship-requests")
    public ResponseEntity<List<TutorshipRequest>> getPendingTutorshipRequests() {
        return ResponseEntity.ok(adminService.getPendingTutorshipRequests());
    }
    
    @GetMapping("/notifications/scholarships")
    public ResponseEntity<List<Scholarship>> getPendingScholarships() {
        return ResponseEntity.ok(adminService.getPendingScholarships());
    }
    
    @PostMapping("/tutorship-requests/{requestId}/approve")
    public ResponseEntity<?> approveTutorshipRequest(@PathVariable Long requestId) {
        try {
            TutorshipAnnouncement announcement = adminService.approveTutorshipRequest(requestId);
            return ResponseEntity.ok(announcement);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/tutorship-requests/{requestId}/reject")
    public ResponseEntity<?> rejectTutorshipRequest(@PathVariable Long requestId) {
        try {
            adminService.rejectTutorshipRequest(requestId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/scholarships/{scholarshipId}/approve")
    public ResponseEntity<?> approveScholarship(@PathVariable Long scholarshipId) {
        try {
            adminService.approveScholarship(scholarshipId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/scholarships/{scholarshipId}/reject")
    public ResponseEntity<?> rejectScholarship(@PathVariable Long scholarshipId) {
        try {
            adminService.rejectScholarship(scholarshipId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/finance")
    public ResponseEntity<Map<String, Object>> getFinanceSummary() {
        return ResponseEntity.ok(adminService.getFinanceSummary());
    }
}




