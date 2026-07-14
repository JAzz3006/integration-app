package com.example.service.integration_app.service;
import com.example.service.integration_app.entity.DataBaseEntity;
import com.example.service.integration_app.repository.DataBaseEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DataBaseEntityService {

    private final DataBaseEntityRepository  repository;

    @Cacheable("databaseEntities")
    public List<DataBaseEntity> findAll(){
        return repository.findAll();
    }

    public DataBaseEntity findBYId(UUID id){
        return repository.findById(id).orElseThrow();
    }

    @Cacheable("databaseEntityByName")
    public DataBaseEntity findByName(String name){
        DataBaseEntity probe = new DataBaseEntity();
        probe.setName(name);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withIgnorePaths("id", "date");
        Example<DataBaseEntity> example = Example.of(probe, matcher);
        return repository.findOne(example).orElseThrow();
    }

    @CacheEvict(value = "databaseEntities", allEntries = true)
    public DataBaseEntity create(DataBaseEntity entity){
        DataBaseEntity forSave = new DataBaseEntity();
        forSave.setName(entity.getName());
        forSave.setDate(entity.getDate());
        return repository.save(forSave);
    }

    @Caching(evict = {
            @CacheEvict(value = "databaseEntities", allEntries = true),
            @CacheEvict(value = "databaseEntityByName", allEntries = true)
    })
    public DataBaseEntity update(UUID id, DataBaseEntity entity){
        DataBaseEntity forUpdate = findBYId(id);
        forUpdate.setName(entity.getName());
        forUpdate.setDate(entity.getDate());
        return repository.save(forUpdate);
    }

    @Caching(evict = {
            @CacheEvict(value = "databaseEntities", allEntries = true),
            @CacheEvict(value = "databaseEntityByName", allEntries = true)
    })
    public void deleteById(UUID id){
        repository.deleteById(id);
    }
}