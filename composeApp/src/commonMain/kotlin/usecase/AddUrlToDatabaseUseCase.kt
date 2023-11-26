package usecase

import repository.LinksRepository

class AddUrlToDatabaseUseCase(
    private val linksRepository: LinksRepository
) {
    suspend operator fun invoke(url: String) = linksRepository.tryAddToDb(url)
}