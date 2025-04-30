package com.fifa_app.league_manager.endpoint.rest;

import com.fifa_app.league_manager.model.Positions;
import lombok.*;

@AllArgsConstructor@NoArgsConstructor@Getter@Setter@EqualsAndHashCode@ToString
public class CreateOrUpdatePlayer {
    private String name;
    private String id;
    private int number;
    private Positions position;
    private String nationality;
    private int age;
}
