package com.news.data.api.mappers

import com.ns.news.data.api.model.SectionItem
import com.ns.news.domain.Result

class SectionMapper {
    fun toDomain(categoryItem: SectionItem): Result<SectionItem> {
        val(id, api, name, inHamburger, inBreadcrumb, logo, subSections, _
        ) = categoryItem
        return SectionItem.of(id=id, api=api, name=name, inHamburger=inHamburger, inBreadcrumb=inBreadcrumb,
        logo=logo, subSections=subSections)
    }
}