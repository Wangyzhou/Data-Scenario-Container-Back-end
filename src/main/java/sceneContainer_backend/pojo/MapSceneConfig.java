package sceneContainer_backend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/4/17 20:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MapSceneConfig {
    @Id
    private String id;
    private String sceneId;
    private Double bearing;
    private Double pitch;
    private Double zoom;
    private List<Double> sceneEnvelop;
    private List<Double> center;
    private List<SceneTool> sceneToolLab;   //TODO:["toolId1","toolId2","toolId3","toolId4"] 待定！
    private List<MapSceneLayer> sceneLayerGroup;    //["layerId1","layerId2","layerId3","layerId4"]
}
