package com.nextgis.dbgenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import SQLite.Database;

public class DBGenerator {
	static SQLite.Database db;
	private static final String files[] = { "lines", "stations", "portals",
			"graph", "interchanges" };

	public static void main(String[] args) {
		db = new SQLite.Database();
		try {
			db.set_encoding("UTF-8");
		} catch (SQLite.Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		File rootPath = new File(args[0]);
		if (rootPath.exists()) {
			try {
				SQLConverter converter = new SQLConverter(
						rootPath.getAbsolutePath(), db);
				for (int i = 0; i < files.length; i++) {
					try {
						converter.convertFile(files[i], "csv", "sql");
						System.out.println(String.format(
								"Table %s processed from %s.%s", files[i],
								files[i], "csv"));
					} catch (IOException e) {
						System.err.println(String.format("Table %s processing error.",
								files[i]));
						e.printStackTrace();
					} catch (SQLite.Exception e) {
						System.err.println(String.format("Table %s processing error.",
								files[i]));
						e.printStackTrace();
					}
				}
			} catch (java.lang.Exception e) {
				
				System.err.println("error: " + e);
			} finally {
				try {
					System.out.println("cleaning up ...");
					db.close();
				} catch (Exception e) {
					System.err.println("error: " + e);
				}
			}
		}
	}

	public static class SQLConverter {

		String rootPath;
		Database mDB;

		public SQLConverter(String rootPath, Database db) {
			mDB = db;
			this.rootPath = rootPath;
		}

		public void convertFile(String nameWithoutPreffix, String inputPreffix,
				String outputPreffix) throws IOException, SQLite.Exception {
			final String INSERT_INTO_STATEMENT = "INSERT INTO %s (%s) VALUES \n%s\n;\n";

			if ((new File(rootPath)).exists()) {
				mDB.open(String.format("%s/%s.%s", rootPath, "metroaccess",
						"sqlite3"), 0666, "unix-none", false);
				mDB.set_encoding("UTF-8");

				FileInputStream fis = new FileInputStream(new File(
						String.format("%s/%s.%s", this.rootPath,
								nameWithoutPreffix, inputPreffix)));
				BufferedReader fileReader = new BufferedReader(
						new InputStreamReader(fis, "UTF-8"));

				String header = fileReader.readLine();
				String columnHeaders[] = header.split(";");
				mDB.exec(getCreateTableSQL(columnHeaders, nameWithoutPreffix),
						null);
				String columns = getColumns(columnHeaders);
				String values = new String();
				boolean isFirstLine = true;
				String line = fileReader.readLine();
				while (line != null) {
					try {
						values = String.format("( %s )", getValues(line.split(";")));
						mDB.exec(String.format(INSERT_INTO_STATEMENT,
							nameWithoutPreffix, columns, values), null);
					} catch(SQLite.Exception e) {
						System.err.println(String.format("SQL Statement with error: %s", String.format(INSERT_INTO_STATEMENT,
							nameWithoutPreffix, columns, values)));
						throw e;
					}
					line = fileReader.readLine();
				}
				// System.out.println(String.format(INSERT_INTO_STATEMENT,
				// nameWithoutPreffix, columns, values));
//				try {
//					mDB.exec(String.format(INSERT_INTO_STATEMENT,
//						nameWithoutPreffix, columns, values), null);
//				} catch(SQLite.Exception e) {
//					System.err.println(String.format("SQL Statement with error: %s", String.format(INSERT_INTO_STATEMENT,
//						nameWithoutPreffix, columns, values)));
//					throw e;
//				} finally {
//					fileReader.close();
//				}
			}
		}

		private String getColumns(String[] line) {
			String values = new String();
			boolean isFirst = true;
			for (int i = 0; i < line.length; i++) {
				if (isFirst) {
					values = values.concat(String.format(" %s", line[i]));
					isFirst = false;
				} else {
					values = values.concat(String.format(", %s", line[i]));
				}
			}
			return values;
		}

		private String getValues(String[] line) {
			String values = new String();
			boolean isFirst = true;
			for (int i = 0; i < line.length; i++) {
				if (isFirst) {
					values = values.concat(String.format(" \'%s\'", line[i]));
					isFirst = false;
				} else {
					values = values.concat(String.format(", \'%s\'", line[i]));
				}
			}
			return values;
		}

		private String getCreateTableSQL(String[] columnHeaders,
				String tableName) {
			final String CREATE_TABLE_STATEMENT_FORMAT = "CREATE TABLE IF NOT EXISTS %s (\n_id INTEGER PRIMARY KEY, \n%s\n);\n";
			final String CREATE_TABLE_COLUMN_FORMAT = "%s %s";
			boolean isFirstColumn = true;
			String createColumns = new String();
			for (int i = 0; i < columnHeaders.length; i++) {
				String columnDefinition = String.format(
						CREATE_TABLE_COLUMN_FORMAT, columnHeaders[i], "TEXT");
				if (isFirstColumn) {
					createColumns = columnDefinition;
					isFirstColumn = false;
				} else {
					createColumns = createColumns.concat(String.format(",\n%s",
							columnDefinition));
				}
			}
			return String.format(CREATE_TABLE_STATEMENT_FORMAT, tableName,
					createColumns);
		}
	}
}
