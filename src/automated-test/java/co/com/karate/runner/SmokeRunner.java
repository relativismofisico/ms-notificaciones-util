package co.com.karate.runner;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Ejecuta únicamente escenarios @smoke — ideal para validación rápida post-deploy.
 * Compatible con @production-safe: en prod solo corren escenarios que son @smoke Y @production-safe.
 */
class SmokeRunner {

    @Test
    void runSmokeTests() {
        String env = System.getProperty("karate.env", "dev");
        String tag = "prod".equals(env) ? "@smoke,@production-safe" : "@smoke";

        Results results = Runner.path("classpath:karate")
                .tags(tag)
                .outputCucumberJson(true)
                .parallel(3);

        assertEquals(0, results.getFailCount(),
                "Smoke tests fallidos:\n" + results.getErrorMessages());
    }
}