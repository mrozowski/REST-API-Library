package pl.kul.LibraryService.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.kul.LibraryService.auth.AuthUser;
import pl.kul.LibraryService.model.LibraryBook;
import pl.kul.LibraryService.model.LibraryUser;
import pl.kul.LibraryService.service.LibraryService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;


@RestController
@RequestMapping("api/library/books")
public class LibraryController {
    final private LibraryService service;

    @Autowired
    public LibraryController(LibraryService service) {
        this.service = service;
    }

    // hasRole('ROLE_') hasAnyRole('ROLE_') hasAuthority('permission') hasAnyAuthority('permission')

    @GetMapping(path="/{limit}/{offset}")
    public List<LibraryBook> getBooks(@PathVariable("limit") int limit, @PathVariable("offset") int offset){
        return service.getBooks(limit, offset);
    }

    @GetMapping(path="/{limit}")
    public List<LibraryBook> getBooks(@PathVariable("limit") int limit){
        return service.getBooks(limit);
    }

    @GetMapping(path="/popular/{limit}")
    public List<LibraryBook> getMostPopularBooks(@PathVariable("limit") int limit){
        return service.getMostPopularBooks(limit);
    }

    @GetMapping(path="/search/title/{title}")
    public List<LibraryBook> getBooksByTitle(@PathVariable("title") String title){
        return service.getBooksByTitle(title);
    }

    @GetMapping(path="/discover")
    public List<LibraryBook> discover(){
        return service.discover();
    }

    @GetMapping(path="/id/{id}")
    public Optional<LibraryBook> getBookById(@PathVariable("id") int id){
        return service.getBookById(id);
    }


    @GetMapping("/copies/{id}")
    public int availableCopies(@PathVariable("id") int id){
        return service.availableCopies(id);
    }


}

