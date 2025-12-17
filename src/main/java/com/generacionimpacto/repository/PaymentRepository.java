package com.generacionimpacto.repository;

import com.generacionimpacto.model.Payment;
import com.generacionimpacto.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByTutor(User tutor);
}

