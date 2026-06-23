package com.example.aironyproject.domain.accommodations.entity;

import com.example.aironyproject.common.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "accommodations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Accommodation extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// 숙소 ID
	private long id;

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
