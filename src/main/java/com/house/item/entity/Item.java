package com.house.item.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Iterator;
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

    public void updateItem(String name, ItemType type, Location location, String locationMemo, String photoName, int priority, List<Long> labelNos) {
        this.name = name;
        this.type = type;
        this.location = location;
        this.locationMemo = locationMemo;
        this.photoName = photoName;
        this.priority = priority;

        Iterator<ItemLabel> iterator = this.itemLabels.iterator();
        while (iterator.hasNext()) {
            Long labelNo = iterator.next().getLabel().getLabelNo();
            if (labelNos.contains(labelNo)) {
                labelNos.remove(labelNo);
            } else {
                iterator.remove();
            }
        }

        for (Long labelNo : labelNos) {
            this.itemLabels.add(ItemLabel.builder()
                    .label(Label.builder()
                            .labelNo(labelNo)
                            .build())
                    .build());
        }
    }
}
