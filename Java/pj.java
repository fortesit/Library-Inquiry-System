import java.sql.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
class pj{
	public static Connection conn = null;
	public static Statement stmt = null;
	public static int fselect=0;
	public static String strt = null;
	public static void main(String[] args){
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		// Login SQL
		try{
			Class.forName("oracle.jdbc.driver.OracleDriver");
		}catch(Exception x){
			System.out.println("Unable to load the driver class!");
			System.exit(0);
		}  // Finish driver loading
		try{
		conn=DriverManager.getConnection("jdbc:oracle:thin:@db12.cse.cuhk.edu.hk:1521:db12","d093","123456");
		System.out.println("");
			System.out.println("=========Login to server success========");
		System.out.println("");
		}catch(Exception x){
			System.out.println("Unable to login Oracle SQL server");
			System.out.println("Please try again later!");
			System.exit(0);
		}

		// End login SQL server
		System.out.println("Welcome to 3170 Library Enquiry System   Ver:Final");
		System.out.println("   ");
		while(true){

		System.out.println("   ");
		System.out.println("Please select the function:");
		System.out.println("1. Create all table");
		System.out.println("2. Delete all table");
		System.out.println("3. Load data into database");
		System.out.println("4. List all overdue book copies");
		System.out.println("5. Show the borrow history of a user by UserID");
		System.out.println("6. Show ranking of books");
		System.out.println("7. Exit the system");
		System.out.println("Enter the No. (1-7)");
		
		try{
		strt = in.readLine();
		}catch(Exception x){
			System.out.println("ERROR"+x.toString());
		} 
		try{
		fselect =Integer.parseInt(strt);
		}catch(Exception x){
			System.out.println("ERROR"+x.toString());
		} 

		switch(fselect){
		case 1:
		try{
			stmt = conn.createStatement();
			stmt.executeUpdate ("CREATE TABLE Book (callnum CHAR(8),title CHAR(30),publish DATE,PRIMARY KEY (callnum))");
			stmt.executeUpdate ("CREATE TABLE Authorship (aname CHAR(25) NOT NULL,"+
			"callnum CHAR(8) NOT NULL,PRIMARY KEY (aname,callnum), FOREIGN KEY(callnum) REFERENCES Book ON DELETE CASCADE)");
			stmt.executeUpdate ("CREATE TABLE Copy (callnum CHAR(8) NOT NULL,"+
			"copynum INTEGER,PRIMARY KEY (copynum, callnum), FOREIGN KEY (callnum) REFERENCES Book ON DELETE CASCADE)");
			stmt.executeUpdate ("CREATE TABLE Category (catid CHAR(1),"+
			"maxbk INTEGER,period INTEGER,PRIMARY KEY (catid))");
			stmt.executeUpdate ("CREATE TABLE Users (userid CHAR(10),name CHAR(25),"+
			"address CHAR(100),catid CHAR(1) NOT NULL,PRIMARY KEY (userid),"+
			"FOREIGN KEY (catid) REFERENCES Category)");
			stmt.executeUpdate ("CREATE TABLE Borrow (userid CHAR(10),callnum CHAR(8) NOT NULL,"+
			"copynum INTEGER,checkout DATE,return DATE,"+
			"FOREIGN KEY (callnum) REFERENCES Book ON DELETE CASCADE,"+
			"FOREIGN KEY (userid) REFERENCES Users ON DELETE CASCADE)");
			stmt.close();
			System.out.println("Create table success");
		}catch(Exception x){
			System.out.println("ERROR"+x.toString());
		}
		break;
		case 2:
		try{
			stmt = conn.createStatement();
			stmt.executeUpdate ("DROP TABLE Borrow");
			stmt.close();
		}catch(Exception x){
			System.out.println("ERROR"+x.toString());
		}

		try{
			stmt = conn.createStatement();
			stmt.executeUpdate ("DROP TABLE Copy");
			stmt.close();
		}catch(Exception x){
			System.out.println("ERROR"+x.toString());
		}
		try{
			stmt = conn.createStatement();
			stmt.executeUpdate ("DROP TABLE Authorship");
			stmt.close();
		}catch(Exception x){
			System.out.println("ERROR"+x.toString());
		}
		try{
			stmt = conn.createStatement();
			stmt.executeUpdate ("DROP TABLE Users");
			stmt.close();
		}catch(Exception x){
			System.out.println("ERROR"+x.toString());
		}
		try{
			stmt = conn.createStatement();
			stmt.executeUpdate ("DROP TABLE Book");
			stmt.close();
		}catch(Exception x){
			System.out.println("ERROR"+x.toString());
		}
		try{
			stmt = conn.createStatement();
			stmt.executeUpdate ("DROP TABLE Category");
			stmt.close();
		}catch(Exception x){
			System.out.println("ERROR"+x.toString());
		}
			System.out.println("Drop table done");
		break;
		case 3:
			
			

			try{
			String temps;
			BufferedReader infile = new BufferedReader(new
			FileReader(new File("./Book.txt")));
			while((temps=infile.readLine())!=null){
			String[] tokens;
			int ti=0;
			tokens = temps.split("	");
			while(ti<tokens.length){
			PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Book values(?,?,to_date(?,'dd/mm/yyyy'))");
		//	System.out.println("0 ."+tokens[ti]);
		//	System.out.println("1 ."+tokens[ti+2]);
		//	System.out.println("2 ."+tokens[ti+4]);
			pstmt.setString(1,tokens[ti]);
			pstmt.setString(2,tokens[ti+2]);
			pstmt.setString(3,tokens[ti+4]);
			pstmt.execute();
			pstmt.close();
			ti+=5;
			}
			
			}
			infile.close();
			}catch(Exception x){
			System.out.println("ERROR"+x.toString());
			}


			try{
			String temps;
			BufferedReader infile = new BufferedReader(new
			FileReader(new File("./Book.txt")));
			while((temps=infile.readLine())!=null){
			String[] tokens;
			int ti=0;
			tokens = temps.split("	");
			while(ti<tokens.length){
		//	System.out.println("0 ."+tokens[ti]);
		//	System.out.println("1 ."+tokens[ti+1]);
			for(int j=0;j<Integer.parseInt(tokens[ti+1]);j++){
			PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Copy values(?,?)");
			pstmt.setString(1,tokens[ti]);  // Call No
			pstmt.setInt(2,j); // Copy No
			pstmt.execute();
			pstmt.close();
			}
			ti+=5;
			}
			}
			infile.close();
			}catch(Exception x){
			System.out.println("ERROR"+x.toString());
			}


			try{
			String temps;
			BufferedReader infile = new BufferedReader(new
			FileReader(new File("./Book.txt")));
			while((temps=infile.readLine())!=null){
			String[] tokens,tokenss;
			
			int ti=0;
			int tii=0;
			tokens = temps.split("	");
			while(ti<tokens.length){
			tokenss= tokens[ti+3].split(",");
			while(tii<tokenss.length){
			PreparedStatement pstmt = conn.prepareStatement("INSERT INTO authorship values(?,?)");
			pstmt.setString(1,tokenss[tii]);
			pstmt.setString(2,tokens[ti]);
			tii++;
			pstmt.execute();
			pstmt.close();
			}// Inner while
			ti+=5;
			}
			}
			infile.close();
			}catch(Exception x){
			System.out.println("ERROR"+x.toString());
			}

			try{
			String temps;
			BufferedReader infile = new BufferedReader(new
			FileReader(new File("./Category.txt")));
			while((temps=infile.readLine())!=null){
			String[] tokens;
			int ti=0;
			tokens = temps.split("	");
			PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Category values(?,?,?)");
			while(ti<tokens.length){
		//	System.out.println(ti+". "+tokens[ti]);
			if(ti%3==0)
			pstmt.setInt(1,Integer.parseInt(tokens[ti]));
			if(ti%3==1)
			pstmt.setInt(2,Integer.parseInt(tokens[ti]));
			if(ti%3==2)
			pstmt.setInt(3,Integer.parseInt(tokens[ti]));
			ti++;}
			pstmt.execute();
			pstmt.close();
			}
			infile.close();
			}catch(Exception x){
			System.out.println("ERROR"+x.toString());
			}

			try{
			String temps;
			BufferedReader infile = new BufferedReader(new
			FileReader(new File("./User.txt")));
			while((temps=infile.readLine())!=null){
			String[] tokens;
			int ti=0;
			tokens = temps.split("	");
			PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Users values(?,?,?,?)");
			while(ti<tokens.length){
		//	System.out.println(ti+". "+tokens[ti]);
			if(ti%4==0)
			pstmt.setString(1,tokens[ti]);
			if(ti%4==1)
			pstmt.setString(2,tokens[ti]);
			if(ti%4==2)
			pstmt.setString(3,tokens[ti]);
			if(ti%4==3)
			pstmt.setString(4,tokens[ti]);
			ti++;}
			pstmt.execute();
			pstmt.close();
			}
			infile.close();
			}catch(Exception x){
			System.out.println("ERROR"+x.toString());
			}
			
			try{
			String temps;
			BufferedReader infile = new BufferedReader(new
			FileReader(new File("./Checkout.txt")));
			while((temps=infile.readLine())!=null){
			String[] tokens;
			int ti=0;
			tokens = temps.split("	");
			PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Borrow values(?,?,?,"
			+"to_date(?,'dd/mm/yyyy'),to_date(?,'dd/mm/yyyy'))");
			while(ti<tokens.length){
		//	System.out.println(ti+". "+tokens[ti]);
			if(ti%5==0)
			pstmt.setString(1,tokens[ti]);
			if(ti%5==1)
			pstmt.setString(2,tokens[ti]);
			if(ti%5==2)
			pstmt.setInt(3,Integer.parseInt(tokens[ti]));
			if(ti%5==3)
			pstmt.setString(4,tokens[ti]);
			if(ti%5==4){
			if(tokens[ti].equals("null")){
			pstmt.setNull(5,java.sql.Types.DATE);
			//System.out.println("Send Null");
			}
			else{
			pstmt.setString(5,tokens[ti]);
			//System.out.println("Send Date");
			}
			}
			ti++;}
			pstmt.execute();
			pstmt.close();
			}
			infile.close();
			}catch(Exception x){
			System.out.println("ERROR"+x.toString());
			}

			break;

		case 4:
			try{
			String r1,r2,r3,r4,r6;
			java.sql.Date r5;

			
			stmt = conn.createStatement();

			ResultSet rs=stmt.executeQuery("SELECT bo.callnum,bo.copynum,bk.title,bo.userid,bo.checkout,"+
			"(floor((SELECT SYSDATE FROM dual)-bo.checkout-c.period))"+
			" FROM borrow bo,book bk,category c,Users u"+
			" WHERE bo.callnum=bk.callnum AND bo.userid=u.userid AND u.catid=c.catid"+
			" AND ((SELECT SYSDATE FROM dual)-bo.checkout > (c.period+1))"+
			" AND NVL(bo.return,to_date('01-01-0001','DD-MM-YYYY'))=to_date('01-01-0001','DD-MM-YYYY')"+
			" ORDER BY bo.callnum,bo.copynum ASC");
				System.out.println("Call Number    Copy Number    Title                           UserID     Checkout date         Overdue date");
				System.out.println("-----------------------------------------------------------------------------------------------------------");
			while(rs.next()){
				r1=rs.getString(1);
				r2=rs.getString(2);
				r3=rs.getString(3);
				r4=rs.getString(4);
				r5=rs.getDate(5);
				r6=rs.getString(6);
				System.out.print(r1+"         ");
				System.out.print(r2+"         ");
				System.out.print(r3+"  ");
				System.out.print(r4+"     ");
				System.out.print(r5+"            ");
				System.out.println(r6);

			}
			stmt.close();
			}catch(Exception x){
			System.out.println("ERROR"+x.toString());
			}

			break;

		case 5:
			try{
			//java.sql.Date r1;
			String r1,r2,r3,r4;
			PreparedStatement pstmt = conn.prepareStatement("SELECT to_char(bo.checkout,'DD-MM-YYYY'),bo.callnum,bk.title,bo.return"+
			" FROM borrow bo,book bk"+
			" WHERE bo.userid=? AND bo.callnum=bk.callnum"+
			" ORDER BY bo.checkout DESC");
			System.out.print("Please enter a user ID to start search:");
			try{
			strt = in.readLine();
			}catch(Exception x){
			System.out.println("ERROR"+x.toString());
			} 
			System.out.println();
			pstmt.setString(1,strt);
			ResultSet rs=pstmt.executeQuery();
			
			
				System.out.println("Checkout date     Call Number   Title                   Returned? (Y/N)");
				System.out.println("--------------------------------------------------------------------------");
			while(rs.next()){
				r1=rs.getString(1);
				r2=rs.getString(2);
				r3=rs.getString(3);
				r4=rs.getString(4);
				System.out.print(r1+"        ");
				System.out.print(r2+"   ");
				System.out.print(r3+"   ");
				if(r4==null)
				System.out.print("N");
				else
				System.out.print("Y");
				System.out.println();
			}
			pstmt.close();
			pstmt = conn.prepareStatement("SELECT COUNT(*)"+
			" FROM borrow bo"+
			" WHERE bo.userid=?");
			pstmt.setString(1,strt);
			rs=pstmt.executeQuery();
			while(rs.next()){
				r1=rs.getString(1);
				System.out.print("No. of books the user read:");
				System.out.println(r1);
			}
			pstmt.close();
			pstmt = conn.prepareStatement("SELECT COUNT(*)"+
			" FROM borrow bo,category c,Users u"+
			" WHERE bo.userid=? AND bo.userid=u.userid AND u.catid=c.catid"+
			" AND (bo.return-bo.checkout > c.period)"+
			" AND NVL(bo.return,to_date('01-01-0001','DD-MM-YYYY'))!=to_date('01-01-0001','DD-MM-YYYY')");
			pstmt.setString(1,strt);
			rs=pstmt.executeQuery();
			while(rs.next()){
				r1=rs.getString(1);
				System.out.print("No. of times the user returned a book copy after the loan period:");
				System.out.println(r1);
			}
			pstmt.close();
			}catch(Exception x){
			System.out.println("ERROR"+x.toString());
			}



		break;

		case 6:

			try{
			String dst=null,det=null;
			java.util.Date dsu=null,deu=null;
			java.sql.Date ds=null,de=null;
			int r1=0;
			String r2,r3;
			
			PreparedStatement pstmt = conn.prepareStatement("SELECT t.cnt,t.cno,bk.title"+
			" FROM book bk,"+
			"(SELECT bo.callnum AS cno,count(*) AS cnt"+
			" FROM borrow bo"+
			" WHERE bo.checkout>=? AND bo.checkout<=?"+
			" GROUP BY bo.callnum)t"+
			" WHERE t.cno=bk.callnum"+
			" ORDER BY t.cnt DESC");
			System.out.print("Please enter a start day (DD-MM-YYYY):");
			try{
			dst = in.readLine();
			}catch(Exception x)
			{System.out.println("Unable to read in"+x.toString());} 
			
			System.out.print("Please enter a end day (DD-MM-YYYY):");
			try{
			det = in.readLine();
			}catch(Exception x)
			{System.out.println("Unable to read in"+x.toString());}
			SimpleDateFormat dateformat = new SimpleDateFormat("d-M-y");
			dsu= dateformat.parse(dst);
			deu= dateformat.parse(det);
			ds = new java.sql.Date(dsu.getTime());
			de = new java.sql.Date(deu.getTime());
			pstmt.setDate(1,ds);
			pstmt.setDate(2,de);
			ResultSet rs=pstmt.executeQuery();
			
			System.out.println("Ranking     Call Number    Title");
			System.out.println("----------------------------------------------------------------------------");
			int rank=1,prevcnt=0,prevrank=1;
			while(rs.next()){
				r1=rs.getInt(1);
				r2=rs.getString(2);
				r3=rs.getString(3);

				if(r1==prevcnt){
				System.out.print(prevrank+"          ");
				}
				else{
				System.out.print(rank+"          ");
				prevrank=rank;
				}
				prevcnt=r1;
				rank++;
				System.out.print(r2+"  ");
				System.out.print(r3+"  ");
				System.out.print(r1+"  ");
				System.out.println();
				
			}
			}catch(Exception x){
			System.out.println("ERROR"+x.toString());
			}
				System.out.println();
			System.out.println("----------------------------------------------------------------------------");
				System.out.println();
		break;

		case 7:
			try{
			conn.close();
			}catch(Exception x){;}
			System.exit(0);
		default: 
			System.out.println("Please enter No. within 1-6");
		} //End of switch
	
	} // End of while(1) loop

	} // End of main

}// End of class
