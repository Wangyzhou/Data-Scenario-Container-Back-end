package sceneContainer_backend.pojo.PostgresPOJO;

import com.mongodb.BasicDBObject;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/3/10 16:09
 */

@Data
@NoArgsConstructor
public class TestShp {

    @Id
    private String id;
    private String name;
    private int size;
    private Date Date;

    private List<BasicDBObject> attrInfo;
    private List<Double> bounds;
}
