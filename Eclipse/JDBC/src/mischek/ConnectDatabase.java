package mischek;

import java.sql.*;

import org.postgresql.ds.PGSimpleDataSource;

/**
 * 
 * @author Matthias Mischek
 *
 */
public class ConnectDatabase {
public static void main(String[] args) {
// Datenquelle erzeugen und konfigurieren
 PGSimpleDataSource ds = new PGSimpleDataSource();
 ds.setServerName("172.17.0.160");
 ds.setDatabaseName("schokofabrik");
 ds.setUser("schokouser");
 ds.setPassword("schokoUser");
 // Verbindung herstellen
 try(
		 Connection con = ds.getConnection();
		 // Abfrage vorbereiten und ausführen
		 Statement st = con.createStatement();
		 ResultSet rs = st.executeQuery("select * from produkt");){
 
 // Ergebnisse verarbeiten
 while (rs.next()) { // Cursor bewegen
  String wert = rs.getString(2);
  System.out.println(wert);
 }
 }catch (SQLException se){
  se.printStackTrace(System.err);
 }
 } 
}