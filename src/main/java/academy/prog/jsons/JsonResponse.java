package academy.prog.jsons;

import academy.prog.models.Message;
import academy.prog.models.User;

import java.util.List;

public class JsonResponse {
    private List<Message> messages;
    private List<User> users;

    public List<Message> getMessages() {
        return messages;
    }
    public List<User> getUsers() {
        return users;
    }
    @Override
    public String toString() {
        return "JsonResponse{" +
                "messages=" + messages +
                ", users=" + users +
                '}';
    }
}
