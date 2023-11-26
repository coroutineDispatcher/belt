package usecase

import repository.LinksRepository

class ObserveLinkPropertiesUseCase(
    private val repository: LinksRepository
) {
    operator fun invoke() = repository.linkPropertiesObserver
}