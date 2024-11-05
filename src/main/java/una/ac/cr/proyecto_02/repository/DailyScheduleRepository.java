package una.ac.cr.proyecto_02.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import una.ac.cr.proyecto_02.entity.DailySchedule;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyScheduleRepository extends JpaRepository<DailySchedule, Long> {

    @Query("SELECT ds FROM DailySchedule ds WHERE ds.date = :date")
    List<DailySchedule> findByDate(LocalDate date);
}
