import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AddressDb {
    private static final String DRIVER = "jdbc:oracle:thin:@prod";
    private static final String LOGIN = "admin";
    private static final String PASSWORD = "beefhead";
    private static final String ADD_PERSON = "insert into AddressEntry values (?, ?, ?)";
    private static final String FIND_PERSON_BY_NAME = "select * from AddressEntry where name = '";
    private static final String GET_ALL_PERSONS = "select * from AddressEntry";
    private Executor executor = Executors.newFixedThreadPool(18);
    private int CACHE_SIZE = 50;


    public AddressDb() {//wat is this
        try {
            Class.forName("oracle.jdbc.ThinDriver");
        } catch (ClassNotFoundException e) {
            //NOP or logging
        }

        new Thread().start();
    }

    /**
     * Add the given person to DB.
     */
    public void addPerson(Person person) {//TODO: different statements. what is correct to update db(addPerson)
        try (Connection connection = DriverManager.getConnection(DRIVER, LOGIN, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(ADD_PERSON)) {
            // TODO:Logic
            statement.setLong(1, System.currentTimeMillis());
            statement.setString(2, person.getName());
            statement.setString(3, person.getPhoneNumber().getNumber());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Looks up the given person, null if not found.
     */
    public Person findPerson(String name) {
        try (Connection connection = DriverManager.getConnection(DRIVER, LOGIN, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_PERSON_BY_NAME + name + "'")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String foundName = resultSet.getString("name");
                PhoneNumber phoneNumber = new PhoneNumber(resultSet.getString("phoneNumber"));
                return new Person(foundName, phoneNumber);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get all persons.
     */
    public List<Person> getAll() {
        try (Connection connection = DriverManager.getConnection(DRIVER, LOGIN, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(GET_ALL_PERSONS)) {
            ResultSet resultSet = statement.executeQuery();
            List<Person> entries = new ArrayList<>();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                PhoneNumber phoneNumber = new PhoneNumber(resultSet.getString("phoneNumber"));
                Person person = new Person(name, phoneNumber);
                entries.add(person);
            }
            return entries;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
