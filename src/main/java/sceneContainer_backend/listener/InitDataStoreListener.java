package sceneContainer_backend.listener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/3/10 21:59
 */
@Component
public class InitDataStoreListener implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${resourcesPath}")
    private String rootPath;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        File file = new File(rootPath);
        System.out.println("rootPath = " + rootPath);
        if(!file.exists()) {
            file.mkdirs();
        }
    }
}
