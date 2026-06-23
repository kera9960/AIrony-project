package com.example.aironyproject.domain.accommodations.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;

@Getter
public enum AccommodationStatus {
	ACTIVE("예약 가능"),
	INACTIVE("예약 불가");

	private final String status;

	AccommodationStatus(String status){
		this.status = status;
	}

	@JsonCreator
	public static AccommodationStatus from(String str){
		for(AccommodationStatus status : AccommodationStatus.values()){
			if(status.getStatus().equals(str)){
				return status;
			}
		}
		throw new IllegalArgumentException("일치하는 상태가 없습니다.");
	}
}

