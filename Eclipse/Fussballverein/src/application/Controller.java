package application;


import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

import org.postgresql.ds.PGSimpleDataSource;
import org.postgresql.util.PSQLException;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

/**
 * 
 * @author Matthias Mischek
 * Controller
 */
public class Controller implements Initializable{

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
	
	@SuppressWarnings("rawtypes")
	private ObservableList<ObservableList> data;
	
	@SuppressWarnings("rawtypes")
	@FXML
	private TableView ausgabe;
	
	@FXML
	private TextField eingabe;
	
	@FXML
	private Button start, connectButton;
	
	@FXML
	private TextField ip, dbName, user, passwort;
	
	@FXML
	private Label status, selectText;
	
	@FXML
	private Tab abfragePane, updatePane;
	
	private PGSimpleDataSource ds;


	public void connect(ActionEvent event){
		
		data = FXCollections.observableArrayList();
		
		// Datenquelle erzeugen und konfigurieren
		ds = new PGSimpleDataSource();
		ds.setServerName(ip.getText());
		ds.setDatabaseName(dbName.getText());
		ds.setUser(user.getText());
		ds.setPassword(passwort.getText());
		// Verbindung herstellen
		try(
			Connection con = ds.getConnection();
			
			){
			
			status.setText("Verbunden!");
			abfragePane.setDisable(false);
			updatePane.setDisable(false);
			
			}catch (Exception se){
			System.err.println("Error");
			status.setText("Bitter erneut versuchen.");
			
		}
		
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void abfrage(ActionEvent event){
		
		try(
			Connection con = ds.getConnection();
			// Abfrage vorbereiten und ausführen
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(eingabe.getText());
			){
			
			
			for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                final int j = i;
				TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {                                                                                              
                        return new SimpleStringProperty(param.getValue().get(j).toString());                        
                    }                    
                });

                ausgabe.getColumns().addAll(col); 
    
            }
			
			// Ergebnisse verarbeiten
			while (rs.next()) { // Cursor bewegen
				ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                    //Iterate Column
                    row.add(rs.getString(i));
                    
                }
              
                data.add(row);
			}
		
			ausgabe.setItems(data);
			
		}catch (PSQLException se){
			System.err.println("Error");
			selectText.setText("Error. Bitte korrigieren Sie ihre Eingabe.");
			
		}
		catch (Exception se){
			System.err.println("Error");
			selectText.setText("Error. Bitte korrigieren Sie ihre Eingabe.");
			
		}
		
		
		
	}
	
	
	

	
	

}