package com.house.item.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ItemQuantityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemQuantityLogNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_NO")
    private Item item;

    @Enumerated(EnumType.STRING)
    private QuantityType type;

    private String mall;

    private LocalDateTime date;

    private int count;

    private int price;
}
