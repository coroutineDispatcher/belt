package model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import io.realm.kotlin.types.annotations.PrimaryKey

class Tag : RealmObject {
    @PrimaryKey
    var id = RealmUUID.random()
    var name: String = ""
}
