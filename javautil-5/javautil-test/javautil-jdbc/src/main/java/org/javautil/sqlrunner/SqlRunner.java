//package org.javautil.sqlrunner;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class SqlRunner {
//
//
//
//
//
//
//#class StatementsMap
//        #addSqlState,e
//#
//# 
//class SqlStatements:
//	name = None
//	type = None
//	statementList = []
//	statementLines = []
//	statementList = []
//	def __init__(self, name, type):
//		""" Constructs SqlRunner for the given file
//		"""
//    		self.name = name
//		self.type = type
//		#print self.statementLines
//
//	def addLine(self,text):
//                print self.name + ": " + text.rstrip()
//		self.statementLines.append(text)
//
//	def addStatement(self,statementName):
//		statement = Statement(statementName)
//	        self.statementList.append(statement)	
//		return statement
//
//	def getStatements(self):
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
//							
//#class StatementsByName:
//        #for key, value in statements:
//           ##print key
//           #print value
//
//class SqlRunner:
//	sqlFile = None
//	#
//	# Regular Expression
//	#
//	openComment = "(\s*/\*\s*)"  #1
//	sql = "([Ss][Qq][Ll])"       #2
//	type = "([;:])"              #3
//	whitespace = "(\s*)"         #4
//	name = "(\w*)" 		     #5
//	commentClose = "(\s*\*/\s*)" #6
//	regex = re.compile(openComment + sql + type + 
//		whitespace + name + commentClose)
//
//	statementSetMap = {}
//	statementSetList = []
//	#
//	#
//	#
//	def __init__(self, file):
//		""" Constructs SqlRunner for the given file
//		"""
//    		self.file = file
//
//	def dumpStatementSets(self):
//	 	for name in self.statementSetList:
//			print name + ":"
//			sqlStatements = self.statementSetMap.get(name)
//			for line in sqlStatements.statementLines:
//				print line.rstrip()
//
//		for name in self.statementSetList:
//			sqlStatements = self.statementSetMap.get(name)
//			statements = sqlStatements.getStatements()
//			for statement in statements:
//				print "Statement:"
//				print statement	
//			
//	
//	def readSqlFile(self,fileName): 
//	    print fileName
//	    print "Processing file: " + fileName
//	    sqlStatements = None
//	    sqlFile = open(fileName)
//	    for line in sqlFile:
//	       retval = self.parse_line(line) 
//	        
//	       if retval != None: 
//		   type = retval[0]
//		   name = retval[1]
//	           print 'Name: "' + name + ' type: "' + type + '""'
//		   sqlStatements = SqlStatements(name,type);
//		   self.statementSetMap[name] = sqlStatements
//		   self.statementSetList.append(name)
//	       elif sqlStatements != None:
//                   sqlStatements.addLine(line)
//
//	    file.close(sqlFile) 
//	    self.dumpStatementSets()
//
//
//	#/* https://docs.python.org/2/library/re.html */
//	def parse_line(self,line,trace = 'False'):
//	#	print "processing: '" + line + "'"
//		m = self.regex.match(line)
//		retval = None
//		if m != None:
//			gWhole = m.group(0)
//			gLeading = m.group(1)
//			gSql = m.group(2)
//			gType = m.group(3)
//			gWhiteSpace = m.group(4)
//			gStatementName = m.group(5)
//			gCommentClose = m.group(6)
//			if trace == True:
//				print "0: whole '" + m.group(0) + "'"  #whole expression
//				print "1: leading '" + m.group(1) + "'"  #leading /*
//				print "2: sql '" + m.group(2) + "'"  #sql
//				print "3: type '" + m.group(3) + "'"  # : or ;
//				print "4: whiteSpace'" + m.group(4) + "'"  # whitespace 
//				print "5: statement_name '" + m.group(5) + "'"  
//					# statement name
//				print "6: commentClose '" + m.group(6) + "'"  # comment close
//			retval = (gType, gStatementName)
//			return retval
//	
//	
//	
//if __name__ == "__main__":
//	fileName= sys.argv[1]
//	print "processing file"
//	print fileName
//	sqlRunner = SqlRunner(fileName)
//	sqlRunner.readSqlFile(fileName)
