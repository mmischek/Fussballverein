package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import javafx.scene.control.ComboBox;
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
 * 
 */
public class Controller implements Initializable {

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("rawtypes")
	private ObservableList<ObservableList> data;

	@SuppressWarnings("rawtypes")
	@FXML
	private TableView ausgabe, anzeige;

	@FXML
	private TextField eingabe, updateTextfield;

	@FXML
	private Button start, connectButton, auswahl, updateB, auswahlB, anzeigeB, dbeinfugen, deleteB, insertB, delete;

	@FXML
	private TextField ip, dbName, user, passwort, insnname, insvname, insNr, pnr, cash, pos, von, bis, geg, manns, erg;

	@FXML
	private Label status, selectText, hinzText, status2;

	@FXML
	private Tab abfragePane, updatePane, anzeigePane, insertPane;

	private PGSimpleDataSource ds;

	@SuppressWarnings("rawtypes")
	@FXML
	private ComboBox updateBox, updateBox2;

	private String[] updatear;

	private Connection con;

	/**
	 * Methode zum Verbinden zur Datenbank Zugangsdaten kann der User für ein
	 * TextField eingeben
	 * 
	 * @param event
	 * @throws SQLException
	 */
	public void connect(ActionEvent event) throws SQLException {

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
			this.con = ds.getConnection(); // Verbinden
			status.setText("Verbunden!");
			abfragePane.setDisable(false);
			updatePane.setDisable(false);
			anzeigePane.setDisable(false);
			insertPane.setDisable(false);

			// Exception handling
		} catch (PSQLException e) {
			System.err.println("Error");
			status.setText("Bitter erneut versuchen.");

		} catch (Exception se) {
			con.rollback();
			System.err.println("Error");
			status.setText("Bitte erneut versuchen.");

		}

	}

	/**
	 * Methode für Insert Erstellt ein INSERT Statement und ließt Eingaben von
	 * User ein Ich habe die Tabelle "Spiele" von der Fussballverein DB verwendet
	 * 
	 * @param event
	 * @throws SQLException
	 */
	public void insert(ActionEvent event) throws SQLException {
		if (insNr.getText().matches("^-?\\d+$")) {

			//int num = Integer.parseInt(insNr.getText());
			String ergebnis = erg.getText();
			String gegner = geg.getText();
			String mannschaft = manns.getText();
			try {

				Statement st = con.createStatement();

				con.setAutoCommit(false);

				st.executeUpdate("INSERT INTO Spiel VALUES (" + gegner + ", '" + mannschaft + "', '" + ergebnis + "')");
				hinzText.setText("erfolgreich hinzugefügt!");

				con.commit();

			} catch (PSQLException se) {
				con.rollback();
				System.err.println("Error");
				selectText.setText("Error. Bitte korrigieren Sie ihre Eingabe.");
				hinzText.setText("INSERT ERROR!");

			} catch (Exception se) {
				con.rollback();
				System.err.println("Error");
				selectText.setText("Error. Bitte korrigieren Sie ihre Eingabe.");
				hinzText.setText("INSERT ERROR!");

			}

		} else {
			hinzText.setText("Bitte für die Nummer nur Zahlen eingeben!");

		}
	}

	/**
	 * Methode zum Laden der Tabelle Person und fügt diese in eine TableView ein
	 * 
	 * @param event
	 * @throws SQLException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void anzeige(ActionEvent event) throws SQLException {

		try {

			con = ds.getConnection();
			// Abfrage vorbereiten und ausführen
			Statement st = con.createStatement();
			con.setAutoCommit(false);
			ResultSet rs = st.executeQuery("select * from Spieler");
			anzeigeB.setDisable(true);
			dbeinfugen.setDisable(true);
			pnr.setDisable(false);
			pos.setDisable(false);
			cash.setDisable(false);
			auswahlB.setDisable(false);
			von.setDisable(false);
			bis.setDisable(false);

			for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
				final int j = i;
				TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
				col.setCellValueFactory(
						new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
							public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
								return new SimpleStringProperty(param.getValue().get(j).toString());
							}
						});

				anzeige.getColumns().addAll(col);

			}

			// Ergebnisse verarbeiten
			while (rs.next()) { // Cursor bewegen
				ObservableList<String> row = FXCollections.observableArrayList();
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					// Iterate Column
					row.add(rs.getString(i));

				}

				data.add(row);
			}
			updateBox.setItems(data);
			anzeige.setItems(data);
			con.commit();

		} catch (PSQLException se) {
			con.rollback();
			System.err.println("Error");
			selectText.setText("Error. Bitte korrigieren Sie ihre Eingabe.");

		} catch (Exception se) {
			con.rollback();
			System.err.println("Error");
			selectText.setText("Error. Bitte korrigieren Sie ihre Eingabe.");

		}

	}

	/**
	 * Methode für das Statement "Update" Ändert Datensätze
	 * 
	 * @param event
	 * @throws SQLException 
	 */
	@FXML
	public void update(ActionEvent event) throws SQLException {

		try {
			status2.setText("Datensatz wurde erfolgreich geaendert!");
			aktualisieren(event);
			Connection con = ds.getConnection();
			// Abfrage vorbereiten und ausführen
			Statement st = con.createStatement();
			con.setAutoCommit(false);
			st.executeUpdate("UPDATE Spieler SET position=\'" + pos.getText()
					+ "\', gehalt=\'" + cash.getText() + "\', von=\'" + von.getText() +"\', bis=\'" + bis.getText() + "\' WHERE persnr=\'" + updatear[0] + "\'");
			
		con.commit();
		} catch (SQLException se) {
			con.rollback();
			System.err.println("Update - Error");
			se.printStackTrace(System.err);
		}

	}

	/**
	 * Methode für das Statement "Delete" Löscht Datensätze aus der Tabelle
	 * Damit delete eines datensatzes in der Personen Tabelle musste ich zuerst
	 * den Verweis zu mitarbeiter in der Schokodb löschen
	 * 
	 * @param event
	 * @throws SQLException 
	
	@FXML
	public void delete(ActionEvent event) throws SQLException {

		try {
			status2.setText("Datensatz wurde erfolgreich geloescht!");
			aktualisieren(event);
			Connection con = ds.getConnection();
			// Abfrage vorbereiten und ausführen
			Statement st = con.createStatement();
			con.setAutoCommit(false);
			st.executeUpdate("DELETE FROM person WHERE nummer=\'" + updatear[0] + "\'");
			con.commit();

		} catch (SQLException se) {
			con.rollback();
			System.err.println("Delete - Error");
			se.printStackTrace(System.err);
		}

	}
	 */

	/**
	 * Methode für die Auswahl der Datensätze bei Update und Insert ComboBox
	 * gibt Daten TextField
	 * 
	 * @param event
	 */
	@FXML
	public void auswahl(ActionEvent event) {

		String input = updateBox.getValue() + "";
		input = input.substring(1, input.length() - 1);
		updatear = input.split(", ");
		updateB.setDisable(false);
		deleteB.setDisable(false);
		pnr.setText(updatear[0]);
		pos.setText(updatear[1]);
		cash.setText(updatear[2]);
		von.setText(updatear[3]);
		bis.setText(updatear[4]);
	
	}

	/**
	 * Löscht Anzeige und Tabelle
	 * 
	 * @param event
	 * @throws SQLException
	 */
	@FXML
	public void aktualisieren(ActionEvent event) throws SQLException {
		anzeigeB.setDisable(false);
		dbeinfugen.setDisable(false);
		anzeige.getColumns().clear();
		data.clear();
		data = FXCollections.observableArrayList();
		connect(null);

	}

	/**
	 * Methode für Eingabe von Select Befehlen
	 * 
	 * @param event
	 * @throws SQLException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void abfrage(ActionEvent event) throws SQLException {

		try {
			Connection con = ds.getConnection();
			// Abfrage vorbereiten und ausführen
			Statement st = con.createStatement();
			con.setAutoCommit(false);
			ResultSet rs = st.executeQuery("select " + eingabe.getText());

			for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
				final int j = i;
				TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
				col.setCellValueFactory(
						new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
							public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
								return new SimpleStringProperty(param.getValue().get(j).toString());
							}
						});

				ausgabe.getColumns().addAll(col);

			}

			// Ergebnisse verarbeiten
			while (rs.next()) { // Cursor bewegen
				ObservableList<String> row = FXCollections.observableArrayList();
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					// Iterate Column
					row.add(rs.getString(i));

				}

				data.add(row);
			}

			ausgabe.setItems(data);
			con.commit();

		} catch (PSQLException se) {
			con.rollback();
			System.err.println("Error PSQLException");
			selectText.setText("Error. Bitte korrigieren Sie ihre Eingabe.");

		} catch (Exception se) {
			con.rollback();
			System.err.println("Error Exception");
			selectText.setText("Error. Bitte korrigieren Sie ihre Eingabe.");

		}

	}
}
