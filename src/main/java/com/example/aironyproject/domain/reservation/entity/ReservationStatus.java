package com.example.aironyproject.domain.reservation.entity;

/**
 * 허용되는 예약 상태 전이
 *
 * PENDING_PAYMENT → CONFIRMED : 결제 성공
 * PENDING_PAYMENT → CANCELED  : 결제 실패 또는 결제 전 취소
 * CONFIRMED       → CANCELED  : 예약 취소 및 환불 완료
 *
 * CANCELED 상태에서는 다른 상태로 변경 불가
 */
public enum ReservationStatus {

    PENDING_PAYMENT {
        @Override
        public boolean canTransitTo(ReservationStatus target) {
            return target == CONFIRMED
                    || target == CANCELED;
        }
    },

    CONFIRMED {
        @Override
        public boolean canTransitTo(ReservationStatus target) {
            return target == CANCELED;
        }
    },

    CANCELED {
        @Override
        public boolean canTransitTo(ReservationStatus target) {
            return false;
        }
    };

    public abstract boolean canTransitTo(ReservationStatus target);
}
