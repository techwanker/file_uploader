
CREATE OR REPLACE PACKAGE BODY logger_persistence
is
     g_job_log_id            pls_integer;
     g_job_step_id           pls_integer;

 

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
    ) is
    pragma autonomous_transaction ;                
    begin  
        insert into job_log (
          job_log_id,   schema_name,      process_name,   thread_name,        
          status_msg,   status_ts,        sid,            module_name,
          classname,    tracefile_name
        ) values (
          p_job_log_id,  p_schema_name,   p_process_name,  p_thread_name, 
          p_status_msg,  systimestamp,    p_sid,           p_module_name,
          p_classname,   p_tracefile_name
        );
        commit;
   end save_job_log;
   
   function save_job_step (
        p_job_log_id  in pls_integer, 
        p_step_name   in varchar, 
        p_step_info   in varchar, 
        p_classname   in varchar,     
        p_start_ts    in timestamp,
        p_stacktrace  in varchar
   ) return number
   is 
            pragma autonomous_transaction ;             

   begin
	   insert into job_step (
        job_step_id,   job_log_id, step_name, step_info, 
        classname,     start_ts,   stacktrace
      ) values (
        job_step_id_seq.nextval, p_job_log_id, p_step_name, p_step_info, 
        p_classname,   p_start_ts,   p_stacktrace
      ) returning job_step_id into g_job_step_id;
      return g_job_step_id;
      
   end save_job_step;
   
    procedure finish_step is 
        pragma autonomous_transaction ;   
    begin
       update job_step 
       set end_ts = systimestamp
       where job_step_id = g_job_step_id;
    end finish_step;


     procedure persist_job_log ( 
    						 p_job_log_id   in number,
    						 p_schema_name  in varchar,
    						 p_process_name in varchar,
    						 
    						 p_thread_name  in varchar,
    						 p_status_msg   in varchar,
    						 p_status_ts    in timestamp,
    						 
    						 p_sid          in pls_integer,
                             p_module_name  in varchar,
                             p_classname    in varchar, 
                             
                             p_tracefile_name in varchar) 
                     --      return varchar
   is
      PRAGMA AUTONOMOUS_TRANSACTION;
   begin
	   insert into job_log (    
	        job_log_id,     schema_name,    process_name, 
	        thread_name,    status_msg,     status_ts,
        	sid,            module_name,    classname,
        	tracefile_name,
       ) values (
	        p_job_log_id,   p_schema_name,  p_process_name, 
	        p_thread_name,  p_status_msg,   p_status_ts,    
	        p_sid,          p_module_name,  p_classname,
	        p_tracefile_name
	   );
	   g_job_log_id := p_job_log_id;
   end persist_job_log;

   procedure end_job(p_elapsed_milliseconds in pls_integer)
   --::* update job_log.status_id to 'C' and status_msg to 'DONE'
   --::>
   is
       PRAGMA AUTONOMOUS_TRANSACTION;

   begin


       update job_log
       set
              SID = NULL,
              status_msg = 'DONE',
              status_ts = SYSDATE,
              elapsed_millis = p_elapsed_milliseconds
       where job_log_id = g_job_log_id;

      commit;
   --   logger.set_action('end_job complete');
   end end_job;
   
   --::<
   procedure abort_job(p_elapsed_milliseconds in pls_integer,p_stacktrace in varchar)
   --::* procedure abort_job
   --::* update job_log
   --::* elapsed_time
   --::* status_id = 'I'
   --::* status_msg = 'ABORT'
   --::>
   is
      PRAGMA AUTONOMOUS_TRANSACTION;

   begin
      update job_log
      set SID = null,
          status_msg = 'ABORT',
          status_ts = current_timestamp,
          abort_stacktrace = p_stacktrace,
          elapsed_millis = p_elapsed_milliseconds
       where job_log_id = g_job_log_id;

      COMMIT;
      set_action('abort_job complete');
   end abort_job;



end logger_persistence;
/
--#<
show errors
--#>
