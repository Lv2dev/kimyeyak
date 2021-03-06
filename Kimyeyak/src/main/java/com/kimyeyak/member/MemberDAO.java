package com.kimyeyak.member;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.kimyeyak.dbconnect.JDBConnect;

public class MemberDAO extends JDBConnect {
	private static MemberDAO memberDAO = new MemberDAO();

	// 생성자
	private MemberDAO() {

	}

	// getters and setters

	// 인스턴스 getter
	public static MemberDAO getInstance() {
		if (memberDAO == null) {
			memberDAO = new MemberDAO();
		}
		return memberDAO;
	}

	// method

	// 아이디 중복 체크, 중복없음 true, 중복있음 false;
	public synchronized boolean idCheck(String id) throws SQLException {
		try {
			conn = dbConn.getConn();

			query = new StringBuffer();

			query.append("SELECT id FROM member ");
			query.append("WHERE id = ?");

			pstmt = conn.prepareStatement(query.toString());
			pstmt.setString(1, id);

			rs = pstmt.executeQuery();

			int cnt = 0;
			while (rs.next()) {
				cnt++;
			}

			if (cnt != 0) {
				return false;
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("아이디 중복체크 에러 \n" + e.getMessage());
			return false;
		} finally {
			disconnectPstmt();
		}
	}

	// 회원가입
	public synchronized boolean joinMember(MemberDTO memberDTO) throws SQLException {
		try {
			conn = dbConn.getConn();
			query = new StringBuffer();
			query.append(
					"INSERT INTO member(id, pw, name, nickname, tel, email, bday, jday, type, gender, question, answer) ");
			query.append("VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

			// 현재시간 timestamp
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Calendar cal = Calendar.getInstance();
			String today = null;
			today = formatter.format(cal.getTime());
			Timestamp ts = Timestamp.valueOf(today);

			pstmt = conn.prepareStatement(query.toString());

			pstmt.setString(1, memberDTO.getId());
			pstmt.setString(2, memberDTO.getPw());
			pstmt.setString(3, memberDTO.getName());
			pstmt.setString(4, memberDTO.getNickname());
			pstmt.setString(5, memberDTO.getTel());
			pstmt.setString(6, memberDTO.getEmail());
			pstmt.setDate(7, memberDTO.getbDay());
			pstmt.setTimestamp(8, ts);
			pstmt.setInt(9, memberDTO.getType());
			pstmt.setInt(10, memberDTO.getGender());
			pstmt.setString(11, memberDTO.getQuestion());
			pstmt.setString(12, memberDTO.getAnswer());

			if (pstmt.executeUpdate() != 1) {
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("회원가입 에러\n" + e.getMessage());
			return false;
		} finally {
			disconnectPstmt();
		}
	}

	// 비밀번호 수정
	public synchronized boolean replacePw(MemberDTO memberDTO, String pw) throws SQLException {
		try {
			conn = dbConn.getConn();
			query = new StringBuffer();
			query.append("UPDATE member ");
			query.append("SET pw = ? ");
			query.append("WHERE id = ?");

			pstmt = conn.prepareStatement(query.toString());

			pstmt.setString(1, memberDTO.getPw());
			pstmt.setString(2, memberDTO.getId());

			if (pstmt.executeUpdate() != 1) {
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("비밀번호 수정 에러" + e.getMessage());
			return false;
		} finally {
			disconnectPstmt();
		}
	}

	// 로그인
	public synchronized boolean memberLogin(String id, String pw) throws SQLException {
		try {
			conn = dbConn.getConn();

			query = new StringBuffer();

			query.append("SELECT id FROM member ");
			query.append("WHERE id = ? AND pw = ?");

			pstmt = conn.prepareStatement(query.toString());

			pstmt.setString(1, id);
			pstmt.setString(2, pw);

			rs = pstmt.executeQuery();
			int cnt = 0;
			while (rs.next()) {
				cnt++;
			}

			if (cnt == 1) {
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("로그인 에러\n" + e.getMessage());
			return false;
		} finally {
			disconnectPstmt();
		}
	}

	// 비밀번호 찾기 전 질문 가져오기
	public synchronized String getQuestion(String id) throws SQLException {
		try {
			conn = dbConn.getConn();
			query = new StringBuffer();

			query.append("SELECT id, question, answer FROM member ");
			query.append("WHERE id = ?");

			pstmt = conn.prepareStatement(query.toString());

			pstmt.setString(1, id);

			rs = pstmt.executeQuery();

			int cnt = 0;
			String question = "";
			while (rs.next()) {
				question = rs.getString("question");
				cnt++;
			}

			if (cnt != 1) {
				return null;
			}

			return question;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("비밀번호찾기 질문 가져오기 오류\n" + e.getMessage());
			return null;
		} finally {
			disconnectStmt();
		}
	}

	// 질문 가져온 후 비밀번호 가져오기
	public synchronized String getPassword(String id, String answer) throws SQLException {
		try {
			conn = dbConn.getConn();
			query = new StringBuffer();

			query.append("SELECT pw FROM member ");
			query.append("WHERE id = ? AND answer = ?");

			pstmt = conn.prepareStatement(query.toString());

			pstmt.setString(1, id);
			pstmt.setString(2, answer);

			rs = pstmt.executeQuery();

			int cnt = 0;
			String password = "";
			while (rs.next()) {
				password = rs.getString("pw");
				cnt++;
			}

			if (cnt != 1) {
				return null;
			}

			return password;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("비밀번호 가져오기 오류 \n" + e.getMessage());
			return null;
		} finally {
			disconnectStmt();
		}
	}

	// 로그인한 회원의 정보를 한번에 가져오는 메서드
	public synchronized MemberDTO getMemberInfo(String id) throws SQLException {
		try {
			conn = dbConn.getConn();

			query = new StringBuffer();

			query.append("SELECT * FROM member ");
			query.append("WHERE id = ?");

			pstmt = conn.prepareStatement(query.toString());

			pstmt.setString(1, id);

			rs = pstmt.executeQuery();

			MemberDTO memberDTO = new MemberDTO();

			int cnt = 0;
			while (rs.next()) {
				memberDTO.setId(id);
				memberDTO.setPw(rs.getString("pw"));
				memberDTO.setName(rs.getString("name"));
				memberDTO.setNickname(rs.getString("nickname"));
				memberDTO.setTel(rs.getString("tel"));
				memberDTO.setEmail(rs.getString("email"));
				memberDTO.setbDay(rs.getDate("bday"));
				memberDTO.setjDay(rs.getDate("jday"));
				memberDTO.setType(rs.getInt("type"));
				memberDTO.setGender(rs.getInt("gender"));
				memberDTO.setQuestion(rs.getString("question"));
				memberDTO.setAnswer(rs.getString("answer"));
				cnt++;
			}

			if (cnt < 1) {
				return null;
			}

			return memberDTO;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("회원정보 가져오기 오류 \n" + e.getMessage());
			return null;
		} finally {
			disconnectPstmt();
		}
	}

	// 회원정보수정 메서드 - name, nickname, tel, email, question, answer만 수정가능
	// memberDTO1 : 원래 memberDTO, memberDTO2 : 변경할 memberDTO
	// 수정 성공하면 바뀐 내용의 memberDTO 리턴, 수정 실패하면 원래 내용의 memberDTO return
	public synchronized boolean editMemberInfo(MemberDTO memberDTO) throws SQLException {
		try {
			conn = dbConn.getConn();
			query = new StringBuffer();
			query.append("UPDATE member ");
			query.append("SET name = ?, ");
			query.append(" nickname = ?, ");
			query.append(" tel = ?, ");
			query.append(" email = ?, ");
			query.append(" question = ?, ");
			query.append(" answer = ? ");
			query.append("WHERE id = ?");

			pstmt = conn.prepareStatement(query.toString());

			pstmt.setString(1, memberDTO.getName());
			pstmt.setString(2, memberDTO.getNickname());
			pstmt.setString(3, memberDTO.getTel());
			pstmt.setString(4, memberDTO.getEmail());
			pstmt.setString(5, memberDTO.getQuestion());
			pstmt.setString(6, memberDTO.getAnswer());
			pstmt.setString(7, memberDTO.getId());

			pstmt.executeUpdate();

			if (pstmt.executeUpdate() != 1) {
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("회원정보 수정 오류 \n" + e.getMessage());
			return false;
		} finally {
			disconnectPstmt();
		}
	}

	// 회원탈퇴
	public synchronized boolean deleteMember(MemberDTO memberDTO) throws SQLException {
		try {

			conn = dbConn.getConn();
			query = new StringBuffer();
			query.append("DELETE FROM member ");
			query.append("WHERE id = ?");

			pstmt = conn.prepareStatement(query.toString());
			pstmt.setString(1, memberDTO.getId());
			pstmt.executeUpdate();

			if (pstmt.executeUpdate() != 1) {
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("회원탈퇴 오류\n" + e.getMessage());
			return false;
		} finally {
			disconnectPstmt();
		}
	}

	// 주소추가
	public synchronized boolean newAddress(MemberDTO mdto) throws SQLException {
		try {
			conn = dbConn.getConn();
			query = new StringBuffer();
			query.append("update member set ");
			query.append("address = ?, address_x = ?, address_y = ? where id = '" + mdto.getId() + "'");

			pstmt = conn.prepareStatement(query.toString());

			pstmt.setString(1, mdto.getAddress());
			pstmt.setDouble(2, mdto.getAddressX());
			pstmt.setDouble(3, mdto.getAddressY());

			if (pstmt.executeUpdate() != 1) {
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			System.out.println("주소추가에러");
			return false;
		} finally {
			disconnectPstmt();
		}
	}

	// 주소 가져오기
	public synchronized MemberDTO getAddressInfo(String id) throws SQLException {
		try {
			conn = dbConn.getConn();
			stmt = conn.createStatement();
			query = new StringBuffer();

			query.append("SELECT * FROM member ");
			query.append("WHERE id = '" + id + "' and address IS NOT null");

			rs = stmt.executeQuery(query.toString());

			int cnt = 0;
			MemberDTO mdto = new MemberDTO();
			while (rs.next()) {
				mdto.setId(rs.getString("id"));
				mdto.setAddressX(rs.getDouble("address_x"));
				mdto.setAddressX(rs.getDouble("address_y"));
				mdto.setAddress(rs.getString("address"));
				cnt++;
			}

			if (cnt != 1) {
				return null;
			}

			return mdto;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("주소가져오기 에러 \n" + e.getMessage());
			return null;
		} finally {
			disconnectStmt();
		}
	}

	// 주소가 추가되어 있는지 확인하는 메서드
	public synchronized boolean isAddressAdded(String userId) throws SQLException {
		try {
			conn = dbConn.getConn();
			query = new StringBuffer();

			query.append("select * from member where address is not null AND id = ?");// userId

			pstmt = conn.prepareStatement(query.toString());
			pstmt.setString(1, userId);
			rs = pstmt.executeQuery();

			int cnt = 0;
			while (rs.next()) {
				cnt++;
			}

			if (cnt == 0) { // address컬럼이 null이면
				return false;
			}
			return true;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("주소가 추가되어 있는지 확인하는 메서드 isAddressAdded() 오류 " + e.getMessage());
			return false;
		} finally {
			disconnectPstmt();
		}
	}

	// 주소추가
	public synchronized boolean newAddress(Double x, Double y, String address, String userId) throws SQLException {
		try {
			conn = dbConn.getConn();
			query = new StringBuffer();
			query.append("update member set ");
			query.append("address = ?, address_x = ?, address_y = ? where id = ?");

			pstmt = conn.prepareStatement(query.toString());

			pstmt.setString(1, address);
			pstmt.setDouble(2, x);
			pstmt.setDouble(3, y);
			pstmt.setString(4, userId);

			if (pstmt.executeUpdate() != 1) {
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			System.out.println("주소추가에러 " + e.getMessage());
			return false;
		} finally {
			disconnectPstmt();
		}
	}

}