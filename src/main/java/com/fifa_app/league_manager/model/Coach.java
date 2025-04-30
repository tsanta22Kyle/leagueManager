package com.fifa_app.league_manager.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Data
@EqualsAndHashCode
@ToString
public class Coach {
    @JsonIgnore
    private String id;
    private String name;
    private String country;
}
