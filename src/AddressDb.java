import java.sql.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AddressDb {
    private Executor executor = Executors.newFixedThreadPool(18);
    private int CACHE_SIZE = 50;

    public AddressDb() {
        try {
            Class.forName("oracle.jdbc.ThinDriver");
        } catch (ClassNotFoundException e) {
        }

        new Thread().start();
    }

    public void addPerson(Person person) {
        try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@prod", "admin", "beefhead");
        PreparedStatement statement = connection.prepareStatement("insert into AddressEntry values (?, ?, ?)");){
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
        PreparedStatement preparedStatement;
        try {
            ResultSet resultSet;
            Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@prod", "admin", "beefhead");
            preparedStatement = connection.prepareStatement("select * from AddressEntry where name = '" + name + "'");
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String foundName = resultSet.getString("name");
                PhoneNumber phoneNumber = new PhoneNumber(resultSet.getString("phoneNumber"));
                Person person = new Person(foundName, phoneNumber);
                return person;
            } else {
                return null;
            }

        } catch (SQLException e) {
            return null;
        } catch (IllegalArgumentException x) {
            throw x;
        }
    }

    public List<Person> getAll() {
        ResultSet result = null;
        try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@prod", "admin", "beefhead");
        PreparedStatement statement = connection.prepareStatement("select * from AddressEntry")){

            HashSet set = new HashSet();

            result = statement.executeQuery();
            List<Person> entries = new LinkedList<Person>();
            while (result.next()) {
                String name = result.getString("name");
                PhoneNumber phoneNumber = new PhoneNumber(result.getString("phoneNumber"));
                Person person = new Person(name, phoneNumber);
                entries.add(person);
                set.add(person);
            }
            return entries;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException err) {
                    throw new RuntimeException(err);
                }
            }
        }
    }
}
