package com.house.item.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.exception.SubtractCountExceedItemQuantityException;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemNo;

    private String name;

    @Enumerated(EnumType.STRING)
    private ItemType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOCATION_NO")
    private Location location;

    private String photoName;

    private int quantity;

    private int priority;

    private int threshold;

    private String memo;

    @Builder.Default
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemLabel> itemLabels = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ItemQuantityLog> itemQuantityLogs = new ArrayList<>();

    public void updateItem(String name, ItemType type, Location location, String photoName,
        int priority, int threshold, String memo, List<Long> labelNos) {
        this.name = name;
        this.type = type;
        this.location = location;
        this.photoName = photoName;
        this.priority = priority;
        this.threshold = threshold;
        this.memo = memo;

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
            this.itemLabels.add(
                ItemLabel.builder()
                    .item(this)
                    .label(
                        Label.builder()
                            .labelNo(labelNo)
                            .build()
                    )
                    .build()
            );
        }
    }

    public void addQuantity(int count) {
        this.quantity += count;
    }

    public void subtractQuantity(int count) {
        if (this.quantity < count) {
            throw new SubtractCountExceedItemQuantityException(
                ExceptionCodeMessage.SUBTRACT_COUNT_EXCEEDED_ITEM_QUANTITY_EXCEPTION.message());
        }
        this.quantity -= count;
    }
}
