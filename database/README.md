## MariaDB setup

To connect to the database, you need to set it up first.

### 1. Install MariaDB (Windows)

Download MariaDB Community Server if you do not have it yet:

https://mariadb.com/downloads/

During installation:

- keep the default port `3306`;
- create a `root` user;
- set a password for `root` if you want;
- install MariaDB as a Windows service.

### 2. Open MariaDB

Open PowerShell, Command Prompt, or Terminal and run one of these:

```bash
# use this if you set a password for root
mariadb -u root -p 

# use this  if you did not set a password for root
mariadb -u root 
```

### 3. Create the database

First, check the port. Run:

```sql
SHOW VARIABLES LIKE 'port';

```

The result should be `3306`.

Then, check the available collations. Run:

```sql
SHOW COLLATION WHERE Charset = 'utf8mb4';
```

If `utf8mb4_unicode_ci` is in the list, run:

```sql
CREATE DATABASE alexandria
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE alexandria;
```

If `utf8mb4_unicode_ci` is not on the list, go to AI and say:

*"My classmate asked me to run this: `CREATE DATABASE alexandria CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`, but my MariaDB doesn't support `utf8mb4_unicode_ci`. What should I replace it with?"*

Follow the instructions, create the database with a supported collation, notify your classmates about collation problem please and then run:

```sql
USE alexandria;
```

### 4. Create the database user

Run:

```sql
CREATE USER 'alexandria'@'localhost'
    IDENTIFIED BY 'alexandria';

GRANT ALL PRIVILEGES
    ON alexandria.*
    TO 'alexandria'@'localhost';

FLUSH PRIVILEGES;
```

Now you have the local database with specified credentials:

```text
username: alexandria
password: alexandria
host: localhost
port: 3306
database: alexandria
```

### 5. Create the tables

The database schema is stored in this project in `database/schema.sql`. Open it, copy the whole file, paste it into the MariaDB terminal, and press Enter to create all tables.

### 6. Check the tables

Run:

```sql
SHOW TABLES;
```

You should see:

```text
users
texts
search_results
text_statistics
term_analysis
text_comparisons
text_comparison_texts
term_comparisons
term_comparison_texts
quotations
```

### 7. Exit MariaDB

```sql
exit;
```

### 8. Connect as the project user

Next time, you can connect directly with:

```bash
mariadb -u alexandria -p
```

Enter the password:

```text
alexandria
```
---

If you're wondering why we create a separate `alexandria` user for MariaDB instead of using `root`:

- everyone in the team uses the same database credentials;
- the same credentials is used in the backend connection configuration;
- later, the same setup is easier to reproduce in deployment and automated environments.
