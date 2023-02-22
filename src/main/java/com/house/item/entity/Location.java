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
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_NO")
    private User user;

    @Enumerated(EnumType.STRING)
    private LocationType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUPER_LOCATION")
    private Location room;

    @OneToMany(mappedBy = "room")
    private List<Location> places = new ArrayList<>();

    private String name;

    public void updateRoom(String name) {
        this.name = name;
    }

    public void updatePlace(Location room, String name) {
        if (room != null) {
            this.room = room;
        }
        this.name = name;
    }
}
