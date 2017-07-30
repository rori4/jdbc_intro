package entity.manager;

import connection.ConnectionManager;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class EntityManager implements DBContext {
    private Connection conn;

    public EntityManager() throws SQLException {
        conn = ConnectionManager.getConnection();
    }

    @Override
    public <E> boolean persist(E entity) throws IllegalAccessException, SQLException, NoSuchFieldException {
        createTableIfNotExists(entity);

        Field primaryKey = entity.getClass().getDeclaredField("id");
        primaryKey.setAccessible(true);
        Object value = primaryKey.get(entity);
        if (value == null || (long) value <= 0) {
            insert(entity, primaryKey);
        } else {
            update(entity, primaryKey);
        }

        return true;
    }

    private <E> void update(E entity, Field primaryKey) throws IllegalAccessException, SQLException {
        String tableName = entity.getClass().getSimpleName().toLowerCase();
        String updateQuery = "UPDATE " + tableName + " SET ";
        String where = " WHERE ";
        Field[] fields = entity.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            String fName = field.getName();
            Object fValue = field.get(entity);
            if (field.getName().equals(primaryKey.getName())){
                where += fName + " =" + "'" + fValue + "';";
                continue;
            }

            if (i<fields.length-1){
                updateQuery += fName + " = " + "'" + fValue + "', ";
            } else{
                updateQuery += fName + " = " + "'" + fValue + "'";
            }
        }
        updateQuery += where;
        try (PreparedStatement statement = conn.prepareStatement(updateQuery)) {
            statement.execute();
        }
    }


    private <E> void insert(E entity, Field primaryKey) throws IllegalAccessException {
        String tableName = entity.getClass().getSimpleName().toLowerCase();
        String sqlInsertQuery = "INSERT INTO " + tableName + " (";

        String columns = "";
        String values = "";

        Field[] fields = entity.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            if (!field.getName().equals(primaryKey.getName())) {
                columns += field.getName();
                Object value = field.get(entity);
                values += "'" + value + "'";
                if (i < fields.length - 1) {
                    columns += ",";
                    values += ",";
                }
            }
        }

        sqlInsertQuery += columns + ")" + "VALUES (" + values + ")";

        try (PreparedStatement statement = conn.prepareStatement(sqlInsertQuery)) {
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private <E> boolean createTableIfNotExists(E entity) throws IllegalAccessException, SQLException {
        String tableName = entity.getClass().getSimpleName().toLowerCase();
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY ";
        Field[] fields = entity.getClass().getDeclaredFields();
        String columns = "";
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            String fieldName = field.getName();
            if (!fieldName.equals("id")) {
                columns += fieldName + " " + type(field, entity);
            }
            if (i < fields.length - 1) {
                columns += ",";
            }
        }
        createTableQuery += columns + ")";

        try (PreparedStatement statement = conn.prepareStatement(createTableQuery)) {
            return statement.execute();
        }
    }

    private String type(Field field, Object entity) throws IllegalAccessException {
        Object value = field.get(entity);
        if (value instanceof Integer) {
            return "int";
        }
        if (value instanceof Long) {
            return "bigint";
        }
        if (value instanceof String) {
            return "varchar(20)";
        }
        if (value instanceof LocalDate) {
            return "Date";
        }
        return null;
    }

    @Override
    public void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
