package usecase

import model.LinkProperty
import repository.LinksRepository

class ToggleFavouriteItemUseCase(private val repository: LinksRepository) {
    suspend operator fun invoke(linkProperty: LinkProperty) =
        repository.toggleFavouriteItem(linkProperty)
}