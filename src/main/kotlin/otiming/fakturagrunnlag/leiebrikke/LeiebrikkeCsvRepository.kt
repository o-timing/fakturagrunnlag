package otiming.fakturagrunnlag.leiebrikke

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import otiming.fakturagrunnlag.Brikkenummer
import java.io.BufferedReader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object LeiebrikkeCsvRepository {
    fun readLeiebrikkeCsv(filename: String): List<LeiebrikkeCsvRow> {
        val currentWorkingDir: Path = Paths.get("").toAbsolutePath()

        val reader: BufferedReader =
            Files.newBufferedReader(currentWorkingDir.resolve(filename))
        val records: Iterable<CSVRecord> =
            CSVFormat.RFC4180.builder().setHeader().setSkipHeaderRecord(true).build().parse(reader)

        val leiebrikker: List<LeiebrikkeCsvRow> = records.map { record ->
            LeiebrikkeCsvRow(
                kortnavn = record["kortnavn"],
                brikkenummer = Brikkenummer(record["brikkenummer"]),
                eier = record["eier"],
                kommentar = record["kommentar"]
            )
        }
        return leiebrikker
    }
}