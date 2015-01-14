package org.fenixedu.oddjet;

import org.fenixedu.commons.configuration.ConfigurationInvocationHandler;
import org.fenixedu.commons.configuration.ConfigurationManager;
import org.fenixedu.commons.configuration.ConfigurationProperty;

public class OddjetConfiguration {
    @ConfigurationManager(description = "ODDJET Configuration")
    public interface ConfigurationProperties {
        @ConfigurationProperty(key = "oddjet.openoffice.service.host", description = "Host address of the OpenOffice service.",
                defaultValue = "localhost")
        public String openOfficeHost();

        @ConfigurationProperty(key = "oddjet.openoffice.service.port", description = "Port of the OpenOffice service.",
                defaultValue = "8100")
        public Integer openOfficePort();

        @ConfigurationProperty(key = "oddjet.openoffice.service.output", description = "Printing output format",
                defaultValue = "pdf")
        public String openOfficeOutput();

    }

    public static ConfigurationProperties getConfiguration() {
        return ConfigurationInvocationHandler.getConfiguration(ConfigurationProperties.class);
    }
}
