package com.sismics.music.core.dao.dbi.mapper;

import com.sismics.music.core.model.dbi.Role;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Role result set mapper.
 *
 * @author jtremeaux
 */
public class RoleMapper implements ResultSetMapper<Role> {
    @Override
    public Role map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new Role(
                r.getString("id"),
                r.getString("name"),
                r.getTimestamp("createdate"),
                r.getTimestamp("deletedate"));
    }
}
