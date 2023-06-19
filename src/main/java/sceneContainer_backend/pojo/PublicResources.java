package sceneContainer_backend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/3/9 15:35
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicResources {

    @Id
    private String id;
    private String name;
    private String ptName;
    private String title;
    private String downloadNum;
    private Data date;
    private String size;
    private Binary pic;

}
