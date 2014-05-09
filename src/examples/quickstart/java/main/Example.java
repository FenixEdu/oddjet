package main;

import org.fenixedu.oddjet.Template;
import org.fenixedu.oddjet.table.EntryListTableData;

import domain.Domain;
import domain.Person;

/**
 * ODDJET simple example.
 * 
 * @author Gil Lacerda (gil.lacerda@tecnico.ulisboa.pt)
 * 
 */
public class Example {

    /**
     * Don't forget to run <code>$ soffice --headless --accept="socket,host=<host>,port=<port>;urp;"</code> in a terminal before
     * executing this function.
     */
    public static void main(String[] args) {

        //build a simple domain
        Domain domain = new Domain();

        //construct the template object, passing it the template document
        Template template = new Template("src/examples/quickstart/templates/Contacts.odt");

        for (Person targetPerson : domain.persons) {
            //add the necessary data to instantiate the template.
            template.addParameter("person", targetPerson);
            template.addTableDataSource("contacts", new EntryListTableData(targetPerson.contacts));

            //instantiate, convert to pdf and save
            template.saveInstancePDF("target/" + targetPerson.name + " Contacts.pdf");

            //clear the template data - it's not really necessary in this case!
            //template.clearParameters();
            //template.clearTableDataSources();
        }

    }
}
