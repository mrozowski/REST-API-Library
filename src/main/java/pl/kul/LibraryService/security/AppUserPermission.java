package pl.kul.LibraryService.security;

public enum AppUserPermission {
    // permissions for each table in database (for reading and writing)
    
    BOOKS_READ("books:read"),
    BOOKS_WRITE("books:write"),
    BOOK_COPY_READ("book_copy:read"),
    BOOK_COPY_WRITE("book_copy:write"),
    ORDERS_READ("orders:read"),
    ORDERS_WRITE("orders:write"),
    USER_READ("users:read"),
    USER_WRITE("users:write");

    private final String permission;


    AppUserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
