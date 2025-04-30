package com.fifa_app.league_manager.model;


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
    private String country;
}
