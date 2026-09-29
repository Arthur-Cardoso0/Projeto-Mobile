package com.example.Habitarium

import com.example.Habitarium.ui.screens.catalog
import com.example.Habitarium.ui.screens.searchProducts
import com.example.Habitarium.ui.screens.currency
import org.junit.Assert.*
import org.junit.Test
import java.util.Locale

class CatalogSearchTest {
    @Test fun searchIgnoresCaseAccentsAndOuterWhitespace() {
        assertEquals(listOf("fita"), searchProducts("  ACUSTICA  ", "Todos", true).map { it.id })
    }
    @Test fun categoryAndQueryAreAppliedTogether() {
        assertTrue(searchProducts("cimenticia", "Drywall", true).isEmpty())
        assertEquals(3, searchProducts("cimenticia", "Steel Frame", true).size)
    }
    @Test fun sortingCanBeReversedWithoutDroppingProducts() {
        val ascending = searchProducts("", "Todos", true)
        assertEquals(catalog.size, ascending.size)
        assertEquals(ascending.reversed(), searchProducts("", "Todos", false))
        assertEquals("montante", ascending.first().id)
        assertEquals("eternit-15", ascending.last().id)
    }
    @Test fun emptyCategoriesAndUnknownTermsReturnNoResults() {
        assertTrue(searchProducts("", "Forros", true).isEmpty())
        assertTrue(searchProducts("inexistente", "Todos", true).isEmpty())
    }
    @Test fun pricesUseBrazilianFormattingRegardlessOfDeviceLocale() {
        val previous = Locale.getDefault()
        try {
            Locale.setDefault(Locale.US)
            assertTrue(currency(89.9).contains("89,90"))
            assertTrue(currency(89.9).contains("R$"))
        } finally { Locale.setDefault(previous) }
    }
}
