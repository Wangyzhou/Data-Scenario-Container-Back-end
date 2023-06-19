//package sceneContainer_backend.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import sceneContainer_backend.domains.ResponseResult;
//import sceneContainer_backend.pojo.dto.CreateMapSceneDTO;
//import sceneContainer_backend.service.MapSceneService;
//
///**
// * @description:
// * @author: yzwang
// * @time: 2023/4/17 21:11
// */
//@RestController
//@RequestMapping("/scene")
//public class MapSceneController {
//
//    @Autowired
//    private MapSceneService mapSceneService;
//    @PostMapping
//    public ResponseResult create(@RequestBody CreateMapSceneDTO createMapSceneDTO) {
//        System.out.println("createMapSceneDTO = " + createMapSceneDTO);
//
//        return mapSceneService.createMapSceneConfig(createMapSceneDTO);
//    }
//}
