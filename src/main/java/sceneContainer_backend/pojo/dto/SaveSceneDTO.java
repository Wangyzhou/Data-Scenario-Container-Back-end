package sceneContainer_backend.pojo.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import sceneContainer_backend.pojo.ServiceDataVSceneConfig;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/6/12 14:55
 */
@Data
public class SaveSceneDTO {

    private String sceneId;

    private MultipartFile sceneImg;

    private String sceneType;

    private String lastUpdatedTime;

    private String editNum;

//    private SaveServuceDataVSceneConfigDTO sceneConfig;
}
