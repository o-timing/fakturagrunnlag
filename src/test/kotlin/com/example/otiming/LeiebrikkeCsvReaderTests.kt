package com.example.otiming

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.io.BufferedReader
import java.nio.file.Files
import java.nio.file.Paths


data class LeiebrikkeCsv(
    val kortnummer: String?,
    val brikkenummer: Brikkenummer,
    val brikkeeier: String?,
    val kommentar: String?
)

@SpringBootTest
class LeiebrikkeCsvReaderTests() {

    private fun readFile(filename: String): BufferedReader {
        val path = Paths.get(javaClass.classLoader.getResource(filename).toURI())
        return Files.newBufferedReader(path)
    }

    private fun readLeiebrikkeCsv(reader: BufferedReader): List<LeiebrikkeCsv> {
        val records: Iterable<CSVRecord> =
            CSVFormat.RFC4180.builder().setHeader().setSkipHeaderRecord(true).build().parse(reader)

        val foo = records.map { record ->
            LeiebrikkeCsv(
                kortnummer = record["kortnummer"],
                brikkenummer = Brikkenummer(record["brikkenummer"]),
                brikkeeier = record["brikkeeier"],
                kommentar = record["kommentar"]
            )
        }
        return foo
    }

    @Test
    fun testSlurpFile() {
        val reader = readFile("O-Timing Leiebrikker.csv")

        val leiebrikker: List<LeiebrikkeCsv> = readLeiebrikkeCsv(reader)

        println(leiebrikker.joinToString("\n"))
    }

}

