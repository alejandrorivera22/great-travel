package com.alex.great_travel.domain.repositories;

import com.alex.great_travel.domain.entities.TourEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface TourRepository  extends CrudRepository<TourEntity, Long> {
    @Modifying
    @Query("delete from tour t where t.id = :id")
    void removeById(Long id);
}
