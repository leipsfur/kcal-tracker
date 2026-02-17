package de.leipsfur.kcal_track.domain.model

object PortionUnit {
    const val GRAM = "g"
    const val MILLILITER = "ml"
    const val PIECE = "Stück"
    const val SLICE = "Scheibe"
    const val PORTION = "Portion"
    const val TABLESPOON = "EL"
    const val TEASPOON = "TL"

    val all: List<String> = listOf(GRAM, MILLILITER, PIECE, SLICE, PORTION, TABLESPOON, TEASPOON)
}
