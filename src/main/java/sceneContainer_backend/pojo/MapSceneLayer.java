package sceneContainer_backend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/4/17 20:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MapSceneLayer {
    private String id;
    private String layerName;
    private String dataType;
    private String mvtUrl;
    private String type;
    private Map<String, Object> layout;
    private Map<String, Object> paint;
    private String sourceLayer;
}
