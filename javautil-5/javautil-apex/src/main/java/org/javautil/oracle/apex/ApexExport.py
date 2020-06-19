 class ApexExporter:
    
    applicationSelect =  """select application_id, application_name, workspace " 
            "from apex_applications "
            "where workspace like upper(:workspace_name) and workspace != 'INTERNAL'"""
            
            