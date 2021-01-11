package pl.kul.LibraryService.service;


import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pl.kul.LibraryService.auth.AuthUser;
import pl.kul.LibraryService.dao.LibraryDataAccessService;
import pl.kul.LibraryService.exception.ApiRequestException;
import pl.kul.LibraryService.model.LibraryBook;
import pl.kul.LibraryService.model.LibraryUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {
    LibraryService service;

    @Mock
    private LibraryDataAccessService database;

    @BeforeEach
    public void before(){
        //JdbcTemplate mock = mock(JdbcTemplate.class);
        //LibraryDataAccessService dao = new LibraryDataAccessService(mock);
        this.service = new LibraryService(database);
    }

    @Test
    public void borrowBookWithEmptyUsername_ShouldThrowApiRequestException(){
        Exception exception = assertThrows(ApiRequestException.class, () -> {
            service.borrowBook(1, "");
        });
        String expectedMessage = "Invalid user";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void borrowBookWithInvalidBookId_ShouldThrowApiRequestException(){
        int bookId = -1;
        Exception exception = assertThrows(ApiRequestException.class, () -> {
            service.borrowBook(bookId, "CorrectUsername");
        });

        String expectedMessage = "Incorrect book id = " + bookId;
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void giveBookBackWithInvalidBookId_ShouldThrowApiRequestException(){
        int bookCopyId = -1;
        Exception exception = assertThrows(ApiRequestException.class, () -> {
            service.giveBookBack(bookCopyId, "CorrectUsername");
        });

        String expectedMessage = "Incorrect book copy id = " + bookCopyId;
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void registerNewUserWithInvalidEmail_ShouldThrowApiRequestException(){
        String username = "Username";
        String email = "username@gmailcom";  //missed dot
        String password = "user123";
        AuthUser newUser = new AuthUser(username, email, password, null, true, true, true, true);

        Exception exception = assertThrows(ApiRequestException.class, () -> {
            service.registerNewUser(newUser);
        });

        String expectedMessage = "Invalid email";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }


    @Test
    public void registerNewUserWithInvalidUsername_ShouldApiRequestException(){
        String username = "_user^&name";  //invalid username
        String email = "username@gmail.com";
        String password = "user123";
        AuthUser newUser = new AuthUser(username, email, password, null, true, true, true, true);

        Exception exception = assertThrows(ApiRequestException.class, () -> {
            service.registerNewUser(newUser);
        });

        String expectedMessage = "Invalid username";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void registerNewUserWithTooShortPassword_ShouldThrowApiRequestException(){
        String username = "Username";
        String email = "username@gmail.com";
        String password = "1f";   //to short password
        AuthUser newUser = new AuthUser(username, email, password, null, true, true, true, true);

        Exception exception = assertThrows(ApiRequestException.class, () -> {
            service.registerNewUser(newUser);
        });

        String expectedMessage = "Invalid password";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }


    @Test
    public void borrowBookWithProperId() throws ParseException {
        UUID userId = UUID.fromString("97bd8327-ee80-4429-9d54-839491f7f3ac");
        String username = "Mirek";
        int bookCopyId =32;

        given(database.getUserByUsername(username)).willReturn(Optional.of(new LibraryUser(userId, username, "mirek@wp.pl")));
        given(database.borrowBook(10, userId)).willReturn(bookCopyId);

        int borrowBook = service.borrowBook(10, username);
        Assert.assertEquals(bookCopyId, borrowBook);
    }

    @Test
    public void getBookByIdWithValidId_ShouldReturnBook() throws ParseException {
        int bookId = 10;
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        LibraryBook book = new LibraryBook(10, "harry", "ralong", 4.5, 50, 120, formatter.parse("01.02.2009"), "publisher");


        given(database.getBookById(bookId)).willReturn(java.util.Optional.of(book));

        Optional<LibraryBook> bookOptional = service.getBookById(bookId);
        Assert.assertEquals(book, bookOptional.get());

    }

    @Test
    public void getBookByIdWithInvalidId_ShouldThrowApiRequestException() {
        int bookId = -1;

        Exception exception = assertThrows(ApiRequestException.class, () -> {
            service.getBookById(bookId);
        });

        String expectedMessage = "Incorrect parameter = "+ bookId;
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    public void getBookListOfSizeTwo_ShouldReturnListOfTwoBooks() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        LibraryBook book = new LibraryBook(10, "harry", "ralong", 4.5, 50, 120, formatter.parse("01.02.2009"), "publisher");
        LibraryBook book2 = new LibraryBook(11, "Lorry", "ralong", 4.2, 43, 125, formatter.parse("01.02.2019"), "publisher");
        List<LibraryBook> libraryBooks = Arrays.asList(book, book2);
        given(database.getBooks(2)).willReturn(libraryBooks);

        List<LibraryBook> books = service.getBooks(2);
        Assert.assertEquals(2, books.size());
        Assert.assertEquals(libraryBooks, books);
    }


}