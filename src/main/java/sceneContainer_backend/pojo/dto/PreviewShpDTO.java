package sceneContainer_backend.pojo.dto;

import io.swagger.models.auth.In;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/4/7 14:01
 */

@Data
public class PreviewShpDTO {
    private String id;
    private String name;
    private String type;
    private Integer srid;
    private String code;
    private String date;
    private Integer size;
    private Integer downloadNum;
    private String description;
    private List<Double> bbox;
    private String ptName;
}
