set echo on 
@prepare_connection.sql
@my_session_info.sql
@dblogger_uninstall.sr.sql
@logger_message_formatter.plsql.sr.sql
@dblogger_install_tables.sr.sql
@dblogger_install.pks.sr.sql
@dblogger_install.pkb.sr.sql
@logger_persistence.pks.sr.sql
@logger_persistence.pkb.sr.sql
