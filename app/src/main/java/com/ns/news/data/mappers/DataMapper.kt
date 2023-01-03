package com.ns.news.data.mappers

import com.ns.news.data.api.model.CellsItem
import com.ns.news.data.api.model.SectionItem
import com.ns.news.domain.Result
import com.ns.news.domain.model.Cell

class DataMapper {
    fun toDomain(categoryItem: SectionItem): Result<SectionItem> {
        val(sectionId, api, name, inHamburger, inBreadcrumb, logo, subSections, _
        ) = categoryItem
        return SectionItem.of(sectionId=sectionId, api=api, name=name, inHamburger=inHamburger, inBreadcrumb=inBreadcrumb,
        logo=logo, subSections=subSections)
    }

    fun toDomain(cellItem: CellsItem, sectionId: String = "") : Cell {
        val(
        actionText,
        data,
        link,
        _,
        cellType,
        type,
        title) = cellItem
        return CellsItem.of(actionText= actionText, data= data, sectionId= sectionId, link = link,
            cellType = cellType, type = type, title = title)
    }
}