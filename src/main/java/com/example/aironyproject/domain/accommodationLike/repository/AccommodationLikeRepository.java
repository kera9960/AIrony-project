package com.example.aironyproject.domain.accommodationLike.repository;

import com.example.aironyproject.domain.accommodationLike.entity.AccommodationLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationLikeRepository extends JpaRepository<AccommodationLike, Long> {

    boolean existsByUser_IdAndAccommodation_Id(Long userId, Long accommodationId);
}
