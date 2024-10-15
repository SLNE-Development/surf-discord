package dev.slne.discord.persistence

import dev.slne.discord.ticket.Ticket
import jakarta.persistence.Persistence
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration

val persistenceManager = Persistence.createEntityManagerFactory("discord")
val sessionFactory = DiscordPersistence.configureHibernate()

object DiscordPersistence {
    fun configureHibernate(): SessionFactory {
        val configuration = Configuration()
        configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
        configuration.setProperty(
            "hibernate.connection.url",
            "jdbc:postgresql://localhost:5432/mydb"
        )
        configuration.setProperty("hibernate.connection.username", "myuser")
        configuration.setProperty("hibernate.connection.password", "mypassword")
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
        configuration.setProperty("hibernate.hbm2ddl.auto", "update")
        configuration.setProperty("hibernate.show_sql", "true")


        configuration.addAnnotatedClass(Ticket::class.java)

        val serviceRegistry = StandardServiceRegistryBuilder()
            .applySettings(configuration.properties)
            .build()

        return configuration.buildSessionFactory(serviceRegistry)
    }
}


suspend fun <T> SessionFactory.withSession(block: suspend (session: Session) -> T): T =
    withContext(Dispatchers.IO) {
        val session = openSession()
        val transaction = session.beginTransaction()

        try {
            val result = block(session)
            transaction.commit()
            result
        } catch (e: Exception) {
            transaction.rollback()
            throw e
        } finally {
            session.close()
        }
    }
