package usecase

import repository.LinksRepository

class DeleteTagUseCase(private val repository: LinksRepository) {
    suspend operator fun invoke(tag: String, linkPropertyToModify: model.LinkProperty) =
        repository.deleteTag(tag, linkPropertyToModify)
}
