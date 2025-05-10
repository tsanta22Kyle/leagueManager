package com.fifa_app.league_manager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Data
@EqualsAndHashCode
@ToString
public class Coach {

    private String id;
    private String name;
    @JsonProperty("nationality")
    private String country;
}
