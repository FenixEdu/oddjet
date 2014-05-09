package domain;

import java.util.ArrayList;
import java.util.List;

public class Person {

    public String name;
    public int age;
    public String email;
    public List<Person> contacts;

    public Person(String name, int age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
        this.contacts = new ArrayList<Person>();
    }

}
