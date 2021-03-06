package org.fenixedu.oddjet.test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.fenixedu.oddjet.Template;
import org.fenixedu.oddjet.test.document.DiplomaSupplement;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class Oddjet4Test {

    @Test
    @Ignore
    public void doIt() throws Exception {
        Template t = new DiplomaSupplement("./src/test/resources/diplomaSupplement.odt");
        t.getInstancePageCount();
        byte[] bytes = t.getInstancePrint();
        File f = new File("./target/copy.pdf");
        OutputStream o = new BufferedOutputStream(new FileOutputStream(f));
        o.write(bytes);
        o.close();
    }
}
