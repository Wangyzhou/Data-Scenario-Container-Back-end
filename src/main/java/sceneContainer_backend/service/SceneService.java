package sceneContainer_backend.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sceneContainer_backend.domains.ResponseResult;
import sceneContainer_backend.pojo.MapSceneConfig;
import sceneContainer_backend.pojo.Scene;
import sceneContainer_backend.pojo.ServiceDataVSceneConfig;
import sceneContainer_backend.pojo.dto.CreateSceneDTO;
import sceneContainer_backend.pojo.dto.SaveSceneDTO;
import sceneContainer_backend.pojo.dto.SaveServuceDataVSceneConfigDTO;
import sceneContainer_backend.repository.GeoDataFileRepository;
import sceneContainer_backend.repository.MapSceneConfigRepository;
import sceneContainer_backend.repository.SceneRepository;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Integer.parseInt;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/4/17 22:16
 */
@Service
@Slf4j
public class SceneService {

    @Autowired
    private SceneRepository sceneRepository;

    @Autowired
    private GeoDataFileRepository geoDataFileRepository;

    @Autowired
    private MapSceneConfigRepository mapSceneConfigRepository;

    @Autowired
    private MapSceneService mapSceneService;

    @Autowired
    private ServiceDataVSceneService serviceDataVSceneService;

    public ResponseResult createScene(CreateSceneDTO createSceneDTO) {
        Scene scene = new Scene();
        scene.setSceneId(UUID.randomUUID().toString());
        scene.setSceneName(createSceneDTO.getName());
        scene.setSceneType(createSceneDTO.getType());
        scene.setDataSet(createSceneDTO.getDataSet());
        scene.setToolSet(createSceneDTO.getToolSet());
        scene.setUserId(createSceneDTO.getUserId());
        scene.setEditNum(0);
        Date date = new Date();
        String newData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        scene.setCreateTime(newData);
        scene.setLastUpdatedTime(newData);
        scene.setSceneImg(null);
        sceneRepository.insert(scene);
        switch (createSceneDTO.getType()) {
            case "analysis":
                return mapSceneService.createMapSceneConfig(scene);
            case "visualization_service":
                return serviceDataVSceneService.createServiceDataVConfig(scene);
            default:
                return new ResponseResult(201, "未知的错误！");
        }
    }

    public ResponseResult getSceneList(String userId) {
        List<Scene> sceneList = sceneRepository.findAllByUserId(userId);
        return new ResponseResult<>(200, "获取成功！", sceneList);
    }

    public ResponseResult getSceneConfig(String sceneType, String sceneId) {
        switch (sceneType) {
            case "analysis":
                MapSceneConfig mapSceneConfig = mapSceneService.getMapSceneConfigBySceneId(sceneId);
                return new ResponseResult(200, "获取成功", JSON.toJSON(mapSceneConfig));
            case "visualization_service":
                ServiceDataVSceneConfig serviceDataVSceneConfig = serviceDataVSceneService.getServiceDataVSceneConfigBySceneId(sceneId);
                return new ResponseResult(200, "获取成功", serviceDataVSceneConfig);
            default:
                return new ResponseResult(201, "获取失败");
        }
    }

    public ResponseResult deleteScene(String sceneId) {
        Scene deleteScene = sceneRepository.findSceneBySceneId(sceneId);
        String sceneType = deleteScene.getSceneType();
        switch (sceneType) {
            case "analysis":
                mapSceneConfigRepository.deleteBySceneId(sceneId);
                break;
        }
        sceneRepository.delete(deleteScene);
        return new ResponseResult(200, "删除成功！");
    }

    public ResponseResult saveScene(SaveSceneDTO saveSceneDTO, Object sceneConfig) {
        Scene scene = sceneRepository.findSceneBySceneId(saveSceneDTO.getSceneId());
        try {
            scene.setSceneImg(new Binary(saveSceneDTO.getSceneImg().getBytes()));
            scene.setEditNum(parseInt(saveSceneDTO.getEditNum()));
            scene.setLastUpdatedTime(saveSceneDTO.getLastUpdatedTime());
            sceneRepository.save(scene);
            switch (saveSceneDTO.getSceneType()) {
                case "visualization_service":
                    serviceDataVSceneService.saveSceneConfig((SaveServuceDataVSceneConfigDTO)sceneConfig);
                    break;
                case "analysis":
                    mapSceneService.saveAnalysisSceneConfig((SaveServuceDataVSceneConfigDTO)sceneConfig);
                    break;
            }
            return new ResponseResult(200, "场景保存成功！");
        } catch (IOException e) {
            return new ResponseResult(201, e.getMessage());
        }
    }
}
