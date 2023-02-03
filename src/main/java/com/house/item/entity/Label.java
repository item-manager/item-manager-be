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
public class Label {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long labelNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_NO")
    private User user;

    private String name;

    @OneToMany(mappedBy = "label", orphanRemoval = true)
    private List<ItemLabel> itemLabels = new ArrayList<>();

    @Builder
    private Label(Long labelNo, User user, String name, List<ItemLabel> itemLabels) {
        this.labelNo = labelNo;
        this.user = user;
        this.name = name;
        if (itemLabels != null) {
            this.itemLabels = itemLabels;
        }
    }
}
