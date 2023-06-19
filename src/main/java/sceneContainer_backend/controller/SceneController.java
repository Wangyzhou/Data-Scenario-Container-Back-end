package sceneContainer_backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sceneContainer_backend.domains.ResponseResult;
import sceneContainer_backend.pojo.Scene;
import sceneContainer_backend.pojo.ServiceDataVSceneConfig;
import sceneContainer_backend.pojo.dto.CreateMapSceneDTO;
import sceneContainer_backend.pojo.dto.CreateSceneDTO;
import sceneContainer_backend.pojo.dto.SaveSceneDTO;
import sceneContainer_backend.pojo.dto.SaveServuceDataVSceneConfigDTO;
import sceneContainer_backend.repository.SceneRepository;
import sceneContainer_backend.service.MapSceneService;
import sceneContainer_backend.service.SceneService;

import java.util.Date;
import java.util.UUID;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/4/21 17:02
 */
@RestController
@RequestMapping("/scene")
public class SceneController {

    @Autowired
    private SceneService sceneService;

    @PostMapping
    public ResponseResult createScene(@RequestBody CreateSceneDTO createSceneDTO) {
        return sceneService.createScene(createSceneDTO);
    }

    @GetMapping
    public ResponseResult getSceneList(@RequestParam("userId") String userId) {
        return sceneService.getSceneList(userId);
    }

    @GetMapping("/getSceneConfig")
    public ResponseResult getSceneConfig(@RequestParam("sceneType") String sceneType,
                                         @RequestParam("sceneId") String sceneId) {
        return sceneService.getSceneConfig(sceneType, sceneId);
    }

    @DeleteMapping
    public ResponseResult deleteScene(@RequestParam("sceneId") String sceneId) {
        return sceneService.deleteScene(sceneId);
    }

    @PostMapping(value = "/save")
    public ResponseResult saveScene(SaveSceneDTO saveSceneDTO, @RequestParam("sceneConfig")String sceneConfig) {
        System.out.println("sceneConfig = " + sceneConfig);
        ObjectMapper objectMapper = new ObjectMapper();
        SaveServuceDataVSceneConfigDTO saveServuceDataVSceneConfigDTO = null;
        try {
            saveServuceDataVSceneConfigDTO = objectMapper.readValue(sceneConfig, SaveServuceDataVSceneConfigDTO.class);
            System.out.println("saveServuceDataVSceneConfigDTO = " + saveServuceDataVSceneConfigDTO);
            System.out.println("testData = " + saveSceneDTO);
            return sceneService.saveScene(saveSceneDTO, saveServuceDataVSceneConfigDTO);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
//        System.out.println("sceneImg = " + sceneImg);
//        System.out.println("lastUpdatedTime = " + lastUpdatedTime);
//        System.out.println("editNum = " + editNum);
    }
}
