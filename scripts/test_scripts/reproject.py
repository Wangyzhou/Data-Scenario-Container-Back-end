from osgeo import osr
from osgeo import ogr
# 打开 shp
driver: ogr.Driver = ogr.GetDriverByName("ESRI Shapefile")
src_ds: ogr.DataSource = driver.Open("D:/unZipFiles/js_railway.shp")
# 创建投影转换
src_layer: ogr.Layer = src_ds.GetLayer()
src_prj: osr.SpatialReference = src_layer.GetSpatialRef()
print(src_prj)
dst_prj: osr.SpatialReference = osr.SpatialReference()
# 设置投影策略
src_prj.SetAxisMappingStrategy(osr.OAMS_TRADITIONAL_GIS_ORDER)
dst_prj.SetAxisMappingStrategy(osr.OAMS_TRADITIONAL_GIS_ORDER)
dst_prj.ImportFromEPSG(4326)
ct: osr.CoordinateTransformation = osr.CoordinateTransformation(
    src_prj, dst_prj)
# 创建 shp
dst_ds: ogr.DataSource = driver.CreateDataSource('D:/unZipFiles/output/js_railway.shp')
dst_layer: ogr.Layer = dst_ds.CreateLayer(
    'test', srs=dst_prj, geom_type=ogr.wkbLineString, options=["ENCODING=UTF-8"])
# 创建字段
src_FeatureDefn: ogr.FeatureDefn = src_layer.GetLayerDefn()
for i in range(0, src_FeatureDefn.GetFieldCount()):
    dst_layer.CreateField(src_FeatureDefn.GetFieldDefn(i))
src_feature: ogr.Feature = src_layer.GetNextFeature()
dst_featureDefn: ogr.FeatureDefn = dst_layer.GetLayerDefn()
while (src_feature):
    dst_feature: ogr.Feature = ogr.Feature(dst_featureDefn)
    # 添加 geom
    geom: ogr.Geometry = src_feature.GetGeometryRef()
    geom.Transform(ct)
    dst_feature.SetGeometry(geom)
    # 添加字段
    try:
        dst_fieldNum: int = dst_feature.GetFieldCount()
        for j in range(0, dst_fieldNum):
            dst_feature.SetField(j, src_feature.GetField(j))
    except:
        print('字段添加出现异常')
    finally:
        # 不管字段添加成功与否都添加 feature
        dst_layer.CreateFeature(dst_feature)
        src_feature = src_layer.GetNextFeature()
# 释放内存
del src_ds, dst_ds


# if __name__ == '__main__':
    