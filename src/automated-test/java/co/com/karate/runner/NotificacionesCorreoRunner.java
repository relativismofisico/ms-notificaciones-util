package co.com.karate.runner;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Runner principal — ejecuta toda la suite funcional en paralelo.
 * Excluye escenarios marcados con @skip.
 * En producción (karate.env=prod) solo corre @production-safe.
 */
class NotificacionesCorreoRunner {

    @Test
    void runFunctionalTests() {
        String env = System.getProperty("karate.env", "dev");
        String[] tags = "prod".equals(env)
                ? new String[]{"@production-safe"}
                : new String[]{"~@skip"};

        Results results = Runner.path("classpath:karate/notificar-correo")
                .tags(tags)
                .outputCucumberJson(true)
                .parallel(5);

        generateCucumberReport(results.getReportDir());

        assertEquals(0, results.getFailCount(),
                "Pruebas fallidas:\n" + results.getErrorMessages());
    }

    private void generateCucumberReport(String reportDir) {
        File reportOutputDir = new File("build/karate-reports/cucumber");
        Configuration config = new Configuration(reportOutputDir, "ms-notificaciones-util");
        config.addClassifications("Ambiente", System.getProperty("karate.env", "dev"));
        config.addClassifications("Rama", System.getenv().getOrDefault("GIT_BRANCH", "local"));

        Collection<File> jsonFiles = listJsonFiles(new File(reportDir));
        if (!jsonFiles.isEmpty()) {
            List<String> jsonPaths = new ArrayList<>();
            jsonFiles.forEach(f -> jsonPaths.add(f.getAbsolutePath()));
            ReportBuilder reportBuilder = new ReportBuilder(jsonPaths, config);
            reportBuilder.generateReports();
        }
    }

    private Collection<File> listJsonFiles(File dir) {
        List<File> files = new ArrayList<>();
        if (dir.exists() && dir.isDirectory()) {
            File[] candidates = dir.listFiles((d, name) -> name.endsWith(".json"));
            if (candidates != null) {
                for (File f : candidates) {
                    files.add(f);
                }
            }
        }
        return files;
    }
}