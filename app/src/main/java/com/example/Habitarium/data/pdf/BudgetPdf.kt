package com.example.Habitarium.data.pdf

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PdfLine(
    val name: String,
    val company: String,
    val quantity: Int,
    val unitPrice: Double
)

object BudgetPdf {
    private const val PAGE_W = 595
    private const val PAGE_H = 842
    private const val MARGIN = 40f

    private const val NAME_W = 210
    private const val COMPANY_X = 262f
    private const val COMPANY_W = 100
    private const val QTY_RIGHT = 405f
    private const val UNIT_RIGHT = 480f
    private const val TOTAL_RIGHT = 555f

    private val GREEN = Color.parseColor("#1D9E75")
    private val GREEN_DARK = Color.parseColor("#0F6E56")
    private val GRAY = Color.parseColor("#6B6B6B")
    private val DIVIDER = Color.parseColor("#E0E0E0")

    private val money: NumberFormat =
        NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))

    private fun paint(size: Float, color: Int, bold: Boolean = false) =
        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = size
            this.color = color
            typeface = if (bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        }

    private fun layoutOf(text: String, paint: TextPaint, width: Int, maxLines: Int = 3) =
        StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
            .setMaxLines(maxLines)
            .setEllipsize(TextUtils.TruncateAt.END)
            .build()

    private fun Canvas.drawLayout(layout: StaticLayout, x: Float, y: Float) {
        save()
        translate(x, y)
        layout.draw(this)
        restore()
    }

    private fun Canvas.drawRight(text: String, right: Float, baseline: Float, paint: Paint) {
        drawText(text, right - paint.measureText(text), baseline, paint)
    }

    private fun drawHeader(canvas: Canvas): Float {
        val fill = Paint().apply { color = GREEN }
        canvas.drawRect(0f, 0f, PAGE_W.toFloat(), 80f, fill)
        canvas.drawText("Habitarium", MARGIN, 38f, paint(22f, Color.WHITE, bold = true))
        canvas.drawText("Solicitação de orçamento", MARGIN, 60f, paint(12f, Color.WHITE))
        val date = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")).format(Date())
        canvas.drawRight("Emitido em $date", PAGE_W - MARGIN, 60f, paint(10f, Color.WHITE))
        return 110f
    }

    private fun drawTableHeader(canvas: Canvas, y: Float): Float {
        val label = paint(9f, GRAY, bold = true)
        val base = y + 9f
        canvas.drawText("ITEM", MARGIN, base, label)
        canvas.drawText("EMPRESA", COMPANY_X, base, label)
        canvas.drawRight("QTD", QTY_RIGHT, base, label)
        canvas.drawRight("UNIT.", UNIT_RIGHT, base, label)
        canvas.drawRight("SUBTOTAL", TOTAL_RIGHT, base, label)
        val line = Paint().apply { color = DIVIDER; strokeWidth = 1f }
        canvas.drawLine(MARGIN, y + 16f, PAGE_W - MARGIN, y + 16f, line)
        return y + 20f
    }

    private fun drawFooter(canvas: Canvas, pageNumber: Int) {
        canvas.drawText(
            "Página $pageNumber",
            MARGIN,
            (PAGE_H - 24).toFloat(),
            paint(9f, GRAY)
        )
    }

    private fun build(lines: List<PdfLine>): PdfDocument {
        val doc = PdfDocument()
        var pageNumber = 1
        fun newPage() = doc.startPage(
            PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, pageNumber).create()
        )

        var page = newPage()
        var canvas = page.canvas
        var y = drawHeader(canvas)
        y = drawTableHeader(canvas, y)

        val body = paint(10f, Color.BLACK)
        val bodyBold = paint(10f, Color.BLACK, bold = true)
        val small = paint(9f, GRAY)
        val divider = Paint().apply { color = DIVIDER; strokeWidth = 0.5f }

        fun breakPage() {
            drawFooter(canvas, pageNumber)
            doc.finishPage(page)
            pageNumber++
            page = newPage()
            canvas = page.canvas
            y = drawTableHeader(canvas, MARGIN)
        }

        lines.forEach { line ->
            val nameLayout = layoutOf(line.name, body, NAME_W)
            val companyLayout = layoutOf(line.company, small, COMPANY_W, 2)
            val rowH = maxOf(nameLayout.height, companyLayout.height) + 14f
            if (y + rowH > PAGE_H - 70) breakPage()

            canvas.drawLayout(nameLayout, MARGIN, y + 4f)
            canvas.drawLayout(companyLayout, COMPANY_X, y + 4f)
            val baseline = y + 4f + body.textSize
            canvas.drawRight(line.quantity.toString(), QTY_RIGHT, baseline, body)
            canvas.drawRight(money.format(line.unitPrice), UNIT_RIGHT, baseline, body)
            canvas.drawRight(
                money.format(line.unitPrice * line.quantity),
                TOTAL_RIGHT, baseline, bodyBold
            )
            y += rowH
            canvas.drawLine(MARGIN, y, PAGE_W - MARGIN, y, divider)
        }

        if (y + 90 > PAGE_H - 50) breakPage()
        val total = lines.sumOf { it.unitPrice * it.quantity }
        y += 24f
        canvas.drawText("Total geral", UNIT_RIGHT - 120f, y, paint(13f, Color.BLACK, bold = true))
        canvas.drawRight(money.format(total), TOTAL_RIGHT, y, paint(14f, GREEN_DARK, bold = true))
        y += 28f
        val note = layoutOf(
            "Valores de demonstração, sujeitos à confirmação do fornecedor.",
            paint(9f, GRAY), PAGE_W - (MARGIN * 2).toInt(), 2
        )
        canvas.drawLayout(note, MARGIN, y)

        drawFooter(canvas, pageNumber)
        doc.finishPage(page)
        return doc
    }

    /** Salva em Downloads (Android 10+) ou nos Documentos do app (versões anteriores). */
    fun saveToDownloads(context: Context, lines: List<PdfLine>): Uri? {
        val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val fileName = "Orcamento_Habitarium_$stamp.pdf"
        val doc = build(lines)
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                    ?: return null
                resolver.openOutputStream(uri)?.use { doc.writeTo(it) } ?: return null
                uri
            } else {
                val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                    ?: return null
                val file = File(dir, fileName)
                file.outputStream().use { doc.writeTo(it) }
                Uri.fromFile(file)
            }
        } catch (e: Exception) {
            android.util.Log.e("BudgetPdf", "Falha ao salvar PDF", e)
            null
        } finally {
            doc.close()
        }
    }
}