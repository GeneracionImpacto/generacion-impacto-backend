package com.generacionimpacto.service;

import com.generacionimpacto.dto.ReservationDTO;
import com.generacionimpacto.dto.ScholarshipRequestDTO;
import com.generacionimpacto.model.*;
import com.generacionimpacto.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import java.math.BigDecimal;

@Service
@Transactional
public class StudentService {
    
    @Autowired
    private TutorshipAnnouncementRepository announcementRepository;
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private ScholarshipRepository scholarshipRepository;
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ScheduleRepository scheduleRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    public List<TutorshipAnnouncement> getAllAnnouncements() {
        return announcementRepository.findAll();
    }
    
    public Reservation bookTutorship(Long studentId, ReservationDTO dto) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        
        TutorshipAnnouncement announcement = announcementRepository.findById(dto.getAnnouncementId())
                .orElseThrow(() -> new RuntimeException("Anuncio no encontrado"));
        
        // Validar que el estudiante no tenga otra reservación a la misma hora
        LocalDateTime startTime = dto.getDateTime();
        LocalDateTime endTime = startTime.plusHours(1);
        List<Reservation> conflictingReservations = reservationRepository
                .findByStudentAndDateTimeBetween(student, startTime, endTime);
        
        if (!conflictingReservations.isEmpty()) {
            throw new RuntimeException("Ya tienes una reservación a esta hora");
        }
        
        // Validar que el tutor esté disponible
        if (!isTutorAvailable(announcement.getTutor().getId(), startTime)) {
            throw new RuntimeException("El tutor no está disponible en este horario");
        }
        
        Reservation reservation = new Reservation();
        reservation.setStudent(student);
        reservation.setAnnouncement(announcement);
        reservation.setDateTime(dto.getDateTime());
        reservation.setPaid(false);
        reservation.setScholarshipUsed(false);
        
        reservation.setPaid(true);
        reservation = reservationRepository.save(reservation);
        
        // Crear pago para el tutor
        Payment payment = new Payment();
        payment.setTutor(announcement.getTutor());
        payment.setReservation(reservation);
        payment.setAmount(new BigDecimal("20.00"));
        paymentRepository.save(payment);
        
        // Notificar al tutor
        notificationService.notifyTutorshipReserved(reservation);
        notificationService.notifyTutorshipPaid(reservation);
        notificationService.notifyReservationConfirmed(reservation);
        
        return reservation;
    }
    
    private boolean isTutorAvailable(Long tutorId, LocalDateTime dateTime) {
        User tutor = userRepository.findById(tutorId)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));
        
        // Verificar horarios disponibles del tutor (si hay horarios configurados)
        List<Schedule> schedules = scheduleRepository.findByTutor(tutor);
        if (!schedules.isEmpty()) {
            boolean isInSchedule = schedules.stream()
                    .anyMatch(s -> s.getDayOfWeek() == dateTime.getDayOfWeek() &&
                            dateTime.toLocalTime().isAfter(s.getStartTime().minusMinutes(1)) &&
                            dateTime.toLocalTime().isBefore(s.getEndTime()));
            
            if (!isInSchedule) {
                return false;
            }
        }
        
        // Verificar que no tenga otra reservación a esa hora
        LocalDateTime endTime = dateTime.plusHours(1);
        List<Reservation> tutorReservations = reservationRepository
                .findByAnnouncement_Tutor(tutor)
                .stream()
                .filter(r -> {
                    LocalDateTime rStart = r.getDateTime();
                    LocalDateTime rEnd = rStart.plusHours(1);
                    return dateTime.isBefore(rEnd) && endTime.isAfter(rStart);
                })
                .toList();
        
        return tutorReservations.isEmpty();
    }
    
    public Scholarship requestScholarship(Long studentId, ScholarshipRequestDTO dto) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        
        Scholarship scholarship = new Scholarship();
        scholarship.setStudent(student);
        scholarship.setCourseName(dto.getCourseName());
        scholarship.setReason(dto.getReason());
        scholarship.setStartDate(dto.getStartDate());
        scholarship.setEndDate(dto.getEndDate());
        scholarship.setStatus(RequestStatus.PENDING);
        
        scholarship = scholarshipRepository.save(scholarship);
        
        // Notificar a admins
        notificationService.notifyAdminsOfNewScholarship(scholarship);
        
        return scholarship;
    }
    
    public List<Notification> getNotifications(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        return notificationRepository.findByUserOrderByCreatedAtDesc(student);
    }
    
    public List<Reservation> getMyReservations(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        return reservationRepository.findByStudent(student);
    }
    
    public List<User> getMyTutors(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        return reservationRepository.findByStudent(student)
                .stream()
                .map(r -> r.getAnnouncement().getTutor())
                .distinct()
                .collect(Collectors.toList());
    }
}

