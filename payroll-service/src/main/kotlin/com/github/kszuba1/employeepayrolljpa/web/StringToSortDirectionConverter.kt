package com.github.kszuba1.employeepayrolljpa.web

import org.springframework.core.convert.converter.Converter
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component


@Component
class StringToSortDirectionConverter : Converter<String, Sort.Direction> {
    override fun convert(source: String): Sort.Direction =
        Sort.Direction.fromString(source.trim())
}
