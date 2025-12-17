package com.generacionimpacto.service;

import com.generacionimpacto.dto.TutorshipRequestDTO;
import com.generacionimpacto.model.*;
import com.generacionimpacto.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TutorService {
    
    private static final BigDecimal TUTORSHIP_PRICE = new BigDecimal("25.00");
    private static final BigDecimal TUTOR_PAYMENT = new BigDecimal("20.00");
    
    @Autowired
    private TutorshipRequestRepository tutorshipRequestRepository;
    
    @Autowired
    private TutorshipAnnouncementRepository announcementRepository;
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private ScheduleRepository scheduleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    public TutorshipRequest createTutorshipRequest(Long tutorId, TutorshipRequestDTO dto) {
        User tutor = userRepository.findById(tutorId)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));
        
        TutorshipRequest request = new TutorshipRequest();
        request.setTutor(tutor);
        request.setCourseName(dto.getCourseName());
        request.setTeacherName(dto.getTeacherName());
        request.setPeriod(dto.getPeriod());
        request.setDescription(dto.getDescription());
        request.setVideoUrl(dto.getVideoUrl());
        request.setStatus(RequestStatus.PENDING);
        
        request = tutorshipRequestRepository.save(request);
        
        // Notificar a admins
        notificationService.notifyAdminsOfNewRequest(request);
        
        return request;
    }
    
    public List<TutorshipAnnouncement> getMyAnnouncements(Long tutorId) {
        User tutor = userRepository.findById(tutorId)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));
        return announcementRepository.findByTutor(tutor);
    }
    
    public List<Notification> getNotifications(Long tutorId) {
        User tutor = userRepository.findById(tutorId)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));
        return notificationRepository.findByUserOrderByCreatedAtDesc(tutor);
    }
    
    public BigDecimal getTotalEarnings(Long tutorId) {
        User tutor = userRepository.findById(tutorId)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));
        List<Payment> payments = paymentRepository.findByTutor(tutor);
        return payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public List<Payment> getPayments(Long tutorId) {
        User tutor = userRepository.findById(tutorId)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));
        return paymentRepository.findByTutor(tutor);
    }
    
    public List<User> getMyStudents(Long tutorId) {
        User tutor = userRepository.findById(tutorId)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));
        return reservationRepository.findByAnnouncement_Tutor(tutor)
                .stream()
                .map(Reservation::getStudent)
                .distinct()
                .collect(Collectors.toList());
    }
    
    public List<Reservation> getReservations(Long tutorId) {
        User tutor = userRepository.findById(tutorId)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));
        return reservationRepository.findByAnnouncement_Tutor(tutor);
    }
    
    public List<Schedule> getSchedule(Long tutorId) {
        User tutor = userRepository.findById(tutorId)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));
        return scheduleRepository.findByTutor(tutor);
    }
    
    public Schedule addAvailableHours(Long tutorId, Schedule schedule) {
        User tutor = userRepository.findById(tutorId)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));
        schedule.setTutor(tutor);
        return scheduleRepository.save(schedule);
    }
}




