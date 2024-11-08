package MCTG.core.service;

import MCTG.core.models.user.Users;
import MCTG.persistence.dao.Dao;

import java.util.Optional;

public class AuthorizationService {
    private final Dao<Users> userDao;

    public AuthorizationService(Dao<Users> userDao) {
        this.userDao = userDao;
    }

    public boolean isAuthorized(String bearer) {
        if (bearer.isEmpty()) {
            return false;
        }
        String token = bearer.split(" ")[1];
        String username = token.split("-")[0];
        return checkToken(username, token);
    }

    public boolean isAuthorized(String username, String bearer) {
        if (username.isEmpty() || bearer.isEmpty()) {
            return false;
        }
        String token = bearer.split(" ")[1];
        return checkToken(username, token);
    }

    private boolean checkToken(String username, String token) {
        Optional<Users> dbUser = userDao.get(username);
        return dbUser.isPresent() && dbUser.get().getToken().equals(token);
    }
}
