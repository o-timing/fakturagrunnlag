# Oppskrift

## Restore backup av databasen

```bash
cd ~/projects/orientering/o-timing/etiming-database
```

- Pakk ut backup-filen i `backups/`
- Endre `$DB_NAME` i `foo.pl` (TODO gi dette scriptet et bedre navn)
- endre i `volumes` i `docker-compose.yml` til å peke på utpakkede backup-filen som ble kopiert inn i backups
    - ```dockerfile
        volumes:
              - ./backups/OCCløp4_2024202404232350.bak:/backup.bak:ro ```
      
- start databasen vha `docker-compose up`
- i et annet shell; kjør `./list-files-in-backup.sh` (TODO gi dette scriptet et bedre navn)
- sjekk at det er mulig å connecte til databasen fra IntelliJ

## Klargjør databasen

### Slett alle ledige startnummer

```sql
-- tell antall deltakere før sletting
SELECT COUNT(*)
FROM name;

-- sjekk hvor mange ledige startnummer som finnes før sletting
SELECT COUNT(*)
FROM name
WHERE name = 'Ledig'
  AND ename = 'Startnr'
  AND status = 'V'
  AND team = 'NOTEAM';

-- slett alle ledige startnummer
DELETE
FROM name
WHERE name = 'Ledig'
  AND ename = 'Startnr'
  AND status = 'V'
  AND team = 'NOTEAM';

-- tell antall deltakere etter sletting
SELECT COUNT(*)
FROM name;
```

### Hvis det kun finnes ett arrangement; knytt alle løpere til dette arrangementet

```sql
-- list alle arrangement i databasen
SELECT *
FROM arr;

-- sjekk hvor mange deltakere som er knyttet til de ulike arrangementene 
-- og hvor mange som ikke er knyttet til noe arrangement 
SELECT COUNT(*), arr, sub
FROM name
GROUP BY arr, sub;

-- knytt alle deltakere som ikke er koblet
-- til noe arrangement til det ene arrangementet som finnes 
UPDATE name
SET arr = 'N',
    SUB = '1'
WHERE arr IS NULL
   OR SUB IS NULL;

-- sjekk hvor mange deltakere som er knyttet til de ulike arrangementene 
-- og hvor mange som ikke er knyttet til noe arrangement
--
-- foventningen er her at det kun skal være en rad som viser at alle 
-- deltakerene er koblet til det samme arrangementet
SELECT COUNT(*), arr, sub
FROM name
GROUP BY arr, sub;
```

### Sjekk om det finnes løpere som er knyttet til en ikke gyldig klubb

```sql
SELECT *
FROM name
WHERE NOT EXISTS (SELECT *
                  FROM team
                  WHERE name.team = team.code);
```

### Sjekk om det finnes noen som ikke har noen brikke

```sql
--
SELECT ecard, ecard2, ecard3, ecard4, team.name, status.namestr, *
FROM name
         LEFT JOIN team ON (name.team = team.code)
         LEFT JOIN status ON (name.status = status.code)
WHERE ecard IS NULL
  AND ecard2 IS NULL
  AND ecard3 IS NULL
  AND ecard4 IS NULL;
```

(Hva gjør vi med de som ikke har fått tildelt noen brikke?)

### Sjekk at alle ukjente løpere er koblet

```sql
SELECT *
FROM name
WHERE ename LIKE '%Ukjent%';

SELECT *
FROM name
WHERE status = 'U';

SELECT *
FROM name
WHERE ecard IN ('4295770', '4434957', '4338505');

DELETE
FROM name
WHERE ename LIKE '%Ukjent%';

SELECT *
FROM name
WHERE ename LIKE '%Ukjent%';
````

## Opprett `otiming`-tabeller

### Opprett `otiming_leiebrikker`-tabellen

```sql
CREATE TABLE otiming_leiebrikker
(
    brikkenummer INT         NOT NULL
        CONSTRAINT otiming_leiebrikker_pk PRIMARY KEY,
    eier         VARCHAR(40) NOT NULL,
    kortnavn     VARCHAR(10),
    kommentar    VARCHAR(100)
);
```

Populer denne tabellen med data fra `~/projects/orientering/o-timing/faktura2/src/test/resources/O-Timing Leiebrikker.csv`
ved å paste rett inn i tabellen i IntelliJ

### Opprett `otiming_eventor_raw`

```sql
CREATE TABLE otiming_eventor_raw
(
    eventId  INT           NOT NULL
        CONSTRAINT otiming_eventor_raw_pk PRIMARY KEY,
    endpoint VARCHAR(100)  NOT NULL,
    xml      NVARCHAR(MAX) NOT NULL,
    hentet   DATETIME2     NOT NULL
);
```

### Opprett `otiming_eventor_entryfees`

```sql
CREATE TABLE otiming_eventor_entryfees
(
    entryFeeId Int, -- primary key
    eventId    Int,
    name       NVARCHAR(100),
    amount     Int hentet DATETIME2 NOT NULL
);
```

### Opprett `otiming_eventor_entryfees`

```sql
CREATE TABLE otiming_eventor_entries
(
    brikkenummer INT         NOT NULL
        CONSTRAINT otiming_leiebrikker_pk PRIMARY KEY,
    eier         VARCHAR(40) NOT NULL,
    kortnavn     VARCHAR(10),
    kommentar    VARCHAR(100)
);
```

```sql
CREATE TABLE otiming_eventor_eventclasses
(
    brikkenummer INT         NOT NULL
        CONSTRAINT otiming_leiebrikker_pk PRIMARY KEY,
    eier         VARCHAR(40) NOT NULL,
    kortnavn     VARCHAR(10),
    kommentar    VARCHAR(100)
);
```

## Last ned data fra eventor

Finn ut eventor sin arrangement-id (denne brukes for å få ut data fra Eventor )
```sql
SELECT id
FROM day;
```


TODO:

- renskrive dokumentasjon til markdown
- eksempel på excel-ark:
    - https://docs.google.com/spreadsheets/d/1HS-Qj4o8iDZNx9pMIDrYx4tttK3JSmfa8jO4oSX23pc/edit#gid=481689688
- Dokumenter fra start



Hvordan kobler man personer i eventor og personer i etiming?

select kid, name, ename
from name;

"kid" er eventorId'en

Lese inn alle kontigenter fra eventor

koble dem sammen med løpere

undersøke hva de gamle scriptene gjorde og skriv ned en liste her

flytte backup-tingene inn under denne katalogen
Dokumentere hvordan man laster inn backup

sjekk av database

- hvis otiming-tabellene ikke finnes så må disse opprettes
    - otiming_leiebrikker
- slette alle Ledig Startnr
- sjekke at alle løperene har en brikke
    - det er greit å mangle brikke hvis status er Påmeldt (I)

sjekke alle som har brikkenummer < 4103640

select ecardfee, *
from name
where ecard < 4103640;


