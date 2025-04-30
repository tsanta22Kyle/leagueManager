package com.fifa_app.league_manager.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor@NoArgsConstructor@Getter
@Setter@EqualsAndHashCode
public class Player {
    private String name;
    private String id;
    private int number;
    private Positions position;
    private String country;
    private int age;
    private List<PlayerClub> clubs = new ArrayList<PlayerClub>();
}
