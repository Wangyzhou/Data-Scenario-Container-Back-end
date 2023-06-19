package sceneContainer_backend.pojo.dto;

import lombok.Data;
import sceneContainer_backend.pojo.MapSceneLayer;

import java.util.List;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/4/17 21:14
 */
@Data
public class CreateMapSceneDTO {
    private String id;
    private String name;
    private String type;
    private String userId;
    private List<MapSceneLayer> layers;
    private List<String> toolLab;
}
