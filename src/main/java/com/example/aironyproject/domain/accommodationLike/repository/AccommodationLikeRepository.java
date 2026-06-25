package com.example.aironyproject.domain.accommodationLike.repository;

import com.example.aironyproject.domain.accommodationLike.entity.AccommodationLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccommodationLikeRepository extends JpaRepository<AccommodationLike, Long>,
        AccommodationLikeCustomRepository{

    boolean existsByUser_IdAndAccommodation_Id(Long userId, Long accommodationId);

    Optional<AccommodationLike> findByUser_IdAndAccommodation_Id(Long userId, Long accommodationId);

    List<AccommodationLike> findAllByUser_IdOrderByCreatedAtDesc(Long userId);
}
