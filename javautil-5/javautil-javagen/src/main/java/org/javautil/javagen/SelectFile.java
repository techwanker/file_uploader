package org.javautil.javagen;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.javautil.dataset.DatasetMetadata;
import org.javautil.dataset.DatasetMetadataFactory;
import org.javautil.file.FileHelper;

public class SelectFile {
	private final Connection conn;
	private final File file;
	private PreparedStatement ps;

	public SelectFile(Connection conn, File file) {
		if (conn == null) {
			throw new IllegalArgumentException("conn is null");
		}
		if (file == null) {
			throw new IllegalArgumentException("file is null");
		}
		this.conn = conn;
		this.file = file;
	}

	public DatasetMetadata getDatasetMetadata() throws SQLException,
			IOException {
		ResultSetMetaData meta = getResultSetMetaData();
		DatasetMetadata columnInfo = DatasetMetadataFactory.getInstance(meta);
		dispose();
		return columnInfo;
	}

	String getSqlText() throws IOException {
		String retval = FileHelper.getFileText(file);
		return retval;
	}

	ResultSetMetaData getResultSetMetaData() throws SQLException, IOException {
		ps = conn.prepareStatement(getSqlText());
		ps.executeQuery();
		ResultSetMetaData rset = ps.getMetaData();
		return rset;
	}

	void dispose() throws SQLException {
		ps.close();
	}

}
