package com.generacionimpacto.repository;

import com.generacionimpacto.model.TutorshipAnnouncement;
import com.generacionimpacto.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TutorshipAnnouncementRepository extends JpaRepository<TutorshipAnnouncement, Long> {
    List<TutorshipAnnouncement> findByTutor(User tutor);
}




