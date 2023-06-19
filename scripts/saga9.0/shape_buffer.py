import configparser
import os
import saga
cf = configparser.ConfigParser() # 实例化
cf.read(r'config.ini',encoding='utf-8')
sec = cf.sections()
saga_path = 'D:/majorSoftware/saga-9.0.0_x64/'
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
def Run_Shapes_Buffer(input_path,output_path,buffer_field,buffer_default_distance,buffer_intesect_desolve,buffer_distance_scale,buffer_nzones,buffer_darc):
    # Get the tool:
    Tool = saga_api.SG_Get_Tool_Library_Manager().Get_Tool('shapes_tools', '18')
    if not Tool:
        print('Failed to request tool: Shapes Buffer')
        return False

    # Set the parameter interface:
    Tool.Reset()
    Tool.Set_Parameter('SHAPES', saga_api.SG_Get_Data_Manager().Add(input_path))
    Tool.Set_Parameter('DIST_FIELD', buffer_field)
    Tool.Set_Parameter('DIST_FIELD_DEFAULT', buffer_default_distance)
    Tool.Set_Parameter('DIST_SCALE', buffer_distance_scale)
    Tool.Set_Parameter('DISSOLVE', buffer_intesect_desolve)
    Tool.Set_Parameter('NZONES', buffer_nzones)
    Tool.Set_Parameter('POLY_INNER', False)
    Tool.Set_Parameter('DARC', buffer_darc)

    # Execute the tool:
    if not Tool.Execute():
        print('failed to execute tool: ' + Tool.Get_Name().c_str())
        return False

    # Request the results:
    Data = Tool.Get_Parameter('BUFFER').asShapes()
    Data.Save('{:s}'.format(output_path))
    # job is done, free memory resources:
    saga_api.SG_Get_Data_Manager().Delete_All()

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
