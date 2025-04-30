package com.fifa_app.league_manager.dao.mapper;


import com.fifa_app.league_manager.dao.operations.CoachOperations;
import com.fifa_app.league_manager.model.ClubCoach;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.time.Instant;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ClubCoachMapper implements Function<ResultSet, ClubCoach> {
    private final CoachOperations coachOperations;

    @Override
    @SneakyThrows
    public ClubCoach apply(ResultSet resultSet) {
        ClubCoach clubCoach = new ClubCoach();

        clubCoach.setId(resultSet.getString("id"));

        clubCoach.setCoach(coachOperations.getCoachById(resultSet.getString("coach_id")));
        // clubCoach.setClub();

        clubCoach.setStartDate(resultSet.getTimestamp("start_date").toInstant());
        clubCoach.setEndDate(resultSet.getTimestamp("end_date").toInstant());

        return clubCoach;
    }
}
