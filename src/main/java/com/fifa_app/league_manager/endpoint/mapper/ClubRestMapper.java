package com.fifa_app.league_manager.endpoint.mapper;

import com.fifa_app.league_manager.endpoint.rest.ClubRest;
import com.fifa_app.league_manager.model.Club;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ClubRestMapper implements Function<Club, ClubRest> {
    @Override
    public ClubRest apply(Club club) {
        return null;
    }

    public ClubRest toRest(Club club) {
        ClubRest clubRest = new ClubRest();

        clubRest.setId(club.getId());
        clubRest.setStadium(club.getStadium());
        clubRest.setAcronym(club.getAcronym());
        clubRest.setName(club.getName());
        clubRest.setCoach(club.getCoach());
        clubRest.setYearCreation(club.getYearCreation());

        return clubRest;
    }
}
