package sceneContainer_backend.pojo.dto;

import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Auther wyjq
 * @Date 2022/5/29
 **/

@Data
public class TileVectorLayersDTO {
    String id;
    List<String> field;
    int minzoom;
    int maxzoom;
    List<Double> bounds;
//    List<JSONObject> vector_layers; //[{id:"",field:[],min:0,max:22,bounds:[-180,85,180,85]}]
}
