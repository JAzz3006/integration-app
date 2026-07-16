package com.example.service.integration_app.service;
import com.example.service.integration_app.configuration.properties.AppCacheProperties;
import com.example.service.integration_app.entity.DataBaseEntity;
import com.example.service.integration_app.repository.DataBaseEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheManager = "redisCacheManager")
public class DataBaseEntityService {

    private final DataBaseEntityRepository  repository;

    @Cacheable(AppCacheProperties.СacheNames.DATABASE_ENTITIES)
    public List<DataBaseEntity> findAll(){
        return repository.findAll();
    }

    @Cacheable(value = AppCacheProperties.СacheNames.DATABASE_ENTITY_BY_ID, key = "#id")
    public DataBaseEntity findBYId(UUID id){
        return repository.findById(id).orElseThrow();
    }

    @Cacheable(AppCacheProperties.СacheNames.DATABASE_ENTITY_BY_NAME)
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

//    @Caching(evict = {
//            @CacheEvict(value = "databaseEntities", allEntries = true),
//            @CacheEvict(value = "databaseEntityByName", allEntries = true)
//    })
    @CacheEvict(
            cacheNames = AppCacheProperties.СacheNames.DATABASE_ENTITY_BY_ID,
            key = "#id",
            beforeInvocation = true
    )
    public DataBaseEntity update(UUID id, DataBaseEntity entity){
        DataBaseEntity forUpdate = findBYId(id);
        forUpdate.setName(entity.getName());
        forUpdate.setDate(entity.getDate());
        return repository.save(forUpdate);
    }

//    @Caching(evict = {
//            @CacheEvict(value = "databaseEntities", allEntries = true),
//            @CacheEvict(value = "databaseEntityByName", allEntries = true)
//    })
    @CacheEvict(
            cacheNames = AppCacheProperties.СacheNames.DATABASE_ENTITY_BY_ID,
            key = "#id",
            beforeInvocation = true
    )
    public void deleteById(UUID id){
        repository.deleteById(id);
    }
}