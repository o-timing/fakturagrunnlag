# Oppskrift

## Restore backup av databasen

```bash
cd ~/projects/orientering/o-timing/etiming-database
```

- Pakk ut backup-filen i `backups/`
- Endre `$DB_NAME` i `foo.pl` (TODO gi dette scriptet et bedre navn)
  - Nydalten2024_2024202408251925.bak (filnavn) = Nydalten2024_2024 (databasenavn)
  - (dette scriptet brukes for å kjøre restore av en database inne i docker containeren)
- endre i `volumes` i `docker-compose.yml` til å peke på utpakkede backup-filen som ble kopiert inn i backups
    - ```dockerfile
        volumes:
              - ./backups/Nydalten2024_2024202408251925.bak:/backup.bak:ro ```
      
- start databasen vha `docker compose up`
- i et annet shell; kjør `./list-files-in-backup.sh` (TODO gi dette scriptet et bedre navn)
  - (dette scriptet brukes for å trigge restore av databasen inne i docker containeren)
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

```sql
-- sjekk om det finnes noen andre spor etter ledige startnummer
SELECT *
FROM name
WHERE name = 'Ledig'
   OR ename = 'Startnr'
   OR status = 'V'
   OR team = 'NOTEAM';

SELECT *
FROM name
WHERE name IS NULL
   OR ename IS NULL;

DELETE
FROM name
WHERE name IS NULL
   OR ename IS NULL;
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
SELECT name.name, name.ename, ecard, ecard2, ecard3, ecard4, team.name, status.namestr, *
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

DELETE
FROM name
WHERE ename LIKE '%Ukjent%';

SELECT *
FROM name
WHERE ename LIKE '%Ukjent%';
````

## Opprett `otiming`-tabeller

```
mise run fakturagrunnlag "migrate-db"
```

## Legg inn leiebrikker

Populer `otiming_leiebrikker` med data fra `~/projects/orientering/o-timing/faktura2/src/test/resources/O-Timing Leiebrikker.csv`
ved å paste rett inn i tabellen i IntelliJ

## Last ned data fra eventor

Finn ut eventor sin arrangement-id (denne brukes for å få ut data fra Eventor )
```sql
SELECT id
FROM day;
```

Denne id'en legges inn i:
com.example.otiming.PopulateEventorTablesTests.getEventId

Nå er det klart for å laste ned fra eventor
Dette gjøres vha 
```
mise run fakturagrunnlag "fetch-data-from-eventor"
```

## Populer eventor-tabeller

Nå finnes all xml som trengs i databasen

Det neste som må gjøres er å tolke denne xml'en
dette gjøres vha å kjøre testene:

```
mise run fakturagrunnlag "populate-eventor-tables"
```

Bytt ut leiebrikkepris her:
com.example.otiming.OtimingFakturaRapportTests.LEIEBRIKKE_LEIE

## Lage excel rapport
kjør testen:

```
mise run fakturagrunnlag "generate-excel-report"
```

## TODO
- gjør det mulig å lese inn leiebrikker fra csv vha mise
- ta med leiebrikkepris i excelarket og bruk den i formel slik at den kan endres i excel-arket
- ta med alle kontigentene i excel-arket og bruk dem i en formel slik at det er mulig å endre dem i excel-arket
- ta med knytningen mellom kontigent og klasse og bruk det som en formel slik at det er mulig å endre dem i excel-arket
- mise target for å starte database
- se på spring shell
- script manuell sql


### Løse notater:

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


