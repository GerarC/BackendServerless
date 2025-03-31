package serverless.challenge.dto.request;

public class UserIdRequest {
    private String id;

    public UserIdRequest(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public UserIdRequest setId(String id) {
        this.id = id;
        return this;
    }
}
