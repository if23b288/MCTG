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
        Optional<Users> dbUser = userDao.get(token.split("-")[0]);
        if (dbUser.isEmpty()) {
            return false;
        }
        return dbUser.get().getToken().equals(token);
    }

    public boolean isAuthorized(String username, String bearer) {
        if (bearer.isEmpty()) {
            return false;
        }
        String token = bearer.split(" ")[1];
        Optional<Users> dbUser = userDao.get(username);
        if (dbUser.isEmpty()) {
            return false;
        }
        return dbUser.get().getToken().equals(token);
    }
}
