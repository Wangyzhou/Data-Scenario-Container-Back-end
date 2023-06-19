package sceneContainer_backend;

import cn.hutool.core.util.IdUtil;
import org.bson.types.Binary;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sceneContainer_backend.pojo.GeoDataFile;
import sceneContainer_backend.pojo.PostgresPOJO.TestShp;
import sceneContainer_backend.repository.GeoDataFileRepository;
import sceneContainer_backend.repository.MogoDBRepository.CatalogRepository;
import sceneContainer_backend.repository.PostgresRepository.TestShpDao;
import com.mongodb.BasicDBObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sceneContainer_backend.utils.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

@SpringBootTest
class SpringSecurityApplicationTests {

    @Autowired
    private GeoDataFileRepository geoDataFileRepository;

    @Autowired
    private CatalogRepository catalogRepository;
    @Autowired
    private TestShpDao testShpDao;

    @Value("${resourcesPath}")
    private String resourcesRoot;

    @Test
    void contextLoads() {
        TestShp testShp = new TestShp();
        testShp.setId("1241413");
        testShp.setName("shp");
        testShp.setSize(123233);
        testShp.setDate(new Date());
        List<BasicDBObject> list = new ArrayList<>();
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("column_name", "gid");
        basicDBObject.put("data_type", "integer");
        list.add(basicDBObject);
        BasicDBObject basicDBObject1 = new BasicDBObject();
        basicDBObject1.put("column_name", "name");
        basicDBObject1.put("data_type", "character");
        list.add(basicDBObject1);
        System.out.println(list);
        testShp.setAttrInfo(list);
        testShpDao.save(testShp);
    }

    @Test
    void testFindOneByMd5AndUserId() {
//        HashMap<String, String> namelist = new HashMap<>();
//        namelist.put("1213113", "js");
//
//        GeoDataFile geoDataFile = new GeoDataFile("1212", "aweqwre", namelist, "121", "js_1231313", "js", 12123, "/temp/js.zip", 0, new Date());
//        geoDataFileRepository.insert(geoDataFile);
        GeoDataFile aweqwre = geoDataFileRepository.findOneByMd5AndUserId("aweqwre", "122");
        System.out.println("aweqwre = " + aweqwre);
    }

    @Test
    void testFindAncestorCatalog() {
        //"7d08bb53-ab52-46fa-afe0-f6d43473f44c"
        String catalogId = "7d08bb53-ab52-46fa-afe0-f6d43473f44c";
        String userId = "a7d81426-1e5a-4a49-b8e7-1288e2bf961f";
        String parentId = "";
        String catalogPath = "";
        LinkedList<String> catalogIdList = new LinkedList<>();
        catalogIdList.addFirst(catalogId);
        while ((parentId = catalogRepository.getCatalogById(catalogId).getParentId()).compareTo("-1") != 0) {
            catalogId = parentId;
            catalogIdList.addFirst(catalogId);
        }
        Iterator<String> it = catalogIdList.iterator();
        while (it.hasNext()) {
            catalogPath = catalogPath + "/" + it.next();
        }
        System.out.println("catalogPath = " + catalogPath);
        String path = resourcesRoot + "/" + userId + catalogPath;
        System.out.println("path = " + path);
        File geoFile = new File(path);
        geoFile.mkdirs();
    }

    @Test
    void testDeleteFile() {
        String filePath1 = "E:/DataSecenContainerResources/a7d81426-1e5a-4a49-b8e7-1288e2bf961f/70024b5c-346f-4771-bcfd-06b060e8204c/abddae29-c302-4e3a-aa77-f60a387e9c30/7d08bb53-ab52-46fa-afe0-f6d43473f44c/ha.txt";
        String filePath2 = "E:/DataSecenContainerResources/a7d81426-1e5a-4a49-b8e7-1288e2bf961f/70024b5c-346f-4771-bcfd-06b060e8204c/abddae29-c302-4e3a-aa77-f60a387e9c30/7d08bb53-ab52-46fa-afe0-f6d43473f44c/hi.txt";
        String filePath3 = "E:/DataSecenContainerResources/a7d81426-1e5a-4a49-b8e7-1288e2bf961f/70024b5c-346f-4771-bcfd-06b060e8204c/abddae29-c302-4e3a-aa77-f60a387e9c30/7d08bb53-ab52-46fa-afe0-f6d43473f44c/hu.txt";
        ArrayList<String> filePathList = new ArrayList<>();
        filePathList.add(filePath1);
        filePathList.add(filePath2);
        filePathList.add(filePath3);
        filePathList.forEach(file -> FileUtils.deleteFile(file));
    }

    @Test
    void testTbName() {
        String originName = "js_rigon.zip";
        String filePrefix = ".shp";
        String dataType = filePrefix.substring(1);
        String tableName = originName.substring(0, originName.lastIndexOf(".")) + IdUtil.objectId();
        System.out.println("dataType = " + dataType);

    }

    @Test
    void testGeoFileDAO() {
        GeoDataFile oneById = geoDataFileRepository.findOneById("640c499a5dce98d89699edd4");
        System.out.println("oneById = " + oneById);
    }

    @Test
    void testListFiles() {
        String catalogPath = "E:\\DataSecenContainerResources\\a270426e-fc90-4e19-932a-839d5274e9e9\\3085e2e2-5381-4376-8035-3f86e9934668\\80cffc2c-8efa-4dad-8711-4cd2ca1d9f30";
        File folder = new File(catalogPath);
        String name1 = folder.getName();
        File[] files = folder.listFiles((dir, name) -> name.contains("js_railway_buffer6445eab15dce250570ab5368"));
        ArrayList<File> fileArrayList = new ArrayList<>();
        for (File file : files) {
            fileArrayList.add(file);
            System.out.println(file.getAbsolutePath());
        }
        FileUtils.toZip(fileArrayList, catalogPath + "/" + "js_railway_buffer6445eab15dce250570ab5368.zip");
        //删除文件
        fileArrayList.forEach(file -> FileUtils.deleteFile(file.getAbsolutePath()));
        //调用导入资源接口
        System.out.println("files.length = " + files.length);
    }

//    @Test
//    @PostMapping("/test/testBinary")
//    void testBinaryData(@RequestParam("data") Binary testData) {
//        System.out.println("testData = " + testData);
//    }
}
