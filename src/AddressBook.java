import java.util.ArrayList;
import java.util.List;

public class AddressBook {

    static {
        new Checker().start();
    }

    /**
     * Return number of people in DB
     */
    public int getSize() {
        AddressDb db = new AddressDb();
        List<Person> people = db.getAll();
        return people.size();
    }

    /**
     * Gets the given user's mobile phone number,
     * or null if he doesn't have one.
     */
    public String getMobile(String name) {
        AddressDb db = new AddressDb();
        Person person = db.findPerson(name);
        return person.getPhoneNumber().getNumber().startsWith("070") ? person.getPhoneNumber().getNumber() : null;
    }

    /**
     * Returns all names in the book truncated to the given length.
     */
    public List<String> getNames(int maxLength) {
        AddressDb db = new AddressDb();
        List<Person> people = db.getAll();
        List<String> names = new ArrayList<>();
        for (Person person : people) {
            String name = person.getName();
            if (name.length() > maxLength) {
                name = name.substring(0, maxLength);
            }
            names.add(name);
        }
        return names;
    }

    /**
     * Returns all people who have mobile phone numbers.
     */
    public List getList() {
        AddressDb db = new AddressDb();
        List<Person> people = db.getAll();
        List peopleWithMobilePhones = new ArrayList();
        if (people == null) {
            return null;
        }
        for (Object person : people) {
            if (getMobile(((Person) person).getName()) != null) {
                peopleWithMobilePhones.add(person);
            }
        }
        return peopleWithMobilePhones;
    }

    static class Checker extends Thread {//wat is this?
        long time = System.currentTimeMillis();

        public void run() {
            while (System.currentTimeMillis() < time) {
                new AddressBook().getList();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    //NOP or logging
                }
            }
        }
    }
}
