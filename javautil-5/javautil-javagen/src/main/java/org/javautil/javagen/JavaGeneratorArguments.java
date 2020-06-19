package org.javautil.javagen;

import java.io.File;

import org.javautil.commandline.annotations.FileExists;
import org.javautil.commandline.annotations.Optional;
import org.javautil.commandline.annotations.Required;

public class JavaGeneratorArguments {

	@Required
	private String dtoPackageName;

	@Optional
	private boolean generateInsert;

	@Required
	private String daoImplClassName;

	@Optional
	private String dtoClassName;

	@Required
	private File sourceCodeDirectory;

	@FileExists
	@Optional
	private File selectFile;

	@Optional
	private final boolean generatePrimitives = false;

	private String daoPackageName;

	private String tableName;

	public String getDtoPackageName() {
		return dtoPackageName;
	}

	public void setDtoPackageName(String dtoPackageName) {
		this.dtoPackageName = dtoPackageName;
	}

	public boolean isGenerateInsert() {
		return generateInsert;
	}

	public void setGenerateInsert(boolean generateInsert) {
		this.generateInsert = generateInsert;
	}

	public String getDaoImplClassName() {
		return daoImplClassName;
	}

	public void setDaoImplClassName(String daoImplClassName) {
		this.daoImplClassName = daoImplClassName;
	}

	public String getDtoClassName() {
		if (dtoClassName == null) {
			throw new IllegalStateException(
					"setDtoClassName(String className) has not been called");
		}
		return dtoClassName;
	}

	public void setDtoClassName(String dtoClassName) {
		this.dtoClassName = dtoClassName;
	}

	public File getSourceCodeDirectory() {
		return sourceCodeDirectory;
	}

	public void setSourceCodeDirectory(File sourceCodeDirectory) {
		this.sourceCodeDirectory = sourceCodeDirectory;
	}

	public File getSelectFile() {
		return selectFile;
	}

	public void setSelectFile(File selectFile) {
		this.selectFile = selectFile;
	}

	public void setDaoPackageName(String daoPackageName) {
		this.daoPackageName = daoPackageName;
	}

	public boolean isGeneratePrimitives() {
		return generatePrimitives;
	}

	public void setDatasourceName(String datasourceName) {

	}

	public String getDaoPackageName() {
		return daoPackageName;
	}

	public String getTableName() {
		return tableName;
	}

}
