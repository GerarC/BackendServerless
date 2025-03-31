package serverless.challenge.dto.request;

public class UserRequest {
    private String name;
    private String email;


    public UserRequest(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public UserRequest() {
    }

    public String getName() {
        return name;
    }

    public UserRequest setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserRequest setEmail(String email) {
        this.email = email;
        return this;
    }

    @Override
    public String toString() {
        return "UserRequest{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
