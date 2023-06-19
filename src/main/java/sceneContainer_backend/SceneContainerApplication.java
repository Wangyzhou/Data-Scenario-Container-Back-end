package sceneContainer_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "sceneContainer_backend.*")
public class SceneContainerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SceneContainerApplication.class, args);
	}

}
