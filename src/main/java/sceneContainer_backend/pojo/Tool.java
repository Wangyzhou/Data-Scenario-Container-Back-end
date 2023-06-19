package sceneContainer_backend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/4/20 21:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tool {
    @Id
    private String id;
    private String name;
    private String label;
    private String type;
    private String path;
    private String description;
}
