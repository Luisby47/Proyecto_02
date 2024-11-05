package una.ac.cr.proyecto_02.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import una.ac.cr.proyecto_02.entity.TaskEntity;

@Repository
public interface  TaskRepository  extends JpaRepository<TaskEntity, Long> {
}
