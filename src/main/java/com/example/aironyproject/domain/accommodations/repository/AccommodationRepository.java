package com.example.aironyproject.domain.accommodations.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.aironyproject.domain.accommodations.entity.Accommodation;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
}
