package com.generacionimpacto.repository;

import com.generacionimpacto.model.RequestStatus;
import com.generacionimpacto.model.Scholarship;
import com.generacionimpacto.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScholarshipRepository extends JpaRepository<Scholarship, Long> {
    List<Scholarship> findByStudent(User student);
    List<Scholarship> findByStatus(RequestStatus status);
}




