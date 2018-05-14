package service

import data.rbac.subject.Subject
import data.rbac.subject.SubjectDao
import org.apache.shiro.crypto.hash.Sha256Hash
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import service.exception.SubjectAlreadyExist
import java.sql.SQLException

class SubjectService(override val kodein: Kodein) : KodeinAware, Service<Subject, SubjectDao>() {
    enum class Selector(val value: String) {
        ID("id"), LOGIN("login")
    }

    override val dao: SubjectDao by instance()
    private val roleService: RoleService by instance()

    @Throws(SQLException::class, SubjectAlreadyExist::class)
    fun createSubject(login: String, password: String): Subject {

        findBy(Selector.LOGIN.value, login)?.let {
            throw SubjectAlreadyExist(login)
        }

        val subject = Subject()
        subject.login = login
        subject.password = Sha256Hash(password).toHex()
        dao.create(subject)

        return subject

    }


}



