# -*- coding: utf-8 -*-
"""
Created on Tue Feb  7 21:46:28 2023

@author: Administrator
"""

#! /usr/bin/env python

#_________________________________________
##########################################
# Initialize the environment...

# Windows: set/adjust the 'SAGA_PATH' environment variable before importing saga_helper
import os
# import warnings
# warnings.filterwarnings('ignore')
# if os.name == 'nt' and os.getenv('SAGA_PATH') is None:
#     os.environ['SAGA_PATH'] = 'D:/majorSoftware/saga-8.5.0_x64/'
import saga_helper, saga_api

saga_helper.Initialize(True)
saga_api.SG_UI_Msg_Lock(True)

#_________________________________________
##########################################
def Run_Shapes_Buffer(input_path,output_path,buffer_field,buffer_default_distance,buffer_intesect_desolve,buffer_distance_scale,buffer_nzones,buffer_darc):
    Tool = saga_api.SG_Get_Tool_Library_Manager().Get_Tool('shapes_tools', '18')
    if Tool == None:
        print('Failed to request tool: Shapes Buffer')
        return False
    Tool.Reset()

    fullFilePath = 'D:/unZipFiles/js_city_point_u.shp'
    # fullFilePath = saga_api.CSG_String(fullFilePath)
    #fullFilePath = saga_api.CSG_String.to_StdString(fullFilePath)
    # Tool.Set_Parameter('SHAPES', saga_api.SG_Get_Data_Manager().Add(fullFilePath.w_str()))
    # Tool.Get_Parameter('SHAPES').asSHAPE()
    Tool.Get_Parameter('SHAPES').asShapes().Add_Item(saga_api.SG_Get_Data_Manager().Add(fullFilePath))
    Tool.Set_Parameter('DIST_FIELD', buffer_field)
    Tool.Set_Parameter('DIST_FIELD_DEFAULT', buffer_default_distance)
    Tool.Set_Parameter('DISSOLVE', buffer_intesect_desolve)
    Tool.Set_Parameter('DIST_SCALE', buffer_distance_scale)
    Tool.Set_Parameter('NZONES', buffer_nzones)
    Tool.Set_Parameter('DARC', buffer_darc)

    if Tool.Execute() == False:
        print('failed to execute tool: ' + Tool.Get_Name().c_str())
        return False

    #_____________________________________
    # Save results to file:
    Path = os.path.split(output_path)[0] + os.sep

    Data = Tool.Get_Parameter('BUFFER').asDataObject()
    Data.Save(Path + Data.Get_Name())

    #_____________________________________
    saga_api.SG_Get_Data_Manager().Delete_All() # job is done, free memory resources

    return True


#_________________________________________
##########################################
if __name__ == '__main__':
    import sys
    input_path = sys.argv[1]
    output_path = sys.argv[2]
    buffer_field = '<not set>' if sys.argv[3] == 'null' else sys.argv[3]
    buffer_default_distance = float(sys.argv[4])
    buffer_intesect_desolve = False if sys.argv[5] == 'false' else True
    buffer_distance_scale = float(sys.argv[6])
    buffer_nzones = int(sys.argv[7])
    buffer_darc = int(sys.argv[8])
    Run_Shapes_Buffer(input_path,output_path,buffer_field,buffer_default_distance,buffer_intesect_desolve,buffer_distance_scale,buffer_nzones,buffer_darc)
