package com.example.aironyproject.domain.accommodationLike.entity;

import com.example.aironyproject.common.entity.BaseTimeEntity;
import com.example.aironyproject.domain.accommodations.entity.Accommodation;
import com.example.aironyproject.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "accommodation_likes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_accommodation_likes_user_accommodation",
                        columnNames = {"user_id", "accommodation_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccommodationLike extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id")
    private Accommodation accommodation;

    public AccommodationLike(User user, Accommodation accommodation) {
        this.user = user;
        this.accommodation = accommodation;
    }
}
