/*
 * Copyright (C) 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.wallpaper.customization.ui.binder

import android.graphics.Typeface
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import com.android.themepicker.R
import com.android.wallpaper.customization.ui.viewmodel.ThemePickerCustomizationOptionsViewModel

/** Binds the clock font picker rows in the clock font floating sheet. */
object ClockFontFloatingSheetBinder {

    private data class FontOption(val family: String, val labelRes: Int)

    private val options =
        listOf(
            FontOption("", R.string.astral_clock_font_default),
            FontOption("sans-serif", R.string.astral_clock_font_sans),
            FontOption("serif", R.string.astral_clock_font_serif),
            FontOption("cursive", R.string.astral_clock_font_cursive),
            FontOption("casual", R.string.astral_clock_font_casual),
            FontOption("roboto-flex", R.string.astral_clock_font_roboto_flex),
        )

    fun bind(view: View, viewModel: ThemePickerCustomizationOptionsViewModel) {
        val context = view.context
        val container = view.findViewById<LinearLayout>(R.id.clock_font_options_container)
        container.removeAllViews()

        val current =
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ASTRAL_CLOCK_FONT)
                ?: ""
        val selectedFamily = options.firstOrNull { it.family == current }?.family ?: ""

        val layoutInflater = LayoutInflater.from(context)
        val rows = mutableListOf<Pair<String, RadioButton>>()
        options.forEach { option ->
            val row = layoutInflater.inflate(R.layout.clock_font_option_row, container, false)
            val radio = row.findViewById<RadioButton>(R.id.clock_font_option_radio)
            val label = row.findViewById<TextView>(R.id.clock_font_option_label)
            val sample = row.findViewById<TextView>(R.id.clock_font_option_sample)
            radio.isClickable = false

            label.text = context.getString(option.labelRes)
            sample.typeface =
                if (option.family.isEmpty()) {
                    Typeface.create("monospace", Typeface.NORMAL)
                } else {
                    Typeface.create(option.family, Typeface.NORMAL)
                }

            radio.isChecked = option.family == selectedFamily
            rows.add(option.family to radio)

            row.setOnClickListener {
                rows.forEach { (family, button) -> button.isChecked = family == option.family }
                Settings.Secure.putString(
                    context.contentResolver,
                    Settings.Secure.ASTRAL_CLOCK_FONT,
                    option.family,
                )
                viewModel.clockPickerViewModel.onClockFontFamilySelected()
            }
            container.addView(row)
        }
    }
}
