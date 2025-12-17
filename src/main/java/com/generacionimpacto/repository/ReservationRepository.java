package com.generacionimpacto.repository;

import com.generacionimpacto.model.Reservation;
import com.generacionimpacto.model.TutorshipAnnouncement;
import com.generacionimpacto.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByStudent(User student);
    List<Reservation> findByAnnouncement(TutorshipAnnouncement announcement);
    List<Reservation> findByAnnouncement_Tutor(User tutor);
    List<Reservation> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Reservation> findByStudentAndDateTimeBetween(User student, LocalDateTime start, LocalDateTime end);
    List<Reservation> findByAnnouncementAndDateTimeBetween(TutorshipAnnouncement announcement, LocalDateTime start, LocalDateTime end);
}




