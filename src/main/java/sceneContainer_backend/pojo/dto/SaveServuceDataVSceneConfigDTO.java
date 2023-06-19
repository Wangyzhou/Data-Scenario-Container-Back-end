package sceneContainer_backend.pojo.dto;

import lombok.Data;
import sceneContainer_backend.pojo.MapSceneLayer;

import java.util.List;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/6/12 16:27
 */
@Data
public class SaveServuceDataVSceneConfigDTO {
    private String id;
    private Double bearing;
    private Double pitch;
    private Double zoom;
    private List<Double> center;
    private List<Double> sceneEnvelop;
    private List<MapSceneLayer> sceneLayerGroup;
}
