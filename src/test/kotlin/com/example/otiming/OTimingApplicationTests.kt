package com.example.otiming

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.nio.file.Files
import java.nio.file.Paths

@SpringBootTest
class OTimingApplicationTests {


	@Test
	fun contextLoads() {
		val eventorService = EventorFileService()

		println(eventorService.getEntries(18000))
		println(eventorService.getEntryFees(18000))
		println(eventorService.getEventClasses(18000))

	}

}
