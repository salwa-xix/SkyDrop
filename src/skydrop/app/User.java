package skydrop.app;

public class User {
    private String name;
    private String phone;
    private String password;
    private String district;

    public User(String name, String phone, String password, String district) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.district = district;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public String getDistrict() {
        return district;
    }

    // VALIDATION

    public boolean isValidName() {
        return name != null && !name.isEmpty();
    }

    public boolean isValidPhone() {
        return phone != null && phone.matches("\\d{10}"); // 10 digits
    }

    public boolean isValidPassword() {
        return password != null && password.length() >= 6;
    }

    public boolean isValidDistrict() {
        return district != null && !district.isEmpty();
    }

    public boolean isValidUser() {
        return isValidName() && isValidPhone() && isValidPassword() && isValidDistrict();
    }

    // LOGIN

    public boolean matchesLogin(String phone, String password) {
        return this.phone.equals(phone) && this.password.equals(password);
    }

    //  REGISTER

    public boolean register(DatabaseController db) {
        // 1)
        if (!isValidUser()) {
            System.out.println("Invalid user data");
            return false;
        }

        // 2)
        if (db.userExists(this.phone)) {
            System.out.println("User already exists");
            return false;
        }

        // 3)
        db.insertUser(this);
        return true;
    }
}