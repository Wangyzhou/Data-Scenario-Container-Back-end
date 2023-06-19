import configparser
import os
import saga
cf = configparser.ConfigParser() # 实例化
cf.read(r'config.ini',encoding='utf-8')
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
def Run_Merge_Layers(input_path_list, output_path, src_info, field_match, src_delete):
    # Get the tool:
    Tool = saga_api.SG_Get_Tool_Library_Manager().Get_Tool('shapes_tools', '2')
    if not Tool:
        print('Failed to request tool: Merge Layers')
        return False

    # Set the parameter interface:
    Tool.Reset()
    for input_file_path in input_path_list:
        Tool.Get_Parameter('INPUT').asList().Add_Item(saga_api.SG_Get_Data_Manager().Add(input_file_path))
    Tool.Set_Parameter('SRCINFO', src_info)
    Tool.Set_Parameter('MATCH', field_match)
    Tool.Set_Parameter('DELETE', src_delete)

    # Execute the tool:
    if not Tool.Execute():
        print('failed to execute tool: ' + Tool.Get_Name().c_str())
        return False

    # Request the results:
    Data = Tool.Get_Parameter('MERGED').asShapes()
    Data.Save('{:s}/{:s}'.format(output_path, str(Data.Get_Name()).replace(' ','_')))

    # job is done, free memory resources:
    saga_api.SG_Get_Data_Manager().Delete_All()

    return True


#_________________________________________
##########################################
if __name__ == '__main__':
    import sys
    # For a single file based input it might look like following:
    input_path_list = list(map(str, sys.argv[1].split(',')))
    output_path = sys.argv[2]
    src_info = False if(sys.argv[3] == 'false') else True 
    field_match = False if(sys.argv[4] == 'false') else True 
    src_delete = False if(sys.argv[5] == 'false') else True 
    Run_Merge_Layers(input_path_list, output_path, src_info, field_match, src_delete)
