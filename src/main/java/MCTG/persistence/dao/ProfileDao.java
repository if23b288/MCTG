package MCTG.persistence.dao;

import MCTG.core.models.user.Profile;
import MCTG.persistence.DbConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ProfileDao implements Dao<Profile> {
    @Override
    public Optional<Profile> get(String username) {
        Profile profile = new Profile(username);
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                SELECT username, pName, bio, image
                FROM profile
                WHERE username = ?
                """)
        ) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                profile.setPName(resultSet.getString("pName"));
                profile.setBio(resultSet.getString("bio"));
                profile.setImage(resultSet.getString("image"));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return Optional.of(profile);
    }

    @Override
    public Collection<Profile> getAll() {
        return List.of();
    }

    @Override
    public void save(Profile profile) {
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                INSERT INTO profile (username, pName, bio, image)
                VALUES (?, ?, ?, ?)
                """)
        ) {
            statement.setString(1, profile.getUsername());
            statement.setString(2, profile.getPName());
            statement.setString(3, profile.getBio());
            statement.setString(4, profile.getImage());
            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void update(Profile profile) {

    }

    @Override
    public void delete(Profile profile) {

    }
}
