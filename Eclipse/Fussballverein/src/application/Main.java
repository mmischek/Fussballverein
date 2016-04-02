package application;
	
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.postgresql.ds.PGSimpleDataSource;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;



public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/application/View.fxml"));
			Scene scene = new Scene(root);
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
		

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
 