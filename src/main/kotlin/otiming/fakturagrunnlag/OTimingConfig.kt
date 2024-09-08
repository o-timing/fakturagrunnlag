package otiming.fakturagrunnlag

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "o-timing")
data class OTimingConfig(
    val eventor: EventorConfig,
    val emit: EmitConfig
)

data class EmitConfig(
    val databasenavn: String
)


data class EventorConfig(
    val apiKey: String
) {
    fun censoredApiKey() : String {
        if (apiKey.isBlank()) {
            return "<empty>"
        } else {
            return apiKey.take(2) +
                    "*".repeat(apiKey.length - 4) +
                    apiKey.takeLast(2)
        }
    }
}

