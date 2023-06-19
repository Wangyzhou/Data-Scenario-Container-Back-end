package sceneContainer_backend.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Service;
import sceneContainer_backend.domains.ResponseResult;
import sceneContainer_backend.pojo.ServiceDataVSceneTool;
import sceneContainer_backend.pojo.dto.CreateServiceDataVSceneToolDTO;
import sceneContainer_backend.repository.ServiceDataVSceneToolRepository;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/5/23 20:36
 */
@Service
@Slf4j
public class ServiceDataVSceneToolService {

    @Autowired
    ServiceDataVSceneToolRepository serviceDataVSceneToolRepository;

    public ResponseResult create(CreateServiceDataVSceneToolDTO createServiceDataVSceneToolDTO) {
        ServiceDataVSceneTool serviceDataVSceneTool = new ServiceDataVSceneTool();
        serviceDataVSceneTool.setId(UUID.randomUUID().toString());
        serviceDataVSceneTool.setName(createServiceDataVSceneToolDTO.getName());
        serviceDataVSceneTool.setLabel(createServiceDataVSceneToolDTO.getLabel());
        serviceDataVSceneTool.setDescription(createServiceDataVSceneToolDTO.getDescription());
        try {
            serviceDataVSceneTool.setToolImg(new Binary(createServiceDataVSceneToolDTO.getToolImg().getBytes()));
            serviceDataVSceneToolRepository.save(serviceDataVSceneTool);
            return new ResponseResult(200,"创建工具成功！");
        } catch (IOException e) {
            return new ResponseResult(201,"创建工具失败！");
        }
    }

    public ResponseResult getServiceDataVSceneToolList() {
        List<ServiceDataVSceneTool> toolList = serviceDataVSceneToolRepository.findAll();
        return new ResponseResult(200,"获取成功！",toolList);

    }
}
