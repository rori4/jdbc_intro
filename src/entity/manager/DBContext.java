package entity.manager;

import java.sql.SQLException;

public interface DBContext {
    <E> boolean persist(E entity) throws IllegalAccessException, SQLException, NoSuchFieldException;
    void closeConnection();
}
