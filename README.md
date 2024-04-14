# Schnack-Backend

## Einrichten der Entwicklungsumgebung

### Die Umgebungsvariable für das Datenbank-Passwort erstellen

- Die Windows Eingabeaufforderung öffnen und den folgenden Befehl ausführen: <br>
  `set SCHNACK_DATABASE_PASSWORD=EuerPasswort` (Wird nach dem schliessen der Konsole wieder gelöscht)
- In den Umgebungsvariablen der Windowseinstellungen den Eintrag vornehmen (Bleibt dauerhaft)

### (Datenbank Variante 1) Die MySQL-Datenbank in einen Docker Container erstellen

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) herunterladen und installieren
- Die Windows Eingabeaufforderung öffnen und den folgenden Befehl ausführen: <br>
  `docker run --name MySql -p 3306:3306 -v c/PfadAufEuremPC:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=EuerPasswort -d mysql:5.7`

### (Datenbank Variante 2) Die MySQL-Datenbank mit XAMPP erstellen

**Bitte den Abschnitt über die Konfiguration von MySQL mit XAMPP ergänzen.**

- [XAMPP](https://www.apachefriends.org/de/index.html) herunterladen und installieren
- MySQL konfigurieren und starten

### Das Datenbank-Schema schnack erstellen

Um das Projekt zu starten, muss das Schema Schnack vorhanden sein. Es kann mittels des Befehls `CREATE SCHEMA schnack;`
oder mithilfe des UIs von IntelliJ oder MySQL Workbench erstellt werden.

### Das Schnack-Backend Repository klonen und starten

- IntelliJ öffnen
- File -> New -> From Version Control...
- Auf der linken Seite GitHub auswählen und autorisieren, falls nötig.
- Das Projekt Schnack-Backend klonen
- Alle Dependencies mit Maven installieren, falls dies nicht automatisch geschieht.
- Das Projekt starten