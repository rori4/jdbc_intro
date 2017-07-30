import entity.manager.DBContext;
import entity.manager.EntityManager;
import model.User;

import java.sql.SQLException;
import java.time.LocalDate;

public class OrmDemo {
    public static void main(String[] args) throws SQLException, IllegalAccessException, NoSuchFieldException {
        DBContext em = new EntityManager();
        try {
            User u1 = new User("Ivan", 25, LocalDate.now());
            em.persist(u1);
            u1.setAge(27);
            u1.setId(6);
            em.persist(u1);
        } finally {
            em.closeConnection();
        }
    }
}
