package pl.kul.LibraryService.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kul.LibraryService.auth.AuthUser;
import pl.kul.LibraryService.service.LibraryService;

import javax.validation.Valid;

@RestController
public class RegisterController {
    private final LibraryService service;

    @Autowired
    public RegisterController(LibraryService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public int registerNewUser(@RequestBody @Valid AuthUser newUser){
        return service.registerNewUser(newUser);
    }
}
