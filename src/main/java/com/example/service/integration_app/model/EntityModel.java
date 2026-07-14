package com.example.service.integration_app.model;
import com.example.service.integration_app.entity.DataBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EntityModel {
    private UUID id;
    private String name;
    private Instant date;

    public static EntityModel from(DataBaseEntity entity){
        return new EntityModel(
                entity.getId(),
                entity.getName(),
                entity.getDate()
        );
    }
}