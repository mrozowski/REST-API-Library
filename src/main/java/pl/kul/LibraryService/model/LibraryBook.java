package pl.kul.LibraryService.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class LibraryBook {
    private final int id;
    private final String title;
    private final String authors;
    private final double rating;
    private final double popularity;
    private final int pages;
    private final Date year;
    private final String publisher;

    public LibraryBook(@JsonProperty("id") int id,
                       @JsonProperty("title") String title,
                       @JsonProperty("authors") String authors,
                       @JsonProperty("rating") double rating,
                       @JsonProperty("popularity") double popularity,
                       @JsonProperty("pages") int pages,
                       @JsonProperty("year") Date year,
                       @JsonProperty("publisher") String publisher)
    {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.rating = rating;
        this.popularity = popularity;
        this.pages = pages;
        this.year = year;
        this.publisher = publisher;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthors() {
        return authors;
    }

    public double getRating() {
        return rating;
    }

    public double getPopularity() {
        return popularity;
    }

    public int getPages() {
        return pages;
    }

    public Date getYear() {
        return year;
    }

    public String getPublisher() {
        return publisher;
    }
}
