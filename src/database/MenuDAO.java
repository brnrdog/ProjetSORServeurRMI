package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import beans.Element;
import beans.Menu;

public class MenuDAO extends BaseDonnees{

	public ArrayList<Menu> findAll() throws SQLException {
		ArrayList<Menu> menus = new ArrayList<Menu>();
		String sql = "";
		sql += "SELECT * FROM menu";
		open();
		PreparedStatement ps = co.prepareStatement(sql);
		System.out.println("EXECUTING QUERY: " + ps);
		ResultSet rs = ps.executeQuery();
		while(rs.next()) {
			Menu menu = new Menu();
			menu.setNom(rs.getString("nom"));
			menu.setExpiration(rs.getDate("expiration"));
			menu.setId(rs.getInt("id"));
			addElements(menu);
			menus.add(menu);
		}
		close();
		return menus;
	}
	
	public void createMenu(Menu m) throws SQLException {
		String sql = "INSERT INTO menu ";
		sql += "(nom, expiration)";
		sql += "VALUES (?, ?)";
		open();
		PreparedStatement ps = co.prepareStatement(sql);
		ps.setString(1, m.getNom());
		ps.setDate(2, m.getExpiration());
		System.out.println("CREATING MENU... " + ps);
		ps.executeUpdate();
		for (Element element : m.getElements()) {
			sql = "SELECT id FROM element WHERE nom=?";
			PreparedStatement ps2 = co.prepareStatement(sql);
			ps2.setString(1, element.getNom());
			ResultSet rs = ps2.executeQuery();
			rs.next();
			int id = rs.getInt("id");
			sql = "INSERT INTO menus_elements (menu_id, element_id) VALUES (LAST_INSERT_ID(),?)";
			PreparedStatement ps3 = co.prepareStatement(sql);
			ps3.setInt(1,id);
			ps3.executeUpdate();
		}
		close();
	}
	
	public void updateMenu(Menu m) throws SQLException {
		String sql = "UPDATE menu ";
		sql += "SET nom = ? ";
		sql += "expiration = ? ";
		sql += "WHERE id = ?";
		open();
		PreparedStatement ps = co.prepareStatement(sql);
		ps.setString(1, m.getNom());
		ps.setDate(2, m.getExpiration());
		ps.setInt(3, m.getId());
		ps.executeUpdate();
		close();
	}
	
	private void addElements(Menu m) throws SQLException {
		String sql = "SELECT el.id, el.nom, el.image, el.type ";
		sql += "FROM (Menu m ";
		sql += "LEFT JOIN menus_elements mel ";
		sql += "ON m.id = mel.menu_id) ";
		sql += "LEFT JOIN element el ";
		sql += "ON mel.element_id = el.id ";
		sql += "WHERE m.id = ?";
		PreparedStatement ps = co.prepareStatement(sql);
		ps.setInt(1, m.getId());
		ResultSet rs = ps.executeQuery();
		while(rs.next()) {
			Element el = new Element();
            el.setId(rs.getInt("id"));
			el.setNom(rs.getString("nom"));
			el.setImage(rs.getBytes("image"));
			el.setType(rs.getString("type"));
			m.addElement(el);
		}
	}
	
}
