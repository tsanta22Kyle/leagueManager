package com.fifa_app.league_manager.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Setter
@EqualsAndHashCode
@ToString
public class PlayingTime {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;
    private int value;

    private DurationUnit unit;
}
