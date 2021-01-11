package pl.kul.LibraryService.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.kul.LibraryService.auth.AuthUser;
import pl.kul.LibraryService.model.LibraryUser;
import pl.kul.LibraryService.model.UserHistory;
import pl.kul.LibraryService.service.LibraryService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.security.authorization.AuthorityReactiveAuthorizationManager.hasRole;

@RestController
@RequestMapping("api/library/users")
public class UserController {

    final private LibraryService service;

    @Autowired
    public UserController(LibraryService service) {
        this.service = service;
    }

    @GetMapping
    public Optional<LibraryUser> getUserByUsername(Principal principal){
        return service.getUserByUsername(principal.getName());
    }

    @GetMapping("borrow/{bookId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public int borrowBook(@PathVariable("bookId") int bookId, Principal principal){
        return service.borrowBook(bookId, principal.getName());
    }

    @GetMapping("borrow/back/{bookCopyId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public int giveBookBack(@PathVariable("bookCopyId") int bookCopyId, Principal principal){
        return service.giveBookBack(bookCopyId, principal.getName());
    }

    @GetMapping("history")
    public List<UserHistory> readHistory(Principal principal){
        return service.readHistory(principal.getName(), false);
    }

    @GetMapping("history/current")
    public List<UserHistory> currentReadingBooks(Principal principal){
        return service.readHistory(principal.getName(), true);
    }

    @DeleteMapping("delete")
    public int deleteUserAccount(@RequestBody @Valid AuthUser user, Principal principal){
        return service.deleteUserAccount(user.getPassword(), principal.getName());
    }



}
