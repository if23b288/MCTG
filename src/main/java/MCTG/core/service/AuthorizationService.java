package MCTG.core.service;

import MCTG.core.models.Users;
import MCTG.persistence.dao.Dao;

import java.util.Optional;

public class AuthorizationService {
    private final Dao<Users> userDao;

    public AuthorizationService(Dao<Users> userDao) {
        this.userDao = userDao;
    }

    public boolean isAuthorized(String bearer) {
        System.out.print("Token: " + bearer);
        String token = bearer.split(" ")[1];
        Optional<Users> dbUser = userDao.get(token.split("-")[0]);
        if (dbUser.isEmpty()) {
            return false;
        }
        return dbUser.get().getToken().equals(token);
    }
}
