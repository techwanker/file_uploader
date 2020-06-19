package org.javautil.dex.renderer;

import java.io.IOException;
import java.sql.SQLException;

import org.javautil.dex.renderer.interfaces.RenderingException;
import org.javautil.document.MimeType;
import org.javautil.jdbc.resultset.JdbcResultSetHelper;

public class CsvWriter extends AbstractRenderer {
	private static final RenderingCapability capability = new RenderingCapability(
			MimeType.CSV, false);

	public CsvWriter() {
		super(capability);
	}

	@Override
	public void process() throws RenderingException, IOException {
		try {
			final JdbcResultSetHelper rsh = new JdbcResultSetHelper(getRset());
			rsh.setDateFormatter(getDateFormatter());
			String txt = rsh.getMetaDataAsCsv();
			write(txt);
			write(newline);

			while (rsh.next()) {
				txt = rsh.getNextAsCsv();
				write(txt);
				write(newline);
				rowsProcessed++;
			}

		} catch (final SQLException sqe) {
			throw new RenderingException(sqe);
		}
	}
}
