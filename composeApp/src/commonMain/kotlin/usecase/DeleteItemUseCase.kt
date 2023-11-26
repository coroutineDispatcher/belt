package usecase

import model.LinkProperty
import repository.LinksRepository

class DeleteItemUseCase(private val repository: LinksRepository) {
    suspend operator fun invoke(linkProperty: LinkProperty) = repository.deleteItem(linkProperty)
}
