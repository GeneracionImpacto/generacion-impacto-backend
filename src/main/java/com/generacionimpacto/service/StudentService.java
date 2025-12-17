package com.generacionimpacto.service;

import com.generacionimpacto.dto.ReservationDTO;
import com.generacionimpacto.dto.ScholarshipRequestDTO;
import com.generacionimpacto.model.*;
import com.generacionimpacto.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;
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

    /**
     * Returns available 1-hour slots for a given announcement and date.
     * If tutor has no schedule configured, we assume availability from 08:00 to 20:00.
     */
    public List<LocalDateTime> getAvailableSlots(Long announcementId, Long studentId, LocalDate date) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        TutorshipAnnouncement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Anuncio no encontrado"));

        User tutor = announcement.getTutor();

        // Base windows from schedule (or default 08:00-20:00)
        List<Schedule> schedules = scheduleRepository.findByTutorAndDayOfWeek(tutor, date.getDayOfWeek());
        List<TimeWindow> windows = new ArrayList<>();
        if (schedules.isEmpty()) {
            windows.add(new TimeWindow(LocalTime.of(8, 0), LocalTime.of(20, 0)));
        } else {
            for (Schedule s : schedules) {
                windows.add(new TimeWindow(s.getStartTime(), s.getEndTime()));
            }
        }

        // Tutor reservations for that date (any announcement by that tutor)
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
        List<Reservation> tutorReservations = reservationRepository.findByAnnouncement_TutorAndDateTimeBetween(tutor, dayStart, dayEnd);

        // Student reservations for that date
        List<Reservation> studentReservations = reservationRepository.findByStudentAndDateTimeBetween(student, dayStart, dayEnd);

        List<LocalDateTime> slots = new ArrayList<>();
        for (TimeWindow w : windows) {
            LocalTime t = w.start;
            while (!t.plusHours(1).isAfter(w.end)) {
                LocalDateTime slotStart = date.atTime(t);
                LocalDateTime slotEnd = slotStart.plusHours(1);

                boolean overlapsTutor = tutorReservations.stream().anyMatch(r -> overlaps(slotStart, slotEnd, r.getDateTime(), r.getDateTime().plusHours(1)));
                if (overlapsTutor) {
                    t = t.plusHours(1);
                    continue;
                }

                boolean overlapsStudent = studentReservations.stream().anyMatch(r -> overlaps(slotStart, slotEnd, r.getDateTime(), r.getDateTime().plusHours(1)));
                if (overlapsStudent) {
                    t = t.plusHours(1);
                    continue;
                }

                slots.add(slotStart);
                t = t.plusHours(1);
            }
        }
        return slots;
    }

    private boolean overlaps(LocalDateTime aStart, LocalDateTime aEnd, LocalDateTime bStart, LocalDateTime bEnd) {
        return aStart.isBefore(bEnd) && aEnd.isAfter(bStart);
    }

    private static class TimeWindow {
        final LocalTime start;
        final LocalTime end;
        TimeWindow(LocalTime start, LocalTime end) {
            this.start = start;
            this.end = end;
        }
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

