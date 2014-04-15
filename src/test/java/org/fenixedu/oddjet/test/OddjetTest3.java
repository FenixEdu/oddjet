package org.fenixedu.oddjet.test;

import java.util.Locale;

import org.fenixedu.oddjet.Template;
import org.fenixedu.oddjet.test.bean.Person;
import org.fenixedu.oddjet.test.bean.Shirt;
import org.fenixedu.oddjet.test.bean.localeObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class OddjetTest3 {

    private Person person;
    private localeObject randW;

    @Before
    public void setUp() {

        person = new Person();
        person.setAge(22);
        person.setName("Zeca");
        Shirt zecasShirt = new Shirt();
        localeObject zecasShirtsColor = new localeObject();
        zecasShirtsColor.addContent(Locale.ENGLISH, "blue");
        zecasShirtsColor.addContent(Locale.FRENCH, "bleu");
        zecasShirt.setColor(zecasShirtsColor);
        person.setCurrentShirt(zecasShirt);
        localeObject zecasFruit = new localeObject();
        zecasFruit.addContent(Locale.ENGLISH, "peach");
        zecasFruit.addContent(Locale.FRENCH, "pêche");
        person.setFavouriteFruit(zecasFruit);
        randW = new localeObject();
        randW.addContent(Locale.ENGLISH, "Sarcophagus");
        randW.addContent(Locale.FRENCH, "Sarcophage");

    }

    @Test
    public void AttrAccess() throws Exception {
        Template t = new Template("./src/test/resources/AttrAccessTest.EN.odt", Locale.ENGLISH);
        t.addParameter("person", person);
        t.addParameter("randomWord", randW);
        t.saveInstance("./target/AttrAccessTestInst.EN.odt");
    }

    @Test
    public void diffLocale() throws Exception {
        Template t = new Template("./src/test/resources/AttrAccessTest.FR.odt", Locale.FRENCH);
        person.setDob("1992");
        t.addParameter("person", person);
        t.addParameter("randomWord", randW);
        t.saveInstance("./target/AttrAccessTestInst.FR.odt");
    }
}
