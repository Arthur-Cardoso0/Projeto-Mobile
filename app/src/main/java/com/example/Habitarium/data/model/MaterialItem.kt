package com.example.Habitarium.data.model

import com.example.Habitarium.ui.theme.Product
import com.google.gson.annotations.SerializedName

data class MaterialItem(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("category") val category: String,
    @SerializedName("price") val price: Double,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("company") val company: String? = null,
    @SerializedName("brand") val brand: String? = null,
    @SerializedName("rating") val rating: Float? = null,
    @SerializedName("reviewCount") val reviewCount: Int? = null,
    @SerializedName("stock") val stock: Int? = null,
    @SerializedName("isSponsored") val isSponsored: Boolean? = null,
    @SerializedName("hasOwnInstallation") val hasOwnInstallation: Boolean? = null,
    @SerializedName("specs") val specs: Map<String, String>? = null
)

fun MaterialItem.toProduct(): Product = Product(
    id = id,
    name = name,
    price = price,
    category = category,
    company = company ?: "Habitarium",
    brand = brand ?: "",
    specs = specs ?: emptyMap(),
    stock = stock ?: 0,
    rating = rating ?: 0f,
    reviewCount = reviewCount ?: 0,
    isSponsored = isSponsored ?: false,
    hasOwnInstallation = hasOwnInstallation ?: false,
    imageUrl = imageUrl.sized()
)

private fun String.sized(width: Int = 400): String =
    if (contains("images.unsplash.com") && !contains("?"))
        "$this?w=$width&q=75&auto=format&fit=crop"
    else this