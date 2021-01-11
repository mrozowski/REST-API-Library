package pl.kul.LibraryService.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.UUID;

public class UserHistory {
    private final UUID userId;
    private final String title;
    private final int bookCopyId;
    private final Date borrowDate;
    private final Date returnedDate;

    public UserHistory(@JsonProperty("userId") UUID userId,
                       @JsonProperty("title") String title,
                       @JsonProperty("bookCopyId") int bookCopyId,
                       @JsonProperty("borrowDate") Date borrowDate,
                       @JsonProperty("returnedDate") Date returnedDate) {
        this.userId = userId;
        this.title = title;
        this.bookCopyId = bookCopyId;
        this.borrowDate = borrowDate;
        this.returnedDate = returnedDate;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public int getBookCopyId() {
        return bookCopyId;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public Date getReturnedDate() {
        return returnedDate;
    }
}
