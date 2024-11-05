package una.ac.cr.proyecto_02.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "weather_condition")

public class WeatherCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long condition_Id;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Column(length = 50)
    private String condition;
    private Integer temperature;
}
