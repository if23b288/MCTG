package MCTG.persistence;

import MCTG.core.models.User;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Database {
    private List<User> users = new ArrayList<>();

    public void addUser(User user) {
        this.users.add(user);
    }

    public User getUser(String username) {
        for (User user : this.users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public void addToken(String token, String username) {
        for (User user : this.users) {
            if (user.getUsername().equals(username)) {
                user.setToken(token);
            }
        }
    }
}
