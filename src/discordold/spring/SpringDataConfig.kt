package dev.slne.discordold.spring

import dev.slne.discordold.config.botConfig
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Role
import org.springframework.jdbc.datasource.DriverManagerDataSource
import java.sql.SQLException
import javax.sql.DataSource

@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
class SpringDataConfig {

    private val log = ComponentLogger.logger()

    @Bean
    @Primary
    fun dataSource(): DataSource {
        val databaseConfig = botConfig.database
        val dataSource = DriverManagerDataSource()

        with(dataSource) {
            setDriverClassName("org.mariadb.jdbc.Driver")
            username = databaseConfig.username
            password = databaseConfig.password
            url =
                "jdbc:mariadb://${databaseConfig.hostname}:${databaseConfig.port}/${databaseConfig.database}"
        }

        return dataSource
    }

    private fun validateDatasource(dataSource: DriverManagerDataSource) {
        try {
            dataSource.connection.use {
                log.info("Database connection established.")
            }
        } catch (e: SQLException) {
            val message = buildString {
                appendLine("Failed to tryEstablishConnection to the database.")
                appendLine("The database connection could not be established using the provided configuration.")
                appendLine("Database URL: ${dataSource.url}")
                appendLine("Username: ${dataSource.username}")
                appendLine("Password set: ${dataSource.password != null}")
                appendLine("Check if the database server is running and accessible.")
                appendLine("Verify that the database URL, username, and password are correct.")
                appendLine("Ensure that the database driver (org.mariadb.jdbc.Driver) is compatible.")
            }

            throw IllegalStateException(message, e)
        }
    }
}