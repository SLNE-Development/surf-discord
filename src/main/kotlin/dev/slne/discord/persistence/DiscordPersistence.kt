package dev.slne.discord.persistence

import dev.slne.discord.config.botConfig
import dev.slne.discord.persistence.external.Whitelist
import dev.slne.discord.ticket.Ticket
import dev.slne.discord.ticket.message.TicketMessage
import dev.slne.discord.ticket.message.attachment.TicketMessageAttachment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration
import org.hibernate.cfg.FetchSettings
import org.hibernate.cfg.JdbcSettings
import org.hibernate.cfg.SchemaToolingSettings
import org.hibernate.tool.schema.Action

val sessionFactory = DiscordPersistence.configureHibernate()

object DiscordPersistence {

    fun configureHibernate(): SessionFactory {
        val config = botConfig.database


        val configuration = Configuration()
        configuration.setProperty(JdbcSettings.JAKARTA_JDBC_DRIVER, "org.mariadb.jdbc.Driver")
        configuration.setProperty(
            JdbcSettings.JAKARTA_JDBC_URL,
            "jdbc:mariadb://${config!!.hostname}:${config.port}/${config.database}"
        )
        configuration.setProperty(JdbcSettings.JAKARTA_JDBC_USER, "${config.username}")
        configuration.setProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD, "${config.password}")
        configuration.setProperty(SchemaToolingSettings.HBM2DDL_AUTO, Action.ACTION_NONE)

        configuration.setProperty(JdbcSettings.SHOW_SQL, false)
        configuration.setProperty(JdbcSettings.FORMAT_SQL, true)
        configuration.setProperty(JdbcSettings.USE_SQL_COMMENTS, true)
        configuration.setProperty(FetchSettings.DEFAULT_BATCH_FETCH_SIZE, 50)
//        configuration.setProperty(StatisticsSettings.GENERATE_STATISTICS, true)

        configuration.addAnnotatedClass(Ticket::class.java)
        configuration.addAnnotatedClass(TicketMessage::class.java)
        configuration.addAnnotatedClass(TicketMessageAttachment::class.java)
        configuration.addAnnotatedClass(Whitelist::class.java)

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

suspend inline fun <reified T> Session.findAll(): List<T> = sessionFactory.withSession { session ->
    val query = session.criteriaBuilder.createQuery(T::class.java)
    val root = query.from(T::class.java)
    session.createQuery(query.select(root)).resultList
}

fun <T> Session.upsert(entity: T, persisted: T.() -> Boolean): T {
    if (entity.persisted()) {
        return merge(entity)
    } else {
        persist(entity)
        return entity
    }
}
