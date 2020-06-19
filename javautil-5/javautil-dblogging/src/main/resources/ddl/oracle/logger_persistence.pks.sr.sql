
CREATE OR REPLACE PACKAGE logger_persistence
is
--   g_job_msg_dir    varchar2 (32) := 'UT_PROCESS_LOG_DIR';
--   g_filter_level          pls_integer := G_INFO ;
--   g_record_level          pls_integer := G_INFO ;
--   g_file_handle           UTL_FILE.file_type;
--   g_log_file_name         varchar2 (255);
--   g_last_log_seq_nbr      pls_integer;
--   g_dbms_output_level     pls_integer        := 5;
--   g_process_start_tm      timestamp;
--   g_process_end_tm        timestamp;
--   g_process_name          varchar2 (128);
--   g_process_status_id     pls_integer;
---- set by get_caller
--   g_owner_name            varchar2 (100);
--   g_caller_name           varchar2 (100);
--   g_line_number           pls_integer;
--   g_caller_type           varchar2 (100);
----
--   g_sid                   pls_integer;
--   g_current_schema        varchar2 (32);
--   g_current_user          varchar2 (32);
--   g_session_user          varchar2 (32);
--   g_proxy_user            varchar2 (32);
--   g_who_called_me_level   BINARY_integer     := 6;
--   g_job_log_id            pls_integer;

 

    procedure save_job_log (
        p_job_log_id   in number,
   		p_schema_name  in varchar2,
    	p_process_name in varchar2,
        p_classname    in varchar2,
        p_module_name  in varchar2,
        p_status_msg   in varchar2,
        p_thread_name  in varchar2,
        p_trace_level  in pls_integer default logger.G_INFO,
        p_tracefile_name in varchar2,
        p_sid          in pls_integer
    );
   
   function save_job_step (
        p_job_log_id  in pls_integer, 
        p_step_name   in varchar, 
        p_step_info   in varchar, 
        p_classname   in varchar,     
        p_start_ts    in timestamp,
        p_stacktrace  in varchar
   ) return number;
   
   
    procedure finish_step; 


   
procedure persist_job_log ( 
    						 p_job_log_id   in number,
    						 p_schema_name  in varchar,
    						 p_process_name in varchar,
    						 p_thread_name  in varchar,
    						 p_status_msg   in varchar,
    						 p_status_ts    in timestamp,
    						 p_sid          in pls_integer,
                             p_module_name  in varchar,
                             p_classname    in varchar);
                    --         return varchar;

      procedure end_job(p_elapsed_milliseconds in pls_integer);

   procedure abort_job(p_elapsed_milliseconds in pls_integer,p_stacktrace in varchar);
   
end logger_persistence;
/
--#<
show errors
--#>
