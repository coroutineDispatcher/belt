package usecase

import model.LinkSearchOperation
import repository.LinksRepository

class ObserveLinkPropertiesUseCase(
    private val repository: LinksRepository
) {
    operator fun invoke(search: LinkSearchOperation) = repository.linkPropertiesObserver(search)
}
