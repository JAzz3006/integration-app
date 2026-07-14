package com.example.service.integration_app.entity;
import com.example.service.integration_app.model.EntityModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DataBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "date")
    private Instant date;

    public static DataBaseEntity from(EntityModel model){
        return new DataBaseEntity(
                model.getId(),
                model.getName(),
                model.getDate()
        );
    }
}