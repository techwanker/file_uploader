//package org.javautil.sqlrunner;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.log4j.Logger;
//
//public class SqlStatements {
//	Logger logger = Logger.getLogger(this.getClass());
//
//	String name;
//	String type; // convert to Enum
//	List<SqlStatement >statementList = new ArrayList<SqlStatement>();
//	List<String > statementLines = new ArrayList<String>();
//	//statementList = []
//	
//	public SqlStatements(String name, String statementType) {
//
//    		this.name = name;
//    		this.type = statementType;
//	}
//
//	public void addLine(String text) {
//	
//        logger.info(name + ": " + text.trim());  // TODO This should be a right trim
//		statementLines.add(text);
//	}
////
////	public void  addStatement(statementName):
////		statement = Statement(statementName)
////	        self.statementList.append(statement)	
////		return statement
//
//	public  getStatements(self):
//		if self.type == ":":
//		    returnValue =  self.getCodeBlock()
//		elif self.type == ";":
//		    returnValue = self.getSqlStatements()
//                else:
//		    print "This is an error" # should be exceptions
//		return returnValue
//
//	def getSqlStatements(self):
//		statements = [];
//		statementText = ""
//		for line in self.statementList:
//			stripLine = line.rstrip();
//			endChar = stripLine[-1:-1]
//                        print "endChar '" + endChar;
//                        if endChar == ";":
//				lineText = stripLine[0:-2]
//				print "lineText: '" + lineText + "'"
//				statementText += lineText
//				statements.append(statementText)
//			else:
//				statements.append(stripLine)
//		print "Statement: "
//                print statementText
//		statementText = ""
//		return statements
//
//	def getCodeBlock(self):
//		statements = [];
//		statementText = ""
//		endOfStatementFound = False
//		for line in self.statementList:
//			stripLine = line.rstrip();
//			if endOfStatementFound == True:
//				if stripLine != "":
//					print "Unexpected after '/'"
//			if stripLine == "/":
//				endOfStatementFound = True
//			else:
//				statements.append(stripLine)
//		print "Statement: "
//                print statementText
//		statementText = ""
//		return statements
