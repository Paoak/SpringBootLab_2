package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.exception.NotValidException;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImplTemplate implements UserService {
    private final JdbcTemplate jdbcTemplate;

    public UserServiceImplTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserDto createUser(UserDto userDto) {

        final String INSERT_SQL = "INSERT INTO PERSON(FULL_NAME, TITLE, AGE) VALUES (?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setString(1, userDto.getFullName());
                    ps.setString(2, userDto.getTitle());
                    ps.setLong(3, userDto.getAge());
                    return ps;
                }, keyHolder);

        userDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return userDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        final String UPDATE_SQL = "UPDATE PERSON SET FULL_NAME = ?, TITLE = ?, AGE = ? WHERE ID = ?";

        if (userDto.getId() == null) {
            throw new NotValidException("ID must be not null");
        }
        if (getUserById(userDto.getId()) != null) {
            jdbcTemplate.update(UPDATE_SQL,
                    userDto.getFullName(),
                    userDto.getTitle(),
                    userDto.getAge(),
                    userDto.getId());
            log.info("User with ID = {} update", userDto.getId());
            return userDto;
        } else {
            throw new NotFoundException("User with ID = " + userDto.getId() + " not found");
        }
    }

    @Override
    public UserDto getUserById(Long id) {
        final String GET_SQL = "SELECT * FROM PERSON WHERE ID = ?";
        if (id == null) {
            throw new NotValidException("ID must be not null");
        }
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(GET_SQL, id);
        UserDto userDto;
        if (userRows.first()) {
            userDto = new UserDto(
                    userRows.getLong("ID"),
                    userRows.getString("FULL_NAME"),
                    userRows.getString("TITLE"),
                    userRows.getInt("AGE"));
        } else {
            throw new NotFoundException("User with ID = " + id + " not found");
        }
        return userDto;
    }

    @Override
    public void deleteUserById(Long id) {
        final String DELETE_SQL = "DELETE FROM PERSON WHERE ID = ? ";

        if (id == null) {
            throw new NotValidException("ID must be not null");
        }

        if (jdbcTemplate.update(DELETE_SQL, id) == 0) {
            throw new NotFoundException("User with ID = " + id + " not found");
        }
    }
}