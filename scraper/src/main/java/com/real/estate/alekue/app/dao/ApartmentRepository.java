package com.real.estate.alekue.app.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;

@Repository
public interface ApartmentRepository extends CrudRepository<Apartment, Long> {

    Apartment findByUrl(String url);

    @Query(value = "select (case when a1.id > a2.id then a1.id else a2.id end) as id from apartment a1 " +
            "join apartment a2 on a1.url = a2.url and a1.date_time = a2.date_time and a1.id <> a2.id " +
            "where a1.date_time = ?1 order by (case when a1.id > a2.id then a1.id else a2.id end)",
            nativeQuery = true)
    Collection<Long> findMaxIdentsForDuplicateUrlsForDateTime(LocalDate dateTime);

}
