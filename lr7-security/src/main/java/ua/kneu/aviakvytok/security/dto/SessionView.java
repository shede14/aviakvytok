package ua.kneu.aviakvytok.security.dto;

import java.util.Date;
import java.util.List;

/** Активний користувач і його сесії (з SessionRegistry). */
public record SessionView(String username, List<String> roles, List<Session> sessions) {

    public record Session(String id, Date lastRequest, boolean expired) {
    }
}
