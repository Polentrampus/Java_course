import config.AppConfig;
import config.KafkaConfig;
import config.TransferMessageDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import service.ConsumerService;

@Slf4j
@Component
public class ConsumerApplication {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                AppConfig.class,
                KafkaConfig.class
        );
        context.scan("config", "service", "dto", "model");


        ConsumerService consumerService = context.getBean(ConsumerService.class);

        Runtime.getRuntime().addShutdownHook(new Thread(context::close));
    }
}