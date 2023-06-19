# -*- coding: utf-8 -*-
"""
Created on Wed Apr 19 16:07:03 2023

@author: Administrator
"""

#! /usr/bin/env python

#_________________________________________
##########################################
# Initialize the environment...

# Windows: set/adjust the 'SAGA_PATH' environment variable before importing saga_helper
import os
import sys
if os.name == 'nt' and os.getenv('SAGA_PATH') is None:
    os.environ['SAGA_PATH'] = 'D:/majorSoftware/saga-8.5.0_x64/'
import saga_helper, saga_api

saga_helper.Initialize(True)


#_________________________________________
#########################################
def Run_Merge_Layers(input_path_list, output_path, src_info, field_match, src_delete):
    Tool = saga_api.SG_Get_Tool_Library_Manager().Get_Tool('shapes_tools', '2')
    if Tool == None:
        print('Failed to request tool: Merge Layers')
        return False
    Tool.Reset()
    
    # Tool.Set_Parameter('INPUT',input_path_list)
    # Tool.Get_Parameter('INPUT').asList().Add_Item(saga_api.SG_Get_Data_Manager().Add('D:/unZipFiles/output/js_city_point_u [Buffer].shp'))
    # Tool.Get_Parameter('INPUT').asList().Add_Item(saga_api.SG_Get_Data_Manager().Add('D:/unZipFiles/output/js_city_region_u [Buffer].shp'))
    for input_file_path in input_path_list:
        Tool.Get_Parameter('INPUT').asList().Add_Item(saga_api.SG_Get_Data_Manager().Add(input_file_path))
    # .Add_Item('Shapes input list')
    Tool.Set_Parameter('SRCINFO', src_info)
    Tool.Set_Parameter('MATCH', field_match)
    Tool.Set_Parameter('DELETE', src_delete)

    if Tool.Execute() == False:
        print('failed to execute tool: ' + Tool.Get_Name().c_str())
        return False

    #_____________________________________
    # Save results to file:
    Path = os.path.split(output_path)[0] + os.sep
    # print(Path)
    Data = Tool.Get_Parameter('MERGED').asDataObject()
    Data.Save(Path + Data.Get_Name())

    #_____________________________________
    saga_api.SG_Get_Data_Manager().Delete_All() # job is done, free memory resources

    return True


#_________________________________________
##########################################
if __name__ == '__main__':
    print('This is a simple template for using a SAGA tool through Python.')
    print('Please edit the script to make it work properly before using it!')

    # For a single file based input it might look like following:
    input_path_list = list(map(str, sys.argv[1].split(',')))
    output_path = sys.argv[2]
    src_info = False if(sys.argv[3] == 'false') else True 
    field_match = False if(sys.argv[4] == 'false') else True 
    src_delete = False if(sys.argv[5] == 'false') else True 
    Run_Merge_Layers(input_path_list, output_path, src_info, field_match, src_delete)
