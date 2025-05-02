package com.fifa_app.league_manager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Coach coach;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ClubParticipation> clubParticipations;
}
