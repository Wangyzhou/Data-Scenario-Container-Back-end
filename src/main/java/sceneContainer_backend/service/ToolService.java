package sceneContainer_backend.service;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import sceneContainer_backend.domains.ResponseResult;
import sceneContainer_backend.pojo.Catalog;
import sceneContainer_backend.pojo.GeoDataFile;
import sceneContainer_backend.pojo.MapSceneLayer;
import sceneContainer_backend.pojo.Tool;
import sceneContainer_backend.pojo.dto.CreateToolDTO;
import sceneContainer_backend.pojo.dto.InvokeToolDTO;
import sceneContainer_backend.pojo.dto.UploadShapefileDTO;
import sceneContainer_backend.repository.GeoDataFileRepository;
import sceneContainer_backend.repository.MogoDBRepository.CatalogRepository;
import sceneContainer_backend.repository.ToolRepository;
import sceneContainer_backend.service.MongoDBService.CatalogService;
import sceneContainer_backend.utils.ApplicationContextHelper;
import sceneContainer_backend.utils.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/4/20 21:14
 */
@Service
@Slf4j
public class ToolService {

    @Autowired
    private CatalogService catalogService;
    @Autowired
    private ToolRepository toolRepository;

    @Autowired
    private MapSceneService mapSceneService;
    @Autowired
    private GeoDataFileService geoDataFileService;
    @Autowired
    private GeoDataFileRepository geoDataFileRepository;

    @Autowired
    private CatalogRepository catalogRepository;
    @Value("${activatePythonEnv}")
    private String activateEnvCmd;

    @Value("${invokePythonScript}")
    private String invokePythonStr;

    @Value("${backendIP}")
    private String backendIP;

    @Value("${server.port}")
    private String port;

    public ResponseResult createTool(CreateToolDTO createToolDTO) {
        Tool tool = new Tool();
        tool.setId(UUID.randomUUID().toString());
        tool.setName(createToolDTO.getName());
        tool.setType(createToolDTO.getType());
        tool.setPath(createToolDTO.getPath());
        tool.setDescription(createToolDTO.getDescription());
        toolRepository.insert(tool);
        return new ResponseResult(200, "成功创建工具");
    }

    public ResponseResult getToolList(String type) {
        List<Tool> toolList = toolRepository.findAllByType(type);
        return new ResponseResult(200, "获取工具列表成功", toolList);
    }

    public ResponseResult invokeTool(InvokeToolDTO invokeToolDTO) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        String toolName = invokeToolDTO.getToolName();
        Class clazz = Class.forName("sceneContainer_backend.service.ToolService");
        Object obj = ApplicationContextHelper.popBean(clazz);
        Class<?>[] parameterTypes = {String.class, Map.class};
        Method method = clazz.getDeclaredMethod(toolName, parameterTypes);
        //设置参数
        Object[] methodArgs = {invokeToolDTO.getToolId(), invokeToolDTO.getToolConfig()};
        ResponseResult result = (ResponseResult) method.invoke(obj, methodArgs);
        if (result.getCode() == 200) {
            String shpId = result.getData().toString();
            GeoDataFile geoFile = geoDataFileRepository.findOneById(shpId);
            MapSceneLayer mapSceneLayer = new MapSceneLayer();
            String layerVisualType = mapSceneService.getLayerVisualType(geoFile.getDataType());
            HashMap<String, Object> layout = new HashMap<>();
            layout.put("visibility", "visible");
            mapSceneLayer.setLayout(layout);
            mapSceneLayer.setId(shpId);
            mapSceneLayer.setLayerName(geoFile.getDisplayName());
            mapSceneLayer.setType(layerVisualType);
            String ptName = geoFile.getPtName();
            mapSceneLayer.setSourceLayer(ptName);
            mapSceneLayer.setDataType(geoFile.getDataType());
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
            return new ResponseResult(200, toolName + "工具执行成功！", JSON.toJSON(mapSceneLayer));
        }
        return new ResponseResult(201, "工具执行失败！");
    }

    public ResponseResult shape_buffer(String toolId, Map<String, Object> toolConfig) {
        Tool tool = toolRepository.findToolById(toolId);
        String projectPath = System.getProperty("user.dir");
        String relPath = tool.getPath();
        String totalTollPath = projectPath + relPath;
        System.out.println("totalTollPath = " + totalTollPath);
        GeoDataFile geoDataFile = geoDataFileRepository.findOneById(toolConfig.get("layerId").toString());
        String path = geoDataFile.getPath();
        String shapePath = path.replace(".zip", ".shp");
        String outputName = geoDataFile.getOriginalName().replace(".zip", "") + "_buffer";
        String outputCatalog = catalogService.getCatalogFileSystemListByCatalogId(toolConfig.get("outputPath").toString());
        String outputPath = outputCatalog + "/" + outputName;
//        String outputShpPath = outputPath + ".shp";
//        System.out.println("outputPath = " + outputPath);
        String[] toolParamsStrArr = {shapePath, outputPath, toolConfig.get("field").toString(), toolConfig.get("distance").toString(), toolConfig.get("disolved").toString(), toolConfig.get("scale").toString(), toolConfig.get("zonesNum").toString(), toolConfig.get("arcVertexDistance").toString()};
        String toolParamsStr = String.join(" ", toolParamsStrArr);
        System.out.println("toolParamsStr = " + toolParamsStr);
        String pythonScript = MessageFormat.format(invokePythonStr, activateEnvCmd, totalTollPath, toolParamsStr);
        System.out.println("pythonScript = " + pythonScript);
        Process pro = null;
        try {
            pro = Runtime.getRuntime().exec(pythonScript);
            String line;
//
            BufferedReader buf = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            int okayCount = 0;
            while ((line = buf.readLine()) != null) {
                log.info("-----------out:" + line);
                if (line.contains("okay")) {
                    okayCount++;
                }
            }

            if (okayCount >= 2) {
                log.info("invoke tool: " + toolId);
                log.info("importResource...");
                ResponseResult saveResult = saveResult(outputCatalog, outputName, toolConfig.get("outputPath").toString());
                if (saveResult.getCode() == 200) {
                    return new ResponseResult(200, "success", saveResult.getData().toString());
                }
            }
            log.error("invoke tool error!!!");
            return new ResponseResult(201, "fail");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ResponseResult saveResult(String catalogPath, String fileName, String catalogId) {
        File folder = new File(catalogPath);
        File[] files = folder.listFiles((dir, name) -> name.contains(fileName));
        ArrayList<File> fileArrayList = new ArrayList<>();
        for (File file : files) {
            fileArrayList.add(file);
        }
        //压缩为zip
        FileUtils.toZip(fileArrayList, catalogPath + "/" + fileName + ".zip");
        //删除源文件集
        fileArrayList.forEach(file -> FileUtils.deleteFile(file.getAbsolutePath()));
        Catalog catalog = catalogRepository.getCatalogById(catalogId);
        ResponseResult responseResult = geoDataFileService.importSharedGeoResource(catalogId, catalogPath + "/" + fileName + ".zip", catalog.getUserId());
        String shpId = responseResult.getData().toString();

        //调用导入资源接口
        if (responseResult.getCode() == 200) {
            return new ResponseResult(200, "success", shpId);
        }
        return new ResponseResult(201, "fail");
    }
}
