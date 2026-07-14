package com.example.service.integration_app.repository;
import com.example.service.integration_app.entity.DataBaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DataBaseEntityRepository extends JpaRepository<DataBaseEntity, UUID> {
}