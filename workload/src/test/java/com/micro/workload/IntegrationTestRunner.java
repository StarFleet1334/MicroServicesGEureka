package com.micro.workload;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Runs all Cucumber features located in src/test/resources/features/integration
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/feature/integration",
        glue = "com.micro.workload.steps.integration",
        plugin = {"pretty", "html:target/new-reports.html", "json:target/cucumber.json"},
        monochrome = true,
        publish = true
)
public class IntegrationTestRunner {
}
