package sceneContainer_backend.service;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sceneContainer_backend.domains.ResponseResult;
import sceneContainer_backend.pojo.*;
import sceneContainer_backend.pojo.dto.SaveServuceDataVSceneConfigDTO;
import sceneContainer_backend.repository.*;

import java.util.*;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/5/23 20:18
 */
@Service
public class ServiceDataVSceneService {

    @Autowired
    private GeoDataFileRepository geoDataFileRepository;

    @Autowired
    private ServiceDataVSceneToolRepository serviceDataVSceneToolRepository;

    @Autowired
    private ServiceDataVSceneConfigRepository serviceDataVSceneConfigRepository;

    @Autowired
    private MapSceneService mapSceneService;

    @Value("${backendIP}")
    private String backendIP;

    @Value("${server.port}")
    private String port;

    public void saveSceneConfig(SaveServuceDataVSceneConfigDTO sceneConfig) {
        ServiceDataVSceneConfig config = serviceDataVSceneConfigRepository.findServiceDataVSceneConfigById(sceneConfig.getId());
        config.setSceneEnvelop(sceneConfig.getSceneEnvelop());
        config.setBearing(sceneConfig.getBearing());
        config.setCenter(sceneConfig.getCenter());
        config.setZoom(sceneConfig.getZoom());
        config.setPitch(sceneConfig.getPitch());
        config.setSceneLayerGroup(sceneConfig.getSceneLayerGroup());
        serviceDataVSceneConfigRepository.save(config);
    }

    public ResponseResult createServiceDataVConfig(Scene scene) {
        ServiceDataVSceneConfig serviceDataVSceneConfig = new ServiceDataVSceneConfig();
        serviceDataVSceneConfig.setId(IdUtil.objectId());
        serviceDataVSceneConfig.setSceneId(scene.getSceneId());
        serviceDataVSceneConfig.setBearing(0.0);
        serviceDataVSceneConfig.setPitch(0.0);
        serviceDataVSceneConfig.setZoom((double) -1);
        ArrayList<MapSceneLayer> sceneLayers = new ArrayList<>();
        List<GeoDataFile> layers = new ArrayList<>();
        scene.getDataSet().forEach(dataMap -> {
            MapSceneLayer mapSceneLayer = new MapSceneLayer();
            GeoDataFile geoDataFile = geoDataFileRepository.findOneById(dataMap.get("id"));
            layers.add(geoDataFile);
            String layerVisualType = mapSceneService.getLayerVisualType(geoDataFile.getDataType());
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
        serviceDataVSceneConfig.setSceneLayerGroup(sceneLayers);
        ArrayList<ServiceDataVSceneTool> serviceDataVSceneTools = new ArrayList<>();
        scene.getToolSet().forEach(pTool -> {
            ServiceDataVSceneTool tool = serviceDataVSceneToolRepository.findServiceDataVSceneToolById(pTool.get("id").toString());
            serviceDataVSceneTools.add(tool);
        });
        serviceDataVSceneConfig.setSceneToolLab(serviceDataVSceneTools);
        List<Double> minBounds = mapSceneService.getMutiBoundsMinEnvelop(layers);
        serviceDataVSceneConfig.setSceneEnvelop(minBounds);
        serviceDataVSceneConfig.setCenter(mapSceneService.getCenter(minBounds));
        serviceDataVSceneConfigRepository.insert(serviceDataVSceneConfig);
        return new ResponseResult(200, "创建场景成功！", JSON.toJSON(serviceDataVSceneConfig));
    }

    public ServiceDataVSceneConfig getServiceDataVSceneConfigBySceneId(String sceneId) {
        return serviceDataVSceneConfigRepository.findServiceDataVSceneConfigBySceneId(sceneId);
    }
}
