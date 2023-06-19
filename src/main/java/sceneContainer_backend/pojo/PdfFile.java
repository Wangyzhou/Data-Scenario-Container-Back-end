package sceneContainer_backend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.Map;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/4/12 20:56
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PdfFile {
    @Id
    private String id;
    private String md5;
    private Map<String, String> nameList;
    private String userId;
    private String originalName;
    private String displayName;
    private int size;
    private String path;        //存储路径
    private int downloadNum;      // 下载次数
    private Date date;
}
