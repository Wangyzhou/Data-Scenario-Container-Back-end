package sceneContainer_backend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/4/11 16:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Image {
    private String id;
    private String md5;
    private String originName;  //去后缀的名字
    private int size;
    private String userId;
    private String path;        //存储路径
    private int downloadNum;      // 下载次数
    private Date date;
    private String dataType;

    /**
     * @description:
     * @author: yzwang
     * @time: 2023/5/24 14:37
     */
    @Data
    public static class ServiceDataVSceneTool {
        private String id;
        private String name;
        private String label;
        private String description;
    }
}
