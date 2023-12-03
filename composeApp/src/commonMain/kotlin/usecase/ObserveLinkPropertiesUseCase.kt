package usecase

import model.Search
import repository.LinksRepository

class ObserveLinkPropertiesUseCase(
    private val repository: LinksRepository
) {
    operator fun invoke(search: Search) = repository.linkPropertiesObserver(search)
}
