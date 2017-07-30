import java.sql.*;

public class JDBCIntro {
    //There are many commands that you can add to the end
    private static final String URL = "jdbc:mysql://localhost:3306/jdbcdemodb?createDatabaseIfNotExist=true";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    public static void main(String[] args) throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("Connection established successfully");

            try (Statement stmt = conn.createStatement()) {
                String query = "CREATE TABLE IF NOT EXISTS users(" +
                        "id int AUTO_INCREMENT PRIMARY KEY," +
                        "username varchar(20)," +
                        "password varchar(20))";
                stmt.executeUpdate(query);
            }
            /*try (Statement stmt = conn.createStatement()) {
                String query = "INSERT INTO users (username, password)" +
                        "VALUES ('Ivan','123'),('Gosho','abc')";
                stmt.executeUpdate(query);
            }*/

            if (login(conn,"Ivan", "123")){
                System.out.println("Ivan logged in successfully !");
            }
            if (login(conn,"Gosho", "abc")){
                System.out.println("Gosho logged in successfully !");
            }
        }
    }

    private static boolean login(Connection conn, String user, String pass) throws SQLException {
        String query = "SELECT COUNT(id) FROM users " +
                "WHERE username='" + user + "'AND password='" + pass + "'";

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            rs.next();
            int usersCount = rs.getInt(1);
            return usersCount > 0;
        }
    }
}
