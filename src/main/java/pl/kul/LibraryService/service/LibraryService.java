package pl.kul.LibraryService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerErrorException;
import pl.kul.LibraryService.auth.AuthUser;
import pl.kul.LibraryService.dao.LibraryDao;
import pl.kul.LibraryService.exception.ApiRequestException;
import pl.kul.LibraryService.exception.ResourceAlreadyExists;
import pl.kul.LibraryService.exception.ResourceNotFoundException;
import pl.kul.LibraryService.model.LibraryBook;
import pl.kul.LibraryService.model.LibraryUser;
import pl.kul.LibraryService.model.UserHistory;
import pl.kul.LibraryService.security.PasswordConfig;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LibraryService {

    final private LibraryDao libraryDao;

    @Autowired
    public LibraryService(@Qualifier("postgres") LibraryDao libraryDao) {
        this.libraryDao = libraryDao;
    }

    public List<LibraryBook> getBooks(int limit, int offset) {
        if(limit < 1 ) throw new ApiRequestException("Incorrect first parameter = " + limit);
        if(offset < 0 ) throw new ApiRequestException("Incorrect second parameter = " + offset);

        return libraryDao.getBooks(limit, offset);
    }

    public List<LibraryBook> getBooks(int limit){
        if(limit < 1 ) throw new ApiRequestException("Incorrect parameter = " + limit);
        return libraryDao.getBooks(limit);
    }

    public List<LibraryBook> getMostPopularBooks(int limit){
        if(limit < 1 ) throw new ApiRequestException("Incorrect parameter = " + limit);
        return libraryDao.getMostPopularBooks(limit);
    }

    public List<LibraryBook> getBooksByTitle(String title){
        if(title.isEmpty()) throw new ApiRequestException("Incorrect book title");
        return libraryDao.getBooksByTitle(title);
    }

    public Optional<LibraryBook> getBookById(int id){
        if(id < 1 ) throw new ApiRequestException("Incorrect parameter = " + id);

        Optional<LibraryBook> bookById = libraryDao.getBookById(id);
        if(!bookById.isPresent()) throw new ResourceNotFoundException("Book with given id does not exist");
        return bookById;
    }

    public int availableCopies(int id){
        if(id < 1 ) throw new ApiRequestException("Incorrect parameter = " + id);
        int result = libraryDao.availableCopies(id);
        if(result == -1) throw new ResourceNotFoundException("Book with given id does not exist");
        return result;
    }

    public List<LibraryBook> discover(){
        //return 10 random books from 100 with the highest rating
        List<LibraryBook> highRatedBooks = libraryDao.getHighRatedBooks(100);
        Random random = new Random();

        Set<Integer> generated = new LinkedHashSet<>();
        while (generated.size() < 10)
        {
            Integer next = random.nextInt(100);
            generated.add(next);
        }
        System.out.println(generated);

        return generated.stream()
                .map(highRatedBooks::get)
                .collect(Collectors.toList());
    }

    ////////// user //////////

    public int borrowBook(int bookId, String username){
        if(bookId < 1) throw new ApiRequestException("Incorrect book id = " + bookId);
        if(username == null || username.isEmpty()) throw new ApiRequestException("Invalid user");

        Optional<LibraryUser> user = libraryDao.getUserByUsername(username);
        if(user.isPresent()){
            UUID userId = user.get().getId();
            int result = libraryDao.borrowBook(bookId, userId);  // book copy_id
            if(result == -1) throw new InternalError("Server error. Pleas try again later");
            else if(result == 0) throw new ResourceNotFoundException("There is no book copies available at the moment");
            else return result;
        }
        return 0;
    }

    public int giveBookBack(int bookCopyId, String username){
        if(bookCopyId < 1) throw new ApiRequestException("Incorrect book copy id = " + bookCopyId);
        //if(username == null || username.isEmpty()) throw new ApiRequestException("Invalid user");

        Optional<LibraryUser> user = libraryDao.getUserByUsername(username);
        if(user.isPresent()){
            UUID userId = user.get().getId();
            int result = libraryDao.giveBookBack(bookCopyId, userId);
            if(result == -1) throw new InternalError("Server error. Pleas try again later");
            else if(result == 0) throw new ResourceNotFoundException("There is no book borrowed with this data");
            else return result;
        }
        return 0;
    }

    public List<UserHistory> readHistory(String username, boolean current) {
        Optional<LibraryUser> user = libraryDao.getUserByUsername(username);
        if(user.isPresent()){
            UUID userId = user.get().getId();
            return libraryDao.readHistory(userId, current);
        }
        else return new ArrayList<>();
    }

    public Optional<LibraryUser> getUserByUsername(String username){
        if(username.trim().isEmpty()) throw new ApiRequestException("Incorrect parameter = " + username);
        return libraryDao.getUserByUsername(username);
    }

    public int registerNewUser(AuthUser newUser){
        String username = newUser.getUsername();
        String email = newUser.getEmail();
        String password = newUser.getPassword();

        if(!isPasswordValid(password)) throw new ApiRequestException("Invalid password");
        if(!isUsernameValid(username)) throw new ApiRequestException("Invalid username");
        if(!isEmailValid(email)) throw new ApiRequestException("Invalid email");

        if(libraryDao.isUsernameExist(username)) throw new ResourceAlreadyExists(String.format("Username %s already exist", username));
        if(libraryDao.isEmailExist(email)) throw new ResourceAlreadyExists(String.format("Email %s already exist", email));

        UUID id = UUID.randomUUID();
        LibraryUser libraryUser = new LibraryUser(id, username, email);
        String encryptPassword = new PasswordConfig()
                .passwordEncoder()
                .encode(password);

        return libraryDao.registerNewUser(libraryUser, encryptPassword);
    }

    public int deleteUserAccount(String password, String username){
        if(password.isEmpty()) throw new ApiRequestException("Invalid password");
        if(username.isEmpty()) throw new ApiRequestException("Invalid user");


        int result = libraryDao.deleteUserAccount(password, username);
        if(result == -2) throw new ApiRequestException("Incorrect password");
        if(result == -1) throw new ResourceAlreadyExists("The user must return the borrowed books before deleting an account");
        if(result == 0) throw new ResourceNotFoundException("User not found");
        else return result;
    }


    /////// private ///////

    private boolean isEmailValid(String email) {
        if(email == null) return false;
        return email.matches("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$");
    }

    private boolean isPasswordValid(String password) {
        if(password == null) return false;
        if(password.length() < 6) return false;
        if(password.length() > 254) return false;
        return true;
    }

    private boolean isUsernameValid(String username) {
        if(username == null) return false;
        if(username.length() < 3 || username.length() > 15) return false;
        if(!username.matches("^[A-Za-z0-9]+(?:[ _-][A-Za-z0-9]+)*$")) return false;
        return true;
    }


}
