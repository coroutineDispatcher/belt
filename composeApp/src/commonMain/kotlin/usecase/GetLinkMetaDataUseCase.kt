package usecase

import repository.LinksRepository

class GetLinkMetaDataUseCase(
    private val linksRepository: LinksRepository
) {
    suspend operator fun invoke(url: String) = linksRepository.getLinkMetadata(url)
}