package nz.ac.canterbury.seng302.gardenersgrove;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GardenDBTest {

    private static final String JDBC_URL = "jdbc:h2:mem:countries;DB_CLOSE_DELAY=-1";
    private static final String JDBC_USER = "sa";
    private static final String JDBC_PASSWORD = "password";

    private static Connection connection;

    @BeforeAll
    public static void setUp() throws Exception {
        connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
        createTable();
        populateData();
    }

    @AfterAll
    public static void tearDown() throws Exception {
        connection.close();
    }

    private static void createTable() throws SQLException {
        String createTableSQL = "CREATE TABLE garden (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) NOT NULL, location VARCHAR(255) NOT NULL, size INT NOT NULL)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(createTableSQL)) {
            preparedStatement.executeUpdate();
        }
    }

    private static void populateData() throws SQLException {
        String insertSQL = "INSERT INTO garden (name, location, size) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, "Garden1");
            preparedStatement.setString(2, "Location1");
            preparedStatement.setInt(3, 100);
            preparedStatement.executeUpdate();

            preparedStatement.setString(1, "Garden2");
            preparedStatement.setString(2, "Location2");
            preparedStatement.setInt(3, 200);
            preparedStatement.executeUpdate();

            preparedStatement.setString(1, "Garden3");
            preparedStatement.setString(2, "Location3");
            preparedStatement.setInt(3, 300);
            preparedStatement.executeUpdate();

            preparedStatement.setString(1, "Garden4");
            preparedStatement.setString(2, "Location4");
            preparedStatement.setInt(3, 400);
            preparedStatement.executeUpdate();

            preparedStatement.setString(1, "Garden5");
            preparedStatement.setString(2, "Location5");
            preparedStatement.setInt(3, 500);
            preparedStatement.executeUpdate();
        }
    }
    @Test
    public void testDatabaseConnection() {
        assertNotNull(connection);
    }

    @Test
    public void testGardenData() throws SQLException {
        String query = "SELECT COUNT(*) FROM garden";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            assertTrue(resultSet.next());
            assertEquals(5, resultSet.getInt(1));
        }
    }

    @Test
    public void testGardenSize() throws SQLException {
        String query = "SELECT size FROM garden WHERE name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, "Garden3");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                assertTrue(resultSet.next());
                assertEquals(300, resultSet.getInt("size"));
            }
        }
    }

    @Test
    public void testNonExistentGarden() throws SQLException {
        String query = "SELECT * FROM garden WHERE name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, "NonExistentGarden");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                assertFalse(resultSet.next());
            }
        }
    }
}
