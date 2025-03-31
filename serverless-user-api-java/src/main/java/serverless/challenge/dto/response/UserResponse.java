package serverless.challenge.dto.response;

public class UserResponse {
    private String id;
    private String name;
    private String email;

    public UserResponse(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public UserResponse() {
    }

    public String getId() {
        return id;
    }

    public UserResponse setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public UserResponse setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserResponse setEmail(String email) {
        this.email = email;
        return this;
    }

    @Override
    public String toString() {
        return "UserResponse{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
