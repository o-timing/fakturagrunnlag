package com.example.otiming

import org.springframework.jdbc.core.JdbcTemplate

object CheckIfTableExists {

    /**
     * Sjekker om gitt tabellnavn finnes i databasen
     *
     * @return true hvis den finnes, false hvis den ikke finnes
     */
    fun finnesTabell(jdbcTemplate: JdbcTemplate, tableName: String): Boolean {
        val count: Int = jdbcTemplate.queryForObject(
            """
                SELECT count(*)
                FROM sys.tables
                WHERE name = ? 
                  AND schema_name(schema_id) = 'dbo'
        """.trimIndent(),
            Integer::class.java,
            tableName
        )!!.toInt()
        return count == 1
    }
}