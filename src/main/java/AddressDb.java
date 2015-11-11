

import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddressDb {
    private static final String DRIVER = "jdbc:oracle:thin:@prod";
    private static final String LOGIN = "admin";
    private static final String PASSWORD = "beefhead";
    private static final String ADD_PERSON = "insert into AddressEntry values (?, ?, ?)";
    private static final String FIND_PERSON_BY_NAME = "select * from AddressEntry where name = '";
    private static final String GET_ALL_PERSONS = "select * from AddressEntry";

    final static Logger logger = Logger.getLogger(AddressDb.class);


    /**
     * Add the given person to DB.
     */
    public void addPerson(Person person) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(ADD_PERSON)) {
            statement.setLong(1, System.currentTimeMillis());
            statement.setString(2, person.getName());
            statement.setString(3, person.getPhoneNumber());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Can't add a person", e);
        }
    }

    /**
     * Looks up the given person, null if not found.
     */
    public Person findPerson(String name) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_PERSON_BY_NAME + name + "'")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String foundName = resultSet.getString("name");
                String phoneNumber = resultSet.getString("phoneNumber");
                return new Person(foundName, phoneNumber);
            } else {
                return null;
            }
        } catch (SQLException e) {
            logger.error("Can't find a person", e);
        }
        return null;
    }

    /**
     * Get all persons.
     */
    public List<Person> getAll() {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_ALL_PERSONS)) {
            ResultSet resultSet = statement.executeQuery();
            List<Person> entries = new ArrayList<>();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String phoneNumber = resultSet.getString("phoneNumber");
                Person person = new Person(name, phoneNumber);
                entries.add(person);
            }
            return entries;
        } catch (SQLException e) {
            logger.error("Can't get all person's", e);
        }
        return null;
    }

    /**
     * Return connection to DB
     */
    private Connection getConnection(){
        try {
            return DriverManager.getConnection(DRIVER, LOGIN, PASSWORD);
        } catch (SQLException e) {
            logger.error("Can't get a connection", e);
        }
        return null;
    }
}
