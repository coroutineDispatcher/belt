package model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import io.realm.kotlin.types.annotations.PrimaryKey

class LinkProperty : RealmObject {
    @PrimaryKey
    var id: RealmUUID = RealmUUID.random()
    var title: String = ""
    var image: String? = null
    var url: String = ""
    var createdAt: String = ""
    var updatedAt: String = ""
    var tags: RealmList<String> = realmListOf()
    var favorite: Boolean = false
}