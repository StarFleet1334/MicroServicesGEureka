package com.micro.workload.workloadComponentTest;


import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/feature/workload",
        glue = "com.micro.workload.workloadComponentTest.steps",
        plugin = {"pretty", "html:target/new-reports.html", "json:target/cucumber.json"},
        monochrome = true,
        publish = true
)
public class WorkloadCucumberTest {
}
