package com.house.item.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_NO")
    private User user;

    private String name;

    @Enumerated(EnumType.STRING)
    private ItemType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOCATION_NO")
    private Location location;

    private String locationMemo;

    private String photoName;

    private int quantity;

    private int priority;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemLabel> itemLabels = new ArrayList<>();
}
