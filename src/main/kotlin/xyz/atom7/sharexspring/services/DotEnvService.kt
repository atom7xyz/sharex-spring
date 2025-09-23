package xyz.atom7.sharexspring.services

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import xyz.atom7.sharexspring.logging.AppLogger
import java.io.File

@Service
class DotEnvService(
    logger: AppLogger
) {

    private var dotenv: Dotenv
    private val envFile = File(".env")

    init {
        envFile.createNewFile()

        if (envFile.readLines().isEmpty()) {
            try {
                val defaultEnvResource = ClassPathResource("defaults/.env")
                if (defaultEnvResource.exists()) {
                    defaultEnvResource.inputStream.use {
                        envFile.writeBytes(it.readAllBytes())
                    }
                    logger.info("Loaded default .env file")
                }
            } catch (e: Exception) {
                logger.warning("Could not copy default .env file: ${e.message}")
            }
        }

        dotenv = dotenv()
    }

    fun write(key: String, value: String) {
        val lines = envFile.readLines().toMutableList()
        var keyUpdated = false

        for (i in lines.indices) {
            val line = lines[i].trim()

            if (line.isEmpty() || line.startsWith("#")) {
                continue
            }

            if (line.startsWith("$key=")) {
                lines[i] = "$key=$value"
                keyUpdated = true
                break
            }
        }

        if (!keyUpdated) {
            lines.add("$key=$value")
        }

        envFile.writeText(lines.joinToString("\n"))
        dotenv = dotenv()
    }

    fun read(key: String): String {
        return dotenv.get(key)
    }

}