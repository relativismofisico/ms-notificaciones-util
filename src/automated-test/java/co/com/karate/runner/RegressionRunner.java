package co.com.karate.runner;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Ejecuta la suite completa @regression — para pipelines de CI/CD nocturnos o de pre-merge.
 * No se ejecuta en producción.
 */
class RegressionRunner {

    @Test
    void runRegressionTests() {
        String env = System.getProperty("karate.env", "dev");
        if ("prod".equals(env)) {
            return;
        }

        Results results = Runner.path("classpath:karate")
                .tags("@regression")
                .outputCucumberJson(true)
                .parallel(5);

        assertEquals(0, results.getFailCount(),
                "Regression tests fallidos:\n" + results.getErrorMessages());
    }
}