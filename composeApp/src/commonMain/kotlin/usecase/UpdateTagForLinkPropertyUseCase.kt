package usecase

import model.LinkProperty
import model.LinkTagOperation
import repository.LinksRepository

class UpdateTagForLinkPropertyUseCase(
    private val linksRepository: LinksRepository
) {
    suspend operator fun invoke(
        linkProperty: LinkProperty,
        newTag: String,
        operation: LinkTagOperation = LinkTagOperation.Add
    ) =
        linksRepository.updateTagForLinkProperty(linkProperty, newTag, operation)
}
