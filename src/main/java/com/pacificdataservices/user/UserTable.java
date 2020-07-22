package com.pacificdataservices.user;

public class UserTable {
	
  
   public  static final String createTable  = 
			"create table user ( "
					+ "   user_id number(9) primary key, "
					+ "   user_name varchar(32),"
					+ "   first_name varchar(16), "
					+ "   phn_nbr number(16), "
					+ "   date_of_birth date)";
			
	public static final String insert =
			"insert into user ("
			+ "	user_id, user_name"
			+ ") values"
			+ "(?,?)";
	
//		@Test public void testConnection() throws SQLException {
//			Connection conn = dataSource.getConnection();
//			Statement ss = conn.createStatement();
//			ss.execute("drop table user if exists");
//			ss.execute(createTable);
//			
//					"create table user ( "
//					+ "   user_id number(9) primary key, "
//					+ "   user_name varchar(32),"
//					+ "   first_name varchar(16), "
//					+ "   phn_nbr number(16), "
//					+ "   date_of_birth date)");
//			
//
//			PreparedStatement
//	}
}
