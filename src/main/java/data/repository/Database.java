package data.repository;

import web.exception.UserAuthException;

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
    List<T> findAllByName(String name);
    void deleteAll();
}
