package software.ulpgc.kata4.persistence;

import software.ulpgc.kata4.model.Movie;

import java.sql.*;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

public class SQLiteMovieStore implements MovieStore {
    private final String url;

    public SQLiteMovieStore(String database) {
        this.url = "jdbc:sqlite:" + database;
        createTable();
    }

    @Override
    public void saveAll(List<Movie> movies) {
        String sql = """
                INSERT INTO movies (title, year, duration)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = connect()){
            connection.setAutoCommit(false);

            try {
                clear(connection);

                try (PreparedStatement statement = connection.prepareStatement(sql)) {

                    int count = 0;

                    for (Movie movie : movies) {
                        statement.setString(1, movie.title());
                        statement.setInt(2, movie.year());
                        statement.setInt(3, movie.duration());

                        statement.addBatch();

                        if (++count % 1000 == 0) {
                            statement.executeBatch();
                        }
                    }

                    statement.executeBatch();
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Movie> loadAll() throws SQLException {
        List<Movie> movies = new ArrayList<>();

        String sql = """
                SELECT title, year, duration
                FROM movies
                """;

        try (Connection connection = connect();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                movies.add(new Movie(
                        resultSet.getString("title"),
                        resultSet.getInt("year"),
                        resultSet.getInt("duration")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return movies;
    }

    private void createTable() {

        String sql = """
                CREATE TABLE IF NOT EXISTS movies (
                    title TEXT NOT NULL,
                    year INTEGER NOT NULL,
                    duration INTEGER NOT NULL)
                """;

        try (Connection connection = connect();
            Statement statement = connection.createStatement()){

            statement.execute(sql);

        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    private void clear(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()){
            statement.execute("DELETE FROM movies");
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url);
    }

}
