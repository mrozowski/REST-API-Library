package pl.kul.LibraryService.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.kul.LibraryService.auth.AuthUserService;
import pl.kul.LibraryService.jwt.JwtConfig;
import pl.kul.LibraryService.model.LibraryBook;
import pl.kul.LibraryService.model.LibraryUser;
import pl.kul.LibraryService.service.LibraryService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebMvcTest(LibraryController.class)
class UserControllerTest {

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

//    @Test
//    @WithMockUser(username="Mirek")
//    void getUserByUsername_ShouldReturnUser() throws Exception {
//        String username = "Mirek";
//        LibraryUser user = new LibraryUser(UUID.fromString("97bd8327-ee80-4429-9d54-839491f7f3ac"), username, "mirek@gmail.com");
//
//        when(service.getUserByUsername(username)).thenReturn(java.util.Optional.of(user));
//        RequestBuilder request = MockMvcRequestBuilders.get("/api/library/users");
//
//        MvcResult result = mockMvc.perform(request).andReturn();
//        String content = result.getResponse().getContentAsString();
//
//        ObjectMapper mapper = new ObjectMapper();
//        LibraryUser readValue = mapper.readValue(content, new TypeReference<LibraryUser>() {});
//        Assert.assertEquals(user.getId(), readValue.getId());
//    }

    @Test
    void borrowBook() {
    }

    @Test
    void giveBookBack() {
    }

    @Test
    void readHistory() {
    }

    @Test
    void currentReadingBooks() {
    }

    @Test
    void deleteUserAccount() {
    }
}