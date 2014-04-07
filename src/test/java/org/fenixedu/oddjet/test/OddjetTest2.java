package org.fenixedu.oddjet.test;

import org.fenixedu.oddjet.Template;
import org.fenixedu.oddjet.test.document.DiplomaSupplement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class OddjetTest2 {

    @Test
    public void doIt() throws Exception {
        Template t = new DiplomaSupplement("./src/test/resources/diplomaSupplement.odt");

        t.saveInstance("./target/diplomaSupplementInst.odt");
    }
}
