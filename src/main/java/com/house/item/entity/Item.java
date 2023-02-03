package com.house.item.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Builder
    private Item(Long itemNo, User user, String name, ItemType type, Location location, String locationMemo, String photoName, int quantity, int priority, List<ItemLabel> itemLabels) {
        this.itemNo = itemNo;
        this.user = user;
        this.name = name;
        this.type = type;
        this.location = location;
        this.locationMemo = locationMemo;
        this.photoName = photoName;
        this.quantity = quantity;
        this.priority = priority;
        if (itemLabels != null) {
            this.itemLabels = itemLabels;
        }
    }
}
