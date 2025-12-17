package com.generacionimpacto.repository;

import com.generacionimpacto.model.RequestStatus;
import com.generacionimpacto.model.TutorshipRequest;
import com.generacionimpacto.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TutorshipRequestRepository extends JpaRepository<TutorshipRequest, Long> {
    List<TutorshipRequest> findByTutor(User tutor);
    List<TutorshipRequest> findByStatus(RequestStatus status);
}




