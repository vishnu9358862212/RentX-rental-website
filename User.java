public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String userType; // tenant, landlord
    private boolean isBanned;

    public User() {
    }

    public User(int id, String name, String email, String password, String userType, boolean isBanned) {
        validateInputs(name, email, password, userType);
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.isBanned = isBanned;
    }

    private void validateInputs(String name, String email, String password, String userType) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (userType == null || !userType.matches("tenant|landlord")) {
            throw new IllegalArgumentException("User type must be 'tenant' or 'landlord'");
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUserType() {
        return userType;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email");
        }
        this.email = email;
    }

    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        this.password = password;
    }

    public void setUserType(String userType) {
        if (userType == null || !userType.matches("tenant|landlord")) {
            throw new IllegalArgumentException("User type must be 'tenant' or 'landlord'");
        }
        this.userType = userType;
    }

    public void setBanned(boolean banned) {
        this.isBanned = banned;
    }
}