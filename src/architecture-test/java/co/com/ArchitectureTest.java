package co.com;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "co.com.email",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class ArchitectureTest {

    private static final String DOMAIN = "co.com.email.domain..";
    private static final String APP_SERVICE = "co.com.email.service..";
    private static final String APP_REPOSITORY = "co.com.email.repositories..";
    private static final String INFRA_CONTROLLER = "co.com.email.controller..";
    private static final String INFRA_KAFKA = "co.com.email.kafka..";
    private static final String INFRA_CLIENT = "co.com.email.client..";
    private static final String INFRA_SCHEDULER = "co.com.email.scheduler..";
    private static final String INFRA_SECURITY = "co.com.email.security..";
    private static final String INFRA_CONFIG = "co.com.email.config..";

    // ================================================================
    // RULE 1: Domain must not depend on Application layer
    // ================================================================
    @ArchTest
    static final ArchRule domainMustNotDependOnApplicationService =
            noClasses().that().resideInAPackage(DOMAIN)
                    .should().dependOnClassesThat().resideInAPackage(APP_SERVICE)
                    .as("Domain layer must not depend on Application service layer");

    @ArchTest
    static final ArchRule domainMustNotDependOnRepositories =
            noClasses().that().resideInAPackage(DOMAIN)
                    .should().dependOnClassesThat().resideInAPackage(APP_REPOSITORY)
                    .as("Domain layer must not depend on repository (output port) layer");

    // ================================================================
    // RULE 2: Domain must not depend on Infrastructure layer
    // ================================================================
    @ArchTest
    static final ArchRule domainMustNotDependOnInfrastructure =
            noClasses().that().resideInAPackage(DOMAIN)
                    .should().dependOnClassesThat().resideInAnyPackage(
                            INFRA_CONTROLLER, INFRA_KAFKA, INFRA_CLIENT,
                            INFRA_SCHEDULER, INFRA_SECURITY, INFRA_CONFIG
                    )
                    .as("Domain layer must not depend on any Infrastructure adapter");

    // ================================================================
    // RULE 3: Application (service) must not depend on Infrastructure
    // ================================================================
    @ArchTest
    static final ArchRule applicationMustNotDependOnInfrastructure =
            noClasses().that().resideInAPackage(APP_SERVICE)
                    .should().dependOnClassesThat().resideInAnyPackage(
                            INFRA_CONTROLLER, INFRA_KAFKA, INFRA_CLIENT,
                            INFRA_SCHEDULER, INFRA_SECURITY, INFRA_CONFIG
                    )
                    .as("Application layer must not depend on Infrastructure adapters");

    // ================================================================
    // RULE 4: Repository output ports must not depend on services
    // ================================================================
    @ArchTest
    static final ArchRule repositoriesMustNotDependOnApplicationServices =
            noClasses().that().resideInAPackage(APP_REPOSITORY)
                    .should().dependOnClassesThat().resideInAnyPackage(
                            APP_SERVICE, INFRA_CONTROLLER, INFRA_KAFKA,
                            INFRA_CLIENT, INFRA_SCHEDULER, INFRA_SECURITY, INFRA_CONFIG
                    )
                    .as("Repository output ports must not depend on Application services or Infrastructure");

    // ================================================================
    // RULE 5: Web controllers must not depend on other infra adapters
    // ================================================================
    @ArchTest
    static final ArchRule controllersMustNotDependOnInfraAdapters =
            noClasses().that().resideInAPackage(INFRA_CONTROLLER)
                    .should().dependOnClassesThat().resideInAnyPackage(
                            INFRA_KAFKA, INFRA_CLIENT, INFRA_SCHEDULER, APP_REPOSITORY
                    )
                    .as("Web controllers must not depend on other Infrastructure adapters or output ports");

    // ================================================================
    // RULE 6: Kafka consumers must not depend on web or scheduler infra
    // ================================================================
    @ArchTest
    static final ArchRule kafkaMustNotDependOnWebOrSchedulerInfrastructure =
            noClasses().that().resideInAPackage(INFRA_KAFKA)
                    .should().dependOnClassesThat().resideInAnyPackage(
                            INFRA_CONTROLLER, INFRA_SCHEDULER, APP_REPOSITORY
                    )
                    .as("Kafka consumers must not depend on web controllers, schedulers or output ports directly");

    // ================================================================
    // RULE 7: No cyclic dependencies between top-level packages
    // ================================================================
    @ArchTest
    static final ArchRule noCyclicDependencies =
            SlicesRuleDefinition.slices()
                    .matching("co.com.email.(*)..")
                    .should().beFreeOfCycles()
                    .as("No cyclic dependencies between top-level packages in co.com.email");

    // ================================================================
    // RULE 8: @RestController classes must reside in controller package
    // ================================================================
    @ArchTest
    static final ArchRule restControllersMustBeInControllerPackage =
            classes().that().areAnnotatedWith(RestController.class)
                    .should().resideInAPackage(INFRA_CONTROLLER)
                    .as("Classes annotated @RestController must reside in the controller package");

    // ================================================================
    // RULE 9: @Service classes must reside in application service package
    // ================================================================
    @ArchTest
    static final ArchRule serviceAnnotationMustBeInServicePackage =
            classes().that().areAnnotatedWith(Service.class)
                    .should().resideInAPackage(APP_SERVICE)
                    .as("Classes annotated @Service must reside in the application service package");

    // ================================================================
    // RULE 10: Classes named *Impl must implement at least one interface
    // ================================================================
    @ArchTest
    static final ArchRule implClassesMustImplementAnInterface =
            classes().that().haveSimpleNameEndingWith("Impl")
                    .should(new ArchCondition<JavaClass>("implement at least one interface") {
                        @Override
                        public void check(JavaClass javaClass, ConditionEvents events) {
                            if (javaClass.getInterfaces().isEmpty()) {
                                events.add(SimpleConditionEvent.violated(
                                        javaClass,
                                        javaClass.getName() + " does not implement any interface"));
                            }
                        }
                    })
                    .as("Classes suffixed with 'Impl' must implement at least one interface");

    // ================================================================
    // RULE 11: Controllers must depend on service interfaces, not impls
    // ================================================================
    @ArchTest
    static final ArchRule controllersMustNotDependOnServiceImplementations =
            noClasses().that().resideInAPackage(INFRA_CONTROLLER)
                    .should().dependOnClassesThat().haveSimpleNameEndingWith("Impl")
                    .as("Web controllers must depend on service interfaces, not their implementations");

    // ================================================================
    // RULE 12: Kafka consumers must not depend on service implementations
    // ================================================================
    @ArchTest
    static final ArchRule kafkaConsumersMustNotDependOnServiceImplementations =
            noClasses().that().resideInAPackage(INFRA_KAFKA)
                    .should().dependOnClassesThat().haveSimpleNameEndingWith("Impl")
                    .as("Kafka consumers must depend on application service interfaces, not implementations");

    // ================================================================
    // RULE 13: Infrastructure client adapters must implement a service port
    // ================================================================
    @ArchTest
    static final ArchRule clientImplementationsMustImplementServicePorts =
            classes().that().resideInAPackage(INFRA_CLIENT)
                    .and().haveSimpleNameEndingWith("Impl")
                    .should().implement(
                            DescribedPredicate.describe("a service port interface from " + APP_SERVICE,
                                    (JavaClass iface) -> iface.getPackageName().startsWith("co.com.email.service")
                                            || iface.getPackageName().startsWith("co.com.email.client"))
                    )
                    .as("Infrastructure client implementations must implement a port defined in the service or client layer");
}