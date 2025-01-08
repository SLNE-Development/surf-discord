package dev.slne.discord.spring

import dev.slne.discord.config.botConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

@Configuration
class SpringDataConfig {
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
}