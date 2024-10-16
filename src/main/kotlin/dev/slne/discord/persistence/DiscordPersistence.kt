package dev.slne.discord.persistence

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
        val configuration = Configuration()
        configuration.setProperty("hibernate.connection.driver_class", "org.mariadb.jdbc.Driver")
        configuration.setProperty(
            "hibernate.connection.url",
            "jdbc:mariadb://51.195.3.239:3306/s23_surf_dev"
        )
        configuration.setProperty("hibernate.connection.username", "u23_evmQtjuUbU")
        configuration.setProperty("hibernate.connection.password", "N.ooV2nNsM1POH+iPc9+3s43")
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MariaDBDialect")
        configuration.setProperty("hibernate.hbm2ddl.auto", "update")
        configuration.setProperty("hibernate.show_sql", "true")


        configuration.addAnnotatedClass(Ticket::class.java)
        configuration.addAnnotatedClass(TicketMessage::class.java)
        configuration.addAnnotatedClass(TicketMessageAttachment::class.java)

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

// TODO: 15.10.2024 17:54 - does this work?
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
