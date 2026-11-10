package com.example.Habitarium.ui.screens

import java.text.Normalizer

val categoryGroups = listOf(
    "Drywall", "Steel Frame", "EIFS", "Cimentícia", "Siding",
    "Coberturas", "Isolamentos", "Fixação e químicos", "Ferramentas", "Outros"
)

private fun String.groupKey() =
    Normalizer.normalize(lowercase(), Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "")

fun categoryGroupOf(category: String): String {
    val c = category.groupKey()
    return when {
        "siding" in c -> "Siding"
        "eifs" in c -> "EIFS"
        "cimentici" in c || "limitadores" in c -> "Cimentícia"
        "drywall" in c -> "Drywall"
        "steel frame" in c || "paine" in c -> "Steel Frame"
        "cobertura" in c || "subtelha" in c || "telhado" in c -> "Coberturas"
        "isolamento" in c || "barreira" in c -> "Isolamentos"
        "parafuso" in c || "fixa" in c || "quimico" in c || "selante" in c -> "Fixação e químicos"
        "ferramenta" in c -> "Ferramentas"
        else -> "Outros"
    }
}