package com.generacionimpacto.service;

import com.generacionimpacto.model.*;
import com.generacionimpacto.repository.NotificationRepository;
import com.generacionimpacto.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public Notification createNotification(User user, String title, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRead(false);
        return notificationRepository.save(notification);
    }
    
    public void notifyTutorshipRequestApproved(TutorshipRequest request) {
        createNotification(
            request.getTutor(),
            "Solicitud de tutoría aprobada",
            "Tu solicitud para ofrecer tutorías en " + request.getCourseName() + " ha sido aprobada."
        );
    }
    
    public void notifyTutorshipRequestRejected(TutorshipRequest request) {
        createNotification(
            request.getTutor(),
            "Solicitud de tutoría rechazada",
            "Tu solicitud para ofrecer tutorías en " + request.getCourseName() + " ha sido rechazada."
        );
    }
    
    public void notifyTutorshipReserved(Reservation reservation) {
        createNotification(
            reservation.getAnnouncement().getTutor(),
            "Tutoría reservada",
            "El estudiante " + reservation.getStudent().getName() + " ha reservado una tutoría para " + 
            reservation.getAnnouncement().getCourseName() + " el " + reservation.getDateTime()
        );
    }
    
    public void notifyTutorshipPaid(Reservation reservation) {
        createNotification(
            reservation.getAnnouncement().getTutor(),
            "Tutoría pagada",
            "Has recibido el pago de " + reservation.getStudent().getName() + " por la tutoría de " + 
            reservation.getAnnouncement().getCourseName()
        );
    }
    
    public void notifyUpcomingTutorship(Reservation reservation) {
        createNotification(
            reservation.getAnnouncement().getTutor(),
            "Tutoría próxima",
            "Tienes una tutoría programada en los próximos 60 minutos: " + 
            reservation.getAnnouncement().getCourseName() + " con " + reservation.getStudent().getName()
        );
    }
    
    public void notifyScholarshipApproved(Scholarship scholarship) {
        createNotification(
            scholarship.getStudent(),
            "Beca aprobada",
            "Tu solicitud de beca para " + scholarship.getCourseName() + " ha sido aprobada."
        );
    }
    
    public void notifyScholarshipRejected(Scholarship scholarship) {
        createNotification(
            scholarship.getStudent(),
            "Beca rechazada",
            "Tu solicitud de beca para " + scholarship.getCourseName() + " ha sido rechazada."
        );
    }
    
    public void notifyReservationConfirmed(Reservation reservation) {
        createNotification(
            reservation.getStudent(),
            "Reservación confirmada",
            "Tu reservación para " + reservation.getAnnouncement().getCourseName() + 
            " con " + reservation.getAnnouncement().getTutor().getName() + " ha sido confirmada."
        );
    }
    
    public void notifyAdminsOfNewRequest(TutorshipRequest request) {
        List<User> admins = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ADMIN)
                .toList();
        
        for (User admin : admins) {
            createNotification(
                admin,
                "Nueva solicitud de tutoría",
                "El tutor " + request.getTutor().getName() + " ha solicitado crear un anuncio para " + 
                request.getCourseName()
            );
        }
    }
    
    public void notifyAdminsOfNewScholarship(Scholarship scholarship) {
        List<User> admins = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ADMIN)
                .toList();
        
        for (User admin : admins) {
            createNotification(
                admin,
                "Nueva solicitud de beca",
                "El estudiante " + scholarship.getStudent().getName() + " ha solicitado una beca para " + 
                scholarship.getCourseName()
            );
        }
    }
}




