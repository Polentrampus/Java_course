import config.AppConfig;
import config.KafkaConfig;
import config.SchedulingConfig;
import config.TransferMessageSerializer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import service.ProducerService;

public class ProducerApplication {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                AppConfig.class,
                KafkaConfig.class,
                SchedulingConfig.class
        );
        context.scan("config", "service", "dto", "model");

        ProducerService producerService = context.getBean(ProducerService.class);

        Runtime.getRuntime().addShutdownHook(new Thread(context::close));
    }
}
