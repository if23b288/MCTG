package MCTG.persistence.dao;

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
    Optional<T> get(int number);

    Optional<T> get(String text);

    Collection<T> getAll();

    // CREATE
    void save(T t);

    // UPDATE
    void update(T t);

    // DELETE
    void delete(T t);
}
