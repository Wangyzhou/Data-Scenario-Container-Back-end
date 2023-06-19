package sceneContainer_backend.pojo.dto;

import lombok.Data;

import java.util.Map;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/4/23 21:34
 */
@Data
public class InvokeToolDTO {
    private String toolId;
    private String toolName;
    private Map<String, Object> toolConfig;
}
