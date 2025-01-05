package com.micro.workload;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/feature/trainer",
        glue = "com.micro.workload.steps.trainer",
        plugin = {"pretty", "html:target/new-reports.html", "json:target/cucumber.json"},
        monochrome = true,
        publish = true
)
public class TrainerCucumberTest {
}
