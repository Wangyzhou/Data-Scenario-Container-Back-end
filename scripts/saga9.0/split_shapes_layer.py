import configparser
import os
import saga
cf = configparser.ConfigParser() # 实例化
cf.read(r'./config.ini',encoding='utf-8')
sec = cf.sections()
saga_path = cf.get("saga", "path")
#! /usr/bin/env python

#_________________________________________
##########################################
# Initialize the environment...

# Windows: Let the 'SAGA_PATH' environment variable point to
# the SAGA installation folder before importing 'saga'
# or alternatively set it in 'saga.py' itself.
os.environ['SAGA_PATH'] = saga_path
# Call 'Initialize()' to load SAGA's standard tool libraries!
saga.Initialize(True)
# Import 'saga' before importing 'saga_api' for the first time!
import saga_api

#_________________________________________
##########################################
def Run_Split_Shapes_Layer(input_path,output_path,is_create_extent,nx,ny,split_method):
    # Get the tool:
    Tool = saga_api.SG_Get_Tool_Library_Manager().Get_Tool('shapes_tools', '15')
    if not Tool:
        print('Failed to request tool: Split Shapes Layer')
        return False

    # Set the parameter interface:
    Tool.Reset()
    Tool.Set_Parameter('SHAPES', saga_api.SG_Get_Data_Manager().Add(input_path))
    if is_create_extent:
      Tool.Set_Parameter('EXTENT', saga_api.SG_Get_Create_Pointer()) # optional output, remove this line, if you don't want to create it
    Tool.Set_Parameter('NX', nx)
    Tool.Set_Parameter('NY', ny)
    Tool.Set_Parameter('METHOD', split_method) # 'intersects'

    # Execute the tool:
    if not Tool.Execute():
        print('failed to execute tool: ' + Tool.Get_Name().c_str())
        return False

    # Request the results:
    List = Tool.Get_Parameter('CUTS').asShapesList()
    print(List)
    for i in range(0, List.Get_Item_Count()):
        List.Get_Shapes(i).Save('{:s}/{:s}_{:d}'.format(output_path,input_path[input_path.rfind('/') + 1: input_path.rfind('.shp')] + '_' + str(List.Get_Name()).replace(' ','_'), i))

    Data = Tool.Get_Parameter('EXTENT').asShapes()
    Data.Save('{:s}/{:s}'.format(output_path, input_path[input_path.rfind('/') + 1: input_path.rfind('.shp')] + '_' + str(Data.Get_Name()).replace(' ','_').replace('[', '').replace(']','')))

    # job is done, free memory resources:
    saga_api.SG_Get_Data_Manager().Delete_All()

    return True


#_________________________________________
##########################################
if __name__ == '__main__':
    import sys
    input_path = sys.argv[1]
    output_path = sys.argv[2]
    is_create_extent = False if sys.argv[3] == 'false' else True
    nx = int(sys.argv[4])
    ny = int(sys.argv[5])
    split_method = sys.argv[6]
    Run_Split_Shapes_Layer(input_path,output_path,is_create_extent,nx,ny,split_method)
