package pl.kul.LibraryService.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.kul.LibraryService.auth.AuthUserService;
import pl.kul.LibraryService.exception.ApiException;
import pl.kul.LibraryService.exception.ApiRequestException;
import pl.kul.LibraryService.jwt.JwtConfig;
import pl.kul.LibraryService.model.LibraryBook;
import pl.kul.LibraryService.service.LibraryService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@WebMvcTest(LibraryController.class)
class LibraryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private LibraryController controller;

    @MockBean
    private LibraryService service;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private AuthUserService authUserService;

    @MockBean
    private JwtConfig jwtConfig;

    @BeforeEach
    public void before(){
        controller = new LibraryController(service);
    }

    @Test
    public void givenSimpleRequestForBooks_whenRequestIsExecuted_thenDefaultResponseStatusCodeIs200() throws Exception{
        List<LibraryBook> books = new ArrayList<>();

        when(service.getBooks(10)).thenReturn(books);
        RequestBuilder request = MockMvcRequestBuilders.get("/api/library/books/10");
        MvcResult result = mockMvc.perform(request).andReturn();
        Assert.assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void givenSimpleRequestForBooks_whenRequestIsExecuted_thenDefaultResponseContentTypeIsJson() throws Exception {
        //Check if response content type is Json
        // Given
        String expectedResultType = "application/json";
        RequestBuilder request = MockMvcRequestBuilders.get("/api/library/books/10");

        // When
        MvcResult result = mockMvc.perform(request).andReturn();

        // Then
        Assert.assertEquals(expectedResultType, result.getResponse().getContentType() );
    }

    @Test
    public void givenSimpleRequestForTwoBooks_ShouldReturnListOfTwoBooks() throws Exception{
        List<LibraryBook> libraryBooks = getListOfBooks(2);

        when(service.getBooks(2)).thenReturn(libraryBooks);
        RequestBuilder request = MockMvcRequestBuilders.get("/api/library/books/2");
        MvcResult result = mockMvc.perform(request).andReturn();
        String content = result.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        List<LibraryBook> readValue = mapper.readValue(content, new TypeReference<List<LibraryBook>>() {});

        Assert.assertEquals(libraryBooks.size(), readValue.size());
    }

    @Test
    public void getPopularBookList_ShouldReturnListOfBooks() throws Exception {
        List<LibraryBook> libraryBooks = getListOfBooks(2);

        when(service.getMostPopularBooks(2)).thenReturn(libraryBooks);
        RequestBuilder request = MockMvcRequestBuilders.get("/api/library/books/popular/2");
        MvcResult result = mockMvc.perform(request).andReturn();
        String content = result.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        List<LibraryBook> readValue = mapper.readValue(content, new TypeReference<List<LibraryBook>>() {});

        Assert.assertEquals(libraryBooks.size(), readValue.size());
    }

    @Test
    public void getBooksByTitle_givenTitleIsHarry_ShouldReturnListOfBooksWithHarryInTheTitle() throws Exception {
        List<LibraryBook> libraryBooks = getListOfBooks(10);

        String title = "Harry";
        when(service.getBooksByTitle(title)).thenReturn(libraryBooks.stream().filter(e->e.getTitle().contains(title)).collect(Collectors.toList()));
        RequestBuilder request = MockMvcRequestBuilders.get("/api/library/books/search/title/"+ title);
        MvcResult result = mockMvc.perform(request).andReturn();
        String content = result.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        List<LibraryBook> readValue = mapper.readValue(content, new TypeReference<List<LibraryBook>>() {});

        Assert.assertTrue(readValue.get(0).getTitle().contains(title));
    }

    @Test
    public void discoverBooks_ShouldReturnListOfTenBooks() throws Exception {
        int p = 10;
        List<LibraryBook> libraryBooks = getListOfBooks(p);

        when(service.discover()).thenReturn(libraryBooks);
        RequestBuilder request = MockMvcRequestBuilders.get("/api/library/books/discover");
        MvcResult result = mockMvc.perform(request).andReturn();
        String content = result.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        List<LibraryBook> readValue = mapper.readValue(content, new TypeReference<List<LibraryBook>>() {});
        Assert.assertEquals(p, readValue.size());
    }

    @Test
    public void getBookCopies_shouldReturnNumberOfAvailableCopies() throws Exception {
        int id = 10;
        int copies = 4;
        when(service.availableCopies(id)).thenReturn(copies);
        RequestBuilder request = MockMvcRequestBuilders.get("/api/library/books/copies/" + id);
        MvcResult result = mockMvc.perform(request).andReturn();
        String content = result.getResponse().getContentAsString();

        Assert.assertEquals(copies, Integer.parseInt(content));
    }



    private List<LibraryBook> getListOfBooks(int p) throws ParseException {
        if(p > 10) p = 10;
        if(p < 2) p = 2;
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        LibraryBook book = new LibraryBook(10, "Harry Potter", "Ralong", 4.5, 50, 120, formatter.parse("01.02.2009"), "publisher");
        LibraryBook book2 = new LibraryBook(11, "Top 10 Roles", "Ralong", 3.9, 43, 125, formatter.parse("01.02.2019"), "publisher");
        LibraryBook book3 = new LibraryBook(12, "Demons", "Stone", 3.9, 43, 125, formatter.parse("01.02.2019"), "publisher");
        LibraryBook book4 = new LibraryBook(13, "Quo Vadis", "Marrick", 3.9, 43, 125, formatter.parse("01.02.2019"), "publisher");
        LibraryBook book5 = new LibraryBook(14, "Tomorrow", "Marrick", 3.9, 43, 125, formatter.parse("01.02.2019"), "publisher");
        LibraryBook book6 = new LibraryBook(15, "Where should we go", "Oinawa", 3.9, 43, 125, formatter.parse("01.02.2019"), "publisher");
        LibraryBook book7 = new LibraryBook(16, "Jakarta", "ralong", 3.9, 43, 125, formatter.parse("01.02.2019"), "publisher");
        LibraryBook book8 = new LibraryBook(17, "1992", "Marrick", 3.9, 43, 125, formatter.parse("01.02.2019"), "publisher");
        LibraryBook book9 = new LibraryBook(18, "Life of nature", "Roberto", 3.9, 43, 125, formatter.parse("06.02.2013"), "publisher");
        LibraryBook book10 = new LibraryBook(19, "Goodbye", "Oinawa", 3.9, 43, 125, formatter.parse("01.02.2019"), "publisher");

        List<LibraryBook> libraryBooks = Arrays.asList(book, book2, book3, book4, book5, book6, book7, book8, book9, book10);
        List<LibraryBook> result = new ArrayList<>();

        for(int i=0; i<p; i++){
            result.add(libraryBooks.get(i));
        }
        return result;
    }




}