package com.example.aironyproject.domain.reservation.repository;

import com.example.aironyproject.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("""
            select case when count(r) > 0 then true else false end
            from Reservation r
            where r.accommodation.id = :accommodationId
              and r.status in (
                    com.example.aironyproject.domain.reservation.entity.ReservationStatus.PENDING_PAYMENT,
                    com.example.aironyproject.domain.reservation.entity.ReservationStatus.CONFIRMED
              )
              and r.checkInDate < :checkOutDate
              and r.checkOutDate > :checkInDate
            """)
    boolean existsOverlappingReservation(
            @Param("accommodationId") Long accommodationId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );
}
