package com.airmoldova.repository;
import com.airmoldova.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface FlightRepo extends JpaRepository<Flight, Integer> {
    @Query("from Flight " +
                  "where airport_dep = 'LUKK' " +
                  "and date_departure > current_timestamp + '24 hours'")
    @Override
    List<Flight> findAll();
}
