package una.ac.cr.proyecto_02.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import una.ac.cr.proyecto_02.entity.TaskEntity;
import una.ac.cr.proyecto_02.entity.WeatherCondition;

import java.util.Optional;


@Repository
public interface WeatherConditionRepository extends JpaRepository<WeatherCondition, Long> {

    @Query("SELECT wc FROM WeatherCondition wc WHERE wc.condition = :condition")
    Optional<WeatherCondition> findByCondition(String condition);

}
