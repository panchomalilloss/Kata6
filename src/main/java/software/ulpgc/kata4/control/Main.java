package software.ulpgc.kata4.control;

import com.sun.net.httpserver.HttpServer;
import software.ulpgc.kata4.adapter.HistogramHttpAdapter;
import software.ulpgc.kata4.adapter.HistogramService;
import software.ulpgc.kata4.io.RemoteMovieLoader;
import software.ulpgc.kata4.model.Movie;
import software.ulpgc.kata4.persistence.MovieStore;
import software.ulpgc.kata4.persistence.SQLiteMovieStore;
import software.ulpgc.kata4.view.HistogramDisplay;
import software.ulpgc.kata4.viewmodel.Histogram;
import software.ulpgc.kata4.viewmodel.HistogramBuilder;

import javax.swing.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException, IOException {
        HistogramService service =
                new HistogramService(
                        new SQLiteMovieStore("movies.db")
                );

        HttpServer server =
                HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext(
                "/histogram",
                new HistogramHttpAdapter(service)
        );

        server.start();

        System.out.println("Server started");
    }
}
