package sceneContainer_backend.pojo.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/4/21 16:57
 */
@Data
public class CreateSceneDTO {
    private String name;
    private String type;
    private String userId;
    private List<Map<String, String>> dataSet;
    private List<Map<String, Object>> toolSet;
}
