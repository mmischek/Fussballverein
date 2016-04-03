package application;


import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
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
	private TableView ausgabe, updateTable, anzeige;
	
	@FXML
	private TextField eingabe, updateTextfield;
	
	@FXML
	private Button start, connectButton, auswahl, updateB, auswahlB;
	
	@FXML
	private TextField ip, dbName, user, passwort, insnname, insvname, insNr, pnr, vname, nname;
	
	@FXML
	private Label status, selectText, hinzText;
	
	@FXML
	private Tab abfragePane, updatePane, anzeigePane, insertPane;
	
	private PGSimpleDataSource ds;
	
	@FXML
	private ComboBox updateBox;
	
	private String[] updatear;
	
	private Connection con;


	public void connect(ActionEvent event){
		
		data = FXCollections.observableArrayList();
		
		// Datenquelle erzeugen und konfigurieren
		ds = new PGSimpleDataSource();
		ds.setServerName(ip.getText());
		ds.setDatabaseName(dbName.getText());
		ds.setUser(user.getText());
		ds.setPassword(passwort.getText());
		// Verbindung herstellen
		try
		
		{
			
			this.con = ds.getConnection();
			
			status.setText("Verbunden!");
			abfragePane.setDisable(false);
			updatePane.setDisable(false);
			anzeigePane.setDisable(false);
			insertPane.setDisable(false);
			
			}catch (Exception se){
			System.err.println("Error");
			status.setText("Bitter erneut versuchen.");
			
		}
		
	}
	


	
	
	public void insert(ActionEvent event){
		if (insNr.getText().matches("^-?\\d+$")) {

		
			   int num = Integer.parseInt(insNr.getText());
			   String vnam = insvname.getText();
			   String nnam = insnname.getText();

		
		try{
			
			Statement st = con.createStatement();
			st.executeUpdate("INSERT INTO Person VALUES (" + num + ", '" + vnam + "', '" + nnam + "')");
			hinzText.setText("erfolgreich hinzugefügt!");
		
			
			
		}catch (PSQLException se){
			System.err.println("Error");
			selectText.setText("Error. Bitte korrigieren Sie ihre Eingabe.");
			hinzText.setText("INSERT ERROR!");
			
		}
		catch (Exception se){
			System.err.println("Error");
			selectText.setText("Error. Bitte korrigieren Sie ihre Eingabe.");
			hinzText.setText("INSERT ERROR!");
			
		}
			
		
	}else{
		hinzText.setText("Bitte für die Nummer nur Zahlen eingeben!");
		
	}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void anzeige(ActionEvent event){
		
		try{
			
			con = ds.getConnection();
			// Abfrage vorbereiten und ausführen
			Statement st = con.createStatement();
				ResultSet rs = st.executeQuery("select * from Person");
			
			pnr.setDisable(false);
			vname.setDisable(false);
			nname.setDisable(false);
			auswahlB.setDisable(false);
			
			for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                final int j = i;
				TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {                                                                                              
                        return new SimpleStringProperty(param.getValue().get(j).toString());                        
                    }                    
                });

                anzeige.getColumns().addAll(col); 
              
    
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
			updateBox.setItems(data);
			anzeige.setItems(data);
			
			
			
		}catch (PSQLException se){
			System.err.println("Error");
			selectText.setText("Error. Bitte korrigieren Sie ihre Eingabe.");
			
		}
		catch (Exception se){
			System.err.println("Error");
			selectText.setText("Error. Bitte korrigieren Sie ihre Eingabe.");
			
		}
	
	
	
	

	}
	
	@FXML
	public void update(ActionEvent event){

		try{
			Connection con = ds.getConnection();
			// Abfrage vorbereiten und ausführen
			Statement st = con.createStatement();
			st.executeUpdate("UPDATE person SET vorname=\'"+vname.getText()+"\', nachname=\'"+nname.getText()+"\', nummer=\'"+pnr.getText()+"\' WHERE nummer=\'"+updatear[0]+"\'");

			System.out.println("Update successful");

		} catch (SQLException se){
			System.err.println("Update - Error");
			se.printStackTrace(System.err);
		}

	}
	
	
	@FXML
	public void auswahl(ActionEvent event){

		String input = updateBox.getValue()+"";

		input = input.substring(1,input.length()-1);

		updatear = input.split(", ");

		System.out.println(input);

		updateB.setDisable(false);

		pnr.setText(updatear[0]);
		vname.setText(updatear[1]);
		nname.setText(updatear[2]);

		//bezeichnungTF.setDisable(false);
		//gewichtTF.setDisable(false);

	}
	
	
	@FXML
	public void aktualisieren(ActionEvent event){

		anzeige.getColumns().clear();

		data.clear();
		data = FXCollections.observableArrayList();
		connect(null);

	}
	
	











@SuppressWarnings({ "unchecked", "rawtypes" })
public void abfrage(ActionEvent event){
	
	try{
		Connection con = ds.getConnection();
		// Abfrage vorbereiten und ausführen
		Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select " + eingabe.getText());
		
		for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
            final int j = i;
			TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
            col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
                public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {                                                                                              
                    return new SimpleStringProperty(param.getValue().get(j).toString());                        
                }                    
            });

            ausgabe.getColumns().addAll(col); 
            updateTable.getColumns().addAll(col); 

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
		updateTable.setItems(data);
		
		
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
	

	
	

