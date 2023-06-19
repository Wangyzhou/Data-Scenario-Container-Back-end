package sceneContainer_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sceneContainer_backend.domains.ResponseResult;
import sceneContainer_backend.pojo.dto.CreateServiceDataVSceneToolDTO;
import sceneContainer_backend.service.ServiceDataVSceneService;
import sceneContainer_backend.service.ServiceDataVSceneToolService;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/5/23 20:43
 */
@RestController
@RequestMapping("/serviceDataVSceneTool")
public class ServiceDataVSceneToolController {

    @Autowired
    private ServiceDataVSceneToolService serviceDataVSceneToolService;

    @PostMapping
    public ResponseResult create(CreateServiceDataVSceneToolDTO createServiceDataVSceneToolDTO) {
        return serviceDataVSceneToolService.create(createServiceDataVSceneToolDTO);
    }

    @GetMapping
    public ResponseResult getServiceDataVSceneToolList() {
        return serviceDataVSceneToolService.getServiceDataVSceneToolList();
    }

}
