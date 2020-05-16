package com.real.estate.alekue.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApartmentRepository extends CrudRepository<Apartment, Long>, JpaSpecificationExecutor<Apartment> {

    @Query("SELECT new com.real.estate.alekue.dao.PriceAverage(floor(avg(a.price)), a.dateTime) "
            + "FROM Apartment AS a GROUP BY a.dateTime ORDER BY a.dateTime DESC")
    List<PriceAverage> getAverages();

    @Query("SELECT DISTINCT a.district FROM Apartment a")
    List<String> findUniqueDistrict();

}
