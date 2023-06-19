package sceneContainer_backend.pojo;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.List;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/4/7 10:41
 */
@Data
@AllArgsConstructor
public class TileJSON {
    public class TileJson {

        //参考https://github.com/mapbox/tilejson-spec/tree/master/2.1.0
        @Id
        @ApiModelProperty(value = "id", hidden = true)
        String id = IdUtil.objectId();

        String tilejson = "2.1.0";  //遵循的tilejson的标准

        String name;  // xxx

        String description;  // xxx
        String version = "1.0.0";  // 自己的版本
        String attribution = "";
        int minzoom = 0;
        int maxzoom = 22;

        List<Double> bounds;

        List<Double> center;

        List<String> tiles;

        List<JSONObject> vector_layers;

    }
}
