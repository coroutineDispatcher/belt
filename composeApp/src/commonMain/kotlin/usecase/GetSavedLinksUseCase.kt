package usecase

import repository.LinksRepository

class GetSavedLinksUseCase(
    private val linksRepository: LinksRepository
) {
    operator fun invoke() = Unit // TODO
}