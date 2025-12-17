package com.generacionimpacto.repository;

import com.generacionimpacto.model.Schedule;
import com.generacionimpacto.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByTutor(User tutor);
    List<Schedule> findByTutorAndDayOfWeek(User tutor, DayOfWeek dayOfWeek);
}




