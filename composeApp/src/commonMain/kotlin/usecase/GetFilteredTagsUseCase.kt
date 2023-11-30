package usecase

import repository.LinksRepository

class GetFilteredTagsUseCase(
    private val linksRepository: LinksRepository
) {
    operator fun invoke(filter: String) = linksRepository.filteredTags(filter)
}
