package co.com.email.util.template;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

@Component
public class TemplateRenderer {

    private final MustacheFactory mustacheFactory = new DefaultMustacheFactory();

    public String render(String templateContent, Map<String, Object> data) {

        Mustache mustache = mustacheFactory.compile(new StringReader(templateContent), "template");

        StringWriter writer = new StringWriter();
        mustache.execute(writer, data);

        return writer.toString();
    }
}
