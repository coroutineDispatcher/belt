package usecase

import repository.LinksRepository

class IsValidUrlUseCase(private val linksRepository: LinksRepository) {
    operator fun invoke(url: String) = linksRepository.isValidUrl(url)
}
