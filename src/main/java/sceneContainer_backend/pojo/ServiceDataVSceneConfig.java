package sceneContainer_backend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/5/23 16:55
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDataVSceneConfig {
    @Id
    private String id;
    private String sceneId;
    private Double bearing;
    private Double pitch;
    private Double zoom;
    private List<Double> sceneEnvelop;
    private List<Double> center;
    private List<ServiceDataVSceneTool> sceneToolLab;
    private List<MapSceneLayer> sceneLayerGroup;    //["layerId1","layerId2","layerId3","layerId4"]
}
