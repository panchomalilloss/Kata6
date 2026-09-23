package software.ulpgc.kata4.adapter;

import software.ulpgc.kata4.model.Movie;
import software.ulpgc.kata4.persistence.MovieStore;
import software.ulpgc.kata4.viewmodel.Histogram;
import software.ulpgc.kata4.viewmodel.HistogramBuilder;

import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;

public class HistogramService {

    private final MovieStore store;

    public HistogramService(MovieStore store) {
        this.store = store;
    }

    public Histogram histogram(String attribute, int binSize) throws SQLException {
        if(binSize <= 0){
            throw new IllegalArgumentException();
        }

        Function<Movie, Integer> extractor = switch (attribute){
            case "year" -> Movie::year;
            case "duration" -> Movie::duration;
            default -> throw new IllegalArgumentException();
        };

        List<Movie> movies = store.loadAll();
        return new HistogramBuilder(movies)
                .build(movie -> bin(extractor.apply(movie), binSize));
    }

    private int bin(int value, int size) {
        return value < 0 ? -1 : (value/size) * size;
    }
}
