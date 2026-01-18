package keronBall;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import database.DBConnection;
import jakarta.servlet.http.HttpServletRequest;

public class KeronBallService {
	private static final KeronBallService instance = new KeronBallService();
	public static KeronBallService getInstance() {
		return instance;
	}
	
	private KeronBallService() {
	}
	
	
	public void createAllDB(HttpServletRequest request) {
		System.out.println("createAllDB");
		
		String sqlDir = request.getServletContext().getRealPath("/resources/sql");
		if (sqlDir==null) {
			throw new IllegalStateException("resources/sql null");
		}
		
		File dir = new File(sqlDir);
		if (!dir.exists()||!dir.isDirectory()) {
			throw new IllegalStateException("SQL 디렉터리가 존재하지 않습니다");
		}
		File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".sql"));
		if (files == null || files.length == 0) {
            throw new IllegalStateException("실행할 .sql 파일이 없습니다: " + dir.getAbsolutePath());
        }
		Arrays.sort(files, Comparator.comparing(File::getName));
		
		try (	Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement()				){
			
			conn.setAutoCommit(false);
			
			for (File f : files) {
				List<String> sqlStatements = readAndSplitSqlStatements(f);
				
				System.out.println("생성:" + f.getName());
				for (String sql : sqlStatements) {
					String s = sql.trim();
					if(!s.isEmpty()) {
						stmt.execute(s);
					}
				}
			}
			
			conn.commit();
			
		} catch (Exception e) {
			System.out.println("CREATEALLDB 뭔가 잘못됨");
			e.printStackTrace();
		}
		
		
	}
	public void deleteAllDB() {
		System.out.println("deleteAllDB");
		
		try (
				Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement()
				
				) {
			
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");

            List<String> tables = getAllTables();

            for (String tableName : tables) {
                String dropSQL = "DROP TABLE IF EXISTS `" + tableName + "`";
                System.out.println("삭제: " + tableName);
                stmt.execute(dropSQL);
            }

            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");

            System.out.println("모든 테이블 삭제 완료");

			
		} catch (Exception e) {
			System.out.println("DELETEALLDB 뭔가 잘못됨");
			e.printStackTrace();
		}
		
		
		
		
	}
	
	private List<String> readAndSplitSqlStatements(File file) throws Exception {
		List<String> list = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
			
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				
				// 빈 줄 / 라인 주석 무시
				if (line.isEmpty() || line.startsWith("--")) {
					continue;
				}
				
				sb.append(line).append(' ');
				
				// 라인이 ; 로 끝나면 한 문장 완료로 처리
				if (line.endsWith(";")) {
					list.add(sb.toString());
					sb.setLength(0);
				}
			}
		}
		
		// 파일 끝났는데 ; 없이 남아있으면(실수 방지)
		if (sb.toString().trim().length() > 0) {
			throw new IllegalStateException("SQL 문장이 ; 로 끝나지 않았습니다: " + file.getName());
		}
		
		return list;
	}
	
	public List<String> getAllTables(){
		List<String> tables = new ArrayList<>();

	    String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'lms'";
	    try (	Connection conn = DBConnection.getConnection();
	    		Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {

	        while (rs.next()) {
	            tables.add(rs.getString("table_name"));
	        }
	    } catch (Exception e){
	    	e.printStackTrace();
	    }
	    return tables;

	}
	
	
}
