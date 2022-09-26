package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.exception.NotValidException;
import com.edu.ulab.app.service.BookService;
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
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class BookServiceImplTemplate implements BookService {

    private final JdbcTemplate jdbcTemplate;

    public BookServiceImplTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        final String INSERT_SQL = "INSERT INTO BOOK(TITLE, AUTHOR, PAGE_COUNT, USER_ID) VALUES (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps =
                                connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                        ps.setString(1, bookDto.getTitle());
                        ps.setString(2, bookDto.getAuthor());
                        ps.setLong(3, bookDto.getPageCount());
                        ps.setLong(4, bookDto.getUserId());
                        return ps;
                    }
                },
                keyHolder);

        bookDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return bookDto;
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        final String UPDATE_SQL = "UPDATE BOOK SET TITLE = ?, AUTHOR = ?, PAGE_COUNT = ?, USER_ID = ? WHERE ID = ?";

        if (bookDto.getId() == null) {
            throw new NotValidException("ID must be not null");
        }
        if (getBookById(bookDto.getId()) != null) {
            jdbcTemplate.update(UPDATE_SQL,
                    bookDto.getTitle(),
                    bookDto.getAuthor(),
                    bookDto.getPageCount(),
                    bookDto.getUserId(),
                    bookDto.getId());
            log.info("Book with ID = {} update", bookDto.getId());
            return bookDto;
        } else {
            throw new NotFoundException("Book with ID = " + bookDto.getId() + " not found");
        }
    }

    @Override
    public BookDto getBookById(Long id) {
        final String GET_SQL = "SELECT * FROM BOOK WHERE ID = ?";

        if (id == null) {
            throw new NotValidException("ID must be not null");
        }
        SqlRowSet bookRows = jdbcTemplate.queryForRowSet(GET_SQL, id);
        BookDto bookDto;
        if (bookRows.first()) {
            bookDto = new BookDto(
                    bookRows.getLong("ID"),
                    bookRows.getLong("USER_ID"),
                    bookRows.getString("TITLE"),
                    bookRows.getString("AUTHOR"),
                    bookRows.getInt("PAGE_COUNT"));
        } else {
            throw new NotFoundException("Book with ID = " + id + " not found");
        }
        return bookDto;
    }

    @Override
    public void deleteBookById(Long id) {
        final String DELETE_SQL = "DELETE FROM BOOK WHERE ID = ? ";

        if (id == null) {
            throw new NotValidException("ID must be not null");
        }

        if (jdbcTemplate.update(DELETE_SQL, id) == 0) {
            throw new NotFoundException("Book with ID = " + id + " not found");
        }
    }

    @Override
    public List<BookDto> getBooksByUserId(Long userId) {
        final String GET_BOOKS_SQL = "SELECT * FROM BOOK WHERE USER_ID = ? ";
        return jdbcTemplate.query(GET_BOOKS_SQL, (rs, rowNum) -> new BookDto(
                        rs.getLong("ID"),
                        rs.getLong("USER_ID"),
                        rs.getString("TITLE"),
                        rs.getString("AUTHOR"),
                        rs.getInt("PAGE_COUNT")),
                userId);
    }

    @Override
    public void deleteBooksByUserId(Long id) {
        final String DELETE_SQL = "DELETE FROM BOOK WHERE USER_ID = ? ";

        if (id == null) {
            throw new NotValidException("ID must be not null");
        }

        if (jdbcTemplate.update(DELETE_SQL, id) == 0) {
            throw new NotFoundException("User with ID = " + id + " not found");
        }
    }
}