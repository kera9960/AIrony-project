package com.example.aironyproject.domain.accommodations.entity;



import com.example.aironyproject.common.entity.BaseTimeEntity;
import com.example.aironyproject.domain.accommodations.enums.AccommodationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "accommodations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Accommodation extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// 숙소 ID
	private Long id;

	// 지역 컬럼 추가
	@Column(nullable = false)
	private String region;

	// 숙소명
	@Column(nullable = false)
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
	@Enumerated(EnumType.STRING)
	private AccommodationStatus status;

	public Accommodation(String region,String name, String address, String description, int price, AccommodationStatus status){
		this.region = region;
		this.name = name;
		this.address = address;
		this.description = description;
		this.price = price;
		this.status = status;
	}
}
