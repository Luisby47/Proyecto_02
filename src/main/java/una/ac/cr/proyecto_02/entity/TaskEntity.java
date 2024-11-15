package una.ac.cr.proyecto_02.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.scheduling.config.Task;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tasks")

public class TaskEntity {
    @Id
    private Long taskId;

    @Column(nullable = false, length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Priority priority;

    @Column(nullable = false)
    private Integer estimatedTime;

    private LocalDateTime deadline;

    @ManyToOne
    @JoinColumn(name = "weather_requirement", referencedColumnName = "condition_id")
    private WeatherCondition weatherRequirement;

    @ManyToMany
    @JoinTable(
            name = "task_dependencies",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "dependency_id")
    )
    private List<TaskEntity> dependencies;





}
