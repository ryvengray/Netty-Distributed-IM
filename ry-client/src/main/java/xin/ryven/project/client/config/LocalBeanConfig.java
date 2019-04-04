package xin.ryven.project.client.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @author gray
 */
@Configuration
public class LocalBeanConfig {

    private final ApplicationProperties applicationProperties;

    @Autowired
    public LocalBeanConfig(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }


}