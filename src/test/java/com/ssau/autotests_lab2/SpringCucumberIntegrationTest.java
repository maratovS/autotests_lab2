
package com.ssau.autotests_lab2;

import com.ssau.autotests_lab2.db.repository.CalculationResultRepository;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(classes = AutotestsLab2Application.class)
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources",
        glue = "com.ssau.autotests_lab2",
        tags = "@all",
        dryRun = false,
        snippets = CucumberOptions.SnippetType.CAMELCASE
)
@ContextConfiguration(classes = AutotestsLab2Application.class)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
public abstract class SpringCucumberIntegrationTest {

    private ManualRestDocumentation restDocumentation;

    public void setUp() {
        restDocumentation = new ManualRestDocumentation("target/generated-snippets");

        restDocumentation.beforeTest(CucumberTestsDefinition.class, "setUp");
    }

    public void tearDown() {
        restDocumentation.afterTest();
    }

}