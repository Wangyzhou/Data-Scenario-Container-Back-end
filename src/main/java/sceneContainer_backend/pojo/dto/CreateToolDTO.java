package sceneContainer_backend.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/4/20 21:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateToolDTO {
    private String name;
    private String type;
    private String path;
    private String description;
}
