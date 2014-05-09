package domain;

import java.util.ArrayList;
import java.util.List;

public class Domain {

    public List<Person> persons;

    public Domain() {
        persons = new ArrayList<Person>();
        Person oscar = new Person("Oscar Nunes", 23, "onunes@mail.com");
        Person andre = new Person("André Pereira", 26, "apereira@mail.com");
        Person ana = new Person("Ana Mota", 31, "amota@mail.com");
        Person olavo = new Person("Olavo Silva", 19, "osilva@mail.com");
        oscar.contacts.add(ana);
        oscar.contacts.add(andre);
        oscar.contacts.add(olavo);
        andre.contacts.add(ana);
        andre.contacts.add(oscar);
        olavo.contacts.add(ana);
        olavo.contacts.add(oscar);
        ana.contacts.add(andre);
        ana.contacts.add(olavo);
        ana.contacts.add(oscar);
        persons.add(ana);
        persons.add(andre);
        persons.add(olavo);
        persons.add(oscar);
    }

}
