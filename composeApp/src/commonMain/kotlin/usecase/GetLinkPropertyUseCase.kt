package usecase

import model.LinkProperty
import repository.LinksRepository

class GetLinkPropertyUseCase(private val linksRepository: LinksRepository) {
    operator fun invoke(linkProperty: LinkProperty) =
        linksRepository.getLinkPropertyByIdAsFlow(linkProperty.id)
}
