package sceneContainer_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sceneContainer_backend.domains.ResponseResult;
import sceneContainer_backend.pojo.dto.CreateToolDTO;
import sceneContainer_backend.pojo.dto.InvokeToolDTO;
import sceneContainer_backend.service.ToolService;

import java.lang.reflect.InvocationTargetException;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/4/20 21:06
 */
@RestController
@RequestMapping("/tool")
public class ToolController {

    @Autowired
    private ToolService toolService;
    @PostMapping
    public ResponseResult createTool(@RequestBody CreateToolDTO createToolDTO) {
        return toolService.createTool(createToolDTO);
    }
    @GetMapping("/getToolList")
    public ResponseResult getToolList(@RequestParam String type) {
        return toolService.getToolList(type);
    }

    @PostMapping("/invoke")
    public ResponseResult invoke(@RequestBody InvokeToolDTO invokeToolDTO) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        return toolService.invokeTool(invokeToolDTO);
    }
}
