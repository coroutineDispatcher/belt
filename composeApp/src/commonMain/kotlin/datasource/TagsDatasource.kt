package datasource

import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.RealmUUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import model.Tag

class TagsDatasource(private val realm: Realm) {

    fun tagsObservable(filter: String): Flow<List<Tag>> =
        realm.query<Tag>().asFlow().map { realmResult ->
            realmResult.list.filter { it.name.contains(filter) }
        }

    suspend fun addNewTag(tag: String) {
        if (tag.exists()) return

        val tagToInsert = Tag().apply {
            id = RealmUUID.random()
            name = tag
        }

        realm.write {
            copyToRealm(tagToInsert)
        }
    }

    private fun String.exists(): Boolean {
        val result = realm.query<Tag>("name == $0", this).first().find()
        return result != null
    }
}
