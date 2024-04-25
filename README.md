TODO:
- Dokumenter fra start
    - gå inn i ~/projects/orientering/o-timing/etiming-database
    - pakk ut backup i backups
    - endre $DB_NAME i foo.pl
    - endre
        volumes:
              - ./backups/OCCløp4_2024202404232350.bak:/backup.bak:ro
      til å peke på riktig backup-fil i docker-compose.yml
    - start databasen vha "docker-compose up"
    - i et annet shell kjør:
        - ./list-files-in-backup.sh
    - sjekk om det er mulig å connecte fra IntelliJ
    - sjekk om database er OK
      - slett alle Ledige startnummer:
      
select count(*)
from name;

select count(*)
from name
where name = 'Ledig'
and ename = 'Startnr'
and status = 'V'
and team = 'NOTEAM' ;

delete
from name
where name = 'Ledig'
and ename = 'Startnr'
and status = 'V'
and team = 'NOTEAM' ;

select count(*)
from name;

- sjekk om det finnes mer enn et arrangement

select *
from arr;

- hvis det kun finnes ett arrangement knytt alle løpere til dette arrangementet

select *
from arr;

select count(*), arr, sub
from name
group by arr, sub ;

update name
set arr = 'N', SUB = '1'
where arr is null or SUB is null;

select count(*), arr, sub
from name
group by arr, sub ;

Sjekk at det ikke finnes noen løpere som ikke er knyttet til en gyldig klubb

select *
from name
where not exists (
select *
from team
where name.team = team.code
);

Sjekk om det finnes noen som ikke har noen brikke:

select ecard, ecard2, ecard3, ecard4, team.name, status.namestr, *
from name
left join team on (name.team = team.code)
left join status on (name.status = status.code)
where ecard is null
and ecard2 is null
and ecard3 is null
and ecard4 is null;

Lag otiming_leiebrikker:
Hvis tabellen ikke finne:

create table otiming_leiebrikker
(
brikkenummer int not null constraint otiming_leiebrikker_pk primary key,
eier         varchar(40) not null,
kortnavn     varchar(10),
kommentar    varchar(100)
);

Populer denne tabellen med data fra csv-fil
dette kan f.eks. gjøres ved å paste inn deler av fila inn i databasetabellen i intellij 

Finn ut eventorID:

select id
from day;

Denne brukes for å få ut data fra Eventor




undersøke hva de gamle scriptene gjorde og skriv ned en liste her

flytte backup-tingene inn under denne katalogen
Dokumentere hvordan man laster inn backup

sjekk av database
- hvis otiming-tabellene ikke finnes så må disse opprettes
    - otiming_leiebrikker
- slette alle Ledig Startnr
- sjekke at alle løperene har en brikke
    - det er greit å mangle brikke hvis status er Påmeldt (I)
