package org.javautil.dex.renderer;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

import org.javautil.jdbc.BindVariableValue;

public interface CrossTabWriter {

    public abstract void addBindVariableValues(Collection<BindVariableValue> values);

   // public abstract void process(WriterSet writers) throws SQLException, IOException;

    public abstract void setCrosstabColumns(Collection<String> columnNames);

    public abstract void setCrosstabColumns(ArrayList<String> crossTabColumns);

    public abstract void setCrosstabRows(Collection<String> names);

    public abstract void setCrossTabRows(ArrayList<String> crossTabRows);

    public abstract void setCrosstabValues(Collection<String> values);

    public abstract void setResultSet(ResultSet resultSet);

}
