package application;


import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import org.postgresql.ds.PGSimpleDataSource;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Controller implements Initializable{

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
	
	@FXML
	private TextArea ausgabe;
	
	@FXML
	private TextField eingabe;
	
	@FXML
	private Button start;

	
	public void schoko(ActionEvent event){
		System.out.println("Ausgabe:");

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
				 ResultSet rs = st.executeQuery(eingabe.getText());){
		 
		 // Ergebnisse verarbeiten
		 while (rs.next()) { // Cursor bewegen
		  String wert = rs.getString(2);
		  ausgabe.setText(wert);
		  System.out.println(wert);
		 }
		 }catch (SQLException se){
		  se.printStackTrace(System.err);
		 }
		
		
	}
	
}
