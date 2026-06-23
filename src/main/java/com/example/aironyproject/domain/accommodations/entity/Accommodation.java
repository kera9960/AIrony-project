package com.example.aironyproject.domain.accommodations.entity;

import org.springframework.data.annotation.Id;

import com.example.aironyproject.common.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

public class Accommodation extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// 숙소 ID
	private Long id;

	// 숙소명
	@Column(nullable = false, unique = true)
	private String name;

	//숙소 주소
	@Column(nullable = false)
	private String address;

	//숙소 설명
	@Column(nullable = false)
	private String description;

	// 가격
	@Column(nullable = false)
	private int price;

	// 숙소 상태
	@Column(nullable = false)
	private AccommodationStatus status;

	public Accommodation(String name, String address, String description, int price, AccommodationStatus status){
		this.name = name;
		this.address = address;
		this.description = description;
		this.price = price;
		this.status = status;
	}
}
