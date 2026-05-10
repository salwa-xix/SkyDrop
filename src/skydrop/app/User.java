package skydrop.app;

public class User {

    private String name;
    private String phone;
    private String password;
    private String district;

    public User(String name,
                String phone,
                String password,
                String district) {

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
}