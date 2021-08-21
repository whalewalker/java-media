package data.repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface Database <T> {
    void save(T t);
    boolean contain(T t);
    void delete(T t);
    List<T> findAll();
    int size();
    Optional<T> findById(String id);
    Optional<T> findByUsername(String username);
    List<T> findByName(String name);
    void deleteAll();
    Optional<T> findByEmail(String email);
}
