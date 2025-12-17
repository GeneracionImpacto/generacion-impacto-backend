package com.generacionimpacto.service;

import com.generacionimpacto.model.*;
import com.generacionimpacto.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AdminService {
    
    private static final BigDecimal TUTORSHIP_PRICE = new BigDecimal("25.00");
    private static final BigDecimal TUTOR_PAYMENT = new BigDecimal("20.00");
    private static final BigDecimal COMPANY_COMMISSION = new BigDecimal("5.00");
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TutorshipRequestRepository tutorshipRequestRepository;
    
    @Autowired
    private TutorshipAnnouncementRepository announcementRepository;
    
    @Autowired
    private ScholarshipRepository scholarshipRepository;
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    public Map<String, Long> getStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalStudents", userRepository.countByRole(Role.STUDENT));
        stats.put("totalTutors", userRepository.countByRole(Role.TUTOR));
        stats.put("totalAnnouncements", announcementRepository.count());
        stats.put("totalReservations", reservationRepository.count());
        return stats;
    }
    
    public List<User> getAllTutors() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.TUTOR)
                .toList();
    }
    
    public List<User> getAllStudents() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.STUDENT)
                .toList();
    }
    
    public List<TutorshipRequest> getPendingTutorshipRequests() {
        return tutorshipRequestRepository.findByStatus(RequestStatus.PENDING);
    }
    
    public List<Scholarship> getPendingScholarships() {
        return scholarshipRepository.findByStatus(RequestStatus.PENDING);
    }
    
    public TutorshipAnnouncement approveTutorshipRequest(Long requestId) {
        TutorshipRequest request = tutorshipRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        request.setStatus(RequestStatus.APPROVED);
        request.setReviewedAt(LocalDateTime.now());
        tutorshipRequestRepository.save(request);
        
        // Crear anuncio
        TutorshipAnnouncement announcement = new TutorshipAnnouncement();
        announcement.setTutor(request.getTutor());
        announcement.setCourseName(request.getCourseName());
        announcement.setDescription(request.getDescription());
        announcement = announcementRepository.save(announcement);
        
        // Notificar al tutor
        notificationService.notifyTutorshipRequestApproved(request);
        
        return announcement;
    }
    
    public void rejectTutorshipRequest(Long requestId) {
        TutorshipRequest request = tutorshipRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        request.setStatus(RequestStatus.REJECTED);
        request.setReviewedAt(LocalDateTime.now());
        tutorshipRequestRepository.save(request);
        
        // Notificar al tutor
        notificationService.notifyTutorshipRequestRejected(request);
    }
    
    public void approveScholarship(Long scholarshipId) {
        Scholarship scholarship = scholarshipRepository.findById(scholarshipId)
                .orElseThrow(() -> new RuntimeException("Beca no encontrada"));
        
        scholarship.setStatus(RequestStatus.APPROVED);
        scholarshipRepository.save(scholarship);
        
        // Notificar al estudiante
        notificationService.notifyScholarshipApproved(scholarship);
    }
    
    public void rejectScholarship(Long scholarshipId) {
        Scholarship scholarship = scholarshipRepository.findById(scholarshipId)
                .orElseThrow(() -> new RuntimeException("Beca no encontrada"));
        
        scholarship.setStatus(RequestStatus.REJECTED);
        scholarshipRepository.save(scholarship);
        
        // Notificar al estudiante
        notificationService.notifyScholarshipRejected(scholarship);
    }
    
    public Map<String, Object> getFinanceSummary() {
        List<Reservation> allReservations = reservationRepository.findAll();
        
        BigDecimal totalEarnings = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;
        
        for (Reservation reservation : allReservations) {
            if (reservation.isPaid() && !reservation.isScholarshipUsed()) {
                // Estudiante pagó -> ganancia de 5 soles
                totalEarnings = totalEarnings.add(COMPANY_COMMISSION);
            } else if (reservation.isScholarshipUsed()) {
                // Beca usada -> empresa paga 20 soles al tutor
                totalExpenses = totalExpenses.add(TUTOR_PAYMENT);
            }
        }
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalEarnings", totalEarnings);
        summary.put("totalExpenses", totalExpenses);
        summary.put("netProfit", totalEarnings.subtract(totalExpenses));
        
        // Obtener todos los pagos
        List<Payment> allPayments = paymentRepository.findAll();
        summary.put("payments", allPayments);
        
        return summary;
    }
}




