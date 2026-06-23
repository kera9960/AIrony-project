package com.example.aironyproject.domain.accommodations.entity;

import com.example.aironyproject.common.exception.CustomException;
import com.example.aironyproject.common.exception.ErrorCode;
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
		throw new CustomException(ErrorCode.VALIDATION_FAILED);
	}
}

