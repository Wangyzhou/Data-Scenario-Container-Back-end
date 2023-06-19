package sceneContainer_backend.service;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import sceneContainer_backend.domains.ResponseResult;
import sceneContainer_backend.pojo.*;
import sceneContainer_backend.pojo.dto.CreateMapSceneDTO;
import sceneContainer_backend.pojo.dto.SaveServuceDataVSceneConfigDTO;
import sceneContainer_backend.repository.GeoDataFileRepository;
import sceneContainer_backend.repository.MapSceneConfigRepository;
import sceneContainer_backend.repository.ToolRepository;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/4/17 22:09
 */
@Service
@Slf4j
public class MapSceneService {

    @Autowired
    private GeoDataFileRepository geoDataFileRepository;

    @Autowired
    private ToolRepository toolRepository;

    @Autowired
    private MapSceneConfigRepository mapSceneConfigRepository;

    @Value("${backendIP}")
    private String backendIP;

    @Value("${server.port}")
    private String port;

    public ResponseResult createMapSceneConfig(Scene scene) {
        //1、创建Scene记录，需要id、name、type、createTime、lastUpdatedTime、userId、editNum、img属性
        MapSceneConfig mapSceneConfig = new MapSceneConfig();
        mapSceneConfig.setId(IdUtil.objectId());
        mapSceneConfig.setSceneId(scene.getSceneId());
        mapSceneConfig.setBearing(0.0);
        mapSceneConfig.setPitch(0.0);
        mapSceneConfig.setZoom((double) -1);
        ArrayList<MapSceneLayer> sceneLayers = new ArrayList<>();
        List<GeoDataFile> layers = new ArrayList<>();
        scene.getDataSet().forEach(dataMap -> {
            MapSceneLayer mapSceneLayer = new MapSceneLayer();
            GeoDataFile geoDataFile = geoDataFileRepository.findOneById(dataMap.get("id"));
            layers.add(geoDataFile);
            String layerVisualType = getLayerVisualType(geoDataFile.getDataType());
            String ptName = geoDataFile.getPtName();
            HashMap<String, Object> layout = new HashMap<>();
            layout.put("visibility", "visible");
            mapSceneLayer.setId(geoDataFile.getId());
            mapSceneLayer.setLayerName(geoDataFile.getDisplayName());
            mapSceneLayer.setDataType(geoDataFile.getDataType());
            mapSceneLayer.setSourceLayer(ptName);
            mapSceneLayer.setType(layerVisualType);
            mapSceneLayer.setLayout(layout);
            mapSceneLayer.setMvtUrl("http://" + backendIP + ":" + port + "/mvt/" + ptName + "/{z}/{x}/{y}.pbf");
            HashMap<String, Object> paint = new HashMap<>();
            Random random = new Random();
            int r = random.nextInt(256);
            int g = random.nextInt(256);
            int b = random.nextInt(256);
            paint.put(layerVisualType + "-color", String.format("#%02X%02X%02X", r, g, b));
            paint.put(layerVisualType + "-opacity", 1);
            if (layerVisualType.equals("fill")) {
                paint.put(layerVisualType + "-outline-color", "rgba(255,255,255,1)");
            }
            mapSceneLayer.setPaint(paint);
            sceneLayers.add(mapSceneLayer);
        });
        mapSceneConfig.setSceneLayerGroup(sceneLayers);
        ArrayList<SceneTool> sceneToolSet = new ArrayList<>();
        scene.getToolSet().forEach(toolMap -> {
            Tool tool = toolRepository.findToolById(toolMap.get("id").toString());
            SceneTool sceneTool = new SceneTool();
            sceneTool.setId(tool.getId());
            sceneTool.setLabel(tool.getName());
            sceneTool.setType(tool.getType());
            sceneTool.setDescription(tool.getDescription());
            sceneToolSet.add(sceneTool);
        });
        mapSceneConfig.setSceneToolLab(sceneToolSet);
        List<Double> minBounds = this.getMutiBoundsMinEnvelop(layers);
        mapSceneConfig.setSceneEnvelop(minBounds);
        mapSceneConfig.setCenter(this.getCenter(minBounds));
        mapSceneConfigRepository.insert(mapSceneConfig);
        //2、创建用于场景可视化的MapScene记录，需要id、sceneId、bearing、pitch、zoom、sceneEnvelop、center、sceneToolLab、sceneLayerGroup属性
        //其中sceneLayerGroup又需要mvtUrl、type、layout、paint、sourceLayer属性
        return new ResponseResult(200, "创建场景成功！", JSON.toJSON(mapSceneConfig));
    }

    public void saveAnalysisSceneConfig(SaveServuceDataVSceneConfigDTO saveServuceDataVSceneConfigDTO) {
        MapSceneConfig mapSceneConfig = mapSceneConfigRepository.findMapSceneConfigById(saveServuceDataVSceneConfigDTO.getId());
        mapSceneConfig.setCenter(saveServuceDataVSceneConfigDTO.getCenter());
        mapSceneConfig.setSceneEnvelop(saveServuceDataVSceneConfigDTO.getSceneEnvelop());
        mapSceneConfig.setSceneLayerGroup(saveServuceDataVSceneConfigDTO.getSceneLayerGroup());
        mapSceneConfig.setPitch(saveServuceDataVSceneConfigDTO.getPitch());
        mapSceneConfig.setBearing(saveServuceDataVSceneConfigDTO.getBearing());
        mapSceneConfig.setZoom(saveServuceDataVSceneConfigDTO.getZoom());
        mapSceneConfigRepository.save(mapSceneConfig);
    }

    public MapSceneConfig getMapSceneConfigBySceneId(String sceneId) {
        return mapSceneConfigRepository.findMapSceneConfigBySceneId(sceneId);
    }

    public List<Double> getMutiBoundsMinEnvelop(@NonNull List<GeoDataFile> layers) {
        List<Double> mutiBoundsMinEnvelop = layers.get(0).getBounds();
        layers.forEach(layer -> {
            List<Double> bounds = layer.getBounds();
            if (bounds.get(0) < mutiBoundsMinEnvelop.get(0)) {
                mutiBoundsMinEnvelop.set(0, bounds.get(0));
            }
            if (bounds.get(1) < mutiBoundsMinEnvelop.get(1)) {
                mutiBoundsMinEnvelop.set(1, bounds.get(1));
            }
            if (bounds.get(2) > mutiBoundsMinEnvelop.get(2)) {
                mutiBoundsMinEnvelop.set(2, bounds.get(2));
            }
            if (bounds.get(3) > mutiBoundsMinEnvelop.get(3)) {
                mutiBoundsMinEnvelop.set(3, bounds.get(3));
            }
        });
        return mutiBoundsMinEnvelop;
    }

    public List<Double> getCenter(List<Double> bounds) {
        ArrayList<Double> center = new ArrayList<>();
        center.add((bounds.get(0) + bounds.get(2)) / 2);
        center.add((bounds.get(1) + bounds.get(3)) / 2);
        return center;
    }

    public String getLayerVisualType(String geoType) {
        switch (geoType) {
            case "MULTILINESTRING":
                return "line";
            case "POINT":
                return "circle";
            case "MULTIPOLYGON":
                return "fill";
            default:
                return null;
        }
    }
}
