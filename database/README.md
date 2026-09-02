## Database Setup

## Install MariaDB

### Windows

1. Download MariaDB Community Server: https://mariadb.com/downloads/
2. Run the installer.
3. Keep port `3306`.
4. Set a password for the MariaDB `root` user.
5. Install MariaDB as a Windows service.

### macOS

Install MariaDB with Homebrew:

```bash
brew install mariadb
```

Start it:

```bash
brew services start mariadb
```

## Create the Local Project Database

Start MariaDB before running the setup script.

Windows, open Command Prompt as Administrator:

```bash
net start MariaDB
```

macOS:

```bash
brew services start mariadb
```

From the project root, run:

```bash
./scripts/setup-db.sh
```

Enter the MariaDB `root` password. If `root` has no password, press Enter.

The script creates the `alexandria` database and user, creates all tables from `schema.sql`, and loads `data.sql`.

## Use the Application

After setup, start the Java application normally. MariaDB usually continues running in the background, including after a computer restart.

If the application cannot connect to the database, start MariaDB server using the command for your operating system above.

## Update the Schema

After changing `schema.sql` or `data.sql`, run from the project root:

```bash
./scripts/setup-db.sh
```

This deletes all data in Alexandria tables and creates the tables again. The database and user remain.

## Application Connection

```text
username: alexandria
password: alexandria
host: localhost
port: 3306
database: alexandria
```

## Docker Later

Later, MariaDB will run in Docker. The schema and DAO code stay the same; only the JDBC host changes from `localhost` to the MariaDB service name.
