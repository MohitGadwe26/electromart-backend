package com.electromart.user.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_browsing_history")
public class UserBrowsingHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "category_id", nullable = false)
    private Long categoryId;
    
    @Column(name = "view_count")
    private Integer viewCount = 1;
    
    @Column(name = "last_viewed")
    private LocalDateTime lastViewed;
}

