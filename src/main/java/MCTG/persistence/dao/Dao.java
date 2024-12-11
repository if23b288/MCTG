package MCTG.persistence.dao;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

/**
 * Implementation of the Data-Access-Object Pattern
 *
 * @param <T>
 */
// DAO overview see: https://www.baeldung.com/java-dao-pattern
public interface Dao<T> {
    // READ
    Optional<T> get(String text);

    Collection<T> getAll();

    // CREATE
    void save(T t) throws SQLException;

    // UPDATE
    void update(T t);

    // DELETE
    void delete(String id);
}
