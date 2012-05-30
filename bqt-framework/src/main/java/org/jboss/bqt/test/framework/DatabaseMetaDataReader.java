package org.jboss.bqt.test.framework;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.bqt.core.util.TestLogger;

public class DatabaseMetaDataReader {

	private Connection connection;

	private boolean selectstar = false;
	private boolean selectcolumns = true;

	private List<String> queries = null;

	public DatabaseMetaDataReader(Connection connection) {
		this.connection = connection;
	}

	public void createSelectStar(boolean selectstar) {
		this.selectstar = selectstar;
	}

	public void createSelectColumns(boolean selectcolumns) {
		this.selectcolumns = selectcolumns;
	}

	public List<String> getQueries() throws Exception {
		if (queries != null)
			return queries;

		ResultSet rs = null;

		try {

			DatabaseMetaData dbmd = connection.getMetaData();

			rs = dbmd.getTables("%", "%", "%", new String[] {"TABLE","VIEW"});
			queries = loadQueries(rs, dbmd);

			System.out.println("Completed Execution");
		} catch (SQLException e) {
			// SQLException has child exceptions/ navigate the children
			TestLogger.log("Error reading metadata: " + e.getMessage());//$NON-NLS-1$
			e.printStackTrace();
			
			queries = Collections.EMPTY_LIST;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Throwable t) {

				}
			}

		}
		this.connection = null;

		return queries;

	}

	private List<String> loadQueries(ResultSet results, DatabaseMetaData dbmd)
			throws SQLException {
		List<String> queries = new ArrayList<String>();

		int queryCnt = 0;
		// Walk through each row of results
		
		

		for (int i = 1; results.next(); i++) {
			queryCnt++;
			
			String tname = results.getString("TABLE_NAME");
			String schemaname = results.getString("TABLE_SCHEM");
			String catname = results.getString("TABLE_CAT");

			if (this.selectcolumns) {
				if (tname != null) {

						String sql = createSQL(catname, schemaname, tname, dbmd);
						queries.add(sql);

				}
			}

			if (this.selectstar) {
				queries.add("Select * From " + tname);
			}
		}

		return queries;

	}
	
//	private List<String[]> getColumnsInfo(ResultSet results, DatabaseMetaData dbmd) throws SQLException {
//		List<String[]> rows = new ArrayList<String[]>();
//
//		for (int row = 1; results.next(); row++) {
//			
//			String[] rowValue = new String[3];
//
//			
//			rowValue[1] = results.getString("TABLE_SCHEM");
//			rowValue[0] = results.getString("TABLE_CAT");
//			rowValue[2] = results.getString("COLUMN_NAME");
//			
//			rows.add(rowValue);
//		}
//		
//		return rows;
//	}

	private String createSQL(String catname, String schemaname, String tname,
			DatabaseMetaData dbmd) throws SQLException {

		// Walk through each row of results to get each column in the query
		
		List<String[]> rows = new ArrayList<String[]>();

		StringBuffer sb = new StringBuffer("Select ");
		Set<String> cnames = new HashSet<String>();

		ResultSet results = null;
		try {
			results = dbmd.getColumns((catname != null ? catname : "%"),
					(schemaname != null ? schemaname : "%"), tname,
					"%");
	
			
//			ResultSetMetaData rsmeta = results.getMetaData();
//			int columnCnt = rsmeta.getColumnCount();
//			Set<String> selectable = new HashSet(columnCnt);
//			for (int c=1 ; c < columnCnt+1; c++) {
//				String colname = rsmeta.getColumnName(c);
//				
//			}
			

			for (int row = 1; results.next(); row++) {
				
				String[] rowValue = new String[3];
	
				
				rowValue[1] = results.getString("TABLE_SCHEM");
				rowValue[0] = results.getString("TABLE_CAT");
				rowValue[2] = results.getString("COLUMN_NAME");
				
				rows.add(rowValue);	
			}
		} finally {
			if (results != null) {
				try {
					results.close();
				} catch (Throwable t) {

				}
			}
		}
			
		int i=0;
		for (String[] rowValue : rows) {

			i++;
			boolean isSelectable = true;
//			boolean isSelectable = isColumnSelectable(rowValue[0], rowValue[1],
//					tname, rowValue[2], dbmd);

			if (!isSelectable) {
				System.out.println("Column " + rowValue[2]
						+ " is not selectable");
				continue;
			}

			if (i > 1) {
				sb.append(", ");
			}

			if (cnames.contains(rowValue[2])) {
				System.out.println("Duplicate Column " + rowValue[2]
						+ " in table " + tname);
			}
			cnames.add(rowValue[2]);

			sb.append(rowValue[2]);
		}

		sb.append(" From " + tname);
		return sb.toString();
	}

	private boolean isColumnSelectable(String catalogname, String schemaname,
			String tablename, String columname, DatabaseMetaData dbmd)
			throws SQLException {

		ResultSet rs = null;
		try {
			rs = dbmd.getColumnPrivileges((catalogname != null ? catalogname
					: "%"), (schemaname != null ? schemaname : "%"), tablename,
					columname);

			String priviledge = rs.getString("PRIVILEGE");

			if (priviledge != null && priviledge.toUpperCase().equals("SELECT")) {
				return true;
			}
			return false;

		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Throwable t) {

				}
			}
		}

	}

}
