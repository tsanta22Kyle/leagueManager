package com.fifa_app.league_manager.model;


import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class PlayerTransfer {
    private String id;
    private Instant transferDate;

    private Player player;
    private Club club;
    private TransferType type;
}
