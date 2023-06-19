package sceneContainer_backend.pojo;

import sceneContainer_backend.pojo.dto.ChildrenDataDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/3/9 19:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catalog {

    @Id
    String id;
    String name;
    String parentId;
    List<ChildrenDataDTO> children;    // 子文件和子文件夹
    int total;      // 文件数
    String userId;
    int level;
    Date date;
}
