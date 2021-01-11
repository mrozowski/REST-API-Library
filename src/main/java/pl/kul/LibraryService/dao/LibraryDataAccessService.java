package pl.kul.LibraryService.dao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import pl.kul.LibraryService.model.LibraryBook;
import pl.kul.LibraryService.model.LibraryUser;
import pl.kul.LibraryService.model.UserHistory;
import pl.kul.LibraryService.security.PasswordConfig;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository("postgres")
public class LibraryDataAccessService implements LibraryDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LibraryDataAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //////////////////// books method ///////////////////
    @Override
    public List<LibraryBook> getBooks(int limit, int offset) {
        String sql = "SELECT * FROM books limit ? offset ?";
        return jdbcTemplate.query(sql, new Object[]{limit, offset}, mapBooks());
    }

    @Override
    public List<LibraryBook> getMostPopularBooks(int limit) {
        if(limit < 1 || limit > 10) limit = 10;
        String sql = "SELECT id, title, author, rating, popularity, pages, year, publisher FROM books ORDER BY popularity DESC LIMIT ?";
        return jdbcTemplate.query(sql, new Object[]{limit}, mapBooks());
    }

    @Override
    public List<LibraryBook> getBooksByTitle(String title) {
        String sql = "SELECT id, title, author, rating, popularity, pages, year, publisher FROM books WHERE title ilike ?";
        return jdbcTemplate.query(sql, new Object[]{"%"+title+"%"}, mapBooks());
    }

    @Override
    public List<LibraryBook> getHighRatedBooks(int limit) {
        String sql = "SELECT id, title, author, rating, popularity, pages, year, publisher FROM books ORDER BY rating DESC LIMIT ?";
        return jdbcTemplate.query(sql, new Object[]{limit}, mapBooks());
    }

    @Override
    public Optional<LibraryBook> getBookById(int id) {
        String sql = "SELECT id, title, author, rating, popularity, pages, year, publisher FROM books WHERE id = ?";
        try{
            LibraryBook libraryBook = jdbcTemplate.queryForObject(sql, new Object[]{id}, mapBooks());
            return Optional.ofNullable(libraryBook);
        }
        catch(EmptyResultDataAccessException e){
            return Optional.empty();
        }

    }

    @Override
    public int availableCopies(int bookId) {
        String sql = "SELECT count(id) from book_copies WHERE book_id = ? AND borrowed = false";
        try{
            Integer integer = jdbcTemplate.queryForObject(sql, new Object[]{bookId}, Integer.class);
            return integer;
        }
        catch (EmptyResultDataAccessException e){
            return -1;
        }


    }


    ///////////////////// users methods ////////////////////////

    @Override
    public Optional<LibraryUser> getUserByUsername(String username) {
        if(username.isEmpty()) return Optional.empty();
        String sql = "SELECT id, username, email FROM users WHERE username = ?";
        LibraryUser libraryUser = jdbcTemplate.queryForObject(sql, new Object[]{username}, mapUsers());

        return Optional.ofNullable(libraryUser);
    }

    @Override
    public boolean isUsernameExist(String username) {
        if(username.isEmpty()) return false;
        String sql = "SELECT count(id) FROM users WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{username}, Integer.class);
        return count != 0;
    }

    @Override
    public boolean isEmailExist(String email) {
        if(email.isEmpty()) return false;
        String sql = "SELECT count(id) FROM users WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{email}, Integer.class);
        return count != 0;
    }

    @Override
    public int registerNewUser(LibraryUser newUser, String password) {
        if(newUser == null || password.isEmpty()) return -1;
        String sql = "INSERT INTO users(id, username, password, email, role) VALUES(?, ?, ?, ?, 'USER');";
        return jdbcTemplate.update(sql, newUser.getId(), newUser.getUsername(), password, newUser.getEmail());
    }

    @Override
    public int borrowBook(int bookId, UUID userId) {
        // TODO create function in database for handling borrowing book

        String sql = "SELECT id FROM book_copies WHERE book_id = ? AND borrowed = false LIMIT 1;";
        Integer bookCopyId = 0;
        try{
            bookCopyId = jdbcTemplate.queryForObject(sql, new Object[]{bookId}, Integer.class);
        }
        catch(EmptyResultDataAccessException e){
            return 0;
        }

        if(bookCopyId != null && bookCopyId > 0){
            String sqlUpdate = "BEGIN;" + "UPDATE book_copies SET borrowed = true WHERE id = ?";
            jdbcTemplate.update(sqlUpdate, bookCopyId);

            String sqlSaveData = "INSERT INTO orders(user_id, book_id, date_issued, date_returned) VALUES(?, ?, to_date(?, 'YYYY-MM-DD HH24:MI:SS'), null); " + "COMMIT;";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = simpleDateFormat.format(new Date());
            
            try{
                jdbcTemplate.update(sqlSaveData, userId, bookCopyId, date);
            }catch(Exception e){
                jdbcTemplate.update("ROLLBACK;");
                return -1;
            }
            return bookCopyId;
        }

        return 0;
    }

    @Override
    public int giveBookBack(int bookCopyId, UUID userId) {
        String sqlForId = "SELECT id from orders where date_returned IS NULL AND book_id = ? AND user_id = ? ;";
        Integer orderId = 0;

        try{
            orderId = jdbcTemplate.queryForObject(sqlForId, new Object[]{bookCopyId, userId}, Integer.class);
        }
        catch (EmptyResultDataAccessException e){
            return 0;
        }

        String sql = "BEGIN; " + "UPDATE orders SET date_returned = to_date(?, 'YYYY-MM-DD HH24:MI:SS') where id = ?";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        jdbcTemplate.update(sql, date, orderId);

        String sqlBookCopies = "UPDATE book_copies set borrowed = false where id = ? ;" + "COMMIT;";
        try{
            jdbcTemplate.update(sqlBookCopies, bookCopyId);
            return 1; // everything went well
        }catch(Exception e){
            jdbcTemplate.update("ROLLBACK;");
            return -1;  // server problems
        }

    }

    @Override
    public List<UserHistory> readHistory(UUID userId, boolean current) {
        String sql = "SELECT e.book_id, e.date_issued, e.date_returned, b.title FROM orders e JOIN book_copies c ON e.book_id = c.id JOIN books b ON c.book_id = b.id WHERE e.user_id = ? ";
        if(current)
            sql += "AND e.date_returned IS NULL order by e.date_issued DESC;"; // if current is true then return only current reading books
        else
            sql += "order by e.date_issued DESC;";
        return jdbcTemplate.query(sql, new Object[]{userId}, mapUserHistory(userId));
    }

    @Override
    public int deleteUserAccount(String password, String username) {
        // password must be hashed
        PasswordEncoder passwordEncoder = new PasswordConfig().passwordEncoder();
        try{
            String sqlGetUserId = "SELECT id FROM users where username = ? ;";
            String userId_ = jdbcTemplate.queryForObject(sqlGetUserId, new Object[]{username}, String.class);
            if(userId_ == null || userId_.isEmpty()) return 0; //just in case
            UUID userId = UUID.fromString(userId_);

            //check if password is valid
            String sqlCheckPassword = "SELECT password FROM users where id = ? ;";
            String dbPassword = jdbcTemplate.queryForObject(sqlCheckPassword, new Object[]{userId}, String.class);
            if(!passwordEncoder.matches(password, dbPassword)) return -2;

            //first check if user does not keep any books
            String sqlCheckIfHasBooks = "SELECT count(*) FROM orders WHERE user_id = ? AND date_returned IS NULL;";
            Integer currentBooks = jdbcTemplate.queryForObject(sqlCheckIfHasBooks, new Object[]{userId}, Integer.class);
            if(currentBooks != null && currentBooks > 0 ){
                System.out.println("Books: " + currentBooks);
                return -1; //cannot delete user
            }

            //delete all his history
            String sqlDeleteUserHistory = "DELETE FROM orders WHERE user_id = ?;";
            jdbcTemplate.update(sqlDeleteUserHistory, userId);

            //delete user from user table
            String sqlDeleteUserAccount = "DELETE FROM users WHERE id = ?;";
            int update = jdbcTemplate.update(sqlDeleteUserAccount, userId);
            return update; // 1

        }
        catch (EmptyResultDataAccessException e){
            System.out.println(e.getMessage());
            return 0;
        }
    }

    ////////////////////////////////// private //////////////////////////////////

    private RowMapper<LibraryBook> mapBooks() {
        return (resultSet, i) -> new LibraryBook(
                resultSet.getInt("id"),
                resultSet.getString("title"),
                resultSet.getString("author"),
                resultSet.getDouble("rating"),
                resultSet.getDouble("popularity"),
                resultSet.getInt("pages"),
                resultSet.getDate("year"),
                resultSet.getString("publisher"));
    }

    private RowMapper<LibraryUser> mapUsers() {
        return (resultSet, i) -> new LibraryUser(
                UUID.fromString(resultSet.getString("id")),
                resultSet.getString("username"),
                resultSet.getString("email"));
    }

    private RowMapper<UserHistory> mapUserHistory(UUID userId) {
        return (resultSet, i) -> new UserHistory(
                userId,
                resultSet.getString("title"),
                resultSet.getInt("book_id"),
                resultSet.getDate("date_issued"),
                resultSet.getDate("date_returned"));
    }
}
