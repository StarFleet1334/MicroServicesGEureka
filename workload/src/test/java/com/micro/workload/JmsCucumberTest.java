package com.micro.workload;



import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/feature/jms",
        glue = "com.micro.workload.steps.jms",
        plugin = {"pretty", "html:target/new-reports.html", "json:target/cucumber.json"},
        monochrome = true,
        publish = true
)
public class JmsCucumberTest {

}
