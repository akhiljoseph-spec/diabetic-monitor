package com.diabeticmonitor.app.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.diabeticmonitor.app.data.db.entity.GlucoseReading
import com.diabeticmonitor.app.data.db.entity.MedicationEntry
import com.diabeticmonitor.app.data.db.entity.UserProfile
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfExporter {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 40f

    fun generateAndShare(
        context: Context,
        readings: List<GlucoseReading>,
        medications: List<MedicationEntry>,
        profile: UserProfile?,
        onSuccess: (Intent) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val pdfDocument = PdfDocument()
            var pageNumber = 1
            var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
            var page = pdfDocument.startPage(pageInfo)
            var canvas: Canvas = page.canvas
            var yPos = MARGIN

            // Paints
            val titlePaint = Paint().apply {
                color = Color.parseColor("#1565C0")
                textSize = 22f
                isFakeBoldText = true
            }
            val headerPaint = Paint().apply {
                color = Color.parseColor("#283593")
                textSize = 14f
                isFakeBoldText = true
            }
            val bodyPaint = Paint().apply {
                color = Color.BLACK
                textSize = 11f
            }
            val subPaint = Paint().apply {
                color = Color.GRAY
                textSize = 10f
            }
            val linePaint = Paint().apply {
                color = Color.parseColor("#BDBDBD")
                strokeWidth = 1f
            }

            fun newPageIfNeeded(needed: Float) {
                if (yPos + needed > PAGE_HEIGHT - MARGIN) {
                    pdfDocument.finishPage(page)
                    pageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    yPos = MARGIN
                }
            }

            fun drawLine() {
                canvas.drawLine(MARGIN, yPos, PAGE_WIDTH - MARGIN, yPos, linePaint)
                yPos += 8f
            }

            // Title
            canvas.drawText("Diabetic Monitor Report", MARGIN, yPos, titlePaint)
            yPos += 28f
            canvas.drawText(
                "Generated: ${SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())}",
                MARGIN, yPos, subPaint
            )
            yPos += 20f
            drawLine()

            // Patient Info
            profile?.let { p ->
                canvas.drawText("Patient Information", MARGIN, yPos, headerPaint)
                yPos += 18f
                if (p.name.isNotEmpty()) {
                    canvas.drawText("Name: ${p.name}  |  Age: ${p.age}  |  Type: ${p.diabetesType}", MARGIN, yPos, bodyPaint)
                    yPos += 16f
                }
                if (p.doctorName.isNotEmpty()) {
                    canvas.drawText("Doctor: ${p.doctorName}", MARGIN, yPos, bodyPaint)
                    yPos += 16f
                }
                yPos += 4f
                drawLine()
            }

            // Stats
            if (readings.isNotEmpty()) {
                canvas.drawText("Glucose Summary", MARGIN, yPos, headerPaint)
                yPos += 18f
                val avg = readings.map { it.glucoseLevel }.average().toFloat()
                val min = readings.minOf { it.glucoseLevel }
                val max = readings.maxOf { it.glucoseLevel }
                canvas.drawText(
                    "Total Readings: ${readings.size}   Avg: ${"%.1f".format(avg)} mg/dL   Min: ${"%.1f".format(min)}   Max: ${"%.1f".format(max)}",
                    MARGIN, yPos, bodyPaint
                )
                yPos += 20f
                drawLine()

                // Readings Table
                canvas.drawText("Glucose Readings", MARGIN, yPos, headerPaint)
                yPos += 18f
                // Header row
                canvas.drawText("Date/Time", MARGIN, yPos, subPaint)
                canvas.drawText("Session", 180f, yPos, subPaint)
                canvas.drawText("Level", 340f, yPos, subPaint)
                canvas.drawText("Status", 420f, yPos, subPaint)
                yPos += 14f
                drawLine()

                readings.take(60).forEach { r ->
                    newPageIfNeeded(18f)
                    canvas.drawText(DateTimeUtils.formatDateTime(r.timestamp), MARGIN, yPos, bodyPaint)
                    canvas.drawText(r.sessionType, 180f, yPos, bodyPaint)
                    canvas.drawText("${r.glucoseLevel} ${r.unit}", 340f, yPos, bodyPaint)
                    canvas.drawText(GlucoseColorUtils.getLabelForLevel(r.glucoseLevel), 420f, yPos, bodyPaint)
                    yPos += 16f
                }
                yPos += 8f
                drawLine()
            }

            // Medications Table
            if (medications.isNotEmpty()) {
                newPageIfNeeded(80f)
                canvas.drawText("Medications", MARGIN, yPos, headerPaint)
                yPos += 18f
                canvas.drawText("Date/Time", MARGIN, yPos, subPaint)
                canvas.drawText("Type", 180f, yPos, subPaint)
                canvas.drawText("Name", 250f, yPos, subPaint)
                canvas.drawText("Dose", 380f, yPos, subPaint)
                canvas.drawText("Taken", 460f, yPos, subPaint)
                yPos += 14f
                drawLine()

                medications.take(40).forEach { m ->
                    newPageIfNeeded(18f)
                    canvas.drawText(DateTimeUtils.formatDateTime(m.scheduledTime), MARGIN, yPos, bodyPaint)
                    canvas.drawText(m.medicationType, 180f, yPos, bodyPaint)
                    canvas.drawText(m.name, 250f, yPos, bodyPaint)
                    canvas.drawText("${m.dose} ${m.unit}", 380f, yPos, bodyPaint)
                    canvas.drawText(if (m.isTaken) "Yes" else "No", 460f, yPos, bodyPaint)
                    yPos += 16f
                }
                yPos += 8f
                drawLine()
            }

            // Doctor notes
            profile?.doctorNotes?.let { notes ->
                if (notes.isNotEmpty()) {
                    newPageIfNeeded(50f)
                    canvas.drawText("Doctor Notes", MARGIN, yPos, headerPaint)
                    yPos += 18f
                    canvas.drawText(notes, MARGIN, yPos, bodyPaint)
                    yPos += 20f
                }
            }

            pdfDocument.finishPage(page)

            // Save to cache
            val fileName = "glucose_report_${System.currentTimeMillis()}.pdf"
            val file = File(context.cacheDir, fileName)
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Glucose Monitoring Report")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            onSuccess(Intent.createChooser(shareIntent, "Share Report via"))
        } catch (e: Exception) {
            onError(e.message ?: "Failed to generate PDF")
        }
    }
}
