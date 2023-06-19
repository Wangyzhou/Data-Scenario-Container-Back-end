package sceneContainer_backend.pojo.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/5/23 20:46
 */
@Data
public class CreateServiceDataVSceneToolDTO {
    private String name;
    private String label;
    private String description;
    private MultipartFile toolImg;
}
