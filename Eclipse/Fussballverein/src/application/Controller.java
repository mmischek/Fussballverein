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
import javafx.scene.paint.Color;
import javafx.util.Callback;

/**
 * date: 18.04.2016
 * @author Matthias Mischek
 * @version 1.0 In dieser Klassen befinden sich alle wichtigen Methoden, welche
 *          von der GUI ausgeführt werden
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
	private TableView anzeige, anzeigeTwo;

	@FXML
	private TextField eingabe, updateTextfield, dateInsert, mannsInsert, gegInsert;

	@FXML
	private Button connectButton, auswahl, updateB, auswahlB, anzeigeB, dbeinfugen, deleteB, insertB, delete,
			dbanzeigen, dbeinfuegen, auswahlBspiel, updateBspiel;

	@FXML
	private TextField ip, dbName, user, passwort, insnname, insvname, insNr, pnr, cash, pos, von, bis, geg, manns, erg,
			dateField, bezField, gegField;

	@FXML
	private Label status, hinzText, status2, spiellabel, spielerlabel, statusSpiel;

	@FXML
	private Tab updatePane, anzeigePane, insertPane, updateSpielT, anzeigeSpielT;

	private PGSimpleDataSource ds;

	@SuppressWarnings("rawtypes")
	@FXML
	private ComboBox updateBox, updateBoxTwo, ergField, ergInsert;

	private String[] updatear;

	private Connection con;

	/**
	 * JDBC Methode zum Verbinden zur Datenbank Zugangsdaten kann der User für
	 * ein TextField eingeben
	 * 
	 * @param event
	 *            Wenn der Button "Verbinden" gedrückt wird
	 * @throws SQLException
	 *             Wirft SQL Exception
	 */
	@SuppressWarnings("unchecked")
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

			updatePane.setDisable(false);
			anzeigePane.setDisable(false);
			insertPane.setDisable(false);
			updateSpielT.setDisable(false);
			anzeigeSpielT.setDisable(false);

			ObservableList<String> stand = FXCollections.observableArrayList("Sieg", "Unentschieden", "Niederlage");

			ergField.setItems(stand);
			ergInsert.setItems(stand);
			status.setText("Erfolgreich Verbunden!");
			status.setTextFill(Color.GREEN);

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
	 * User ein; Ich habe die Tabelle "Spiele" von der Fussballverein DB
	 * verwendet
	 * 
	 * @param event
	 *            Wenn der Button "Insert" gedrückt wird
	 * @throws SQLException
	 *             	wegen Rollback
	 */
	public void insert(ActionEvent event) throws SQLException {

		String dateins = dateInsert.getText();

		String gegins = gegInsert.getText();
		String mannsIns = mannsInsert.getText();
		String ergIns = (String) ergInsert.getValue();

		try {

			Statement st = con.createStatement();

			con.setAutoCommit(false);

			st.executeUpdate("INSERT INTO Spiel VALUES ('" + dateins + "', '" + mannsIns + "', '" + gegins + "', '"
					+ ergIns + "')"); // Insert Statement

			hinzText.setText("erfolgreich hinzugefügt!");
			con.commit(); // Transaktion

		} catch (PSQLException se) {
			con.rollback();

			System.err.println("Error PSQL");

			hinzText.setText("INSERT ERROR! Bitte Eingabe prüfen!");

		} catch (Exception se) {
			con.rollback();
			System.err.println("Error");

			hinzText.setText("INSERT ERROR! Bitte Eingabe prüfen!");

		}
	}

	/**
	 * Methode zum Laden der Tabelle Person und fügt diese in eine TableView ein
 	 * Den Algorithmus für das Einfügen in eine TableView habe aus folgendem
	 * Forum:
	 * 
	 * link: http://stackoverflow.com/questions/36637532/javafx-using-dynamic-
	 *       table-view-with-database-editable-cell-problems
	 * @param event	wenn "DB anzeigen" geklickt wird
	 * @throws SQLException	wegen Rollback
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void anzeige(ActionEvent event) throws SQLException {

		try {
			aktualisierenTwo(event);
			con = ds.getConnection();
			// Abfrage vorbereiten und ausführen
			Statement st = con.createStatement();
			con.setAutoCommit(false);
			ResultSet rs = st.executeQuery("select * from Spieler");
			anzeigeB.setDisable(true);
			dbeinfugen.setDisable(true);
			auswahlB.setDisable(false);

			/**
			 * Den Algorithmus habe ich aus Folgendem Forum:
			 * 
			 * link: http://stackoverflow.com/questions/36637532/javafx-using-
			 *       dynamic- table-view-with-database-editable-cell-problems
			 * 
			 */

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

			// Ergebnisse verarbeiten9
			while (rs.next()) { // Cursor bewegen
				ObservableList<String> row = FXCollections.observableArrayList();
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					// Iterate Column
					row.add(rs.getString(i));

				}

				data.add(row);
			}
			updateBox.setItems(data);// In ComboBox einfügen
			anzeige.setItems(data);// In TableView einfügen
			con.commit(); // Transaktion

		} catch (PSQLException se) {
			con.rollback();
			System.err.println("Error");
			spielerlabel.setText("Error! Bitte kontaktieren Sie den Admin.");

		} catch (Exception se) {
			con.rollback();
			System.err.println("Error");
			spielerlabel.setText("Error! Bitte kontaktieren Sie den Admin.");

		}

	}

	/**
	 * Methode zum Laden der Tabelle Person und fügt diese in eine TableView ein
	 * Den Algorithmus für das Einfügen in eine TableView habe aus folgendem
	 * Forum:
	 * 
	 * link: http://stackoverflow.com/questions/36637532/javafx-using-dynamic-
	 *       table-view-with-database-editable-cell-problems
	 * @param event	wenn "DB anzeigen" geklickt wird
	 * @throws SQLException	wegen Rollback
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void anzeigeSpiel(ActionEvent event) throws SQLException {

		try {

			data = FXCollections.observableArrayList();
			con = ds.getConnection();
			// Abfrage vorbereiten und ausführen
			Statement st = con.createStatement();
			con.setAutoCommit(false);
			ResultSet res = st.executeQuery("select * from Spiel");
			dbanzeigen.setDisable(true);
			dbeinfuegen.setDisable(true);
			auswahlBspiel.setDisable(false);

			/**
			 * Den Algorithmus habe ich aus Folgendem Forum:
			 * 
			 * @link http://stackoverflow.com/questions/36637532/javafx-using-
			 *       dynamic- table-view-with-database-editable-cell-problems
			 * 
			 */

			for (int i = 0; i < res.getMetaData().getColumnCount(); i++) {
				final int j = i;
				TableColumn col = new TableColumn(res.getMetaData().getColumnName(i + 1));
				col.setCellValueFactory(
						new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
							public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
								return new SimpleStringProperty(param.getValue().get(j).toString());
							}
						});

				anzeigeTwo.getColumns().addAll(col);

			}

			// Ergebnisse verarbeiten
			while (res.next()) { // Cursor bewegen
				ObservableList<String> row = FXCollections.observableArrayList();
				for (int i = 1; i <= res.getMetaData().getColumnCount(); i++) {
					// Iterate Column
					row.add(res.getString(i));

				}

				data.add(row);
			}
			updateBoxTwo.setItems(data);// In ComboBox einfügen
			anzeigeTwo.setItems(data);// In TableView einfügen
			con.commit(); // Transaktion

		} catch (PSQLException se) {
			con.rollback();
			System.err.println("Error PSQL");
			spiellabel.setText("Error! Bitte kontaktieren Sie den Admin.");

		} catch (Exception se) {
			con.rollback();
			System.err.println("Error Exception");
			spiellabel.setText("Error! Bitte kontaktieren Sie den Admin.");

		}

	}

	/**
	 * Methode für das Statement "Update" Ändert Datensätze
	 * 
	 * @param event	Wenn "Update" geklickt werden
	 * @throws SQLException	wegen Rollback
	 */
	@FXML
	public void update(ActionEvent event) throws SQLException {

		try {

			aktualisieren(event);
			Connection con = ds.getConnection();
			// Abfrage vorbereiten und ausführen
			Statement st = con.createStatement();
			con.setAutoCommit(false);
			st.executeUpdate(
					"UPDATE Spieler SET position=\'" + pos.getText() + "\', gehalt=\'" + cash.getText() + "\', von=\'"
							+ von.getText() + "\', bis=\'" + bis.getText() + "\' WHERE persnr=\'" + updatear[0] + "\'");

			con.commit(); // Transaktion
			status2.setText("Datensatz wurde erfolgreich geaendert!");
		} catch (SQLException se) {
			con.rollback();
			System.err.println("Update - Error");
			status2.setText("Error! Bitte korrigieren Sie Ihre Eingabe.");

		} catch (Exception se) {
			con.rollback();
			System.err.println("Error Exception");
			status2.setText("Error! Bitte korrigieren Sie Ihre Eingabe.");

		}

	}

	/**
	 * Methode für das Statement "Update" Ändert Datensätze
	 * 
	 * @param event wenn "Update" geklickt wird
	 * @throws SQLException	wegen Rollback
	 */
	@FXML
	public void updateSpiel(ActionEvent event) throws SQLException {

		try {
			// status2.setText("Datensatz wurde erfolgreich geaendert!");
			aktualisierenTwo(event);
			Connection con = ds.getConnection();
			// Abfrage vorbereiten und ausführen
			Statement st = con.createStatement();
			con.setAutoCommit(false);
			st.executeUpdate("UPDATE Spiel SET datum=\'" + dateField.getText() + "\', bezeichnung=\'"
					+ bezField.getText() + "\', gegner=\'" + gegField.getText() + "\', ergebnis=\'"
					+ ergField.getValue() + "\' WHERE datum=\'" + updatear[0] + "\'");

			statusSpiel.setText("Der Datenatz wurde erfolgreich geändert.");
			con.commit(); // Transaktion
		} catch (SQLException se) {
			con.rollback();
			System.err.println("Update - Error");
			statusSpiel.setText("Error! Bitte korrigieren Sie Ihre Eingabe.");
		} catch (Exception se) {
			con.rollback();
			System.err.println("Update - Error");
			statusSpiel.setText("Error! Bitte korrigieren Sie Ihre Eingabe.");
		}

	}

	/**
	 * Methode für die Auswahl der Datensätze bei Update und Insert ComboBox
	 * gibt Daten TextField
	 * 
	 * @param event	wenn "Auswahl" gekilckt wird
	 */
	@FXML
	public void auswahl(ActionEvent event) {
		try {

			String input = updateBox.getValue() + "";
			input = input.substring(1, input.length() - 1);
			updatear = input.split(", ");
			updateB.setDisable(false);

			pnr.setText(updatear[0]);
			pos.setText(updatear[1]);
			cash.setText(updatear[2]);
			von.setText(updatear[3]);
			bis.setText(updatear[4]);
			pnr.setDisable(false);
			pos.setDisable(false);
			cash.setDisable(false);
			auswahlB.setDisable(false);
			von.setDisable(false);
			bis.setDisable(false);
		} catch (Exception se) {
			System.err.println("Update - Error");
			status2.setText("Error! Bitte wählen Sie zuerst einen Spieler aus.");

		}

	}

	/**
	 * Methode für die Auswahl der Datensätze bei Update und Insert ComboBox
	 * gibt Daten TextField
	 * 
	 * @param event	wenn "Auswahl" geklickt wird
	 */
	@SuppressWarnings("unchecked")
	@FXML
	public void auswahlTwo(ActionEvent event) {
		try {

			String input = updateBoxTwo.getValue() + "";
			input = input.substring(1, input.length() - 1);
			updatear = input.split(", ");

			dateField.setText(updatear[0]);
			bezField.setText(updatear[1]);
			gegField.setText(updatear[2]);
			ergField.setValue(updatear[3]);
			ergField.setDisable(false);
			dateField.setDisable(false);
			bezField.setDisable(false);
			gegField.setDisable(false);
			ergField.setDisable(false);
			updateBspiel.setDisable(false);
		} catch (Exception se) {
			System.err.println("Update - Error");
			statusSpiel.setText("Error! Bitte wählen Sie zuerst ein Spiel aus.");

		}

	}

	/**
	 * Löscht Anzeige und Tabelle aus TableView
	 * 
	 * @param event	wenn "anzeige löschen" geklickt wird
	 * @throws SQLException wegen rollback
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
	 * Löscht Anzeige und Tabelle aus TableView
	 * 
	 * @param event wenn "anzeige löschen" geklickt wird
	 * @throws SQLException wegenRollback
	 */
	@FXML
	public void aktualisierenTwo(ActionEvent event) throws SQLException {
		dbanzeigen.setDisable(false);
		dbeinfuegen.setDisable(false);
		anzeigeTwo.getColumns().clear();
		data.clear();
		data = FXCollections.observableArrayList();
		connect(null);

	}

}
