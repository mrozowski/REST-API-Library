package pl.kul.LibraryService.dao;

import pl.kul.LibraryService.model.LibraryBook;
import pl.kul.LibraryService.model.LibraryUser;
import pl.kul.LibraryService.model.UserHistory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LibraryDao {
    ///// books /////
    List<LibraryBook> getBooks(int limit, int offset);
    default  List<LibraryBook> getBooks(int limit){
        return getBooks(limit, 0);
    }

    List<LibraryBook> getMostPopularBooks(int limit);
    List<LibraryBook> getHighRatedBooks(int limit);
    List<LibraryBook> getBooksByTitle(String title);
    Optional<LibraryBook> getBookById(int id);
    int availableCopies(int bookId);

    ///// users /////
    Optional<LibraryUser> getUserByUsername(String username);
    boolean isUsernameExist(String username);
    boolean isEmailExist(String email);
    int registerNewUser(LibraryUser newUser, String password);
    int borrowBook(int bookId, UUID userId);
    int giveBookBack(int bookCopyId, UUID userId);
    List<UserHistory> readHistory(UUID userId, boolean current);
    int deleteUserAccount(String password, String username);

}

//select * from book_copies limit 1 offset 0;  select only first element
//select * from book_copies limit 1 offset 24;  select only 25th element
//select * from book_copies limit 10 offset 0;  select first 10 elements


