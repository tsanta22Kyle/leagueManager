package com.fifa_app.league_manager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Club {
    private String id;
    private String name;
    private long yearCreation;
    private String acronym;
    private String stadium;
    private Coach coach;

    @JsonIgnore
    private List<Season> seasons;
}
