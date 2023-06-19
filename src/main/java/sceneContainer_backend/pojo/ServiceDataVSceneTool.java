package sceneContainer_backend.pojo;

import lombok.Data;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/5/23 20:11
 */
@Data
public class ServiceDataVSceneTool {
    @Id
    private String id;
    private String name;
    private String label;
    private String description;
    private Binary toolImg;
}
