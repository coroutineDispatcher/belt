package di

import datasource.LinkDatasource
import io.kamel.core.config.DefaultCacheSize
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default
import io.kamel.image.config.imageBitmapDecoder
import io.ktor.client.HttpClient
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import model.LinkProperty
import repository.LinksRepository
import usecase.AddUrlToDatabaseUseCase
import usecase.DeleteItemUseCase
import usecase.IsValidUrlUseCase
import usecase.ObserveLinkPropertiesUseCase
import usecase.ToggleFavouriteItemUseCase
import viewmodel.main.MainViewModel

object BeltAppDI {
    private val realm by lazy {
        Realm.open(RealmConfiguration.create(schema = setOf(LinkProperty::class)))
    }
    private val httpClient by lazy { HttpClient() }
    private val linkDatasource by lazy { LinkDatasource(httpClient, realm) }
    private val linkRepository by lazy { LinksRepository(linkDatasource) }
    private val toggleFavouriteItemUseCase by lazy { ToggleFavouriteItemUseCase(linkRepository) }
    private val observeLinkPropertiesUseCase by lazy { ObserveLinkPropertiesUseCase(linkRepository) }
    private val isValidUrlUseCase by lazy { IsValidUrlUseCase(linkRepository) }
    private val addUrlToDatabaseUseCase by lazy { AddUrlToDatabaseUseCase(linkRepository) }
    private val deleteItemUseCase by lazy { DeleteItemUseCase(linkRepository) }

    val mainViewModel by lazy {
        MainViewModel(
            addUrlToDatabaseUseCase,
            isValidUrlUseCase,
            observeLinkPropertiesUseCase,
            toggleFavouriteItemUseCase,
            deleteItemUseCase
        )
    }

    val kamelConfig = KamelConfig {
        takeFrom(KamelConfig.Default)
        imageBitmapCacheSize = DefaultCacheSize
        imageBitmapDecoder()
    }
}
