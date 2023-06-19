package sceneContainer_backend.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/3/9 19:54
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChildrenDataDTO {
    private String id;
    private String name;
    private String fileType;
    private String dataType;
    private Date date;
}
