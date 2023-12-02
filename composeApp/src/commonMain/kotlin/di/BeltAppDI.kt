package di

import datasource.LinkDatasource
import datasource.TagsDatasource
import io.kamel.core.config.DefaultCacheSize
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default
import io.kamel.image.config.imageBitmapDecoder
import io.ktor.client.HttpClient
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import model.LinkProperty
import model.Tag
import repository.LinksRepository
import usecase.AddUrlToDatabaseUseCase
import usecase.DeleteItemUseCase
import usecase.GetFilteredTagsUseCase
import usecase.GetLinkPropertyUseCase
import usecase.IsValidUrlUseCase
import usecase.ObserveLinkPropertiesUseCase
import usecase.ToggleFavouriteItemUseCase
import usecase.UpdateTagForLinkPropertyUseCase
import viewmodel.main.MainViewModel
import viewmodel.tags.TagsViewModel

object BeltAppDI {
    private val realm by lazy {
        Realm.open(RealmConfiguration.create(schema = setOf(LinkProperty::class, Tag::class)))
    }
    private val httpClient by lazy { HttpClient() }
    private val linkDatasource by lazy { LinkDatasource(httpClient, realm) }
    private val tagsDatasource by lazy { TagsDatasource(realm) }
    private val linkRepository by lazy { LinksRepository(linkDatasource, tagsDatasource) }
    private val toggleFavouriteItemUseCase by lazy { ToggleFavouriteItemUseCase(linkRepository) }
    private val observeLinkPropertiesUseCase by lazy { ObserveLinkPropertiesUseCase(linkRepository) }
    private val isValidUrlUseCase by lazy { IsValidUrlUseCase(linkRepository) }
    private val addUrlToDatabaseUseCase by lazy { AddUrlToDatabaseUseCase(linkRepository) }
    private val deleteItemUseCase by lazy { DeleteItemUseCase(linkRepository) }
    private val getTagsUseCase by lazy { GetFilteredTagsUseCase(linkRepository) }
    private val updateTagForLinkPropertyUseCase by lazy {
        UpdateTagForLinkPropertyUseCase(
            linkRepository
        )
    }
    private val getLinkPropertyUseCase by lazy { GetLinkPropertyUseCase(linkRepository) }

    fun mainViewModel() = MainViewModel(
        addUrlToDatabaseUseCase,
        isValidUrlUseCase,
        observeLinkPropertiesUseCase,
        toggleFavouriteItemUseCase,
        deleteItemUseCase,
        getTagsUseCase
    )

    fun tagsViewModel(linkProperty: LinkProperty) = TagsViewModel(
        getTagsUseCase,
        updateTagForLinkPropertyUseCase,
        linkProperty,
        getLinkPropertyUseCase
    )

    val kamelConfig = KamelConfig {
        takeFrom(KamelConfig.Default)
        imageBitmapCacheSize = DefaultCacheSize
        imageBitmapDecoder()
    }
}
