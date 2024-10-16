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

val sessionFactory = DiscordPersistence.configureHibernate()

object DiscordPersistence {

    fun configureHibernate(): SessionFactory {
        val config = botConfig.database


        val configuration = Configuration()
        configuration.setProperty("hibernate.connection.driver_class", "org.mariadb.jdbc.Driver")
        configuration.setProperty(
            "hibernate.connection.url",
            "jdbc:mariadb://${config!!.hostname}:${config.port}/${config.database}"
        )
        configuration.setProperty("hibernate.connection.username", "${config.username}")
        configuration.setProperty("hibernate.connection.password", "${config.password}")
        configuration.setProperty("hibernate.hbm2ddl.auto", "none")

        configuration.setProperty("hibernate.show_sql", "false");
        configuration.setProperty("hibernate.format_sql", "true");
        configuration.setProperty("hibernate.use_sql_comments", "true");
//        configuration.setProperty("hibernate.generate_statistics", "true");

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
