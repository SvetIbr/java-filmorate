package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;


@Component
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> findAllUsers() {
        String sql = "SELECT * FROM users ORDER BY user_id";
        return jdbcTemplate.query(sql, this::makeUser);
    }

    public User createUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");

        Map<String, Object> values = new HashMap<>();
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("name", user.getName());
        values.put("birthday", user.getBirthday());

        user.setId(simpleJdbcInsert.executeAndReturnKey(values).longValue());
        return user;
    }

    public User updateUser(User user) {
        String sql = "UPDATE users SET login = ?, email = ?, name = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, user.getLogin(), user.getEmail(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    public void deleteAllUsers() {
        String sql = "DELETE FROM users";
        jdbcTemplate.update(sql);
    }

    public User getUserById(Long id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        List<User> result = jdbcTemplate.query(sql, this::makeUser, id);
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    public void addToFriends(Long idUser, Long idFriend) {
        String sql = "INSERT INTO friendship (user_id1, user_id2, confirmed) VALUES(?, ?, ?)";
        jdbcTemplate.update(sql, idUser, idFriend, false);
    }

    public void deleteFromFriends(Long idUser, Long idFriend) {
        String sql = "DELETE FROM friendship WHERE user_id1 = ? AND user_id2 = ?";
        jdbcTemplate.update(sql, idUser, idFriend);
    }

    public List<User> getFriendsByUser(Long idUser) {
        String sql = "SELECT * FROM users WHERE user_id IN (SELECT user_id1 FROM friendship WHERE user_id2 = ?)";
        return jdbcTemplate.query(sql, this::makeUser, idUser);
    }

    public List<User> getCommonFriends(Long idUser, Long otherId) {
        String sql = "SELECT * FROM users WHERE user_id IN " +
                "((SELECT user_id1 FROM friendship WHERE user_id2 = ?) " +
                "FULL JOIN (SELECT user_id1 FROM friendship WHERE user_id2 = ?))";
        return jdbcTemplate.query(sql, this::makeUser, idUser, otherId);
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();

        return new User(id, email, login, name, birthday);
    }

    public Set<Long> loadFriends (User user) {
        String sql = "(SELECT user_id2 id FROM friendship  WHERE user_id1 = ?) " +
                "UNION (SELECT user_id1 id FROM friendship  WHERE user_id2 = ? AND  confirmed = true)";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, user.getId());
        Set<Long> friends = new HashSet<>();
        while (sqlRowSet.next()) {
            friends.add(sqlRowSet.getLong("id"));
        }
        return friends;
    }

    public boolean checkFriendship(Long userId, Long friendId, Boolean confirmed) {
        String sql = "SELECT * FROM friendship WHERE user_id1 = ? AND user_id2 = ? AND  confirmed = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, userId, friendId, confirmed);
        return rows.next();
    }

    public void acceptToFriends(Long idUser, Long idFriend) {
        String sql = "UPDATE friendship SET user_id1 = ?, user_id2 = ?, confirmed = ? " +
                        "WHERE user_id1 = ? AND user_id2 = ?";
        jdbcTemplate.update(sql, idUser, idFriend, true, idUser, idFriend);

    }

    public void deleteFromConfirmFriends(Long idUser, Long idFriend) {
        String sql = "UPDATE friendship SET user_id1 = ?, user_id2 = ?, confirmed = ? " +
                "WHERE user_id1 = ? AND user_id2 = ?";
        jdbcTemplate.update(sql, idFriend, idUser, false, idFriend, idUser);
    }
}
