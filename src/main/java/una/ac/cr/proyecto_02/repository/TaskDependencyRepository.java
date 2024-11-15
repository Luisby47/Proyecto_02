package una.ac.cr.proyecto_02.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import una.ac.cr.proyecto_02.entity.TaskDependency;

import java.util.List;

@Repository
public interface TaskDependencyRepository extends JpaRepository<TaskDependency, Long> {
    List<TaskDependency> findByTask_TaskId(Long taskId);
}
