package org.javautil.dex;

import java.util.Collection;

public interface Crosstabber {
	public abstract void setCrosstabRowKeyColumnNames(Collection<String> rowColumnNames);
	public abstract void setCrosstabCellColumnNames(Collection<String> cellColumnNames);
	public abstract void setCrosstabColumnColumnNames(Collection<String> columnColumnNames);
}
