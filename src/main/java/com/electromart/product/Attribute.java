package com.electromart.product;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
@Table(name ="attributes")
public class Attribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;          // Watt, RPM, Length

    @Column(nullable = false)
    private String dataType;      // TEXT, NUMBER, BOOLEAN

    private String unit;          // W, mm, RPM

    private boolean filterable;

    private Boolean active;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}

